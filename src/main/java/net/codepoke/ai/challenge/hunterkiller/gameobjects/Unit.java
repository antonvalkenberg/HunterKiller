package main.java.net.codepoke.ai.challenge.hunterkiller.gameobjects;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import main.java.net.codepoke.ai.challenge.hunterkiller.enums.Direction;

/**
 * Abstract class representing a Unit in the game.
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
@Setter
@Getter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public abstract class Unit extends GameObject {

	//region Constants
	
	/**
	 * Default health points for a Unit.
	 */
	public static final int DEFAULT_UNIT_HP = 5;
	
	/**
	 * Default orientation for a Unit.
	 */
	public static final Direction DEFAULT_ORIENTATION = Direction.NORTH;
	
	/**
	 * Default Field of View range for a Unit.
	 */
	public static final int DEFAULT_FOV_RANGE = 3;
	
	/**
	 * Default Field of View angle for a Unit.
	 */
	public static final int DEFAULT_FOV_ANGLE = 90;
	
	/**
	 * Default attack range for a Unit.
	 */
	public static final int DEFAULT_ATTACK_RANGE = 4;
	
	/**
	 * Default attack damage for a Unit.
	 */
	public static final int DEFAULT_ATTACK_DAMAGE = 3;
	
	/**
	 * Default cooldown for a Unit's special attack.
	 */
	public static final int DEFAULT_SPECIAL_COOLDOWN = 5;
	
	/**
	 * Default spawn cost for a Unit.
	 */
	public static final int DEFAULT_SPAWN_COST = 5;
	
	/**
	 * Default score that a Unit is worth.
	 */
	public static final int DEFAULT_SCORE = 25;

	//endregion
	
	//region Properties

	/**
	 * The Direction the Unit is facing.
	 */
	public Direction orientation = DEFAULT_ORIENTATION;
	
	/**
	 * The range (in squares) of the Unit's Field of View.
	 */
	public int fieldOfViewRange = DEFAULT_FOV_RANGE;
	
	/**
	 * The angle (in degrees) of the Unit's Field of View.
	 */
	public int fieldOfViewAngle = DEFAULT_FOV_ANGLE;
	
	/**
	 * The range (in squares) of the Unit's attacks.
	 */
	public int attackRange = DEFAULT_ATTACK_RANGE;
	
	/**
	 * The damage the Unit's attacks inflict.
	 */
	public int attackDamage = DEFAULT_ATTACK_DAMAGE;
	
	/**
	 * The remaining cool down time (in ticks) of the Unit's special attack.
	 */
	public int specialAttackCooldown = DEFAULT_SPECIAL_COOLDOWN;
	
	/**
	 * The cost to spawn an instance of this Unit.
	 */
	public int spawnCost = DEFAULT_SPAWN_COST;
	
	/**
	 * The score this Unit is worth when defeated by the opposing team.
	 */
	public int scoreWorth = DEFAULT_SCORE;

	//endregion
	
	//region Constructor

	/**
	 * Constructs a new instance of a Unit with default HP.
	 * @param mapPosition The Unit's position on the Map.
	 * @param facing The Direction the Unit is facing.
	 * @param fovRange The Unit's Field of View range.
	 * @param fovAngle The Unit's Field of View angle.
	 * @param attckRange The Unit's attack range.
	 * @param attckDmg The Unit's attack damage.
	 * @param cooldown The cooldown of the Unit's special attack.
	 * @param cost The cost to spawn the Unit.
	 * @param score The score the Unit is worth.
	 */
	public Unit(int mapPosition, Direction facing, int fovRange, int fovAngle, int attckRange, int attckDmg, int cooldown, int cost, int score) {
		this(mapPosition, DEFAULT_UNIT_HP, facing, fovRange, fovAngle, attckRange, attckDmg, cooldown, cost, score);
	}
	
	/**
	 * Constructs a new instance of a Unit.
	 * @param mapPosition The Unit's position on the Map.
	 * @param maxHP The Unit's maximum number of health points.
	 * @param facing The Direction the Unit is facing.
	 * @param fovRange The Unit's Field of View range.
	 * @param fovAngle The Unit's Field of View angle.
	 * @param attckRange The Unit's attack range.
	 * @param attckDmg The Unit's attack damage.
	 * @param cooldown The cooldown of the Unit's special attack.
	 * @param cost The cost to spawn the Unit.
	 * @param score The score the Unit is worth.
	 */
	public Unit(int mapPosition, int maxHP, Direction facing, int fovRange, int fovAngle, int attckRange, int attckDmg, int cooldown, int cost, int score) {
		this(mapPosition, maxHP, maxHP, facing, fovRange, fovAngle, attckRange, attckDmg, cooldown, cost, score);
	}

	/**
	 * Constructs a new instance of a Unit.
	 * @param mapPosition The Unit's position on the Map.
	 * @param maxHP The Unit's maximum number of health points.
	 * @param currentHP The Unit's current number of health points.
	 * @param facing The Direction the Unit is facing.
	 * @param fovRange The Unit's Field of View range.
	 * @param fovAngle The Unit's Field of View angle.
	 * @param attckRange The Unit's attack range.
	 * @param attckDmg The Unit's attack damage.
	 * @param cooldown The cooldown of the Unit's special attack.
	 * @param cost The cost to spawn the Unit.
	 * @param score The score the Unit is worth.
	 */
	public Unit(int mapPosition, int maxHP, int currentHP, Direction facing, int fovRange, int fovAngle, int attckRange, int attckDmg, int cooldown, int cost, int score) {
		super(mapPosition, maxHP, currentHP);
		orientation = facing;
		fieldOfViewRange = fovRange;
		fieldOfViewAngle = fovAngle;
		attackRange = attckRange;
		attackDamage = attckDmg;
		specialAttackCooldown = cooldown;
		spawnCost = cost;
		scoreWorth = score;
	}
	
	//endregion
	
}
