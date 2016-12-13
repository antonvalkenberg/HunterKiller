package main.java.net.codepoke.ai.challenge.hunterkiller.gameobjects.map;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import main.java.net.codepoke.ai.challenge.hunterkiller.gameobjects.GameObject;

/**
 * Class representing a door in the game.
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class Door extends MapFeature {

	//region Constants

	/**
	 * Doors are indestructible.
	 */
	public static final boolean DEFAULT_DOOR_DESTRUCTIBLE = false;

	/**
	 * Door are created closed and block Line of Sight in that state.
	 */
	public static final boolean DEFAULT_DOOR_BLOCKS_LOS = true;
	
	/**
	 * Doors can be moved over. (At which point they will open and remain open for a number of ticks)
	 */
	public static final boolean DEFAULT_DOOR_WALKABLE = true;
	
	/**
	 * Once opened a Door will remain open for this amount of ticks.
	 */
	public static final int DOOR_OPEN_TICKS = 10;
	
	//endregion
	
	//region Properties

	/**
	 * Timer that indicates how many ticks the Door will remain open for.
	 */
	public int openTimer = 0;

	//endregion
	
	//region Constructor

	/**
	 * Constructs a new instance of a Door.
	 * @param mapPosition The Door's position on the Map.
	 */
	public Door(int mapPosition) {
		this(mapPosition, 0);
	}
	
	/**
	 * Constructs a new instance of a Door with a specified time before it closes.
	 * @param mapPosition The Door's position of the Map.
	 * @param timeToClose Amount of ticks before the Door closes.
	 */
	public Door(int mapPosition, int timeToClose) {
		super(mapPosition, DEFAULT_DOOR_DESTRUCTIBLE, timeToClose <= 0, DEFAULT_DOOR_WALKABLE);
		openTimer = timeToClose;
	}
	
	//endregion
	
	/**
	 * Whether or not this Door is blocking Line of Sight.
	 */
	@Override
	public boolean isBlockingLOS() {
		return !isOpen();
	}
	
	/**
	 * Whether or not this Door is open.
	 * @return ^
	 */
	public boolean isOpen() {
		return openTimer > 0;
	}
	
	/**
	 * Open this Door. It will close after a predetermined amount of ticks.
	 */
	public void open() {
		openTimer = DOOR_OPEN_TICKS;
	}
	
	@Override
	public GameObject copy() {
		return new Door(this.getPosition(), this.getOpenTimer());
	}

}
