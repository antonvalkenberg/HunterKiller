/**
 * 
 */
package main.java.net.codepoke.ai.challenge.hunterkiller;

import net.codepoke.ai.GameRules.Action;
import net.codepoke.ai.GameRules.State;

/**
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
public class HunterKillerState implements State {
  
  /**
   * The round number this state is currently in.
   */
  private int currentRound;
  
  public HunterKillerState(Map map, Player[] players, int currentRound) {
    // TODO Auto-generated constructor stub
  }
  
  /**
   * Returns the round number this state is currently in.
   * 
   * @return
   */
  public int getCurrentRound() {
    return currentRound;
  }
  
  //region Overridden methods
  
  @Override
  public long hashKey() {
    // TODO Auto-generated method stub
    return 0;
  }
  
  @Override
  public State copy() {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  public int getCurrentPlayer() {
    // TODO Auto-generated method stub
    return 0;
  }
  
  @Override
  public Action createNullMove() {
    // TODO Auto-generated method stub
    return null;
  }
  
  //endregion
  
}
