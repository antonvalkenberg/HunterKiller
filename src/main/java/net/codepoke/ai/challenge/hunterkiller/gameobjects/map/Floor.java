package main.java.net.codepoke.ai.challenge.hunterkiller.gameobjects.map;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import main.java.net.codepoke.ai.challenge.hunterkiller.gameobjects.GameObject;

/**
 * Class representing a floor tile in the game.
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class Floor extends MapFeature {

	//region Constants

	/**
	 * Floors are indestructible.
	 */
	public static final boolean DEFAULT_FLOOR_DESTRUCTIBLE = false;
	
	/**
	 * Floors do not block Line of Sight.
	 */
	public static final boolean DEFAULT_FLOOR_BLOCKS_LOS = false;
	
	/**
	 * Floors can be moved over.
	 */
	public static final boolean DEFAULT_FLOOR_WALKABLE = true;
	
	//endregion
	
	//region Constructor

	/**
	 * Constructs a new instance of a Floor.
	 * @param mapPosition The Floor's position on the Map.
	 */
	public Floor(int mapPosition) {
		super(mapPosition, DEFAULT_FLOOR_DESTRUCTIBLE, DEFAULT_FLOOR_BLOCKS_LOS, DEFAULT_FLOOR_WALKABLE);
	}

	//endregion
	
	@Override
	public GameObject copy() {
		return new Floor(this.getPosition());
	}

}
