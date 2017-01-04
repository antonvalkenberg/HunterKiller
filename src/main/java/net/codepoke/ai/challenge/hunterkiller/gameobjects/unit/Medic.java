package net.codepoke.ai.challenge.hunterkiller.gameobjects.unit;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.codepoke.ai.challenge.hunterkiller.MapLocation;
import net.codepoke.ai.challenge.hunterkiller.enums.Direction;
import net.codepoke.ai.challenge.hunterkiller.enums.TileType;

/**
 * Class representing a Medic unit in HunterKiller.
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Medic
		extends Unit {

	// region Constants

	/**
	 * Health points for a Medic.
	 */
	public static final int MEDIC_MAX_HP = 2;
	/**
	 * Field of View range for a Medic.
	 */
	public static final int MEDIC_FOV_RANGE = 3;
	/**
	 * Field of View angle for a Medic.
	 */
	public static final int MEDIC_FOV_ANGLE = 90;
	/**
	 * Attack range for a Medic.
	 */
	public static final int MEDIC_ATTACK_RANGE = 4;
	/**
	 * Attack damage for a Medic.
	 */
	public static final int MEDIC_ATTACK_DAMAGE = 1;
	/**
	 * Cooldown of a Medic's special attack.
	 */
	public static final int MEDIC_COOLDOWN = 2;
	/**
	 * Spawn cost for a Medic.
	 */
	public static final int MEDIC_SPAWN_COST = 10;
	/**
	 * The amount of points a Medic is worth when defeated.
	 */
	public static final int MEDIC_SCORE = 50;
	/**
	 * The amount of health points a Medic's special attack heals.
	 */
	public static final int MEDIC_SPECIAL_HEAL = 2;

	// endregion

	// region Constructor

	/**
	 * Constructs a new instance of a Medic on the specified location on the Map, facing the specified
	 * direction.
	 * 
	 * {@link Medic#Medic(int, int, MapLocation, int, int, Direction, int, int, int, int, int, int, int)}
	 */
	public Medic(int id, int spawningPlayerID, MapLocation mapLocation, Direction facing) {
		this(id, spawningPlayerID, mapLocation, MEDIC_MAX_HP, MEDIC_MAX_HP, facing, MEDIC_FOV_RANGE, MEDIC_FOV_ANGLE, MEDIC_ATTACK_RANGE,
				MEDIC_ATTACK_DAMAGE, MEDIC_COOLDOWN, MEDIC_SPAWN_COST, MEDIC_SCORE);
	}

	/**
	 * Constructs a new instance of Medic.
	 * 
	 * @param id
	 *            The Medic's unique identifier.
	 * @param spawningPlayerID
	 *            The ID of the Player that spawned this Medic.
	 * @param mapLocation
	 *            The Medic's location on the map.
	 * @param maxHP
	 *            The maximum amount of health points the Medic has.
	 * @param currentHP
	 *            The current amount of health points the Medic has.
	 * @param facing
	 *            The Direction the Medic is facing.
	 * @param fovRange
	 *            The range (in squares) of the Medic's Field of View.
	 * @param fovAngle
	 *            The angle (in degrees) of the Medic's Field of View.
	 * @param attckRange
	 *            The range (in squares) of the Medic's attacks.
	 * @param attckDmg
	 *            The damage the Medic's attacks inflict.
	 * @param cooldown
	 *            he remaining cool down time (in ticks) of the Medic's special attack.
	 * @param cost
	 *            The cost to spawn a Medic.
	 * @param score
	 *            The score the Medic is worth when defeated
	 */
	public Medic(int id, int spawningPlayerID, MapLocation mapLocation, int maxHP, int currentHP, Direction facing, int fovRange,
					int fovAngle, int attckRange, int attckDmg, int cooldown, int cost, int score) {
		super(id, spawningPlayerID, mapLocation, maxHP, currentHP, facing, fovRange, fovAngle, attckRange, attckDmg, cooldown, cost, score);
	}

	// endregion

	// region Overridden methods

	@Override
	public Medic copy(int id) {
		Medic newM = new Medic(id, this.getSquadPlayerID(), this.getLocation(), this.getHpMax(), this.getHpCurrent(),
								this.getOrientation(), this.getFieldOfViewRange(), this.getFieldOfViewAngle(), this.getAttackRange(),
								this.getAttackDamage(), this.getSpecialAttackCooldown(), this.getSpawnCost(), this.getScoreWorth());
		newM.updateFieldOfView(this.getFieldOfView());
		return newM;
	}

	@Override
	public Medic copy() {
		return this.copy(this.getID());
	}

	/**
	 * Start the cooldown of the medic's special attack.
	 */
	@Override
	public void startCooldown() {
		this.setSpecialAttackCooldown(MEDIC_COOLDOWN);
	}

	@Override
	public String toString() {
		return "" + TileType.MEDIC.txt;
	}

	// endregion

}
