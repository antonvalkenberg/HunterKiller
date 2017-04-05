package net.codepoke.ai.challenge.hunterkiller.orders;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.codepoke.ai.challenge.hunterkiller.Map;
import net.codepoke.ai.challenge.hunterkiller.MapLocation;
import net.codepoke.ai.challenge.hunterkiller.enums.UnitOrderType;
import net.codepoke.ai.challenge.hunterkiller.enums.UnitType;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.mapfeature.MapFeature;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.mapfeature.Structure;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Unit;

/**
 * Class representing a {@link HunterKillerOrder} performed by a {@link Unit}. An order can
 * currently involve movement, rotation or attacking.
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
@Getter
@Setter
@NoArgsConstructor
public class UnitOrder
		extends HunterKillerOrder {

	// region Properties

	/**
	 * The type of unit this order is for.
	 */
	private UnitType unitType;

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
	 * Constructs a new order for a unit.
	 * 
	 * {@link UnitOrder#UnitOrder(Unit, UnitOrderType, MapLocation)}
	 */
	public UnitOrder(Unit unit, UnitOrderType type) {
		super(unit);
		this.orderType = type;
		this.unitType = unit.getType();
	}

	/**
	 * Constructs a new order for a unit with a target location.
	 * 
	 * @param unit
	 *            The unit the order is for.
	 * @param type
	 *            The type of order.
	 * @param target
	 *            The target location for this order.
	 */
	public UnitOrder(Unit unit, UnitOrderType type, MapLocation target) {
		this(unit, type);
		this.targetLocation = new MapLocation(target.getX(), target.getY());
	}

	// endregion

	// region Public methods

	/**
	 * Whether or not this order is targeting a location that contains either a Unit of Structure.
	 * 
	 * @param unit
	 *            The unit that this order is for.
	 * @param map
	 *            The current state of the Map.
	 */
	public boolean isAttackOrderWithoutTarget(Unit unit, Map map) {
		if (!isAttackOrder())
			return false;
		Unit target = map.getUnitAtLocation(targetLocation);
		MapFeature feature = map.getFeatureAtLocation(targetLocation);
		return target == null && !(feature instanceof Structure);
	}

	/**
	 * Whether or not this order is targeting an ally Structure.
	 * 
	 * {@link UnitOrder#isAttackOrderWithoutTarget(Unit, Map)}
	 */
	public boolean isAttackOrderTargetingAllyBase(Unit unit, Map map) {
		if (!isAttackOrder())
			return false;
		MapFeature feature = map.getFeatureAtLocation(targetLocation);
		return feature instanceof Structure && ((Structure) feature).getControllingPlayerID() == unit.getControllingPlayerID();
	}

	/**
	 * Whether or not this order is targeting an ally Unit.
	 * 
	 * {@link UnitOrder#isAttackOrderWithoutTarget(Unit, Map)}
	 */
	public boolean isAttackOrderTargetingAllyUnit(Unit unit, Map map) {
		if (!isAttackOrder())
			return false;
		Unit target = map.getUnitAtLocation(targetLocation);
		return target != null && target.getControllingPlayerID() == unit.getControllingPlayerID();
	}

	/**
	 * Whether or not this order is an attack order (either attack or special attack).
	 */
	public boolean isAttackOrder() {
		return orderType == UnitOrderType.ATTACK || orderType == UnitOrderType.ATTACK_SPECIAL;
	}

	// endregion

}
