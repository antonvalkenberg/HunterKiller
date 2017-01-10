package net.codepoke.ai.challenge.hunterkiller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import net.codepoke.ai.challenge.hunterkiller.enums.BaseOrderType;
import net.codepoke.ai.challenge.hunterkiller.enums.Direction;
import net.codepoke.ai.challenge.hunterkiller.enums.UnitOrderType;
import net.codepoke.ai.challenge.hunterkiller.enums.UnitType;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.mapfeature.Base;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Unit;
import net.codepoke.ai.challenge.hunterkiller.orders.BaseOrder;
import net.codepoke.ai.challenge.hunterkiller.orders.HunterKillerOrder;
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
	 * Returns a list containing all legal orders for a base in the current state. For a list of all types of orders
	 * available to a Base, see {@link BaseOrderType}.
	 * 
	 * @param state
	 *            The current {@link HunterKillerState} of the game.
	 * @param base
	 *            The {@link Base} to receive legal orders for.
	 */
	public static List<BaseOrder> getAllLegalOrders(HunterKillerState state, Base base) {
		// Create a list to write to
		List<BaseOrder> orders = new ArrayList<BaseOrder>();

		// Check if the base can spawn anything
		if (base.canSpawn(state)) {
			// If so, return now because nothing can be spawned
			return orders;
		}

		// Check for each unit type if the base can spawn it
		for (UnitType type : UnitType.values()) {
			if (base.canSpawn(type, state))
				orders.add(base.spawn(type));
		}

		// Return the list of legal orders
		return orders;
	}

	/**
	 * Returns a list containing all legal orders for a unit in the current state. For a list of all types of orders
	 * available to a Unit, see {@link UnitOrderType}.
	 * 
	 * @param state
	 *            The current {@link HunterKillerState} of the game.
	 * @param unit
	 *            The {@link Unit} to receive legal orders for.
	 */
	public static List<UnitOrder> getAllLegalOrders(HunterKillerState state, Unit unit) {
		// Create a list to write to
		List<UnitOrder> orders = new ArrayList<UnitOrder>();

		// Get the map we are currently on
		Map map = state.getMap();
		// And the unit's location
		MapLocation unitLocation = unit.getLocation();

		// Can always rotate east or west
		orders.add(unit.rotate(true));
		orders.add(unit.rotate(false));

		// Check what movement options we have
		for (Direction direction : Direction.values()) {
			if (map.isMovePossible(unitLocation, direction))
				orders.add(unit.move(direction, map));
		}

		// TODO create attack orders for locations in player's FoV versus Unit's?
		// Get the Player's field-of-view
		// HashSet<MapLocation> playerFoV = state.getPlayer(unit.getSquadPlayerID()).getCombinedFieldOfView(map);
		// Get the Unit's field-of-view
		HashSet<MapLocation> unitFoV = unit.getFieldOfView();

		// Get the unit's attack range
		int attackRange = Unit.getAttackRange(unit.getType());

		for (MapLocation location : unitFoV) {
			// Check if this location is within the unit's attack range
			if (map.getDistance(unitLocation, location) <= attackRange) {
				// Check if the special for this unit is available
				if (unit.getSpecialAttackCooldown() <= 0) {
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
