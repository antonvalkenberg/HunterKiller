package net.codepoke.ai.challenge.hunterkiller.enums;

import lombok.AllArgsConstructor;

/**
 * Enumeration of the directions used in HunterKiller. Currently only the cardinal directions are
 * supported.
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
@AllArgsConstructor
public enum Direction {
  /**
   * On this game's map structure, north is: decreasing Y, equal X.
   */
  NORTH(270),
  /**
   * On this game's map structure, east is: equal Y, increasing X.
   */
  EAST(0),
  /**
   * On this game's map structure, south is: increasing Y, equal X.
   */
  SOUTH(90),
  /**
   * On this game's map structure, west is: equal Y, decreasing X.
   */
  WEST(180);
  
  /**
   * The angle of this direction, which assumes that X-positive, Y==0 will be 0, and increases
   * counter-clockwise. This is primarily used in
   * {@link net.codepoke.ai.challenge.hunterkiller.LineOfSight LineOfSight} calculations and can be
   * safely ignored.
   */
  public float angle;
  
  /**
   * Returns the direction that is directly opposite of this.
   * 
   * @return {@link Direction}
   */
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
  
  /**
   * Returns the direction a {@link net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Unit
   * Unit} will face when rotating with respect to their current orientation.
   * 
   * @param facing
   *          The unit's current orientation.
   * @param rotation
   *          The desired rotation.
   * 
   * @return {@link Direction}
   */
  public static Direction rotate(Direction facing, Rotation rotation) {
    switch(facing) {
      case NORTH:
        //When facing north, rotating east will face you east, rotating west will face you west
        switch(rotation) {
          case EAST:
            return EAST;
          case WEST:
            return WEST;
          default:
            return null;
        }
      case EAST:
        //When facing east, rotating east will face you south, rotating west will face you north
        switch(rotation) {
          case EAST:
            return SOUTH;
          case WEST:
            return NORTH;
          default:
            return null;
        }
      case SOUTH:
        //When facing south, rotating east will face you west, rotating west will face you east
        switch(rotation) {
          case EAST:
            return WEST;
          case WEST:
            return EAST;
          default:
            return null;
        }
      case WEST:
        //When facing west, rotating east will face you north, rotating west will face you south
        switch(rotation) {
          case EAST:
            return NORTH;
          case WEST:
            return SOUTH;
          default:
            return null;
        }
      default:
        return null;
    }
  }
  
  /**
   * Enumeration of the rotations used in HunterKiller. Currently only east and west are supported.
   * 
   * @author Anton Valkenberg (anton.valkenberg@gmail.com)
   *
   */
  public enum Rotation {
    EAST, WEST;
  }
  
}
