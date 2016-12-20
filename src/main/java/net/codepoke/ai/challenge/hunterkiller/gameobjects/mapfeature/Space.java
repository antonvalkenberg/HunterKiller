package main.java.net.codepoke.ai.challenge.hunterkiller.gameobjects.mapfeature;

import main.java.net.codepoke.ai.challenge.hunterkiller.MapLocation;
import main.java.net.codepoke.ai.challenge.hunterkiller.enums.TileType;

/**
 * Class representing a space tile in the game.
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
public class Space extends MapFeature {
  
  //region Constants
  
  /**
   * Space is indestructible.
   */
  public static final boolean SPACE_DESTRUCTIBLE = false;
  /**
   * Space does not block Line of Sight.
   */
  public static final boolean SPACE_BLOCKS_LOS = false;
  /**
   * Space can not be moved over.
   */
  public static final boolean SPACE_WALKABLE = false;
  
  //endregion
  
  //region Constructor
  
  /**
   * Constructs a new instance of Space.
   * 
   * @param id
   *          The Space's unique identifier.
   * @param mapLocation
   *          The Space's location on the Map.
   */
  public Space(int id, MapLocation mapLocation) {
    super(id, mapLocation, SPACE_DESTRUCTIBLE, SPACE_BLOCKS_LOS, SPACE_WALKABLE);
  }
  
  //endregion
  
  @Override
  public Space copy(int id) {
    return new Space(id, this.getLocation());
  }
  
  public String toString() {
    return TileType.SPACE.txt;
  }
}
