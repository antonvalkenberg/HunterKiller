package main.java.net.codepoke.ai.challenge.hunterkiller.gameobjects;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import main.java.net.codepoke.ai.challenge.hunterkiller.HunterKillerState;

/**
 * Abstract class representing any object in the game that can be placed on the map.
 * The game engine will call the object at the start of each round for a response.
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
@Setter
@Getter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public abstract class GameObject {

	/**
	 * The default position if the object has not been placed yet.
	 */
	public static final int NOT_PLACED = -1;

	/**
	 * The position index on the board.
	 */
	int position = NOT_PLACED;

	/**
	 * The default amount of health points an object has.
	 */
	public static final int DEFAULT_HP = 1;
	
	/**
	 * The maximum amount of health points for this object.
	 */
	int hpMax = DEFAULT_HP;
	
	/**
	 * The amount of health points this object currently has.
	 */
	int hpCurrent = hpMax;
	
	/**
	 * Creates a copy of this GameObject
	 * @return A GameObject that is a copy of this object
	 */
	public abstract GameObject copy();

	/**
	 * The tick call received upon every start of a round.
	 * When true is returned, the GameObject should be removed from the board.
	 */
	public boolean tick(HunterKillerState state) {
		return false;
	}
}
