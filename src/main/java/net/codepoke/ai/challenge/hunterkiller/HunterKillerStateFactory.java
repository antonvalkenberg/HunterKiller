package net.codepoke.ai.challenge.hunterkiller;

import java.util.Random;
import lombok.NoArgsConstructor;
import net.codepoke.ai.GameRules.Generator;
import net.codepoke.ai.challenge.hunterkiller.FourPatch.DataCreation;
import net.codepoke.ai.challenge.hunterkiller.enums.Direction;
import net.codepoke.ai.challenge.hunterkiller.enums.PremadeMap;
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
import com.badlogic.gdx.utils.IntArray;

/**
 * Class representing a {@link Generator} for a {@link Map}. Contains methods to generate a map from
 * a {@link FourPatch} or string representation.
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
public class HunterKillerStateFactory implements Generator<HunterKillerState> {
  
  //region Public methods
  
  /**
   * Constructs a {@link Map} from a specific {@link PremadeMap}.
   * 
   * @param premade
   *          The type of map to construct.
   * @param playerIDs
   *          The IDs of the players in the game. Note that these correspond to a section-index in
   *          the grid from {@link FourPatch.DataCreation#create(char, int, int, int)}.
   * @return The constructed {@link Map} object.
   */
  public static Map constructMap(PremadeMap premade, IntArray playerIDs) {
    //Create a FourPatch
    FourPatch patch = new FourPatch(new HunterKillerMapCreation(), premade.mapData, premade.quadrantAWidth, premade.quadrantAHeight);
    return constructFromFourPatch(patch, playerIDs, premade.spawnDirection);
  }
  
  /**
   * This method uses a {@link FourPatch} to create a full {@link Map}.
   * 
   * @param patch
   *          The {@link FourPatch} that will be used to construct the map.
   * @param playerIDs
   *          The IDs of the players in the game. See {@link #constructMap(PremadeMap, IntArray)}.
   * @param patchBaseSpawnDirection
   *          The {@link Direction} that the base in the patch uses to spawn it's units.
   * 
   * @return The constructed {@link Map} object.
   */
  public static Map constructFromFourPatch(FourPatch patch, IntArray playerIDs, Direction patchBaseSpawnDirection) {
    //Create a new Map
    Map map = new Map(patch.getGridWidth(), patch.getGridHeight());
    
    //Set up the HunterKillerMapCreation
    HunterKillerMapCreation.setup(playerIDs, map, patchBaseSpawnDirection);
    
    //Call map construction through FourPatch
    patch.createGrid();
    
    //Return the created Map
    return map;
  }
  
  //endregion
  
  //region Overridden methods
  
  /**
   * Generates an initial state of the game from a collection of players that will participate and a
   * String defining some options for the game. This method makes the following assumptions:
   * <ul>
   * <li>{@code playerNames} contains the class names of the {@link Player}s to load, located in:
   * {@code /players}</li>
   * <li>Currently supported player amounts are: {@code 2, 3, 4}.</li>
   * <li>Currently supported options are: {@code none}.</li>
   * </ul>
   */
  @Override
  public HunterKillerState generateInitialState(String[] playerNames, String options) {
    //Select a random premade map to create
    Random r = new Random();
    PremadeMap premade = PremadeMap.values()[r.nextInt(PremadeMap.values().length)];
    
    //Check that either 2, 3 or 4 players are provided, other amounts are not supported
    if(playerNames.length < 2 || playerNames.length > 4) {
      //TODO throw an error.
    }
    
    //Define player IDs according to grid sections, this varies by the amount of players
    IntArray playerIDs;
    switch(playerNames.length) {
      case 4:
        //All four 'corners' are used for players
        playerIDs = new IntArray(new int[] { 0, 2, 6, 8 });
        break;
      case 3:
        //In the case of 3 players, use a random one of the 2 semi-mirrored corners (index 2 and 6)
        playerIDs = new IntArray(new int[] { 0, r.nextBoolean() ? 2 : 6, 8 });
        break;
      case 2:
      default:
        //Only the two opposite corners
        playerIDs = new IntArray(new int[] { 0, 8 });
        break;
    }
    
    //Construct the map
    Map map = constructMap(premade, playerIDs);
    
    //Load the players
    Player[] players = new Player[playerNames.length];
    for(int i = 0; i < players.length; i++) {
      //TODO Assign and load player classes to a random playerID
    }
    
    //Assign bases and units on the map to players
    for(Player player : players) {
      map.assignObjectsToPlayer(player);
    }
    
    //Create the initial state
    return new HunterKillerState(map, players, 1, 0);
  }
  
  //endregion
  
  //region Internal classes
  
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
  public static class HunterKillerMapCreation implements DataCreation {
    
    /**
     * The IDs of the players in the game.
     */
    private static IntArray playerIDs;
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
     *          The IDs of the players in the game. See
     *          {@link HunterKillerStateFactory#constructMap(PremadeMap, IntArray)}.
     * @param newMap
     *          The map that the objects will be created on.
     * @param spawn
     *          The direction the base on the patch should spawn it's unit in.
     */
    public static void setup(IntArray players, Map newMap, Direction spawn) {
      playerIDs = players;
      map = newMap;
      patchBaseSpawnDirection = spawn;
    }
    
    /**
     * Reset the temporary variables.
     */
    public static void reset() {
      playerIDs = null;
      map = null;
      patchBaseSpawnDirection = null;
    }
    
    @Override
    public void create(char data, int x, int y, int sectionIndex) {
      //Create the map location and position
      MapLocation location = new MapLocation(x, y);
      int mapPosition = map.toPosition(x, y);
      
      //Some documentation about what happens in the following switch-case:
      //  The MapFeature objects are mostly straightforward (except Base, see below).
      //  The Unit objects + Base object are slightly more complicated, because the section index affects them:
      //    - If the section index appears in our player-ID collection, actual Units/Bases need to be created.
      //    - Otherwise they should be ignored and replaced by Floor-tiles.
      boolean replaceByFloor = !playerIDs.contains(sectionIndex);
      
      //Check what type to create
      TileType tile = TileType.valueOf(data);
      switch(tile) {
      //Straightforward MapFeatures
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
        //Units and Bases
        case INFECTED:
        case MEDIC:
        case SOLDIER:
        case BASE:
          if(replaceByFloor)
            map.place(mapPosition, new Floor(map.requestNewGameObjectID(), location));
          else if(tile == TileType.INFECTED) {
            map.place(mapPosition, new Infected(map.requestNewGameObjectID(), sectionIndex, location, Unit.DEFAULT_ORIENTATION));
          }
          else if(tile == TileType.MEDIC) {
            map.place(mapPosition, new Medic(map.requestNewGameObjectID(), sectionIndex, location, Unit.DEFAULT_ORIENTATION));
          }
          else if(tile == TileType.SOLDIER) {
            map.place(mapPosition, new Soldier(map.requestNewGameObjectID(), sectionIndex, location, Unit.DEFAULT_ORIENTATION));
          }
          else {
            //For Bases, we also need to determine the location of where they spawn Units.
            //This location is always adjacent to the base, in a predefined direction.
            //Initialise the spawn location with the location for the base defined in the FourPatch (section index 0).
            MapLocation spawnLocation = map.getLocationInDirection(location, patchBaseSpawnDirection, SPAWN_DISTANCE_FROM_BASE);
            switch(sectionIndex) {
            //We already know it's one of our player-IDs, so it can be only one of the following 4:
              case 0:
                //Already initialised it for our patch-section, break out
                break;
              case 2:
                //Section 2 (top right of map), spawns in opposite direction when WEST or EAST
                if(patchBaseSpawnDirection == Direction.NORTH || patchBaseSpawnDirection == Direction.SOUTH) {
                  //spawning in same direction, so break out
                  break;
                }
              case 6:
                //Section 6 (bottom left of map), spawns in opposite direction when NORTH or SOUTH
                if(patchBaseSpawnDirection == Direction.WEST || patchBaseSpawnDirection == Direction.EAST) {
                  //spawning in same direction, so break out
                  break;
                }
              case 8:
                //Section 8 always spawns in the opposite direction
              default:
                spawnLocation = map.getLocationInDirection(location, patchBaseSpawnDirection.getOppositeDirection(), SPAWN_DISTANCE_FROM_BASE);
                break;
            }
            
            //Now that we have defined our spawn location, we can create the Base
            map.place(mapPosition, new Base(map.requestNewGameObjectID(), sectionIndex, location, spawnLocation));
          }
          break;
        default:
          System.err.println("WARNING: Unsupported TileType found during map creation!");
          break;
      }
    }
    
  }
  
  //endregion
  
}
