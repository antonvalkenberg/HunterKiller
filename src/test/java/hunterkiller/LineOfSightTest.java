package hunterkiller;

import static org.junit.Assert.assertTrue;

import java.util.HashSet;

import net.codepoke.ai.challenge.hunterkiller.Map;
import net.codepoke.ai.challenge.hunterkiller.MapLocation;
import net.codepoke.ai.challenge.hunterkiller.enums.Direction;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.GameObject;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.mapfeature.Floor;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.mapfeature.Wall;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Infected;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Soldier;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test class for LineOfSight.
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
public class LineOfSightTest {

	private Map testMap;
	private static final int testWidth = 4;
	private static final int testHeight = 4;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {

	}

	@Before
	public void setUp() throws Exception {
		testMap = new Map(testWidth, testHeight);
	}

	@After
	public void tearDown() throws Exception {

	}

	/**
	 * Tests a line-of-sight setup with no obstacles:
	 * 
	 * <pre>
	 *    - - - -
	 *    - U - -
	 *    - - - -
	 *    - - - -
	 * </pre>
	 * 
	 * Note that in this diagram, 'U' stands for the Unit and '-' stands for Floor tiles. This setup
	 * is tested with different Unit orientations and once for an Infected.
	 */
	@Test
	public void testOpenVision() {
		// Fill map with Floors
		GameObject[][] mapContent = new GameObject[testMap.getMapWidth() * testMap.getMapHeight()][Map.INTERNAL_MAP_LAYERS];
		for (int i = 0; i < mapContent.length; i++) {
			mapContent[i][Map.INTERNAL_MAP_FEATURE_INDEX] = new Floor(testMap.requestNewGameObjectID(), testMap.toLocation(i));
		}
		testMap.setMapContent(mapContent);

		// In the next sections, when visualising the FOV; '.' refers to visible tiles, and '#' refers to obscured
		// tiles.

		// Create a new soldier at [1,1] facing NORTH
		Soldier northSoldier = new Soldier(testMap.requestNewGameObjectID(), 0, new MapLocation(1, 1), Direction.NORTH);
		// Get the field-of-view for the north-facing soldier
		HashSet<MapLocation> northFOV = testMap.getFieldOfView(northSoldier);
		// Check that the FOV looks like:
		// . . . #
		// # U # #
		// # # # #
		// # # # #
		assertTrue(northFOV.size() == 4);
		assertTrue(northFOV.contains(new MapLocation(0, 0)));
		assertTrue(northFOV.contains(new MapLocation(1, 0)));
		assertTrue(northFOV.contains(new MapLocation(2, 0)));
		assertTrue(northFOV.contains(new MapLocation(1, 1)));

		// Create a new soldier at [1,1] facing EAST
		Soldier eastSoldier = new Soldier(testMap.requestNewGameObjectID(), 0, new MapLocation(1, 1), Direction.EAST);
		// Get the field-of-view for the east-facing soldier
		HashSet<MapLocation> eastFOV = testMap.getFieldOfView(eastSoldier);
		// Check that the FOV looks like:
		// # # . .
		// # U . .
		// # # . .
		// # # # #
		assertTrue(eastFOV.size() == 7);
		assertTrue(eastFOV.contains(new MapLocation(2, 0)));
		assertTrue(eastFOV.contains(new MapLocation(3, 0)));
		assertTrue(eastFOV.contains(new MapLocation(1, 1)));
		assertTrue(eastFOV.contains(new MapLocation(2, 1)));
		assertTrue(eastFOV.contains(new MapLocation(3, 1)));
		assertTrue(eastFOV.contains(new MapLocation(2, 2)));
		assertTrue(eastFOV.contains(new MapLocation(3, 2)));

		// Create a new soldier at [1,1] facing SOUTH
		Soldier southSoldier = new Soldier(testMap.requestNewGameObjectID(), 0, new MapLocation(1, 1), Direction.SOUTH);
		// Get the field-of-view for the south-facing soldier
		HashSet<MapLocation> southFOV = testMap.getFieldOfView(southSoldier);
		// Check that the FOV looks like:
		// # # # #
		// # U # #
		// . . . #
		// . . . #
		assertTrue(southFOV.size() == 7);
		assertTrue(southFOV.contains(new MapLocation(1, 1)));
		assertTrue(southFOV.contains(new MapLocation(0, 2)));
		assertTrue(southFOV.contains(new MapLocation(1, 2)));
		assertTrue(southFOV.contains(new MapLocation(2, 2)));
		assertTrue(southFOV.contains(new MapLocation(0, 3)));
		assertTrue(southFOV.contains(new MapLocation(1, 3)));
		assertTrue(southFOV.contains(new MapLocation(2, 3)));

		// Create a new soldier at [1,1] facing WEST
		Soldier westSoldier = new Soldier(testMap.requestNewGameObjectID(), 0, new MapLocation(1, 1), Direction.WEST);
		// Get the field-of-view for the west-facing soldier
		HashSet<MapLocation> westFOV = testMap.getFieldOfView(westSoldier);
		// Check that the FOV looks like:
		// . # # #
		// . U # #
		// . # # #
		// # # # #
		assertTrue(westFOV.size() == 4);
		assertTrue(westFOV.contains(new MapLocation(0, 0)));
		assertTrue(westFOV.contains(new MapLocation(0, 1)));
		assertTrue(westFOV.contains(new MapLocation(1, 1)));
		assertTrue(westFOV.contains(new MapLocation(0, 2)));

		// Create a new infected at [1,2]
		Infected infected = new Infected(testMap.requestNewGameObjectID(), 0, new MapLocation(1, 2), Direction.NORTH);
		// Get the field-of-view for the infected
		HashSet<MapLocation> infectedFOV = testMap.getFieldOfView(infected);
		// Check that the FOV looks like:
		// . . . .
		// . . . .
		// . U . .
		// . . . .
		assertTrue(infectedFOV.size() == 16);
		assertTrue(infectedFOV.contains(new MapLocation(0, 0)));
		assertTrue(infectedFOV.contains(new MapLocation(0, 1)));
		assertTrue(infectedFOV.contains(new MapLocation(0, 2)));
		assertTrue(infectedFOV.contains(new MapLocation(0, 3)));
		assertTrue(infectedFOV.contains(new MapLocation(1, 0)));
		assertTrue(infectedFOV.contains(new MapLocation(1, 1)));
		assertTrue(infectedFOV.contains(new MapLocation(1, 2)));
		assertTrue(infectedFOV.contains(new MapLocation(1, 3)));
		assertTrue(infectedFOV.contains(new MapLocation(2, 0)));
		assertTrue(infectedFOV.contains(new MapLocation(2, 1)));
		assertTrue(infectedFOV.contains(new MapLocation(2, 2)));
		assertTrue(infectedFOV.contains(new MapLocation(2, 3)));
		assertTrue(infectedFOV.contains(new MapLocation(3, 0)));
		assertTrue(infectedFOV.contains(new MapLocation(3, 1)));
		assertTrue(infectedFOV.contains(new MapLocation(3, 2)));
		assertTrue(infectedFOV.contains(new MapLocation(3, 3)));
	}

