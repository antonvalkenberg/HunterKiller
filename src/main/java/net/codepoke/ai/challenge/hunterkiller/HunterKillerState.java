package main.java.net.codepoke.ai.challenge.hunterkiller;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import main.java.net.codepoke.ai.challenge.hunterkiller.actions.NullMove;
import net.codepoke.ai.GameRules.State;

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
public class HunterKillerState implements State {
  
  //region Properties
  
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
  
  //endregion
  
  //region Constructor
  
  /**
   * Constructs a new state.
   * 
   * @param map
   *          The {@link Map} that is being played on.
   * @param players
   *          The players in the game.
   * @param currentRound
   *          The current round of the game.
   * @param currentPlayerID
   *          The ID of the player that is currently active.
   */
  public HunterKillerState(Map map, Player[] players, int currentRound, int currentPlayerID) {
    this.currentRound = currentRound;
    this.activePlayerID = currentPlayerID;
    //Get a deep copy of the map
    this.map = map.copy();
    //Make a deep copy of the players array
    this.players = new Player[players.length];
    for(int i = 0; i < players.length; i++) {
      this.players[i] = players[i].copy();
    }
  }
  
  //endregion
  
  //region Public methods
  
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
   * Determines whether or not this state represents a completed game.
   * 
   * @return
   */
  public boolean isDone() {
    //A game is completed once only 1 base remains
    return map.getCurrentBaseCount() == 1;
  }
  
  /**
   * Ends the turn for the currently active player and alerts the map that a new player turn can
   * commence.
   */
  public void endPlayerTurn() {
    //Select the next player (next ID)
    activePlayerID = ++activePlayerID % players.length;
    //Check if we've reached a new round
    if(activePlayerID == 0) {
      currentRound++;
    }
    //Do a tick after each player's turn, to check for killed units/features
    map.tick(this);
  }
  
  //endregion
  
  //region Overridden methods
  
  @Override
  public long hashKey() {
    // TODO Create a hash of:
    //    - round #
    //    - active player ID
    //    - player IDs
    //    - all object IDs on the map
    return hashCode();
  }
  
  @Override
  public State copy() {
    return new HunterKillerState(map, players, currentRound, activePlayerID);
  }
  
  @Override
  public int getCurrentPlayer() {
    return activePlayerID;
  }
  
  @Override
  public HunterKillerAction createNullMove() {
    return new NullMove(this);
  }
  
  //endregion
  
}
