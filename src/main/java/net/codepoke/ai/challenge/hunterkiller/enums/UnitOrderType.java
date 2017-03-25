package net.codepoke.ai.challenge.hunterkiller.enums;

import lombok.AllArgsConstructor;

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
@AllArgsConstructor
public enum UnitOrderType {
	// If adding a type, don't forget to add it to the documentation list above!
	MOVE(true),
	ROTATE_CLOCKWISE(false),
	ROTATE_COUNTER_CLOCKWISE(false),
	ATTACK(true),
	ATTACK_SPECIAL(true);

	public static final UnitOrderType[] values = values();
	
	public boolean hasLocation;
	
}
