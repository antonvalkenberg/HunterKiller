package main.java.net.codepoke.ai.challenge.hunterkiller.gameobjects;

import main.java.net.codepoke.ai.challenge.hunterkiller.enums.Direction;

/**
 * This interface defines the properties and methods for a Unit in the game.
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
public interface Unit {

	public int position;
	
	public Direction orientation;
	
	public int fieldOfViewRange;
	
	public int fieldOfViewAngle;
	
	public int attackRange;
	
	public int attackDamage;
	
	public int specialAttackCooldown;
	
	public int spawnCost;
	
	public int scoreWorth;
}
