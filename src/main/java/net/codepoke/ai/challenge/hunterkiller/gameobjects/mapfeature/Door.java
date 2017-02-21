package net.codepoke.ai.challenge.hunterkiller.gameobjects.mapfeature;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.codepoke.ai.challenge.hunterkiller.HunterKillerConstants;
import net.codepoke.ai.challenge.hunterkiller.Map;
import net.codepoke.ai.challenge.hunterkiller.MapLocation;
import net.codepoke.ai.challenge.hunterkiller.enums.TileType;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Unit;

/**
 * Class representing a door in HunterKiller.
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
@Getter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Door
		extends MapFeature {

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
	 * {@link Door#Door(MapLocation, int)}
	 */
	public Door(MapLocation mapLocation) {
		this(mapLocation, 0);
	}

	/**
	 * Constructs a new instance of a Door with a specified time before it closes.
	 * 
	 * @param mapLocation
	 *            The Door's location of the Map.
	 * @param timeToClose
	 *            Amount of rounds before the Door closes.
	 */
	public Door(MapLocation mapLocation, int timeToClose) {
		super(mapLocation, HunterKillerConstants.DOOR_DESTRUCTIBLE, timeToClose <= 0, HunterKillerConstants.DOOR_WALKABLE);
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
		openTimer = HunterKillerConstants.DOOR_OPEN_ROUNDS;
		isBlockingLOS = false;
	}

	/**
	 * Close this Door. This method does nothing if there is a Unit at this Door's location.
	 * 
	 * @param map
	 *            The {@link Map} this Door is on.
	 */
	public void close(Map map) {
		if (map.getUnitAtLocation(getLocation()) == null) {
			openTimer = 0;
			isBlockingLOS = true;
		}
	}

	/**
	 * Reduces the timer that keeps track of how long this door remains open. Will not reduce the timer below 1 if a
	 * Unit is present at the Door's location.
	 * 
	 * @param map
	 *            The {@link Map} this Door is on.
	 */
	public void reduceTimer(Map map) {
		// Don't reduce if already at 0
		if (openTimer > 0)
			openTimer--;
		// Check if the door should be closed now
		if (openTimer == 0 && !isBlockingLOS) {
			// Get any Unit that might be at this Door's location
			Unit unit = map.getUnitAtLocation(getLocation());
			if (unit != null) {
				// If there is a Unit, don't close
				openTimer = 1;
				return;
			} else
				isBlockingLOS = true;
		}
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
	public Door copy() {
		Door newD = new Door(this.getLocation(), openTimer);
		newD.setID(this.getID());
		return newD;
	}

	@Override
	public String toString() {
		return isOpen() ? "" + TileType.DOOR_OPEN.txt : "" + TileType.DOOR_CLOSED.txt;
	}

	// endregion

}
