package net.codepoke.ai.challenge.hunterkiller.gameobjects.mapfeature;

import lombok.NoArgsConstructor;
import net.codepoke.ai.challenge.hunterkiller.HunterKillerConstants;
import net.codepoke.ai.challenge.hunterkiller.MapLocation;
import net.codepoke.ai.challenge.hunterkiller.enums.TileType;

/**
 * Class representing a space tile in HunterKiller.
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
@NoArgsConstructor
public class Space
		extends MapFeature {

	// region Constructor

	/**
	 * Constructs a new instance of Space.
	 * 
	 * @param mapLocation
	 *            The Space's location on the Map.
	 */
	public Space(MapLocation mapLocation) {
		super(mapLocation, HunterKillerConstants.SPACE_DESTRUCTIBLE, HunterKillerConstants.SPACE_BLOCKS_LOS,
				HunterKillerConstants.SPACE_WALKABLE);
	}

	// endregion

	// region Overridden methods

	@Override
	public Space copy() {
		Space newS = new Space(this.getLocation());
		newS.setID(this.getID());
		return newS;
	}

	@Override
	public String toString() {
		return "" + TileType.SPACE.txt;
	}

	// endregion

}
