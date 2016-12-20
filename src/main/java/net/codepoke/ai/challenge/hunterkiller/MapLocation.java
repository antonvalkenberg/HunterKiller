package main.java.net.codepoke.ai.challenge.hunterkiller;

import lombok.EqualsAndHashCode;
import lombok.Getter;

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
  public static final double getEuclideanDist(int x1, int y1, int x2, int y2) {
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
  public static final double getEuclideanDist(MapLocation location1, MapLocation location2) {
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
  public static final int getManhattanDist(int x1, int y1, int x2, int y2) {
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
  public static final int getManhattanDist(MapLocation location1, MapLocation location2) {
    return getManhattanDist(location1.x, location1.y, location2.x, location2.y);
  }
  
  //endregion
}
