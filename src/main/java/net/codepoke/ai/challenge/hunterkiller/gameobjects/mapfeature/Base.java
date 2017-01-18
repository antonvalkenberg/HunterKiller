package net.codepoke.ai.challenge.hunterkiller.gameobjects.mapfeature;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.codepoke.ai.challenge.hunterkiller.Constants;
import net.codepoke.ai.challenge.hunterkiller.HunterKillerState;
import net.codepoke.ai.challenge.hunterkiller.Map;
import net.codepoke.ai.challenge.hunterkiller.MapLocation;
import net.codepoke.ai.challenge.hunterkiller.enums.BaseOrderType;
import net.codepoke.ai.challenge.hunterkiller.enums.TileType;
import net.codepoke.ai.challenge.hunterkiller.enums.UnitType;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.Controlled;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Unit;
import net.codepoke.ai.challenge.hunterkiller.orders.BaseOrder;

/**
 * Class representing the base for a player. The base can spawn {@link Unit}s on a nearby {@link MapLocation}.
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
@Getter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Base
		extends MapFeature
		implements Controlled {

	// region Properties

	/**
	 * The location on the map where this base spawns it's {@link Unit}s.
	 */
	private MapLocation spawnLocation;

	/**
	 * The unique identifier of the {@link net.codepoke.ai.challenge.hunterkiller.Player Player} this
	 * base belongs to.
	 */
	private int controllingPlayerID;

	// endregion

	// region Constructor

	/**
	 * Constructs a new instance of a Base with default values.
	 * 
	 * {@link Base#Base(MapLocation, MapLocation, int, int, boolean, boolean, boolean)}
	 */
	public Base(int playerID, MapLocation mapLocation, MapLocation spawnLocation) {
		this(playerID, mapLocation, spawnLocation, Constants.BASE_MAX_HP, Constants.BASE_DESTRUCTIBLE, Constants.BASE_BLOCKING_LOS,
				Constants.BASE_WALKABLE);
	}

	/**
	 * Constructs a new instance of a Base with a specific HP, and uses default values for other
	 * values.
	 * 
	 * {@link Base#Base(MapLocation, MapLocation, int, int, boolean, boolean, boolean)}
	 */
	public Base(int playerID, MapLocation mapLocation, MapLocation spawnLocation, int maxHP, boolean destructible, boolean blockingLOS,
				boolean walkable) {
		this(playerID, mapLocation, spawnLocation, maxHP, maxHP, destructible, blockingLOS, walkable);
	}

	/**
	 * Constructs a new instance of a Base.
	 * 
	 * @param controllingPlayerID
	 *            The ID of the player that controls this Base.
	 * @param mapLocation
	 *            The location of the Base on the map.
	 * @param spawnLocation
	 *            The location of the Base's spawn point.
	 * @param maxHP
	 *            The maximum amount of health points the Base has.
	 * @param currentHP
	 *            The current amount of health points the Base has.
	 * @param destructible
	 *            Whether or not the Base is destructible.
	 * @param blockingLOS
	 *            Whether or not the Base blocks Line of Sight for Units.
	 * @param walkable
	 *            Whether or not Units can move over the Base.
	 */
	public Base(int playerID, MapLocation mapLocation, MapLocation spawnLocation, int maxHP, int currentHP, boolean destructible,
				boolean blockingLOS, boolean walkable) {
		super(mapLocation, maxHP, currentHP, destructible, blockingLOS, walkable);
		this.controllingPlayerID = playerID;
		this.spawnLocation = new MapLocation(spawnLocation.getX(), spawnLocation.getY());
	}

	// endregion

	// region Public methods

	/**
	 * Whether or not this base can spawn anything in the current game state.
	 * 
	 * @param state
	 *            The current game state.
	 */
	public boolean canSpawn(HunterKillerState state) {
		// Get the current map
		Map map = state.getMap();
		// Check if the spawn location is traversable
		return map.isTraversable(spawnLocation);
	}

	/**
	 * Whether or not the specified type of unit can be spawned by this base.
	 * 
	 * @param unitType
	 *            The type of unit.
	 * @param state
	 *            The current state of the game.
	 */
	public boolean canSpawn(UnitType unitType, HunterKillerState state) {
		// Get the player's resource
		int playerResource = state.getPlayer(controllingPlayerID)
									.getResource();
		// Check if the resource amount is at least the cost to spawn the specified unit type
		return playerResource >= Unit.getSpawnCost(unitType);
	}

	/**
	 * Returns an order to spawn a unit from this base.
	 * 
	 * @param unitType
	 *            The type of unit to spawn.
	 */
	public BaseOrder spawn(UnitType unitType) {
		switch (unitType) {
		case Infected:
			return new BaseOrder(this, BaseOrderType.SPAWN_INFECTED);
		case Medic:
			return new BaseOrder(this, BaseOrderType.SPAWN_MEDIC);
		case Soldier:
			return new BaseOrder(this, BaseOrderType.SPAWN_SOLDIER);
		default:
			System.err.println(String.format("WARNING: Unsupported unit type: %s", unitType));
			return null;
		}
	}

	// endregion

	// region Overridden methods

	@Override
	public Base copy() {
		Base newB = new Base(controllingPlayerID, this.getLocation(), spawnLocation, this.getHpMax(), this.getHpCurrent(),
								this.isDestructible(), this.isBlockingLOS(), this.isWalkable());
		newB.setID(this.getID());
		return newB;
	}

	@Override
	public String toString() {
		return "" + TileType.BASE.txt;
	}

	// endregion

}
