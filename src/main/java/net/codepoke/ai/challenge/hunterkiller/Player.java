package net.codepoke.ai.challenge.hunterkiller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.codepoke.ai.GameRules.Result.Ranking;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.mapfeature.Structure;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Unit;

import com.badlogic.gdx.utils.IntArray;

/**
 * Abstract class representing a player in HunterKiller. A player has a {@link Structure} from which they
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
	 * The ID of the {@link Structure} that is this player's command center.
	 */
	private int commandCenterID = -1;

	/**
	 * Collection of IDs of {@link Structure}s that this player controls.
	 */
	private IntArray structureIDs;

	/**
	 * Collection of IDs of {@link Unit}s that this player controls.
	 */
	private IntArray unitIDs;

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
	 * @param startingResources
	 *            The amount of resources a player starts the game with.
	 */
	public Player(int id, String name, int mapSection, int startingResources) {
		this.ID = id;
		this.name = name;
		this.mapSection = mapSection;
		this.resource = startingResources;
		// Create a new list to store the units into (unordered, educated guess on initial capacity)
		unitIDs = new IntArray(false, 10);
		structureIDs = new IntArray(false, 3);
	}

	/**
	 * Constructs a new player with a default amount of resources.
	 * 
	 * {@link Player#Player(int, String, int, int)}
	 */
	public Player(int id, String name, int mapSection) {
		this(id, name, mapSection, Constants.PLAYER_STARTING_RESOURCE);
	}

	// endregion

	// region Public methods

	/**
	 * Returns a deep copy of this player.
	 */
	public Player copy() {
		Player newPlayer = new Player(this.getID(), this.getName(), this.getMapSection());

		// Copy the command center's ID
		newPlayer.commandCenterID = this.commandCenterID;
		// Copy all the Structures
		newPlayer.structureIDs.addAll(this.structureIDs);
		// Copy all the Units
		newPlayer.unitIDs.addAll(this.unitIDs);
		// Set the resources
		newPlayer.setResource(this.resource);
		// Set the score
		newPlayer.setScore(this.score);

		return newPlayer;
	}

	/**
	 * Returns the combined Field-of-View of the player, in a given state. This Field-of-View is made up of the union of
	 * all it's Unit's Field-of-View, and it's Structures.
	 * 
	 * @param map
	 *            The {@link Map} to get the field-of-view from.
	 */
	public HashSet<MapLocation> getCombinedFieldOfView(Map map) {
		HashSet<MapLocation> fieldOfViewSet = new HashSet<MapLocation>();
		// Start with the Structure field of view
		for (int i = 0; i < structureIDs.size; i++) {
			Structure struct = (Structure) map.getObject(structureIDs.get(i));
			fieldOfViewSet.addAll(map.getFieldOfView(struct));
		}
		// Go through our units
		for (int i = 0; i < unitIDs.size; i++) {
			// Get the Unit from the map
			Unit unit = (Unit) map.getObject(unitIDs.get(i));
			fieldOfViewSet.addAll(unit.getFieldOfView());
		}
		// Return the collection
		return fieldOfViewSet;
	}

	/**
	 * Returns a collection of {@link Structure}s that this player is currently controlling on the provided {@link Map}.
	 * 
	 * @param map
	 *            The map to get the structures from.
	 */
	public List<Structure> getStructures(Map map) {
		List<Structure> structures = new ArrayList<Structure>();
		for (int i = 0; i < structureIDs.size; i++) {
			// Make sure the structure did not get removed just before this call
			if (map.getObject(structureIDs.get(i)) != null) {
				structures.add((Structure) map.getObject(structureIDs.get(i)));
			}
		}
		return structures;
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
			// Make sure the unit did not get removed just before this call
			if (map.getObject(unitIDs.get(i)) != null) {
				units.add((Unit) map.getObject(unitIDs.get(i)));
			}
		}
		return units;
	}

	/**
	 * Awards a score to this player.
	 * 
	 * @param value
	 *            The value of the score awarded to this player.
	 */
	public void awardScore(int value) {
		score += value;
	}

	/**
	 * Awards resources to this player.
	 * 
	 * @param amount
	 *            The amount of resources awarded to this player.
	 */
	public void awardResource(int amount) {
		resource += amount;
	}

	/**
	 * Assign a {@link Structure} to be this player's command center.
	 * 
	 * @param commandCenter
	 *            The structure to assign.
	 */
	public void assignCommandCenter(Structure commandCenter) {
		this.commandCenterID = commandCenter.getID();
	}

	/**
	 * Adds a Unit to this Player's control.
	 * 
	 * @param unitID
	 *            The ID of the unit to add.
	 */
	public void addUnit(int unitID) {
		unitIDs.add(unitID);
	}

	/**
	 * Removes a Unit from this Player's control.
	 * 
	 * @param unitID
	 *            The ID of the unit to remove.
	 */
	public void removeUnit(int unitID) {
		unitIDs.removeValue(unitID);
	}

	/**
	 * Adds a Structure to this Player's control.
	 * 
	 * @param structureID
	 *            The ID of the structure to add.
	 */
	public void addStructure(int structureID) {
		structureIDs.add(structureID);
	}

	/**
	 * Removes a Structure from this Player's control.
	 * 
	 * @param structureID
	 *            The ID of the structure to remove.
	 */
	public void removeStructure(int structureID) {
		structureIDs.removeValue(structureID);
	}

	/**
	 * Inform this Player that it's command center has been destroyed. This method will remove all Unit IDs from the
	 * player's collection and will half the player's current score. Also any structures that are currently under this
	 * player's control will be released.
	 * 
	 * @param map
	 *            The map that the game is being played on.
	 * @param commandCenterID
	 *            The unique identifier of the {@link Structure} that was destroyed.
	 */
	public void informCommandCenterDestroyed(Map map, int commandCenterID) {
		// Check if the IDs match
		if (this.commandCenterID == commandCenterID) {
			// Release all currently controlled structures
			List<Structure> structures = getStructures(map);
			for (Structure structure : structures) {
				structure.setControllingPlayerID(Constants.STRUCTURE_NO_CONTROL);
			}
			// Clear our collection of Structure IDs
			structureIDs.clear();

			// Remove all units
			List<Unit> units = getUnits(map);
			for (Unit unit : units) {
				map.unregisterGameObject(unit);
			}
			// Clear collection of Unit IDs
			unitIDs.clear();

			// Half the player's score
			this.setScore((int) (this.score / 2f));
		}
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
		return StringExtensions.format("%s (ID: %d)", this.name, this.ID);
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
