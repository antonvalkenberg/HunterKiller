package net.codepoke.ai.challenge.hunterkiller;

import java.io.File;
import java.util.Random;

import lombok.NoArgsConstructor;
import net.codepoke.ai.Generator;
import net.codepoke.ai.challenge.hunterkiller.FourPatch.DataCreation;
import net.codepoke.ai.challenge.hunterkiller.FourPatch.Sections;
import net.codepoke.ai.challenge.hunterkiller.enums.Direction;
import net.codepoke.ai.challenge.hunterkiller.enums.StructureType;
import net.codepoke.ai.challenge.hunterkiller.enums.TileType;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.mapfeature.Door;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.mapfeature.Floor;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.mapfeature.Space;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.mapfeature.Structure;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.mapfeature.Wall;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Infected;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Medic;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Soldier;
import net.codepoke.ai.network.MatchRequest;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.IntIntMap;

/**
 * Class representing a {@link Generator} for a {@link Map}. Contains methods to generate a map from
 * a {@link FourPatch} or string representation.
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
public class HunterKillerStateFactory
		implements Generator<HunterKillerState> {

	private static final Random r = new Random();

	public Array<MapSetup> mapRotation = new Array<MapSetup>();

	public HunterKillerStateFactory() {
		// Load in all maps defined in the folder /maps/
		File maps = new File("maps\\");

		// Check if any files can be found in the directory
		if (maps.listFiles() == null) {
			// Use a predefined basic map
			String rawMapData = StringExtensions.format("..........%n..........%n..████████%n..█B______%n..█_______%n..█_______%n..█_______%n..█_______%n..█_______%n..█_______");
			mapRotation.add(new MapSetup("basic", rawMapData));
		} else {

			for (File mapFile : maps.listFiles()) {
				try {
					// Check if the file isn't a directory
					if (mapFile.isDirectory())
						continue;

					FileHandle fileH = Gdx.files.getFileHandle(mapFile.getAbsolutePath(), FileType.Absolute);
					String rawMapData = fileH.readString()
												.replace("\r\n", "\n");

					String[] mapLines = rawMapData.split(FourPatch.NEWLINE_SEPARATOR);

					// Check if we have any lines of settings
					if (Character.isDigit(mapLines[0].charAt(0))) {
						// First line is the size/orientation
						String[] optionsLine1 = mapLines[0].split(" ");
						int qWidth = Integer.parseInt(optionsLine1[0]);
						int qHeight = Integer.parseInt(optionsLine1[1]);
						Direction spawnDirection = Direction.parse(optionsLine1[2]);

						// Check if there is a second line of settings
						if (Character.isDigit(mapLines[1].charAt(0))) {
							// Second line is the amount of starting resources for players and the base
							// resource-generation
							String[] optionsLine2 = mapLines[1].split(" ");
							int startingResources = Integer.parseInt(optionsLine2[0]);
							int baseResourceGeneration = Integer.parseInt(optionsLine2[1]);
							rawMapData = rawMapData.substring(rawMapData.indexOf(mapLines[2]));

							mapRotation.add(new MapSetup(mapFile.getName(), rawMapData, qWidth, qHeight, spawnDirection, startingResources,
															baseResourceGeneration));
						} else {

							rawMapData = rawMapData.substring(rawMapData.indexOf(mapLines[1]));
							mapRotation.add(new MapSetup(mapFile.getName(), rawMapData, qWidth, qHeight, spawnDirection));
						}
					} else {
						// Assume the whole map needs to be copied and we can use defaults.
						mapRotation.add(new MapSetup(mapFile.getName(), rawMapData));
					}
				} catch (Exception e) {
					System.err.println("Error during parsing of file: " + mapFile.getName());
					e.printStackTrace();
				}
			}
		}
	}

	// region Public methods
	
	@Override
	public int[] getSupportedPlayers() {
		return new int[]{2,4};
	}

	/**
	 * Constructs a {@link Map} from a specific {@link MapSetup}.
	 * 
	 * @param premade
	 *            The type of map to construct.
	 * @param players
	 *            The players in the game.
	 * @return The constructed {@link Map} object.
	 */
	public static Map constructMap(MapSetup premade, Player[] players) {
		// Create a FourPatch
		FourPatch patch = new FourPatch(new HunterKillerMapCreation(), premade.mapData, premade.quadrantAWidth, premade.quadrantAHeight);
		return constructFromFourPatch(premade.name, patch, players, premade.spawnDirection);
	}

	/**
	 * This method uses a {@link FourPatch} to create a full {@link Map}.
	 * 
	 * @param mapName
	 *            The name of the map.
	 * @param patch
	 *            The {@link FourPatch} that will be used to construct the map.
	 * @param players
	 *            The players in the game.
	 * @param patchBaseSpawnDirection
	 *            The {@link Direction} that the base in the patch uses to spawn it's units.
	 * 
	 * @return The constructed {@link Map} object.
	 */
	public static Map constructFromFourPatch(String mapName, FourPatch patch, Player[] players, Direction patchBaseSpawnDirection) {
		// Create a new Map
		Map map = new Map(mapName, patch.getGridWidth(), patch.getGridHeight());

		// Set up the HunterKillerMapCreation
		HunterKillerMapCreation.setup(players, map, patchBaseSpawnDirection);

		// Call map construction through FourPatch
		patch.createGrid();

		// Return the created Map
		return map;
	}

	/**
	 * Generate an initial {@link HunterKillerState} from a specific {@link MapSetup}.
	 * 
	 * {@link HunterKillerStateFactory#generateInitialState(String[], String)}.
	 */
	public static HunterKillerState generateInitialStateFromPremade(MapSetup premade, String[] playerNames, String options) {
		// Make sure the options string is not null
		if (options == null)
			options = "";
		// Check that either 2, 3 or 4 players are provided, other amounts are not supported
		if (playerNames.length < 2 || playerNames.length > 4) {
			throw new HunterKillerException(
											StringExtensions.format("Unsupported amount of players: %d. Only 2, 3 and 4 players are currently supported.",
																	playerNames.length));
		}

		// Select the map section we will be using for the players
		IntArray playerSections;
		switch (playerNames.length) {
		case 4:
			// All four 'corners' are used for players
			playerSections = new IntArray(new int[] { 0, 2, 6, 8 });
			break;
		case 3:
			// In the case of 3 players, use a random one of the 2 semi-mirrored corners (index 2 and 6)
			playerSections = new IntArray(new int[] { 0, r.nextBoolean() ? 2 : 6, 8 });
			break;
		case 2:
		default:
			// Only the two opposite corners
			playerSections = new IntArray(new int[] { 0, 8 });
			break;
		}

		// Check if we need to randomise the sections, so on re-creation the same player does not end up in the same
		// section each time.
		if (!options.contains("nonRandomSections")) {
			playerSections.shuffle();
		}

		// Load the players
		Player[] players = new Player[playerNames.length];
		for (int i = 0; i < players.length; i++) {
			players[i] = new Player(i, playerNames[i], playerSections.get(i), premade.startingResources);
		}

		// Set the resource generation of bases
		HunterKillerConstants.setBASE_RESOURCE_GENERATION(premade.baseResourceGeneration);

		// Construct the map
		Map map = constructMap(premade, players);

		// Make sure the map assigns the objects to the players
		for (Player player : players) {
			map.assignObjectsToPlayer(player);
		}

		// Set the initial field of view for the Units that were created on the map
		map.updateFieldOfView();

		// Create the initial state
		return new HunterKillerState(map, players, 1, 0);
	}

	// endregion

	// region Overridden methods

	/**
	 * Generates an initial state of the game from a collection of players that will participate and a
	 * {@link MatchRequest}.
	 * 
	 * @param playerNames
	 *            Collection of names for the players in the game.
	 * @param request
	 *            Either a {@link MatchRequest} or a {@link HunterKillerMatchRequest}.
	 * 
	 * @returns HunterKillerState adhering to the specifications set out by the parameters.
	 */
	@Override
	public HunterKillerState generateInitialState(String[] playerNames, MatchRequest request) {
		Array<MapSetup> maps = new Array<MapSetup>(mapRotation);
		String options = "";

		// Check if we are dealing with a specific HunterKillerMatchRequest
		if (request instanceof HunterKillerMatchRequest) {
			HunterKillerMatchRequest hkRequest = (HunterKillerMatchRequest) request;

			// Make sure the options string is not null
			if (hkRequest.options != null)
				options = hkRequest.options;

			// Check the maps to see if they fall within the request's restrictions
			Array<MapSetup> requestMaps = new Array<MapSetup>();
			for (MapSetup setup : mapRotation) {
				if ((hkRequest.mapType == null || setup.name.contains(hkRequest.mapType.getFileFlag()))
					&& (hkRequest.gameType == null || setup.name.contains(hkRequest.gameType.getFileFlag()))
					&& (hkRequest.mapName == null || setup.name.contains(hkRequest.mapName)))
					requestMaps.add(setup);
			}
			maps = requestMaps;
		}

		// Select a random premade map to create
		MapSetup premade = maps.random();
		// Generate the initial state from this premade map
		return generateInitialStateFromPremade(premade, playerNames, options);
	}

	// endregion

	// region Internal classes

	/**
	 * Implements the {@link DataCreation} interface in order to create the {@link Map} at the start
	 * of the game.
	 * 
	 * Uses temporary variables, NOT MULTITHREADABLE.
	 * 
	 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
	 *
	 */
	@NoArgsConstructor
	public static class HunterKillerMapCreation
			implements DataCreation {

		/**
		 * The IDs of the players in the game.
		 */
		private static IntIntMap sectionPlayerIDMap;
		/**
		 * Reference to the map that the objects will be created on.
		 */
		private static Map map;
		/**
		 * The direction the base on the patch should spawn it's unit in.
		 */
		private static Direction patchBaseSpawnDirection;
		/**
		 * The amount of tiles/squares Units are spawned away from the Structure.
		 */
		private static final int SPAWN_DISTANCE_FROM_BASE = 1;

		/**
		 * Set up the temporary variables that need to be accessed when creating objects on the map.
		 * 
		 * @param players
		 *            The players in the game.
		 * @param newMap
		 *            The map that the objects will be created on.
		 * @param spawn
		 *            The direction the base on the patch should spawn it's unit in.
		 */
		public static void setup(Player[] players, Map newMap, Direction spawn) {
			map = newMap;
			patchBaseSpawnDirection = spawn;
			// Create the player-section map
			sectionPlayerIDMap = new IntIntMap(players.length);
			for (Player player : players) {
				if (!sectionPlayerIDMap.containsKey(player.getMapSection())) {
					sectionPlayerIDMap.put(player.getMapSection(), player.getID());
				} else {
					throw new HunterKillerException(
													StringExtensions.format("Player with ID %d is assigned section %d, which is already assigned to player with ID %d",
																			player.getID(),
																			player.getMapSection(),
																			sectionPlayerIDMap.get(player.getMapSection(), -1)));
				}
			}
		}

		/**
		 * Reset the temporary variables.
		 */
		public static void reset() {
			map = null;
			patchBaseSpawnDirection = null;
			sectionPlayerIDMap.clear();
		}

		@Override
		public void create(char data, int x, int y, Sections section) {

			// Create the map location and position
			MapLocation location = new MapLocation(x, y);
			int mapPosition = map.toPosition(x, y);
			int sectionIndex = section.ordinal();

			// Some documentation about what happens in the following switch-case:
			// The MapFeature objects are mostly straightforward (except Structure, see below).
			// The Unit objects + Structure object are slightly more complicated, because the section index affects
			// them:
			// - If the section index appears in our section-playerID map, actual Units/Bases need to be created.
			// - Otherwise they should be ignored.
			boolean ignoreUnitAndBase = !sectionPlayerIDMap.containsKey(sectionIndex);

			// Check what type to create
			TileType tile = TileType.valueOf(data);
			switch (tile) {
			// Straightforward MapFeatures
			case DOOR_CLOSED:
				Door door = new Door(location);
				map.registerGameObject(door);
				map.place(mapPosition, door);
				break;
			case DOOR_OPEN:
				Door openDoor = new Door(location, HunterKillerConstants.DOOR_OPEN_ROUNDS);
				map.registerGameObject(openDoor);
				map.place(mapPosition, openDoor);
				break;
			case FLOOR:
				Floor floor = new Floor(location);
				map.registerGameObject(floor);
				map.place(mapPosition, floor);
				break;
			case SPACE:
				Space space = new Space(location);
				map.registerGameObject(space);
				map.place(mapPosition, space);
				break;
			case WALL:
				Wall wall = new Wall(location);
				map.registerGameObject(wall);
				map.place(mapPosition, wall);
				break;
			case OBJECTIVE:
				Structure objective = new Structure(location, StructureType.Objective);
				map.registerGameObject(objective);
				map.place(mapPosition, objective);
				break;
			case STRONGHOLD:
				Structure stronghold = new Structure(location, StructureType.Stronghold);
				map.registerGameObject(stronghold);
				map.place(mapPosition, stronghold);
				break;
			case OUTPOST:
				Structure outpost = new Structure(location, StructureType.Outpost);
				outpost.setSpawnLocation(location);
				map.registerGameObject(outpost);
				map.place(mapPosition, outpost);
				break;
			// Units and Bases
			case INFECTED:
			case MEDIC:
			case SOLDIER:
			case BASE:
				// Always place a Floor under a Unit
				Floor tempFloor = new Floor(location);
				map.registerGameObject(tempFloor);
				map.place(mapPosition, tempFloor);

				// Determine to which player-ID the Unit/Base will be assigned
				// (the default value is not important, because if there is no playerID for this section, no Units will
				// be created).
				int playerID = sectionPlayerIDMap.get(sectionIndex, -1);

				if (!ignoreUnitAndBase && tile == TileType.INFECTED) {
					Infected infected = new Infected(playerID, location, HunterKillerConstants.UNIT_DEFAULT_ORIENTATION);
					map.registerGameObject(infected);
					map.place(mapPosition, infected);

				} else if (!ignoreUnitAndBase && tile == TileType.MEDIC) {
					Medic medic = new Medic(playerID, location, HunterKillerConstants.UNIT_DEFAULT_ORIENTATION);
					map.registerGameObject(medic);
					map.place(mapPosition, medic);

				} else if (!ignoreUnitAndBase && tile == TileType.SOLDIER) {
					Soldier soldier = new Soldier(playerID, location, HunterKillerConstants.UNIT_DEFAULT_ORIENTATION);
					map.registerGameObject(soldier);
					map.place(mapPosition, soldier);

				} else if (!ignoreUnitAndBase) {
					// Remove the Floor
					map.unregisterGameObject(tempFloor);
					// For Bases, we also need to determine the location of where they spawn Units.
					// This location is always adjacent to the base, in a predefined direction.
					// Initialise the spawn location with the location for the base defined in the FourPatch (section
					// index 0).
					MapLocation spawnLocation = map.getLocationInDirection(location, patchBaseSpawnDirection, SPAWN_DISTANCE_FROM_BASE);
					switch (section) {
					// We already know it's one of our player-IDs, so it can be only one of the following 4:
					case A:
						// Already initialised it for our patch-section, break out
						break;
					case A_H:
						// Section 2 (top right of map), spawns in opposite direction when EAST or WEST
						if (patchBaseSpawnDirection == Direction.NORTH || patchBaseSpawnDirection == Direction.SOUTH) {
							// spawning in same direction, so break out
							break;
						}
					case A_V:
						// Section 6 (bottom left of map), spawns in opposite direction when NORTH or SOUTH
						if (patchBaseSpawnDirection == Direction.WEST || patchBaseSpawnDirection == Direction.EAST) {
							// spawning in same direction, so break out
							break;
						}
					case A_Mirror:
						// Section 8 always spawns in the opposite direction
					default:
						spawnLocation = map.getLocationInDirection(	location,
																	patchBaseSpawnDirection.getOppositeDirection(),
																	SPAWN_DISTANCE_FROM_BASE);
						break;
					}

					// Now that we have defined our spawn location, we can create the base
					Structure base = new Structure(location, StructureType.Base);
					base.setControllingPlayerID(playerID);
					base.setSpawnLocation(spawnLocation);
					map.registerGameObject(base);
					map.place(mapPosition, base);
				}
				break;
			default:
				System.err.println("WARNING: Unsupported TileType found during map creation!");
				break;
			}
		}

	}

	// endregion

}