	/**
	 * Tests a line-of-sight setup where a Unit is against a wall:
	 * 
	 * <pre>
	 *    - - [ -
	 *    - U [ -
	 *    - - [ -
	 *    - - [ -
	 * </pre>
	 * 
	 * Note that in this diagram, 'U' stands for the Unit, '[' stands for a Wall tile, and '-' stands
	 * for a Floor tile. This setup is tested with different Unit orientations and once for an
	 * Infected.
	 */
	@Test
	public void testWallVision() {
		// Fill map with Floors, except for the first column
		GameObject[][] mapContent = new GameObject[testMap.getMapWidth() * testMap.getMapHeight()][Map.INTERNAL_MAP_LAYERS];
		for (int i = 0; i < mapContent.length; i++) {
			MapLocation location = testMap.toLocation(i);
			if (location.getX() == 2) {
				mapContent[i][Map.INTERNAL_MAP_FEATURE_INDEX] = new Wall(testMap.requestNewGameObjectID(), location);
			} else {
				mapContent[i][Map.INTERNAL_MAP_FEATURE_INDEX] = new Floor(testMap.requestNewGameObjectID(), location);
			}
		}
		testMap.setMapContent(mapContent);

		// In the next sections, when visualising the FOV; '.' refers to visible tiles, and '#' refers to obscured
		// tiles.

		// Create a new soldier at [1,1] facing NORTH
		Soldier northSoldier = new Soldier(testMap.requestNewGameObjectID(), 0, new MapLocation(1, 1), Direction.NORTH);
		// Get the field-of-view for the north-facing soldier
		HashSet<MapLocation> northFOV = testMap.getFieldOfView(northSoldier);
		// Check that the FOV looks like:
		// . . . #
		// # U # #
		// # # # #
		// # # # #
		assertTrue(northFOV.size() == 4);
		assertTrue(northFOV.contains(new MapLocation(0, 0)));
		assertTrue(northFOV.contains(new MapLocation(1, 0)));
		assertTrue(northFOV.contains(new MapLocation(2, 0)));
		assertTrue(northFOV.contains(new MapLocation(1, 1)));

		// Create a new soldier at [1,1] facing EAST
		Soldier eastSoldier = new Soldier(testMap.requestNewGameObjectID(), 0, new MapLocation(1, 1), Direction.EAST);
		// Get the field-of-view for the east-facing soldier
		HashSet<MapLocation> eastFOV = testMap.getFieldOfView(eastSoldier);
		// Check that the FOV looks like:
		// # # . #
		// # U . #
		// # # . #
		// # # # #
		assertTrue(eastFOV.size() == 4);
		assertTrue(eastFOV.contains(new MapLocation(2, 0)));
		assertTrue(eastFOV.contains(new MapLocation(1, 1)));
		assertTrue(eastFOV.contains(new MapLocation(2, 1)));
		assertTrue(eastFOV.contains(new MapLocation(2, 2)));

		// Create a new soldier at [1,1] facing SOUTH
		Soldier southSoldier = new Soldier(testMap.requestNewGameObjectID(), 0, new MapLocation(1, 1), Direction.SOUTH);
		// Get the field-of-view for the south-facing soldier
		HashSet<MapLocation> southFOV = testMap.getFieldOfView(southSoldier);
		// Check that the FOV looks like:
		// # # # #
		// # U # #
		// . . . #
		// . . . #
		assertTrue(southFOV.size() == 7);
		assertTrue(southFOV.contains(new MapLocation(1, 1)));
		assertTrue(southFOV.contains(new MapLocation(0, 2)));
		assertTrue(southFOV.contains(new MapLocation(1, 2)));
		assertTrue(southFOV.contains(new MapLocation(2, 2)));
		assertTrue(southFOV.contains(new MapLocation(0, 3)));
		assertTrue(southFOV.contains(new MapLocation(1, 3)));
		assertTrue(southFOV.contains(new MapLocation(2, 3)));

		// Create a new soldier at [1,1] facing WEST
		Soldier westSoldier = new Soldier(testMap.requestNewGameObjectID(), 0, new MapLocation(1, 1), Direction.WEST);
		// Get the field-of-view for the west-facing soldier
		HashSet<MapLocation> westFOV = testMap.getFieldOfView(westSoldier);
		// Check that the FOV looks like:
		// . # # #
		// . U # #
		// . # # #
		// # # # #
		assertTrue(westFOV.size() == 4);
		assertTrue(westFOV.contains(new MapLocation(0, 0)));
		assertTrue(westFOV.contains(new MapLocation(0, 1)));
		assertTrue(westFOV.contains(new MapLocation(1, 1)));
		assertTrue(westFOV.contains(new MapLocation(0, 2)));

		// Create a new infected at [1,2]
		Infected infected = new Infected(testMap.requestNewGameObjectID(), 0, new MapLocation(1, 2), Direction.NORTH);
		// Get the field-of-view for the infected
		HashSet<MapLocation> infectedFOV = testMap.getFieldOfView(infected);
		// Check that the FOV looks like:
		// . . . #
		// . . . #
		// . U . #
		// . . . #
		assertTrue(infectedFOV.size() == 12);
		assertTrue(infectedFOV.contains(new MapLocation(0, 0)));
		assertTrue(infectedFOV.contains(new MapLocation(0, 1)));
		assertTrue(infectedFOV.contains(new MapLocation(0, 2)));
		assertTrue(infectedFOV.contains(new MapLocation(0, 3)));
		assertTrue(infectedFOV.contains(new MapLocation(1, 0)));
		assertTrue(infectedFOV.contains(new MapLocation(1, 1)));
		assertTrue(infectedFOV.contains(new MapLocation(1, 2)));
		assertTrue(infectedFOV.contains(new MapLocation(1, 3)));
		assertTrue(infectedFOV.contains(new MapLocation(2, 0)));
		assertTrue(infectedFOV.contains(new MapLocation(2, 1)));
		assertTrue(infectedFOV.contains(new MapLocation(2, 2)));
		assertTrue(infectedFOV.contains(new MapLocation(2, 3)));
	}

