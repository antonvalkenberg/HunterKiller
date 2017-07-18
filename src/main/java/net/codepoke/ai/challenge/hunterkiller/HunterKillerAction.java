package net.codepoke.ai.challenge.hunterkiller;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.codepoke.ai.Action;
import net.codepoke.ai.challenge.hunterkiller.enums.StructureOrderType;
import net.codepoke.ai.challenge.hunterkiller.enums.UnitOrderType;
import net.codepoke.ai.challenge.hunterkiller.enums.UnitType;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.GameObject;
import net.codepoke.ai.challenge.hunterkiller.orders.HunterKillerOrder;
import net.codepoke.ai.challenge.hunterkiller.orders.StructureOrder;
import net.codepoke.ai.challenge.hunterkiller.orders.UnitOrder;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;

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
@EqualsAndHashCode
@NoArgsConstructor
public class HunterKillerAction
		implements Action, Serializable {

	public static int ORDER_STRUCTURE = 0, ORDER_UNIT = 1;

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
	private Array<HunterKillerOrder> orders;

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
		orders = new Array<HunterKillerOrder>();
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
			for (int i = 0; i < orders.size; i++) {
				if (orders.get(i)
							.getObjectID() == object.getID()) {
					orders.removeIndex(i);
					return true;
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
		int[] ids = new int[orders.size];
		for (int i = 0; i < orders.size; i++) {
			ids[i] = orders.get(i)
							.getObjectID();
		}
		return ids;
	}

	// endregion

	// region Overridden methods

	@Override
	public void write(Json json) {

		// We read an array: {actingPlayerID, currentRound, orders.size, per order[objectId, type, {orderType} /
		// {unitType, orderType, x,y}]
		json.writeArrayStart("actions");

		json.writeValue(actingPlayerID);
		json.writeValue(currentRound);

		json.writeValue(orders.size);

		for (int i = 0; i < orders.size; i++) {

			HunterKillerOrder order = orders.get(i);

			json.writeValue(order.objectID);

			if (order instanceof StructureOrder) {
				StructureOrder sOrder = (StructureOrder) order;

				json.writeValue(ORDER_STRUCTURE);
				json.writeValue(sOrder.getOrderType()
										.ordinal());
			} else if (order instanceof UnitOrder) {
				UnitOrder uOrder = (UnitOrder) order;

				MapLocation loc = uOrder.getTargetLocation();

				json.writeValue(ORDER_UNIT);
				json.writeValue(uOrder.getUnitType()
										.ordinal());
				json.writeValue(uOrder.getOrderType()
										.ordinal());

				if (uOrder.getOrderType().hasLocation) {
					json.writeValue(loc.getX());
					json.writeValue(loc.getY());
				}
			}

		}

		json.writeArrayEnd();

	}

	@Override
	public void read(Json json, JsonValue jsonData) {

		// We read an array: {actingPlayerID, currentRound, orders.size, per order[objectId, type, {orderType} /
		// {unitType, orderType, x,y}]
		JsonValue raw = jsonData.getChild("actions");

		actingPlayerID = raw.asInt();
		currentRound = (raw = raw.next).asInt();

		orders = new Array<HunterKillerOrder>((raw = raw.next).asInt());

		// Each next
		while ((raw = raw.next) != null) {

			int objectID = raw.asInt();
			int type = (raw = raw.next).asInt();

			HunterKillerOrder order = null;

			if (type == ORDER_STRUCTURE) {
				StructureOrder sOrder = new StructureOrder();

				sOrder.setOrderType(StructureOrderType.values[(raw = raw.next).asInt()]);

				order = sOrder;
			} else if (type == ORDER_UNIT) {
				UnitOrder uOrder = new UnitOrder();

				uOrder.setUnitType(UnitType.values[(raw = raw.next).asInt()]);

				UnitOrderType orderType = UnitOrderType.values[(raw = raw.next).asInt()];
				uOrder.setOrderType(orderType);

				if (orderType.hasLocation) {
					uOrder.setTargetLocation(new MapLocation((raw = raw.next).asInt(), (raw = raw.next).asInt()));
				}

				order = uOrder;
			}

			order.objectID = objectID;
			orders.add(order);

		}

	}

	// endregion

}
