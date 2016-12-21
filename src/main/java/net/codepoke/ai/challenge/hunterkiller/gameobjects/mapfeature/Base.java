package main.java.net.codepoke.ai.challenge.hunterkiller.gameobjects.mapfeature;

import lombok.Getter;
import main.java.net.codepoke.ai.challenge.hunterkiller.MapLocation;
import main.java.net.codepoke.ai.challenge.hunterkiller.enums.TileType;
import main.java.net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Unit;

/**
 * Class representing the base for a player. The base can spawn {@link Unit}s and is the ultimate
 * objective of the game.
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
@Getter
public class Base extends MapFeature {
  
  //region Constants
  
  /**
   * Health points for a Base.
   */
  public static final int BASE_MAX_HP = 50;
  /**
   * Bases are destructible.
   */
  public static final boolean BASE_DESTRUCTIBLE = true;
  /**
   * Bases block Line of Sight.
   */
  public static final boolean BASE_BLOCKING_LOS = true;
  /**
   * Bases can not be moved over.
   */
  public static final boolean BASE_WALKABLE = false;
  
  //endregion
  
  //region Properties
  
  /**
   * The location on the map where this Base spawns it's {@link Unit}s.
   */
  private MapLocation spawnLocation;
  
  //endregion
  
  //region Constructor
  
  /**
   * Constructs a new instance of a Base.
   * 
   * @param id
   *          The Base's unique identifier.
   * @param mapLocation
   *          The location of the Base on the map.
   * @param spawnLocation
   *          The location of the Base's spawn point.
   */
  public Base(int id, MapLocation mapLocation, MapLocation spawnLocation) {
    this(id, mapLocation, spawnLocation, BASE_MAX_HP, BASE_DESTRUCTIBLE, BASE_BLOCKING_LOS, BASE_WALKABLE);
  }
  
  /**
   * Constructs a new instance of a Base.
   * 
   * @param id
   *          The Base's unique identifier.
   * @param mapLocation
   *          The location of the Base on the map.
   * @param spawnLocation
   *          The location of the Base's spawn point.
   * @param maxHP
   *          The maximum amount of health points the Base has.
   * @param destructible
   *          Whether or not the Base is destructible.
   * @param blockingLOS
   *          Whether or not the Base blocks Line of Sight for Units.
   * @param walkable
   *          Whether or not Units can move over the Base.
   */
  public Base(int id, MapLocation mapLocation, MapLocation spawnLocation, int maxHP, boolean destructible, boolean blockingLOS, boolean walkable) {
    this(id, mapLocation, spawnLocation, maxHP, maxHP, destructible, blockingLOS, walkable);
  }
  
  /**
   * Constructs a new instance of a Base.
   * 
   * @param id
   *          The Base's unique identifier.
   * @param mapLocation
   *          The location of the Base on the map.
   * @param spawnLocation
   *          The location of the Base's spawn point.
   * @param maxHP
   *          The maximum amount of health points the Base has.
   * @param currentHP
   *          The current amount of health points the Base has.
   * @param destructible
   *          Whether or not the Base is destructible.
   * @param blockingLOS
   *          Whether or not the Base blocks Line of Sight for Units.
   * @param walkable
   *          Whether or not Units can move over the Base.
   */
  public Base(int id, MapLocation mapLocation, MapLocation spawnLocation, int maxHP, int currentHP, boolean destructible, boolean blockingLOS, boolean walkable) {
    super(id, mapLocation, maxHP, currentHP, destructible, blockingLOS, walkable);
    this.spawnLocation = new MapLocation(spawnLocation.getX(), spawnLocation.getY());
  }
  
  //endregion
  
  @Override
  public Base copy(int id) {
    return new Base(id, this.getLocation(), spawnLocation, this.getHpMax(), this.getHpCurrent(), this.isDestructible(), isBlockingLOS, this.isWalkable());
  }
  
  public String toString() {
    return TileType.BASE.txt;
  }
  
}
