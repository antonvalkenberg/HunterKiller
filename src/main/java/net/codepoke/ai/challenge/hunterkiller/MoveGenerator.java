package net.codepoke.ai.challenge.hunterkiller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import net.codepoke.ai.challenge.hunterkiller.enums.Direction;
import net.codepoke.ai.challenge.hunterkiller.enums.StructureOrderType;
import net.codepoke.ai.challenge.hunterkiller.enums.UnitOrderType;
import net.codepoke.ai.challenge.hunterkiller.enums.UnitType;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.Controlled;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.mapfeature.MapFeature;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.mapfeature.Structure;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.mapfeature.Wall;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Infected;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Medic;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Soldier;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Unit;
import net.codepoke.ai.challenge.hunterkiller.orders.HunterKillerOrder;
import net.codepoke.ai.challenge.hunterkiller.orders.StructureOrder;
import net.codepoke.ai.challenge.hunterkiller.orders.UnitOrder;

import com.badlogic.gdx.utils.Array;

/**
 * This class generates legal {@link HunterKillerOrder}s.
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
public class MoveGenerator {

	/**
	 * Simple Random to avoid alloc'ing
	 */
	public static Random RNG = new Random();

	/**
	 * Arrays used to ad-hoc shuffle to create RNG.
	 */
	private static ThreadLocal<Array<UnitType>> UNIT_TYPES = new ThreadLocal<Array<UnitType>>() {
		@Override
		protected Array<UnitType> initialValue() {
			return Array.with(UnitType.values);
		}
	};
	private static ThreadLocal<Array<Direction>> DIRECTIONS = new ThreadLocal<Array<Direction>>() {
		@Override
		protected Array<Direction> initialValue() {
			return Array.with(Direction.values);
		}
	};
	private static ThreadLocal<Array<UnitOrderType>> UNIT_ORDER_TYPES = new ThreadLocal<Array<UnitOrderType>>() {
		@Override
		protected Array<UnitOrderType> initialValue() {
			return Array.with(UnitOrderType.values);
		}
	};

	/**
	 * Returns a list containing all legal orders for a structure in the current state. For a list of all types of
	 * orders available to a Structure, see {@link StructureOrderType}.
	 * 
	 * @param state
	 *            The current {@link HunterKillerState} of the game.
	 * @param structure
	 *            The {@link Structure} to receive legal orders for.
	 */
	public static List<StructureOrder> getAllLegalOrders(HunterKillerState state, Structure structure) {
		// Create a list to write to
		List<StructureOrder> orders = new ArrayList<StructureOrder>();

		// Check if the structure can spawn anything
		if (!structure.canSpawnAUnit(state)) {
			// If not, return now because nothing can be spawned
			return orders;
		}

		// Check for each unit type if the structure can spawn it
		for (UnitType type : UnitType.values) {
			if (structure.canSpawn(state, type))
				orders.add(structure.spawn(type));
		}

		// Return the list of legal orders
		return orders;
	}

	/**
	 * Returns a list containing all legal orders for a structure in the current state. For a list of all types of
	 * orders available to a Structure, see {@link StructureOrderType}.
	 * 
	 * @param state
	 *            The current {@link HunterKillerState} of the game.
	 * @param structure
	 *            The {@link Structure} to receive legal orders for.
	 */
	public static StructureOrder getRandomOrder(HunterKillerState state, Structure structure) {

		Array<UnitType> types = UNIT_TYPES.get();
		types.shuffle();

		// Check for each unit type if the structure can spawn it
		for (UnitType type : types) {
			if (structure.canSpawn(state, type))
				return structure.spawn(type);
		}

		// Return the list of legal orders
		return null;
	}

	/**
	 * Returns a random order for the given unit.
	 */
	public static UnitOrder getRandomOrder(HunterKillerState state, Unit unit) {

		Array<UnitOrderType> types = UNIT_ORDER_TYPES.get();
		types.shuffle();

		for (UnitOrderType type : types) {

			UnitOrder move = null;

			if (!type.hasLocation) {
				// Rotate
				return unit.rotate(type == UnitOrderType.ROTATE_CLOCKWISE);
			} else if (type != UnitOrderType.MOVE) {
				// Attack
				move = getRandomAttackOrder(state, unit, false, type == UnitOrderType.ATTACK_SPECIAL);
			} else {
				// Move
				move = getRandomMoveOrder(state, unit);
			}

			if (move != null)
				return move;
		}

		return null;
	}

	/**
	 * Returns a list containing all legal orders for a unit in the current state. For a list of all types of orders
	 * available to a Unit, see {@link UnitOrderType}.
	 * 
	 * {@link MoveGenerator#getAllLegalOrders(HunterKillerState, Unit, boolean, boolean, boolean)}
	 */
	public static List<UnitOrder> getAllLegalOrders(HunterKillerState state, Unit unit) {
		return getAllLegalOrders(state, unit, true, true, true);
	}

	/**
	 * Returns a list containing all legal orders for a unit in the current state. For a list of all types of orders
	 * available to a Unit, see {@link UnitOrderType}.
	 * 
	 * @param state
	 *            The current {@link HunterKillerState} of the game.
	 * @param unit
	 *            The {@link Unit} to receive legal orders for.
	 * @param includeRotation
	 *            Whether or not to include orders in the rotation category.
	 * @param includeMovement
	 *            Whether or not to include orders in the movement category.
	 * @param includeAttack
	 *            Whether or not to include orders in the attack category.
	 */
	public static List<UnitOrder> getAllLegalOrders(HunterKillerState state, Unit unit, boolean includeRotation, boolean includeMovement,
			boolean includeAttack) {
		// Create a list to write to
		List<UnitOrder> orders = new ArrayList<UnitOrder>();

		// Get all legal movement orders
		if (includeMovement)
			orders.addAll(getAllLegalMoveOrders(state, unit));

		// Get all legal attack orders
		if (includeAttack)
			orders.addAll(getAllLegalAttackOrders(state, unit));

		// Get all legal rotation orders
		if (includeRotation)
			orders.addAll(getAllLegalRotationOrders(state, unit));

		// Return the list of legal orders
		return orders;
	}

	/**
	 * Returns a collection of {@link UnitOrder}s containing all legal orders in the movement category. See
	 * {@link UnitOrderType} for a list of possible orders.
	 * 
	 * {@link MoveGenerator#getAllLegalOrders(HunterKillerState, Unit)}
	 */
	public static List<UnitOrder> getAllLegalMoveOrders(HunterKillerState state, Unit unit) {
		// Create a list to write to
		List<UnitOrder> orders = new ArrayList<UnitOrder>();

		// Get the map we are currently on
		Map map = state.getMap();
		// And the unit's location
		MapLocation unitLocation = unit.getLocation();

		// Check what movement options we have
		for (Direction direction : Direction.values) {
			MapLocation newLocaction = map.getAdjacentLocationInDirection(unitLocation, direction);
			if (map.isMovePossible(unitLocation, newLocaction))
				orders.add(unit.move(newLocaction, map));
		}

		return orders;
	}

	/**
	 * Returns a random legal move order. See {@link UnitOrderType} for a list of possible orders.
	 */
	public static UnitOrder getRandomMoveOrder(HunterKillerState state, Unit unit) {

		// Get the map we are currently on & the unit's location
		Map map = state.getMap();
		MapLocation unitLocation = unit.getLocation();

		Array<Direction> directions = DIRECTIONS.get();
		directions.shuffle();

		// Check what movement options we have
		for (Direction direction : directions) {
			MapLocation newLocaction = map.getAdjacentLocationInDirection(unitLocation, direction);
			if (map.isMovePossible(unitLocation, newLocaction))
				return unit.move(newLocaction, map);
		}

		return null;
	}

	/**
	 * Returns a collection of {@link UnitOrder}s containing all legal orders in the rotation category. See
	 * {@link UnitOrderType} for a list of possible orders.
	 * 
	 * {@link MoveGenerator#getAllLegalOrders(HunterKillerState, Unit)}
	 */
	public static List<UnitOrder> getAllLegalRotationOrders(HunterKillerState state, Unit unit) {
		// Create a list to write to
		List<UnitOrder> orders = new ArrayList<UnitOrder>();

		// Can always rotate east or west
		orders.add(unit.rotate(true));
		orders.add(unit.rotate(false));

		return orders;
	}

	/**
	 * Returns a collection of {@link UnitOrder}s containing all legal orders in the attack category. See
	 * {@link UnitOrderType} for a list of possible orders.
	 * 
	 * {@link MoveGenerator#getAllLegalAttackOrders(HunterKillerState, Unit, boolean)}
	 */
	public static List<UnitOrder> getAllLegalAttackOrders(HunterKillerState state, Unit unit) {
		return getAllLegalAttackOrders(state, unit, false);
	}

	/**
	 * Returns a collection of {@link UnitOrder}s containing all legal orders, from the Unit's Field-of-View, in the
	 * attack category. See {@link UnitOrderType} for a list of possible orders.
	 * 
	 * @param state
	 *            The current {@link HunterKillerState} of the game.
	 * @param unit
	 *            The {@link Unit} to receive legal orders for.
	 * @param usePlayersFoV
	 *            Whether or not to use the Player's Field-of-View, instead of the Unit's.
	 */
	public static List<UnitOrder> getAllLegalAttackOrders(HunterKillerState state, Unit unit, boolean usePlayersFoV) {
		// Create a list to write to
		List<UnitOrder> orders = new ArrayList<UnitOrder>();

		// Get the map we are currently on
		Map map = state.getMap();
		// And the unit's location
		MapLocation unitLocation = unit.getLocation();

		// Create a field-of-view set
		HashSet<MapLocation> fov = null;

		// Determine which field-of-view we'll be using
		if (usePlayersFoV) {
			fov = state.getPlayer(unit.getControllingPlayerID())
						.getCombinedFieldOfView(map);
		} else {
			fov = unit.getFieldOfView();
		}

		if (unit instanceof Infected) {
			// Since we know an infected can only do a melee attack (range = 1)
			for (Direction direction : Direction.values) {
				// Make an order for each adjacent location
				MapLocation targetLocation = map.getAdjacentLocationInDirection(unitLocation, direction);
				if (targetLocation != null)
					orders.add(unit.attack(targetLocation, false));
			}
			// Also add the unit's own location as a possibility
			orders.add(unit.attack(unitLocation, false));
		} else {
			// Get the unit's attack range
			int attackRange = Unit.getAttackRange(unit.getType());

			// Go through the field-of-view
			for (MapLocation location : fov) {
				// Check if this location is within the unit's attack range
				if (map.getDistance(unitLocation, location) <= attackRange) {
					// Check if the special for this unit is available
					if (unit.getSpecialAttackCooldown() <= 0) {
						// A Soldier's special can't target Walls
						if (unit instanceof Soldier && map.getFeatureAtLocation(location) instanceof Wall)
							continue;
						// Create a special attack order for this location
						orders.add(unit.attack(location, true));
					}
					// Create an attack order for this location
					orders.add(unit.attack(location, false));
				}
			}
		}

		// Return the list of legal orders
		return orders;
	}

	/**
	 * Returns a random legal attack order, from the Unit's Field-of-View. See {@link UnitOrderType} for a list of
	 * possible orders.
	 * Friendly-fire moves are already removed.
	 * 
	 * @param state
	 *            The current {@link HunterKillerState} of the game.
	 * @param unit
	 *            The {@link Unit} to receive legal orders for.
	 * @param usePlayersFoV
	 *            Whether or not to use the Player's Field-of-View, instead of the Unit's.
	 */
	public static UnitOrder getRandomAttackOrder(HunterKillerState state, Unit unit, boolean usePlayersFoV, boolean useSpecial) {

		// If we want special, but we can't, stop
		if (useSpecial && unit.getSpecialAttackCooldown() > 0)
			return null;

		// Get the map we are currently on
		Map map = state.getMap();
		// And the unit's location
		MapLocation unitLocation = unit.getLocation();

		// Create a field-of-view set
		HashSet<MapLocation> fov = null;

		// Determine which field-of-view we'll be using
		if (usePlayersFoV) {
			fov = state.getPlayer(unit.getControllingPlayerID())
						.getCombinedFieldOfView(map);
		} else {
			fov = unit.getFieldOfView();
		}

		if (unit instanceof Infected) {

			Array<Direction> directions = DIRECTIONS.get();
			directions.shuffle();

			// Since we know an infected can only do a melee attack (range = 1)
			for (Direction direction : directions) {
				// Make an order for the first adjacent location that contains a unit.
				MapLocation targetLocation = map.getAdjacentLocationInDirection(unitLocation, direction);

				if (targetLocation == null)
					continue;

				Unit target = map.getUnitAtLocation(targetLocation);
				if (target != null) {

					// No sense in killing allied infected.
					if (target instanceof Infected && target.getControllingPlayerID() == unit.getControllingPlayerID())
						continue;

					return unit.attack(targetLocation, false);
				} else {
					MapFeature feature = map.getFeatureAtLocation(targetLocation);
					if (!(feature instanceof Structure))
						continue;
					if (((Structure) feature).getControllingPlayerID() == unit.getControllingPlayerID())
						continue;

					return unit.attack(targetLocation, false);
				}
			}

		} else {
			// Get the unit's attack range
			int attackRange = Unit.getAttackRange(unit.getType());

			// TODO: Optimize
			Array<MapLocation> locations = new Array<MapLocation>();

			for (MapLocation location : fov) {
				locations.add(location);
			}

			locations.shuffle();

			// Go through the field-of-view
			for (MapLocation location : locations) {

				// Check if this location is within the unit's attack range
				if (map.getDistance(unitLocation, location) > attackRange)
					continue;

				Controlled target = map.getUnitAtLocation(location);
				MapFeature feature = null;

				if (target == null) {
					feature = map.getFeatureAtLocation(location);
					if (feature instanceof Controlled)
						target = (Controlled) feature;
				}

				// If we don't target anything, continue
				if (target == null)
					continue;
				// If we target a friendly feature, continue
				if (target instanceof MapFeature && target.getControllingPlayerID() == unit.getControllingPlayerID())
					continue;

				if (useSpecial) {

					// Soldiers can't grenade walls
					if (unit instanceof Soldier && feature instanceof Wall)
						continue;

					// Do not target friendlies if we aren't a medic.
					if (!(unit instanceof Medic) && target.getControllingPlayerID() == unit.getControllingPlayerID())
						continue;

					// Do not heal enemies
					if (unit instanceof Medic && target.getControllingPlayerID() != unit.getControllingPlayerID())
						continue;

					// Do not heal non-damaged units
					if (unit instanceof Medic && target instanceof Unit && !((Unit) target).isDamaged())
						continue;

					return unit.attack(location, true);
				} else {

					// Don't target friendlies
					if (target.getControllingPlayerID() == unit.getControllingPlayerID())
						continue;

					return unit.attack(location, false);
				}
			}
		}

		return null;
	}

}
