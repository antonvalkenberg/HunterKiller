package main.java.net.codepoke.ai.challenge.hunterkiller;

import lombok.Getter;

/**
 * Represents a location on the Map.
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
@Getter
public class MapLocation {

	//region Properties
	
	private int x;
	
	private int y;
	
	//endregion
	
	//region Constructor
	
	public MapLocation(int x, int y) {
		this.x = x;
		this.y = y;
	}

	//endregion
	
}
