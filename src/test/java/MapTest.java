package test.java;

import static org.junit.Assert.assertEquals;
import main.java.net.codepoke.ai.challenge.hunterkiller.HunterKillerStateFactory;
import main.java.net.codepoke.ai.challenge.hunterkiller.Map;
import main.java.net.codepoke.ai.challenge.hunterkiller.MapLocation;
import main.java.net.codepoke.ai.challenge.hunterkiller.enums.PremadeMap;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test class for Map.
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
public class MapTest {
  
  //region Properties
  
  private Map testMap;
  private static final int testWidth = 4;
  private static final int testHeight = 4;
  
  //endregion
  
  //region Setup methods
  
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    
  }
  
  @AfterClass
  public static void tearDownAfterClass() throws Exception {
    
  }
  
  @Before
  public void setUpBeforeMethod() throws Exception {
    testMap = new Map(testWidth, testHeight);
  }
  
  @After
  public void tearDownAfterMethod() throws Exception {
    testMap = null;
  }
  
  //endregion
  
  //region Test methods
  
  @Test
  public void testConversion() {
    int mapWidth = testMap.getMapWidth();
    
    //Test one
    MapLocation base = new MapLocation(0, 0);
    MapLocation transBase = testMap.toLocation(0);
    assertEquals(0, Map.toPosition(base, mapWidth));
    assertEquals(0, Map.toPosition(0, 0, mapWidth));
    assertEquals(0, transBase.getX());
    assertEquals(0, transBase.getY());
    
    //Test two
    MapLocation loc = new MapLocation(2, 1);
    MapLocation transLoc = testMap.toLocation(6);
    assertEquals(6, Map.toPosition(loc, mapWidth));
    assertEquals(6, Map.toPosition(2, 1, mapWidth));
    assertEquals(6, Map.toPosition(loc.getX(), loc.getY(), mapWidth));
    assertEquals(2, transLoc.getX());
    assertEquals(1, transLoc.getY());
  }
  
  @Test
  public void testDistances() {
    double allowedErrorMargin = 0.001;
    
    //Test one
    MapLocation location1 = new MapLocation(0, 0);
    MapLocation location2 = new MapLocation(2, 1);
    assertEquals(Math.sqrt(5.0), MapLocation.getEuclideanDist(location1, location2), allowedErrorMargin);
    assertEquals(Math.sqrt(5.0), MapLocation.getEuclideanDist(location1.getX(), location1.getY(), location2.getX(), location2.getY()), allowedErrorMargin);
    assertEquals(3, MapLocation.getManhattanDist(location1, location2));
    assertEquals(3, MapLocation.getManhattanDist(location1.getX(), location1.getY(), location2.getX(), location2.getY()));
    
    //Test two
    MapLocation location3 = new MapLocation(2, 1);
    MapLocation location4 = new MapLocation(0, 0);
    assertEquals(Math.sqrt(5.0), MapLocation.getEuclideanDist(location3, location4), allowedErrorMargin);
    assertEquals(Math.sqrt(5.0), MapLocation.getEuclideanDist(location3.getX(), location3.getY(), location4.getX(), location4.getY()), allowedErrorMargin);
    assertEquals(3, MapLocation.getManhattanDist(location3, location4));
    assertEquals(3, MapLocation.getManhattanDist(location3.getX(), location3.getY(), location4.getX(), location4.getY()));
    
    //Test three
    MapLocation location5 = new MapLocation(3, 3);
    MapLocation location6 = new MapLocation(3, 3);
    assertEquals(0.0, MapLocation.getEuclideanDist(location5, location6), allowedErrorMargin);
    assertEquals(0.0, MapLocation.getEuclideanDist(location5.getX(), location5.getY(), location6.getX(), location6.getY()), allowedErrorMargin);
    assertEquals(0, MapLocation.getManhattanDist(location5, location6));
    assertEquals(0, MapLocation.getManhattanDist(location5.getX(), location5.getY(), location6.getX(), location6.getY()));
  }
  
  @Test
  public void testCreation() {
    //Test the creation from a pre made map
    Map createdMap = HunterKillerStateFactory.constructMap(PremadeMap.TEST);
    String x = createdMap.toString();
    //TODO: test
  }
  
  //endregion
}
