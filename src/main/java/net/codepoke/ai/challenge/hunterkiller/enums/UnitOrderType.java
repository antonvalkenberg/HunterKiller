package net.codepoke.ai.challenge.hunterkiller.enums;

/**
 * Enumeration of all types of
 * {@link net.codepoke.ai.challenge.hunterkiller.orders.HunterKillerOrder HunterKillerOrder}s
 * available to a {@link net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Unit Unit}. These
 * currently include:
 * <ul>
 * <li>Movement
 * <ul>
 * <li>Move up (North)</li>
 * <li>Move right (East)</li>
 * <li>Move down (South)</li>
 * <li>Move left (West)</li>
 * </ul>
 * </li>
 * <li>Rotation
 * <ul>
 * <li>Rotate left (West)</li>
 * <li>Rotate right (East)</li>
 * </ul>
 * </li>
 * <li>Attacking
 * <ul>
 * <li>Attack a location</li>
 * <li>Special attack a location</li>
 * </ul>
 * </li>
 * </ul>
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
public enum UnitOrderType {
  //If adding a type, don't forget to add it to the documentation list above!
  MOVE_NORTH, MOVE_EAST, MOVE_SOUTH, MOVE_WEST, ROTATE_WEST, ROTATE_EAST, ATTACK, ATTACK_SPECIAL;
}
