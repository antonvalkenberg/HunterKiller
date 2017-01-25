package net.codepoke.ai.challenge.hunterkiller.gameobjects;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.codepoke.ai.challenge.hunterkiller.Constants;
import net.codepoke.ai.challenge.hunterkiller.HunterKillerState;
import net.codepoke.ai.challenge.hunterkiller.MapLocation;

/**
 * Abstract class representing any object in HunterKiller that can be placed on the map. The game
 * engine will call the object at the start of each round for a response.
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class GameObject {

	// region Properties

	/**
	 * The GameObject's ID, assigned to them by the {@link net.codepoke.ai.challenge.hunterkiller.Map
	 * Map}.
	 */
	@Setter
	private int ID = -1;

	/**
	 * The location on the Map.
	 */
	private MapLocation location = Constants.GAMEOBJECT_NOT_PLACED;

	/**
	 * The maximum amount of health points for this object.
	 */
	@Setter
	private int hpMax = Constants.GAMEOBJECT_DEFAULT_HP;

	/**
	 * The amount of health points this object currently has.
	 */
	@Setter
	private int hpCurrent = hpMax;

	// endregion

	// region Constructor

	/**
	 * Constructs a new instance of a GameObject with default HP.
	 * 
	 * {@link GameObject#GameObject(MapLocation, int, int)}
	 */
	public GameObject(MapLocation mapLocation) {
		this(mapLocation, Constants.GAMEOBJECT_DEFAULT_HP);
	}

	/**
	 * Constructs a new instance of a GameObject at full health.
	 * 
	 * {@link GameObject#GameObject(MapLocation, int, int)}
	 */
	public GameObject(MapLocation mapLocation, int maxHP) {
		this(mapLocation, maxHP, maxHP);
	}

	/**
	 * Constructs a new instance of a GameObject.
	 * 
	 * @param mapLocation
	 *            The GameObject's location on the Map.
	 * @param maxHP
	 *            The maximum amount of health points this GameObject can have.
	 * @param currentHP
	 *            The current amount of health points this GameObject has.
	 */
	public GameObject(MapLocation mapLocation, int maxHP, int currentHP) {
		location = new MapLocation(mapLocation.getX(), mapLocation.getY());
		hpMax = maxHP;
		hpCurrent = currentHP;
	}

	// endregion

	// region Public methods

	/**
	 * Creates a copy of this GameObject.
	 */
	public abstract GameObject copy();

	/**
	 * Returns a string representation of this GameObject.
	 */
	@Override
	public abstract String toString();

	/**
	 * The tick call received upon the end of a player turn. When true is returned, the GameObject
	 * should be removed from the map.
	 */
	public boolean tick(HunterKillerState state) {
		return false;
	}

	/**
	 * Set this object's location
	 * 
	 * @param location
	 *            The new location for this object
	 */
	public void setLocation(MapLocation location) {
		this.location = new MapLocation(location.getX(), location.getY());
	}

	/**
	 * Reduce this object's health points.
	 * 
	 * @param amount
	 *            The amount to reduce by.
	 */
	public void reduceHP(int amount) {
		this.hpCurrent -= amount;
	}

	/**
	 * Increase this object's health points.
	 * 
	 * @param amount
	 *            The amount to increase by.
	 */
	public void increaseHP(int amount) {
		if (this.hpCurrent + amount > this.hpMax) {
			this.hpCurrent = hpMax;
		} else {
			this.hpCurrent += amount;
		}
	}

	/**
	 * Whether or not this object is damaged.
	 */
	public boolean isDamaged() {
		return this.getHpCurrent() < this.getHpMax();
	}

	// endregion

}
