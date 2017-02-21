package net.codepoke.ai.challenge.hunterkiller.gameobjects.mapfeature;

import lombok.NoArgsConstructor;
import net.codepoke.ai.challenge.hunterkiller.HunterKillerConstants;
import net.codepoke.ai.challenge.hunterkiller.MapLocation;
import net.codepoke.ai.challenge.hunterkiller.enums.TileType;

/**
 * Class representing a wall in the game.
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
@NoArgsConstructor
public class Wall
		extends MapFeature {

	// region Constructor

	/**
	 * Constructs a new instance of a Wall.
	 * 
	 * @param mapLocation
	 *            The Wall's location on the Map.
	 */
	public Wall(MapLocation mapLocation) {
		super(mapLocation, HunterKillerConstants.WALL_DESTRUCTIBLE, HunterKillerConstants.WALL_BLOCKS_LOS,
				HunterKillerConstants.WALL_WALKABLE);
	}

	// endregion

	// region Overridden methods

	@Override
	public Wall copy() {
		Wall newW = new Wall(this.getLocation());
		newW.setID(this.getID());
		return newW;
	}

	@Override
	public String toString() {
		return "" + TileType.WALL.txt;
	}

	// endregion

}
