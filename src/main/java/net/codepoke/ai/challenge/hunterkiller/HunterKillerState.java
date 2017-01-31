package net.codepoke.ai.challenge.hunterkiller;

import java.util.HashSet;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.codepoke.ai.AIUtility;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.GameObject;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.mapfeature.Structure;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Unit;
import net.codepoke.ai.challenge.hunterkiller.orders.NullMove;
import net.codepoke.ai.states.HiddenState;
import net.codepoke.ai.states.SequentialState;

import com.badlogic.gdx.utils.Array;

/**
 * Class representing the state of the HunterKiller game. In this state one {@link Player} is the
 * currently active player. Contains the {@link Map} on which the game is being played. There is
 * also a round number that is being tracked.
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HunterKillerState
		implements HiddenState, SequentialState {

	// region Properties

	/**
	 * The round number this state is currently in.
	 */
	private int currentRound;

	/**
	 * The ID of the player that is the active player in this state.
	 */
	private int activePlayerID;

	/**
	 * The players in the game.
	 */
	private Player[] players;

	/**
	 * The map that the game is being played on.
	 */
	private Map map;

	// endregion

	// region Constructor

	/**
	 * Constructs a new state.
	 * 
	 * @param map
	 *            The {@link Map} that is being played on.
	 * @param players
	 *            The players in the game.
	 * @param currentRound
	 *            The current round of the game.
	 * @param currentPlayerID
	 *            The ID of the player that is currently active.
	 */
	public HunterKillerState(Map map, Player[] players, int currentRound, int currentPlayerID) {
		this.currentRound = currentRound;
		this.activePlayerID = currentPlayerID;
		this.players = players;
		this.map = map;
	}

	/**
	 * Copy constructor.
	 * 
	 * @param otherState
	 *            The state to copy.
	 */
	public HunterKillerState(HunterKillerState otherState) {
		this.currentRound = otherState.currentRound;
		this.activePlayerID = otherState.activePlayerID;
		// Get a deep copy of the map
		this.map = otherState.map.copy();
		// Make a deep copy of the players array
		this.players = new Player[otherState.getNumberOfPlayers()];
		for (int i = 0; i < players.length; i++) {
			this.players[i] = otherState.getPlayer(i)
										.copy();
		}
	}

	// endregion

	// region Public methods

	/**
	 * Returns the round number this state is currently in.
	 */
	public int getCurrentRound() {
		return currentRound;
	}

	/**
	 * Returns the number of players in the game.
	 */
	public int getNumberOfPlayers() {
		return players.length;
	}

	/**
	 * Returns the player with a specific ID. If no such player is found, null is returned.
	 * 
	 * @param playerID
	 *            The ID of the player to return.
	 */
	public Player getPlayer(int playerID) {
		for (int i = 0; i < players.length; i++) {
			if (players[i].getID() == playerID)
				return players[i];
		}
		return null;
	}

	/**
	 * Returns the currently active player.
	 */
	public Player getActivePlayer() {
		return players[activePlayerID];
	}

	/**
	 * Determines whether or not this state represents a completed game.
	 */
	public boolean isDone() {
		// A game is completed once only 1 command center remains, or if we have reached the maximum allowed number of
		// rounds and the last player has made their move
		return map.getCurrentCommandCenterCount() == 1
				|| (currentRound >= Constants.RULES_MAX_GAME_ROUNDS && activePlayerID == players[players.length - 1].getID());
	}

	/**
	 * Ends the turn for the currently active player and alerts the map that a new player turn can
	 * commence.
	 */
	public void endPlayerTurn() {
		// Select the next player (next ID)
		activePlayerID = ++activePlayerID % players.length;

		// Check if we've reached a new round
		if (activePlayerID == 0) {
			// Reduce open-timers for Doors and special-attack cooldowns for Units.
			map.timer();

			// Increase round count
			currentRound++;

			// If the next round-threshold has been reached, make structures generate things
			if (currentRound % Constants.RULES_STRUCTURE_GENERATION_FREQUENCY == 0) {
				Array<GameObject> objects = map.getObjects();
				for (int i = 0; i < objects.size; i++) {
					if (objects.get(i) instanceof Structure) {
						Structure structure = (Structure) objects.get(i);
						if (structure.isGeneratesResource())
							structure.awardResourcesToController(this);
						if (structure.isGeneratesScore())
							structure.awardScoreToController(this);
					}
				}
			}
		}

		// Do a tick on the map after each player's turn
		map.tick(this);
	}

	// endregion

	// region Overridden methods

	@Override
	public long hashKey() {
		return hashCode();
	}

	/**
	 * Creates a deep copy of this state through the copy constructor.
	 */
	@Override
	public HunterKillerState copy() {
		return new HunterKillerState(this);
	}

	/**
	 * This method returns the ID of the currently active player. Implementation of
	 * {@link SequentialState#getCurrentPlayer()}.
	 */
	@Override
	public int getCurrentPlayer() {
		return activePlayerID;
	}

	@Override
	public HunterKillerAction createNullMove() {
		return new NullMove(this);
	}

	@Override
	public int[] getPlayerTurnOrder() {
		return AIUtility.defaultTurnOrder(players.length);
	}

	@Override
	public void prepare(int activePlayerID) {
		// We need to remove any units that belong to another player, and are not in the active player's FoV
		GameObject[][] mapContent = map.getMapContent();
		HashSet<MapLocation> playerFoV = players[activePlayerID].getCombinedFieldOfView(map);

		// Go through each position on the map
		for (int i = 0; i < mapContent.length; i++) {
			// Check if this location lies outside of the player's field-of-view
			if (!playerFoV.contains(map.toLocation(i))) {
				// Check if there is a unit there
				if (mapContent[i][Constants.MAP_INTERNAL_UNIT_INDEX] != null) {
					// Check if that unit belongs to another player
					Unit unit = (Unit) mapContent[i][Constants.MAP_INTERNAL_UNIT_INDEX];
					if (unit.getControllingPlayerID() != activePlayerID) {
						// Remove the unit from the map
						map.unregisterGameObject(unit);
					}
				}
			}
		}
	}

	// endregion

}