	/**
	 * Tests a line-of-sight setup where a Unit is against a wall:
	 * 
	 * <pre>
	 *    U - - -
	 *    [ - - -
	 *    - - - -
	 *    - - - -
	 * </pre>
	 * 
	 * Note that in this diagram, 'U' stands for the Unit, '[' stands for a Wall tile, and '-' stands
	 * for a Floor tile. This setup is tested with the Unit in EAST and SOUTH orientation and once for
	 * an Infected.
	 */
	@Test
	public void testCornerVision() {
		// Fill map with Floors, except for position #4
		GameObject[][] mapContent = new GameObject[testMap.getMapWidth() * testMap.getMapHeight()][Map.INTERNAL_MAP_LAYERS];
		for (int i = 0; i < mapContent.length; i++) {
			MapLocation location = testMap.toLocation(i);
			if (i == 4) {
				mapContent[i][Map.INTERNAL_MAP_FEATURE_INDEX] = new Wall(testMap.requestNewGameObjectID(), location);
			} else {
				mapContent[i][Map.INTERNAL_MAP_FEATURE_INDEX] = new Floor(testMap.requestNewGameObjectID(), location);
			}
		}
		testMap.setMapContent(mapContent);

		// In the next sections, when visualising the FOV; '.' refers to visible tiles, and '#' refers to obscured
		// tiles.

		// Create a new soldier at [0,0] facing EAST
		Soldier eastSoldier = new Soldier(testMap.requestNewGameObjectID(), 0, new MapLocation(0, 0), Direction.EAST);
		// Get the field-of-view for the east-facing soldier
		HashSet<MapLocation> eastFOV = testMap.getFieldOfView(eastSoldier);
		// Check that the FOV looks like:
		// U . . .
		// # . . #
		// # # # #
		// # # # #
		assertTrue(eastFOV.size() == 6);
		assertTrue(eastFOV.contains(new MapLocation(0, 0)));
		assertTrue(eastFOV.contains(new MapLocation(1, 0)));
		assertTrue(eastFOV.contains(new MapLocation(2, 0)));
		assertTrue(eastFOV.contains(new MapLocation(3, 0)));
		assertTrue(eastFOV.contains(new MapLocation(1, 1)));
		assertTrue(eastFOV.contains(new MapLocation(2, 1)));

		// Create a new soldier at [0,0] facing SOUTH
		Soldier southSoldier = new Soldier(testMap.requestNewGameObjectID(), 0, new MapLocation(0, 0), Direction.SOUTH);
		// Get the field-of-view for the south-facing soldier
		HashSet<MapLocation> southFOV = testMap.getFieldOfView(southSoldier);
		// Check that the FOV looks like:
		// U # # #
		// . . # #
		// # . # #
		// # # # #
		assertTrue(southFOV.size() == 4);
		assertTrue(southFOV.contains(new MapLocation(0, 0)));
		assertTrue(southFOV.contains(new MapLocation(0, 1)));
		assertTrue(southFOV.contains(new MapLocation(1, 1)));
		assertTrue(southFOV.contains(new MapLocation(1, 2)));

		// Create a new infected at [0,0]
		Infected infected = new Infected(testMap.requestNewGameObjectID(), 0, new MapLocation(0, 0), Direction.NORTH);
		// Get the field-of-view for the infected
		HashSet<MapLocation> infectedFOV = testMap.getFieldOfView(infected);
		// Check that the FOV looks like:
		// U . . .
		// . . . .
		// # . . #
		// # # # #
		assertTrue(infectedFOV.size() == 10);
		assertTrue(infectedFOV.contains(new MapLocation(0, 0)));
		assertTrue(infectedFOV.contains(new MapLocation(1, 0)));
		assertTrue(infectedFOV.contains(new MapLocation(2, 0)));
		assertTrue(infectedFOV.contains(new MapLocation(3, 0)));
		assertTrue(infectedFOV.contains(new MapLocation(0, 1)));
		assertTrue(infectedFOV.contains(new MapLocation(1, 1)));
		assertTrue(infectedFOV.contains(new MapLocation(2, 1)));
		assertTrue(infectedFOV.contains(new MapLocation(3, 1)));
		assertTrue(infectedFOV.contains(new MapLocation(1, 2)));
		assertTrue(infectedFOV.contains(new MapLocation(2, 2)));
	}

