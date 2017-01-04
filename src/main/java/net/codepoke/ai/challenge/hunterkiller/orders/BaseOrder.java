package net.codepoke.ai.challenge.hunterkiller.orders;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.codepoke.ai.challenge.hunterkiller.HunterKillerAction;
import net.codepoke.ai.challenge.hunterkiller.enums.BaseOrderType;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.mapfeature.Base;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Unit;

/**
 * Class representing a {@link HunterKillerOrder} performed by a {@link Base}. Currently, the base
 * can only spawn {@link Unit}s.
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BaseOrder
		extends HunterKillerOrder {

	// region Properties

	/**
	 * The type of order.
	 */
	private BaseOrderType orderType;

	// endregion

	// region Constructor

	/**
	 * Constructs a new instance.
	 * 
	 * @param base
	 *            The base this order is for.
	 * @param type
	 *            The type of order.
	 * @param actionIndex
	 *            The index this order has in the {@link HunterKillerAction}.
	 */
	public BaseOrder(Base base, BaseOrderType type, int actionIndex) {
		super(base, actionIndex);
		this.orderType = type;
	}

	// endregion

}
