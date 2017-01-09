package net.codepoke.ai.challenge.hunterkiller.gameobjects.mapfeature;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.codepoke.ai.challenge.hunterkiller.Constants;
import net.codepoke.ai.challenge.hunterkiller.MapLocation;
import net.codepoke.ai.challenge.hunterkiller.enums.TileType;

/**
 * Class representing a floor tile in HunterKiller.
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Floor
		extends MapFeature {

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
		super(id, mapLocation, Constants.FLOOR_DESTRUCTIBLE, Constants.FLOOR_BLOCKS_LOS, Constants.FLOOR_WALKABLE);
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
