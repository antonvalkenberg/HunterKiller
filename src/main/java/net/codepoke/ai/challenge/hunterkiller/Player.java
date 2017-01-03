package net.codepoke.ai.challenge.hunterkiller;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import net.codepoke.ai.GameRules.Result.Ranking;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.mapfeature.Base;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Unit;

/**
 * Abstract class representing a player in HunterKiller. A player has a {@link Base} from which they
 * can spawn {@link Unit}s, payed for with a currency that is acquired automatically over time. When
 * a player can act during a turn, they create a {@link HunterKillerAction} that contains at most
 * one order per base or unit under their control.
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
@Getter
public class Player
		implements Comparable<Player> {

	// region Constants

	/**
	 * The amount of resources a player starts the game with.
	 */
	public static final int PLAYER_STARTING_RESOURCE = 10;

	// endregion

	// region Properties

	/**
	 * The player's name.
	 */
	private String name;

	/**
	 * The player's unique identifier.
	 */
	private int ID;

	/**
	 * The player's current resources.
	 */
	@Setter
	protected int resource;

	/**
	 * The {@link Base} that is assigned to this player.
	 */
	private Base base;

	/**
	 * Collection of {@link Unit}s that this player controls.
	 */
	protected List<Unit> squad;

	/**
	 * The score this player has accumulated during the game.
	 */
	private int score;

	// endregion

	// region Constructor

	/**
	 * Constructs a new player.
	 * 
	 * @param id
	 *            The player's unique identifier.
	 * @param name
	 *            The player's name.
	 */
	public Player(int id, String name) {
		this.ID = id;
		this.name = name;
		this.resource = PLAYER_STARTING_RESOURCE;
		// Create a new list to store the squad into
		squad = new ArrayList<Unit>();
	}

	// endregion

	// region Public methods

	/**
	 * Returns a deep copy of this player.
	 */
	public Player copy() {
		Player newPlayer = new Player(this.getID(), this.getName());

		// Copy the Base
		newPlayer.assignBase(this.base.copy());
		// Copy all the Units
		for (Unit unit : this.squad) {
			newPlayer.addUnitToSquad(unit.copy());
		}
		// Set the resources
		newPlayer.setResource(this.resource);

		return newPlayer;
	}

	// endregion

	// region Overridden methods

	/**
	 * Compares two players according to their scores. Zero means the two players have equal score. A
	 * negative number means this player has a higher score. A positive number means the other player
	 * has a higher score. This seems nonintuitive, but when we make our {@link Ranking}, we want the
	 * player with the highest score as the first in the collection (the lowest index).
	 */
	@Override
	public int compareTo(Player other) {
		if (this.score > other.score) {
			return -1;
		} else if (this.score == other.score) {
			return 0;
		} else {
			return 1;
		}
	}

	@Override
	public int hashCode() {
		return ID;
	}

	@Override
	public String toString() {
		return String.format("%s (ID: %d)", this.name, this.ID);
	}

	// endregion

	// region Protected methods

	/**
	 * Assign a {@link Base} to this player.
	 * 
	 * @param base
	 *            The base to assign.
	 */
	protected void assignBase(Base base) {
		this.base = base;
	}

	/**
	 * Awards a score to this player.
	 * 
	 * @param value
	 *            The value of the score awarded to this player.
	 */
	protected void awardScore(int value) {
		score += value;
	}

	/**
	 * Adds a Unit to this Player's squad.
	 * 
	 * @param unit
	 *            The unit to add.
	 */
	protected void addUnitToSquad(Unit unit) {
		squad.add(unit);
	}

	/**
	 * Removes a Unit from this Player's squad.
	 * 
	 * @param unit
	 *            The unit to remove.
	 */
	protected void removeUnitFromSquad(Unit unit) {
		squad.remove(unit);
	}

	// endregion

}
