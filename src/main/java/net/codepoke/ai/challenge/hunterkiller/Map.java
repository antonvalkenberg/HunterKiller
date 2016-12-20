package main.java.net.codepoke.ai.challenge.hunterkiller;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import main.java.net.codepoke.ai.challenge.hunterkiller.actions.UnitAction;
import main.java.net.codepoke.ai.challenge.hunterkiller.enums.Direction;
import main.java.net.codepoke.ai.challenge.hunterkiller.gameobjects.GameObject;
import main.java.net.codepoke.ai.challenge.hunterkiller.gameobjects.mapfeature.Base;
import main.java.net.codepoke.ai.challenge.hunterkiller.gameobjects.mapfeature.Door;
import main.java.net.codepoke.ai.challenge.hunterkiller.gameobjects.mapfeature.Floor;
import main.java.net.codepoke.ai.challenge.hunterkiller.gameobjects.mapfeature.MapFeature;
import main.java.net.codepoke.ai.challenge.hunterkiller.gameobjects.mapfeature.Space;
import main.java.net.codepoke.ai.challenge.hunterkiller.gameobjects.mapfeature.Wall;
import main.java.net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Infected;
import main.java.net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Medic;
import main.java.net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Soldier;
import main.java.net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Unit;

/**
 * The map on which the game is played. The map is internally represented as a 2-dimensional array.
 * The first dimension contains all locations on the map (width * height) mapped to a positional
 * index ((y * width) + x). The second dimension contains 2 layers of {@link GameObject}s, where the
 * first layer contains all {@link MapFeature} objects that are present on the map (i.e.
 * {@link Wall}, {@link Floor}), and the second layer contains all {@link Unit} objects (i.e.
 * {@link Soldier}, {@link Medic}). Note that the width of the map is treated as the X-axis, and the
 * height as the Y-axis. Also, the coordinate (0,0) is the top-left corner of the map.
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
public class Map {
  
  //region Constants
  
  /**
   * Amount of layers in our internal map representation. Currently we have 2: MapFeatures and
   * Units.
   */
  public static final int INTERNAL_MAP_LAYERS = 2;
  /**
   * The index of the {@link MapFeature} layer in our internal map representation.
   */
  public static final int INTERNAL_MAP_FEATURE_INDEX = 0;
  /**
   * The index of the {@link Unit} layer in our internal map representation.
   */
  public static final int INTERNAL_MAP_UNIT_INDEX = 1;
  /**
   * Defines the separator used to create space between the different layers in the toString method.
   */
  private static final String TOSTRING_LAYER_SEPARATOR = " | ";
  
  //endregion
  
  //region Properties
  
  /**
   * The height of this map.
   */
  @Getter
  private int mapHeight;
  
  /**
   * The width of this map.
   */
  @Getter
  private int mapWidth;
  
  /**
   * This is the internal representation. See {@link Map} for details.
   */
  @Getter
  private GameObject[][] mapContent;
  
  /**
   * A counter that records the latest ID given out to a player.
   */
  private int internalPlayerIDCounter;
  
  /**
   * A counter that records the latest ID given out to a game object.
   */
  private int internalObjectIDCounter;
  
  //endregion
  
  //region Constructor
  
  /**
   * Constructs a new empty map with specified width and height.
   * 
   * @param width
   *          The map's width
   * @param height
   *          The map's height
   */
  public Map(int width, int height) {
    mapWidth = width;
    mapHeight = height;
    //Map will have (width * height) positions
    mapContent = new GameObject[width * height][INTERNAL_MAP_LAYERS];
    //Reset the Player and Unit ID counters
    internalPlayerIDCounter = -1;
    internalObjectIDCounter = -1;
  }
  
  //endregion
  
  //region Public methods
  
  /**
   * Returns the positional index corresponding to the provided {@link MapLocation} for the current
   * map.
   * 
   * @param location
   *          The location to get the position for.
   * @return Positional index on the internal map representation for the provided location.
   */
  public int toPosition(MapLocation location) {
    return toPosition(location, mapWidth);
  }
  
  /**
   * Returns the positional index corresponding to the provided {@link MapLocation} for a map with
   * specified width.
   * 
   * @param location
   *          The location to get the position for.
   * @param width
   *          The width of the map.
   * @return Positional index on the internal map representation for the provided location.
   */
  public static int toPosition(MapLocation location, int width) {
    return toPosition(location.getX(), location.getY(), width);
  }
  
  /**
   * Returns the positional index corresponding to the provided X and Y coordinates for the current
   * map.
   * 
   * @param x
   *          X coordinate.
   * @param y
   *          Y coordinate.
   * @return Positional index on the internal map representation for the provided coordinates.
   */
  public int toPosition(int x, int y) {
    return toPosition(x, y, mapWidth);
  }
  
  /**
   * Returns the positional index corresponding to the provided X and Y coordinates for a map with
   * specified width.
   * 
   * @param x
   *          X coordinate.
   * @param y
   *          Y coordinate.
   * @param width
   *          The width of the map.
   * @return Positional index on the internal map representation for the provided coordinates.
   */
  public static int toPosition(int x, int y, int width) {
    return (y * width) + x;
  }
  
  /**
   * Returns the {@link MapLocation} for a specific positional index.
   * 
   * @param position
   *          The positional index to get the location for.
   * @return {@link MapLocation} corresponding to the positional index.
   */
  public MapLocation toLocation(int position) {
    return toLocation(position, mapWidth);
  }
  
  /**
   * Returns the {@link MapLocation} for a positional index on a map with a specific width.
   * 
   * @param position
   *          The positional index to get the location for.
   * @param width
   *          The width of the map.
   * @return {@link MapLocation} corresponding to the positional index.
   */
  public static MapLocation toLocation(int position, int width) {
    return new MapLocation(position % width, position / width);
  }
  
  /**
   * Whether or not the specified X-coordinate is within this Map's boundaries.
   * 
   * @param x
   *          The X-coordinate.
   * @return Boolean value indicating if the coordinate is on the map.
   */
  public boolean isXonMap(int x) {
    return x >= 0 && x < mapWidth;
  }
  
  /**
   * Whether or not the specified Y-coordinate is within this Map's boundaries.
   * 
   * @param y
   *          The Y-coordinate.
   * @return Boolean value indicating if the coordinate is on the map.
   */
  public boolean isYonMap(int y) {
    return y >= 0 && y < mapHeight;
  }
  
  /**
   * Whether or not the specified location is traversable. This method checks:
   * <ul>
   * <li>If the {@link MapLocation} is on the map</li>
   * <li>If the {@link MapFeature} is walkable</li>
   * <li>If there is no {@link Unit} present on the location</li>
   * </ul>
   * 
   * @param location
   *          The location to check
   * @return Boolean value indicating if the location is traversable.
   */
  public boolean isTraversable(MapLocation location) {
    int locationPosition = toPosition(location);
    //Check some things
    boolean validX = isXonMap(location.getX());
    boolean validY = isYonMap(location.getY());
    //A tile can be walked on/over if there is no Unit in the way, and the MapFeature on that location is walkable
    boolean unitPresent = mapContent[locationPosition][INTERNAL_MAP_UNIT_INDEX] != null;
    boolean isFeature = mapContent[locationPosition][INTERNAL_MAP_FEATURE_INDEX] != null && mapContent[locationPosition][INTERNAL_MAP_FEATURE_INDEX] instanceof MapFeature;
    boolean featureWalkable = isFeature && ((MapFeature)mapContent[locationPosition][INTERNAL_MAP_FEATURE_INDEX]).isWalkable();
    boolean walkable = !unitPresent && featureWalkable;
    //Traversable if all three are ok
    return validX && validY && walkable;
  }
  
  /**
   * Returns whether or not a {@link UnitAction} that describes a move is possible from a specific
   * location.
   * 
   * @param fromLocation
   *          The location to move from.
   * @param move
   *          The action the unit is attempting to make.
   * @return
   */
  public boolean isMovePossible(MapLocation fromLocation, UnitAction move) {
    //Switch on the type of move described in the UnitAction
    switch(move.getActionType()) {
      case MOVE_NORTH:
        return isMovePossible(fromLocation, Direction.NORTH);
      case MOVE_EAST:
        return isMovePossible(fromLocation, Direction.EAST);
      case MOVE_SOUTH:
        return isMovePossible(fromLocation, Direction.SOUTH);
      case MOVE_WEST:
        return isMovePossible(fromLocation, Direction.WEST);
      default:
        //Rest of the UnitActionTypes are not moves, so they should return false
        return false;
    }
  }
  
  /**
   * Returns whether or not a move in a {@link Direction} is possible on this map.
   * 
   * @param fromLocation
   *          The location the move is from.
   * @param direction
   *          The direction to move to.
   * @return
   */
  public boolean isMovePossible(MapLocation fromLocation, Direction direction) {
    //Determine the target location
    MapLocation targetLocation = getAdjacentLocationInDirection(fromLocation, direction);
    //Start by checking if the target location exists on the map
    if(targetLocation == null)
      return false;
    //Check if the target location is traversable
    return isTraversable(targetLocation);
  }
  
  /**
   * Removes the object from the map and places it on the target location.
   * 
   * @param targetLocation
   *          The location to place the object on.
   * @param object
   *          The object to move.
   * @return Whether or not the move was successful.
   */
  public boolean move(MapLocation targetLocation, GameObject object) {
    boolean success = false;
    //Check if the targetLocation is traversable
    if(!isTraversable(targetLocation))
      return false;
    //Check if the object is a Unit
    if(!(object instanceof Unit))
      return false;
    //Remove the object
    success = remove(toPosition(object.getLocation()), object);
    //Only continue with placement if removal was successful
    if(success)
      success = place(toPosition(targetLocation), object);
    return success;
  }
  
  /**
   * Returns the {@link MapLocation} that is 1 tile from a location. Returns null if no such
   * location exists.
   * 
   * @param location
   *          The location to start from.
   * @param direction
   *          The {@link Direction} to go to.
   * @return
   */
  public MapLocation getAdjacentLocationInDirection(MapLocation location, Direction direction) {
    return getLocationInDirection(location, direction, 1);
  }
  
  /**
   * Returns the {@link MapLocation} that is a distance away from a location. Returns null if no
   * such location exists.
   * 
   * @param location
   *          The location to start from.
   * @param direction
   *          The {@link Direction} to go to.
   * @param distance
   *          The amount of tiles to go.
   * @return
   */
  public MapLocation getLocationInDirection(MapLocation location, Direction direction, int distance) {
    int targetPosition = getPositionInDirection(toPosition(location), direction, distance);
    return targetPosition >= 0 ? toLocation(targetPosition) : null;
  }
  
  /**
   * Returns the positional index that is a distance away from a position.
   * 
   * @param position
   *          The positional index to start from.
   * @param direction
   *          The {@link Direction} to go to.
   * @param distance
   *          The amount of tiles to go.
   * @return
   */
  public int getPositionInDirection(int position, Direction direction, int distance) {
    if(distance < 0)
      return -1;
    int targetPosition = -1;
    switch(direction) {
      case NORTH:
        //North is decreasing Y, equal X. Or in positions; -(width * distance)
        targetPosition = position - (mapWidth * distance);
        break;
      case EAST:
        //East is equal Y, increasing X. Or in positions; +distance
        targetPosition = position + distance;
        break;
      case SOUTH:
        //South is increasing Y, equal X. Or in positions; +(width * distance)
        targetPosition = position + (mapWidth * distance);
        break;
      case WEST:
        //West is equal Y, decreasing X. Or in positions; -distance
        targetPosition = position - distance;
        break;
      default:
        System.err.println("UNHANDLED DIRECTION TYPE!");
    }
    //Check if the target position doesn't go out of bounds.
    return targetPosition > 0 && targetPosition < mapWidth * mapHeight ? targetPosition : -1;
  }
  
  /**
   * Returns the neighbours in the available {@link Direction}s to a location. Only locations that
   * are on the map are returned.
   * 
   * @param location
   *          The location to get the neighbours for.
   * @return A collections of MapLocations.
   */
  public List<MapLocation> getNeighbours(MapLocation location) {
    //Set up a list
    List<MapLocation> neighbours = new ArrayList<MapLocation>();
    //Go through all directions
    for(Direction direction : Direction.values()) {
      //See if there is an adjacent location
      MapLocation adjacent = getAdjacentLocationInDirection(location, direction);
      if(adjacent != null)
        //If so, add it to the list
        neighbours.add(adjacent);
    }
    //Return the list
    return neighbours;
  }
  
  /**
   * Returns the number of {@link Base}s on the map. Used to determine if the game has ended. (Note:
   * game ends when only 1 base remains)
   * 
   * @return
   */
  public int getCurrentBaseCount() {
    //Bases are MapFeatures
    int count = 0;
    for(int i = 0; i < mapWidth * mapHeight; i++) {
      if(mapContent[i][INTERNAL_MAP_FEATURE_INDEX] instanceof Base)
        count++;
    }
    return count;
  }
  
  /**
   * Returns the next available ID for a new player.
   * 
   * @return
   */
  public int requestNewPlayerID() {
    //Return the next ID
    internalPlayerIDCounter++;
    return internalPlayerIDCounter;
  }
  
  /**
   * Returns the next available ID for a new game object.
   * 
   * @return
   */
  public int requestNewGameObjectID() {
    //Return the next ID
    internalObjectIDCounter++;
    return internalObjectIDCounter;
  }
  
  /**
   * Returns a string representation of the current map state. In this representation the left side
   * is the MapFeature layer while the right side is the Unit layer.
   */
  public String toString() {
    StringBuilder builder = new StringBuilder();
    for(int y = 0; y < mapHeight; y++) {
      //This will be one line of the printed map
      StringBuilder lineBuilder = new StringBuilder();
      //Separate the two layers with ' | '
      lineBuilder.append(TOSTRING_LAYER_SEPARATOR);
      for(int x = 0; x < mapWidth; x++) {
        int currentPosition = toPosition(x, y);
        GameObject[] objects = mapContent[currentPosition];
        String featureLevel = objects[INTERNAL_MAP_FEATURE_INDEX] != null ? objects[INTERNAL_MAP_FEATURE_INDEX].toString() : " ";
        String unitLevel = objects[INTERNAL_MAP_UNIT_INDEX] != null ? objects[INTERNAL_MAP_UNIT_INDEX].toString() : " ";
        //Add feature level first, unit level on other side
        lineBuilder.insert(x, featureLevel);
        lineBuilder.insert(x + TOSTRING_LAYER_SEPARATOR.length(), unitLevel);
      }
      //Done with the line, append it
      builder.append(lineBuilder.toString() + "\n");
    }
    return builder.toString();
  }
  
  /**
   * Creates a deep copy of this map.
   * 
   * @return
   */
  public Map copy() {
    //Create a new map
    Map newMap = new Map(this.mapWidth, this.mapHeight);
    //Deep copy the map content
    GameObject[][] content = copyMapContent();
    //Set some things
    newMap.setObjectIDCounter(this.internalObjectIDCounter);
    newMap.setPlayerIDCounter(this.internalPlayerIDCounter);
    newMap.setMapContent(content);
    //Return the created map
    return newMap;
  }
  
  //endregion
  
  //region Protected methods
  
  /**
   * Places a {@link GameObject} on the map.
   * 
   * @param position
   *          The position on the map to place the object at.
   * @param object
   *          The object to place.
   * @return Whether or not the placement was successful.
   */
  protected boolean place(int position, GameObject object) {
    //Check which layer the object needs to be at
    int layer = -1;
    if(object instanceof MapFeature)
      layer = INTERNAL_MAP_FEATURE_INDEX;
    else if(object instanceof Unit)
      layer = INTERNAL_MAP_UNIT_INDEX;
    else {
      System.err.println("WARNING: Unable to place object on map, unknown type");
      return false;
    }
    //Check if there isn't already an object at the specified position
    if(mapContent[position][layer] != null) {
      System.err.println("WARNING: Unable to place object on map, space occupied");
      return false;
    }
    //Place the object
    object.setLocation(toLocation(position));
    mapContent[position][layer] = object;
    return true;
  }
  
  /**
   * Removes a {@link GameObject} from the map.
   * 
   * @param position
   *          The position on the map to remove the object from.
   * @param object
   *          The object to remove.
   * @return Whether or not the removal was successful.
   */
  protected boolean remove(int position, GameObject object) {
    //Check which layer the object should be at
    int layer = -1;
    if(object instanceof MapFeature)
      layer = INTERNAL_MAP_FEATURE_INDEX;
    else if(object instanceof Unit)
      layer = INTERNAL_MAP_UNIT_INDEX;
    else {
      System.err.println("WARNING: Unable to remove object from map, unknown type");
      return false;
    }
    //Check if there is an object to remove
    if(mapContent[position][layer] == null) {
      System.err.println("WARNING: Unable to remove object from map, space is empty");
      return false;
    }
    //Check if the object to remove is equal to the object currently at that position
    if(!mapContent[position][layer].equals(object)) {
      System.err.println("WARNING: Unable to remove object from map, no matching object found");
      return false;
    }
    //Remove the object
    mapContent[position][layer] = null;
    return true;
  }
  
  /**
   * Ticks the map forward, this means that we should check the {@link GameObject}s on the map to
   * see if any should be removed.
   * 
   * @param state
   *          The current state of the game.
   */
  protected void tick(HunterKillerState state) {
    for(int i = 0; i < mapWidth * mapHeight; i++) {
      for(int j = 0; j < INTERNAL_MAP_LAYERS; j++) {
        GameObject object = mapContent[i][j];
        //Check if there is anything there
        if(object != null && object.tick(state)) {
          //Returning true indicates that the object should be removed
          remove(i, object);
        }
      }
    }
  }
  
  /**
   * Set the content of this Map.
   * 
   * @param content
   *          The content of the {@link Map}.
   */
  protected void setMapContent(GameObject[][] content) {
    this.mapContent = content;
  }
  
  /**
   * Set the player ID counter to a specific number. Mainly used for copy method.
   * 
   * @param counter
   *          The number the counter should be set to.
   */
  protected void setPlayerIDCounter(int counter) {
    this.internalPlayerIDCounter = counter;
  }
  
  /**
   * Set the object ID counter to a specific number. Mainly used for copy method.
   * 
   * @param counter
   *          The number the counter should be set to.
   */
  protected void setObjectIDCounter(int counter) {
    this.internalObjectIDCounter = counter;
  }
  
  /**
   * Creates a deep copy of this map's content.
   * 
   * @return
   */
  protected GameObject[][] copyMapContent() {
    int positions = this.mapWidth * this.mapHeight;
    //Create a new content array
    GameObject[][] newContent = new GameObject[positions][INTERNAL_MAP_LAYERS];
    for(int i = 0; i < positions; i++) {
      for(int j = 0; j < INTERNAL_MAP_LAYERS; j++) {
        GameObject object = this.mapContent[i][j];
        //Check if we are dealing with a MapFeature
        if(object != null && object instanceof MapFeature) {
          //Maintain the original ID here, because we also copy the counters in the map.
          if(object instanceof Base) {
            Base base = (Base)object;
            newContent[i][j] = base.copy(base.getID());
          }
          else if(object instanceof Door) {
            Door door = (Door)object;
            newContent[i][j] = door.copy(door.getID());
          }
          else if(object instanceof Floor) {
            Floor floor = (Floor)object;
            newContent[i][j] = floor.copy(floor.getID());
          }
          else if(object instanceof Space) {
            Space space = (Space)object;
            newContent[i][j] = space.copy(space.getID());
          }
          else if(object instanceof Wall) {
            Wall wall = (Wall)object;
            newContent[i][j] = wall.copy(wall.getID());
          }
          else {
            System.err.println("WARNING: Unknown MapFeature type found while copying content!");
          }
        }
        else if(object != null && object instanceof Unit) {
          //Or if we are dealing with a Unit
          if(object instanceof Infected) {
            //Maintain the original ID here, because we also copy the counters in the map.
            Infected infected = (Infected)object;
            newContent[i][j] = infected.copy(infected.getID());
          }
          else if(object instanceof Medic) {
            Medic medic = (Medic)object;
            newContent[i][j] = medic.copy(medic.getID());
          }
          else if(object instanceof Soldier) {
            Soldier soldier = (Soldier)object;
            newContent[i][j] = soldier.copy(soldier.getID());
          }
          else {
            System.err.println("WARNING: Unknown Unit type found while copying content!");
          }
        }
        //Otherwise just leave this null
      }
    }
    //Return the created content
    return newContent;
  }
  
  //endregion
}
