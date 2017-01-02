/**
 * 
 */
package hunterkiller;

import java.util.HashSet;
import net.codepoke.ai.challenge.hunterkiller.LineOfSight;
import net.codepoke.ai.challenge.hunterkiller.Map;
import net.codepoke.ai.challenge.hunterkiller.MapLocation;
import net.codepoke.ai.challenge.hunterkiller.enums.Direction;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.GameObject;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.mapfeature.Floor;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Soldier;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
public class LineOfSightTest {
  
  private LineOfSight los;
  private Map testMap;
  private static final int testWidth = 4;
  private static final int testHeight = 4;
  
  /**
   * @throws java.lang.Exception
   */
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    
  }
  
  /**
   * @throws java.lang.Exception
   */
  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }
  
  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    testMap = new Map(testWidth, testHeight);
    los = testMap.getLineOfSight();
  }
  
  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {
  }
  
  /**
   * Test method for
   * {@link net.codepoke.ai.challenge.hunterkiller.LineOfSight#compute(net.codepoke.ai.challenge.hunterkiller.MapLocation, int, net.codepoke.ai.challenge.hunterkiller.enums.Direction, float)}
   * .
   */
  @Test
  public void testComputeMapLocationIntDirectionFloat() {
    //Fill map with Floors
    GameObject[][] mapContent = new GameObject[testMap.getMapWidth() * testMap.getMapHeight()][Map.INTERNAL_MAP_LAYERS];
    for(int i = 0; i < mapContent.length; i++) {
      mapContent[i][Map.INTERNAL_MAP_FEATURE_INDEX] = new Floor(testMap.requestNewGameObjectID(), testMap.toLocation(i));
    }
    testMap.setMapContent(mapContent);
    
    Soldier soldier = new Soldier(testMap.requestNewGameObjectID(), 0, new MapLocation(0, 0), Direction.EAST);
    
    HashSet<MapLocation> fov = testMap.getFieldOfView(soldier);
    
    String test = "";
  }
  
}
