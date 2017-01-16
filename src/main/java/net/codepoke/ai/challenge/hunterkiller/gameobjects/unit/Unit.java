package net.codepoke.ai.challenge.hunterkiller.gameobjects.unit;

import java.util.HashSet;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.codepoke.ai.challenge.hunterkiller.Constants;
import net.codepoke.ai.challenge.hunterkiller.HunterKillerState;
import net.codepoke.ai.challenge.hunterkiller.Map;
import net.codepoke.ai.challenge.hunterkiller.MapLocation;
import net.codepoke.ai.challenge.hunterkiller.enums.Direction;
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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Unit
		extends GameObject implements Controlled {

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
	private Direction orientation = Constants.UNIT_DEFAULT_ORIENTATION;

	/**
	 * The range (in squares) of the Unit's Field of View.
	 */
	private int fieldOfViewRange = Constants.UNIT_DEFAULT_FOV_RANGE;

	/**
	 * The angle (in degrees) of the Unit's Field of View.
	 */
	private int fieldOfViewAngle = Constants.UNIT_DEFAULT_FOV_ANGLE;

	/**
	 * The range (in squares) of the Unit's attacks.
	 */
	private int attackRange = Constants.UNIT_DEFAULT_ATTACK_RANGE;

	/**
	 * The damage the Unit's attacks inflict.
	 */
	private int attackDamage = Constants.UNIT_DEFAULT_ATTACK_DAMAGE;

	/**
	 * The remaining cool down time (in ticks) of the Unit's special attack.
	 */
	private int specialAttackCooldown = Constants.UNIT_DEFAULT_SPECIAL_COOLDOWN;

	/**
	 * The cost to spawn an instance of this Unit.
	 */
	private int spawnCost = Constants.UNIT_DEFAULT_SPAWN_COST;

	/**
	 * The score this Unit is worth when defeated by the opposing team.
	 */
	private int scoreWorth = Constants.UNIT_DEFAULT_SCORE;

	/**
	 * The current field-of-view of this Unit.
	 */
	private HashSet<MapLocation> fieldOfView;

	// endregion

	// region Constructor

	/**
	 * Constructs a new instance of a Unit with default HP.
	 * 
	 * {@link Unit#Unit(int, int, UnitType, MapLocation, int, int, Direction, int, int, int, int, int, int, int)}
	 */
	public Unit(int id, int spawningPlayerID, UnitType unitType, MapLocation mapLocation, Direction facing, int fovRange, int fovAngle,
				int attckRange, int attckDmg, int cooldown, int cost, int score) {
		this(id, spawningPlayerID, unitType, mapLocation, Constants.UNIT_DEFAULT_HP, facing, fovRange, fovAngle, attckRange, attckDmg,
				cooldown, cost, score);
	}

	/**
	 * Constructs a new instance of a Unit with full health.
	 * 
	 * {@link Unit#Unit(int, int, UnitType, MapLocation, int, int, Direction, int, int, int, int, int, int, int)}
	 */
	public Unit(int id, int spawningPlayerID, UnitType unitType, MapLocation mapLocation, int maxHP, Direction facing, int fovRange,
				int fovAngle, int attckRange, int attckDmg, int cooldown, int cost, int score) {
		this(id, spawningPlayerID, unitType, mapLocation, maxHP, maxHP, facing, fovRange, fovAngle, attckRange, attckDmg, cooldown, cost,
				score);
	}

	/**
	 * Constructs a new instance of a Unit.
	 * 
	 * @param id
	 *            The Unit's unique identifier.
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
	public Unit(int id, int spawningPlayerID, UnitType unitType, MapLocation mapLocation, int maxHP, int currentHP, Direction facing,
				int fovRange, int fovAngle, int attckRange, int attckDmg, int cooldown, int cost, int score) {
		super(id, mapLocation, maxHP, currentHP);
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
	 * @return
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
		this.fieldOfView = new HashSet<MapLocation>(fieldOfView.size());
		for (MapLocation location : fieldOfView) {
			this.fieldOfView.add(new MapLocation(location.getX(), location.getY()));
		}
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
			return Constants.INFECTED_ATTACK_RANGE;
		case Medic:
			return Constants.MEDIC_ATTACK_RANGE;
		case Soldier:
			return Constants.SOLDIER_ATTACK_RANGE;
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
			return Constants.INFECTED_SPAWN_COST;
		case Medic:
			return Constants.MEDIC_SPAWN_COST;
		case Soldier:
			return Constants.SOLDIER_SPAWN_COST;
		default:
			return 0;
		}
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
	 * Returns an order to move this unit.
	 * 
	 * @param direction
	 *            The {@link Direction} to move the unit in.
	 * @param map
	 *            The {@link Map} the unit is on.
	 */
	public UnitOrder move(Direction direction, Map map) {
		UnitOrderType type;
		// Determine the correct order-type for the direction
		switch (direction) {
		case EAST:
			type = UnitOrderType.MOVE_EAST;
			break;
		case NORTH:
			type = UnitOrderType.MOVE_NORTH;
			break;
		case SOUTH:
			type = UnitOrderType.MOVE_SOUTH;
			break;
		case WEST:
			type = UnitOrderType.MOVE_WEST;
			break;
		default:
			System.err.println(String.format("WARNING: Unsupported movement direction: %s", direction));
			return null;
		}

		// Get the location that is in the provided direction
		MapLocation targetLocation = map.getAdjacentLocationInDirection(getLocation(), direction);

		// Return the UnitOrder
		return new UnitOrder(this, type, targetLocation);
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

	// endregion
}
