package main.java.net.codepoke.ai.challenge.hunterkiller;

import java.util.ArrayList;
import java.util.Random;
import lombok.val;
import main.java.net.codepoke.ai.challenge.hunterkiller.enums.Direction;
import main.java.net.codepoke.ai.challenge.hunterkiller.enums.PremadeMap;
import main.java.net.codepoke.ai.challenge.hunterkiller.enums.TileType;
import main.java.net.codepoke.ai.challenge.hunterkiller.gameobjects.GameObject;
import main.java.net.codepoke.ai.challenge.hunterkiller.gameobjects.mapfeature.Base;
import main.java.net.codepoke.ai.challenge.hunterkiller.gameobjects.mapfeature.Door;
import main.java.net.codepoke.ai.challenge.hunterkiller.gameobjects.mapfeature.Floor;
import main.java.net.codepoke.ai.challenge.hunterkiller.gameobjects.mapfeature.Space;
import main.java.net.codepoke.ai.challenge.hunterkiller.gameobjects.mapfeature.Wall;
import main.java.net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Infected;
import main.java.net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Medic;
import main.java.net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Soldier;
import main.java.net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Unit;
import main.java.net.codepoke.ai.challenge.hunterkiller.players.TestPlayer;
import net.codepoke.ai.GameRules.Generator;

