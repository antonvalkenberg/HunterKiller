package net.codepoke.ai.challenge.hunterkiller.enums;

import net.codepoke.ai.challenge.hunterkiller.Player;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.mapfeature.Structure;

/**
 * This class enumerates the different types of {@link Structure}s in HunterKiller. Current types are:
 * <ul>
 * <li>Base: can't be captured, allows spawning, generates resources. This acts as a {@link Player}'s command center; if
 * destroyed the player will be eliminated and it's score will be halved.</li>
 * <li>Outpost: can be captured, allows spawning, generates resources.</li>
 * <li>Stronghold: can be captured, does not allow spawning, generates resources.</li>
 * <li>Objective: can be captured, does not allow spawning, generates score points.</li>
 * </ul>
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
public enum StructureType {
	Base,
	Outpost,
	Stronghold,
	Objective;
}
