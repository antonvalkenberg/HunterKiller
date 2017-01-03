package net.codepoke.ai.challenge.hunterkiller.gameobjects.mapfeature;

import net.codepoke.ai.challenge.hunterkiller.MapLocation;
import net.codepoke.ai.challenge.hunterkiller.enums.TileType;

/**
 * Class representing a floor tile in HunterKiller.
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
public class Floor
		extends MapFeature {

	// region Constants

	/**
	 * Floors are indestructible.
	 */
	public static final boolean FLOOR_DESTRUCTIBLE = false;
	/**
	 * Floors do not block Line of Sight.
	 */
	public static final boolean FLOOR_BLOCKS_LOS = false;
	/**
	 * Floors can be moved over.
	 */
	public static final boolean FLOOR_WALKABLE = true;

	// endregion

	// region Constructor

	/**
	 * Constructs a new instance of a Floor.
	 * 
	 * @param id
	 *            The Floor's unique identifier.
	 * @param mapLocation
	 *            The Floor's location on the Map.
	 */
	public Floor(int id, MapLocation mapLocation) {
		super(id, mapLocation, FLOOR_DESTRUCTIBLE, FLOOR_BLOCKS_LOS, FLOOR_WALKABLE);
	}

	// endregion

	// region Overridden methods

	@Override
	public Floor copy(int id) {
		return new Floor(id, this.getLocation());
	}

	@Override
	public Floor copy() {
		return this.copy(this.getID());
	}

	@Override
	public String toString() {
		return "" + TileType.FLOOR.txt;
	}

	// endregion

}
