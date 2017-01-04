package net.codepoke.ai.challenge.hunterkiller.enums;

import lombok.AllArgsConstructor;

/**
 * Defines the various types of tiles that can be at a location and their string representation.
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
@AllArgsConstructor
public enum TileType {
	SPACE('.'),
	FLOOR('_'),
	WALL('█'),
	DOOR_CLOSED('D'),
	DOOR_OPEN('O'),
	SOLDIER('S'),
	MEDIC('M'),
	INFECTED('I'),
	BASE('B');

	public char txt;

	public static TileType valueOf(char c) {
		switch (c) {
		case 'B':
			return BASE;
		case 'I':
			return INFECTED;
		case 'M':
			return MEDIC;
		case 'S':
			return SOLDIER;
		case 'O':
			return DOOR_OPEN;
		case 'D':
			return DOOR_CLOSED;
		case '█':
			return WALL;
		case '_':
			return FLOOR;
		case '.':
		default:
			return SPACE;
		}
	}
}
