package main.java.net.codepoke.ai.challenge.hunterkiller.enums;

/**
 * Defines the various types of tiles that can be at a location and their String representation.
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
public enum TileTypes {
	SPACE("."),
	FLOOR("_"),
	WALL("["),
	DOOR_CLOSED("D"),
	DOOR_OPEN("O"),
	BASE("B"),
	SOLDIER("S"),
	MEDIC("M"),
	INFECTED("I");

	private TileTypes(String txt) {
		this.txt = txt;
	}

	public final String txt;
}
