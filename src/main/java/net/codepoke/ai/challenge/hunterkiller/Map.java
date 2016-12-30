package net.codepoke.ai.challenge.hunterkiller;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.codepoke.ai.challenge.hunterkiller.LineOfSight.BlocksLightFunction;
import net.codepoke.ai.challenge.hunterkiller.LineOfSight.GetDistanceFunction;
import net.codepoke.ai.challenge.hunterkiller.LineOfSight.SetVisibleFunction;
import net.codepoke.ai.challenge.hunterkiller.enums.Direction;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.GameObject;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.mapfeature.Base;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.mapfeature.Door;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.mapfeature.Floor;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.mapfeature.MapFeature;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.mapfeature.Space;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.mapfeature.Wall;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Infected;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Medic;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Soldier;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Unit;
import net.codepoke.ai.challenge.hunterkiller.orders.UnitOrder;

/**
 * The map on which HunterKiller is played. The map is internally represented as a 2-dimensional
 * array. The first dimension contains all locations on the map (width * height) mapped to a
 * positional index ((y * width) + x). The second dimension contains 2 layers of {@link GameObject}
 * s, where the first layer contains all {@link MapFeature} objects that are present on the map
 * (e.g. {@link Wall}, {@link Floor}), and the second layer contains all {@link Unit} objects (e.g.
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
  
  /**
   * The line-of-sight implementation that is used on this map.
   */
  private LineOfSight lineOfSight;
  
  /**
   * Contains this map's implementation of whether or not a location blocks line-of-sight.
   */
  private BlocksLight blocksLight;
  
  /**
   * Contains this map's implementation of the distance measure of a location from 0,0.
   */
  private GetDistance getDistance;
  
  /**
   * Contains this map's implementation of how to handle locations that are deemed visible from
   * another location.
   */
  private SetVisible setVisible;
  
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
    //Create the classes required for line-of-sight
    blocksLight = new BlocksLight(this);
    getDistance = new GetDistance();
    setVisible = new SetVisible();
    lineOfSight = new LineOfSight(blocksLight, setVisible, getDistance);
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
   * Whether or not a {@link MapLocation} is on this map.
   * 
   * @param location
   *          The location.
   * @return
   */
  public boolean isOnMap(MapLocation location) {
    return isXonMap(location.getX()) && isYonMap(location.getY());
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
   * Returns whether or not a {@link UnitOrder} that describes a move is possible from a specific
   * location.
   * 
   * @param fromLocation
   *          The location to move from.
   * @param move
   *          The action the unit is attempting to make.
   * @return
   */
  public boolean isMovePossible(MapLocation fromLocation, UnitOrder move) {
    //Check that there is a unit on the origin location
    if(mapContent[toPosition(fromLocation)][INTERNAL_MAP_UNIT_INDEX] == null)
      return false;
    //Check that the unit that is trying to move is actually at the location they are trying to move from
    if(move.getObjectID() != ((Unit)mapContent[toPosition(fromLocation)][INTERNAL_MAP_UNIT_INDEX]).getID())
      return false;
    
    //Switch on the type of move described in the UnitOrder
    switch(move.getOrderType()) {
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
    int targetPosition = toPosition(targetLocation);
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
      success = place(targetPosition, object);
    //If the move was successful and the target location was a closed Door, open it
    if(success && mapContent[targetPosition][INTERNAL_MAP_FEATURE_INDEX] instanceof Door) {
      Door door = (Door)mapContent[targetPosition][INTERNAL_MAP_FEATURE_INDEX];
      if(!door.isOpen())
        door.open();
    }
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
   * Returns a collection of {@link MapLocation}s that are directly around a centre location. This
   * method is like 'getNeighbours', except that it returns locations in up to 8 directions.
   * 
   * @param location
   *          The centre location.
   * @param includeCentre
   *          Whether or not the centre location should also be included in the result collection.
   * @return
   */
  public List<MapLocation> getAreaAround(MapLocation location, boolean includeCentre) {
    //Set up a list
    List<MapLocation> area = getNeighbours(location);
    //Get the corner positions (North-East, South-East, South-West, North-West)
    //North-East is increasing X, decreasing Y.
    MapLocation northEast = new MapLocation(location.getX() + 1, location.getY() - mapWidth);
    if(isOnMap(northEast))
      area.add(northEast);
    //South-East is increasing X, increasing Y.
    MapLocation southEast = new MapLocation(location.getX() + 1, location.getY() + mapWidth);
    if(isOnMap(southEast))
      area.add(southEast);
    //South-West is decreasing X, increasing Y.
    MapLocation southWest = new MapLocation(location.getX() - 1, location.getY() + mapWidth);
    if(isOnMap(southWest))
      area.add(southWest);
    //North-West is decreasing X, decreasing Y.
    MapLocation northWest = new MapLocation(location.getX() - 1, location.getY() - mapWidth);
    if(isOnMap(northWest))
      area.add(northWest);
    //If the centre was also requested, add it
    if(includeCentre)
      area.add(location);
    //Return the list of locations
    return area;
  }
  
  /**
   * Returns a collection of locations that are in the field-of-view from a certain location.
   * 
   * @param location
   *          The location to view from.
   * @param viewRange
   *          The range of the view.
   * @return
   */
  public List<MapLocation> getFieldOfView(MapLocation location, int viewRange) {
    //Reset any previously computed locations
    setVisible.resetLocations();
    //Ask the line-of-sight implementation to compute the field-of-view
    lineOfSight.compute(location, viewRange);
    //Return the list of computed locations
    return setVisible.getVisibleLocations();
  }
  
  /**
   * Returns the object with the specified ID. If no such object can be found, null is returned.
   * 
   * @param objectID
   *          The unique identifier of the object.
   * @return The object, or null if no object was found.
   */
  public GameObject getObject(int objectID) {
    //Check the map content
    for(int i = 0; i < mapWidth * mapHeight; i++) {
      for(int j = 0; j < INTERNAL_MAP_LAYERS; j++) {
        if(mapContent[i][j] != null && mapContent[i][j].getID() == objectID) {
          return mapContent[i][j];
        }
      }
    }
    return null;
  }
  
  /**
   * Returns the location of the object with the specified ID. If no such object can be found, null
   * is returned.
   * 
   * @param objectID
   *          The unique identifier of the object.
   * @return The {@link MapLocation} of the object, or null if no object was found.
   */
  public MapLocation getObjectLocation(int objectID) {
    //Check the map content
    for(int i = 0; i < mapWidth * mapHeight; i++) {
      for(int j = 0; j < INTERNAL_MAP_LAYERS; j++) {
        if(mapContent[i][j] != null && mapContent[i][j].getID() == objectID) {
          return toLocation(i);
        }
      }
    }
    return null;
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
  
  //region Overridden methods
  
  /**
   * Returns a string representation of the current map state. In this representation the left side
   * is the MapFeature layer while the right side is the Unit layer.
   */
  @Override
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
  
  @Override
  public int hashCode() {
    //We've chosen a prime number close to 50 to make the hash code slightly more like a number that does not occur naturally
    int output = 43;
    //Move through all positions on the map
    for(int i = 0; i < mapContent.length; i++) {
      GameObject[] cell = mapContent[i];
      for(int j = 0; j < cell.length; j++) {
        if(cell[j] == null) {
          //Ignore positions that do not contain anything
          continue;
        }
        //Update the output; XOR the ID of the object with the coordinates (i = position, j = level)
        output ^= cell[j].getID() ^ i ^ j;
      }
    }
    //Return the created code
    return output;
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
   * see if any should be removed. This method also updates the field-of-view of any surviving
   * {@link Unit}s.
   * 
   * @param state
   *          The current state of the game.
   */
  protected void tick(HunterKillerState state) {
    //Check the map content for 'dead' objects
    for(int i = 0; i < mapWidth * mapHeight; i++) {
      for(int j = 0; j < INTERNAL_MAP_LAYERS; j++) {
        GameObject object = mapContent[i][j];
        //Check if there is anything there
        if(object != null) {
          if(object.tick(state)) {
            //Returning true indicates that the object should be removed
            remove(i, object);
            //If the object is a Unit, tell it's Player to remove it from it's squad
            if(object instanceof Unit) {
              Unit unit = (Unit)object;
              state.getPlayer(unit.getSquadPlayerID()).removeUnitFromSquad(unit);
            }
          }
          else {
            //Means that the object is still alive, so check if it's a Unit
            if(object instanceof Unit) {
              Unit unit = (Unit)object;
              //Get the field-of-view collection from the unit's location
              List<MapLocation> fieldOfView = getFieldOfView(unit.getLocation(), unit.getFieldOfViewRange());
              //Tell the unit to update it's field-of-view
              unit.updateFieldOfView(fieldOfView);
            }
          }
        }
      }
    }
  }
  
  /**
   * Reduces the timers on open doors and unit's cooldowns. This method should be called at the
   * start of a new round.
   */
  protected void timer() {
    for(int i = 0; i < mapWidth * mapHeight; i++) {
      for(int j = 0; j < INTERNAL_MAP_LAYERS; j++) {
        GameObject object = mapContent[i][j];
        //Check if there is anything there
        if(object != null) {
          //Check if it's a Door
          if(object instanceof Door) {
            //Reduce the timer if the door is open
            Door door = (Door)object;
            if(door.isOpen())
              door.reduceTimer();
          }
          //Check if it's a Unit
          else if(object instanceof Unit) {
            //Reduce the unit's cooldown
            ((Unit)object).reduceCooldown();
          }
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
  
  /**
   * Attack a location on the map.
   * 
   * @param location
   *          The {@link MapLocation} to attack.
   * @param damage
   *          The damage to inflict.
   * @return Whether or not the attack was successful.
   */
  protected boolean attackLocation(MapLocation location, int damage) {
    //Check if the location is on the map
    if(!(isXonMap(location.getX()) && isYonMap(location.getY())))
      return false;
    int position = toPosition(location);
    //Get the map feature on this position
    MapFeature feature = (MapFeature)mapContent[position][INTERNAL_MAP_FEATURE_INDEX];
    //If the feature is destructible, reduce it's HP by the damage
    if(feature.isDestructible())
      feature.reduceHP(damage);
    //Check if there is a Unit on this position
    if(mapContent[position][INTERNAL_MAP_UNIT_INDEX] != null)
      mapContent[position][INTERNAL_MAP_UNIT_INDEX].reduceHP(damage);
    return true;
  }
  
  //endregion
  
  //region Private classes
  
  private class BlocksLight implements BlocksLightFunction {
    
    private Map map;
    
    public BlocksLight(Map map) {
      this.map = map;
    }
    
    @Override
    public boolean func(int x, int y) {
      //Check if both coordinates are within the map's bounds
      if(!isXonMap(x) || !isYonMap(y))
        return true;
      //Check the feature at the specified position
      return ((MapFeature)map.getMapContent()[map.toPosition(x, y)][Map.INTERNAL_MAP_FEATURE_INDEX]).isBlockingLOS();
    }
    
  }
  
  @NoArgsConstructor
  private class GetDistance implements GetDistanceFunction {
    
    @Override
    public int func(int x, int y) {
      return MapLocation.getManhattanDist(0, 0, x, y);
    }
    
  }
  
  private class SetVisible implements SetVisibleFunction {
    
    @Getter
    private List<MapLocation> visibleLocations;
    
    public SetVisible() {
      this.visibleLocations = new ArrayList<MapLocation>();
    }
    
    public void resetLocations() {
      this.visibleLocations = new ArrayList<MapLocation>();
    }
    
    @Override
    public void func(int x, int y) {
      //Ignore any coordinates that are not on the map
      if(isXonMap(x) && isYonMap(y))
        this.visibleLocations.add(new MapLocation(x, y));
    }
    
  }
  
  //endregion
}
