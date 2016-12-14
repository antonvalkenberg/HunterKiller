package main.java.net.codepoke.ai.challenge.hunterkiller;

import main.java.net.codepoke.ai.challenge.hunterkiller.enums.TileTypes;

/**
 * This class represents an alternative form of the ninepatch concept. It defines the left top quadrant of the board.
 * the repeatX and repeatY values describe the bounds on the plane that needs to be mirrored in the x and y direction
 * respectively. This allows easy symmetrical boards with odd length or widths!
 * 
 * @author Pieter Schaap <pieter.schaap@codepoke.net>
 *
 */
public class FourPatch {
	/**
	 * The length and width of the top left quadrant that is being copied 4 times
	 */
	protected int repeatX, repeatY;

	// Note: data is defined as follows: [y][x][objects]!
	protected TileTypes[][][] data;

	public FourPatch(TileTypes[][][] data, int repeatX, int repeatY) {
		this.data = data;
		this.repeatX = repeatX;
		this.repeatY = repeatY;
	}

	/**
	 * Given a newline separated string with Tile based strings, this will convert it to the correct FourPatch
	 * 
	 * @param data
	 * @param repeatX
	 * @param repeatY
	 */
	public FourPatch(String data, int repeatX, int repeatY) {
		this.data = parseData(data);
		this.repeatX = repeatX;
		this.repeatY = repeatY;
	}

	/**
	 * Parses a String into a 3-dimensional {@link TileTypes} array. Representing a {@link Map} configuration.
	 * 
	 * For example input look at {@link TileTypes}.
	 * 
	 * The meaning of the string corresponds to the String value set in the TileTypes enum.
	 * Note that this method does not support multiple objects at the same position.
	 */
	public TileTypes[][][] parseData(String data) {
		String[] lines = data.split("\n");

		TileTypes[][][] output = new TileTypes[lines.length][lines[0].length()][1];

		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];

			for (int j = 0; j < line.length(); j++) {

				// Convert everything to the tile matchup
				for (TileTypes t : TileTypes.values())
					if (t.txt.equals(line.charAt(j) + "")) {
						output[i][j][0] = t;
						break;
					}
			}

		}

		return output;
	}

	/**
	 * The data representing the tiles of the fourpatch. note that it is supposed to be formatted as [y][x][objects].
	 * Otherwise different uses will be very confusing and hard to debug!
	 * 
	 * @return
	 */
	public TileTypes[][][] getData() {
		return data;
	}

}