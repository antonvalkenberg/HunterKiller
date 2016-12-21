package main.java.net.codepoke.ai.challenge.hunterkiller.actions;

import lombok.Getter;
import main.java.net.codepoke.ai.challenge.hunterkiller.HunterKillerOrder;
import main.java.net.codepoke.ai.challenge.hunterkiller.enums.UnitOrderType;
import main.java.net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Unit;

/**
 * Class representing a {@link HunterKillerOrder} performed by a {@link Unit}. An order can
 * currently involve movement or attacking.
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
@Getter
public class UnitOrder extends HunterKillerOrder {
  
  //region Properties
  
  /**
   * The type of order.
   */
  private UnitOrderType orderType;
  
  //endregion
  
  //region Constructor
  
  /**
   * Constructs a new instance.
   * 
   * @param unit
   *          The unit the order is for.
   * @param type
   *          The type of order.
   */
  public UnitOrder(Unit unit, UnitOrderType type) {
    super(unit);
    this.orderType = type;
  }
  
  //endregion
  
}
