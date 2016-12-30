package net.codepoke.ai.challenge.hunterkiller.gameobjects.mapfeature;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.codepoke.ai.challenge.hunterkiller.HunterKillerState;
import net.codepoke.ai.challenge.hunterkiller.MapLocation;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.GameObject;

/**
 * Abstract class representing a feature on the {@link net.codepoke.ai.challenge.hunterkiller.Map
 * Map} of HunterKiller.
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
@Getter
@EqualsAndHashCode(callSuper = true)
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
  private boolean isDestructible = DEFAULT_DESTRUCTIBLE;
  
  /**
   * Whether or not this feature blocks Line of Sight for Units. This property is protected because
   * the subtype {@link Door} can change it.
   */
  protected boolean isBlockingLOS = DEFAULT_BLOCKING_LOS;
  
  /**
   * Whether or not Units can move over this feature.
   */
  private boolean isWalkable = DEFAULT_WALKABLE;
  
  //endregion
  
  //region Constructor
  
  /**
   * Constructs a new instance of a MapFeature with default HP.
   * 
   * {@link MapFeature#MapFeature(int, MapLocation, int, int, boolean, boolean, boolean)}
   */
  public MapFeature(int id, MapLocation mapLocation, boolean destructible, boolean blockingLOS, boolean walkable) {
    this(id, mapLocation, DEFAULT_HP, destructible, blockingLOS, walkable);
  }
  
  /**
   * Constructs a new instance of a MapFeature.
   * 
   * {@link MapFeature#MapFeature(int, MapLocation, int, int, boolean, boolean, boolean)}
   */
  public MapFeature(int id, MapLocation mapLocation, int maxHP, boolean destructible, boolean blockingLOS, boolean walkable) {
    this(id, mapLocation, maxHP, maxHP, destructible, blockingLOS, walkable);
  }
  
  /**
   * Constructs a new instance of a MapFeature.
   * 
   * @param id
   *          The MapFeature's unique identifier.
   * @param mapLocation
   *          The MapFeature's location on the Map.
   * @param maxHP
   *          The maximum amount of health points this MapFeature will have.
   * @param currentHP
   *          The current amount of health points this MapFeature has.
   * @param destructible
   *          Whether or not the MapFeature is destructible.
   * @param blockingLOS
   *          Whether or not the MapFeature blocks Line of Sight for Units.
   * @param walkable
   *          Whether or not Units can move over the MapFeature.
   */
  public MapFeature(int id, MapLocation mapLocation, int maxHP, int currentHP, boolean destructible, boolean blockingLOS, boolean walkable) {
    super(id, mapLocation, maxHP, currentHP);
    isDestructible = destructible;
    isBlockingLOS = blockingLOS;
    isWalkable = walkable;
  }
  
  //endregion
  
  //region Overridden methods
  
  @Override
  public boolean tick(HunterKillerState state) {
    return this.isDestructible() && this.getHpCurrent() <= 0;
  }
  
  //endregion
}
