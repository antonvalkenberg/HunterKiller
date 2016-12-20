package main.java.net.codepoke.ai.challenge.hunterkiller.actions;

import lombok.Getter;
import main.java.net.codepoke.ai.challenge.hunterkiller.HunterKillerAction;
import main.java.net.codepoke.ai.challenge.hunterkiller.HunterKillerState;
import main.java.net.codepoke.ai.challenge.hunterkiller.enums.UnitActionType;
import main.java.net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Unit;

/**
 * Class representing an {@link Action} performed by a {@link Unit}. An action can currently involve
 * movement or attacking.
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
@Getter
public class UnitAction extends HunterKillerAction {
  
  //region Properties
  
  /**
   * The type of action.
   */
  private UnitActionType actionType;
  
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
  public UnitAction(HunterKillerState state, UnitActionType type) {
    super(state);
    this.actionType = type;
  }
  
  //endregion
  
}
