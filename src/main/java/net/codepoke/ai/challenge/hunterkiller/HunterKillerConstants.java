package net.codepoke.ai.challenge.hunterkiller;

import lombok.Setter;
import net.codepoke.ai.challenge.hunterkiller.enums.Direction;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.mapfeature.MapFeature;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Unit;

/**
 * Contains all constant values used in HunterKiller.
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
public class HunterKillerConstants {

	public static final String GAME_NAME = "HunterKiller";

	// region MapFeature

	/**
	 * The default amount of health points for a MapFeature.
	 */
	public static final int MAPFEATURE_DEFAULT_HP = 5;
	/**
	 * Default destructibility of a MapFeature.
	 */
	public static final boolean MAPFEATURE_DEFAULT_DESTRUCTIBLE = false;
	/**
	 * Whether or not a MapFeature blocks Line of Sight by default.
	 */
	public static final boolean MAPFEATURE_DEFAULT_BLOCKING_LOS = false;
	/**
	 * Whether or not a MapFeature can be moved over by default.
	 */
	public static final boolean MAPFEATURE_DEFAULT_WALKABLE = true;

	// region Structure

	/**
	 * Structures are not controlled by any player by default.
	 */
	public static final int STRUCTURE_NO_CONTROL = -1;
	/**
	 * Health points for a structure.
	 */
	public static final int STRUCTURE_MAX_HP = 50;
	/**
	 * Structures are destructible.
	 */
	public static final boolean STRUCTURE_DESTRUCTIBLE = true;
	/**
	 * Structures block Line of Sight.
	 */
	public static final boolean STRUCTURE_BLOCKING_LOS = true;
	/**
	 * Structures can be moved over.
	 */
	public static final boolean STRUCTURE_WALKABLE = true;
	/**
	 * Structures are capturable.
	 */
	public static final boolean STRUCTURE_CAPTURABLE = true;
	/**
	 * Structures can spawn units.
	 */
	public static final boolean STRUCTURE_ALLOW_SPAWNING = true;
	/**
	 * Structures generate resources.
	 */
	public static final boolean STRUCTURE_GENERATES_RESOURCE = true;
	/**
	 * The default amount of resources a structure generates.
	 */
	public static final int STRUCTURE_RESOURCE_GENERATION = 12;
	/**
	 * Structures do not generate score points.
	 */
	public static final boolean STRUCTURE_GENERATES_SCORE = false;
	/**
	 * The default amount of score points a structure generates.
	 */
	public static final int STRUCTURE_SCORE_GENERATION = 0;
	/**
	 * Structures are not command centers by default.
	 */
	public static final boolean STRUCTURE_IS_COMMAND_CENTER = false;

	// region Base

	/**
	 * Bases can't be moved over.
	 */
	public static final boolean BASE_WALKABLE = false;
	/**
	 * Bases can't be captured.
	 */
	public static final boolean BASE_CAPTURABLE = false;
	/**
	 * Bases are command centers.
	 */
	public static final boolean BASE_IS_COMMAND_CENTER = true;
	/**
	 * Bases generate the default amount of resources.
	 */
	@Setter
	public static int BASE_RESOURCE_GENERATION = STRUCTURE_RESOURCE_GENERATION;

	// endregion

	// region Outpost

	// Nothing different from default

	// endregion

	// region Stronghold

	/**
	 * Strongholds are tough.
	 */
	public static final int STRONGHOLD_MAX_HP = 250;
	/**
	 * Strongholds can't spawn units.
	 */
	public static final boolean STRONGHOLD_ALLOW_SPAWNING = false;
	/**
	 * Strongholds generate more resources than usual.
	 */
	public static final int STRONGHOLD_RESOURCE_GENERATION = 25;

	// endregion

	// region Objective

	/**
	 * Objectives can't be destroyed.
	 */
	public static final boolean OBJECTIVE_DESTRUCTIBLE = false;
	/**
	 * Objectives can't spawn units.
	 */
	public static final boolean OBJECTIVE_ALLOW_SPAWNING = false;
	/**
	 * Objectives don't generate resources.
	 */
	public static final boolean OBJECTIVE_GENERATES_RESOURCE = false;
	/**
	 * Objectives generate score points.
	 */
	public static final boolean OBJECTIVE_GENERATES_SCORE = true;
	/**
	 * The amount of score points an objective generates.
	 */
	public static final int OBJECTIVE_SCORE_GENERATION = 50;

	// endregion

	// endregion

	// region Door

	/**
	 * Doors are indestructible.
	 */
	public static final boolean DOOR_DESTRUCTIBLE = false;
	/**
	 * Door are created closed and block Line of Sight in that state.
	 */
	public static final boolean DOOR_BLOCKS_LOS = true;
	/**
	 * Doors can be moved over. (At which point they will open and remain open for a number of rounds)
	 */
	public static final boolean DOOR_WALKABLE = true;
	/**
	 * Once opened a door will remain open for this amount of rounds.
	 */
	public static final int DOOR_OPEN_ROUNDS = 5;

	// endregion

	// region Floor

	/**
	 * Floors are indestructible.
	 */
	public static final boolean FLOOR_DESTRUCTIBLE = false;
	/**
	 * Floors do not block Line of Sight.
	 */
	public static final boolean FLOOR_BLOCKS_LOS = false;
	/**
	 * Floors can be moved over.
	 */
	public static final boolean FLOOR_WALKABLE = true;

	// endregion

	// region Space

	/**
	 * Space is indestructible.
	 */
	public static final boolean SPACE_DESTRUCTIBLE = false;
	/**
	 * Space does not block Line of Sight.
	 */
	public static final boolean SPACE_BLOCKS_LOS = false;
	/**
	 * Space can not be moved over.
	 */
	public static final boolean SPACE_WALKABLE = false;

	// endregion

	// region Wall

	/**
	 * Walls are indestructible.
	 */
	public static final boolean WALL_DESTRUCTIBLE = false;
	/**
	 * Walls block Line of Sight.
	 */
	public static final boolean WALL_BLOCKS_LOS = true;
	/**
	 * Walls can not be moved over.
	 */
	public static final boolean WALL_WALKABLE = false;

	// endregion

	// endregion

	// region Unit

	/**
	 * Default health points for a Unit.
	 */
	public static final int UNIT_DEFAULT_HP = 5;
	/**
	 * Default orientation for a Unit.
	 */
	public static final Direction UNIT_DEFAULT_ORIENTATION = Direction.NORTH;
	/**
	 * Default Field of View range for a Unit.
	 */
	public static final int UNIT_DEFAULT_FOV_RANGE = 3;
	/**
	 * Default Field of View angle for a Unit.
	 */
	public static final int UNIT_DEFAULT_FOV_ANGLE = 90;
	/**
	 * Default attack range for a Unit.
	 */
	public static final int UNIT_DEFAULT_ATTACK_RANGE = 4;
	/**
	 * Default attack damage for a Unit.
	 */
	public static final int UNIT_DEFAULT_ATTACK_DAMAGE = 3;
	/**
	 * Default cooldown for a Unit's special attack.
	 */
	public static final int UNIT_DEFAULT_SPECIAL_COOLDOWN = 0;
	/**
	 * Default spawn cost for a Unit.
	 */
	public static final int UNIT_DEFAULT_SPAWN_COST = 5;
	/**
	 * Default score that a Unit is worth.
	 */
	public static final int UNIT_DEFAULT_SCORE = 25;
	/**
	 * The movement range for a Unit.
	 */
	public static final int UNIT_MOVEMENT_RANGE = 1;

	// region Infected

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

	// endregion

	// region Medic

	/**
	 * Health points for a Medic.
	 */
	public static final int MEDIC_MAX_HP = 2;
	/**
	 * Field of View range for a Medic.
	 */
	public static final int MEDIC_FOV_RANGE = 3;
	/**
	 * Field of View angle for a Medic.
	 */
	public static final int MEDIC_FOV_ANGLE = 90;
	/**
	 * Attack range for a Medic.
	 */
	public static final int MEDIC_ATTACK_RANGE = 4;
	/**
	 * Attack damage for a Medic.
	 */
	public static final int MEDIC_ATTACK_DAMAGE = 1;
	/**
	 * Cooldown of a Medic's special attack.
	 */
	public static final int MEDIC_COOLDOWN = 2;
	/**
	 * Spawn cost for a Medic.
	 */
	public static final int MEDIC_SPAWN_COST = 10;
	/**
	 * The amount of points a Medic is worth when defeated.
	 */
	public static final int MEDIC_SCORE = 50;
	/**
	 * The amount of health points a Medic's special attack heals.
	 */
	public static final int MEDIC_SPECIAL_HEAL = 2;

	// endregion

	// region Soldier

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
	/**
	 * The damage of the Soldier's special attack.
	 */
	public static final int SOLDIER_SPECIAL_DAMAGE = 3;

	// endregion

	// endregion

	// region GameObject

	/**
	 * The default location if the object has not been placed yet.
	 */
	public static final MapLocation GAMEOBJECT_NOT_PLACED = new MapLocation(-1, -1);
	/**
	 * The default amount of health points an object has.
	 */
	public static final int GAMEOBJECT_DEFAULT_HP = 1;

	// endregion

	// region Rules

	/**
	 * The frequency (in rounds) with which structures generate resources or points.
	 */
	public static final int RULES_STRUCTURE_GENERATION_FREQUENCY = 5;
	/**
	 * The maximum number of rounds a game will have.
	 */
	public static final int RULES_MAX_GAME_ROUNDS = 200;

	// endregion

	// region Map

	/**
	 * Amount of layers in our internal {@link Map} representation. Currently we have 2: {@link MapFeature}s and
	 * {@link Unit}s.
	 */
	public static final int MAP_INTERNAL_LAYERS = 2;
	/**
	 * The index of the {@link MapFeature} layer in our internal representation.
	 */
	public static final int MAP_INTERNAL_FEATURE_INDEX = 0;
	/**
	 * The index of the {@link Unit} layer in our internal representation.
	 */
	public static final int MAP_INTERNAL_UNIT_INDEX = 1;
	/**
	 * Defines the separator used to create space between the different layers in the {@link Map}'s toString method.
	 */
	public static final String MAP_TOSTRING_LAYER_SEPARATOR = " | ";

	// endregion

	// region MoveGenerator

	/**
	 * The default index that an order created by the {@link MoveGenerator} will receive in the
	 * {@link HunterKillerAction}.
	 */
	public static final int MOVEGENERATOR_DEFAULT_ACTION_INDEX = 0;

	// endregion

	// region Player

	/**
	 * The amount of resources a {@link Player} starts the game with.
	 */
	public static final int PLAYER_STARTING_RESOURCE = 10;

	// endregion

}
