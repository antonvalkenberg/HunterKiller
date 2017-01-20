package net.codepoke.ai.challenge.hunterkiller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import net.codepoke.ai.challenge.hunterkiller.enums.Direction;
import net.codepoke.ai.challenge.hunterkiller.enums.StructureOrderType;
import net.codepoke.ai.challenge.hunterkiller.enums.UnitOrderType;
import net.codepoke.ai.challenge.hunterkiller.enums.UnitType;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.mapfeature.Structure;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.mapfeature.Wall;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Infected;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Soldier;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Unit;
import net.codepoke.ai.challenge.hunterkiller.orders.HunterKillerOrder;
import net.codepoke.ai.challenge.hunterkiller.orders.StructureOrder;
import net.codepoke.ai.challenge.hunterkiller.orders.UnitOrder;

/**
 * This class generates legal {@link HunterKillerOrder}s. This method initialises all orders with a default action
 * index. It is advised to adjust these indexes to your preference.
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
public class MoveGenerator {

	/**
	 * Returns a list containing all legal orders for a structure in the current state. For a list of all types of
	 * orders
	 * available to a Structure, see {@link StructureOrderType}.
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
		if (!structure.canSpawn(state)) {
			// If not, return now because nothing can be spawned
			return orders;
		}

		// Check for each unit type if the structure can spawn it
		for (UnitType type : UnitType.values()) {
			if (structure.canSpawn(type, state))
				orders.add(structure.spawn(type));
		}

		// Return the list of legal orders
		return orders;
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

		// Get all legal rotation orders
		if (includeRotation)
			orders.addAll(getAllLegalRotationOrders(state, unit));

		// Get all legal movement orders
		if (includeMovement)
			orders.addAll(getAllLegalMoveOrders(state, unit));

		// Get all legal attack orders
		if (includeAttack)
			orders.addAll(getAllLegalAttackOrders(state, unit));

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
		for (Direction direction : Direction.values()) {
			if (map.isMovePossible(unitLocation, direction))
				orders.add(unit.move(direction, map));
		}

		return orders;
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
		HashSet<MapLocation> fov = new HashSet<MapLocation>();

		// Determine which field-of-view we'll be using
		if (usePlayersFoV) {
			fov = state.getPlayer(unit.getControllingPlayerID())
						.getCombinedFieldOfView(map);
		} else {
			fov = unit.getFieldOfView();
		}

		// Get the unit's attack range
		int attackRange = Unit.getAttackRange(unit.getType());

		// Go through the field-of-view
		for (MapLocation location : fov) {
			// Check if this location is within the unit's attack range
			if (map.getDistance(unitLocation, location) <= attackRange) {
				// Check if the special for this unit is available, but don't create an order for Infected (can't order)
				if (unit.getSpecialAttackCooldown() <= 0 && !(unit instanceof Infected)) {
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

		// Return the list of legal orders
		return orders;
	}
}
