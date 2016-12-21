package main.java.net.codepoke.ai.challenge.hunterkiller.gameobjects.unit;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import main.java.net.codepoke.ai.challenge.hunterkiller.MapLocation;
import main.java.net.codepoke.ai.challenge.hunterkiller.enums.Direction;
import main.java.net.codepoke.ai.challenge.hunterkiller.enums.TileType;

/**
 * Class representing an Infected unit in the game.
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class Infected extends Unit {
  
  //region Constants
  
  /**
   * Health points for an Infected.
   */
  public static final int INFECTED_MAX_HP = 10;
  /**
   * Field of View range for an Infected.
   */
  public static final int INFECTED_FOV_RANGE = 4;
  /**
   * Field of View angle for an Infected.
   */
  public static final int INFECTED_FOV_ANGLE = 360;
  /**
   * Attack range for an Infected.
   */
  public static final int INFECTED_ATTACK_RANGE = 1;
  /**
   * Attack damage for an Infected.
   */
  public static final int INFECTED_ATTACK_DAMAGE = 5;
  /**
   * Cooldown of an Infected's special attack.
   */
  public static final int INFECTED_COOLDOWN = 2;
  /**
   * Spawn cost for an Infected.
   */
  public static final int INFECTED_SPAWN_COST = 10;
  /**
   * The amount of points an Infected is worth when defeated.
   */
  public static final int INFECTED_SCORE = 25;
  
  //endregion
  
  //region Constructor
  
  /**
   * Constructs a new instance of an Infected on the specified location on the Map, facing the
   * specified direction.
   * 
   * @param id
   *          The Infected's unique identifier.
   * @param spawningPlayerID
   *          The ID of the Player that spawned this Infected.
   * @param mapLocation
   *          The Infected's location on the map.
   * @param facing
   *          The Direction the Infected is facing.
   */
  public Infected(int id, int spawningPlayerID, MapLocation mapLocation, Direction facing) {
    this(id, spawningPlayerID, mapLocation, INFECTED_MAX_HP, INFECTED_MAX_HP, facing, INFECTED_FOV_RANGE, INFECTED_FOV_ANGLE, INFECTED_ATTACK_RANGE, INFECTED_ATTACK_DAMAGE, INFECTED_COOLDOWN, INFECTED_SPAWN_COST, INFECTED_SCORE);
  }
  
  /**
   * Constructs a new instance of Infected.
   * 
   * @param id
   *          The Infected's unique identifier.
   * @param spawningPlayerID
   *          The ID of the Player that spawned this Infected.
   * @param mapLocation
   *          The Infected's location on the map.
   * @param maxHP
   *          The maximum amount of health points the Infected has.
   * @param currentHP
   *          The current amount of health points the Infected has.
   * @param facing
   *          The Direction the Infected is facing.
   * @param fovRange
   *          The range (in squares) of the Infected's Field of View.
   * @param fovAngle
   *          The angle (in degrees) of the Infected's Field of View.
   * @param attckRange
   *          The range (in squares) of the Infected's attacks.
   * @param attckDmg
   *          The damage the Infected's attacks inflict.
   * @param cooldown
   *          he remaining cool down time (in ticks) of the Infected's special attack.
   * @param cost
   *          The cost to spawn a Infected.
   * @param score
   *          The score the Infected is worth when defeated
   */
  public Infected(int id, int spawningPlayerID, MapLocation mapLocation, int maxHP, int currentHP, Direction facing, int fovRange, int fovAngle, int attckRange, int attckDmg, int cooldown, int cost, int score) {
    super(id, spawningPlayerID, mapLocation, maxHP, currentHP, facing, fovRange, fovAngle, attckRange, attckDmg, cooldown, cost, score);
  }
  
  //endregion
  
  @Override
  public Infected copy(int id) {
    return new Infected(id, this.getSquadPlayerID(), this.getLocation(), this.getHpMax(), this.getHpCurrent(), this.getOrientation(), this.getFieldOfViewRange(), this.getFieldOfViewAngle(), this.getAttackRange(), this.getAttackDamage(), this.getSpecialAttackCooldown(), this.getSpawnCost(), this.getScoreWorth());
  }
  
  public String toString() {
    return TileType.INFECTED.txt;
  }
  
}