/**
 * Class representing a {@link Generator} for a {@link Map}. Contains methods to generate a map from
 * a {@link FourPatch} or String representation.
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
   * @return The constructed {@link Map} object.
   */
  public static Map constructMap(PremadeMap premade) {
    return premade != null ? constructFromFourPatch(premade.fourPatch, premade.basePosition, premade.spawnDirection) : null;
  }
  
  /**
   * This method interprets a {@link FourPatch} and creates a full {@link Map}.
   * 
   * @param patch
   *          The base-quadrant from which the map will be constructed.
   * @param basePosition
   *          The position on the map where the base is located.
   * @param spawnLocation
   *          The location where the base spawns it's units.
   * @return The constructed {@link Map} object.
   */
  public static Map constructFromFourPatch(FourPatch patch, int basePosition, Direction spawnDirection) {
    //Set the dimensions of the map we are creating
    int mapWidth = patch.quadrantWidth * 2;
    int mapHeight = patch.quadrantHeight * 2;
    //Create an empty Map
    Map newMap = new Map(mapWidth, mapHeight);
    
    //Create the mock map content
    GameObject[][] mapData = new GameObject[mapWidth * mapHeight][Map.INTERNAL_MAP_LAYERS];
    
    //The provided position of the base is where it's at on the quadrant, so we need to adjust it to the actual map.
    MapLocation baseLocation = Map.toLocation(basePosition, patch.quadrantWidth);
    int mapBasePosition = Map.toPosition(baseLocation, mapWidth);
    MapLocation mapBaseLocation = Map.toLocation(mapBasePosition, mapWidth);
    MapLocation mapBaseSpawnLocation = newMap.getLocationInDirection(mapBaseLocation, spawnDirection, 1);
    //Determine the mirrored base's position
    int mapBasePositionMirrored = getMirroredPositionFull(mapBaseLocation.getX(), mapBaseLocation.getY(), mapWidth, mapHeight);
    MapLocation mapBaseLocationMirrored = Map.toLocation(mapBasePositionMirrored, mapWidth);
    MapLocation mapBaseSpawnLocationMirrored = newMap.getLocationInDirection(mapBaseLocationMirrored, spawnDirection.getOppositeDirection(), 1);
    //Set the bases into the mapData
    mapData[mapBasePosition][Map.INTERNAL_MAP_FEATURE_INDEX] = new Base(newMap.requestNewGameObjectID(), mapBaseLocation, mapBaseSpawnLocation);
    mapData[mapBasePositionMirrored][Map.INTERNAL_MAP_FEATURE_INDEX] = new Base(newMap.requestNewGameObjectID(), mapBaseLocationMirrored, mapBaseSpawnLocationMirrored);
    
    //Go through the height of the quadrant
    for(int y = 0; y < patch.data.length; y++) {
      //And the width
      for(int x = 0; x < patch.data[0].length; x++) {
        //Get the objects at this location of the quadrant
        TileType[] tiles = patch.data[y][x];
        //Determine on which positions of the complete map this data needs to be copied
        int[] positions = null;
        //Find all 4 positions in each quadrant
        int topleftPosition = Map.toPosition(x, y, mapWidth);
        int toprightPosition = getMirroredPositionOnlyWidth(x, y, mapWidth);
        int bottomleftPosition = getMirroredPositionOnlyHeight(x, y, mapWidth, mapHeight);
        int bottomrightPosition = getMirroredPositionFull(x, y, mapWidth, mapHeight);
        //Make a collection for easy traversing
        positions = new int[] { topleftPosition, toprightPosition, bottomleftPosition, bottomrightPosition };
        //Add tiles to the previously determined positions
        for(int position : positions) {
          if(tiles != null) {
            for(TileType tile : tiles) {
              if(tile != null) {
                //Create an object of the tile type at the specified location on the map
                MapLocation location = Map.toLocation(position, mapWidth);
                switch(tile) {
                  case SPACE:
                    mapData[position][Map.INTERNAL_MAP_FEATURE_INDEX] = new Space(newMap.requestNewGameObjectID(), location);
                    break;
                  case FLOOR:
                    mapData[position][Map.INTERNAL_MAP_FEATURE_INDEX] = new Floor(newMap.requestNewGameObjectID(), location);
                    break;
                  case WALL:
                    mapData[position][Map.INTERNAL_MAP_FEATURE_INDEX] = new Wall(newMap.requestNewGameObjectID(), location);
                    break;
                  case DOOR_CLOSED:
                    mapData[position][Map.INTERNAL_MAP_FEATURE_INDEX] = new Door(newMap.requestNewGameObjectID(), location);
                    break;
                  case DOOR_OPEN:
                    mapData[position][Map.INTERNAL_MAP_FEATURE_INDEX] = new Door(newMap.requestNewGameObjectID(), location, Door.DOOR_OPEN_TICKS);
                    break;
                  case SOLDIER:
                    mapData[position][Map.INTERNAL_MAP_UNIT_INDEX] = new Soldier(newMap.requestNewGameObjectID(), location, Unit.DEFAULT_ORIENTATION);
                    break;
                  case MEDIC:
                    mapData[position][Map.INTERNAL_MAP_UNIT_INDEX] = new Medic(newMap.requestNewGameObjectID(), location, Unit.DEFAULT_ORIENTATION);
                    break;
                  case INFECTED:
                    mapData[position][Map.INTERNAL_MAP_UNIT_INDEX] = new Infected(newMap.requestNewGameObjectID(), location, Unit.DEFAULT_ORIENTATION);
                    break;
                  default:
                    System.err.println("UNHANDLED TILE TYPE!");
                }
              }
            }
          }
        }
      }
    }
    
    //Place everything on the map
    newMap.setMapContent(mapData);
    //Return the created Map
    return newMap;
  }
  
  /**
   * Generates an initial state of the game from a collection of players that will participate and a
   * String defining some options for the game.
   */
  @Override
  public HunterKillerState generateInitialState(String[] playerNames, String options) {
    //Select a premade map to create
    Random r = new Random();
    PremadeMap premade = PremadeMap.values()[r.nextInt(PremadeMap.values().length)];
    //PremadeMap premade = PremadeMap.TEST;
    
    //Construct the map
    Map map = constructMap(premade);
    //Get the content that is currently on the map
    val mapContent = map.getMapContent();
    
    //Scan for Bases
    val bases = new ArrayList<Base>();
    for(int i = 0; i < map.getMapWidth() * map.getMapHeight(); i++) {
      GameObject feature = mapContent[i][Map.INTERNAL_MAP_FEATURE_INDEX];
      //Check if there is anything, and if so, if it's a Base
      if(feature != null && feature instanceof Base) {
        bases.add((Base)feature);
      }
    }
    
    //Check if there is a Base for each player
    if(bases.size() != playerNames.length) {
      //TODO throw an error?
    }
    
    //Load the players
    Player[] players = new Player[playerNames.length];
    for(int i = 0; i < players.length; i++) {
      //Assign the player a random base
      Base base = bases.remove(r.nextInt(bases.size()));
      //TODO custom class loader?
      players[i] = new TestPlayer(map.requestNewPlayerID(), playerNames[i], base);
    }
    
    //Create the initial state
    //TODO create a turn order for players?
    return new HunterKillerState(map, players, 0, 0);
  }
  
  //endregion
  
  //region Private methods
  
  /**
   * Get the mirrored position in both width and height of a position.
   * 
   * @param x
   *          The X-coordinate of the original position.
   * @param y
   *          The Y-coordinate of the original position.
   * @param mapWidth
   *          The width of the map.
   * @param mapHeight
   *          The height of the map.
   * @return The positional index of the mirrored position.
   */
  private static int getMirroredPositionFull(int x, int y, int mapWidth, int mapHeight) {
    return Map.toPosition((mapWidth - 1) - x, (mapHeight - 1) - y, mapWidth);
  }
  
  /**
   * Get a mirrored position along the width of a map.
   * 
   * @param x
   *          The X-coordinate of the original position.
   * @param y
   *          The Y-coordinate of the original position.
   * @param mapWidth
   *          The width of the map.
   * @return The positional index of the mirrored position.
   */
  private static int getMirroredPositionOnlyWidth(int x, int y, int mapWidth) {
    return Map.toPosition((mapWidth - 1) - x, y, mapWidth);
  }
  
  /**
   * Get a mirrored position along the height of a map.
   * 
   * @param x
   *          The X-coordinate of the original position.
   * @param y
   *          The Y-coordinate of the original position.
   * @param mapWidth
   *          The width of the map.
   * @param mapHeight
   *          The height of the map.
   * @return The positional index of the mirrored position.
   */
  private static int getMirroredPositionOnlyHeight(int x, int y, int mapWidth, int mapHeight) {
    return Map.toPosition(x, (mapHeight - 1) - y, mapWidth);
  }
  
  //endregion
}
