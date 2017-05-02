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
	 * Constructs a new instance of a closed Door.
	 * 
	 * {@link Door#Door(MapLocation, int)}
	 */
	public Door(MapLocation mapLocation) {
		this(mapLocation, 0);
	}

	/**
	 * Constructs a new instance of an open Door with a specified time before it closes. If that time is 0, the Door
	 * will be created closed.
	 * 
	 * @param mapLocation
	 *            The Door's location on the Map.
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
		return !isBlockingLOS;
	}

	/**
	 * Open this Door. It will close after a predetermined amount of rounds.
	 * 
	 * @param map
	 *            The {@link Map} this Door is on.
	 */
	public void open(Map map) {
		openTimer = HunterKillerConstants.DOOR_OPEN_ROUNDS;
		isBlockingLOS = false;
		// Invalidate field-of-view of nearby units
		map.invalidateFieldOfViewFor(getLocation());
	}

	/**
	 * Try to close this Door. This method does not close the door if there is a Unit at this Door's location. In that
	 * case, the Door's open-timer will be set to 1.
	 * 
	 * @param map
	 *            The {@link Map} this Door is on.
	 * @return Whether or not this Door successfully closed.
	 */
	public boolean tryClose(Map map) {
		// Get any Unit that might be at this Door's location
		Unit unit = map.getUnitAtLocation(getLocation());
		if (unit != null) {
			// If there is a Unit, don't close
			openTimer = 1;
			isBlockingLOS = false;
			return false;
		} else {
			openTimer = 0;
			isBlockingLOS = true;
			return true;
		}
	}

	/**
	 * Reduces the timer that keeps track of how long this door remains open. This method will try to close this Door if
	 * its open-timer has run out.
	 * 
	 * @param map
	 *            The {@link Map} this Door is on.
	 */
	public void reduceTimer(Map map) {
		// Don't reduce if already at 0
		if (openTimer > 0)
			openTimer--;
		// Check if the door should be closed now
		if (openTimer <= 0 && !isBlockingLOS) {
			// Try to close it.
			boolean success = tryClose(map);
			if (success) {
				// Invalidate field-of-view of nearby units
				map.invalidateFieldOfViewFor(getLocation());
			}
		}
	}

	// endregion

	// region Overridden methods

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
