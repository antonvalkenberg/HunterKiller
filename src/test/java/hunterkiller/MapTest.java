package hunterkiller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import net.codepoke.ai.challenge.hunterkiller.HunterKillerConstants;
import net.codepoke.ai.challenge.hunterkiller.FourPatch;
import net.codepoke.ai.challenge.hunterkiller.HunterKillerState;
import net.codepoke.ai.challenge.hunterkiller.HunterKillerStateFactory;
import net.codepoke.ai.challenge.hunterkiller.Map;
import net.codepoke.ai.challenge.hunterkiller.MapLocation;
import net.codepoke.ai.challenge.hunterkiller.MapSetup;
import net.codepoke.ai.challenge.hunterkiller.Player;
import net.codepoke.ai.challenge.hunterkiller.StringExtensions;
import net.codepoke.ai.challenge.hunterkiller.enums.Direction;
import net.codepoke.ai.challenge.hunterkiller.enums.StructureType;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.GameObject;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.mapfeature.Door;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.mapfeature.Floor;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.mapfeature.Space;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.mapfeature.Structure;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.mapfeature.Wall;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.badlogic.gdx.utils.Array;

/**
 * Test class for Map.
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
public class MapTest
		extends HunterKillerTest {

	// region Constants

	private static final MapSetup testPathMap = new MapSetup(StringExtensions.format("B_____%n______%n______%n______%n_█D██_%n_█__█_"));

	// endregion

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
		testMap = new Map("map_test", testWidth, testHeight);
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
		String mapPatch = StringExtensions.format("._█%nDBO%nXPH");
		// Create a FourPatch to test
		FourPatch testPatch = new FourPatch(new HunterKillerStateFactory.HunterKillerMapCreation(), mapPatch, 3, 3);
		// Setup the players
		Player[] players = new Player[] { new Player(0, "A", 0), new Player(1, "B", 8) };
		// Set the spawn direction
		Direction spawnDirection = Direction.NORTH;
		// Test the creation from a pre made map
		Map createdMap = HunterKillerStateFactory.constructFromFourPatch("testPatch", testPatch, players, spawnDirection);

		// Go through the MapFeatures
		GameObject[][] content = createdMap.getMapContent();
		int index = HunterKillerConstants.MAP_INTERNAL_FEATURE_INDEX;

		// Map visualisation:
		// . _ █ █ _ .
		// D B O O _ D
		// X P H H P X
		// X P H H P X
		// D _ O O B D
		// . _ █ █ _ .

		// Should be 36 positions on the map
		assertEquals(36, content.length);

		// Go through all positions and check if the correct object was created
		assertTrue(content[0][index] instanceof Space);
		assertTrue(content[1][index] instanceof Floor); // This is also the spawn point for top-left base
		assertTrue(content[2][index] instanceof Wall);
		assertTrue(content[3][index] instanceof Wall);
		assertTrue(content[4][index] instanceof Floor);
		assertTrue(content[5][index] instanceof Space);

		assertTrue(content[6][index] instanceof Door); // This door should be closed
		assertTrue(content[7][index] instanceof Structure);
		assertTrue(content[8][index] instanceof Door); // This door should be open
		assertTrue(content[9][index] instanceof Door); // This door should be open
		assertTrue(content[10][index] instanceof Floor);
		assertTrue(content[11][index] instanceof Door); // This door should be closed

		assertTrue(content[12][index] instanceof Structure);
		assertTrue(content[13][index] instanceof Structure);
		assertTrue(content[14][index] instanceof Structure);
		assertTrue(content[15][index] instanceof Structure);
		assertTrue(content[16][index] instanceof Structure);
		assertTrue(content[17][index] instanceof Structure);

		assertTrue(content[18][index] instanceof Structure);
		assertTrue(content[19][index] instanceof Structure);
		assertTrue(content[20][index] instanceof Structure);
		assertTrue(content[21][index] instanceof Structure);
		assertTrue(content[22][index] instanceof Structure);
		assertTrue(content[23][index] instanceof Structure);

		assertTrue(content[24][index] instanceof Door); // This door should be closed
		assertTrue(content[25][index] instanceof Floor);
		assertTrue(content[26][index] instanceof Door); // This door should be open
		assertTrue(content[27][index] instanceof Door); // This door should be open
		assertTrue(content[28][index] instanceof Structure);
		assertTrue(content[29][index] instanceof Door); // This door should be closed

		assertTrue(content[30][index] instanceof Space);
		assertTrue(content[31][index] instanceof Floor);
		assertTrue(content[32][index] instanceof Wall);
		assertTrue(content[33][index] instanceof Wall);
		assertTrue(content[34][index] instanceof Floor); // This is also the spawn point for bottom-right base
		assertTrue(content[35][index] instanceof Space);

		// Check if the correct types of Structures are created
		assertTrue(((Structure) content[7][index]).getType() == StructureType.Base);

		assertTrue(((Structure) content[12][index]).getType() == StructureType.Objective);
		assertTrue(((Structure) content[13][index]).getType() == StructureType.Outpost);
		assertTrue(((Structure) content[14][index]).getType() == StructureType.Stronghold);
		assertTrue(((Structure) content[15][index]).getType() == StructureType.Stronghold);
		assertTrue(((Structure) content[16][index]).getType() == StructureType.Outpost);
		assertTrue(((Structure) content[17][index]).getType() == StructureType.Objective);

		assertTrue(((Structure) content[18][index]).getType() == StructureType.Objective);
		assertTrue(((Structure) content[19][index]).getType() == StructureType.Outpost);
		assertTrue(((Structure) content[20][index]).getType() == StructureType.Stronghold);
		assertTrue(((Structure) content[21][index]).getType() == StructureType.Stronghold);
		assertTrue(((Structure) content[22][index]).getType() == StructureType.Outpost);
		assertTrue(((Structure) content[23][index]).getType() == StructureType.Objective);

		assertTrue(((Structure) content[28][index]).getType() == StructureType.Base);

		// Check if the doors are created correctly (open/closed)
		// Closed door positions: 6, 11, 24, 29
		assertFalse(((Door) content[6][index]).isOpen());
		assertFalse(((Door) content[11][index]).isOpen());
		assertFalse(((Door) content[24][index]).isOpen());
		assertFalse(((Door) content[29][index]).isOpen());
		// Open door positions: 8, 9, 26, 27
		assertTrue(((Door) content[8][index]).isOpen());
		assertTrue(((Door) content[9][index]).isOpen());
		assertTrue(((Door) content[26][index]).isOpen());
		assertTrue(((Door) content[27][index]).isOpen());

		// Check if the bases have their spawn locations set correctly
		int topLeftSpawnPosition = Map.toPosition(((Structure) content[7][index]).getSpawnLocation(), createdMap.getMapWidth());
		assertEquals(1, topLeftSpawnPosition);
		int bottomRightSpawnPosition = Map.toPosition(((Structure) content[28][index]).getSpawnLocation(), createdMap.getMapWidth());
		assertEquals(34, bottomRightSpawnPosition);
	}

	@Test
	public void testInitialState() {
		HunterKillerStateFactory factory = new HunterKillerStateFactory();
		// Create an initial state
		HunterKillerState initialState = factory.generateInitialState(new String[] { "playerA", "playerB" }, "");

		// Check that the initialState starts in round 1
		assertEquals(1, initialState.getCurrentRound());
		// Make sure the initial state is not done
		assertFalse(initialState.isDone());
	}

	@Test
	public void testFindPathOpponentSpawn() {
		// Re-create the map using the map for testing pathfinding
		String[] playerNames = new String[] { "A", "B" };
		HunterKillerState state = HunterKillerStateFactory.generateInitialStateFromPremade(testPathMap, playerNames, "nonRandomSections");
		Map map = state.getMap();

		// State of the map visualised:
		// B _ _ _ _ _ _ _ _ _ _ _
		// _ _ _ _ _ _ _ _ _ _ _ _
		// _ _ _ _ _ _ _ _ _ _ _ _
		// _ _ _ _ _ _ _ _ _ _ _ _
		// _ █ D █ █ _ _ █ █ D █ _
		// _ █ _ _ █ _ _ █ _ _ █ _
		// _ █ _ _ █ _ _ █ _ _ █ _
		// _ █ D █ █ _ _ █ █ D █ _
		// _ _ _ _ _ _ _ _ _ _ _ _
		// _ _ _ _ _ _ _ _ _ _ _ _
		// _ _ _ _ _ _ _ _ _ _ _ _
		// _ _ _ _ _ _ _ _ _ _ _ B

		// Test 1
		// Ask the map to calculate a path from the Player's base to the opponent's spawn location
		Player player = state.getPlayer(0);
		MapLocation baseLocation = map.getObjectLocation(player.getCommandCenterID());
		Player opponent = state.getPlayer(1);
		MapLocation oppoLocation = ((Structure) map.getObject(opponent.getCommandCenterID())).getSpawnLocation();

		Array<MapLocation> path1 = map.findPath(baseLocation, oppoLocation);

		// Check that the length is correct (specific path can vary)
		assertEquals(21, path1.size);

		// Test 2
		// Ask the map to calculate this path (start = '.', target = '*')
		// B _ _ _ _ _ _ _ _ _ _ _
		// _ _ _ _ _ _ _ _ _ _ _ _
		// _ _ _ _ _ _ _ _ _ _ _ _
		// _ _ _ _ _ _ _ _ _ _ _ _
		// _ █ D █ █ _ _ █ █ D █ _
		// . █ _ _ █ _ _ █ * _ █ _
		// _ █ _ _ █ _ _ █ _ _ █ _
		// _ █ D █ █ _ _ █ █ D █ _
		// _ _ _ _ _ _ _ _ _ _ _ _
		// _ _ _ _ _ _ _ _ _ _ _ _
		// _ _ _ _ _ _ _ _ _ _ _ _
		// _ _ _ _ _ _ _ _ _ _ _ B
		Array<MapLocation> path2 = map.findPath(new MapLocation(0, 5), new MapLocation(8, 5));

		// Check that the length is correct
		assertEquals(14, path2.size);
		// Check that the path is correct
		assertEquals(new MapLocation(0, 4), path2.get(0));
		assertEquals(new MapLocation(0, 3), path2.get(1));
		assertEquals(new MapLocation(1, 3), path2.get(2));
		assertEquals(new MapLocation(2, 3), path2.get(3));
		assertEquals(new MapLocation(3, 3), path2.get(4));
		assertEquals(new MapLocation(4, 3), path2.get(5));
		assertEquals(new MapLocation(5, 3), path2.get(6));
		assertEquals(new MapLocation(6, 3), path2.get(7));
		assertEquals(new MapLocation(7, 3), path2.get(8));
		assertEquals(new MapLocation(8, 3), path2.get(9));
		assertEquals(new MapLocation(9, 3), path2.get(10));
		assertEquals(new MapLocation(9, 4), path2.get(11));
		assertEquals(new MapLocation(9, 5), path2.get(12));
		assertEquals(new MapLocation(8, 5), path2.get(13));
	}

	// endregion

}
