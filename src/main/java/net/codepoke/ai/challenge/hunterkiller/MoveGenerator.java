package net.codepoke.ai.challenge.hunterkiller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import net.codepoke.ai.challenge.hunterkiller.enums.BaseOrderType;
import net.codepoke.ai.challenge.hunterkiller.enums.Direction;
import net.codepoke.ai.challenge.hunterkiller.enums.UnitOrderType;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.mapfeature.Base;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Infected;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Medic;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Soldier;
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

	private static final int DEFAULT_ACTION_INDEX = 0;

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

		// Check if the base's spawn location is occupied
		if (!state.getMap()
					.isTraversable(base.getSpawnLocation())) {
			// If so, return now because nothing can be spawned
			return orders;
		}

		// Get the player
		Player player = state.getPlayer(base.getPlayerID());

		// Check if the player has enough resources to spawn a soldier
		if (player.getResource() >= Soldier.SOLDIER_SPAWN_COST) {
			// Create an order to spawn a Soldier
			orders.add(new BaseOrder(base, BaseOrderType.SPAWN_SOLDIER, DEFAULT_ACTION_INDEX));
		}

		// Check if the player has enough resources to spawn a medic
		if (player.getResource() >= Medic.MEDIC_SPAWN_COST) {
			// Create an order to spawn a Soldier
			orders.add(new BaseOrder(base, BaseOrderType.SPAWN_MEDIC, DEFAULT_ACTION_INDEX));
		}

		// Check if the player has enough resources to spawn an infected
		if (player.getResource() >= Infected.INFECTED_SPAWN_COST) {
			// Create an order to spawn a Soldier
			orders.add(new BaseOrder(base, BaseOrderType.SPAWN_INFECTED, DEFAULT_ACTION_INDEX));
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
		orders.add(new UnitOrder(unit, UnitOrderType.ROTATE_EAST, DEFAULT_ACTION_INDEX));
		orders.add(new UnitOrder(unit, UnitOrderType.ROTATE_WEST, DEFAULT_ACTION_INDEX));

		// Check what movement options we have
		if (map.isMovePossible(unitLocation, Direction.NORTH)) {
			orders.add(new UnitOrder(unit, UnitOrderType.MOVE_NORTH, DEFAULT_ACTION_INDEX,
										map.getLocationInDirection(unitLocation, Direction.NORTH, Unit.MOVEMENT_RANGE)));
		}
		if (map.isMovePossible(unitLocation, Direction.EAST)) {
			orders.add(new UnitOrder(unit, UnitOrderType.MOVE_EAST, DEFAULT_ACTION_INDEX,
										map.getLocationInDirection(unitLocation, Direction.EAST, Unit.MOVEMENT_RANGE)));
		}
		if (map.isMovePossible(unitLocation, Direction.SOUTH)) {
			orders.add(new UnitOrder(unit, UnitOrderType.MOVE_SOUTH, DEFAULT_ACTION_INDEX,
										map.getLocationInDirection(unitLocation, Direction.SOUTH, Unit.MOVEMENT_RANGE)));
		}
		if (map.isMovePossible(unitLocation, Direction.WEST)) {
			orders.add(new UnitOrder(unit, UnitOrderType.MOVE_WEST, DEFAULT_ACTION_INDEX,
										map.getLocationInDirection(unitLocation, Direction.WEST, Unit.MOVEMENT_RANGE)));
		}

		// TODO create attack orders for locations in player's FoV versus Unit's?
		// Get the Player's field-of-view
		// HashSet<MapLocation> playerFoV = state.getPlayer(unit.getSquadPlayerID()).getCombinedFieldOfView(map);
		// Get the Unit's field-of-view
		HashSet<MapLocation> unitFoV = unit.getFieldOfView();

		// Get the unit's attack range
		int attackRange = Unit.getAttackRange(unit);

		for (MapLocation location : unitFoV) {
			// Check if this location is within the unit's attack range
			if (map.getDistance(unitLocation, location) <= attackRange) {
				// Check if the special for this unit is available
				if (unit.getSpecialAttackCooldown() <= 0) {
					// Create a special attack order for this location
					orders.add(new UnitOrder(unit, UnitOrderType.ATTACK_SPECIAL, DEFAULT_ACTION_INDEX, location));
				}
				// Create an attack order for this location
				orders.add(new UnitOrder(unit, UnitOrderType.ATTACK, DEFAULT_ACTION_INDEX, location));
			}
		}

		// Return the list of legal orders
		return orders;
	}

}
