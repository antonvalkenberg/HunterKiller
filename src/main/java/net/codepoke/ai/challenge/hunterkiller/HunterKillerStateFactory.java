package net.codepoke.ai.challenge.hunterkiller;

import java.io.File;
import java.util.Random;
import java.util.regex.Pattern;

import lombok.NoArgsConstructor;
import net.codepoke.ai.Generator;
import net.codepoke.ai.challenge.hunterkiller.FourPatch.DataCreation;
import net.codepoke.ai.challenge.hunterkiller.FourPatch.Sections;
import net.codepoke.ai.challenge.hunterkiller.enums.Direction;
import net.codepoke.ai.challenge.hunterkiller.enums.TileType;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.mapfeature.Base;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.mapfeature.Door;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.mapfeature.Floor;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.mapfeature.Space;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.mapfeature.Wall;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Infected;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Medic;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Soldier;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Unit;

import org.apache.commons.io.IOUtils;

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

	public static Array<MapSetup> MAP_ROTATION = new Array<MapSetup>();

	static {
		// Load in all maps defined in the folder /maps/
		File maps = new File("maps\\");
		Pattern p = Pattern.compile("^\\d");

		for (File mapFile : maps.listFiles()) {
			try {
				String rawMapData = IOUtils.toString(mapFile.toURI());

				String[] mapLines = rawMapData.split(FourPatch.NEWLINE_SEPARATOR);
				if (p.matcher(mapLines[0])
						.find()) {
					// First line is the size/orientation
					String[] options = mapLines[0].split(" ");
					int qWidth = Integer.parseInt(options[0]);
					int qHeight = Integer.parseInt(options[1]);
					Direction spawnDirection = Direction.parse(options[2]);

					rawMapData = rawMapData.substring(rawMapData.indexOf(mapLines[1]));
					MAP_ROTATION.add(new MapSetup(mapFile.getName(), rawMapData, qWidth, qHeight, spawnDirection));
				} else {
					// Assume the whole map needs to be copied and we can use defaults.
					MAP_ROTATION.add(new MapSetup(mapFile.getName(), rawMapData));
				}
			} catch (Exception e) {
				System.err.println("Error during parsing of file: " + mapFile.getName());
				e.printStackTrace();
			}
		}
	}

	// region Public methods

	/**
	 * Constructs a {@link Map} from a specific {@link PremadeMap}.
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
		return constructFromFourPatch(patch, players, premade.spawnDirection);
	}

	/**
	 * This method uses a {@link FourPatch} to create a full {@link Map}.
	 * 
	 * @param patch
	 *            The {@link FourPatch} that will be used to construct the map.
	 * @param players
	 *            The players in the game.
	 * @param patchBaseSpawnDirection
	 *            The {@link Direction} that the base in the patch uses to spawn it's units.
	 * 
	 * @return The constructed {@link Map} object.
	 */
	public static Map constructFromFourPatch(FourPatch patch, Player[] players, Direction patchBaseSpawnDirection) {
		// Create a new Map
		Map map = new Map(patch.getGridWidth(), patch.getGridHeight());

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
			// TODO throw an error.
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
			players[i] = new Player(i, playerNames[i], playerSections.get(i));
		}

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
	 * String defining some options for the game. This method makes the following assumptions:
	 * <ul>
	 * <li>{@code playerNames} contains the names of the players in the game.</li>
	 * <li>Currently supported player amounts are: {@code 2, 3, 4}.</li>
	 * <li>Currently supported options are:
	 * <ul>
	 * <li>{@code nonRandomSections} Indicates that the players should be placed in sections of the map according to the
	 * order supplied in {@code playerNames}.</li>
	 * </ul>
	 * </li>
	 * </ul>
	 */
	@Override
	public HunterKillerState generateInitialState(String[] playerNames, String options) {
		// Make sure the options string is not null
		if (options == null)
			options = "";
		// Select a random premade map to create
		MapSetup premade = MAP_ROTATION.random();
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
		 * The amount of tiles/squares Units are spawned away from the Base.
		 */
		private static final int SPAWN_DISTANCE_FROM_BASE = 1;

		/**
		 * Set up the temporary variables that need to be accessed when creating objects on the map.
		 * 
		 * @param players
		 *            The IDs of the players in the game. See
		 *            {@link HunterKillerStateFactory#constructMap(PremadeMap, IntArray)}.
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
					// TODO throw an error! a map section was assigned to more than one Player
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
			// The MapFeature objects are mostly straightforward (except Base, see below).
			// The Unit objects + Base object are slightly more complicated, because the section index affects them:
			// - If the section index appears in our section-playerID map, actual Units/Bases need to be created.
			// - Otherwise they should be ignored.
			boolean ignoreUnitAndBase = !sectionPlayerIDMap.containsKey(sectionIndex);

			// Check what type to create
			TileType tile = TileType.valueOf(data);
			switch (tile) {
			// Straightforward MapFeatures
			case DOOR_CLOSED:
				map.place(mapPosition, new Door(map.requestNewGameObjectID(), location));
				break;
			case DOOR_OPEN:
				map.place(mapPosition, new Door(map.requestNewGameObjectID(), location, Door.DOOR_OPEN_ROUNDS));
				break;
			case FLOOR:
				map.place(mapPosition, new Floor(map.requestNewGameObjectID(), location));
				break;
			case SPACE:
				map.place(mapPosition, new Space(map.requestNewGameObjectID(), location));
				break;
			case WALL:
				map.place(mapPosition, new Wall(map.requestNewGameObjectID(), location));
				break;
			// Units and Bases
			case INFECTED:
			case MEDIC:
			case SOLDIER:
			case BASE:
				// Always place a Floor under a Unit
				Floor tempFloor = new Floor(map.requestNewGameObjectID(), location);
				map.place(mapPosition, tempFloor);

				// Determine to which player-ID the Unit/Base will be assigned
				// (the default value is not important, because if there is no playerID for this section, no Units will
				// be created).
				int playerID = sectionPlayerIDMap.get(sectionIndex, -1);

				if (!ignoreUnitAndBase && tile == TileType.INFECTED) {
					map.place(mapPosition, new Infected(map.requestNewGameObjectID(), playerID, location, Unit.DEFAULT_ORIENTATION));
				} else if (!ignoreUnitAndBase && tile == TileType.MEDIC) {
					map.place(mapPosition, new Medic(map.requestNewGameObjectID(), playerID, location, Unit.DEFAULT_ORIENTATION));
				} else if (!ignoreUnitAndBase && tile == TileType.SOLDIER) {
					map.place(mapPosition, new Soldier(map.requestNewGameObjectID(), playerID, location, Unit.DEFAULT_ORIENTATION));
				} else if (!ignoreUnitAndBase) {
					// Remove the Floor
					map.remove(mapPosition, tempFloor);
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
						// Section 2 (top right of map), spawns in opposite direction when WEST or EAST
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

					// Now that we have defined our spawn location, we can create the Base
					map.place(mapPosition, new Base(map.requestNewGameObjectID(), playerID, location, spawnLocation));
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
