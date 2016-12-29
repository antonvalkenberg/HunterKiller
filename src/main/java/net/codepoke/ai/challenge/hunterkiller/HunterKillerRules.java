package net.codepoke.ai.challenge.hunterkiller;

import java.util.List;

import com.badlogic.gdx.utils.Array;

import net.codepoke.ai.GameRules;
import net.codepoke.ai.GameRules.Result.Ranking;
import net.codepoke.ai.challenge.hunterkiller.enums.BaseOrderType;
import net.codepoke.ai.challenge.hunterkiller.enums.Direction;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.GameObject;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.mapfeature.MapFeature;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Infected;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Medic;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Soldier;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Unit;
import net.codepoke.ai.challenge.hunterkiller.orders.BaseOrder;
import net.codepoke.ai.challenge.hunterkiller.orders.HunterKillerOrder;
import net.codepoke.ai.challenge.hunterkiller.orders.UnitOrder;

/**
 * Class representing the game logic for HunterKiller.
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
public class HunterKillerRules implements GameRules<HunterKillerState, HunterKillerAction> {
  
  /**
   * Handles the specified action. Also ends the player's turn and checks for a completed game
   * state.
   */
  @Override
  public Result handle(HunterKillerState state, HunterKillerAction action) {
    //Check to make sure only the active player can perform an action
    if(action.getActingPlayerID() != state.getActivePlayerID()) {
      return new Result(false, false, null, "Invalid action", "Player performing the action is not the active player.");
    }
    //Check to make sure the round numbers match
    if(action.getCurrentRound() != state.getCurrentRound()) {
      return new Result(false, false, null, "Invalid action", "Round number of the action did not match the State's.");
    }
    
    //Perform the action requested by the player
    Result actionResult = performAction(state, action);
    //End the player's turn
    state.endPlayerTurn();
    
    //Check if the game has ended
    if(state.isDone()) {
      //Sort the players by score
      Array<Player> players = new Array<Player>(state.getPlayers());
      players.sort();
      //Create a ranking
      Array<Ranking> ranking = new Array<Ranking>();
      for(int i = 0; i < players.size; i++) {
        Player p = players.get(i);
        Ranking r = new Ranking(p.getID(), i);
        ranking.add(r);
      }
      //Return the final result
      return new Result(true, true, ranking, "Game completed", "I say GG, but it wasnt GG. It was BG ... Delirium.");
    }
    
    //Return the result from the player's action
    return actionResult;
  }
  
  /**
   * Performs the action on the current state.
   * 
   * @param state
   *          The current state.
   * @param action
   *          The action to be performed.
   * @return The {@link Result} of the action.
   */
  private Result performAction(HunterKillerState state, HunterKillerAction action) {
    int failCount = 0;
    Player actingPlayer = state.getPlayer(action.getActingPlayerID());
    Map map = state.getMap();
    //Go through the list of orders
    for(HunterKillerOrder order : action.getOrders()) {
      //Check which type of order we are dealing with
      if(order instanceof BaseOrder) {
        BaseOrder baseOrder = (BaseOrder)order;
        //Try to spawn the Unit
        if(!spawnUnit(map, actingPlayer, baseOrder.getOrderType()))
          failCount++;
      }
      else if(order instanceof UnitOrder) {
        UnitOrder unitOrder = (UnitOrder)order;
        switch(unitOrder.getOrderType()) {
          case ROTATE_EAST:
            //Try to rotate the unit east
            if(!rotateUnit(map, unitOrder.getObjectID(), Direction.EAST))
              failCount++;
            break;
          case ROTATE_WEST:
            //Try to rotate the unit west
            if(!rotateUnit(map, unitOrder.getObjectID(), Direction.WEST))
              failCount++;
            break;
          case MOVE_NORTH:
          case MOVE_EAST:
          case MOVE_SOUTH:
          case MOVE_WEST:
            //Try to move the unit
            if(!moveUnit(map, unitOrder))
              failCount++;
            break;
          case ATTACK:
            //Try to execute the ordered attack
            if(!attackLocation(map, unitOrder))
              failCount++;
            break;
          case ATTACK_SPECIAL:
            //Try to execute the ordered attack
            if(!attackSpecial(map, unitOrder))
              failCount++;
            break;
          default:
            System.err.println("WARNING: Unsupported UnitOrderType.");
            failCount++;
            break;
        }
      }
      else {
        System.err.println("WARNING: Unsupported OrderType.");
        failCount++;
      }
    }
    //Return the action as accepted, but add a count of how many orders failed, if any did.
    return new Result(true, false, null, "Action accepted", failCount > 0 ? String.format("%d orders ignored", failCount) : "");
  }
  
  /**
   * Spawn a Unit.
   * 
   * @param map
   *          The map to spawn on.
   * @param player
   *          The player to spawn a unit for.
   * @param spawnType
   *          The type of order that was issued.
   * @return Whether or not the spawning was successful.
   */
  private boolean spawnUnit(Map map, Player player, BaseOrderType spawnType) {
    boolean spawnSuccess = false;
    MapLocation spawnlocation = player.getBase().getSpawnLocation();
    //The direction a unit faces when they spawn will be in line with the direction the spawn location is relative to the base.
    Direction spawnDirection = MapLocation.getDirectionTo(player.getBase().getLocation(), spawnlocation);
    //Make sure we got a direction
    if(spawnDirection == null) {
      System.err.println("WARNING: Spawn location is not on a cardinal direction relative to the base.");
      return false;
    }
    //Get the correct costs
    int spawnCosts = -1;
    switch(spawnType) {
      case SPAWN_INFECTED:
        spawnCosts = Infected.INFECTED_SPAWN_COST;
        break;
      case SPAWN_MEDIC:
        spawnCosts = Medic.MEDIC_SPAWN_COST;
        break;
      case SPAWN_SOLDIER:
        spawnCosts = Soldier.SOLDIER_SPAWN_COST;
        break;
      default:
        System.err.println("WARNING: Unsupported BaseOrderType.");
        return false;
    }
    //Check if the player has enough resources
    if(player.getResource() >= spawnCosts) {
      //Check if the spawn location is available
      if(map.isTraversable(spawnlocation)) {
        //Charge the costs
        player.resource -= spawnCosts;
        //Create a new Unit of the correct type
        Unit unit;
        switch(spawnType) {
          case SPAWN_INFECTED:
            unit = new Infected(map.requestNewGameObjectID(), player.getID(), spawnlocation, spawnDirection);
            break;
          case SPAWN_MEDIC:
            unit = new Medic(map.requestNewGameObjectID(), player.getID(), spawnlocation, spawnDirection);
            break;
          case SPAWN_SOLDIER:
            unit = new Soldier(map.requestNewGameObjectID(), player.getID(), spawnlocation, spawnDirection);
            break;
          default:
            System.err.println("WARNING: Unsupported BaseOrderType.");
            return false;
        }
        //Place the unit on the map
        spawnSuccess = map.place(map.toPosition(spawnlocation), unit);
        //Add the unit to the Player's squad if successfully spawned
        if(spawnSuccess)
          player.addUnitToSquad(unit);
      }
      else {
        //Silently fail this order
        return false;
      }
    }
    else {
      //Silently fail this order
      return false;
    }
    //Return
    return spawnSuccess;
  }
  
  /**
   * Rotate a Unit.
   * 
   * @param map
   *          The map that the unit is on.
   * @param unitID
   *          The unique identifier of the unit.
   * @param rotationalDirection
   *          The direction to rotate the unit in. Note: only EAST and WEST are functional
   *          rotational directions.
   * @return Whether or not the rotation succeeded.
   */
  private boolean rotateUnit(Map map, int unitID, Direction rotationalDirection) {
    boolean rotationalSuccess = false;
    //Check if there is a object on the map with the specified ID.
    GameObject object = map.getObject(unitID);
    //Check if an object was found, and that object is a Unit.
    if(object == null)
      return false;
    if(!(object instanceof Unit))
      return false;
    //Rotate the unit in the specified direction
    Unit unit = (Unit)object;
    switch(rotationalDirection) {
      case EAST:
        unit.setOrientation(unit.getOrientation().rotateEast());
        rotationalSuccess = true;
        break;
      case WEST:
        unit.setOrientation(unit.getOrientation().rotateWest());
        rotationalSuccess = true;
        break;
      default:
        System.err.println("WARNING: Unsupported Rotational Direction.");
        return false;
    }
    //Return
    return rotationalSuccess;
  }
  
  /**
   * Move a Unit.
   * 
   * @param map
   *          The map to move the unit on.
   * @param moveOrder
   *          The order.
   * @return Whether or not the unit was successfully moved.
   */
  private boolean moveUnit(Map map, UnitOrder moveOrder) {
    boolean movementSuccess = false;
    //Check if there is a object on the map with the specified ID.
    GameObject object = map.getObject(moveOrder.getObjectID());
    //Check if an object was found, and that object is a Unit.
    if(object == null)
      return false;
    if(!(object instanceof Unit))
      return false;
    //Check if the ordered move is possible
    if(!map.isMovePossible(map.getObjectLocation(moveOrder.getObjectID()), moveOrder))
      return false;
    //Execute the move
    movementSuccess = map.move(moveOrder.getTargetLocation(), object);
    //Return
    return movementSuccess;
  }
  
  /**
   * Attack a location. Any {@link Unit} or {@link MapFeature} at the location will be damaged.
   * 
   * @param map
   *          The map the attack is being performed on.
   * @param attackOrder
   *          The order.
   * @return Whether or not the attack was successfully executed.
   */
  private boolean attackLocation(Map map, UnitOrder attackOrder) {
    boolean attackSuccess = false;
    //Check if there is a object on the map with the specified ID
    GameObject object = map.getObject(attackOrder.getObjectID());
    //Check if an object was found, and that object is a Unit
    if(object == null)
      return false;
    if(!(object instanceof Unit))
      return false;
    Unit unit = (Unit)object;
    //Check if the target location is in the Unit's field of view
    if(!unit.isInFieldOfView(attackOrder.getTargetLocation()))
      return false;
    //Check if the target location is within the Unit's attack range
    if(unit.getAttackRange() < MapLocation.getManhattanDist(unit.getLocation(), attackOrder.getTargetLocation()))
      return false;
    //Tell the map that the target location is being attacked for X damage
    attackSuccess = map.attackLocation(attackOrder.getTargetLocation(), unit.getAttackDamage());
    //Return
    return attackSuccess;
  }
  
  /**
   * Perform a special attack on a location. The effect of these attacks differ based on the type of
   * {@link Unit} performing the attack.
   * 
   * @param map
   *          The map the attack is being performed on.
   * @param attackOrder
   *          The order.
   * @return Whether or not the attack was successfully executed.
   */
  private boolean attackSpecial(Map map, UnitOrder attackOrder) {
    boolean attackSuccess = false;
    //Check if there is a object on the map with the specified ID.
    GameObject object = map.getObject(attackOrder.getObjectID());
    //Check if an object was found, and that object is a Unit.
    if(object == null)
      return false;
    if(!(object instanceof Unit))
      return false;
    Unit unit = (Unit)object;
    //Check if the Unit's special attack has cooled down
    if(unit.getSpecialAttackCooldown() > 0)
      return false;
    //Check if the target location is in the Unit's field of view.
    if(!unit.isInFieldOfView(attackOrder.getTargetLocation()))
      return false;
    //Check if the target location is within the Unit's attack range
    if(unit.getAttackRange() < MapLocation.getManhattanDist(unit.getLocation(), attackOrder.getTargetLocation()))
      return false;
    //Execute the special action, this is different per Unit type
    if(object instanceof Infected) {
      //The special attack of an infected can't actually be ordered, since it triggers on kill
      //TODO Implement Infected's triggered ability
    }
    else if(object instanceof Medic) {
      //The special attack of a medic heals a unit for an amount
      Unit target = (Unit)map.getMapContent()[map.toPosition(attackOrder.getTargetLocation())][Map.INTERNAL_MAP_UNIT_INDEX];
      if(target != null) {
        target.increaseHP(Medic.MEDIC_SPECIAL_HEAL);
        attackSuccess = true;
      }
    }
    else if(object instanceof Soldier) {
      //The special attack of a soldier is a grenade that does damage in an area
      List<MapLocation> areaOfEffect = map.getAreaAround(attackOrder.getTargetLocation(), true);
      for(MapLocation location : areaOfEffect) {
        //Call an attack on each location inside the area of effect
        if(map.attackLocation(location, Soldier.SOLDIER_SPECIAL_DAMAGE)) {
          //Report success if at least one of the locations is successfully attacked
          attackSuccess = true;
        }
      }
    }
    //If we executed the special action, start the cooldown
    ((Unit)object).startCooldown();
    //Return
    return attackSuccess;
  }
  
}
