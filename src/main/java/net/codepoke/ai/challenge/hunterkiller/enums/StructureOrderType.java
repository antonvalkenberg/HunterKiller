package net.codepoke.ai.challenge.hunterkiller.enums;

/**
 * Enumeration of all types of {@link net.codepoke.ai.challenge.hunterkiller.orders.HunterKillerOrder
 * HunterKillerOrders} available to a {@link net.codepoke.ai.challenge.hunterkiller.gameobjects.mapfeature.Structure
 * Structure}.
 * These currently include:
 * <ul>
 * <li>Spawn a {@link net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Soldier Soldier}.</li>
 * <li>Spawn a {@link net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Medic Medic}.</li>
 * <li>Spawn an {@link net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Infected Infected}.</li>
 * </ul>
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
public enum StructureOrderType {
	// If adding a type, don't forget to add it in the documentation list above!
	SPAWN_INFECTED,
	SPAWN_MEDIC,
	SPAWN_SOLDIER;
}
