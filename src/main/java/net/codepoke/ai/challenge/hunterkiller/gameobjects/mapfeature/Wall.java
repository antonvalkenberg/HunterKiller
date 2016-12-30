package net.codepoke.ai.challenge.hunterkiller.gameobjects.mapfeature;

import net.codepoke.ai.challenge.hunterkiller.MapLocation;
import net.codepoke.ai.challenge.hunterkiller.enums.TileType;

/**
 * Class representing a wall in the game.
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
public class Wall extends MapFeature {
  
  //region Constants
  
  /**
   * Walls are indestructible.
   */
  public static final boolean WALL_DESTRUCTIBLE = false;
  /**
   * Walls block Line of Sight.
   */
  public static final boolean WALL_BLOCKS_LOS = true;
  /**
   * Walls can not be moved over.
   */
  public static final boolean WALL_WALKABLE = false;
  
  //endregion
  
  //region Constructor
  
  /**
   * Constructs a new instance of a Wall.
   * 
   * @param id
   *          the Wall's unique identifier.
   * @param mapLocation
   *          The Wall's location on the Map.
   */
  public Wall(int id, MapLocation mapLocation) {
    super(id, mapLocation, WALL_DESTRUCTIBLE, WALL_BLOCKS_LOS, WALL_WALKABLE);
  }
  
  //endregion
  
  //region Overridden methods
  
  @Override
  public Wall copy(int id) {
    return new Wall(id, this.getLocation());
  }
  
  @Override
  public String toString() {
    return TileType.WALL.txt;
  }
  
  //endregion
  
}
