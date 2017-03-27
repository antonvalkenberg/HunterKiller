package net.codepoke.ai.challenge.hunterkiller.orders;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.codepoke.ai.challenge.hunterkiller.HunterKillerRules;
import net.codepoke.ai.challenge.hunterkiller.Player;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.GameObject;

/**
 * Abstract class representing a {@link Player}'s order to a single {@link GameObject}.
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
@Getter
@EqualsAndHashCode
@NoArgsConstructor
public abstract class HunterKillerOrder {

	// region Properties

	/**
	 * The ID of the object that this order is for.
	 */
	public int objectID;

	/**
	 * Whether or not this order was accepted by the {@link HunterKillerRules}.
	 */
	@Setter
	private boolean accepted = false;

	// endregion

	// region Constructor

	/**
	 * Constructs a new order.
	 * 
	 * @param object
	 *            The object this order is for.
	 */
	public HunterKillerOrder(GameObject object) {
		this.objectID = object.getID();
	}

	// endregion

}
