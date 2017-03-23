package net.codepoke.ai.challenge.hunterkiller.gameobjects.mapfeature;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.codepoke.ai.challenge.hunterkiller.HunterKillerConstants;
import net.codepoke.ai.challenge.hunterkiller.HunterKillerState;
import net.codepoke.ai.challenge.hunterkiller.Map;
import net.codepoke.ai.challenge.hunterkiller.MapLocation;
import net.codepoke.ai.challenge.hunterkiller.Player;
import net.codepoke.ai.challenge.hunterkiller.StringExtensions;
import net.codepoke.ai.challenge.hunterkiller.enums.StructureOrderType;
import net.codepoke.ai.challenge.hunterkiller.enums.StructureType;
import net.codepoke.ai.challenge.hunterkiller.enums.TileType;
import net.codepoke.ai.challenge.hunterkiller.enums.UnitType;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.Controlled;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Unit;
import net.codepoke.ai.challenge.hunterkiller.orders.StructureOrder;

/**
 * Class representing a structure on the map. See {@link StructureType} for a list of types.
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Structure
		extends MapFeature
		implements Controlled {

	// region Properties

	/**
	 * The type of structure.
	 */
	private StructureType type;

	/**
	 * The unique identifier of the {@link net.codepoke.ai.challenge.hunterkiller.Player Player} this
	 * structure is currently being controlled by. Note: this can be unknown if this structure is not being controlled.
	 */
	private int controllingPlayerID = HunterKillerConstants.STRUCTURE_NO_CONTROL;

	/**
	 * Whether or not this structure can be captured.
	 */
	private boolean isCapturable = HunterKillerConstants.STRUCTURE_CAPTURABLE;

	/**
	 * Whether or not this structure allows the spawning of {@link Unit}s.
	 */
	private boolean allowsSpawning = HunterKillerConstants.STRUCTURE_ALLOW_SPAWNING;

	/**
	 * The location on the map where this structure spawns it's {@link Unit}s. Note: this will be null if the structure
	 * does not allow spawning.
	 */
	private MapLocation spawnLocation = null;

	/**
	 * Whether or not the structure generates resources.
	 */
	private boolean generatesResource = HunterKillerConstants.STRUCTURE_GENERATES_RESOURCE;

	/**
	 * The amount of resources this structure generates.
	 */
	private int resourceGeneration = HunterKillerConstants.STRUCTURE_RESOURCE_GENERATION;

	/**
	 * Whether or not the structure generates score points.
	 */
	private boolean generatesScore = HunterKillerConstants.STRUCTURE_GENERATES_SCORE;

	/**
	 * The amount of score points this structure generates.
	 */
	private int scoreGeneration = HunterKillerConstants.STRUCTURE_SCORE_GENERATION;

	/**
	 * Whether or not this structure is a command center. (If a player's command center is destroyed, they are
	 * eliminated)
	 */
	private boolean isCommandCenter = HunterKillerConstants.STRUCTURE_IS_COMMAND_CENTER;

	// endregion

	// region Constructor

	/**
	 * Creates a structure.
	 * 
	 * @param location
	 *            The location on the map where this structure will be placed.
	 * @param type
	 *            The type of structure to create.
	 */
	public Structure(MapLocation location, StructureType type) {
		super(location, HunterKillerConstants.STRUCTURE_MAX_HP, HunterKillerConstants.STRUCTURE_MAX_HP,
				HunterKillerConstants.STRUCTURE_DESTRUCTIBLE, HunterKillerConstants.STRUCTURE_BLOCKING_LOS,
				HunterKillerConstants.STRUCTURE_WALKABLE);
		this.type = type;
		// Set the specifics of each structure type
		switch (type) {
		case Base:
			isCapturable = HunterKillerConstants.BASE_CAPTURABLE;
			setWalkable(HunterKillerConstants.BASE_WALKABLE);
			isCommandCenter = HunterKillerConstants.BASE_IS_COMMAND_CENTER;
			resourceGeneration = HunterKillerConstants.BASE_RESOURCE_GENERATION;
			break;
		case Objective:
			setDestructible(HunterKillerConstants.OBJECTIVE_DESTRUCTIBLE);
			allowsSpawning = HunterKillerConstants.OBJECTIVE_ALLOW_SPAWNING;
			generatesResource = HunterKillerConstants.OBJECTIVE_GENERATES_RESOURCE;
			generatesScore = HunterKillerConstants.OBJECTIVE_GENERATES_SCORE;
			scoreGeneration = HunterKillerConstants.OBJECTIVE_SCORE_GENERATION;
			break;
		case Outpost:
			break;
		case Stronghold:
			setHpMax(HunterKillerConstants.STRONGHOLD_MAX_HP);
			setHpCurrent(HunterKillerConstants.STRONGHOLD_MAX_HP);
			allowsSpawning = HunterKillerConstants.STRONGHOLD_ALLOW_SPAWNING;
			resourceGeneration = HunterKillerConstants.STRONGHOLD_RESOURCE_GENERATION;
			break;
		default:
			throw new RuntimeException("Unsupported Structure type on creation.");
		}
	}

	/**
	 * Copy constructor.
	 * 
	 * @param otherStructure
	 *            The structure to copy.
	 */
	public Structure(Structure otherStructure) {
		super(new MapLocation(otherStructure.getLocation()
											.getX(), otherStructure.getLocation()
																	.getY()), otherStructure.getHpMax(), otherStructure.getHpCurrent(),
				otherStructure.isDestructible(), otherStructure.isBlockingLOS(), otherStructure.isWalkable());
		this.setID(otherStructure.getID());
		this.type = otherStructure.type;
		this.controllingPlayerID = otherStructure.controllingPlayerID;
		this.isCapturable = otherStructure.isCapturable;
		this.allowsSpawning = otherStructure.allowsSpawning;
		if (allowsSpawning)
			this.spawnLocation = new MapLocation(otherStructure.spawnLocation.getX(), otherStructure.spawnLocation.getY());
		this.generatesResource = otherStructure.generatesResource;
		this.resourceGeneration = otherStructure.resourceGeneration;
		this.generatesScore = otherStructure.generatesScore;
		this.scoreGeneration = otherStructure.scoreGeneration;
		this.isCommandCenter = otherStructure.isCommandCenter;
	}

	// endregion

	// region Public methods

	/**
	 * Whether or not this structure can spawn anything in the current game state. This method checks if this structure
	 * allows the spawning of units, and if the spawn location is traversable.
	 * 
	 * @param state
	 *            The current game state.
	 */
	public boolean canSpawn(HunterKillerState state) {
		// Get the current map
		Map map = state.getMap();
		// Check if the spawn location is traversable
		return this.allowsSpawning && map.isTraversable(spawnLocation);
	}

	/**
	 * Whether or not the specified type of order can be executed by this structure.
	 * Calls {@link Structure#canSpawn(HunterKillerState, UnitType)}.
	 * 
	 * @param state
	 *            The current state of the game.
	 * @param type
	 *            The type of order.
	 */
	public boolean canExecute(HunterKillerState state, StructureOrderType type) {
		switch (type) {
		case SPAWN_INFECTED:
			return canSpawn(state, UnitType.Infected);
		case SPAWN_MEDIC:
			return canSpawn(state, UnitType.Medic);
		case SPAWN_SOLDIER:
			return canSpawn(state, UnitType.Soldier);
		default:
			return false;
		}
	}

	/**
	 * Whether or not the specified type of unit can be spawned by this structure. This method checks if this structure
	 * allows the spawning of units, if the spawn location is traversable, and if the player controlling this structure
	 * has enough resources available.
	 * 
	 * @param state
	 *            The current state of the game.
	 * @param unitType
	 *            The type of unit.
	 */
	public boolean canSpawn(HunterKillerState state, UnitType unitType) {
		if (!this.isUnderControl())
			return false;

		// Get the player's resource
		int playerResource = state.getPlayer(controllingPlayerID)
									.getResource();
		// Check if the resource amount is at least the cost to spawn the specified unit type
		return this.allowsSpawning && state.getMap()
											.isTraversable(spawnLocation) && playerResource >= Unit.getSpawnCost(unitType);
	}

	/**
	 * Returns an order to spawn a unit from this structure.
	 * 
	 * @param unitType
	 *            The type of unit to spawn.
	 */
	public StructureOrder spawn(UnitType unitType) {
		switch (unitType) {
		case Infected:
			return new StructureOrder(this, StructureOrderType.SPAWN_INFECTED);
		case Medic:
			return new StructureOrder(this, StructureOrderType.SPAWN_MEDIC);
		case Soldier:
			return new StructureOrder(this, StructureOrderType.SPAWN_SOLDIER);
		default:
			System.err.println(StringExtensions.format("WARNING: Unsupported unit type: %s", unitType));
			return null;
		}
	}

	/**
	 * Instructs this structure to award resources to it's controlling player.
	 * 
	 * @param state
	 *            The current state of the game.
	 */
	public void awardResourcesToController(HunterKillerState state) {
		if (controllingPlayerID != HunterKillerConstants.STRUCTURE_NO_CONTROL && generatesResource) {
			Player myPlayer = state.getPlayer(controllingPlayerID);
			myPlayer.awardResource(resourceGeneration);
		}
	}

	/**
	 * Instructs this structure to award score points to it's controlling player.
	 * 
	 * @param state
	 *            The current state of the game.
	 */
	public void awardScoreToController(HunterKillerState state) {
		if (controllingPlayerID != HunterKillerConstants.STRUCTURE_NO_CONTROL && generatesScore) {
			Player myPlayer = state.getPlayer(controllingPlayerID);
			myPlayer.awardScore(scoreGeneration);
		}
	}

	/**
	 * Whether or not this structure is being controlled by any player.
	 */
	public boolean isUnderControl() {
		return controllingPlayerID != HunterKillerConstants.STRUCTURE_NO_CONTROL;
	}

	// endregion

	// region Overridden methods

	@Override
	public boolean tick(HunterKillerState state) {
		// Check if we need to be controlled by another player
		if (isCapturable) {
			// Check if a unit is standing on our location
			Unit unit = state.getMap()
								.getUnitAtLocation(this.getLocation());
			if (unit != null) {
				// Check if that unit is controlled by a different player than we are
				if (unit.getControllingPlayerID() != controllingPlayerID) {
					// Remove ourself from the current controller's list
					if (controllingPlayerID != HunterKillerConstants.STRUCTURE_NO_CONTROL) {
						state.getPlayer(controllingPlayerID)
								.removeStructure(this.getID());
					}
					// Set the new player as controller
					Player player = state.getPlayer(unit.getControllingPlayerID());
					controllingPlayerID = player.getID();
					player.addStructure(this.getID());
				}
			}
		}
		return this.isDestructible() && this.getHpCurrent() <= 0;
	}

	@Override
	public Structure copy() {
		return new Structure(this);
	}

	@Override
	public String toString() {
		switch (type) {
		case Base:
			return "" + TileType.BASE.txt;
		case Objective:
			return "" + TileType.OBJECTIVE.txt;
		case Outpost:
			return "" + TileType.OUTPOST.txt;
		case Stronghold:
			return "" + TileType.STRONGHOLD.txt;
		default:
			return "?";
		}
	}

	/**
	 * Whether or not this structure is being controlled by the specified player.
	 */
	@Override
	public boolean isControlledBy(Player player) {
		return this.controllingPlayerID == player.getID();
	}

	// endregion

}
