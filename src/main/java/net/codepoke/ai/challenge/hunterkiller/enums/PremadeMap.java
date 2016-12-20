package main.java.net.codepoke.ai.challenge.hunterkiller.enums;

import main.java.net.codepoke.ai.challenge.hunterkiller.FourPatch;
import main.java.net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Unit;

/**
 * Defines a list of pre-made Maps (limited to the top-left quadrant since this is copied).
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
public enum PremadeMap {
  //@formatter:off
  TEST("Test", new FourPatch( "[[[\n"
                            + "[__\n"
                            + "[__\n", 3, 3), 4, Direction.SOUTH);
  
  //@formatter:on
  
  /**
   * The name of the map.
   */
  public String name;
  /**
   * The {@link FourPatch} to create the map with.
   */
  public FourPatch fourPatch;
  /**
   * The position of the {@link Base} on the quadrant.
   */
  public int basePosition;
  /**
   * The {@link Direction} to which the base spawns it's {@link Unit}s.
   */
  public Direction spawnDirection;
  
  /**
   * Create a new instance of a PremadeMap.
   * 
   * @param name
   *          The map's name.
   * @param fourPatch
   *          The FourPatch to create the map with.
   * @param basePosition
   *          The position where the base is located on the quadrant.
   * @param spawnDirection
   *          The location where the base spawns it's units.
   */
  private PremadeMap(String name, FourPatch fourPatch, int basePosition, Direction spawnDirection) {
    this.name = name;
    this.fourPatch = fourPatch;
    this.basePosition = basePosition;
    this.spawnDirection = spawnDirection;
  }
  
}
