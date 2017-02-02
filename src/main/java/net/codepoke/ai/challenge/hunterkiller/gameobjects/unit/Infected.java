package net.codepoke.ai.challenge.hunterkiller.gameobjects.unit;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.codepoke.ai.challenge.hunterkiller.HunterKillerConstants;
import net.codepoke.ai.challenge.hunterkiller.MapLocation;
import net.codepoke.ai.challenge.hunterkiller.enums.Direction;
import net.codepoke.ai.challenge.hunterkiller.enums.TileType;
import net.codepoke.ai.challenge.hunterkiller.enums.UnitType;

/**
 * Class representing an Infected unit in HunterKiller.
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Infected
		extends Unit {

	// region Constructor

	/**
	 * Constructs a new instance of an Infected on the specified location on the Map, facing the
	 * specified direction.
	 * 
	 * {@link Infected#Infected(int, MapLocation, int, int, Direction, int, int, int, int, int, int, int)}
	 */
	public Infected(int spawningPlayerID, MapLocation mapLocation, Direction facing) {
		this(spawningPlayerID, mapLocation, HunterKillerConstants.INFECTED_MAX_HP, HunterKillerConstants.INFECTED_MAX_HP, facing, HunterKillerConstants.INFECTED_FOV_RANGE,
				HunterKillerConstants.INFECTED_FOV_ANGLE, HunterKillerConstants.INFECTED_ATTACK_RANGE, HunterKillerConstants.INFECTED_ATTACK_DAMAGE, 0,
				HunterKillerConstants.INFECTED_SPAWN_COST, HunterKillerConstants.INFECTED_SCORE);
	}

	/**
	 * Constructs a new instance of Infected.
	 * 
	 * @param spawningPlayerID
	 *            The ID of the Player that spawned this Infected.
	 * @param mapLocation
	 *            The Infected's location on the map.
	 * @param maxHP
	 *            The maximum amount of health points the Infected has.
	 * @param currentHP
	 *            The current amount of health points the Infected has.
	 * @param facing
	 *            The Direction the Infected is facing.
	 * @param fovRange
	 *            The range (in squares) of the Infected's Field of View.
	 * @param fovAngle
	 *            The angle (in degrees) of the Infected's Field of View.
	 * @param attckRange
	 *            The range (in squares) of the Infected's attacks.
	 * @param attckDmg
	 *            The damage the Infected's attacks inflict.
	 * @param cooldown
	 *            he remaining cool down time (in ticks) of the Infected's special attack.
	 * @param cost
	 *            The cost to spawn a Infected.
	 * @param score
	 *            The score the Infected is worth when defeated
	 */
	public Infected(int spawningPlayerID, MapLocation mapLocation, int maxHP, int currentHP, Direction facing, int fovRange, int fovAngle,
					int attckRange, int attckDmg, int cooldown, int cost, int score) {
		super(spawningPlayerID, UnitType.Infected, mapLocation, maxHP, currentHP, facing, fovRange, fovAngle, attckRange, attckDmg,
				cooldown, cost, score);
	}

	// endregion

	// region Overridden methods

	@Override
	public Infected copy() {
		Infected newI = new Infected(this.getControllingPlayerID(), this.getLocation(), this.getHpMax(), this.getHpCurrent(),
										this.getOrientation(), this.getFieldOfViewRange(), this.getFieldOfViewAngle(),
										this.getAttackRange(), this.getAttackDamage(), this.getSpecialAttackCooldown(),
										this.getSpawnCost(), this.getScoreWorth());
		newI.setID(this.getID());
		newI.updateFieldOfView(this.getFieldOfView());
		return newI;
	}

	/**
	 * Start the cooldown of the infected's special attack.
	 */
	@Override
	public void startCooldown() {
		this.setSpecialAttackCooldown(HunterKillerConstants.INFECTED_COOLDOWN);
	}

	@Override
	public String toString() {
		return "" + TileType.INFECTED.txt;
	}

	// endregion

}
