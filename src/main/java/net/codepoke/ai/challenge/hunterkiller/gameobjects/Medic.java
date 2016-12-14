package main.java.net.codepoke.ai.challenge.hunterkiller.gameobjects;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import main.java.net.codepoke.ai.challenge.hunterkiller.enums.Direction;

/**
 * Class representing a Medic unit in the game.
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class Medic extends Unit {

	//region Constants
	
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
	
	//endregion

	//region Constructor
	
	/**
	 * Constructs a new instance of a Medic on the specified location on the Map, facing the specified direction.
	 * @param mapPosition The Medic's position on the map.
	 * @param facing The Direction the Medic is facing.
	 */
	public Medic(int mapPosition, Direction facing) {
		this(mapPosition, MEDIC_MAX_HP, MEDIC_MAX_HP, facing, MEDIC_FOV_RANGE, MEDIC_FOV_ANGLE, MEDIC_ATTACK_RANGE, MEDIC_ATTACK_DAMAGE, MEDIC_COOLDOWN, MEDIC_SPAWN_COST, MEDIC_SCORE);
	}
	
	/**
	 * Constructs a new instance of Medic.
	 * @param mapPosition The Medic's position on the map.
	 * @param maxHP The maximum amount of health points the Medic has.
	 * @param currentHP The current amount of health points the Medic has.
	 * @param facing The Direction the Medic is facing.
	 * @param fovRange The range (in squares) of the Medic's Field of View.
	 * @param fovAngle The angle (in degrees) of the Medic's Field of View.
	 * @param attckRange The range (in squares) of the Medic's attacks.
	 * @param attckDmg The damage the Medic's attacks inflict.
	 * @param cooldown he remaining cool down time (in ticks) of the Medic's special attack.
	 * @param cost The cost to spawn a Medic.
	 * @param score The score the Medic is worth when defeated
	 */
	public Medic(int mapPosition, int maxHP, int currentHP, Direction facing, int fovRange, int fovAngle, int attckRange, int attckDmg, int cooldown, int cost, int score) {
		super(mapPosition, maxHP, currentHP, facing, fovRange, fovAngle, attckRange, attckDmg, cooldown, cost, score);
	}

	//endregion

	@Override
	public GameObject copy() {
		return new Medic(position, hpMax, hpCurrent, orientation, fieldOfViewRange, fieldOfViewAngle, attackRange, attackDamage, specialAttackCooldown, spawnCost, scoreWorth);
	}

}
