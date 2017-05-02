package hunterkiller;

import static org.junit.Assert.assertTrue;

import java.util.HashSet;

import net.codepoke.ai.challenge.hunterkiller.HunterKillerConstants;
import net.codepoke.ai.challenge.hunterkiller.Map;
import net.codepoke.ai.challenge.hunterkiller.MapLocation;
import net.codepoke.ai.challenge.hunterkiller.enums.Direction;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.GameObject;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.mapfeature.Door;
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
public class LineOfSightTest
		extends HunterKillerTest {

	// region Properties

	private Map testMap;
	private static final int testWidth = 4;
	private static final int testHeight = 4;

	// endregion

	// region Setup methods

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		testMap = new Map("LoS_test", testWidth, testHeight);
	}

	@After
	public void tearDown() throws Exception {
		testMap = null;
	}

	// endregion

	// region Test methods

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
		GameObject[][] mapContent = new GameObject[testMap.getMapWidth() * testMap.getMapHeight()][HunterKillerConstants.MAP_INTERNAL_LAYERS];
		for (int i = 0; i < mapContent.length; i++) {
			Floor floor = new Floor(testMap.toLocation(i));
			testMap.registerGameObject(floor);
			mapContent[i][HunterKillerConstants.MAP_INTERNAL_FEATURE_INDEX] = floor;
		}
		testMap.setMapContent(mapContent);

		// In the next sections, when visualising the FOV; '.' refers to visible tiles, and '#' refers to obscured
		// tiles.

		// Create a new soldier at [1,1] facing NORTH
		MapLocation testLocation = new MapLocation(1, 1);
		Soldier soldier = new Soldier(0, HunterKillerConstants.GAMEOBJECT_NOT_PLACED, Direction.NORTH);
		testMap.registerGameObject(soldier);
		testMap.place(testLocation, soldier);

		// Get the field-of-view for the north-facing soldier
		HashSet<MapLocation> northFOV = testMap.getFieldOfView(soldier);
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

		// Face the soldier east
		soldier.setOrientation(Direction.EAST);
		// Get the field-of-view for the east-facing soldier
		HashSet<MapLocation> eastFOV = testMap.getFieldOfView(soldier);
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

		// Face the soldier south
		soldier.setOrientation(Direction.SOUTH);
		// Get the field-of-view for the south-facing soldier
		HashSet<MapLocation> southFOV = testMap.getFieldOfView(soldier);
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

		// Face the soldier west
		soldier.setOrientation(Direction.WEST);
		// Get the field-of-view for the west-facing soldier
		HashSet<MapLocation> westFOV = testMap.getFieldOfView(soldier);
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

		// We are done with our trusty soldier, so remove it
		testMap.unregisterGameObject(soldier);

		// Create a new infected at [1,2]
		Infected infected = new Infected(0, HunterKillerConstants.GAMEOBJECT_NOT_PLACED, Direction.NORTH);
		testMap.registerGameObject(infected);
		testMap.place(new MapLocation(1, 2), infected);
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
		GameObject[][] mapContent = new GameObject[testMap.getMapWidth() * testMap.getMapHeight()][HunterKillerConstants.MAP_INTERNAL_LAYERS];
		for (int i = 0; i < mapContent.length; i++) {
			MapLocation location = testMap.toLocation(i);
			if (location.getX() == 2) {
				Wall wall = new Wall(location);
				testMap.registerGameObject(wall);
				mapContent[i][HunterKillerConstants.MAP_INTERNAL_FEATURE_INDEX] = wall;
			} else {
				Floor floor = new Floor(location);
				testMap.registerGameObject(floor);
				mapContent[i][HunterKillerConstants.MAP_INTERNAL_FEATURE_INDEX] = floor;
			}
		}
		testMap.setMapContent(mapContent);

		// In the next sections, when visualising the FOV; '.' refers to visible tiles, and '#' refers to obscured
		// tiles.

		// Create a new soldier at [1,1] facing NORTH
		MapLocation testLocation = new MapLocation(1, 1);
		Soldier soldier = new Soldier(0, HunterKillerConstants.GAMEOBJECT_NOT_PLACED, Direction.NORTH);
		testMap.registerGameObject(soldier);
		testMap.place(testLocation, soldier);

		// Get the field-of-view for the north-facing soldier
		HashSet<MapLocation> northFOV = testMap.getFieldOfView(soldier);
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

		// Face the soldier east
		soldier.setOrientation(Direction.EAST);
		// Get the field-of-view for the east-facing soldier
		HashSet<MapLocation> eastFOV = testMap.getFieldOfView(soldier);
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

		// Face the soldier south
		soldier.setOrientation(Direction.SOUTH);
		// Get the field-of-view for the south-facing soldier
		HashSet<MapLocation> southFOV = testMap.getFieldOfView(soldier);
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

		// Face the soldier west
		soldier.setOrientation(Direction.WEST);
		// Get the field-of-view for the west-facing soldier
		HashSet<MapLocation> westFOV = testMap.getFieldOfView(soldier);
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

		// We are done with our trusty soldier, so remove it
		testMap.unregisterGameObject(soldier);

		// Create a new infected at [1,2]
		Infected infected = new Infected(0, HunterKillerConstants.GAMEOBJECT_NOT_PLACED, Direction.NORTH);
		testMap.registerGameObject(infected);
		testMap.place(new MapLocation(1, 2), infected);
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
	 * for a Floor tile. This setup is tested with the Unit in CLOCKWISE and SOUTH orientation and once for
	 * an Infected.
	 */
	@Test
	public void testCornerVision() {
		// Fill map with Floors, except for position #4
		GameObject[][] mapContent = new GameObject[testMap.getMapWidth() * testMap.getMapHeight()][HunterKillerConstants.MAP_INTERNAL_LAYERS];
		for (int i = 0; i < mapContent.length; i++) {
			MapLocation location = testMap.toLocation(i);
			if (i == 4) {
				Wall wall = new Wall(location);
				testMap.registerGameObject(wall);
				mapContent[i][HunterKillerConstants.MAP_INTERNAL_FEATURE_INDEX] = wall;
			} else {
				Floor floor = new Floor(location);
				testMap.registerGameObject(floor);
				mapContent[i][HunterKillerConstants.MAP_INTERNAL_FEATURE_INDEX] = floor;
			}
		}
		testMap.setMapContent(mapContent);

		// In the next sections, when visualising the FOV; '.' refers to visible tiles, and '#' refers to obscured
		// tiles.

		// Create a new soldier at [0,0] facing EAST
		MapLocation testLocation = new MapLocation(0, 0);
		Soldier soldier = new Soldier(0, HunterKillerConstants.GAMEOBJECT_NOT_PLACED, Direction.EAST);
		testMap.registerGameObject(soldier);
		testMap.place(testLocation, soldier);

		// Get the field-of-view for the east-facing soldier
		HashSet<MapLocation> eastFOV = testMap.getFieldOfView(soldier);
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

		// Face the soldier south
		soldier.setOrientation(Direction.SOUTH);
		// Get the field-of-view for the south-facing soldier
		HashSet<MapLocation> southFOV = testMap.getFieldOfView(soldier);
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

		// We are done with our trusty soldier, so remove it
		testMap.unregisterGameObject(soldier);

		// Create a new infected at [0,0]
		Infected infected = new Infected(0, HunterKillerConstants.GAMEOBJECT_NOT_PLACED, Direction.NORTH);
		testMap.registerGameObject(infected);
		testMap.place(testLocation, infected);
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
	 * for a Floor tile. This setup is tested with the Unit in NORTH and COUNTER_CLOCKWISE orientation and once for
	 * an Infected.
	 */
	@Test
	public void testRoomVision() {
		// Fill map with Floors, except for positions #5, 6, 7, 9, 13
		GameObject[][] mapContent = new GameObject[testMap.getMapWidth() * testMap.getMapHeight()][HunterKillerConstants.MAP_INTERNAL_LAYERS];
		for (int i = 0; i < mapContent.length; i++) {
			MapLocation location = testMap.toLocation(i);
			if (i == 5 || i == 6 || i == 7 || i == 9 || i == 13) {
				Wall wall = new Wall(location);
				testMap.registerGameObject(wall);
				mapContent[i][HunterKillerConstants.MAP_INTERNAL_FEATURE_INDEX] = wall;
			} else {
				Floor floor = new Floor(location);
				testMap.registerGameObject(floor);
				mapContent[i][HunterKillerConstants.MAP_INTERNAL_FEATURE_INDEX] = floor;
			}
		}
		testMap.setMapContent(mapContent);

		// In the next sections, when visualising the FOV; '.' refers to visible tiles, and '#' refers to obscured
		// tiles.

		// Create a new soldier at [3,3] facing WEST
		MapLocation testLocation = new MapLocation(3, 3);
		Soldier soldier = new Soldier(0, HunterKillerConstants.GAMEOBJECT_NOT_PLACED, Direction.WEST);
		testMap.registerGameObject(soldier);
		testMap.place(testLocation, soldier);

		// Get the field-of-view for the west-facing soldier
		HashSet<MapLocation> westFOV = testMap.getFieldOfView(soldier);
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

		// Face the soldier north
		soldier.setOrientation(Direction.NORTH);
		// Get the field-of-view for the north-facing soldier
		HashSet<MapLocation> northFOV = testMap.getFieldOfView(soldier);
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

		// We are done with our trusty soldier, so remove it
		testMap.unregisterGameObject(soldier);

		// Create a new infected at [3,3]
		Infected infected = new Infected(0, HunterKillerConstants.GAMEOBJECT_NOT_PLACED, Direction.NORTH);
		testMap.registerGameObject(infected);
		testMap.place(testLocation, infected);
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

	/**
	 * Tests a line-of-sight setup where a Unit is inside a room facing a door:
	 * 
	 * <pre>
	 *    - - - -
	 *    - [ D [
	 *    - [ - -
	 *    - [ U -
	 * </pre>
	 * 
	 * Note that in this diagram, 'U' stands for the Unit, '[' stands for a Wall tile, 'D' stands for a Door tile, and
	 * '-' stands for a Floor tile. This setup is tested with the Unit facing NORTH while the Door starts closed, opens
	 * and is then closed again.
	 */
	@Test
	public void testDoorVision() {
		// Fill map with Floors, except for positions #5, 7, 9, 13 (which are Walls), and 6 (which is a Door).
		GameObject[][] mapContent = new GameObject[testMap.getMapWidth() * testMap.getMapHeight()][HunterKillerConstants.MAP_INTERNAL_LAYERS];
		for (int i = 0; i < mapContent.length; i++) {
			MapLocation location = testMap.toLocation(i);
			if (i == 5 || i == 7 || i == 9 || i == 13) {
				Wall wall = new Wall(location);
				testMap.registerGameObject(wall);
				mapContent[i][HunterKillerConstants.MAP_INTERNAL_FEATURE_INDEX] = wall;
			} else if (i == 6) {
				Door door = new Door(location);
				testMap.registerGameObject(door);
				mapContent[i][HunterKillerConstants.MAP_INTERNAL_FEATURE_INDEX] = door;
			} else {
				Floor floor = new Floor(location);
				testMap.registerGameObject(floor);
				mapContent[i][HunterKillerConstants.MAP_INTERNAL_FEATURE_INDEX] = floor;
			}
		}
		testMap.setMapContent(mapContent);

		// In the next sections, when visualising the FOV; '.' refers to visible tiles, and '#' refers to obscured
		// tiles.

		// Create a new soldier at [2,3] facing NORTH
		MapLocation testLocation = new MapLocation(2, 3);
		Soldier soldier = new Soldier(0, HunterKillerConstants.GAMEOBJECT_NOT_PLACED, Direction.NORTH);
		testMap.registerGameObject(soldier);
		testMap.place(testLocation, soldier);

		// Get the field-of-view for the north-facing soldier
		HashSet<MapLocation> northFOV = testMap.getFieldOfView(soldier);
		// Check that the FOV looks like:
		// # # # #
		// # . . .
		// # . . .
		// # # U #
		assertTrue(northFOV.size() == 7);
		assertTrue(northFOV.contains(new MapLocation(1, 1)));
		assertTrue(northFOV.contains(new MapLocation(2, 1)));
		assertTrue(northFOV.contains(new MapLocation(3, 1)));
		assertTrue(northFOV.contains(new MapLocation(1, 2)));
		assertTrue(northFOV.contains(new MapLocation(2, 2)));
		assertTrue(northFOV.contains(new MapLocation(3, 2)));
		assertTrue(northFOV.contains(new MapLocation(2, 3)));

		// Open the Door
		Door door = (Door) testMap.getFeatureAtLocation(new MapLocation(2, 1));
		door.open(testMap);

		// Update the soldier's field-of-view
		northFOV = testMap.getFieldOfView(soldier);
		// Check that the FOV looks like:
		// # # . #
		// # . . .
		// # . . .
		// # # U #
		assertTrue(northFOV.size() == 8);
		assertTrue(northFOV.contains(new MapLocation(2, 0)));
		assertTrue(northFOV.contains(new MapLocation(1, 1)));
		assertTrue(northFOV.contains(new MapLocation(2, 1)));
		assertTrue(northFOV.contains(new MapLocation(3, 1)));
		assertTrue(northFOV.contains(new MapLocation(1, 2)));
		assertTrue(northFOV.contains(new MapLocation(2, 2)));
		assertTrue(northFOV.contains(new MapLocation(3, 2)));
		assertTrue(northFOV.contains(new MapLocation(2, 3)));

		// Close the Door
		do {
			door.reduceTimer(testMap);
		} while (door.isOpen());

		// Update the soldier's field-of-view
		northFOV = testMap.getFieldOfView(soldier);
		// Check that the FOV looks like:
		// # # # #
		// # . . .
		// # . . .
		// # # U #
		assertTrue(northFOV.size() == 7);
		assertTrue(northFOV.contains(new MapLocation(1, 1)));
		assertTrue(northFOV.contains(new MapLocation(2, 1)));
		assertTrue(northFOV.contains(new MapLocation(3, 1)));
		assertTrue(northFOV.contains(new MapLocation(1, 2)));
		assertTrue(northFOV.contains(new MapLocation(2, 2)));
		assertTrue(northFOV.contains(new MapLocation(3, 2)));
		assertTrue(northFOV.contains(new MapLocation(2, 3)));
	}

	// endregion

}
