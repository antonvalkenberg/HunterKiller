package net.codepoke.ai.challenge.hunterkiller.gameobjects.mapfeature;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.codepoke.ai.challenge.hunterkiller.Constants;
import net.codepoke.ai.challenge.hunterkiller.MapLocation;
import net.codepoke.ai.challenge.hunterkiller.enums.TileType;

/**
 * Class representing a space tile in HunterKiller.
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Space
		extends MapFeature {

	// region Constructor

	/**
	 * Constructs a new instance of Space.
	 * 
	 * @param id
	 *            The Space's unique identifier.
	 * @param mapLocation
	 *            The Space's location on the Map.
	 */
	public Space(int id, MapLocation mapLocation) {
		super(id, mapLocation, Constants.SPACE_DESTRUCTIBLE, Constants.SPACE_BLOCKS_LOS, Constants.SPACE_WALKABLE);
	}

	// endregion

	// region Overridden methods

	@Override
	public Space copy(int id) {
		return new Space(id, this.getLocation());
	}

	@Override
	public Space copy() {
		return this.copy(this.getID());
	}

	@Override
	public String toString() {
		return "" + TileType.SPACE.txt;
	}

	// endregion

}
