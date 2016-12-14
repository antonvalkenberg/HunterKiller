package main.java.net.codepoke.ai.challenge.hunterkiller.gameobjects;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import main.java.net.codepoke.ai.challenge.hunterkiller.enums.Direction;

/**
 * Class representing a Soldier unit in the game.
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class Soldier extends Unit {

	//region Constants

	/**
	 * Health points for a Soldier.
	 */
	public static final int SOLDIER_MAX_HP = 5;
	/**
	 * Field of View range for a Soldier.
	 */
	public static final int SOLDIER_FOV_RANGE = 3;
	/**
	 * Field of View angle for a Soldier.
	 */
	public static final int SOLDIER_FOV_ANGLE = 90;
	/**
	 * Attack range for a Soldier.
	 */
	public static final int SOLDIER_ATTACK_RANGE = 4;
	/**
	 * Attack damage for a Soldier.
	 */
	public static final int SOLDIER_ATTACK_DAMAGE = 3;
	/**
	 * Cooldown of a Soldier's special attack
	 */
	public static final int SOLDIER_COOLDOWN = 20;
	/**
	 * Spawn cost for a Soldier.
	 */
	public static final int SOLDIER_SPAWN_COST = 5;
	/**
	 * The amount of points a Soldier is worth when defeated.
	 */
	public static final int SOLDIER_SCORE = 25;
	
	//endregion
	
	//region Constructor
	
	/**
	 * Constructs a new instance of a Soldier on the specified location on the Map, facing the specified direction.
	 * @param mapPosition The Soldier's position on the map.
	 * @param facing The Direction the Soldier is facing.
	 */
	public Soldier(int mapPosition, Direction facing) {
		this(mapPosition, SOLDIER_MAX_HP, SOLDIER_MAX_HP, facing, SOLDIER_FOV_RANGE, SOLDIER_FOV_ANGLE, SOLDIER_ATTACK_RANGE, SOLDIER_ATTACK_DAMAGE, SOLDIER_COOLDOWN, SOLDIER_SPAWN_COST, SOLDIER_SCORE);
	}
	
	/**
	 * Constructs a new instance of Soldier.
	 * @param mapPosition The Soldier's position on the map.
	 * @param maxHP The maximum amount of health points the Soldier has.
	 * @param currentHP The current amount of health points the Soldier has.
	 * @param facing The Direction the Soldier is facing.
	 * @param fovRange The range (in squares) of the Soldier's Field of View.
	 * @param fovAngle The angle (in degrees) of the Soldier's Field of View.
	 * @param attckRange The range (in squares) of the Soldier's attacks.
	 * @param attckDmg The damage the Soldier's attacks inflict.
	 * @param cooldown he remaining cool down time (in ticks) of the Soldier's special attack.
	 * @param cost The cost to spawn a Soldier.
	 * @param score The score the Soldier is worth when defeated
	 */
	public Soldier(int mapPosition, int maxHP, int currentHP, Direction facing, int fovRange, int fovAngle, int attckRange, int attckDmg, int cooldown, int cost, int score) {
		super(mapPosition, maxHP, currentHP, facing, fovRange, fovAngle, attckRange, attckDmg, cooldown, cost, score);
	}

	//endregion

	@Override
	public GameObject copy() {
		return new Soldier(position, hpMax, hpCurrent, orientation, fieldOfViewRange, fieldOfViewAngle, attackRange, attackDamage, specialAttackCooldown, spawnCost, scoreWorth);
	}

}
