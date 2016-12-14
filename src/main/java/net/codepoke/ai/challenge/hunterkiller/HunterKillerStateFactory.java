package main.java.net.codepoke.ai.challenge.hunterkiller;

import java.util.HashMap;
import java.util.Map.Entry;

import main.java.net.codepoke.ai.challenge.hunterkiller.enums.PremadeMaps;
import main.java.net.codepoke.ai.challenge.hunterkiller.enums.TileTypes;
import main.java.net.codepoke.ai.challenge.hunterkiller.gameobjects.GameObject;

/**
 * Class representing a {@link Generator} for a Map.
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
public class HunterKillerStateFactory {

	/** Constructs a board for the given {@link PremadeMaps} and amount of players */
	public static Map constructBoard(PremadeMaps premade, int playerCount) {

		if (premade != null) {
			FourPatch patch = premade.fourPatch;
			return constructFromFourPatch(patch, playerCount);
		}

		return null;
	}

	/**
	 * This method interprets a {@link FourPatch} and creates a full {@link Map} from the given fourpatch.
	 * 
	 * @param patch
	 * @param r
	 * @return
	 */
	public static Map constructFromFourPatch(FourPatch patch, int playerCount) {
		int boardWidth = patch.data[0].length + patch.repeatX;
		int boardHeight = patch.data.length + patch.repeatY;

		HashMap<Integer, GameObject[]> boardData = new HashMap<Integer, GameObject[]>();
		
		for (int y = 0; y < patch.data.length; y++) {
			for (int x = 0; x < patch.data[0].length; x++) {
				TileTypes[] tiles = patch.data[y][x];

				int[] indices = null;
				// Fully mirrored part of quadrant
				if (x < patch.repeatX && y < patch.repeatY) {
					// Find all 4 indices for every quadrant
					indices = new int[] { Map.toPosition(x, y, boardWidth), Map.toPosition(boardWidth - x - 1, y, boardWidth),
							Map.toPosition(x, boardHeight - y - 1, boardWidth),
							Map.toPosition(boardWidth - x - 1, boardHeight - y - 1, boardWidth) };
				} else if (x < patch.repeatX) {
					// mirror x, but not y (bottom left area)
					indices = new int[] { Map.toPosition(x, y, boardWidth), Map.toPosition(boardWidth - x - 1, y, boardWidth) };

				} else if (y < patch.repeatY) {
					// mirror y, but not x (bottom right area)
					indices = new int[] { Map.toPosition(x, y, boardWidth), Map.toPosition(x, boardHeight - y - 1, boardWidth) };

				} else {
					// We are in the do not mirror area (right bottom)
					indices = new int[] { Map.toPosition(x, y, boardWidth) };
				}

				// Add tiles to the previously determined indices
				for (int index : indices) {
					GameObject[] gameObjects = new GameObject[Map.INTERNAL_MAP_LAYERS];
					boardData.put(index, gameObjects);
					if (tiles != null) {
						for (TileTypes t : tiles) {
							if (t != null) {
								switch (t) {
								//TODO: implement
								default:
									System.err.println("UNHANDLED TILE TYPE!");
								}
							}
						}
					}
				}
			}
		}
		
		// We processed the fourpatch, time to build up the Map class
		Map result = new Map(boardWidth, boardHeight);

		// Place everything on the board
		for (Entry<Integer, GameObject[]> entry : boardData.entrySet()) {
			//TODO: implement
		}

		return result;
	}

}
