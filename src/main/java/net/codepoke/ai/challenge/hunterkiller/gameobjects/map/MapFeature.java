package main.java.net.codepoke.ai.challenge.hunterkiller.gameobjects.map;

/**
 * This interface defines the properties and methods for a feature on the Map of the game.
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
public interface MapFeature {

	public boolean isDestructable;
	
	public int hp;
	
	public boolean isBlockingLOS;
	
	public boolean isWalkable;
}
