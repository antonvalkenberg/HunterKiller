package net.codepoke.ai.challenge.hunterkiller.orders;

import lombok.Getter;
import net.codepoke.ai.challenge.hunterkiller.enums.BaseOrderType;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.mapfeature.Base;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Unit;

/**
 * Class representing a {@link HunterKillerOrder} performed by a {@link Base}. Currently, the base
 * can only spawn {@link Unit}s.
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
@Getter
public class BaseOrder extends HunterKillerOrder {
  
  //region Properties
  
  /**
   * The type of order.
   */
  private BaseOrderType orderType;
  
  //endregion
  
  //region Constructor
  
  /**
   * Constructs a new instance.
   * 
   * @param base
   *          The base this order is for.
   * @param type
   *          The type of order.
   */
  public BaseOrder(Base base, BaseOrderType type) {
    super(base);
    this.orderType = type;
  }
  
  //endregion
  
}
