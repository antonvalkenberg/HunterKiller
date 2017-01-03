package net.codepoke.ai.challenge.hunterkiller.gameobjects.mapfeature;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.codepoke.ai.challenge.hunterkiller.MapLocation;
import net.codepoke.ai.challenge.hunterkiller.enums.TileType;

/**
 * Class representing a door in HunterKiller.
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class Door
		extends MapFeature {

	// region Constants

	/**
	 * Doors are indestructible.
	 */
	public static final boolean DOOR_DESTRUCTIBLE = false;
	/**
	 * Door are created closed and block Line of Sight in that state.
	 */
	public static final boolean DOOR_BLOCKS_LOS = true;
	/**
	 * Doors can be moved over. (At which point they will open and remain open for a number of rounds)
	 */
	public static final boolean DOOR_WALKABLE = true;
	/**
	 * Once opened a door will remain open for this amount of rounds.
	 */
	public static final int DOOR_OPEN_ROUNDS = 5;

	// endregion

	// region Properties

	/**
	 * Timer that indicates how many rounds the Door will remain open for.
	 */
	private int openTimer = 0;

	// endregion

	// region Constructor

	/**
	 * Constructs a new instance of a Door.
	 * 
	 * {@link Door#Door(int, MapLocation, int)}
	 */
	public Door(int id, MapLocation mapLocation) {
		this(id, mapLocation, 0);
	}

	/**
	 * Constructs a new instance of a Door with a specified time before it closes.
	 * 
	 * @param id
	 *            The Door's unique identifier.
	 * @param mapLocation
	 *            The Door's location of the Map.
	 * @param timeToClose
	 *            Amount of rounds before the Door closes.
	 */
	public Door(int id, MapLocation mapLocation, int timeToClose) {
		super(id, mapLocation, DOOR_DESTRUCTIBLE, timeToClose <= 0, DOOR_WALKABLE);
		openTimer = timeToClose;
	}

	// endregion

	// region Public methods

	/**
	 * Whether or not this Door is open.
	 * 
	 * @return ^
	 */
	public boolean isOpen() {
		return openTimer > 0;
	}

	/**
	 * Open this Door. It will close after a predetermined amount of rounds.
	 */
	public void open() {
		openTimer = DOOR_OPEN_ROUNDS;
		isBlockingLOS = false;
	}

	/**
	 * Close this Door.
	 */
	public void close() {
		openTimer = 0;
		isBlockingLOS = true;
	}

	/**
	 * Reduces the timer that keeps track of how long this door remains open.
	 */
	public void reduceTimer() {
		// Don't reduce if already at 0
		if (openTimer > 0)
			openTimer--;
		// Check if the door should be closed now
		if (openTimer == 0 && !isBlockingLOS)
			isBlockingLOS = true;
	}

	// endregion

	// region Overridden methods

	/**
	 * Whether or not this Door is blocking Line of Sight.
	 */
	@Override
	public boolean isBlockingLOS() {
		return !isOpen();
	}

	@Override
	public Door copy(int id) {
		return new Door(id, this.getLocation(), openTimer);
	}

	@Override
	public Door copy() {
		return this.copy(this.getID());
	}

	@Override
	public String toString() {
		return isOpen() ? "" + TileType.DOOR_OPEN.txt : "" + TileType.DOOR_CLOSED.txt;
	}

	// endregion

}
