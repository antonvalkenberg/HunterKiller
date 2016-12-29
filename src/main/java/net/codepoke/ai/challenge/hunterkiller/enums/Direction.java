package net.codepoke.ai.challenge.hunterkiller.enums;

/**
 * Enumeration of the directions used in the game.
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
public enum Direction {
  /**
   * On this game's map structure, north is: decreasing Y, equal X.
   */
  NORTH,
  /**
   * On this game's map structure, east is: equal Y, increasing X.
   */
  EAST,
  /**
   * On this game's map structure, south is: increasing Y, equal X.
   */
  SOUTH,
  /**
   * On this game's map structure, west is: equal Y, decreasing X.
   */
  WEST;
  
  public Direction getOppositeDirection() {
    switch(this) {
      case NORTH:
        return SOUTH;
      case EAST:
        return WEST;
      case SOUTH:
        return NORTH;
      case WEST:
        return EAST;
      default:
        return null;
    }
  }
  
  public Direction rotateEast() {
    switch(this) {
      case NORTH:
        return EAST;
      case EAST:
        return EAST;
      case SOUTH:
        return EAST;
      case WEST:
        return NORTH;
      default:
        return null;
    }
  }
  
  public Direction rotateWest() {
    switch(this) {
      case NORTH:
        return WEST;
      case EAST:
        return NORTH;
      case SOUTH:
        return WEST;
      case WEST:
        return WEST;
      default:
        return null;
    }
  }
}
