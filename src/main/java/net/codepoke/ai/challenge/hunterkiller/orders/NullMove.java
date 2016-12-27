package main.java.net.codepoke.ai.challenge.hunterkiller.orders;

import main.java.net.codepoke.ai.challenge.hunterkiller.HunterKillerAction;
import main.java.net.codepoke.ai.challenge.hunterkiller.HunterKillerState;

/**
 * Class representing a null move. No action is taken. Does not serve much purpose other than
 * implementing the {@link State} interface.
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
public class NullMove extends HunterKillerAction {
  
  /**
   * Constructor.
   * 
   * @param state
   */
  public NullMove(HunterKillerState state) {
    super(state);
  }
  
}
