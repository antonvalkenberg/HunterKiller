package net.codepoke.ai.challenge.hunterkiller;

import net.codepoke.ai.challenge.hunterkiller.enums.TileType;

/**
 * This class represents an alternative form of the ninepatch concept. It defines the left top
 * quadrant of the board. the quadrantWidth and quadrantHeight values describe the bounds on the
 * plane that needs to be mirrored in the x and y direction respectively. This allows easy
 * symmetrical boards with odd length or widths!
 * 
 * @author Pieter Schaap <pieter.schaap@codepoke.net>
 *
 */
public class FourPatch {
  
  /**
   * The width of the top left quadrant that is being copied 4 times
   */
  protected int quadrantWidth;
  
  /**
   * The height of the top left quadrant that is being copied 4 times
   */
  protected int quadrantHeight;
  
  // Note: data is defined as follows: [y][x][objects]!
  protected TileType[][][] data;
  
  public FourPatch(TileType[][][] data, int quadrantWidth, int quadrantHeight) {
    this.data = data;
    this.quadrantWidth = quadrantWidth;
    this.quadrantHeight = quadrantHeight;
  }
  
  /**
   * Given a newline separated string with TileType based strings, this will convert it to the
   * correct FourPatch
   * 
   * @param data
   * @param quadrantWidth
   * @param quadrantHeight
   */
  public FourPatch(String data, int quadrantWidth, int quadrantHeight) {
    this.data = parseData(data);
    this.quadrantWidth = quadrantWidth;
    this.quadrantHeight = quadrantHeight;
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
    
    TileType[][][] output = new TileType[lines.length][lines[0].length()][1];
    
    for(int i = 0; i < lines.length; i++) {
      String line = lines[i];
      
      for(int j = 0; j < line.length(); j++) {
        
        // Convert everything to the tile matchup
        for(TileType t : TileType.values())
          if(t.txt.equals(line.charAt(j) + "")) {
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
