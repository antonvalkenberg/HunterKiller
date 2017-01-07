package net.codepoke.ai.challenge.hunterkiller;

import java.util.ArrayList;
import java.util.List;

import net.codepoke.ai.challenge.hunterkiller.enums.BaseOrderType;
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

		// TODO finish unit-order move generator

		// Return the list of legal orders
		return orders;
	}

}
