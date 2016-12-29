package net.codepoke.ai.challenge.hunterkiller.orders;

import lombok.Getter;
import net.codepoke.ai.challenge.hunterkiller.MapLocation;
import net.codepoke.ai.challenge.hunterkiller.enums.UnitOrderType;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Unit;

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
  
  /**
   * The target location of this order.
   */
  private MapLocation targetLocation;
  
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
  
  /**
   * Constructs a new instance.
   * 
   * @param unit
   *          The unit the order is for.
   * @param type
   *          The type of order.
   * @param target
   *          The target location for this order.
   */
  public UnitOrder(Unit unit, UnitOrderType type, MapLocation target) {
    this(unit, type);
    this.targetLocation = new MapLocation(target.getX(), target.getY());
  }
  
  //endregion
  
}