	/**
	 * Tests a line-of-sight setup where a Unit is inside a room:
	 * 
	 * <pre>
	 *    - - - -
	 *    - [ [ [
	 *    - [ - -
	 *    - [ - U
	 * </pre>
	 * 
	 * Note that in this diagram, 'U' stands for the Unit, '[' stands for a Wall tile, and '-' stands
	 * for a Floor tile. This setup is tested with the Unit in NORTH and WEST orientation and once for
	 * an Infected.
	 */
	@Test
	public void testRoomVision() {
		// Fill map with Floors, except for positions #5, 6, 7, 9, 13
		GameObject[][] mapContent = new GameObject[testMap.getMapWidth() * testMap.getMapHeight()][Map.INTERNAL_MAP_LAYERS];
		for (int i = 0; i < mapContent.length; i++) {
			MapLocation location = testMap.toLocation(i);
			if (i == 5 || i == 6 || i == 7 || i == 9 || i == 13) {
				mapContent[i][Map.INTERNAL_MAP_FEATURE_INDEX] = new Wall(testMap.requestNewGameObjectID(), location);
			} else {
				mapContent[i][Map.INTERNAL_MAP_FEATURE_INDEX] = new Floor(testMap.requestNewGameObjectID(), location);
			}
		}
		testMap.setMapContent(mapContent);

		// In the next sections, when visualising the FOV; '.' refers to visible tiles, and '#' refers to obscured
		// tiles.

		// Create a new soldier at [3,3] facing WEST
		Soldier westSoldier = new Soldier(testMap.requestNewGameObjectID(), 0, new MapLocation(3, 3), Direction.WEST);
		// Get the field-of-view for the west-facing soldier
		HashSet<MapLocation> westFOV = testMap.getFieldOfView(westSoldier);
		// Check that the FOV looks like:
		// # # # #
		// # # # #
		// # . . #
		// # . . U
		assertTrue(westFOV.size() == 5);
		assertTrue(westFOV.contains(new MapLocation(3, 3)));
		assertTrue(westFOV.contains(new MapLocation(2, 3)));
		assertTrue(westFOV.contains(new MapLocation(1, 3)));
		assertTrue(westFOV.contains(new MapLocation(1, 2)));
		assertTrue(westFOV.contains(new MapLocation(2, 2)));

		// Create a new soldier at [3,3] facing NORTH
		Soldier northSoldier = new Soldier(testMap.requestNewGameObjectID(), 0, new MapLocation(3, 3), Direction.NORTH);
		// Get the field-of-view for the north-facing soldier
		HashSet<MapLocation> northFOV = testMap.getFieldOfView(northSoldier);
		// Check that the FOV looks like:
		// # # # #
		// # # . .
		// # # . .
		// # # # U
		assertTrue(northFOV.size() == 5);
		assertTrue(northFOV.contains(new MapLocation(3, 3)));
		assertTrue(northFOV.contains(new MapLocation(3, 2)));
		assertTrue(northFOV.contains(new MapLocation(3, 1)));
		assertTrue(northFOV.contains(new MapLocation(2, 2)));
		assertTrue(northFOV.contains(new MapLocation(2, 1)));

		// Create a new infected at [3,3]
		Infected infected = new Infected(testMap.requestNewGameObjectID(), 0, new MapLocation(3, 3), Direction.SOUTH);
		// Get the field-of-view for the infected
		HashSet<MapLocation> infectedFOV = testMap.getFieldOfView(infected);
		// Check that the FOV looks like:
		// # # # #
		// # . . .
		// # . . .
		// # . . U
		assertTrue(infectedFOV.size() == 9);
		assertTrue(infectedFOV.contains(new MapLocation(3, 3)));
		assertTrue(infectedFOV.contains(new MapLocation(2, 3)));
		assertTrue(infectedFOV.contains(new MapLocation(1, 3)));
		assertTrue(infectedFOV.contains(new MapLocation(3, 2)));
		assertTrue(infectedFOV.contains(new MapLocation(2, 2)));
		assertTrue(infectedFOV.contains(new MapLocation(1, 2)));
		assertTrue(infectedFOV.contains(new MapLocation(3, 1)));
		assertTrue(infectedFOV.contains(new MapLocation(2, 1)));
		assertTrue(infectedFOV.contains(new MapLocation(1, 1)));
	}

}
