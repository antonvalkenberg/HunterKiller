package net.codepoke.ai.challenge.hunterkiller.gameobjects.unit;

import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import net.codepoke.ai.challenge.hunterkiller.HunterKillerState;
import net.codepoke.ai.challenge.hunterkiller.MapLocation;
import net.codepoke.ai.challenge.hunterkiller.enums.Direction;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.GameObject;

/**
 * Abstract class representing a unit in HunterKiller.
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public abstract class Unit extends GameObject {
  
  //region Constants
  
  /**
   * Default health points for a Unit.
   */
  public static final int DEFAULT_UNIT_HP = 5;
  /**
   * Default orientation for a Unit.
   */
  public static final Direction DEFAULT_ORIENTATION = Direction.NORTH;
  /**
   * Default Field of View range for a Unit.
   */
  public static final int DEFAULT_FOV_RANGE = 3;
  /**
   * Default Field of View angle for a Unit.
   */
  public static final int DEFAULT_FOV_ANGLE = 90;
  /**
   * Default attack range for a Unit.
   */
  public static final int DEFAULT_ATTACK_RANGE = 4;
  /**
   * Default attack damage for a Unit.
   */
  public static final int DEFAULT_ATTACK_DAMAGE = 3;
  /**
   * Default cooldown for a Unit's special attack.
   */
  public static final int DEFAULT_SPECIAL_COOLDOWN = 0;
  /**
   * Default spawn cost for a Unit.
   */
  public static final int DEFAULT_SPAWN_COST = 5;
  /**
   * Default score that a Unit is worth.
   */
  public static final int DEFAULT_SCORE = 25;
  
  //endregion
  
  //region Properties
  
  /**
   * The ID of the player that has this Unit in it's squad.
   */
  private int squadPlayerID;
  
  /**
   * The Direction the Unit is facing.
   */
  @Setter
  private Direction orientation = DEFAULT_ORIENTATION;
  
  /**
   * The range (in squares) of the Unit's Field of View.
   */
  private int fieldOfViewRange = DEFAULT_FOV_RANGE;
  
  /**
   * The angle (in degrees) of the Unit's Field of View.
   */
  private int fieldOfViewAngle = DEFAULT_FOV_ANGLE;
  
  /**
   * The range (in squares) of the Unit's attacks.
   */
  private int attackRange = DEFAULT_ATTACK_RANGE;
  
  /**
   * The damage the Unit's attacks inflict.
   */
  private int attackDamage = DEFAULT_ATTACK_DAMAGE;
  
  /**
   * The remaining cool down time (in ticks) of the Unit's special attack.
   */
  private int specialAttackCooldown = DEFAULT_SPECIAL_COOLDOWN;
  
  /**
   * The cost to spawn an instance of this Unit.
   */
  private int spawnCost = DEFAULT_SPAWN_COST;
  
  /**
   * The score this Unit is worth when defeated by the opposing team.
   */
  private int scoreWorth = DEFAULT_SCORE;
  
  /**
   * The current field-of-view of this Unit.
   */
  private boolean[][] fieldOfView;
  
  //endregion
  
  //region Constructor
  
  /**
   * Constructs a new instance of a Unit with default HP.
   * 
   * {@link Unit#Unit(int, int, MapLocation, int, int, Direction, int, int, int, int, int, int, int)}
   */
  public Unit(int id, int spawningPlayerID, MapLocation mapLocation, Direction facing, int fovRange, int fovAngle, int attckRange, int attckDmg, int cooldown, int cost, int score) {
    this(id, spawningPlayerID, mapLocation, DEFAULT_UNIT_HP, facing, fovRange, fovAngle, attckRange, attckDmg, cooldown, cost, score);
  }
  
  /**
   * Constructs a new instance of a Unit with full health.
   * 
   * {@link Unit#Unit(int, int, MapLocation, int, int, Direction, int, int, int, int, int, int, int)}
   */
  public Unit(int id, int spawningPlayerID, MapLocation mapLocation, int maxHP, Direction facing, int fovRange, int fovAngle, int attckRange, int attckDmg, int cooldown, int cost, int score) {
    this(id, spawningPlayerID, mapLocation, maxHP, maxHP, facing, fovRange, fovAngle, attckRange, attckDmg, cooldown, cost, score);
  }
  
  /**
   * Constructs a new instance of a Unit.
   * 
   * @param id
   *          The Unit's unique identifier.
   * @param spawningPlayerID
   *          The ID of the Player that spawned this Unit.
   * @param mapLocation
   *          The Unit's location on the Map.
   * @param maxHP
   *          The Unit's maximum number of health points.
   * @param currentHP
   *          The Unit's current number of health points.
   * @param facing
   *          The Direction the Unit is facing.
   * @param fovRange
   *          The Unit's Field of View range.
   * @param fovAngle
   *          The Unit's Field of View angle.
   * @param attckRange
   *          The Unit's attack range.
   * @param attckDmg
   *          The Unit's attack damage.
   * @param cooldown
   *          The cooldown of the Unit's special attack.
   * @param cost
   *          The cost to spawn the Unit.
   * @param score
   *          The score the Unit is worth.
   */
  public Unit(int id, int spawningPlayerID, MapLocation mapLocation, int maxHP, int currentHP, Direction facing, int fovRange, int fovAngle, int attckRange, int attckDmg, int cooldown, int cost, int score) {
    super(id, mapLocation, maxHP, currentHP);
    squadPlayerID = spawningPlayerID;
    orientation = facing;
    fieldOfViewRange = fovRange;
    fieldOfViewAngle = fovAngle;
    attackRange = attckRange;
    attackDamage = attckDmg;
    specialAttackCooldown = cooldown;
    spawnCost = cost;
    scoreWorth = score;
  }
  
  //endregion
  
  //region Private methods
  
  /**
   * Calculates the location of the specified location, relative to this Unit's current location and
   * facing.
   * 
   * @param location
   *          The location to translate.
   * @return
   */
  private MapLocation getRelativeLocation(MapLocation location) {
    //TODO return the location of the specified location, relative to this Unit's current location and facing
    return new MapLocation(0, 0);
  }
  
  //endregion
  
  //region Protected methods
  
  /**
   * Set the currently remaining cooldown of this Unit's special attack.
   * 
   * @param cooldownRemaining
   *          The remaining cooldown.
   */
  protected void setSpecialAttackCooldown(int cooldownRemaining) {
    this.specialAttackCooldown = cooldownRemaining;
  }
  
  //endregion
  
  //region Public methods
  
  public abstract void startCooldown();
  
  /**
   * Reduces the cooldown for this unit's special attack.
   */
  public void reduceCooldown() {
    //Don't reduce anything if already at 0
    if(specialAttackCooldown > 0)
      specialAttackCooldown--;
  }
  
  /**
   * Whether or not a location is within this unit's field-of-view.
   * 
   * @param location
   *          The location.
   * @return
   */
  public boolean isInFieldOfView(MapLocation location) {
    MapLocation rl = getRelativeLocation(location);
    //Check internal fov representation
    return fieldOfView[rl.getX()][rl.getY()];
  }
  
  /**
   * Update the unit's internal field-of-view.
   * 
   * @param fieldOfView
   *          The collection of {@link MapLocation}s that are currently in the unit's field-of-view
   */
  public void updateFieldOfView(List<MapLocation> fieldOfView) {
    //TODO implement updating field-of-view in Unit
  }
  
  //endregion
  
  //region Overridden methods
  
  @Override
  public boolean tick(HunterKillerState state) {
    return this.getHpCurrent() <= 0;
  }
  
  //endregion
}
