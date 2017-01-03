package net.codepoke.ai.challenge.hunterkiller;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import net.codepoke.ai.Action;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.GameObject;
import net.codepoke.ai.challenge.hunterkiller.orders.HunterKillerOrder;

import com.badlogic.gdx.utils.IntArray;

/**
 * Class representing an {@link Action} in the game. A {@link Player}'s turn consists of a
 * collection of orders that mutate the current game state. Note that for an action to be executed,
 * the player and round must match the current state's player and round. Otherwise, the action will
 * be ignored.
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
@Getter
public class HunterKillerAction
		implements Action {

	// region Properties

	/**
	 * The ID of the player that is acting in the current state.
	 */
	private int actingPlayerID;

	/**
	 * The round number of the current state.
	 */
	private int currentRound;

	/**
	 * Collection of orders in this action.
	 */
	private List<HunterKillerOrder> orders;

	// endregion

	// region Constructor

	/**
	 * Constructs a new HunterKillerAction to be executed on the provided state.
	 * 
	 * @param state
	 *            The current state of the game.
	 */
	public HunterKillerAction(HunterKillerState state) {
		this.actingPlayerID = state.getCurrentPlayer();
		this.currentRound = state.getCurrentRound();
		orders = new ArrayList<HunterKillerOrder>();
	}

	// endregion

	// region Public methods

	/**
	 * Add an order to this action's list of orders. An order will only be added if the current list
	 * does not yet contain an order for the object that the order is for.
	 * 
	 * @param order
	 *            The order to add.
	 * @return Whether or not the order was successfully added.
	 */
	public boolean addOrder(HunterKillerOrder order) {
		// Check if there isn't already an order for the object
		IntArray objectIDs = new IntArray(getObjectIDs());
		if (!objectIDs.contains(order.getObjectID())) {
			orders.add(order);
			return true;
		}
		return false;
	}

	/**
	 * Tries to remove an order from this action's list of orders.
	 * 
	 * @param object
	 *            The object to remove an order for.
	 * @return Whether or not the order for the specified object was successfully removed.
	 */
	public boolean removeOrderForObject(GameObject object) {
		// Try to remove the order that is for the specified object
		IntArray objectIDs = new IntArray(getObjectIDs());
		if (objectIDs.contains(object.getID())) {
			for (HunterKillerOrder order : orders) {
				if (order.getObjectID() == object.getID()) {
					return orders.remove(order);
				}
			}
			return false;
		}
		return false;
	}

	/**
	 * Returns the IDs of the objects that have orders in this action.
	 * 
	 * @return Array containing the object's IDs.
	 */
	public int[] getObjectIDs() {
		int[] ids = new int[orders.size()];
		for (int i = 0; i < orders.size(); i++) {
			ids[i] = orders.get(i)
							.getObjectID();
		}
		return ids;
	}

	// endregion

}
