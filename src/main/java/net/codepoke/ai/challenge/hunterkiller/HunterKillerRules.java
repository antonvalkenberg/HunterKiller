package net.codepoke.ai.challenge.hunterkiller;

import java.util.List;

import net.codepoke.ai.GameRules;
import net.codepoke.ai.GameRules.Result.Ranking;
import net.codepoke.ai.challenge.hunterkiller.enums.Direction;
import net.codepoke.ai.challenge.hunterkiller.enums.Direction.Rotation;
import net.codepoke.ai.challenge.hunterkiller.enums.StructureOrderType;
import net.codepoke.ai.challenge.hunterkiller.enums.UnitOrderType;
import net.codepoke.ai.challenge.hunterkiller.enums.UnitType;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.Controlled;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.GameObject;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.mapfeature.Structure;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.mapfeature.Wall;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Infected;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Medic;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Soldier;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Unit;
import net.codepoke.ai.challenge.hunterkiller.orders.HunterKillerOrder;
import net.codepoke.ai.challenge.hunterkiller.orders.OrderStatistics;
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

	/** This setting enables/disables the logging of failed or erroneous orders. */
	public static boolean IGNORE_FAILURES = false;

	/** Whether or not logging should be sent to the console. */
	private static final boolean LOG_TO_CONSOLE = false;

	/**
	 * Handles the specified action. Also ends the player's turn and checks for a completed game
	 * state.
	 */
	@Override
	public Result handle(HunterKillerState state, HunterKillerAction action) {
		// Check to make sure only the active player can perform an action
		if (action.getActingPlayerID() != state.getCurrentPlayer()) {
			return new Result(false, false, null, "Invalid action",
								StringExtensions.format("Player performing the action (ID: %d) is not the active player (ID: %d).",
														action.getActingPlayerID(),
														state.getCurrentPlayer()));
		}
		// Check to make sure the round numbers match
		if (action.getCurrentRound() != state.getCurrentRound()) {
			return new Result(false, false, null, "Invalid action",
								StringExtensions.format("Round number of the action (%d) did not match the State's (%d).",
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
		StringBuilder actionFailures = IGNORE_FAILURES ? null : new StringBuilder();

		// Get the orders contained in the action
		Array<HunterKillerOrder> orders = action.getOrders();

		// STATS
		state.getActivePlayer()
				.getStats().issued += orders.size;

		// Go through the collection of orders
		for (HunterKillerOrder order : orders) {
			StringBuilder orderFailures = IGNORE_FAILURES ? null : new StringBuilder();

			// Check if the order is possible
			if (!isOrderPossible(state, order, orderFailures)) {
				failCount++;
				order.setAccepted(false);
				// Log the failure if required
				if (actionFailures != null)
					actionFailures.append(orderFailures.toString());
				continue;
			}

			// Execute the order
			executeOrder(state, order, orderFailures);

			// Check if anything still went wrong
			if (orderFailures != null && orderFailures.length() > 0) {
				failCount++;
				order.setAccepted(false);
				// Log the failure if required
				if (actionFailures != null)
					actionFailures.append(orderFailures.toString());
			} else {
				order.setAccepted(true);

				// STATS
				state.getActivePlayer()
						.getStats().success++;
			}
		}

		// STATS
		state.getActivePlayer()
				.getStats().failed += failCount;

		if (LOG_TO_CONSOLE && actionFailures != null && failCount > 0) {
			System.out.println(StringExtensions.format(	"P(%d)R(%d): %d orders ignored, Reasons:%n%s%n",
														action.getActingPlayerID(),
														state.getCurrentRound(),
														failCount,
														actionFailures.toString()));
		}

		// Return the action as accepted, but add a count of how many orders failed, if any did.
		return new Result(true, false, null, "Action accepted",
							actionFailures != null && failCount > 0 ? StringExtensions.format(	"%d orders ignored, Reasons:%n%s",
																								failCount,
																								actionFailures.toString()) : "");
	}

	/**
	 * Executes an order on the provided state.
	 * 
	 * @param state
	 *            The state of the game.
	 * @param order
	 *            The order to execute.
	 * @param failureReasons
	 *            StringBuilder containing any reasons for why the order could not be executed.
	 */
	public void executeOrder(HunterKillerState state, HunterKillerOrder order, StringBuilder failureReasons) {
		// Most orders need to access these things
		Map map = state.getMap();
		Player activePlayer = state.getActivePlayer();
		OrderStatistics stats = activePlayer.getStats();
		GameObject orderObject = map.getObject(order.getObjectID());

		// Check which type of order we are dealing with
		if (order instanceof StructureOrder) {
			StructureOrder structureOrder = (StructureOrder) order;
			StructureOrderType type = structureOrder.getOrderType();
			Structure structure = (Structure) orderObject;
			MapLocation spawnLocation = structure.getSpawnLocation();
			Direction spawnDirection = MapLocation.getDirectionTo(structure.getLocation(), spawnLocation);
			int spawnCosts = -1;
			Unit unit;

			// Check if the type of the order is supported
			switch (type) {

			case SPAWN_INFECTED:
				unit = new Infected(activePlayer.getID(), spawnLocation, spawnDirection);
				spawnCosts = HunterKillerConstants.INFECTED_SPAWN_COST;

				// STATS
				stats.spawnInfected++;
				break;

			case SPAWN_MEDIC:
				unit = new Medic(activePlayer.getID(), spawnLocation, spawnDirection);
				spawnCosts = HunterKillerConstants.MEDIC_SPAWN_COST;

				// STATS
				stats.spawnMedic++;
				break;

			case SPAWN_SOLDIER:
				unit = new Soldier(activePlayer.getID(), spawnLocation, spawnDirection);
				spawnCosts = HunterKillerConstants.SOLDIER_SPAWN_COST;

				// STATS
				stats.spawnSoldier++;
				break;

			default:
				// Getting here means we have come across a type that is not yet implemented
				if (failureReasons != null)
					failureReasons.append(StringExtensions.format(	"StructureOrder fail for ID %d: Unsupported order type '%s'.%n",
																	structureOrder.objectID,
																	type));
				return;
			}

			// Charge the costs
			activePlayer.setResource(activePlayer.resource - spawnCosts);
			// Register the unit
			map.registerGameObject(unit);
			// Place the unit on the map
			boolean spawnSuccess = map.place(map.toPosition(spawnLocation), unit);
			// Add the unit to the Player's squad if successfully spawned and update it's Field-of-View
			if (spawnSuccess) {
				activePlayer.addUnit(unit.getID());
				unit.updateFieldOfView(map.getFieldOfView(unit));
			}
		} else if (order instanceof UnitOrder) {
			UnitOrder unitOrder = (UnitOrder) order;
			UnitOrderType type = unitOrder.getOrderType();
			Unit unit = (Unit) orderObject;
			MapLocation targetLocation = unitOrder.getTargetLocation();

			// Check if the type of the order is supported
			switch (type) {

			case ROTATE_CLOCKWISE:
				unit.setOrientation(Direction.rotate(unit.getOrientation(), Rotation.CLOCKWISE));
				// Invalidate the unit's field-of-view
				unit.invalidateFieldOfView();

				// STATS
				stats.rotateClockwise++;
				break;

			case ROTATE_COUNTER_CLOCKWISE:
				unit.setOrientation(Direction.rotate(unit.getOrientation(), Rotation.COUNTER_CLOCKWISE));
				// Invalidate the unit's field-of-view
				unit.invalidateFieldOfView();

				// STATS
				stats.rotateCounter++;
				break;

			case MOVE:
				map.move(targetLocation, unit, failureReasons);
				// Invalidate the unit's field-of-view
				unit.invalidateFieldOfView();

				// STATS
				stats.move++;
				break;

			case ATTACK:
				// Tell the map that the target location is being attacked for X damage
				boolean attackSuccess = map.attackLocation(targetLocation, unit.getAttackDamage());

				// STATS
				stats.attack++;

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
					awardPointsForUnitDeath(activePlayer, deadUnit);

					// Spawn a new Infected, on the same team as the Infected that performed this attack
					Infected spawn = new Infected(activePlayer.getID(), targetLocation, unit.getOrientation());
					map.registerGameObject(spawn);
					map.place(targetLocation, spawn);

					// Add the newly spawned unit to the player
					activePlayer.addUnit(spawn.getID());
					spawn.updateFieldOfView(map.getFieldOfView(spawn));

					// If we executed the special action, start the cooldown
					unit.startCooldown();
				}
				// Otherwise, check if there was a Unit on the targeted location, and if it is currently dead
				else if (targetUnit != null && targetUnit.getHpCurrent() <= 0) {
					// Award points to the player
					awardPointsForUnitDeath(activePlayer, targetUnit);
				}

				// STATS
				if (targetUnit != null) {
					stats.attackUnit++;
					if (targetUnit.isControlledBy(activePlayer))
						stats.attackAlly++;
				}
				if (map.getFeatureAtLocation(targetLocation) instanceof Structure) {
					stats.attackStructure++;
				}

				break;

			case ATTACK_SPECIAL:

				// Different for each unit type
				UnitType unitType = unit.getType();
				switch (unitType) {

				case Infected:
					break;

				case Medic:
					map.getUnitAtLocation(targetLocation)
						.increaseHP(HunterKillerConstants.MEDIC_SPECIAL_HEAL);
					unit.startCooldown();

					// STATS
					stats.heal++;
					break;

				case Soldier:
					// The special attack of a soldier is a grenade that does damage in an area
					List<MapLocation> areaOfEffect = map.getAreaAround(targetLocation, true);
					for (MapLocation location : areaOfEffect) {
						// Call an attack on each location inside the area of effect
						if (map.attackLocation(location, HunterKillerConstants.SOLDIER_SPECIAL_DAMAGE)) {
							// Check if there was a Unit on the targeted location, and if it is currently dead
							Unit tU = map.getUnitAtLocation(location);
							if (tU != null && tU.getHpCurrent() <= 0) {
								// Award points to the player
								awardPointsForUnitDeath(activePlayer, tU);
							}
						}
					}
					unit.startCooldown();

					// STATS
					stats.grenade++;
					break;

				default:
					// Getting here means we have come across a type that is not yet implemented
					if (failureReasons != null)
						failureReasons.append(StringExtensions.format(	"UnitOrder fail for ID %d: Unsupported unit type '%s'.%n",
																		unitOrder.objectID,
																		unitType));
					return;

				}
				break;

			default:
				// Getting here means we have come across a type that is not yet implemented
				if (failureReasons != null)
					failureReasons.append(StringExtensions.format(	"UnitOrder fail for ID %d: Unsupported order type '%s'.%n",
																	unitOrder.objectID,
																	type));
				return;
			}
		} else {
			// Getting here means we have come across a type that is not yet implemented
			if (failureReasons != null)
				failureReasons.append(StringExtensions.format(	"Order fail for ID %d: Unsupported order type '%s'.%n",
																order.objectID,
																order.getClass()
																		.getName()));
			return;
		}
	}

	/**
	 * Determines if an order can be executed in the provided state.
	 * 
	 * @param state
	 *            The state of the game.
	 * @param order
	 *            The order.
	 * @param failureReasons
	 *            StringBuilder containing any reasons for why the order would not be possible.
	 * @return Whether or not the order can be successfully executed on the provided game state.
	 */
	public boolean isOrderPossible(HunterKillerState state, HunterKillerOrder order, StringBuilder failureReasons) {
		// Most orders need to access these things
		Map map = state.getMap();

		// Check if the object that the order is for still exists
		GameObject orderObject = map.getObject(order.getObjectID());
		if (orderObject == null) {
			if (failureReasons != null)
				failureReasons.append(StringExtensions.format("Order fail: Could not find object with ID %d.%n", order.getObjectID()));
			return false;
		}

		// Check if the object is being controlled by the currently active player
		if (orderObject instanceof Controlled && !((Controlled) orderObject).isControlledBy(state.getActivePlayer())) {
			if (failureReasons != null)
				failureReasons.append(StringExtensions.format(	"Order fail: Active player (%d) does not control object with ID %d.%n",
																state.getCurrentPlayer(),
																order.getObjectID()));
			return false;
		}

		// Check which type of order we are dealing with
		if (order instanceof StructureOrder) {
			StructureOrder structureOrder = (StructureOrder) order;

			// Check if the order-object is a structure
			if (!(orderObject instanceof Structure)) {
				if (failureReasons != null)
					failureReasons.append(StringExtensions.format(	"StructureOrder fail for ID %d: Source object is not a Structure (%s).%n",
																	structureOrder.objectID,
																	orderObject.getClass()
																				.getName()));
				return false;
			}

			Structure structure = (Structure) orderObject;
			// Check if the structure can spawn any units
			if (!structure.isAllowsSpawning()) {
				if (failureReasons != null)
					failureReasons.append(StringExtensions.format(	"StructureOrder fail for ID %d: Structure cannot spawn units.%n",
																	structureOrder.objectID));
				return false;
			}

			MapLocation spawnLocation = structure.getSpawnLocation();
			// Check if the spawn-location is traversable
			if (!map.isTraversable(spawnLocation, failureReasons)) {
				return false;
			}

			Direction spawnDirection = MapLocation.getDirectionTo(structure.getLocation(), spawnLocation);
			// Check if a spawn direction can be determined
			if (spawnDirection == null) {
				if (failureReasons != null)
					failureReasons.append(StringExtensions.format(	"StructureOrder fail for ID %d: Spawn location (%s) is not on a cardinal direction relative to the structure.%n",
																	structureOrder.objectID,
																	spawnLocation));
				return false;
			}

			StructureOrderType type = structureOrder.getOrderType();
			// Check if the type of the order is supported
			switch (type) {
			case SPAWN_INFECTED:
			case SPAWN_MEDIC:
			case SPAWN_SOLDIER:
				// Check if the player has enough resources to spawn this type
				if (!structure.canExecute(state, type)) {
					if (failureReasons != null)
						failureReasons.append(StringExtensions.format(	"StructureOrder fail for ID %d: Insufficient resources available for order of type '%s'.%n",
																		structureOrder.objectID,
																		type));
					return false;
				}
				return true;
			default:
				// Getting here means we have come across a type that is not yet implemented
				if (failureReasons != null)
					failureReasons.append(StringExtensions.format(	"StructureOrder fail for ID %d: Unsupported order type '%s'.%n",
																	structureOrder.objectID,
																	type));
				return false;
			}

		} else if (order instanceof UnitOrder) {
			UnitOrder unitOrder = (UnitOrder) order;

			// Check if the order-object is a unit
			if (!(orderObject instanceof Unit)) {
				if (failureReasons != null)
					failureReasons.append(StringExtensions.format(	"UnitOrder fail for ID %d: Source object is not a Unit (%s).%n",
																	unitOrder.objectID,
																	orderObject.getClass()
																				.getName()));
				return false;
			}

			UnitOrderType type = unitOrder.getOrderType();

			// Rotations don't need any other checks
			if (type == UnitOrderType.ROTATE_CLOCKWISE || type == UnitOrderType.ROTATE_COUNTER_CLOCKWISE)
				return true;

			MapLocation targetLocation = unitOrder.getTargetLocation();

			// Check if a target for the order has been set
			if (unitOrder.getTargetLocation() == null) {
				if (failureReasons != null)
					failureReasons.append(StringExtensions.format("UnitOrder fail for ID %d: No target location set.%n", unitOrder.objectID));
				return false;
			}

			if (type == UnitOrderType.MOVE) {
				// Check if the ordered move is possible
				return map.isMovePossible(orderObject.getLocation(), unitOrder, failureReasons);
			}

			// Check if the target location is in the unit's field of view
			Unit unit = (Unit) orderObject;
			if (!unit.getFieldOfView()
						.contains(targetLocation)) {
				if (failureReasons != null)
					failureReasons.append(StringExtensions.format(	"UnitOrder (%d -> Attack %s) fail: Target location is not in unit's Field-of-View.%n",
																	unit.getID(),
																	targetLocation));
				return false;
			}

			// Check if the target location is within the Unit's attack range
			if (unit.getAttackRange() < MapLocation.getManhattanDist(unit.getLocation(), targetLocation)) {
				if (failureReasons != null)
					failureReasons.append(StringExtensions.format(	"UnitOrder (%d -> Attack %s) fail: Target location is outside of attack range (range: %d, distance: %d).%n",
																	unit.getID(),
																	targetLocation,
																	unit.getAttackRange(),
																	MapLocation.getManhattanDist(unit.getLocation(), targetLocation)));
				return false;
			}

			// Attacks don't need any other checks
			if (type == UnitOrderType.ATTACK)
				return true;

			if (type == UnitOrderType.ATTACK_SPECIAL) {
				// Check if the Unit's special attack has cooled down
				if (unit.getSpecialAttackCooldown() > 0) {
					if (failureReasons != null)
						failureReasons.append(StringExtensions.format(	"UnitOrder fail for ID %d: Special attack is still on cooldown (%d round(s) remaining).%n",
																		unitOrder.objectID,
																		unit.getSpecialAttackCooldown()));
					return false;
				}

				// Check specific things per unit type
				switch (unit.getType()) {

				case Infected:
					// The special attack of an infected can't actually be ordered, since it triggers on kill
					if (failureReasons != null)
						failureReasons.append(StringExtensions.format(	"UnitOrder fail for ID %d: An Infected's special attack cannot be ordered.%n",
																		unitOrder.objectID));
					return false;

				case Medic:
					// The special attack of a medic heals a unit for an amount
					if (map.getUnitAtLocation(targetLocation) == null) {
						if (failureReasons != null)
							failureReasons.append(StringExtensions.format(	"UnitOrder fail for ID %d: Target location does not contain a Unit to heal.%n",
																			unitOrder.objectID));
						return false;
					}
					return true;

				case Soldier:
					// The special of a Soldier can't have a Wall as it's target
					if (map.getFeatureAtLocation(unitOrder.getTargetLocation()) instanceof Wall) {
						if (failureReasons != null)
							failureReasons.append(StringExtensions.format(	"UnitOrder fail for ID %d: A Soldier's special attack cannot target a Wall.%n",
																			unitOrder.objectID));
						return false;
					}
					return true;

				default:
					break;
				}

			}

			// Getting here means we have come across a type that is not yet implemented
			if (failureReasons != null)
				failureReasons.append(StringExtensions.format(	"UnitOrder fail for ID %d: Unsupported order type '%s'.%n",
																unitOrder.objectID,
																type));
			return false;

		} else {
			// Getting here means we have come across a type that is not yet implemented
			if (failureReasons != null)
				failureReasons.append(StringExtensions.format(	"Order fail for ID %d: Unsupported order type '%s'.%n",
																order.objectID,
																order.getClass()
																		.getName()));
			return false;
		}
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
			player.awardScore(HunterKillerConstants.SOLDIER_SCORE);
		} else if (killedUnit instanceof Medic) {
			player.awardScore(HunterKillerConstants.MEDIC_SCORE);
		} else if (killedUnit instanceof Infected) {
			player.awardScore(HunterKillerConstants.INFECTED_SCORE);
		}
	}

	/**
	 * Adds an order to an action if it is possible to execute it on the provided state. Note: this method will attempt
	 * to execute the order on the provided state.
	 * 
	 * @param action
	 *            The {@link HunterKillerAction} that the order should be added to.
	 * @param state
	 *            The {@link HunterKillerState} to apply the order on.
	 * @param order
	 *            The order.
	 * @param possibleCheckFails
	 *            If the order is not possible in the provided state, this will contain the reason(s) why.
	 * @param orderFails
	 *            If the order failed to execute on the provided state, this will contain the reason(s) why.
	 * @return Whether or not the order was added to the action.
	 */
	public boolean addOrderIfPossible(HunterKillerAction action, HunterKillerState state, HunterKillerOrder order,
			StringBuilder possibleCheckFails, StringBuilder orderFails) {
		boolean addedOrder = false;

		// Make sure this order is possible in the provided state
		if (isOrderPossible(state, order, possibleCheckFails)) {
			// Add the order to the action
			addedOrder = action.addOrder(order);
			// Execute this order on the state
			executeOrder(state, order, orderFails);
		}

		return addedOrder;
	}

}
