package net.codepoke.ai.challenge.hunterkiller.enums;

/**
 * Enumeration of all types of {@link net.codepoke.ai.challenge.hunterkiller.orders.HunterKillerOrder HunterKillerOrder}
 * s
 * available to a {@link net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Unit Unit}. These
 * currently include:
 * <ul>
 * <li>Movement
 * <ul>
 * <li>Move</li>
 * </ul>
 * </li>
 * <li>Rotation
 * <ul>
 * <li>Rotate clockwise</li>
 * <li>Rotate counter-clockwise</li>
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
	// If adding a type, don't forget to add it to the documentation list above!
	MOVE,
	ROTATE_CLOCKWISE,
	ROTATE_COUNTER_CLOCKWISE,
	ATTACK,
	ATTACK_SPECIAL;
}
