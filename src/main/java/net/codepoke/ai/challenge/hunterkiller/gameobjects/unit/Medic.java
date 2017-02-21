package net.codepoke.ai.challenge.hunterkiller.gameobjects.unit;

import lombok.NoArgsConstructor;
import net.codepoke.ai.challenge.hunterkiller.HunterKillerConstants;
import net.codepoke.ai.challenge.hunterkiller.MapLocation;
import net.codepoke.ai.challenge.hunterkiller.enums.Direction;
import net.codepoke.ai.challenge.hunterkiller.enums.TileType;
import net.codepoke.ai.challenge.hunterkiller.enums.UnitType;

/**
 * Class representing a Medic unit in HunterKiller.
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
@NoArgsConstructor
public class Medic
		extends Unit {

	// region Constructor

	/**
	 * Constructs a new instance of a Medic on the specified location on the Map, facing the specified
	 * direction.
	 * 
	 * {@link Medic#Medic(int, MapLocation, int, int, Direction, int, int, int, int, int, int, int)}
	 */
	public Medic(int spawningPlayerID, MapLocation mapLocation, Direction facing) {
		this(spawningPlayerID, mapLocation, HunterKillerConstants.MEDIC_MAX_HP, HunterKillerConstants.MEDIC_MAX_HP, facing,
				HunterKillerConstants.MEDIC_FOV_RANGE, HunterKillerConstants.MEDIC_FOV_ANGLE, HunterKillerConstants.MEDIC_ATTACK_RANGE,
				HunterKillerConstants.MEDIC_ATTACK_DAMAGE, 0, HunterKillerConstants.MEDIC_SPAWN_COST, HunterKillerConstants.MEDIC_SCORE);
	}

	/**
	 * Constructs a new instance of Medic.
	 * 
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
	public Medic(int spawningPlayerID, MapLocation mapLocation, int maxHP, int currentHP, Direction facing, int fovRange, int fovAngle,
					int attckRange, int attckDmg, int cooldown, int cost, int score) {
		super(spawningPlayerID, UnitType.Medic, mapLocation, maxHP, currentHP, facing, fovRange, fovAngle, attckRange, attckDmg, cooldown,
				cost, score);
	}

	// endregion

	// region Overridden methods

	@Override
	public Medic copy() {
		Medic newM = new Medic(this.getControllingPlayerID(), this.getLocation(), this.getHpMax(), this.getHpCurrent(),
								this.getOrientation(), this.getFieldOfViewRange(), this.getFieldOfViewAngle(), this.getAttackRange(),
								this.getAttackDamage(), this.getSpecialAttackCooldown(), this.getSpawnCost(), this.getScoreWorth());
		newM.setID(this.getID());
		newM.updateFieldOfView(this.getFieldOfView());
		return newM;
	}

	/**
	 * Start the cooldown of the medic's special attack.
	 */
	@Override
	public void startCooldown() {
		this.setSpecialAttackCooldown(HunterKillerConstants.MEDIC_COOLDOWN);
	}

	@Override
	public String toString() {
		return "" + TileType.MEDIC.txt;
	}

	// endregion

}
