package main.java.net.codepoke.ai.challenge.hunterkiller;

import lombok.Getter;
import net.codepoke.ai.GameRules.Action;

/**
 * Abstract class representing an {@link Action} in the game. A {@link Player}'s turn consists of a
 * collection of actions that mutate the current game state. Note that for an action to be executed,
 * the player and round must match the current state's player and round. Otherwise, the action will
 * be marked as invalid and ignored.
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
@Getter
public abstract class HunterKillerAction implements Action {
  
  //region Properties
  
  /**
   * The ID of the player that is acting in the current state.
   */
  private int actingPlayerID;
  
  /**
   * The round number of the current state.
   */
  private int currentRound;
  
  //endregion
  
  //region Constructor
  
  /**
   * Constructs a new HunterKillerAction to be executed on the provided state.
   * 
   * @param state
   *          The current state of the game.
   */
  public HunterKillerAction(HunterKillerState state) {
    this.actingPlayerID = state.getCurrentPlayer();
    this.currentRound = state.getCurrentRound();
  }
  
  //endregion
  
}
