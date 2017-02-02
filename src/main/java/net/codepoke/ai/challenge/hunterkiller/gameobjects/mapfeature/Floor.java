package net.codepoke.ai.challenge.hunterkiller.gameobjects.mapfeature;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.codepoke.ai.challenge.hunterkiller.HunterKillerConstants;
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
	 * @param mapLocation
	 *            The Floor's location on the Map.
	 */
	public Floor(MapLocation mapLocation) {
		super(mapLocation, HunterKillerConstants.FLOOR_DESTRUCTIBLE, HunterKillerConstants.FLOOR_BLOCKS_LOS, HunterKillerConstants.FLOOR_WALKABLE);
	}

	// endregion

	// region Overridden methods

	@Override
	public Floor copy() {
		Floor newF = new Floor(this.getLocation());
		newF.setID(this.getID());
		return newF;
	}

	@Override
	public String toString() {
		return "" + TileType.FLOOR.txt;
	}

	// endregion

}
