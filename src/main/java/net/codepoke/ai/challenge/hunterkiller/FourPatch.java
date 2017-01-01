package net.codepoke.ai.challenge.hunterkiller;

/**
 * Represents a class that enables the construction of a grid based on a predefined part. This
 * predefined part is used to build up the grid by mirroring and copying quadrants of it.
 * 
 * Note: this class assumes the coordinate (0,0) to be the top-left point in the grid. The X-axis is
 * increasing towards the right, while the Y-axis increases downward.
 * 
 * <pre>
 * The predefined part of the grid is assumed to have the following quadrants:
 * 
 * A | B
 * - + -
 * C | D
 * 
 * Where quadrants A has dimensions as defined in the properties {@link FourPatch#quadrantAWidth} and {@link FourPatch#quadrantAHeight}.
 * - A is mirrored and copied over 3 times.
 * - B is mirrored and copied over only the Y axis.
 * - C is mirrored and copied over only the X axis.
 * - D is not copied at all.
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
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
public class FourPatch {
  
  /**
   * Responsible for the domain specific conversion of a data char into an instance, given it's
   * location and section on the grid.
   */
  public static interface DataCreation {
    
    /**
     * Create a new instance on a location in the grid.
     * 
     * @param data
     *          The data that represents what instance should be created
     * @param x
     *          The X location on the grid
     * @param y
     *          The Y location on the grid
     * @param sectionIndex
     *          The index of the location's section, according to the following definition:
     * 
     * <pre>
     *          0 | 1 | 2
     *          - + - + -
     *          3 | 4 | 5
     *          - + - + -
     *          6 | 7 | 8
     *          
     * </pre>
     * 
     *          Which follows the quadrant declaration of {@link FourPatch}
     */
    public void create(char data, int x, int y, int sectionIndex);
    
  }
  
  /**
   * An implementation of how to create instances out of the data that is defined in this FourPatch.
   */
  protected DataCreation creation;
  
  /**
   * The width of quadrant 'A' of the predefined part that is being copied.
   */
  protected int quadrantAWidth = -1;
  
  /**
   * The height of quadrant 'A' of the predefined part that is being copied.
   */
  protected int quadrantAHeight = -1;
  
  /**
   * The textual representation of the predefined part that is being used to create the grid. Note
   * that this data is stored as [y][x], because of how we move through the data when creating it.
   */
  protected char[][] data;
  
  /**
   * Create a new FourPatch, where quadrant A has a specific width and height.
   * 
   * @param creation
   *          An implementation of how to create instances out of the data.
   * @param data
   *          The textual representation of the predefined part that is being used to create the
   *          grid. Note that this should be newline separated.
   * @param quadrantAWidth
   *          The width of quadrant A in the {@link FourPatch}.
   * @param quadrantAHeight
   *          The height of quadrant A in the {@link FourPatch}.
   */
  public FourPatch(DataCreation creation, String data, int quadrantAWidth, int quadrantAHeight) {
    this.creation = creation;
    this.quadrantAWidth = quadrantAWidth;
    this.quadrantAHeight = quadrantAHeight;
    //Note that quadrantAWidth and quadrantAHeight should be set before the 'parseData' call.
    this.data = parseData(data);
  }
  
  /**
   * Create a new FourPatch, where the dimensions of quadrant A are the same as the predefined part.
   * 
   * {@link FourPatch#FourPatch(String, int, int)}
   */
  public FourPatch(DataCreation creation, String data) {
    this.data = parseData(data);
  }
  
  /**
   * Parses the data string into the quadrants as defined by {@link FourPatch}. Note: this method
   * sets the values of {@link FourPatch#quadrantAWidth} and {@link FourPatch#quadrantAHeight} to be
   * equal to the provided data's dimensions if they have not been set yet.
   * 
   * @param data
   *          The textual representation of the predefined part.
   * @return 2-dimensional char Array representing the predefined part of the grid.
   */
  private char[][] parseData(String dataString) {
    //Split the data into rows
    String[] rows = dataString.split("\n");
    //Check if anything was found
    if(rows.length <= 0)
      return new char[0][0];
    //Check which row is longest
    int maxRowLength = rows[0].length();
    for(int i = 1; i < rows.length; i++) {
      if(rows[i].length() > maxRowLength)
        maxRowLength = rows[i].length();
    }
    
    //Check if quadrant A's dimensions need to be set
    if(quadrantAWidth < 0)
      quadrantAWidth = maxRowLength;
    if(quadrantAHeight < 0)
      quadrantAHeight = rows.length;
    
    //Initialise the data
    char[][] parsedData = new char[rows.length][maxRowLength];
    //Move through the rows along the Y-axis
    for(int y = 0; y < rows.length; y++) {
      String row = rows[y];
      //Move through this row along the X-axis
      for(int x = 0; x < row.length(); x++) {
        //Note: using [y][x] here, because we move through X before Y
        parsedData[y][x] = row.charAt(x);
      }
    }
    
    return parsedData;
  }
  
  /**
   * Creates the grid. This method calls {@link DataCreation#create(char, int, int, int)} for each
   * location on the grid.
   */
  public void createGrid() {
    //Initialise the width and height of the grid.
    int gridWidth = data[0].length + quadrantAWidth;
    int gridHeight = data.length + quadrantAHeight;
    
    //Note: To get a mirrored coordinate, the following steps are taken:
    //  - take the total dimension of the grid (width for X, height for Y)
    //  - minus 1 because coordinates start at 0
    //  - minus the current value of the coordinate
    
    //Move through the predefined part of the grid. Note: using [y][x] again because of how we created the data array.
    for(int y = 0; y < data.length; y++) {
      for(int x = 0; x < data[y].length; x++) {
        //Check which quadrant this location on the predefined part is in.
        if(x < quadrantAWidth && y < quadrantAHeight) {
          //Quadrant A, this exists 4 times on the grid.
          //The first copy is on the same coordinates, in section 0.
          creation.create(data[y][x], x, y, 0);
          //The second copy is on the same y coordinate, but the x coordinate is mirrored, in section 2.
          creation.create(data[y][x], ((gridWidth - 1) - x), y, 2);
          //The third copy is on the same x coordinate, but the y coordinate is mirrored, in section 6.
          creation.create(data[y][x], x, ((gridHeight - 1) - y), 6);
          //The fourth copy is on mirrored x and mirrored y, in section 8.
          creation.create(data[y][x], ((gridWidth - 1) - x), ((gridHeight - 1) - y), 8);
        }
        else if(x >= quadrantAWidth && y < quadrantAHeight) {
          //Quadrant B, this exists twice on the grid, only mirrored in the Y axis.
          //The first copy is on the same coordinates, in section 1.
          creation.create(data[y][x], x, y, 1);
          //The second copy is on the same x coordinate, but the y coordinate is mirrored, in section 7.
          creation.create(data[y][x], x, ((gridHeight - 1) - y), 7);
        }
        else if(x < quadrantAWidth && y >= quadrantAHeight) {
          //Quadrant C, this exists twice on the grid, only mirrored in the X axis.
          //The first copy is on the same coordinates, in section 3.
          creation.create(data[y][x], x, y, 3);
          //The second copy is on the same y coordinate, but the x coordinate is mirrored, in section 5.
          creation.create(data[y][x], ((gridWidth - 1) - x), y, 5);
        }
        else {
          //Quadrant D, this exists only once on the grid, in section 4.
          creation.create(data[y][x], x, y, 4);
        }
      }
    }
  }
  
  /**
   * Returns the width of the grid that will be created.
   * 
   * @return
   */
  public int getGridWidth() {
    return data[0].length + quadrantAWidth;
  }
  
  /**
   * Returns the height of the grid that will be created.
   * 
   * @return
   */
  public int getGridHeight() {
    return data.length + quadrantAHeight;
  }
  
}
