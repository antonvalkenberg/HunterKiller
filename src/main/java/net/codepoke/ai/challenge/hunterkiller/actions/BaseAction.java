package main.java.net.codepoke.ai.challenge.hunterkiller.actions;

import lombok.Getter;
import main.java.net.codepoke.ai.challenge.hunterkiller.HunterKillerAction;
import main.java.net.codepoke.ai.challenge.hunterkiller.HunterKillerState;
import main.java.net.codepoke.ai.challenge.hunterkiller.enums.BaseActionType;
import main.java.net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Unit;

/**
 * Class representing an {@link Action} performed by a {@link Base}. Currently, the base can only
 * spawn {@link Unit}s.
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
@Getter
public class BaseAction extends HunterKillerAction {
  
  //region Properties
  
  /**
   * The type of action.
   */
  private BaseActionType actionType;
  
  //endregion
  
  //region Constructor
  
  /**
   * Constructs a new instance.
   * 
   * @param state
   *          The current game state.
   * @param type
   *          The type of action.
   */
  public BaseAction(HunterKillerState state, BaseActionType type) {
    super(state);
    this.actionType = type;
  }
  
  //endregion
  
}
