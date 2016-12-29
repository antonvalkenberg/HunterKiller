package net.codepoke.ai.challenge.hunterkiller;

import net.codepoke.ai.challenge.hunterkiller.enums.TileType;

/**
 * Represents a grid of which parts needs to be mirrored over the X and Y axis.
 * 
 * <pre>
 * Given a grid, it will be carved up as follows:
 * 
 * A | B
 * - + -
 * C | D
 * 
 * Where A is the area defined by repeatX and repeatY.
 * - A is mirrored and copied over 4 times (X & Y)
 * - B is mirrored and copied over the X axis
 * - C is mirrored and copied over the Y axis.
 * - D is left as is.
 * 
 * This results in the following grid:
 * 
 *  A | B | A
 *  - + - + -
 *  C | D | C
 *  - + - + -
 *  A | B | A
 * </pre>
 * 
 * @author Pieter Schaap <pieter.schaap@codepoke.net>
 *
 */
public class FourPatch {

	/**
	 * Responsible for the domain specific conversion of a data char into an instance, given the location and quadrant.
	 */
	public static interface QuadrantSetup {

		/**
		 * 
		 * @param data
		 * @param x
		 *            The X location on the matrix
		 * @param y
		 *            The Y location on the matrix
		 * @param quadrant
		 *            The index of one of the quadrant, according to:
		 * 
		 *            <pre>
		 *            0 | 1 | 2 
		 *            - + - + -
		 *            3 | 4 | 5
		 *            - + - + -
		 *            6 | 7 | 8
		 *            </pre>
		 * 
		 *            Which follows the quadrant declaration of {@link FourPatch}
		 */
		public void create(char data, int x, int y, int quadrant);

	}

	protected QuadrantSetup setup;

	/**
	 * The width of the top left quadrant that is being copied 4 times
	 */
	protected int repeatX = -1;

	/**
	 * The height of the top left quadrant that is being copied 4 times
	 */
	protected int repeatY = -1;

	// Note: data is defined as follows: [y][x][objects]!
	protected TileType[][][] data;

	public FourPatch(TileType[][][] data, int quadrantWidth, int quadrantHeight) {
		this.repeatX = quadrantWidth;
		this.repeatY = quadrantHeight;
		this.data = data;
	}

	/**
	 * Given a newline separated string with TileType based strings, this will convert it to the
	 * correct FourPatch
	 */
	public FourPatch(String data, int quadrantWidth, int quadrantHeight) {
		this.repeatX = quadrantWidth;
		this.repeatY = quadrantHeight;
		this.data = parseData(data);
	}

	public FourPatch(String data) {
		this.data = parseData(data);
	}

	/**
	 * Parses a String into a 3-dimensional {@link TileType} array. Representing a {@link Map}
	 * configuration.
	 * 
	 * For example input look at {@link TileType}.
	 * 
	 * The meaning of the string corresponds to the String value set in the TileType enum. Note that
	 * this method does not support multiple objects at the same position.
	 */
	public TileType[][][] parseData(String data) {
		String[] lines = data.split("\n");

		// If no quadrant is given, assume that the given data block needs to be copied wholly.
		if (repeatY == -1 || repeatX == -1) {
			repeatY = lines.length;
			repeatX = lines[0].length();
		}

		TileType[][][] output = new TileType[lines.length][lines[0].length()][1];

		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];

			for (int j = 0; j < line.length(); j++) {

				// Convert everything to the tile matchup
				for (TileType t : TileType.values())
					if (t.txt.equals(line.charAt(j) + "")) {
						output[i][j][0] = t;
						break;
					}
			}

		}

		return output;
	}

	/**
	 * The data representing the tiles of the fourpatch. note that it is supposed to be formatted as
	 * [y][x][objects]. Otherwise different uses will be very confusing and hard to debug!
	 * 
	 * @return
	 */
	public TileType[][][] getData() {
		return data;
	}

}
