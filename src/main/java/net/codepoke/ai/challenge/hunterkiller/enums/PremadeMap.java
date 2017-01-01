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
  TEST("Test",   "[[[\n"
               + "[B_\n"
               + "[__\n", 3, 3, Direction.SOUTH);
  //@formatter:on
  
  /**
   * The name of the map.
   */
  public String name;
  /**
   * The data that will be copied around the map by {@link FourPatch}.
   */
  public String mapData;
  /**
   * The width of the quadrant 'A' in the {@link FourPatch}.
   */
  public int quadrantAWidth;
  /**
   * The height of the quadrant 'A' in the {@link FourPatch}.
   */
  public int quadrantAHeight;
  /**
   * The {@link Direction} to which the base spawns it's {@link Unit}s.
   */
  public Direction spawnDirection;
  
}
