package net.codepoke.ai.challenge.hunterkiller.gameobjects.unit;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.codepoke.ai.challenge.hunterkiller.Constants;
import net.codepoke.ai.challenge.hunterkiller.MapLocation;
import net.codepoke.ai.challenge.hunterkiller.enums.Direction;
import net.codepoke.ai.challenge.hunterkiller.enums.TileType;
import net.codepoke.ai.challenge.hunterkiller.enums.UnitType;

/**
 * Class representing a Soldier unit in HunterKiller.
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Soldier
		extends Unit {

	// region Constructor

	/**
	 * Constructs a new instance of a Soldier on the specified location on the Map, facing the
	 * specified direction.
	 * 
	 * {@link Soldier#Soldier(int, MapLocation, int, int, Direction, int, int, int, int, int, int, int)}
	 */
	public Soldier(int spawningPlayerID, MapLocation mapLocation, Direction facing) {
		this(spawningPlayerID, mapLocation, Constants.SOLDIER_MAX_HP, Constants.SOLDIER_MAX_HP, facing, Constants.SOLDIER_FOV_RANGE,
				Constants.SOLDIER_FOV_ANGLE, Constants.SOLDIER_ATTACK_RANGE, Constants.SOLDIER_ATTACK_DAMAGE, 0,
				Constants.SOLDIER_SPAWN_COST, Constants.SOLDIER_SCORE);
	}

	/**
	 * Constructs a new instance of Soldier.
	 * 
	 * @param spawningPlayerID
	 *            The ID of the Player that spawned this Soldier.
	 * @param mapLocation
	 *            The Soldier's location on the map.
	 * @param maxHP
	 *            The maximum amount of health points the Soldier has.
	 * @param currentHP
	 *            The current amount of health points the Soldier has.
	 * @param facing
	 *            The Direction the Soldier is facing.
	 * @param fovRange
	 *            The range (in squares) of the Soldier's Field of View.
	 * @param fovAngle
	 *            The angle (in degrees) of the Soldier's Field of View.
	 * @param attckRange
	 *            The range (in squares) of the Soldier's attacks.
	 * @param attckDmg
	 *            The damage the Soldier's attacks inflict.
	 * @param cooldown
	 *            he remaining cool down time (in ticks) of the Soldier's special attack.
	 * @param cost
	 *            The cost to spawn a Soldier.
	 * @param score
	 *            The score the Soldier is worth when defeated
	 */
	public Soldier(int spawningPlayerID, MapLocation mapLocation, int maxHP, int currentHP, Direction facing, int fovRange, int fovAngle,
					int attckRange, int attckDmg, int cooldown, int cost, int score) {
		super(spawningPlayerID, UnitType.Soldier, mapLocation, maxHP, currentHP, facing, fovRange, fovAngle, attckRange, attckDmg,
				cooldown, cost, score);
	}

	// endregion

	// region Overridden methods

	@Override
	public Soldier copy() {
		Soldier newS = new Soldier(this.getControllingPlayerID(), this.getLocation(), this.getHpMax(), this.getHpCurrent(),
									this.getOrientation(), this.getFieldOfViewRange(), this.getFieldOfViewAngle(), this.getAttackRange(),
									this.getAttackDamage(), this.getSpecialAttackCooldown(), this.getSpawnCost(), this.getScoreWorth());
		newS.setID(this.getID());
		newS.updateFieldOfView(this.getFieldOfView());
		return newS;
	}

	/**
	 * Start the cooldown of the soldier's special attack.
	 */
	@Override
	public void startCooldown() {
		this.setSpecialAttackCooldown(Constants.SOLDIER_COOLDOWN);
	}

	@Override
	public String toString() {
		return "" + TileType.SOLDIER.txt;
	}

	// endregion

}
