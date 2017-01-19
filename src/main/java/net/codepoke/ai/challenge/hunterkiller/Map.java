package net.codepoke.ai.challenge.hunterkiller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
import net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Medic;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Soldier;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Unit;
import net.codepoke.ai.challenge.hunterkiller.orders.UnitOrder;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.BinaryHeap;
import com.badlogic.gdx.utils.BinaryHeap.Node;
import com.badlogic.gdx.utils.IntArray;

/**
 * The map on which HunterKiller is played. The map is internally represented as a 2-dimensional
 * array. The first dimension contains all locations on the map (width * height) mapped to a
 * positional index ((y * width) + x). The second dimension contains 2 layers of {@link GameObject} s, where the first
 * layer contains all {@link MapFeature} objects that are present on the map
 * (e.g. {@link Wall}, {@link Floor}), and the second layer contains all {@link Unit} objects (e.g. {@link Soldier},
 * {@link Medic}). Note that the width of the map is treated as the X-axis, and the
 * height as the Y-axis. Also, the coordinate (0,0) is the top-left corner of the map.
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
@Getter
public class Map {

	// region Properties

	/**
	 * The name of this map.
	 */
	public String name;

	/**
	 * The height of this map.
	 */
	private int mapHeight;

	/**
	 * The width of this map.
	 */
	private int mapWidth;

	/**
	 * This is the internal representation. See {@link Map} for details.
	 */
	@Setter
	private GameObject[][] mapContent;

	/**
	 * Collection of objects present on this map, indexed by ID.
	 */
	@Setter
	private Array<GameObject> objects;

	/**
	 * A temporary storage for IDs that are currently not being owned by an object.
	 */
	@Setter
	private IntArray idBuffer;

	/**
	 * The line-of-sight implementation that is used on this map.
	 */
	private transient LineOfSight lineOfSight;

	// endregion

	// region Constructor

	public Map() {
		lineOfSight = new LineOfSight(new BlocksLight(), new SetVisible(), new GetManhattanDistance());
	}

	/**
	 * Constructs a new empty map with specified width and height.
	 * 
	 * @param name
	 *            The map's name
	 * @param width
	 *            The map's width
	 * @param height
	 *            The map's height
	 */
	public Map(String name, int width, int height) {
		this.name = name;
		mapWidth = width;
		mapHeight = height;
		// Map will have (width * height) positions
		mapContent = new GameObject[width * height][Constants.MAP_INTERNAL_LAYERS];
		// Create new collections for our ID->Object lookup and ID buffer
		objects = new Array<GameObject>(true, width * height);
		idBuffer = new IntArray();
		// Create the classes required for line-of-sight
		lineOfSight = new LineOfSight(new BlocksLight(), new SetVisible(), new GetManhattanDistance());
	}

	// endregion

	// region Public methods

	/**
	 * Returns the positional index corresponding to the provided {@link MapLocation} for the current
	 * map.
	 * 
	 * @param location
	 *            The location to get the position for.
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
	 *            The location to get the position for.
	 * @param width
	 *            The width of the map.
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
	 *            X coordinate.
	 * @param y
	 *            Y coordinate.
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
	 *            X coordinate.
	 * @param y
	 *            Y coordinate.
	 * @param width
	 *            The width of the map.
	 * @return Positional index on the internal map representation for the provided coordinates.
	 */
	public static int toPosition(int x, int y, int width) {
		return (y * width) + x;
	}

	/**
	 * Returns the {@link MapLocation} for a specific positional index.
	 * 
	 * @param position
	 *            The positional index to get the location for.
	 * @return {@link MapLocation} corresponding to the positional index.
	 */
	public MapLocation toLocation(int position) {
		return toLocation(position, mapWidth);
	}

	/**
	 * Returns the {@link MapLocation} for a positional index on a map with a specific width.
	 * 
	 * @param position
	 *            The positional index to get the location for.
	 * @param width
	 *            The width of the map.
	 * @return {@link MapLocation} corresponding to the positional index.
	 */
	public static MapLocation toLocation(int position, int width) {
		return new MapLocation(position % width, position / width);
	}

	/**
	 * Whether or not the specified X-coordinate is within this Map's boundaries.
	 * 
	 * @param x
	 *            The X-coordinate.
	 * @return Boolean value indicating if the coordinate is on the map.
	 */
	public boolean isXonMap(int x) {
		return x >= 0 && x < mapWidth;
	}

	/**
	 * Whether or not the specified Y-coordinate is within this Map's boundaries.
	 * 
	 * @param y
	 *            The Y-coordinate.
	 * @return Boolean value indicating if the coordinate is on the map.
	 */
	public boolean isYonMap(int y) {
		return y >= 0 && y < mapHeight;
	}

	/**
	 * Whether or not a {@link MapLocation} is on this map.
	 * 
	 * @param location
	 *            The location.
	 * @return
	 */
	public boolean isOnMap(MapLocation location) {
		return isXonMap(location.getX()) && isYonMap(location.getY());
	}

	/**
	 * Whether or not the specified location is traversable. This method does not print the reasons why it returns
	 * false. See {@link Map#isTraversable(MapLocation, boolean)}.
	 */
	public boolean isTraversable(MapLocation location) {
		return isTraversable(location, false);
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
	 *            The location to check.
	 * @param printFailReasons
	 *            Whether or not reasons for failure should be printed.
	 * @return Boolean value indicating if the location is traversable.
	 */
	public boolean isTraversable(MapLocation location, boolean printFailReasons) {
		int locationPosition = toPosition(location);
		// Check if the coordinates exist
		boolean validX = isXonMap(location.getX());
		if (!validX && printFailReasons)
			System.out.printf("WARNING: Location not traversable, X-coordinate is not on the map (%d).%n", location.getX());
		boolean validY = isYonMap(location.getY());
		if (!validY && printFailReasons)
			System.out.printf("WARNING: Location not traversable, Y-coordinate is not on the map (%d).%n", location.getY());

		// There is a unit on a square if the content of the unit layer is not null
		boolean unitPresent = mapContent[locationPosition][Constants.MAP_INTERNAL_UNIT_INDEX] != null;
		if (unitPresent && printFailReasons)
			System.out.println("WARNING: Location not traversable, Unit present.");

		// A feature can be walked on/over if it is walkable, derp
		boolean featureWalkable = ((MapFeature) mapContent[locationPosition][Constants.MAP_INTERNAL_FEATURE_INDEX]).isWalkable();
		if (!featureWalkable && printFailReasons)
			System.out.println("WARNING: Location not traversable, MapFeature is not walkable.");

		// Define walkable
		boolean walkable = !unitPresent && featureWalkable;
		// Traversable if all three are true
		return validX && validY && walkable;
	}

	/**
	 * Returns whether or not a {@link UnitOrder} that describes a move is possible from a specific
	 * location.
	 * 
	 * @param fromLocation
	 *            The location to move from.
	 * @param move
	 *            The action the unit is attempting to make.
	 * @return
	 */
	public boolean isMovePossible(MapLocation fromLocation, UnitOrder move) {
		// Make sure that there is a unit on the origin location
		if (mapContent[toPosition(fromLocation)][Constants.MAP_INTERNAL_UNIT_INDEX] == null) {
			System.out.printf("WARNING: Move not possible, no Unit on origin location (%s).%n", fromLocation);
			return false;
		}
		// Make sure that the unit that is trying to move is actually at the location they are trying to move from
		if (move.getObjectID() != ((Unit) mapContent[toPosition(fromLocation)][Constants.MAP_INTERNAL_UNIT_INDEX]).getID()) {
			System.out.printf(	"WARNING: Move not possible, subject Unit (ID: %d) is not on origin location (%s).%n",
								move.getObjectID(),
								fromLocation);
			return false;
		}

		// Switch on the type of move described in the UnitOrder
		switch (move.getOrderType()) {
		case MOVE_NORTH:
			return isMovePossible(fromLocation, Direction.NORTH) && move.getTargetLocation()
																		.equals(getLocationInDirection(fromLocation, Direction.NORTH, 1));
		case MOVE_EAST:
			return isMovePossible(fromLocation, Direction.EAST) && move.getTargetLocation()
																		.equals(getLocationInDirection(fromLocation, Direction.EAST, 1));
		case MOVE_SOUTH:
			return isMovePossible(fromLocation, Direction.SOUTH) && move.getTargetLocation()
																		.equals(getLocationInDirection(fromLocation, Direction.SOUTH, 1));
		case MOVE_WEST:
			return isMovePossible(fromLocation, Direction.WEST) && move.getTargetLocation()
																		.equals(getLocationInDirection(fromLocation, Direction.WEST, 1));
		default:
			// Rest of the UnitActionTypes are not moves, so they should return false
			System.out.printf("WARNING: Move not possible, unsupported move-order type (%s).%n", move.getOrderType());
			return false;
		}
	}

	/**
	 * Returns whether or not a move in a {@link Direction} is possible on this map.
	 * 
	 * @param fromLocation
	 *            The location the move is from.
	 * @param direction
	 *            The direction to move to.
	 * @return
	 */
	public boolean isMovePossible(MapLocation fromLocation, Direction direction) {
		// Determine the target location
		MapLocation targetLocation = getAdjacentLocationInDirection(fromLocation, direction);
		// Start by checking if the target location exists on the map
		if (targetLocation == null)
			return false;
		// Check if the target location is traversable
		return isTraversable(targetLocation);
	}

	/**
	 * Removes the object from the map and places it on the target location.
	 * 
	 * @param targetLocation
	 *            The location to place the object on.
	 * @param object
	 *            The object to move.
	 * @return Whether or not the move was successful.
	 */
	public boolean move(MapLocation targetLocation, GameObject object) {
		boolean success = false;
		int targetPosition = toPosition(targetLocation);
		// Check if the targetLocation is traversable
		if (!isTraversable(targetLocation)) {
			System.out.println("WARNING: Unable to move, location not traversable.");
			return false;
		}
		// Check if the object is a Unit
		if (!(object instanceof Unit)) {
			System.out.println("WARNING: Unable to move, object is not a Unit.");
			return false;
		}
		// Remove the object
		success = remove(object.getLocation(), object);
		// Only continue with placement if removal was successful
		if (success)
			success = place(targetPosition, object);
		// If the move was successful and the target location was a closed Door, open it
		if (success && mapContent[targetPosition][Constants.MAP_INTERNAL_FEATURE_INDEX] instanceof Door) {
			Door door = (Door) mapContent[targetPosition][Constants.MAP_INTERNAL_FEATURE_INDEX];
			if (!door.isOpen())
				door.open();
		}
		return success;
	}

	/**
	 * Returns the {@link MapLocation} that is 1 tile from a location. Returns null if no such
	 * location exists.
	 * 
	 * @param location
	 *            The location to start from.
	 * @param direction
	 *            The {@link Direction} to go to.
	 * @return
	 */
	public MapLocation getAdjacentLocationInDirection(MapLocation location, Direction direction) {
		return getLocationInDirection(location, direction, 1);
	}

	/**
	 * Returns the {@link MapLocation} that is a distance away from a location. Returns null if no
	 * such location exists, or if the distance is large enough to wrap around the map.
	 * 
	 * @param location
	 *            The location to start from.
	 * @param direction
	 *            The {@link Direction} to go to.
	 * @param distance
	 *            The amount of tiles to go.
	 * @return
	 */
	public MapLocation getLocationInDirection(MapLocation location, Direction direction, int distance) {
		// Check if the distance is not larger than the maximum distance available (on the map) in that direction
		if (distance > getMaxTravelDistance(location, direction))
			return null;

		// Find the position in that direction
		int targetPosition = getPositionInDirection(toPosition(location), direction, distance);
		return targetPosition >= 0 ? toLocation(targetPosition) : null;
	}

	/**
	 * Returns the positional index that is a distance away from a position.
	 * 
	 * @param position
	 *            The positional index to start from.
	 * @param direction
	 *            The {@link Direction} to go to.
	 * @param distance
	 *            The amount of tiles to go.
	 * @return
	 */
	public int getPositionInDirection(int position, Direction direction, int distance) {
		if (distance < 0)
			return -1;
		int targetPosition = -1;
		switch (direction) {
		case NORTH:
			// North is decreasing Y, equal X. Or in positions; -(width * distance)
			targetPosition = position - (mapWidth * distance);
			break;
		case EAST:
			// East is equal Y, increasing X. Or in positions; +distance
			targetPosition = position + distance;
			break;
		case SOUTH:
			// South is increasing Y, equal X. Or in positions; +(width * distance)
			targetPosition = position + (mapWidth * distance);
			break;
		case WEST:
			// West is equal Y, decreasing X. Or in positions; -distance
			targetPosition = position - distance;
			break;
		default:
			System.out.println("UNHANDLED DIRECTION TYPE!");
		}
		// Check if the target position doesn't go out of bounds.
		return targetPosition >= 0 && targetPosition < mapWidth * mapHeight ? targetPosition : -1;
	}

	/**
	 * Returns the maximum distance of travel there is available before the edge of the map is encountered.
	 * 
	 * @param location
	 *            The location to start from.
	 * @param direction
	 *            The direction to travel in.
	 */
	public int getMaxTravelDistance(MapLocation origin, Direction direction) {
		switch (direction) {
		case EAST:
			// When travelling east, X is increasing and Y stays equal
			// This means we can only go as far as the width of the map, minus our current X-coordinate
			// (minus 1 because of indexes)
			return mapWidth - 1 - origin.getX();
		case NORTH:
			// When travelling north, Y is decreasing and X stays equal
			// This means we can only go a number of steps equal to our Y-coordinate
			return origin.getY();
		case SOUTH:
			// When travelling south, Y is increasing and X stays equal
			// This means we can only go as far as the height of the map, minus our current Y-coordinate
			// (minus 1 because of indexes)
			return mapHeight - 1 - origin.getY();
		case WEST:
			// When travelling west, X is decreasing and Y stays equal
			// This means we can only go a number of steps equal to our X-coordinate
			return origin.getX();
		default:
			return 0;
		}
	}

	/**
	 * Returns the neighbours in the available {@link Direction}s to a location. Only locations that
	 * are on the map are returned.
	 * 
	 * @param location
	 *            The location to get the neighbours for.
	 * @return A collections of MapLocations.
	 */
	public List<MapLocation> getNeighbours(MapLocation location) {
		// Set up a list
		List<MapLocation> neighbours = new ArrayList<MapLocation>();
		// Go through all directions
		for (Direction direction : Direction.values()) {
			// See if there is an adjacent location
			MapLocation adjacent = getAdjacentLocationInDirection(location, direction);
			if (adjacent != null)
				// If so, add it to the list
				neighbours.add(adjacent);
		}
		// Return the list
		return neighbours;
	}

	/**
	 * Returns a collection of {@link MapLocation}s that are directly around a centre location. This
	 * method is like 'getNeighbours', except that it returns locations in up to 8 directions.
	 * 
	 * @param location
	 *            The centre location.
	 * @param includeCentre
	 *            Whether or not the centre location should also be included in the result collection.
	 * @return
	 */
	public List<MapLocation> getAreaAround(MapLocation location, boolean includeCentre) {
		// Set up a list
		List<MapLocation> area = getNeighbours(location);
		// Get the corner positions (North-East, South-East, South-West, North-West)
		// North-East is increasing X, decreasing Y.
		MapLocation northEast = new MapLocation(location.getX() + 1, location.getY() - 1);
		if (isOnMap(northEast))
			area.add(northEast);
		// South-East is increasing X, increasing Y.
		MapLocation southEast = new MapLocation(location.getX() + 1, location.getY() + 1);
		if (isOnMap(southEast))
			area.add(southEast);
		// South-West is decreasing X, increasing Y.
		MapLocation southWest = new MapLocation(location.getX() - 1, location.getY() + 1);
		if (isOnMap(southWest))
			area.add(southWest);
		// North-West is decreasing X, decreasing Y.
		MapLocation northWest = new MapLocation(location.getX() - 1, location.getY() - 1);
		if (isOnMap(northWest))
			area.add(northWest);
		// If the centre was also requested, add it
		if (includeCentre)
			area.add(location);
		// Return the list of locations
		return area;
	}

	/**
	 * Returns a 3-by-3 array containing the {@link MapFeature}s around a specific location on the map. This array will
	 * contain null values for locations that are not on the map.
	 * 
	 * @param location
	 *            The {@link MapLocation} to get the features around.
	 */
	public MapFeature[] getMapFeaturesAround(MapLocation location) {
		// Create an array of size 9 because it's a 3x3 area.
		MapFeature[] features = new MapFeature[9];
		// Check if the location is on the map
		if (!isOnMap(location))
			return features;

		// Index 0 is -1,-1
		if (isXonMap(location.getX() - 1) && isYonMap(location.getY() - 1)) {
			features[0] = (MapFeature) mapContent[toPosition(location.getX() - 1, location.getY() - 1)][Constants.MAP_INTERNAL_FEATURE_INDEX];
		}
		// Index 1 is 0, -1
		if (isXonMap(location.getX()) && isYonMap(location.getY() - 1)) {
			features[1] = (MapFeature) mapContent[toPosition(location.getX(), location.getY() - 1)][Constants.MAP_INTERNAL_FEATURE_INDEX];
		}
		// Index 2 is +1, -1
		if (isXonMap(location.getX() + 1) && isYonMap(location.getY() - 1)) {
			features[2] = (MapFeature) mapContent[toPosition(location.getX() + 1, location.getY() - 1)][Constants.MAP_INTERNAL_FEATURE_INDEX];
		}
		// Index 3 is -1, 0
		if (isXonMap(location.getX() - 1) && isYonMap(location.getY())) {
			features[3] = (MapFeature) mapContent[toPosition(location.getX() - 1, location.getY())][Constants.MAP_INTERNAL_FEATURE_INDEX];
		}
		// Index 4 is 0, 0
		features[4] = (MapFeature) mapContent[toPosition(location)][Constants.MAP_INTERNAL_FEATURE_INDEX];
		// Index 5 is +1, 0
		if (isXonMap(location.getX() + 1) && isYonMap(location.getY())) {
			features[5] = (MapFeature) mapContent[toPosition(location.getX() + 1, location.getY())][Constants.MAP_INTERNAL_FEATURE_INDEX];
		}
		// Index 6 is -1, +1
		if (isXonMap(location.getX() - 1) && isYonMap(location.getY() + 1)) {
			features[6] = (MapFeature) mapContent[toPosition(location.getX() - 1, location.getY() + 1)][Constants.MAP_INTERNAL_FEATURE_INDEX];
		}
		// Index 7 is 0, +1
		if (isXonMap(location.getX()) && isYonMap(location.getY() + 1)) {
			features[7] = (MapFeature) mapContent[toPosition(location.getX(), location.getY() + 1)][Constants.MAP_INTERNAL_FEATURE_INDEX];
		}
		// Index 8 is +1, +1
		if (isXonMap(location.getX() + 1) && isYonMap(location.getY() + 1)) {
			features[8] = (MapFeature) mapContent[toPosition(location.getX() + 1, location.getY() + 1)][Constants.MAP_INTERNAL_FEATURE_INDEX];
		}

		return features;
	}

	/**
	 * Returns a collection of locations that are in the Unit's field-of-view.
	 * 
	 * @param unit
	 *            The Unit.
	 * @return
	 */
	public HashSet<MapLocation> getFieldOfView(Unit unit) {
		// Reset any previously computed locations
		lineOfSight.resetVisibleLocations();
		// Ask the line-of-sight implementation to compute the field-of-view
		lineOfSight.compute(unit.getLocation(), unit.getFieldOfViewRange(), unit.getOrientation(), unit.getFieldOfViewAngle());
		// Return the list of computed locations
		return lineOfSight.getVisibleLocations();
	}

	/**
	 * Returns the object with the specified ID. If no such object can be found, null is returned.
	 * 
	 * @param objectID
	 *            The unique identifier of the object.
	 * @return The object, or null if no object was found.
	 */
	public GameObject getObject(int objectID) {
		// Make sure this ID doesn't go out of bounds of our objects-collection.
		if (objects.size <= objectID)
			return null;
		return objects.get(objectID);
	}

	/**
	 * Returns the location of the object with the specified ID. If no such object can be found, null
	 * is returned.
	 * 
	 * @param objectID
	 *            The unique identifier of the object.
	 * @return The {@link MapLocation} of the object, or null if no object was found.
	 */
	public MapLocation getObjectLocation(int objectID) {
		// Make sure this ID doesn't go out of bounds of our objects-collection.
		if (objects.size < objectID || objects.get(objectID) == null)
			return null;
		// Return the object's location
		return objects.get(objectID)
						.getLocation();
	}

	/**
	 * Returns the unit at the specified location on the map, or null if no unit is found.
	 * 
	 * @param location
	 *            The location to find a unit at.
	 * @return
	 */
	public Unit getUnitAtLocation(MapLocation location) {
		if (isOnMap(location) && mapContent[toPosition(location)][Constants.MAP_INTERNAL_UNIT_INDEX] != null) {
			return (Unit) mapContent[toPosition(location)][Constants.MAP_INTERNAL_UNIT_INDEX];
		}
		return null;
	}

	/**
	 * Returns the map feature at the specified location on the map, or null if the location does not exist on this map.
	 * 
	 * @param location
	 *            The location to find a map feature at.
	 * @return
	 */
	public MapFeature getFeatureAtLocation(MapLocation location) {
		if (isOnMap(location)) {
			return (MapFeature) mapContent[toPosition(location)][Constants.MAP_INTERNAL_FEATURE_INDEX];
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
		// Bases are MapFeatures
		int count = 0;
		for (GameObject object : objects) {
			if (object != null && object instanceof Base)
				count++;
		}
		return count;
	}

	/**
	 * Returns the distance between two locations. This method uses the distance measure currently being used by the
	 * Line-of-Sight implementation (see {@link Map#lineOfSight}). For Euclidean distances, it rounds up. Note that
	 * {@link Integer#MAX_VALUE} is returned if the current distance measure is not recognised.
	 * 
	 * @param loc1
	 *            The first location.
	 * @param loc2
	 *            The second location.
	 */
	public int getDistance(MapLocation loc1, MapLocation loc2) {
		// Check what distance function we are using
		GetDistanceFunction function = lineOfSight.getDistanceType();
		if (function instanceof GetManhattanDistance) {
			return MapLocation.getManhattanDist(loc1, loc2);
		} else if (function instanceof GetEuclidianDistance) {
			return (int) Math.ceil(MapLocation.getEuclideanDist(loc1, loc2));
		}
		return Integer.MAX_VALUE;
	}

	/**
	 * Alert the map that an object wants to join the map's collection of objects.
	 * 
	 * @param object
	 *            The object that needs to be registered.
	 */
	public void registerGameObject(GameObject object) {
		int objectID = -1;
		// Check the ID-buffer has any available IDs
		if (idBuffer.size > 0) {
			objectID = idBuffer.pop();
		} else {
			// Return a new ID
			objectID = objects.size;
			// Make sure the collection can hold this new ID
			objects.setSize(objectID + 1);
		}

		// Assign the object it's ID
		object.setID(objectID);

		// Set the object into our object collection
		objects.set(objectID, object);
	}

	/**
	 * Alert the map that this object should be removed and that it's ID can be added to the buffer array.
	 * 
	 * Note: this method calls {@link Map#remove(MapLocation, GameObject)}.
	 * 
	 * @param object
	 *            The object that can be unregistered.
	 */
	public void unregisterGameObject(GameObject object) {
		// Set the space in our object collection to null
		objects.set(object.getID(), null);
		// Add this ID to the buffer
		idBuffer.add(object.getID());
		// Remove the object from the map content
		remove(object.getLocation(), object);
	}

	/**
	 * Updates the Field-of-View for all Units on the map.
	 */
	public void updateFieldOfView() {
		// Check all units
		for (GameObject object : objects) {
			if (object != null && object instanceof Unit) {
				Unit unit = (Unit) object;
				// Get the field-of-view collection for the unit
				HashSet<MapLocation> fieldOfView = getFieldOfView(unit);
				// Tell the unit to update it's field-of-view
				unit.updateFieldOfView(fieldOfView);
			}
		}
	}

	/**
	 * Places an object at a location on the map.
	 * 
	 * {@link Map#place(int, GameObject)}
	 */
	public boolean place(MapLocation location, GameObject object) {
		return place(toPosition(location), object);
	}

	/**
	 * Removes an object from a location on the map.
	 * 
	 * {@link Map#remove(int, GameObject)}
	 */
	public boolean remove(MapLocation location, GameObject object) {
		return remove(toPosition(location), object);
	}

	/**
	 * Places a {@link GameObject} on the map.
	 * 
	 * @param position
	 *            The position on the map to place the object at.
	 * @param object
	 *            The object to place.
	 * @return Whether or not the placement was successful.
	 */
	public boolean place(int position, GameObject object) {
		// Check which layer the object needs to be at
		int layer = -1;
		if (object instanceof MapFeature)
			layer = Constants.MAP_INTERNAL_FEATURE_INDEX;
		else if (object instanceof Unit)
			layer = Constants.MAP_INTERNAL_UNIT_INDEX;
		else {
			System.out.println("WARNING: Unable to place object on map, unknown type");
			return false;
		}
		// Check if there isn't already an object at the specified position
		if (mapContent[position][layer] != null) {
			System.out.println("WARNING: Unable to place object on map, space occupied");
			return false;
		}
		// Place the object
		object.setLocation(toLocation(position));
		mapContent[position][layer] = object;
		return true;
	}

	/**
	 * Removes a {@link GameObject} from the map.
	 * 
	 * @param position
	 *            The position on the map to remove the object from.
	 * @param object
	 *            The object to remove.
	 * @return Whether or not the removal was successful.
	 */
	public boolean remove(int position, GameObject object) {
		// Check which layer the object should be at
		int layer = -1;
		if (object instanceof MapFeature)
			layer = Constants.MAP_INTERNAL_FEATURE_INDEX;
		else if (object instanceof Unit)
			layer = Constants.MAP_INTERNAL_UNIT_INDEX;
		else {
			System.out.println("WARNING: Unable to remove object from map, unknown type");
			return false;
		}
		// Check if there is an object to remove
		if (mapContent[position][layer] == null) {
			System.out.println("WARNING: Unable to remove object from map, space is empty");
			return false;
		}
		// Check if the object to remove has the same ID as the object currently at that position
		if (mapContent[position][layer].getID() != object.getID()) {
			System.out.println("WARNING: Unable to remove object from map, no matching object found");
			return false;
		}
		// Remove the object
		object.setLocation(Constants.GAMEOBJECT_NOT_PLACED);
		mapContent[position][layer] = null;
		return true;
	}

	/**
	 * Creates a deep copy of this map.
	 */
	public Map copy() {
		// Create a new map
		Map newMap = new Map(this.name, this.mapWidth, this.mapHeight);

		// Deep copy the map content & objects
		Array<GameObject> newObjects = new Array<GameObject>(this.objects.size);
		newObjects.size = this.objects.size; // Force size so OoB checks don't crash when we directly set the content
		GameObject[][] newContent = copyMapContent(newObjects);

		// Set some things
		newMap.setMapContent(newContent);
		newMap.setObjects(newObjects);
		newMap.setIdBuffer(new IntArray(idBuffer));
		// Return the created map
		return newMap;
	}

	/**
	 * Returns an ordered {@link Array} containing the locations that form a path from one location to another. This
	 * method uses the A* algorithm.
	 * 
	 * @param from
	 *            The location that is the starting point for the search.
	 * @param to
	 *            The location that is the target of the search.
	 */
	public Array<MapLocation> findPath(MapLocation from, MapLocation to) {
		BinaryHeap<PathNode> open = new BinaryHeap<PathNode>(mapWidth * mapHeight, false);
		PathNode[] nodes = new PathNode[mapWidth * mapHeight];
		IntArray path = new IntArray();
		// Using positions is more convenient with indexing in the 'nodes' array
		int rootPosition = toPosition(from);
		int targetPosition = toPosition(to);

		// Start the search at the root node
		PathNode root = new PathNode(0);
		root.position = rootPosition;
		nodes[rootPosition] = root;
		root.parent = null;
		root.pathCost = 0;

		open.add(root, 0);

		while (open.size > 0) {
			PathNode node = open.pop();
			if (node.position == targetPosition) {
				// If we have reached the target, go back and add all parent nodes to the path.
				while (node != root) {
					path.add(node.position);
					node = node.parent;
				}
				break;
			}
			node.closed = true;

			MapLocation nodeLocation = toLocation(node.position);
			for (Direction move : Direction.values()) {
				// Check if this move is possible. Note: this causes searches that have a location that cannot be
				// traversed to not yield a solution.
				if (!isMovePossible(nodeLocation, move))
					continue;

				MapLocation moveLocation = getLocationInDirection(nodeLocation, move, 1);
				addNode(nodes, open, node, moveLocation, 1, to);
			}

		}

		path.reverse();

		// Translate the positions back into MapLocation before returning the path
		Array<MapLocation> pathLocations = new Array<MapLocation>(true, path.size);
		for (int i = 0; i < path.size; i++) {
			pathLocations.add(toLocation(path.get(i)));
		}

		return pathLocations;
	}

	// endregion

	// region Overridden methods

	/**
	 * Returns a string representation of the current map state. In this representation the left side
	 * is the MapFeature layer while the right side is the Unit layer.
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (int y = 0; y < mapHeight; y++) {
			// This will be one line of the printed map
			StringBuilder lineBuilder = new StringBuilder();
			// Separate the two layers with ' | '
			lineBuilder.append(Constants.MAP_TOSTRING_LAYER_SEPARATOR);
			for (int x = 0; x < mapWidth; x++) {
				int currentPosition = toPosition(x, y);
				GameObject[] objects = mapContent[currentPosition];
				String featureLevel = objects[Constants.MAP_INTERNAL_FEATURE_INDEX] != null	? objects[Constants.MAP_INTERNAL_FEATURE_INDEX].toString()
																							: " ";
				String unitLevel = objects[Constants.MAP_INTERNAL_UNIT_INDEX] != null	? objects[Constants.MAP_INTERNAL_UNIT_INDEX].toString()
																						: ".";
				// Add feature level first, unit level on other side
				lineBuilder.insert(x, featureLevel);
				lineBuilder.append(unitLevel);
			}
			// Done with the line, append it
			builder.append(lineBuilder.toString() + "\n");
		}
		return builder.toString();
	}

	@Override
	public int hashCode() {
		// We've chosen a prime number close to 50 to make the hash code slightly more like a number that does not occur
		// naturally
		int output = 43;
		// Move through all positions on the map
		for (int i = 0; i < mapContent.length; i++) {
			GameObject[] cell = mapContent[i];
			for (int j = 0; j < cell.length; j++) {
				if (cell[j] == null) {
					// Ignore positions that do not contain anything
					continue;
				}
				// Update the output; XOR the ID of the object with the coordinates (i = position, j = level)
				output ^= cell[j].getID() ^ i ^ j;
			}
		}
		// Return the created code
		return output;
	}

	// endregion

	// region Protected methods

	/**
	 * Ticks the map forward, this means that we should check the {@link GameObject}s on the map to
	 * see if any should be removed. This method also updates the field-of-view of any surviving {@link Unit}s.
	 * 
	 * @param state
	 *            The current state of the game.
	 */
	protected void tick(HunterKillerState state) {
		// Check our object collection for 'dead' objects
		for (int i = 0; i < objects.size; i++) {
			GameObject object = objects.get(i);
			// Check if there is anything there
			if (object != null) {
				// If the '.tick' method returns true, that indicates that the object should be removed
				if (object.tick(state)) {
					// Get this object's position on the map
					int mapPosition = toPosition(object.getLocation());

					// Unregister the object
					unregisterGameObject(object);

					// If the object is a Base, replace it with a Space-tile
					if (object instanceof Base) {
						// Create a new Space object
						Space space = new Space(toLocation(mapPosition));
						// Register the object
						registerGameObject(space);
						// Place it on the map
						place(mapPosition, space);

						Base base = (Base) object;
						Player player = state.getPlayer(base.getControllingPlayerID());

						// Remove all of the Player's Units
						IntArray unitIDs = player.getUnitIDs();
						for (int k = 0; k < unitIDs.size; k++) {
							int id = unitIDs.get(k);
							Unit unit = (Unit) getObject(id);
							// Check if the Unit is still around (might have died this same tick)
							if (unit != null) {
								// Unregister the Unit
								unregisterGameObject(unit);
							}
						}

						// Tell the Player that was controlling the Base that it's gone
						player.informBaseDestroyed(base.getID());
					}
					// If the object is a Unit, tell it's Player to remove it
					if (object instanceof Unit) {
						Unit unit = (Unit) object;
						state.getPlayer(unit.getControllingPlayerID())
								.removeUnit(unit.getID());
					}
				}
			}
		}

		// Update the Field-of-View for all remaining Units
		updateFieldOfView();
	}

	/**
	 * Reduces the timers on open doors and unit's cooldowns. This method should be called at the
	 * start of a new round.
	 */
	protected void timer() {
		// Check all our objects
		for (GameObject object : objects) {
			// Check if there is anything there
			if (object != null) {
				// Check if it's a Door
				if (object instanceof Door) {
					// Reduce the timer if the door is open
					Door door = (Door) object;
					if (door.isOpen())
						door.reduceTimer(this);
				}
				// Check if it's a Unit
				else if (object instanceof Unit) {
					// Reduce the unit's cooldown
					((Unit) object).reduceCooldown();
				}
			}
		}
	}

	/**
	 * Creates a deep copy of this map's content.
	 * 
	 * @return
	 */
	protected GameObject[][] copyMapContent(Array<GameObject> objects) {
		int positions = this.mapWidth * this.mapHeight;
		// Create a new content array
		GameObject[][] newContent = new GameObject[positions][Constants.MAP_INTERNAL_LAYERS];
		for (int i = 0; i < positions; i++) {
			for (int j = 0; j < Constants.MAP_INTERNAL_LAYERS; j++) {
				GameObject object = this.mapContent[i][j];
				// Check if there is anything on this position
				if (object != null) {
					GameObject copy = object.copy();
					objects.set(copy.getID(), copy);
					newContent[i][j] = copy;
				}
				// Otherwise just leave this null
			}
		}
		// Return the created content
		return newContent;
	}

	/**
	 * Assigns the Base and all Units a player controls to that player. Note: this method is used
	 * right after the map and players have been created and can safely be ignored.
	 * 
	 * @param player
	 *            The {@link Player} to assign objects to.
	 */
	protected void assignObjectsToPlayer(Player player) {
		// Check for Bases and Units
		for (GameObject object : objects) {
			// Check if there is anything there
			if (object != null) {
				// Check if it's a base and belongs to this player
				if (object instanceof Base && ((Base) object).getControllingPlayerID() == player.getID()) {
					player.assignBase(object.getID());
				}
				// Check if it's a unit and belongs to this player
				else if (object instanceof Unit && ((Unit) object).getControllingPlayerID() == player.getID()) {
					player.addUnit(object.getID());
				}
			}
		}
	}

	/**
	 * Attack a location on the map.
	 * 
	 * @param location
	 *            The {@link MapLocation} to attack.
	 * @param damage
	 *            The damage to inflict.
	 * @return Whether or not the attack was successful.
	 */
	protected boolean attackLocation(MapLocation location, int damage) {
		// Check if the location is on the map
		if (!(isXonMap(location.getX()) && isYonMap(location.getY()))) {
			System.out.println("WARNING: Unable to attack, location not on map.");
			return false;
		}
		int position = toPosition(location);
		// Get the map feature on this position
		MapFeature feature = (MapFeature) mapContent[position][Constants.MAP_INTERNAL_FEATURE_INDEX];
		// If the feature is destructible, reduce it's HP by the damage
		if (feature.isDestructible())
			feature.reduceHP(damage);
		// Check if there is a Unit on this position
		if (mapContent[position][Constants.MAP_INTERNAL_UNIT_INDEX] != null)
			mapContent[position][Constants.MAP_INTERNAL_UNIT_INDEX].reduceHP(damage);
		return true;
	}

	// endregion

	// region Private methods

	/**
	 * Adds a node to the set of searched nodes and checks if this node is part of a path that costs less than the
	 * current lowest cost.
	 * 
	 * @param constructed
	 *            The collection of nodes that have been visited so far.
	 * @param open
	 *            The collection of nodes that still have potential to improve the path.
	 * @param parent
	 *            The node that applying a move to resulted in the current location.
	 * @param location
	 *            The location that a node should be constructed for.
	 * @param cost
	 *            The cost of the move.
	 * @param target
	 *            The target location of the search.
	 */
	private void addNode(PathNode[] constructed, BinaryHeap<PathNode> open, PathNode parent, MapLocation location, int cost,
			MapLocation target) {
		// Get the position of the current location
		int locationPosition = toPosition(location);

		// Walls block expansion of the search.
		MapFeature feature = getFeatureAtLocation(location);
		if (feature instanceof Wall && !((Wall) feature).isDestructible())
			return;

		int pathCost = parent.pathCost + cost;
		float score = pathCost + MapLocation.getManhattanDist(target, location);

		PathNode node = constructed[locationPosition];
		// Check if this location has already had a node created.
		if (node != null) {
			// Check if that node isn't closed and new the cost is lower.
			if (!node.closed && pathCost < node.pathCost) {
				// Update the existing node.
				open.setValue(node, score);
				node.parent = parent;
				node.pathCost = pathCost;
			}
		} else {
			// Create a new node
			node = new PathNode(0);
			node.position = locationPosition;
			node.parent = parent;
			node.pathCost = pathCost;

			constructed[locationPosition] = node;

			open.add(node, score);
		}
	}

	// endregion

	// region Inner classes

	private class PathNode
			extends Node {
		int position, pathCost;
		boolean closed;
		PathNode parent;

		public PathNode(float value) {
			super(value);
		}
	}

	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	public class BlocksLight
			implements BlocksLightFunction {

		@Override
		public boolean func(int x, int y) {
			// Check if both coordinates are within the map's bounds
			if (!isXonMap(x) || !isYonMap(y))
				return true;
			// Check the feature at the specified position
			return ((MapFeature) getMapContent()[toPosition(x, y)][Constants.MAP_INTERNAL_FEATURE_INDEX]).isBlockingLOS();
		}

	}

	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	public class GetManhattanDistance
			implements GetDistanceFunction {

		/**
		 * Note that this function returns the distance to (0,0), because
		 * {@link LineOfSight#LineOfSight(BlocksLightFunction, SetVisibleFunction, GetDistanceFunction)} requires that.
		 */
		@Override
		public int func(int x, int y) {
			return MapLocation.getManhattanDist(0, 0, x, y);
		}

	}

	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	public class GetEuclidianDistance
			implements GetDistanceFunction {

		/**
		 * Note that this function returns the distance to (0,0), because
		 * {@link LineOfSight#LineOfSight(BlocksLightFunction, SetVisibleFunction, GetDistanceFunction)} requires that.
		 */
		@Override
		public int func(int x, int y) {
			return (int) MapLocation.getEuclideanDist(0, 0, x, y);
		}

	}

	public class SetVisible
			implements SetVisibleFunction {

		private HashSet<MapLocation> visibleLocations;

		public SetVisible() {
			this.visibleLocations = new HashSet<MapLocation>();
		}

		@Override
		public void func(int x, int y) {
			// Ignore any coordinates that are not on the map
			if (isXonMap(x) && isYonMap(y))
				this.visibleLocations.add(new MapLocation(x, y));
		}

		public void resetLocations() {
			this.visibleLocations = new HashSet<MapLocation>();
		}

		public HashSet<MapLocation> getVisibleLocations() {
			return visibleLocations;
		}
	}

	// endregion
}
