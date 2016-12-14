package main.java.net.codepoke.ai.challenge.hunterkiller.gameobjects;

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
@NoArgsConstructor
public abstract class GameObject {

	//region Constants
	
	/**
	 * The default position if the object has not been placed yet.
	 */
	public static final int NOT_PLACED = -1;

	/**
	 * The default amount of health points an object has.
	 */
	public static final int DEFAULT_HP = 1;
	
	//endregion

	//region Properties

	/**
	 * The position index on the Map.
	 */
	public int position = NOT_PLACED;

	/**
	 * The maximum amount of health points for this object.
	 */
	public int hpMax = DEFAULT_HP;
	
	/**
	 * The amount of health points this object currently has.
	 */
	public int hpCurrent = hpMax;
	
	//endregion
	
	//region Constructor

	/**
	 * Constructs a new instance of a GameObject with default HP.
	 * @param mapPosition The GameObject's position on the Map.
	 */
	public GameObject(int mapPosition) {
		this(mapPosition, DEFAULT_HP);
	}

	/**
	 * Constructs a new instance of a GameObject.
	 * @param mapPosition The GameObject's position on the Map.
	 * @param maxHP The maximum amount of health points this GameObject can have.
	 */
	public GameObject(int mapPosition, int maxHP) {
		position = mapPosition;
		hpMax = maxHP;
		hpCurrent = maxHP;
	}

	/**
	 * Constructs a new instance of a GameObject.
	 * @param mapPosition The GameObject's position on the Map.
	 * @param maxHP The maximum amount of health points this GameObject can have.
	 * @param currentHP The current amount of health points this GameObject has.
	 */
	public GameObject(int mapPosition, int maxHP, int currentHP) {
		position = mapPosition;
		hpMax = maxHP;
		hpCurrent = currentHP;
	}
	
	//endregion
	
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
