package main.java.net.codepoke.ai.challenge.hunterkiller.gameobjects.map;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import main.java.net.codepoke.ai.challenge.hunterkiller.gameobjects.GameObject;

/**
 * Class representing a wall in the game.
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class Wall extends MapFeature {

	//region Constants

	/**
	 * Walls are indestructible.
	 */
	public static final boolean DEFAULT_WALL_DESTRUCTIBLE = false;
	
	/**
	 * Walls block Line of Sight.
	 */
	public static final boolean DEFAULT_WALL_BLOCKS_LOS = true;
	
	/**
	 * Walls can not be moved over.
	 */
	public static final boolean DEFAULT_WALL_WALKABLE = false;
	
	//endregion
	
	//region Constructor

	/**
	 * Constructs a new instance of a Wall.
	 * @param mapPosition The Wall's position on the Map.
	 */
	public Wall(int mapPosition) {
		super(mapPosition, DEFAULT_WALL_DESTRUCTIBLE, DEFAULT_WALL_BLOCKS_LOS, DEFAULT_WALL_WALKABLE);
	}
	
	//endregion
	
	@Override
	public GameObject copy() {
		return new Wall(this.getPosition());
	}

}
