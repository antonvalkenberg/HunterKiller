package main.java.net.codepoke.ai.challenge.hunterkiller;

import java.util.HashMap;

import main.java.net.codepoke.ai.challenge.hunterkiller.gameobjects.GameObject;

/**
 * Class representing the map on which the game is played.
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
public class Map {
	
	//region Constants
	
	public static final int INTERNAL_MAP_LAYERS = 2;
	
	public static final int INTERNAL_MAP_FEATURE_INDEX = 0;
	
	public static final int INTERNAL_MAP_UNIT_INDEX = 1;

	//endregion
	
	//region Properties
	
	private int mapHeight;
	
	private int mapWidth;
	
	private HashMap<Integer, GameObject[]> mapContent;
	
	//endregion
	
	//region Constructor

	public Map(int width, int height) {
		mapWidth = width;
		mapHeight = height;
		
		mapContent = new HashMap<Integer, GameObject[]>();
		
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				mapContent.put(toPosition(x, y), new GameObject[INTERNAL_MAP_LAYERS]);
			}
		}
	}
	
	//endregion
	
	//region Public methods

	public int toPosition(MapLocation location) {
		return toPosition(location, mapWidth);
	}
	
	public static int toPosition(MapLocation location, int width) {
		return toPosition(location.getX(), location.getY(), width);
	}
	
	public int toPosition(int x, int y) {
		return toPosition(x, y, mapWidth);
	}

	public static int toPosition(int x, int y, int width) {
		return (y * width) + x;
	}
	
	public MapLocation fromIndex(int index) {
		return new MapLocation(index % mapWidth, index / mapWidth);
	}
	
	//endregion
}
