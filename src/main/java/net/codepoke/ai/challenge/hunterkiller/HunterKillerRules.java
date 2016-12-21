package main.java.net.codepoke.ai.challenge.hunterkiller;

import main.java.net.codepoke.ai.challenge.hunterkiller.actions.BaseOrder;
import main.java.net.codepoke.ai.challenge.hunterkiller.actions.UnitOrder;
import main.java.net.codepoke.ai.challenge.hunterkiller.enums.Direction;
import main.java.net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Infected;
import main.java.net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Medic;
import main.java.net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Soldier;
import net.codepoke.ai.GameRules;
import net.codepoke.ai.GameRules.Result.Ranking;
import com.badlogic.gdx.utils.Array;

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
        MapLocation spawnlocation = actingPlayer.getBase().getSpawnLocation();
        switch(baseOrder.getOrderType()) {
          case SPAWN_INFECTED:
            //Check if the player has enough resources
            if(actingPlayer.getResource() >= Infected.INFECTED_SPAWN_COST) {
              //Check if the spawn location is available
              if(map.isTraversable(spawnlocation)) {
                //Charge the costs
                actingPlayer.resource -= Infected.INFECTED_SPAWN_COST;
                //Create a new Infected
                //TODO get proper direction in this call
                Infected unit = new Infected(map.requestNewGameObjectID(), actingPlayer.getID(), spawnlocation, Direction.NORTH);
                //Place the unit on the map
                map.place(map.toPosition(spawnlocation), unit);
                //Add the unit to the Player's squad
                actingPlayer.addUnitToSquad(unit);
              }
              else {
                //Silently fail this order
                failCount++;
              }
            }
            else {
              //Silently fail this order
              failCount++;
            }
            break;
          case SPAWN_MEDIC:
            //Check if the player has enough resources
            if(actingPlayer.getResource() >= Medic.MEDIC_SPAWN_COST) {
              //Check if the spawn location is available
              if(map.isTraversable(spawnlocation)) {
                //Charge the costs
                actingPlayer.resource -= Medic.MEDIC_SPAWN_COST;
                //Create a new Infected
                //TODO get proper direction in this call
                Medic unit = new Medic(map.requestNewGameObjectID(), actingPlayer.getID(), spawnlocation, Direction.NORTH);
                //Place the unit on the map
                map.place(map.toPosition(spawnlocation), unit);
                //Add the unit to the Player's squad
                actingPlayer.addUnitToSquad(unit);
              }
              else {
                //Silently fail this order
                failCount++;
              }
            }
            else {
              //Silently fail this order
              failCount++;
            }
            break;
          case SPAWN_SOLDIER:
            //Check if the player has enough resources
            if(actingPlayer.getResource() >= Soldier.SOLDIER_SPAWN_COST) {
              //Check if the spawn location is available
              if(map.isTraversable(spawnlocation)) {
                //Charge the costs
                actingPlayer.resource -= Soldier.SOLDIER_SPAWN_COST;
                //Create a new Infected
                //TODO get proper direction in this call
                Soldier unit = new Soldier(map.requestNewGameObjectID(), actingPlayer.getID(), spawnlocation, Direction.NORTH);
                //Place the unit on the map
                map.place(map.toPosition(spawnlocation), unit);
                //Add the unit to the Player's squad
                actingPlayer.addUnitToSquad(unit);
              }
              else {
                //Silently fail this order
                failCount++;
              }
            }
            else {
              //Silently fail this order
              failCount++;
            }
            break;
          default:
            System.err.println("WARNING: Unsupported BaseOrderType.");
            failCount++;
            break;
        }
      }
      else if(order instanceof UnitOrder) {
        UnitOrder unitOrder = (UnitOrder)order;
        switch(unitOrder.getOrderType()) {
          case ROTATE_EAST:
            break;
          case ROTATE_WEST:
            break;
          case MOVE_NORTH:
            break;
          case MOVE_EAST:
            break;
          case MOVE_SOUTH:
            break;
          case MOVE_WEST:
            break;
          case ATTACK:
            break;
          case ATTACK_SPECIAL:
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
    
    return new Result();
  }
}
