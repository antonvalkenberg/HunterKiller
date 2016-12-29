package net.codepoke.ai.challenge.hunterkiller.enums;

/**
 * Enumeration of all types of {@link HunterKillerOrder}s available to the {@link Base}. These
 * currently include:
 * <ul>
 * <li>Spawn a Soldier</li>
 * <li>Spawn a Medic</li>
 * <li>Spawn an Infected</li>
 * </ul>
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
public enum BaseOrderType {
  //If adding a type, don't forget to add it in the list above!
  SPAWN_INFECTED, SPAWN_MEDIC, SPAWN_SOLDIER;
}
