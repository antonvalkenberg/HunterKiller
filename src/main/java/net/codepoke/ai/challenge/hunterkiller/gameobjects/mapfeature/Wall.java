package net.codepoke.ai.challenge.hunterkiller.gameobjects.mapfeature;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.codepoke.ai.challenge.hunterkiller.Constants;
import net.codepoke.ai.challenge.hunterkiller.MapLocation;
import net.codepoke.ai.challenge.hunterkiller.enums.TileType;

/**
 * Class representing a wall in the game.
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Wall
		extends MapFeature {

	// region Constructor

	/**
	 * Constructs a new instance of a Wall.
	 * 
	 * @param id
	 *            the Wall's unique identifier.
	 * @param mapLocation
	 *            The Wall's location on the Map.
	 */
	public Wall(int id, MapLocation mapLocation) {
		super(id, mapLocation, Constants.WALL_DESTRUCTIBLE, Constants.WALL_BLOCKS_LOS, Constants.WALL_WALKABLE);
	}

	// endregion

	// region Overridden methods

	@Override
	public Wall copy(int id) {
		return new Wall(id, this.getLocation());
	}

	@Override
	public Wall copy() {
		return this.copy(this.getID());
	}

	@Override
	public String toString() {
		return "" + TileType.WALL.txt;
	}

	// endregion

}
