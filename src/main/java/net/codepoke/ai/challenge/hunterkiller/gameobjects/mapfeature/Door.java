package main.java.net.codepoke.ai.challenge.hunterkiller.gameobjects.mapfeature;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import main.java.net.codepoke.ai.challenge.hunterkiller.MapLocation;
import main.java.net.codepoke.ai.challenge.hunterkiller.enums.TileType;

/**
 * Class representing a door in the game.
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class Door extends MapFeature {
  
  //region Constants
  
  /**
   * Doors are indestructible.
   */
  public static final boolean DOOR_DESTRUCTIBLE = false;
  /**
   * Door are created closed and block Line of Sight in that state.
   */
  public static final boolean DOOR_BLOCKS_LOS = true;
  /**
   * Doors can be moved over. (At which point they will open and remain open for a number of ticks)
   */
  public static final boolean DOOR_WALKABLE = true;
  /**
   * Once opened a Door will remain open for this amount of ticks.
   */
  public static final int DOOR_OPEN_TICKS = 10;
  
  //endregion
  
  //region Properties
  
  /**
   * Timer that indicates how many ticks the Door will remain open for.
   */
  private int openTimer = 0;
  
  //endregion
  
  //region Constructor
  
  /**
   * Constructs a new instance of a Door.
   * 
   * @param id
   *          The Door's unique identifier.
   * @param mapLocation
   *          The Door's location on the Map.
   */
  public Door(int id, MapLocation mapLocation) {
    this(id, mapLocation, 0);
  }
  
  /**
   * Constructs a new instance of a Door with a specified time before it closes.
   * 
   * @param id
   *          The Door's unique identifier.
   * @param mapLocation
   *          The Door's location of the Map.
   * @param timeToClose
   *          Amount of ticks before the Door closes.
   */
  public Door(int id, MapLocation mapLocation, int timeToClose) {
    super(id, mapLocation, DOOR_DESTRUCTIBLE, timeToClose <= 0, DOOR_WALKABLE);
    openTimer = timeToClose;
  }
  
  //endregion
  
  /**
   * Whether or not this Door is blocking Line of Sight.
   */
  @Override
  public boolean isBlockingLOS() {
    return !isOpen();
  }
  
  /**
   * Whether or not this Door is open.
   * 
   * @return ^
   */
  public boolean isOpen() {
    return openTimer > 0;
  }
  
  /**
   * Open this Door. It will close after a predetermined amount of ticks.
   */
  public void open() {
    openTimer = DOOR_OPEN_TICKS;
    isBlockingLOS = false;
  }
  
  /**
   * Close this Door.
   */
  public void close() {
    openTimer = 0;
    isBlockingLOS = true;
  }
  
  @Override
  public Door copy(int id) {
    return new Door(id, this.getLocation(), openTimer);
  }
  
  public String toString() {
    return isOpen() ? TileType.DOOR_OPEN.txt : TileType.DOOR_CLOSED.txt;
  }
  
}
