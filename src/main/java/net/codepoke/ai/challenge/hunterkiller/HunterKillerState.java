package net.codepoke.ai.challenge.hunterkiller;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.codepoke.ai.GameRules.State;
import net.codepoke.ai.challenge.hunterkiller.orders.NullMove;

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
  
  //region Constants
  
  /**
   * The frequency (in rounds) with which resources are awarded to players.
   */
  private static final int RESOURCE_AWARD_FREQUENCY = 3;
  /**
   * The amount of resources awarded to a player.
   */
  private static final int RESOURCE_AWARD_AMOUNT = 12;
  
  //endregion
  
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
   * Returns the player with a specific ID. If no such player is found, null is returned.
   * 
   * @param playerID
   *          The ID of the player to return.
   * @return
   */
  public Player getPlayer(int playerID) {
    for(int i = 0; i < players.length; i++) {
      if(players[i].getID() == playerID)
        return players[i];
    }
    return null;
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
      //Reduce open-timers for Doors and special-attack cooldowns for Units.
      map.timer();
      //Increase round count
      currentRound++;
    }
    //Do a tick on the map after each player's turn
    map.tick(this);
    //If the next round-threshold has been reached, award players with new resources
    if(currentRound % RESOURCE_AWARD_FREQUENCY == 0) {
      for(Player player : players) {
        player.resource += RESOURCE_AWARD_AMOUNT;
      }
    }
  }
  
  //endregion
  
  //region Overridden methods
  
  @Override
  public long hashKey() {
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
