package net.codepoke.ai.challenge.hunterkiller.enums;

import lombok.AllArgsConstructor;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Unit;

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
	 * clockwise. This is primarily used in {@link net.codepoke.ai.challenge.hunterkiller.LineOfSight
	 * LineOfSight} calculations and can be safely ignored.
	 */
	public float angle;

	/**
	 * Returns the direction that is directly opposite of this.
	 * 
	 * @return {@link Direction}
	 */
	public Direction getOppositeDirection() {
		switch (this) {
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
	 *            The unit's current orientation.
	 * @param rotation
	 *            The desired rotation.
	 * 
	 * @return {@link Direction}
	 */
	public static Direction rotate(Direction facing, Rotation rotation) {
		switch (facing) {
		case NORTH:
			// When facing north, rotating clockwise will face you east, rotating counter-clockwise will face you west
			switch (rotation) {
			case CLOCKWISE:
				return EAST;
			case COUNTER_CLOCKWISE:
				return WEST;
			default:
				return null;
			}
		case EAST:
			// When facing east, rotating clockwise will face you south, rotating counter-clockwise will face you north
			switch (rotation) {
			case CLOCKWISE:
				return SOUTH;
			case COUNTER_CLOCKWISE:
				return NORTH;
			default:
				return null;
			}
		case SOUTH:
			// When facing south, rotating clockwise will face you west, rotating counter-clockwise will face you east
			switch (rotation) {
			case CLOCKWISE:
				return WEST;
			case COUNTER_CLOCKWISE:
				return EAST;
			default:
				return null;
			}
		case WEST:
			// When facing west, rotating clockwise will face you north, rotating counter-clockwise will face you south
			switch (rotation) {
			case CLOCKWISE:
				return NORTH;
			case COUNTER_CLOCKWISE:
				return SOUTH;
			default:
				return null;
			}
		default:
			return null;
		}
	}

	/**
	 * Returns the {@link Rotation} required for a Unit to face a specified Direction. Note: this method will return
	 * null when the target Direction is the same as the Unit's current orientation. When the Unit is facing the
	 * opposite direction, it will return {@link Rotation#CLOCKWISE}.
	 * 
	 * @param unit
	 *            The Unit to get the correct rotation for.
	 * @param target
	 *            The target Direction.
	 */
	public static Rotation rotationRequiredToFace(Unit unit, Direction target) {
		Direction facing = unit.getOrientation();
		if (target == facing)
			return null;
		if (target == facing.getOppositeDirection())
			return Rotation.CLOCKWISE;
		switch (facing) {
		case EAST:
			return target == NORTH ? Rotation.COUNTER_CLOCKWISE : Rotation.CLOCKWISE;
		case NORTH:
			return target == WEST ? Rotation.COUNTER_CLOCKWISE : Rotation.CLOCKWISE;
		case SOUTH:
			return target == EAST ? Rotation.COUNTER_CLOCKWISE : Rotation.CLOCKWISE;
		case WEST:
			return target == SOUTH ? Rotation.COUNTER_CLOCKWISE : Rotation.CLOCKWISE;
		default:
			return Rotation.CLOCKWISE;
		}
	}

	/**
	 * Returns the direction given a one letter string (N,E,S or W); returns SOUTH by default (as this is the most
	 * common valid usage (base spawning)).
	 */
	public static Direction parse(String direction) {
		if (direction.equalsIgnoreCase("N"))
			return Direction.NORTH;
		else if (direction.equalsIgnoreCase("E"))
			return Direction.EAST;
		else if (direction.equalsIgnoreCase("S"))
			return Direction.SOUTH;
		else if (direction.equalsIgnoreCase("W"))
			return Direction.WEST;
		else
			return Direction.SOUTH;
	}

	/**
	 * Enumeration of the rotations used in HunterKiller.
	 * 
	 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
	 *
	 */
	public enum Rotation {
		CLOCKWISE,
		COUNTER_CLOCKWISE;
	}

}
