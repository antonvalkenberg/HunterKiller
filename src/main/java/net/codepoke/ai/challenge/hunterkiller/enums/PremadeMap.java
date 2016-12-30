package net.codepoke.ai.challenge.hunterkiller.enums;

import lombok.AllArgsConstructor;
import net.codepoke.ai.challenge.hunterkiller.FourPatch;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Unit;

/**
 * Defines a list of pre-made maps (limited to the top-left quadrant since this is copied).
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
@AllArgsConstructor
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
   * The position of the {@link net.codepoke.ai.challenge.hunterkiller.gameobjects.mapfeature.Base
   * Base} on the quadrant.
   */
  public int basePosition;
  /**
   * The {@link Direction} to which the base spawns it's {@link Unit}s.
   */
  public Direction spawnDirection;
  
}
