package net.codepoke.ai.challenge.hunterkiller.listeners;

import net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Unit;

/**
 * Represents the ability to listen to any events of the health of a {@link Unit} having changed.
 * Note: events are not currently supported.
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
public interface UnitHealthChanged {

	/**
	 * A unit's health changed.
	 * 
	 * @param unit
	 *            The unit that was affected.
	 */
	void unitHealthChanged(Unit unit);

}
