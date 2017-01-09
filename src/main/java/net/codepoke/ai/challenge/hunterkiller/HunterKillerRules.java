package net.codepoke.ai.challenge.hunterkiller;

import java.util.List;

import net.codepoke.ai.GameRules;
import net.codepoke.ai.GameRules.Result.Ranking;
import net.codepoke.ai.challenge.hunterkiller.enums.BaseOrderType;
import net.codepoke.ai.challenge.hunterkiller.enums.Direction;
import net.codepoke.ai.challenge.hunterkiller.enums.Direction.Rotation;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.GameObject;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.mapfeature.Base;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.mapfeature.MapFeature;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Infected;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Medic;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Soldier;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Unit;
import net.codepoke.ai.challenge.hunterkiller.orders.BaseOrder;
import net.codepoke.ai.challenge.hunterkiller.orders.HunterKillerOrder;
import net.codepoke.ai.challenge.hunterkiller.orders.UnitOrder;

import com.badlogic.gdx.utils.Array;

/**
 * Class representing the game logic for HunterKiller.
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
public class HunterKillerRules
		implements GameRules<HunterKillerState, HunterKillerAction> {

	/**
	 * Handles the specified action. Also ends the player's turn and checks for a completed game
	 * state.
	 */
	@Override
	public Result handle(HunterKillerState state, HunterKillerAction action) {
		// Check to make sure only the active player can perform an action
		if (action.getActingPlayerID() != state.getCurrentPlayer()) {
			return new Result(false, false, null, "Invalid action", "Player performing the action is not the active player.");
		}
		// Check to make sure the round numbers match
		if (action.getCurrentRound() != state.getCurrentRound()) {
			return new Result(false, false, null, "Invalid action", "Round number of the action did not match the State's.");
		}

		// Perform the action requested by the player
		Result actionResult = performAction(state, action);
		// End the player's turn
		state.endPlayerTurn();

		// Check if the game has ended
		if (state.isDone()) {
			// Sort the players by score
			Array<Player> players = new Array<Player>(state.getPlayers());
			players.sort();
			// Create a ranking
			Array<Ranking> ranking = new Array<Ranking>();
			for (int i = 0; i < players.size; i++) {
				Player p = players.get(i);
				Ranking r = new Ranking(p.getID(), i);
				ranking.add(r);
			}
			// Return the final result
			return new Result(true, true, ranking, "Game completed", "GG");
		}

		// Return the result from the player's action
		return actionResult;
	}

	/**
	 * Performs the action on the current state.
	 * 
	 * @param state
	 *            The current state.
	 * @param action
	 *            The action to be performed.
	 * @return The {@link Result} of the action.
	 */
	private Result performAction(HunterKillerState state, HunterKillerAction action) {
		int failCount = 0;
		StringBuilder failures = new StringBuilder();
		Player actingPlayer = state.getPlayer(action.getActingPlayerID());
		Map map = state.getMap();
		// Get the orders contained in the action
		List<HunterKillerOrder> orders = action.getOrders();
		// Sort the orders by their natural ordering
		orders.sort(null);
		// Go through the sorted list of orders
		for (HunterKillerOrder order : orders) {
			// Check which type of order we are dealing with
			if (order instanceof BaseOrder) {
				BaseOrder baseOrder = (BaseOrder) order;
				// Try to spawn the Unit
				if (!spawnUnit(map, actingPlayer, baseOrder.getOrderType(), failures))
					failCount++;
			} else if (order instanceof UnitOrder) {
				UnitOrder unitOrder = (UnitOrder) order;
				switch (unitOrder.getOrderType()) {
				case ROTATE_CLOCKWISE:
					// Try to rotate the unit east
					if (!rotateUnit(map, unitOrder.getObjectID(), Rotation.CLOCKWISE, failures))
						failCount++;
					break;
				case ROTATE_COUNTER_CLOCKWISE:
					// Try to rotate the unit west
					if (!rotateUnit(map, unitOrder.getObjectID(), Rotation.COUNTER_CLOCKWISE, failures))
						failCount++;
					break;
				case MOVE_NORTH:
				case MOVE_EAST:
				case MOVE_SOUTH:
				case MOVE_WEST:
					// Try to move the unit
					if (!moveUnit(map, unitOrder, failures))
						failCount++;
					break;
				case ATTACK:
					// Try to execute the ordered attack
					if (!attackLocation(state, actingPlayer, unitOrder, failures))
						failCount++;
					break;
				case ATTACK_SPECIAL:
					// Try to execute the ordered attack
					if (!attackSpecial(map, actingPlayer, unitOrder, failures))
						failCount++;
					break;
				default:
					failures.append(String.format("WARNING: Unsupported UnitOrderType.%n"));
					failCount++;
					break;
				}
			} else {
				failures.append(String.format("WARNING: Unsupported OrderType.%n"));
				failCount++;
			}
		}
		// Return the action as accepted, but add a count of how many orders failed, if any did.
		return new Result(true, false, null, "Action accepted", failCount > 0 ? String.format(	"%d orders ignored, Reasons:%n%s",
																								failCount,
																								failures.toString()) : "");
	}

	/**
	 * Spawn a Unit.
	 * 
	 * @param map
	 *            The map to spawn on.
	 * @param player
	 *            The player to spawn a unit for.
	 * @param spawnType
	 *            The type of order that was issued.
	 * @param failures
	 *            The StringBuilder to append any failures to.
	 * @return Whether or not the spawning was successful.
	 */
	private boolean spawnUnit(Map map, Player player, BaseOrderType spawnType, StringBuilder failures) {
		boolean spawnSuccess = false;
		Base base = (Base) map.getObject(player.getBaseID());
		MapLocation spawnlocation = base.getSpawnLocation();
		// The direction a unit faces when they spawn will be in line with the direction the spawn location is relative
		// to the base.
		Direction spawnDirection = MapLocation.getDirectionTo(base.getLocation(), spawnlocation);
		// Make sure we got a direction
		if (spawnDirection == null) {
			failures.append(String.format("Spawn Failure: Spawn location is not on a cardinal direction relative to the base.%n"));
			return false;
		}
		// Get the correct costs
		int spawnCosts = -1;
		switch (spawnType) {
		case SPAWN_INFECTED:
			spawnCosts = Constants.INFECTED_SPAWN_COST;
			break;
		case SPAWN_MEDIC:
			spawnCosts = Constants.MEDIC_SPAWN_COST;
			break;
		case SPAWN_SOLDIER:
			spawnCosts = Constants.SOLDIER_SPAWN_COST;
			break;
		default:
			failures.append(String.format("Spawn Failure: Unsupported BaseOrderType.%n"));
			return false;
		}
		// Check if the player has enough resources
		if (player.getResource() >= spawnCosts) {
			// Check if the spawn location is available
			if (map.isTraversable(spawnlocation)) {
				// Charge the costs
				player.setResource(player.resource - spawnCosts);
				// Create a new Unit of the correct type
				Unit unit;
				switch (spawnType) {
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
					failures.append(String.format("Spawn Failure: Unsupported BaseOrderType.%n"));
					return false;
				}
				// Place the unit on the map
				spawnSuccess = map.place(map.toPosition(spawnlocation), unit);
				// Add the unit to the Player's squad if successfully spawned and update it's Field-of-View
				if (spawnSuccess) {
					player.addUnitToSquad(unit.getID());
					unit.updateFieldOfView(map.getFieldOfView(unit));
				}
			} else {
				failures.append(String.format("Spawn Failure: Spawn location is not traversable, potential causes: location is off grid, MapFeature at location is not walkable or s Unit is present at location.%n"));
				return false;
			}
		} else {
			failures.append(String.format("Spawn Failure: Insufficient resources.%n"));
			return false;
		}
		// Return
		return spawnSuccess;
	}

	/**
	 * Rotate a Unit.
	 * 
	 * @param map
	 *            The map that the unit is on.
	 * @param unitID
	 *            The unique identifier of the unit.
	 * @param rotation
	 *            The direction to rotate the unit in.
	 * @param failures
	 *            The StringBuilder to append any failures to.
	 * @return Whether or not the rotation succeeded.
	 */
	private boolean rotateUnit(Map map, int unitID, Rotation rotation, StringBuilder failures) {
		// Check if there is a object on the map with the specified ID.
		GameObject object = map.getObject(unitID);
		// Check if an object was found, and that object is a Unit.
		if (object == null) {
			failures.append(String.format("Rotate Failure: No Unit found with specified ID (%d).%n", unitID));
			return false;
		}
		if (!(object instanceof Unit)) {
			failures.append(String.format("Rotate Failure: Cannot rotate non-Unit object.%n"));
			return false;
		}
		// Rotate the unit in the specified direction
		Unit unit = (Unit) object;
		unit.setOrientation(Direction.rotate(unit.getOrientation(), rotation));
		// Return
		return true;
	}

	/**
	 * Move a Unit.
	 * 
	 * @param map
	 *            The map to move the unit on.
	 * @param moveOrder
	 *            The order.
	 * @param failures
	 *            The StringBuilder to append any failures to.
	 * @return Whether or not the unit was successfully moved.
	 */
	private boolean moveUnit(Map map, UnitOrder moveOrder, StringBuilder failures) {
		boolean movementSuccess = false;
		// Check if there is a object on the map with the specified ID.
		GameObject object = map.getObject(moveOrder.getObjectID());
		// Check if an object was found, and that object is a Unit.
		if (object == null) {
			failures.append(String.format("Movement Failure: No Unit found with specified ID (%d).%n", moveOrder.getObjectID()));
			return false;
		}
		if (!(object instanceof Unit)) {
			failures.append(String.format("Movement Failure: Cannot move non-Unit object.%n"));
			return false;
		}
		// Check if a target for the move has been set
		if (moveOrder.getTargetLocation() == null) {
			failures.append(String.format("Movement Failure: No target location set.%n"));
			return false;
		}
		// Check if the ordered move is possible
		if (!map.isMovePossible(map.getObjectLocation(moveOrder.getObjectID()), moveOrder)) {
			failures.append(String.format("Movement Failure: Illegal move supplied.%n"));
			return false;
		}
		// Execute the move
		movementSuccess = map.move(moveOrder.getTargetLocation(), object);
		// Return
		return movementSuccess;
	}

	/**
	 * Attack a location. Any {@link Unit} or {@link MapFeature} at the location will be damaged.
	 * 
	 * @param state
	 *            The current state of the game.
	 * @param player
	 *            The player ordering this attack.
	 * @param attackOrder
	 *            The order.
	 * @param failures
	 *            The StringBuilder to append any failures to.
	 * @return Whether or not the attack was successfully executed.
	 */
	private boolean attackLocation(HunterKillerState state, Player player, UnitOrder attackOrder, StringBuilder failures) {
		boolean attackSuccess = false;

		Map map = state.getMap();
		// Check if there is a object on the map with the specified ID
		GameObject object = map.getObject(attackOrder.getObjectID());
		// Check if an object was found, and that object is a Unit
		if (object == null) {
			failures.append(String.format("Attack Failure: No Unit found with specified ID (%d).%n", attackOrder.getObjectID()));
			return false;
		}
		if (!(object instanceof Unit)) {
			failures.append(String.format("Attack Failure: Cannot attack with non-Unit object.%n"));
			return false;
		}

		Unit unit = (Unit) object;
		MapLocation targetLocation = attackOrder.getTargetLocation();
		// Check if the target location is in the Player's combined field of view
		if (!player.getCombinedFieldOfView(map)
					.contains(targetLocation)) {
			failures.append(String.format("Attack Failure: Target location is not in your Field-of-View.%n"));
			return false;
		}
		// Check if the target location is within the Unit's attack range
		if (unit.getAttackRange() < MapLocation.getManhattanDist(unit.getLocation(), targetLocation)) {
			failures.append(String.format("Attack Failure: Target location outside of attack range.%n"));
			return false;
		}

		// Tell the map that the target location is being attacked for X damage
		attackSuccess = map.attackLocation(targetLocation, unit.getAttackDamage());

		// Check if we need to trigger an Infected's special attack.
		// Several conditions need to hold: (in order of most likely to break out of the statement)
		// - An Infected was the source of the attack
		// - There is a unit on the targeted location
		// - The unit is now dead
		// - The Infected's special attack is not on cooldown
		// - The attack succeeded
		Unit targetUnit = map.getUnitAtLocation(targetLocation);
		if (unit instanceof Infected && targetUnit != null && targetUnit.getHpCurrent() <= 0 && unit.getSpecialAttackCooldown() == 0
			&& attackSuccess) {
			// Remove the dead unit
			Unit deadUnit = map.getUnitAtLocation(targetLocation);
			attackSuccess = map.remove(map.toPosition(targetLocation), deadUnit);
			if (attackSuccess) {
				// Remove the unit from it's owners squad
				state.getPlayer(deadUnit.getControllingPlayerID())
						.removeUnitFromSquad(deadUnit.getID());
				// Award points to the player
				awardPointsForUnitDeath(player, targetUnit);
				// Spawn a new Infected, on the same team as the Infected that performed this attack
				Infected spawn = new Infected(map.requestNewGameObjectID(), player.getID(), targetLocation, unit.getOrientation());
				attackSuccess = map.place(map.toPosition(targetLocation), spawn);
				// Add the newly spawned unit to the player's squad
				if (attackSuccess) {
					player.addUnitToSquad(spawn.getID());
					unit.updateFieldOfView(map.getFieldOfView(unit));
					// If we executed the special action, start the cooldown
					unit.startCooldown();
				}
			}
		}
		// Otherwise, check if there was a Unit on the targeted location, and if it is currently dead
		else if (targetUnit != null && targetUnit.getHpCurrent() <= 0) {
			// Award points to the player
			awardPointsForUnitDeath(player, targetUnit);
		}

		// Return
		return attackSuccess;
	}

	/**
	 * Perform a special attack on a location. The effect of these attacks differ based on the type of {@link Unit}
	 * performing the attack.
	 * 
	 * {@link HunterKillerRules#attackLocation(Map, Player, UnitOrder, StringBuilder)}
	 */
	private boolean attackSpecial(Map map, Player player, UnitOrder attackOrder, StringBuilder failures) {
		boolean attackSuccess = false;
		// Check if there is a object on the map with the specified ID.
		GameObject object = map.getObject(attackOrder.getObjectID());
		// Check if an object was found, and that object is a Unit.
		if (object == null) {
			failures.append(String.format("Special Attack Failure: No Unit found with specified ID (%d).%n", attackOrder.getObjectID()));
			return false;
		}
		if (!(object instanceof Unit)) {
			failures.append(String.format("Special Attack Failure: Cannot attack with non-Unit object.%n"));
			return false;
		}
		Unit unit = (Unit) object;
		MapLocation targetLocation = attackOrder.getTargetLocation();
		// Check if the Unit's special attack has cooled down
		if (unit.getSpecialAttackCooldown() > 0) {
			failures.append(String.format("Special Attack Failure: Ability is still on cooldown.%n"));
			return false;
		}
		// Check if the target location is in the Players's combined field of view.
		if (!player.getCombinedFieldOfView(map)
					.contains(targetLocation)) {
			failures.append(String.format("Special Attack Failure: Target location is not in your Field-of-View.%n"));
			return false;
		}
		// Check if the target location is within the Unit's attack range
		if (unit.getAttackRange() < MapLocation.getManhattanDist(unit.getLocation(), attackOrder.getTargetLocation())) {
			failures.append(String.format("Special Attack Failure: Target location outside of attack range.%n"));
			return false;
		}
		// Execute the special action, this is different per Unit type
		if (object instanceof Infected) {
			// The special attack of an infected can't actually be ordered, since it triggers on kill
			failures.append(String.format("Special Attack Failure: An Infected's special attack cannot be ordered.%n"));
			return false;
		} else if (object instanceof Medic) {
			// The special attack of a medic heals a unit for an amount
			Unit target = (Unit) map.getMapContent()[map.toPosition(attackOrder.getTargetLocation())][Constants.MAP_INTERNAL_UNIT_INDEX];
			if (target != null) {
				target.increaseHP(Constants.MEDIC_SPECIAL_HEAL);
				attackSuccess = true;
			}
		} else if (object instanceof Soldier) {
			// The special attack of a soldier is a grenade that does damage in an area
			List<MapLocation> areaOfEffect = map.getAreaAround(attackOrder.getTargetLocation(), true);
			for (MapLocation location : areaOfEffect) {
				// Call an attack on each location inside the area of effect
				if (map.attackLocation(location, Constants.SOLDIER_SPECIAL_DAMAGE)) {
					// Report success if at least one of the locations is successfully attacked
					attackSuccess = true;
				}
			}
		}
		// If we executed the special action, start the cooldown
		((Unit) object).startCooldown();
		// Return
		return attackSuccess;
	}

	/**
	 * Awards a player an amount of points equal to the score it should receive for the type of Unit that was killed.
	 * 
	 * @param player
	 *            The player who's order lead to the Unit dying.
	 * @param killedUnit
	 *            The Unit that died.
	 */
	private void awardPointsForUnitDeath(Player player, Unit killedUnit) {
		if (killedUnit instanceof Soldier) {
			player.awardScore(Constants.SOLDIER_SCORE);
		} else if (killedUnit instanceof Medic) {
			player.awardScore(Constants.MEDIC_SCORE);
		} else if (killedUnit instanceof Infected) {
			player.awardScore(Constants.INFECTED_SCORE);
		}
	}

}
