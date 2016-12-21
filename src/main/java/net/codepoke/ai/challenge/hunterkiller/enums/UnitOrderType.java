package main.java.net.codepoke.ai.challenge.hunterkiller.enums;

import main.java.net.codepoke.ai.challenge.hunterkiller.HunterKillerOrder;
import main.java.net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Unit;

/**
 * Enumeration of all types of {@link HunterKillerOrder}s available to a {@link Unit}. These
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
 * <li>Rotate left</li>
 * <li>Rotate right</li>
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
  //If adding a type, don't forget to add it in the list above!
  MOVE_NORTH, MOVE_EAST, MOVE_SOUTH, MOVE_WEST, ROTATE_WEST, ROTATE_EAST, ATTACK, ATTACK_SPECIAL;
}
