package net.codepoke.ai.challenge.hunterkiller;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import net.codepoke.ai.challenge.hunterkiller.enums.Direction;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Unit;

/**
 * Defines a list of pre-made maps (limited to the top-left quadrant since this is copied).
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
@Data
@FieldDefaults(level = AccessLevel.PUBLIC)
public class MapSetup {

	/**
	 * The name of the map.
	 */
	String name;
	/**
	 * The data that will be copied around the map by {@link FourPatch}.
	 */
	String mapData;

	/** Whether or not we need to copy in the quadrants from the given string or not. */
	boolean custom;

	/**
	 * The width of the quadrant 'A' in the {@link FourPatch}.
	 */
	int quadrantAWidth;
	/**
	 * The height of the quadrant 'A' in the {@link FourPatch}.
	 */
	int quadrantAHeight;
	/**
	 * The {@link Direction} to which the base spawns it's {@link Unit}s.
	 */
	Direction spawnDirection = Direction.SOUTH;
	/**
	 * The amount of resources players start the game with.
	 */
	int startingResources = HunterKillerConstants.PLAYER_STARTING_RESOURCE;
	/**
	 * The amount of resources the player's bases generate each time.
	 */
	int baseResourceGeneration = HunterKillerConstants.STRUCTURE_RESOURCE_GENERATION;

	public MapSetup(String mapData) {
		this.mapData = mapData;

		String[] lines = mapData.split(FourPatch.NEWLINE_SEPARATOR);
		quadrantAWidth = lines[0].length();
		quadrantAHeight = lines.length;
	}

	public MapSetup(String name, String mapData, boolean custom) {
		this(mapData);
		this.name = name;
		this.custom = custom;

		// We reset the quadrant height/width as it'll be calculated by the Fourpatch.
		if (custom) {
			quadrantAWidth = -1;
			quadrantAHeight = -1;
		}
	}

	public MapSetup(String name, String mapData, int quadrantAWidth, int quadrantAHeight, Direction spawnDirection) {
		this.name = name;
		this.mapData = mapData;
		this.quadrantAWidth = quadrantAWidth;
		this.quadrantAHeight = quadrantAHeight;
		this.spawnDirection = spawnDirection;
	}

	public MapSetup(String name, String mapData, int quadrantAWidth, int quadrantAHeight, Direction spawnDirection, int startingResources,
					int baseResourceGeneration) {
		this(name, mapData, quadrantAWidth, quadrantAHeight, spawnDirection);
		this.startingResources = startingResources;
		this.baseResourceGeneration = baseResourceGeneration;
	}

}
