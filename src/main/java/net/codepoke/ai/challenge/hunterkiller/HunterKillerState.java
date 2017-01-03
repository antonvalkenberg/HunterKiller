package net.codepoke.ai.challenge.hunterkiller;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.codepoke.ai.challenge.hunterkiller.orders.NullMove;
import net.codepoke.ai.states.HiddenState;
import net.codepoke.ai.states.SequentialState;

import com.badlogic.gdx.utils.IntArray;

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
public class HunterKillerState
		implements HiddenState, SequentialState {

	// region Constants

	/**
	 * The frequency (in rounds) with which resources are awarded to players.
	 */
	private static final int RESOURCE_AWARD_FREQUENCY = 3;
	/**
	 * The amount of resources awarded to a player.
	 */
	private static final int RESOURCE_AWARD_AMOUNT = 12;

	// endregion

	// region Properties

	/**
	 * The round number this state is currently in.
	 */
	private int currentRound;

	/**
	 * The index in the Map's playerID collection of the player that is the active player in this
	 * state.
	 */
	private int activePlayerIDIndex;

	/**
	 * The players in the game.
	 */
	private Player[] players;

	/**
	 * Array containing the IDs of the players in the game.
	 */
	private IntArray internalPlayerIDs;

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
	 * @param internalPlayerIDs
	 *            The IDs of the Players for internal use. Note: these will range from [0;#players], instead of their
	 *            personal ID indicating in which section number they spawn/control.
	 * @param currentRound
	 *            The current round of the game.
	 * @param currentPlayerIDIndex
	 *            The index in the internal playerID collection of the player that is currently active.
	 */
	public HunterKillerState(Map map, Player[] players, IntArray internalPlayerIDs, int currentRound, int currentPlayerIDIndex) {
		this.currentRound = currentRound;
		this.activePlayerIDIndex = currentPlayerIDIndex;
		this.players = players;
		this.internalPlayerIDs = internalPlayerIDs;
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
		this.activePlayerIDIndex = otherState.activePlayerIDIndex;
		// Get a deep copy of the map
		this.map = map.copy();
		// Make a deep copy of the players array
		this.players = new Player[players.length];
		for (int i = 0; i < players.length; i++) {
			this.players[i] = players[i].copy();
		}
	}

	// endregion

	// region Public methods

	/**
	 * Returns the round number this state is currently in.
	 * 
	 * @return
	 */
	public int getCurrentRound() {
		return currentRound;
	}

	/**
	 * Returns the number of players in the game.
	 * 
	 * @return
	 */
	public int getNumberOfPlayers() {
		return players.length;
	}

	/**
	 * Returns the player with a specific ID. If no such player is found, null is returned.
	 * 
	 * @param playerID
	 *            The ID of the player to return.
	 * @return
	 */
	public Player getPlayer(int playerID) {
		for (int i = 0; i < players.length; i++) {
			if (players[i].getID() == playerID)
				return players[i];
		}
		return null;
	}

	/**
	 * Set the collection of IDs of the players that are participating in the game.
	 * 
	 * @param playerIDs
	 */
	public void setPlayerIDs(IntArray playerIDs) {
		internalPlayerIDs = new IntArray(true, playerIDs.size);
		internalPlayerIDs.addAll(playerIDs);
	}

	/**
	 * Determines whether or not this state represents a completed game.
	 * 
	 * @return
	 */
	public boolean isDone() {
		// A game is completed once only 1 base remains
		return map.getCurrentBaseCount() == 1;
	}

	/**
	 * Ends the turn for the currently active player and alerts the map that a new player turn can
	 * commence.
	 */
	public void endPlayerTurn() {
		// Select the next player (next ID)
		activePlayerIDIndex = ++activePlayerIDIndex % players.length;
		// Check if we've reached a new round
		if (activePlayerIDIndex == 0) {
			// Reduce open-timers for Doors and special-attack cooldowns for Units.
			map.timer();
			// Increase round count
			currentRound++;
		}
		// Do a tick on the map after each player's turn
		map.tick(this);
		// If the next round-threshold has been reached, award players with new resources
		if (currentRound % RESOURCE_AWARD_FREQUENCY == 0) {
			for (Player player : players) {
				player.setResource(player.resource + RESOURCE_AWARD_AMOUNT);
			}
		}
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

	@Override
	public int getCurrentPlayer() {
		return internalPlayerIDs.get(activePlayerIDIndex);
	}

	@Override
	public HunterKillerAction createNullMove() {
		return new NullMove(this);
	}

	@Override
	public int[] getPlayerTurnOrder() {
		return internalPlayerIDs.items;
		// return AIUtility.defaultTurnOrder(players.length);
	}

	@Override
	public void prepare(int playerIndex) {
		// TODO Remove info from other players from state.
	}

	// endregion

}
