package net.codepoke.ai.challenge.hunterkiller.gameobjects.unit;

import java.util.HashSet;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.codepoke.ai.challenge.hunterkiller.HunterKillerConstants;
import net.codepoke.ai.challenge.hunterkiller.HunterKillerState;
import net.codepoke.ai.challenge.hunterkiller.Map;
import net.codepoke.ai.challenge.hunterkiller.MapLocation;
import net.codepoke.ai.challenge.hunterkiller.Player;
import net.codepoke.ai.challenge.hunterkiller.enums.Direction;
import net.codepoke.ai.challenge.hunterkiller.enums.Direction.Rotation;
import net.codepoke.ai.challenge.hunterkiller.enums.UnitOrderType;
import net.codepoke.ai.challenge.hunterkiller.enums.UnitType;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.Controlled;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.GameObject;
import net.codepoke.ai.challenge.hunterkiller.orders.UnitOrder;

/**
 * Abstract class representing a unit in HunterKiller.
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
@Getter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public abstract class Unit
		extends GameObject
		implements Controlled {

	// region Properties

	/**
	 * The ID of the player that has this Unit in it's squad.
	 */
	private int controllingPlayerID;

	/**
	 * The type of unit.
	 */
	private UnitType type;

	/**
	 * The Direction the Unit is facing.
	 */
	@Setter
	private Direction orientation = HunterKillerConstants.UNIT_DEFAULT_ORIENTATION;

	/**
	 * The range (in squares) of the Unit's Field of View.
	 */
	private int fieldOfViewRange = HunterKillerConstants.UNIT_DEFAULT_FOV_RANGE;

	/**
	 * The angle (in degrees) of the Unit's Field of View.
	 */
	private int fieldOfViewAngle = HunterKillerConstants.UNIT_DEFAULT_FOV_ANGLE;

	/**
	 * The range (in squares) of the Unit's attacks.
	 */
	private int attackRange = HunterKillerConstants.UNIT_DEFAULT_ATTACK_RANGE;

	/**
	 * The damage the Unit's attacks inflict.
	 */
	private int attackDamage = HunterKillerConstants.UNIT_DEFAULT_ATTACK_DAMAGE;

	/**
	 * The remaining cool down time (in ticks) of the Unit's special attack.
	 */
	private int specialAttackCooldown = HunterKillerConstants.UNIT_DEFAULT_SPECIAL_COOLDOWN;

	/**
	 * The cost to spawn an instance of this Unit.
	 */
	private int spawnCost = HunterKillerConstants.UNIT_DEFAULT_SPAWN_COST;

	/**
	 * The score this Unit is worth when defeated by the opposing team.
	 */
	private int scoreWorth = HunterKillerConstants.UNIT_DEFAULT_SCORE;

	/**
	 * The current field-of-view of this Unit.
	 */
	private HashSet<MapLocation> fieldOfView;

	/**
	 * Whether or not this Unit's field-of-view is currently valid. An invalid field-of-view implies that it should be
	 * updated before being accessed.
	 */
	private boolean fieldOfViewValid;

	// endregion

	// region Constructor

	/**
	 * Constructs a new instance of a Unit with default HP.
	 * 
	 * {@link Unit#Unit(int, UnitType, MapLocation, int, int, Direction, int, int, int, int, int, int, int)}
	 */
	public Unit(int spawningPlayerID, UnitType unitType, MapLocation mapLocation, Direction facing, int fovRange, int fovAngle,
				int attckRange, int attckDmg, int cooldown, int cost, int score) {
		this(spawningPlayerID, unitType, mapLocation, HunterKillerConstants.UNIT_DEFAULT_HP, facing, fovRange, fovAngle, attckRange,
				attckDmg, cooldown, cost, score);
	}

	/**
	 * Constructs a new instance of a Unit with full health.
	 * 
	 * {@link Unit#Unit(int, UnitType, MapLocation, int, int, Direction, int, int, int, int, int, int, int)}
	 */
	public Unit(int spawningPlayerID, UnitType unitType, MapLocation mapLocation, int maxHP, Direction facing, int fovRange, int fovAngle,
				int attckRange, int attckDmg, int cooldown, int cost, int score) {
		this(spawningPlayerID, unitType, mapLocation, maxHP, maxHP, facing, fovRange, fovAngle, attckRange, attckDmg, cooldown, cost, score);
	}

	/**
	 * Constructs a new instance of a Unit.
	 * 
	 * @param spawningPlayerID
	 *            The ID of the Player that spawned this Unit.
	 * @param unitType
	 *            The type of Unit.
	 * @param mapLocation
	 *            The Unit's location on the Map.
	 * @param maxHP
	 *            The Unit's maximum number of health points.
	 * @param currentHP
	 *            The Unit's current number of health points.
	 * @param facing
	 *            The Direction the Unit is facing.
	 * @param fovRange
	 *            The Unit's Field of View range.
	 * @param fovAngle
	 *            The Unit's Field of View angle.
	 * @param attckRange
	 *            The Unit's attack range.
	 * @param attckDmg
	 *            The Unit's attack damage.
	 * @param cooldown
	 *            The cooldown of the Unit's special attack.
	 * @param cost
	 *            The cost to spawn the Unit.
	 * @param score
	 *            The score the Unit is worth.
	 */
	public Unit(int spawningPlayerID, UnitType unitType, MapLocation mapLocation, int maxHP, int currentHP, Direction facing, int fovRange,
				int fovAngle, int attckRange, int attckDmg, int cooldown, int cost, int score) {
		super(mapLocation, maxHP, currentHP);
		controllingPlayerID = spawningPlayerID;
		type = unitType;
		orientation = facing;
		fieldOfViewRange = fovRange;
		fieldOfViewAngle = fovAngle;
		attackRange = attckRange;
		attackDamage = attckDmg;
		specialAttackCooldown = cooldown;
		spawnCost = cost;
		scoreWorth = score;

		fieldOfView = new HashSet<MapLocation>();
		fieldOfViewValid = false;
	}

	// endregion

	// region Protected methods

	/**
	 * Set the currently remaining cooldown of this Unit's special attack.
	 * 
	 * @param cooldownRemaining
	 *            The remaining cooldown.
	 */
	protected void setSpecialAttackCooldown(int cooldownRemaining) {
		this.specialAttackCooldown = cooldownRemaining;
	}

	// endregion

	// region Public methods

	public abstract void startCooldown();

	public abstract Unit copy();

	/**
	 * Reduces the cooldown for this unit's special attack.
	 */
	public void reduceCooldown() {
		// Don't reduce anything if already at 0
		if (specialAttackCooldown > 0)
			specialAttackCooldown--;
	}

	/**
	 * Whether or not a location is within this unit's field-of-view.
	 * 
	 * @param location
	 *            The location.
	 */
	public boolean isInFieldOfView(MapLocation location) {
		// Check internal fov representation
		return fieldOfView.contains(location);
	}

	/**
	 * Update the unit's internal field-of-view.
	 * 
	 * @param fieldOfView
	 *            The collection of {@link MapLocation}s that are currently in the unit's field-of-view
	 */
	public void updateFieldOfView(HashSet<MapLocation> fieldOfView) {
		this.fieldOfView = fieldOfView;
		fieldOfViewValid = true;
	}

	/**
	 * Invalidate this Unit's field-of-view. An invalid field-of-view implies that it should be updated before being
	 * accessed.
	 */
	public void invalidateFieldOfView() {
		fieldOfViewValid = false;
	}

	/**
	 * Returns the attack range for a Unit.
	 * 
	 * @param unitType
	 *            The type of Unit.
	 */
	public static int getAttackRange(UnitType unitType) {
		switch (unitType) {
		case Infected:
			return HunterKillerConstants.INFECTED_ATTACK_RANGE;
		case Medic:
			return HunterKillerConstants.MEDIC_ATTACK_RANGE;
		case Soldier:
			return HunterKillerConstants.SOLDIER_ATTACK_RANGE;
		default:
			return 0;
		}
	}

	/**
	 * Returns the spawn cost for a Unit.
	 * 
	 * @param unitType
	 *            The type of Unit.
	 */
	public static int getSpawnCost(UnitType unitType) {
		switch (unitType) {
		case Infected:
			return HunterKillerConstants.INFECTED_SPAWN_COST;
		case Medic:
			return HunterKillerConstants.MEDIC_SPAWN_COST;
		case Soldier:
			return HunterKillerConstants.SOLDIER_SPAWN_COST;
		default:
			return 0;
		}
	}

	/**
	 * Whether or not the specified location is within this unit's attack range.
	 */
	public boolean isWithinAttackRange(MapLocation location) {
		return MapLocation.getManhattanDist(this.getLocation(), location) <= getAttackRange();
	}

	/**
	 * Whether or not the specified location is within the specified range.
	 */
	public boolean isWithinRange(MapLocation location, int range) {
		return MapLocation.getManhattanDist(this.getLocation(), location) <= range;
	}

	/**
	 * Whether or not this unit can use it's special attack.
	 */
	public boolean canUseSpecialAttack() {
		return specialAttackCooldown == 0;
	}

	/**
	 * Returns an order to rotate this unit.
	 * 
	 * @param clockwise
	 *            Whether or not to rotate the unit clockwise.
	 */
	public UnitOrder rotate(boolean clockwise) {
		UnitOrderType type = clockwise ? UnitOrderType.ROTATE_CLOCKWISE : UnitOrderType.ROTATE_COUNTER_CLOCKWISE;
		return new UnitOrder(this, type);
	}

	/**
	 * Returns an order to rotate this unit.
	 * 
	 * @param rotation
	 *            The rotation to order for the unit.
	 */
	public UnitOrder rotate(Rotation rotation) {
		return rotate(rotation == Rotation.CLOCKWISE);
	}

	/**
	 * Returns an order to move this unit.
	 * 
	 * @param direction
	 *            The {@link Direction} to move the unit in.
	 * @param map
	 *            The {@link Map} the unit is on.
	 */
	public UnitOrder move(Direction direction, Map map) {
		// Get the location that is in the provided direction
		MapLocation targetLocation = map.getAdjacentLocationInDirection(getLocation(), direction);

		// Return the UnitOrder
		return new UnitOrder(this, UnitOrderType.MOVE, targetLocation);
	}

	/**
	 * Returns an order to move this unit to the given adjacent location.
	 * 
	 * @param adjacentLocation
	 *            The adjacent {@link MapLocation} to move the unit to.
	 * @param map
	 *            The {@link Map} the unit is on.
	 */
	public UnitOrder move(MapLocation adjacentLocation, Map map) {
		return new UnitOrder(this, UnitOrderType.MOVE, adjacentLocation);
	}

	/**
	 * Returns an order for this unit to attack a specific location on the map. Note: this method does not check whether
	 * or not the specified location is an existing location on the map.
	 * 
	 * @param targetLocation
	 *            The location to attack.
	 * @param useSpecialAttack
	 *            Whether or not to use the unit's special attack when attacking.
	 */
	public UnitOrder attack(MapLocation targetLocation, boolean useSpecialAttack) {
		return new UnitOrder(this, useSpecialAttack ? UnitOrderType.ATTACK_SPECIAL : UnitOrderType.ATTACK, targetLocation);
	}

	/**
	 * Returns an order for this unit to attack a specific unit on the map. Note: this method will return null if the
	 * specified unit cannot be found.
	 * 
	 * @param targetUnit
	 *            The unit to attack.
	 * @param map
	 *            The current map being played on.
	 * @param useSpecialAttack
	 *            Whether or not to use the unit's special attack when attacking.
	 */
	public UnitOrder attack(Unit targetUnit, Map map, boolean useSpecialAttack) {
		// Try to find the unit on the map
		MapLocation targetLocation = map.getObjectLocation(targetUnit.getID());
		// Check if we found anything
		if (targetLocation == null)
			return null;
		// If we did, return the order
		return new UnitOrder(this, useSpecialAttack ? UnitOrderType.ATTACK_SPECIAL : UnitOrderType.ATTACK, targetLocation);
	}

	// endregion

	// region Overridden methods

	@Override
	public boolean tick(HunterKillerState state) {
		return this.getHpCurrent() <= 0;
	}

	/**
	 * Whether or not this unit is being controlled by the specified player.
	 */
	@Override
	public boolean isControlledBy(Player player) {
		return this.controllingPlayerID == player.getID();
	}

	// endregion
}
