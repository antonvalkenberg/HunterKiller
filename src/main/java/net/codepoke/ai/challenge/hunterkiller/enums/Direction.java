package main.java.net.codepoke.ai.challenge.hunterkiller.enums;

/**
 * Enumeration of the directions used in the game.
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
public enum Direction {
  NORTH, EAST, SOUTH, WEST;
  
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
