package main.java.net.codepoke.ai.challenge.hunterkiller.gameobjects.map;

import main.java.net.codepoke.ai.challenge.hunterkiller.gameobjects.GameObject;

/**
 * Class representing a space tile in the game.
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
public class Space extends MapFeature {

	//region Constants

	/**
	 * Space is indestructible.
	 */
	public static final boolean DEFAULT_SPACE_DESTRUCTIBLE = false;
	
	/**
	 * Space does not block Line of Sight.
	 */
	public static final boolean DEFAULT_SPACE_BLOCKS_LOS = false;
	
	/**
	 * Space can not be moved over.
	 */
	public static final boolean DEFAULT_SPACE_WALKABLE = false;
	
	//endregion
	
	//region Constructor

	/**
	 * Constructs a new instance of Space.
	 * @param mapPosition The Space's position on the Map.
	 */
	public Space(int mapPosition) {
		super(mapPosition, DEFAULT_SPACE_DESTRUCTIBLE, DEFAULT_SPACE_BLOCKS_LOS, DEFAULT_SPACE_WALKABLE);
	}
	
	@Override
	public GameObject copy() {
		return new Space(this.getPosition());
	}

}
