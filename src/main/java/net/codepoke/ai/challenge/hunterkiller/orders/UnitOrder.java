package net.codepoke.ai.challenge.hunterkiller.orders;

import lombok.Getter;
import net.codepoke.ai.challenge.hunterkiller.HunterKillerAction;
import net.codepoke.ai.challenge.hunterkiller.MapLocation;
import net.codepoke.ai.challenge.hunterkiller.enums.UnitOrderType;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Unit;

/**
 * Class representing a {@link HunterKillerOrder} performed by a {@link Unit}. An order can
 * currently involve movement, rotation or attacking.
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
@Getter
public class UnitOrder
		extends HunterKillerOrder {

	// region Properties

	/**
	 * The type of order.
	 */
	private UnitOrderType orderType;

	/**
	 * The target location of this order.
	 */
	private MapLocation targetLocation;

	// endregion

	// region Constructor

	/**
	 * Constructs a new instance.
	 * 
	 * {@link UnitOrder#UnitOrder(Unit, UnitOrderType, MapLocation)}
	 */
	public UnitOrder(Unit unit, UnitOrderType type, int actionIndex) {
		super(unit, actionIndex);
		this.orderType = type;
	}

	/**
	 * Constructs a new instance with a target location.
	 * 
	 * @param unit
	 *            The unit the order is for.
	 * @param type
	 *            The type of order.
	 * @param actionIndex
	 *            The index this order has in the {@link HunterKillerAction}.
	 * @param target
	 *            The target location for this order.
	 */
	public UnitOrder(Unit unit, UnitOrderType type, int actionIndex, MapLocation target) {
		this(unit, type, actionIndex);
		this.targetLocation = new MapLocation(target.getX(), target.getY());
	}

	// endregion

}
