package hunterkiller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import net.codepoke.ai.challenge.hunterkiller.FourPatch;
import net.codepoke.ai.challenge.hunterkiller.HunterKillerState;
import net.codepoke.ai.challenge.hunterkiller.HunterKillerStateFactory;
import net.codepoke.ai.challenge.hunterkiller.Map;
import net.codepoke.ai.challenge.hunterkiller.MapLocation;
import net.codepoke.ai.challenge.hunterkiller.Player;
import net.codepoke.ai.challenge.hunterkiller.enums.Direction;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.GameObject;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.mapfeature.Base;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.mapfeature.Door;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.mapfeature.Floor;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.mapfeature.Space;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.mapfeature.Wall;

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
	public void setUpBeforeMethod() throws Exception {
		testMap = new Map(testWidth, testHeight);
	}

	@After
	public void tearDownAfterMethod() throws Exception {
		testMap = null;
	}

	// endregion

	// region Test methods

	@Test
	public void testConversion() {
		int mapWidth = testMap.getMapWidth();

		// Test one
		MapLocation base = new MapLocation(0, 0);
		MapLocation transBase = testMap.toLocation(0);
		assertEquals(0, Map.toPosition(base, mapWidth));
		assertEquals(0, Map.toPosition(0, 0, mapWidth));
		assertEquals(0, transBase.getX());
		assertEquals(0, transBase.getY());

		// Test two
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

		// Test one
		MapLocation location1 = new MapLocation(0, 0);
		MapLocation location2 = new MapLocation(2, 1);
		assertEquals(Math.sqrt(5.0), MapLocation.getEuclideanDist(location1, location2), allowedErrorMargin);
		assertEquals(	Math.sqrt(5.0),
						MapLocation.getEuclideanDist(location1.getX(), location1.getY(), location2.getX(), location2.getY()),
						allowedErrorMargin);
		assertEquals(3, MapLocation.getManhattanDist(location1, location2));
		assertEquals(3, MapLocation.getManhattanDist(location1.getX(), location1.getY(), location2.getX(), location2.getY()));

		// Test two
		MapLocation location3 = new MapLocation(2, 1);
		MapLocation location4 = new MapLocation(0, 0);
		assertEquals(Math.sqrt(5.0), MapLocation.getEuclideanDist(location3, location4), allowedErrorMargin);
		assertEquals(	Math.sqrt(5.0),
						MapLocation.getEuclideanDist(location3.getX(), location3.getY(), location4.getX(), location4.getY()),
						allowedErrorMargin);
		assertEquals(3, MapLocation.getManhattanDist(location3, location4));
		assertEquals(3, MapLocation.getManhattanDist(location3.getX(), location3.getY(), location4.getX(), location4.getY()));

		// Test three
		MapLocation location5 = new MapLocation(3, 3);
		MapLocation location6 = new MapLocation(3, 3);
		assertEquals(0.0, MapLocation.getEuclideanDist(location5, location6), allowedErrorMargin);
		assertEquals(	0.0,
						MapLocation.getEuclideanDist(location5.getX(), location5.getY(), location6.getX(), location6.getY()),
						allowedErrorMargin);
		assertEquals(0, MapLocation.getManhattanDist(location5, location6));
		assertEquals(0, MapLocation.getManhattanDist(location5.getX(), location5.getY(), location6.getX(), location6.getY()));
	}

	@Test
	public void testMapFeatureCreation() {
		String mapPatch = String.format("._█%nDBO");
		// Create a FourPatch to test
		FourPatch testPatch = new FourPatch(new HunterKillerStateFactory.HunterKillerMapCreation(), mapPatch, 3, 2);
		// Setup the players
		Player[] players = new Player[] { new Player(0, "A", 0), new Player(1, "B", 8) };
		// Set the spawn direction
		Direction spawnDirection = Direction.NORTH;
		// Test the creation from a pre made map
		Map createdMap = HunterKillerStateFactory.constructFromFourPatch(testPatch, players, spawnDirection);

		// Go through the MapFeatures
		GameObject[][] content = createdMap.getMapContent();
		int index = Map.INTERNAL_MAP_FEATURE_INDEX;

		// Should be 24 positions on the map
		assertEquals(24, content.length);

		// Go through all positions and check if the correct object was created
		assertTrue(content[0][index] instanceof Space);
		assertTrue(content[1][index] instanceof Floor); // This is also the spawn point for top-left base
		assertTrue(content[2][index] instanceof Wall);
		assertTrue(content[3][index] instanceof Wall);
		assertTrue(content[4][index] instanceof Floor);
		assertTrue(content[5][index] instanceof Space);
		assertTrue(content[6][index] instanceof Door); // This door should be closed
		assertTrue(content[7][index] instanceof Base); // Top-left base
		assertTrue(content[8][index] instanceof Door); // This door should be open
		assertTrue(content[9][index] instanceof Door); // This door should be open
		assertTrue(content[10][index] instanceof Floor);
		assertTrue(content[11][index] instanceof Door); // This door should be closed
		assertTrue(content[12][index] instanceof Door); // This door should be closed
		assertTrue(content[13][index] instanceof Floor);
		assertTrue(content[14][index] instanceof Door); // This door should be open
		assertTrue(content[15][index] instanceof Door); // This door should be open
		assertTrue(content[16][index] instanceof Base); // Bottom-right base
		assertTrue(content[17][index] instanceof Door); // This door should be closed
		assertTrue(content[18][index] instanceof Space);
		assertTrue(content[19][index] instanceof Floor);
		assertTrue(content[20][index] instanceof Wall);
		assertTrue(content[21][index] instanceof Wall);
		assertTrue(content[22][index] instanceof Floor); // This is also the spawn point for bottom-right base
		assertTrue(content[23][index] instanceof Space);

		// Check if the doors are created correctly (open/closed)
		// Closed door positions: 6, 11, 12, 17
		assertFalse(((Door) content[6][index]).isOpen());
		assertFalse(((Door) content[11][index]).isOpen());
		assertFalse(((Door) content[12][index]).isOpen());
		assertFalse(((Door) content[17][index]).isOpen());
		// Open door positions: 8, 9, 14, 15
		assertTrue(((Door) content[8][index]).isOpen());
		assertTrue(((Door) content[9][index]).isOpen());
		assertTrue(((Door) content[14][index]).isOpen());
		assertTrue(((Door) content[15][index]).isOpen());

		// Check if the bases have their spawn locations set correctly
		int topLeftSpawnPosition = Map.toPosition(((Base) content[7][index]).getSpawnLocation(), createdMap.getMapWidth());
		assertEquals(1, topLeftSpawnPosition);
		int bottomRightSpawnPosition = Map.toPosition(((Base) content[16][index]).getSpawnLocation(), createdMap.getMapWidth());
		assertEquals(22, bottomRightSpawnPosition);
	}

	@Test
	public void testInitialState() {
		// Create an initial state
		HunterKillerState initialState = new HunterKillerStateFactory().generateInitialState(new String[] { "playerA", "playerB" }, "");

		// Check that the initialState starts in round 1
		assertEquals(1, initialState.getCurrentRound());
		// Check that the current player has the starting amount of resource
		assertEquals(Player.PLAYER_STARTING_RESOURCE, initialState.getPlayer(initialState.getCurrentPlayer())
																	.getResource());
		// Make sure the initial state is not done
		assertFalse(initialState.isDone());

		// TODO More initial state tests
	}

	// endregion
}
