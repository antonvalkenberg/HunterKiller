package main.java.net.codepoke.ai.challenge.hunterkiller;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import main.java.net.codepoke.ai.challenge.hunterkiller.enums.Direction;

/**
 * Represents a location on the {@link Map}. Contains utility methods for calculating distances
 * between locations.
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
@Getter
@EqualsAndHashCode
public class MapLocation {
  
  //region Properties
  
  /**
   * The location's X coordinate.
   */
  private int x;
  
  /**
   * The location's Y coordinate.
   */
  private int y;
  
  //endregion
  
  //region Constructor
  
  /**
   * Constructs a new MapLocation for the provided coordinates.
   * 
   * @param x
   *          The location's X coordinate.
   * @param y
   *          The location's Y coordinate.
   */
  public MapLocation(int x, int y) {
    this.x = x;
    this.y = y;
  }
  
  //endregion
  
  //region Public methods
  
  /**
   * Returns the Euclidean distance between two locations.
   * 
   * @param x1
   *          The X-coordinate of location 1.
   * @param y1
   *          The Y-coordinate of location 1.
   * @param x2
   *          The X-coordinate of location 2.
   * @param y2
   *          The Y-coordinate of location 2.
   * @return
   */
  public static double getEuclideanDist(int x1, int y1, int x2, int y2) {
    return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
  }
  
  /**
   * Returns the Euclidean distance between two locations.
   * 
   * @param location1
   *          The first location.
   * @param location2
   *          The second location.
   * @return
   */
  public static double getEuclideanDist(MapLocation location1, MapLocation location2) {
    return getEuclideanDist(location1.x, location1.y, location2.x, location2.y);
  }
  
  /**
   * Returns the Manhattan distance between two locations.
   * 
   * @param x1
   *          The X-coordinate of location 1.
   * @param y1
   *          The Y-coordinate of location 1.
   * @param x2
   *          The X-coordinate of location 2.
   * @param y2
   *          The Y-coordinate of location 2.
   * @return
   */
  public static int getManhattanDist(int x1, int y1, int x2, int y2) {
    return Math.abs(x1 - x2) + Math.abs(y1 - y2);
  }
  
  /**
   * Returns the Manhattan distance between two locations.
   * 
   * @param location1
   *          The first location.
   * @param location2
   *          The second location.
   * @return
   */
  public static int getManhattanDist(MapLocation location1, MapLocation location2) {
    return getManhattanDist(location1.x, location1.y, location2.x, location2.y);
  }
  
  /**
   * Returns the {@link Direction} that the target location is in, relative to the origin. Note:
   * This method currently only checks in cardinal directions.
   * 
   * @param origin
   *          The location that is the viewpoint.
   * @param target
   *          The location to get the direction to.
   * @return
   */
  public static Direction getDirectionTo(MapLocation origin, MapLocation target) {
    //Check that the points are not the same
    if(origin.equals(target))
      return null;
    int dX = origin.getX() - target.getX();
    int dY = origin.getY() - target.getY();
    //Check cardinal directions
    if(dY < 0 && dX == 0)
      return Direction.NORTH;
    else if(dY == 0 && dX > 0)
      return Direction.EAST;
    else if(dY > 0 && dX == 0)
      return Direction.SOUTH;
    else if(dY == 0 && dX < 0)
      return Direction.WEST;
    else {
      //Non-cardinal direction
      return null;
    }
  }
  
  //endregion
}
