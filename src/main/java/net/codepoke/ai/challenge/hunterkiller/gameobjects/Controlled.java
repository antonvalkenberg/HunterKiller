package net.codepoke.ai.challenge.hunterkiller.gameobjects;

/** Represents an object controlled by a specific player. */
public interface Controlled {

	/** The ID of the player which controls this object. */
	public int getControllingPlayerID();
	
}
