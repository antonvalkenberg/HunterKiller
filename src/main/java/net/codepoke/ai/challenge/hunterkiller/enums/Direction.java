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
}
