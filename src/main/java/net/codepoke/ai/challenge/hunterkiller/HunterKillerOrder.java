package main.java.net.codepoke.ai.challenge.hunterkiller;

import lombok.Getter;
import main.java.net.codepoke.ai.challenge.hunterkiller.gameobjects.GameObject;

/**
 * Abstract class representing a {@link Player}'s order to a single {@link GameObject}.
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
@Getter
public abstract class HunterKillerOrder {
  
  //region Properties
  
  /**
   * The ID of the object that this order is for.
   */
  public int objectID;
  
  //endregion
  
  //region Constructor
  
  /**
   * Constructs a new order.
   * 
   * @param object
   *          The object this order is for.
   */
  public HunterKillerOrder(GameObject object) {
    this.objectID = object.getID();
  }
  
  //endregion
}
