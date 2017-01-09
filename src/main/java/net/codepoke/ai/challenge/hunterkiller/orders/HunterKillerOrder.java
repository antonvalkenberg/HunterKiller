package net.codepoke.ai.challenge.hunterkiller.orders;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.codepoke.ai.challenge.hunterkiller.HunterKillerAction;
import net.codepoke.ai.challenge.hunterkiller.Player;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.GameObject;

/**
 * Abstract class representing a {@link Player}'s order to a single {@link GameObject}. Note: this
 * class has a natural ordering that is inconsistent with equals.
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class HunterKillerOrder
		implements Comparable<HunterKillerOrder> {

	// region Properties

	/**
	 * The ID of the object that this order is for.
	 */
	public int objectID;

	/**
	 * The index of this order in a {@link HunterKillerAction}.
	 */
	@Setter
	public int actionIndex;

	// endregion

	// region Constructor

	/**
	 * Constructs a new order.
	 * 
	 * @param object
	 *            The object this order is for.
	 * @param index
	 *            The index this order has in the {@link HunterKillerAction}.
	 */
	public HunterKillerOrder(GameObject object, int index) {
		this.objectID = object.getID();
		this.actionIndex = index;
	}

	// endregion

	// region Overridden methods

	/**
	 * Compares two orders according to their index. This is used to order orders when executing a
	 * {@link HunterKillerAction}. Zero means they have the same index (note that this is undesired
	 * within a single action). A negative number means this order has a lower index. A positive
	 * number means the other order has a higher index.
	 */
	@Override
	public int compareTo(HunterKillerOrder other) {
		if (this.actionIndex < other.actionIndex) {
			return -1;
		} else if (this.actionIndex == other.actionIndex) {
			return 0;
		} else {
			return 1;
		}
	}

	// endregion

}
