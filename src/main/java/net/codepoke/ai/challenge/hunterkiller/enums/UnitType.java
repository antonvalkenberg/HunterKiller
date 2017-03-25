package net.codepoke.ai.challenge.hunterkiller.enums;

import net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Infected;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Medic;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Soldier;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Unit;

/**
 * This class enumerates the different types of {@link Unit}s in HunterKiller. Current unit types are:
 * <ul>
 * <li>{@link Soldier}</li>
 * <li>{@link Medic}</li>
 * <li>{@link Infected}</li>
 * </ul>
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
public enum UnitType {
	// If adding a type, don't forget to add it in the documentation list above!
	Soldier,
	Medic,
	Infected;
	
	public static final UnitType[] values = values();
}
