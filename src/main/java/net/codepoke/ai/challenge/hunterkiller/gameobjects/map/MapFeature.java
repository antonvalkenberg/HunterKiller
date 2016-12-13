package main.java.net.codepoke.ai.challenge.hunterkiller.gameobjects.map;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import main.java.net.codepoke.ai.challenge.hunterkiller.gameobjects.GameObject;

/**
 * Abstract class representing a feature on the Map of the game.
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
@Setter
@Getter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public abstract class MapFeature extends GameObject {

	//region Constants

	/**
	 * The default amount of health points for a MapFeature.
	 */
	public static final int DEFAULT_HP = 5;
	
	/**
	 * Default destructibility of a MapFeature.
	 */
	public static final boolean DEFAULT_DESTRUCTIBLE = false;
	
	/**
	 * Whether or not a MapFeature blocks Line of Sight by default.
	 */
	public static final boolean DEFAULT_BLOCKING_LOS = false;
	
	/**
	 * Whether or not a MapFeature can be moved over by default.
	 */
	public static final boolean DEFAULT_WALKABLE = true;
	
	//endregion
	
	//region Properties

	/**
	 * Whether or not this feature on the map is destructible.
	 */
	public boolean isDestructible = DEFAULT_DESTRUCTIBLE;
	
	/**
	 * Whether or not this feature blocks Line of Sight for Units.
	 */
	public boolean isBlockingLOS = DEFAULT_BLOCKING_LOS;
	
	/**
	 * Whether or not Units can move over this feature.
	 */
	public boolean isWalkable = DEFAULT_WALKABLE;
	
	//endregion
	
	//region Constructor

	/**
	 * Constructs a new instance of a MapFeature with default HP.
	 * @param mapPosition The MapFeature's position on the Map.
	 * @param destructible Whether or not the MapFeature is destructible.
	 * @param blockingLOS Whether or not the MapFeature blocks Line of Sight for Units.
	 * @param walkable Whether or not Units can move over the MapFeature.
	 */
	public MapFeature(int mapPosition, boolean destructible, boolean blockingLOS, boolean walkable) {
		this(mapPosition, DEFAULT_HP, destructible, blockingLOS, walkable);
	}
	
	/**
	 * Constructs a new instance of a MapFeature.
	 * @param mapPosition The MapFeature's position on the Map.
	 * @param maxHP The maximum amount of health points this MapFeature will have.
	 * @param destructible Whether or not the MapFeature is destructible.
	 * @param blockingLOS Whether or not the MapFeature blocks Line of Sight for Units.
	 * @param walkable Whether or not Units can move over the MapFeature.
	 */
	public MapFeature(int mapPosition, int maxHP, boolean destructible, boolean blockingLOS, boolean walkable) {
		super(mapPosition, maxHP);
		isDestructible = destructible;
		isBlockingLOS = blockingLOS;
		isWalkable = walkable;
	}
	
	//endregion

}
