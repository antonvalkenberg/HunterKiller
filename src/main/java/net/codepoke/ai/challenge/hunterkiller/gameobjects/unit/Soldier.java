package main.java.net.codepoke.ai.challenge.hunterkiller.gameobjects.unit;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import main.java.net.codepoke.ai.challenge.hunterkiller.MapLocation;
import main.java.net.codepoke.ai.challenge.hunterkiller.enums.Direction;
import main.java.net.codepoke.ai.challenge.hunterkiller.enums.TileType;

/**
 * Class representing a Soldier unit in the game.
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class Soldier extends Unit {
  
  //region Constants
  
  /**
   * Health points for a Soldier.
   */
  public static final int SOLDIER_MAX_HP = 5;
  /**
   * Field of View range for a Soldier.
   */
  public static final int SOLDIER_FOV_RANGE = 3;
  /**
   * Field of View angle for a Soldier.
   */
  public static final int SOLDIER_FOV_ANGLE = 90;
  /**
   * Attack range for a Soldier.
   */
  public static final int SOLDIER_ATTACK_RANGE = 4;
  /**
   * Attack damage for a Soldier.
   */
  public static final int SOLDIER_ATTACK_DAMAGE = 3;
  /**
   * Cooldown of a Soldier's special attack
   */
  public static final int SOLDIER_COOLDOWN = 20;
  /**
   * Spawn cost for a Soldier.
   */
  public static final int SOLDIER_SPAWN_COST = 5;
  /**
   * The amount of points a Soldier is worth when defeated.
   */
  public static final int SOLDIER_SCORE = 25;
  
  //endregion
  
  //region Constructor
  
  /**
   * Constructs a new instance of a Soldier on the specified location on the Map, facing the
   * specified direction.
   * 
   * @param id
   *          The Soldier's unique identifier.
   * @param spawningPlayerID
   *          The ID of the Player that spawned this Soldier.
   * @param mapLocation
   *          The Soldier's location on the map.
   * @param facing
   *          The Direction the Soldier is facing.
   */
  public Soldier(int id, int spawningPlayerID, MapLocation mapLocation, Direction facing) {
    this(id, spawningPlayerID, mapLocation, SOLDIER_MAX_HP, SOLDIER_MAX_HP, facing, SOLDIER_FOV_RANGE, SOLDIER_FOV_ANGLE, SOLDIER_ATTACK_RANGE, SOLDIER_ATTACK_DAMAGE, SOLDIER_COOLDOWN, SOLDIER_SPAWN_COST, SOLDIER_SCORE);
  }
  
  /**
   * Constructs a new instance of Soldier.
   * 
   * @param id
   *          The Soldier's unique identifier.
   * @param spawningPlayerID
   *          The ID of the Player that spawned this Soldier.
   * @param mapLocation
   *          The Soldier's location on the map.
   * @param maxHP
   *          The maximum amount of health points the Soldier has.
   * @param currentHP
   *          The current amount of health points the Soldier has.
   * @param facing
   *          The Direction the Soldier is facing.
   * @param fovRange
   *          The range (in squares) of the Soldier's Field of View.
   * @param fovAngle
   *          The angle (in degrees) of the Soldier's Field of View.
   * @param attckRange
   *          The range (in squares) of the Soldier's attacks.
   * @param attckDmg
   *          The damage the Soldier's attacks inflict.
   * @param cooldown
   *          he remaining cool down time (in ticks) of the Soldier's special attack.
   * @param cost
   *          The cost to spawn a Soldier.
   * @param score
   *          The score the Soldier is worth when defeated
   */
  public Soldier(int id, int spawningPlayerID, MapLocation mapLocation, int maxHP, int currentHP, Direction facing, int fovRange, int fovAngle, int attckRange, int attckDmg, int cooldown, int cost, int score) {
    super(id, spawningPlayerID, mapLocation, maxHP, currentHP, facing, fovRange, fovAngle, attckRange, attckDmg, cooldown, cost, score);
  }
  
  //endregion
  
  @Override
  public Soldier copy(int id) {
    return new Soldier(id, this.getSquadPlayerID(), this.getLocation(), this.getHpMax(), this.getHpCurrent(), this.getOrientation(), this.getFieldOfViewRange(), this.getFieldOfViewAngle(), this.getAttackRange(), this.getAttackDamage(), this.getSpecialAttackCooldown(), this.getSpawnCost(), this.getScoreWorth());
  }
  
  public String toString() {
    return TileType.SOLDIER.txt;
  }
}
