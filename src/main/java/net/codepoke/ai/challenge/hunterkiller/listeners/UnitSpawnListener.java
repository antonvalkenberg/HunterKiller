package net.codepoke.ai.challenge.hunterkiller.listeners;

import net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Unit;

/**
 * Represents the ability to listen to any events of a {@link Unit} being spawned.
 * Note: events are not currently supported.
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
public interface UnitSpawnListener {

	/**
	 * A unit has spawned.
	 * 
	 * @param unit
	 *            The unit that was spawned.
	 */
	void unitSpawned(Unit unit);

}
