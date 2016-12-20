package main.java.net.codepoke.ai.challenge.hunterkiller;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import main.java.net.codepoke.ai.challenge.hunterkiller.gameobjects.mapfeature.Base;
import main.java.net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Unit;
import net.codepoke.ai.GameRules.Action;

/**
 * Abstract class representing a player in the game. A player has a {@link Base} from which they can
 * spawn {@link Unit}s, payed for with a currency that is acquired automatically over time. When a
 * player can act during a turn, they create an {@link Action} collection that contains at most one
 * action per base or unit under their control.
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
@Getter
public abstract class Player {
  
  //region Constants
  
  /**
   * The amount of resources a player starts the game with.
   */
  public static final int PLAYER_STARTING_RESOURCE = 10;
  
  //endregion
  
  //region Properties
  
  /**
   * The player's name.
   */
  private String name;
  
  /**
   * The player's unique identifier.
   */
  private int ID;
  
  /**
   * The player's current resources.
   */
  protected int resource;
  
  /**
   * The {@link Base} that is assigned to this player.
   */
  private Base base;
  
  /**
   * Collection of Units that this player controls.
   */
  protected List<Unit> squad;
  
  /**
   * The score this player has accumulated during the game.
   */
  private int score;
  
  //endregion
  
  //region Constructor
  
  /**
   * Constructs a new player.
   * 
   * @param id
   *          The player's unique identifier.
   * @param name
   *          The player's name.
   * @param base
   *          The base that is assigned to this player.
   */
  public Player(int id, String name, Base base) {
    this.ID = id;
    this.name = name;
    this.base = base;
    //Create a new list to store the squad into
    squad = new ArrayList<Unit>();
  }
  
  //endregion
  
  //region Public methods
  
  public String toString() {
    return String.format("%s (ID: %d)", this.name, this.ID);
  }
  
  //endregion
  
  //region Protected methods
  
  /**
   * Awards a score to this player.
   * 
   * @param value
   *          The value of the score awarded to this player.
   */
  protected void awardScore(int value) {
    score += value;
  }
  
  //endregion
  
  //region Abstract methods
  
  /**
   * Returns a collection of {@link Action}s to enact upon the current game state.
   * 
   * @param state
   *          The current game state.
   * @return A collection of actions forming this player's turn.
   */
  public abstract List<Action> act(HunterKillerState state);
  
  /**
   * Returns a deep copy of this player.
   * 
   * @return
   */
  public abstract Player copy();
  
  //endregion
  
}
