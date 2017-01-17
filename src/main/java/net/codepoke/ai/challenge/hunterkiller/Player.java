package net.codepoke.ai.challenge.hunterkiller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.codepoke.ai.GameRules.Result.Ranking;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.mapfeature.Base;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Unit;

import com.badlogic.gdx.utils.IntArray;

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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Player
		implements Comparable<Player> {

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
	 * The player's section on the map. This is used in map creation and can be safely ignored.
	 */
	private int mapSection = -1;

	/**
	 * The player's current resources.
	 */
	@Setter
	protected int resource;

	/**
	 * The ID of the {@link Base} that is assigned to this player.
	 */
	private int baseID;

	/**
	 * Whether or not this Player's base exists.
	 */
	@Setter
	private boolean baseExists = false;;

	/**
	 * Collection of IDs of {@link Unit}s that this player controls.
	 */
	protected IntArray unitIDs;

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
	 * @param mapSection
	 *            The section assigned to this player during map construction.
	 */
	public Player(int id, String name, int mapSection) {
		this.ID = id;
		this.name = name;
		this.mapSection = mapSection;
		this.resource = Constants.PLAYER_STARTING_RESOURCE;
		// Create a new list to store the units into
		unitIDs = new IntArray();
	}

	// endregion

	// region Public methods

	/**
	 * Returns a deep copy of this player.
	 */
	public Player copy() {
		Player newPlayer = new Player(this.getID(), this.getName(), this.getMapSection());

		// Copy the Base's ID
		newPlayer.assignBase(this.baseID);
		// Copy all the Units
		for (int i = 0; i < unitIDs.size; i++) {
			newPlayer.addUnit(unitIDs.get(i));
		}
		// Set the resources
		newPlayer.setResource(this.resource);
		// Set the score
		newPlayer.setScore(this.score);
		// Set base status
		newPlayer.setBaseExists(this.baseExists);

		return newPlayer;
	}

	/**
	 * Returns the combined Field-of-View of the player, in a given state. This Field-of-View is made up of the union of
	 * all it's Unit's Field-of-View, and it's Base (if that exists).
	 * 
	 * @param map
	 *            The {@link Map} to get the field-of-view from.
	 */
	public HashSet<MapLocation> getCombinedFieldOfView(Map map) {
		HashSet<MapLocation> fieldOfViewSet = new HashSet<MapLocation>();
		// Start with the Base's field of view
		if (baseExists) {
			Base base = (Base) map.getObject(baseID);
			fieldOfViewSet.addAll(map.getAreaAround(base.getLocation(), true));
		}
		// Go through our squad
		for (int i = 0; i < unitIDs.size; i++) {
			// Get the Unit from the map
			Unit unit = (Unit) map.getObject(unitIDs.get(i));
			// Check if the unit isn't dead
			if (unit.getHpCurrent() > 0) {
				fieldOfViewSet.addAll(unit.getFieldOfView());
			}
		}
		// Return the collection
		return fieldOfViewSet;
	}

	/**
	 * Returns this player's {@link Base} on the provided {@link Map}. Note: this method returns null when a Player's
	 * base no longer exists.
	 * 
	 * @param map
	 *            The map to get the base from.
	 */
	public Base getBase(Map map) {
		// Check if our base exists
		if (baseExists)
			return (Base) map.getObject(baseID);
		else
			return null;
	}

	/**
	 * Returns a collection of {@link Unit}s that this player is currently controlling on the provided {@link Map}.
	 * 
	 * @param map
	 *            The map to get the units from.
	 */
	public List<Unit> getUnits(Map map) {
		List<Unit> units = new ArrayList<Unit>();
		for (int i = 0; i < unitIDs.size; i++) {
			units.add((Unit) map.getObject(unitIDs.get(i)));
		}
		return units;
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
	 * @param baseID
	 *            The ID of the base to assign.
	 */
	protected void assignBase(int baseID) {
		this.baseID = baseID;
		baseExists = true;
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
	 * Adds a Unit to this Player's control.
	 * 
	 * @param unitID
	 *            The ID of the unit to add.
	 */
	protected void addUnit(int unitID) {
		unitIDs.add(unitID);
	}

	/**
	 * Removes a Unit from this Player's control.
	 * 
	 * @param unitID
	 *            The ID of the unit to remove.
	 */
	protected void removeUnit(int unitID) {
		unitIDs.removeValue(unitID);
	}

	/**
	 * Inform this Player that it's Base has been destroyed.
	 * 
	 * @param baseID
	 *            The unique identifier of the {@link Base} that was destroyed.
	 */
	protected void informBaseDestroyed(int baseID) {
		// Check if the IDs match
		if (this.baseID == baseID)
			baseExists = false;
		// Otherwise just ignore
	}

	// endregion

	// region Private methods

	/**
	 * Set this player's score
	 * 
	 * @param score
	 *            The score to set.
	 */
	private void setScore(int score) {
		this.score = score;
	}

	// endregion

}
