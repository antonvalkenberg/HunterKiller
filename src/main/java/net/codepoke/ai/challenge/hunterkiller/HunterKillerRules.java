package net.codepoke.ai.challenge.hunterkiller;

import java.util.List;

import net.codepoke.ai.GameRules;
import net.codepoke.ai.GameRules.Result.Ranking;
import net.codepoke.ai.challenge.hunterkiller.enums.Direction;
import net.codepoke.ai.challenge.hunterkiller.enums.Direction.Rotation;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.GameObject;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.mapfeature.MapFeature;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.mapfeature.Structure;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.mapfeature.Wall;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Infected;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Medic;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Soldier;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Unit;
import net.codepoke.ai.challenge.hunterkiller.orders.HunterKillerOrder;
import net.codepoke.ai.challenge.hunterkiller.orders.StructureOrder;
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

	private static final boolean DEBUG = false;

	/**
	 * Handles the specified action. Also ends the player's turn and checks for a completed game
	 * state.
	 */
	@Override
	public Result handle(HunterKillerState state, HunterKillerAction action) {
		// Check to make sure only the active player can perform an action
		if (action.getActingPlayerID() != state.getCurrentPlayer()) {
			return new Result(false, false, null, "Invalid action",
								String.format(	"Player performing the action (ID: %d) is not the active player (ID: %d).",
												action.getActingPlayerID(),
												state.getCurrentPlayer()));
		}
		// Check to make sure the round numbers match
		if (action.getCurrentRound() != state.getCurrentRound()) {
			return new Result(false, false, null, "Invalid action",
								String.format(	"Round number of the action (%d) did not match the State's (%d).",
												action.getCurrentRound(),
												state.getCurrentRound()));
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
			if (order instanceof StructureOrder) {
				StructureOrder structureOrder = (StructureOrder) order;
				// Try to spawn the Unit
				structureOrder.setAccepted(spawnUnit(map, actingPlayer, structureOrder, failures));
			} else if (order instanceof UnitOrder) {
				UnitOrder unitOrder = (UnitOrder) order;
				switch (unitOrder.getOrderType()) {
				case ROTATE_CLOCKWISE:
					// Try to rotate the unit east
					unitOrder.setAccepted(rotateUnit(map, unitOrder.getObjectID(), Rotation.CLOCKWISE, failures));
					break;
				case ROTATE_COUNTER_CLOCKWISE:
					// Try to rotate the unit west
					unitOrder.setAccepted(rotateUnit(map, unitOrder.getObjectID(), Rotation.COUNTER_CLOCKWISE, failures));
					break;
				case MOVE_NORTH:
				case MOVE_EAST:
				case MOVE_SOUTH:
				case MOVE_WEST:
					// Try to move the unit
					unitOrder.setAccepted(moveUnit(map, unitOrder, failures));
					break;
				case ATTACK:
					// Try to execute the ordered attack
					unitOrder.setAccepted(attackLocation(state, actingPlayer, unitOrder, failures));
					break;
				case ATTACK_SPECIAL:
					// Try to execute the ordered attack
					unitOrder.setAccepted(attackSpecial(map, actingPlayer, unitOrder, failures));
					break;
				default:
					failures.append(String.format("WARNING: Unsupported UnitOrderType (%s).%n", unitOrder.getOrderType()));
					failCount++;
					break;
				}
			} else {
				failures.append(String.format("WARNING: Unsupported type of order (%s).%n", order.getClass()
																									.getName()));
				failCount++;
			}

			// Check if our order was accepted or not
			if (!order.isAccepted())
				failCount++;
		}

		if (failCount > 0 && DEBUG) {
			System.out.printf(	"P(%d)R(%d): %d orders ignored, Reasons:%n%s%n",
								action.getActingPlayerID(),
								state.getCurrentRound(),
								failCount,
								failures.toString());
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
	 * @param order
	 *            The order that was issued.
	 * @param failures
	 *            The StringBuilder to append any failures to.
	 * @return Whether or not the spawning was successful.
	 */
	private boolean spawnUnit(Map map, Player player, StructureOrder order, StringBuilder failures) {
		boolean spawnSuccess = false;
		Structure structure = (Structure) map.getObject(order.objectID);
		// Make sure this Structure can spawn
		if (!structure.isAllowsSpawning()) {
			failures.append(String.format("Spawn Failure: Structure does not allow spawning of units.%n"));
			return false;
		}
		MapLocation spawnlocation = structure.getSpawnLocation();
		// The direction a unit faces when they spawn will be in line with the direction the spawn location is relative
		// to the base.
		Direction spawnDirection = MapLocation.getDirectionTo(structure.getLocation(), spawnlocation);
		// Make sure we got a direction
		if (spawnDirection == null) {
			failures.append(String.format("Spawn Failure: Spawn location is not on a cardinal direction relative to the base.%n"));
			return false;
		}
		// Get the correct costs
		int spawnCosts = -1;
		switch (order.getOrderType()) {
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
			failures.append(String.format("Spawn Failure: Unsupported StructureOrderType (%s).%n", order.getOrderType()));
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
				switch (order.getOrderType()) {
				case SPAWN_INFECTED:
					unit = new Infected(player.getID(), spawnlocation, spawnDirection);
					break;
				case SPAWN_MEDIC:
					unit = new Medic(player.getID(), spawnlocation, spawnDirection);
					break;
				case SPAWN_SOLDIER:
					unit = new Soldier(player.getID(), spawnlocation, spawnDirection);
					break;
				default:
					failures.append(String.format("Spawn Failure: Unsupported StructureOrderType (%s).%n", order.getOrderType()));
					return false;
				}
				// Register the unit
				map.registerGameObject(unit);
				// Place the unit on the map
				spawnSuccess = map.place(map.toPosition(spawnlocation), unit);
				// Add the unit to the Player's squad if successfully spawned and update it's Field-of-View
				if (spawnSuccess) {
					player.addUnit(unit.getID());
					unit.updateFieldOfView(map.getFieldOfView(unit));
				}
			} else {
				failures.append(String.format("Spawn Failure: Spawn location is not traversable, potential causes: location is off grid, MapFeature at location is not walkable or a Unit is present at location.%n"));
				return false;
			}
		} else {
			failures.append(String.format(	"Spawn Failure: Insufficient resources: required %d, available %d.%n",
											spawnCosts,
											player.getResource()));
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
			failures.append(String.format("Rotate Failure: Cannot rotate non-Unit object (%s).%n", object.getClass()
																											.getName()));
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
			failures.append(String.format("Movement Failure: Cannot move non-Unit object (%s).%n", object.getClass()
																											.getName()));
			return false;
		}
		// Check if a target for the move has been set
		if (moveOrder.getTargetLocation() == null) {
			failures.append(String.format("Movement Failure: No target location set.%n"));
			return false;
		}
		// Check if the ordered move is possible
		if (!map.isMovePossible(map.getObjectLocation(moveOrder.getObjectID()), moveOrder)) {
			failures.append(String.format("Movement Failure: Illegal move supplied. Possible causes: No unit at origin location, target location is off grid, MapFeature at target location is not walkable or a Unit is present at target location. %n"));
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
			failures.append(String.format("Attack Failure: Cannot attack with non-Unit object (%s).%n", object.getClass()
																												.getName()));
			return false;
		}

		Unit unit = (Unit) object;
		MapLocation targetLocation = attackOrder.getTargetLocation();
		// Check if the target location is in the Player's combined field of view
		if (!player.getCombinedFieldOfView(map)
					.contains(targetLocation)) {
			failures.append(String.format("Attack Failure: Target location is not in player's Field-of-View.%n"));
			return false;
		}
		// Check if the target location is within the Unit's attack range
		if (unit.getAttackRange() < MapLocation.getManhattanDist(unit.getLocation(), targetLocation)) {
			failures.append(String.format(	"Attack Failure: Target location is outside of attack range (range: %d, distance: %d).%n",
											unit.getAttackRange(),
											MapLocation.getManhattanDist(unit.getLocation(), targetLocation)));
			return false;
		}

		// Tell the map that the target location is being attacked for X damage
		attackSuccess = map.attackLocation(targetLocation, unit.getAttackDamage());

		// Check if we need to trigger an Infected's special attack.
		// Several conditions need to hold: (in order of most likely to break out of the statement)
		// - An Infected was the source of the attack
		// - There is a unit on the targeted location
		// - The target is not an Infected
		// - The target is now dead
		// - The Infected's special attack is not on cooldown
		// - The attack succeeded
		Unit targetUnit = map.getUnitAtLocation(targetLocation);
		if (unit instanceof Infected && targetUnit != null && !(targetUnit instanceof Infected) && targetUnit.getHpCurrent() <= 0
			&& unit.getSpecialAttackCooldown() == 0 && attackSuccess) {

			Unit deadUnit = map.getUnitAtLocation(targetLocation);
			// Remove the dead unit from it's owner
			state.getPlayer(deadUnit.getControllingPlayerID())
					.removeUnit(deadUnit.getID());

			// Remove the dead unit from the map
			map.unregisterGameObject(deadUnit);

			// Award points to the player
			awardPointsForUnitDeath(player, deadUnit);

			// Spawn a new Infected, on the same team as the Infected that performed this attack
			Infected spawn = new Infected(player.getID(), targetLocation, unit.getOrientation());
			map.registerGameObject(spawn);
			map.place(targetLocation, spawn);

			// Add the newly spawned unit to the player
			player.addUnit(spawn.getID());
			spawn.updateFieldOfView(map.getFieldOfView(spawn));

			// If we executed the special action, start the cooldown
			unit.startCooldown();
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
			failures.append(String.format("Special Attack Failure: Cannot attack with non-Unit object (%s).%n", object.getClass()
																														.getName()));
			return false;
		}
		Unit unit = (Unit) object;
		MapLocation targetLocation = attackOrder.getTargetLocation();
		// Check if the Unit's special attack has cooled down
		if (unit.getSpecialAttackCooldown() > 0) {
			failures.append(String.format(	"Special Attack Failure: Ability is still on cooldown (%d round(s) remaining).%n",
											unit.getSpecialAttackCooldown()));
			return false;
		}
		// Check if the target location is in the Players's combined field of view.
		if (!player.getCombinedFieldOfView(map)
					.contains(targetLocation)) {
			failures.append(String.format("Special Attack Failure: Target location is not in player's Field-of-View.%n"));
			return false;
		}
		// Check if the target location is within the Unit's attack range
		if (unit.getAttackRange() < MapLocation.getManhattanDist(unit.getLocation(), targetLocation)) {
			failures.append(String.format(	"Special Attack Failure: Target location outside of attack range (range: %d, distance: %d).%n",
											unit.getAttackRange(),
											MapLocation.getManhattanDist(unit.getLocation(), targetLocation)));
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
			if (target == null) {
				failures.append(String.format(	"Special Attack Failure: No Unit found on target location (%s).%n",
												attackOrder.getTargetLocation()));
				return false;
			} else {
				target.increaseHP(Constants.MEDIC_SPECIAL_HEAL);
				attackSuccess = true;
			}
		} else if (object instanceof Soldier) {
			// The special of a Soldier can't have a Wall as it's target (to avoid the explosion going through walls)
			if (map.getFeatureAtLocation(attackOrder.getTargetLocation()) instanceof Wall) {
				failures.append(String.format("Special Attack Failure: A Soldier's special attack cannot target a Wall.%n"));
				return false;
			}
			// The special attack of a soldier is a grenade that does damage in an area
			List<MapLocation> areaOfEffect = map.getAreaAround(attackOrder.getTargetLocation(), true);
			for (MapLocation location : areaOfEffect) {
				// Call an attack on each location inside the area of effect
				if (map.attackLocation(location, Constants.SOLDIER_SPECIAL_DAMAGE)) {
					// Report success if at least one of the locations is successfully attacked
					attackSuccess = true;

					// Check if there was a Unit on the targeted location, and if it is currently dead
					Unit targetUnit = map.getUnitAtLocation(location);
					if (targetUnit != null && targetUnit.getHpCurrent() <= 0) {
						// Award points to the player
						awardPointsForUnitDeath(player, targetUnit);
					}
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
		// Only award points if the unit did not belong to the player itself
		if (player.getUnitIDs()
					.contains(killedUnit.getID()) || killedUnit.getControllingPlayerID() == player.getID())
			return;

		if (killedUnit instanceof Soldier) {
			player.awardScore(Constants.SOLDIER_SCORE);
		} else if (killedUnit instanceof Medic) {
			player.awardScore(Constants.MEDIC_SCORE);
		} else if (killedUnit instanceof Infected) {
			player.awardScore(Constants.INFECTED_SCORE);
		}
	}

}
