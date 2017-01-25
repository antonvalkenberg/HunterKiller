package net.codepoke.ai.challenge.hunterkiller.gameobjects;

import net.codepoke.ai.challenge.hunterkiller.Player;

/** Represents an object controlled by a specific player. */
public interface Controlled {

	/** The ID of the player which controls this object. */
	public int getControllingPlayerID();

	/**
	 * Whether or not this object is being controlled by the specified player.
	 */
	public boolean isControlledBy(Player player);
}
