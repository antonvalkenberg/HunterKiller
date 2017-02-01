package hunterkiller;

import static org.junit.Assert.assertTrue;
import net.codepoke.ai.challenge.hunterkiller.Constants;
import net.codepoke.ai.challenge.hunterkiller.HunterKillerState;
import net.codepoke.ai.challenge.hunterkiller.HunterKillerStateFactory;
import net.codepoke.ai.challenge.hunterkiller.Map;
import net.codepoke.ai.challenge.hunterkiller.MapLocation;
import net.codepoke.ai.challenge.hunterkiller.MapSetup;
import net.codepoke.ai.challenge.hunterkiller.StringExtentions;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.GameObject;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.badlogic.gdx.utils.Array;

/**
 * This class tests the HunterKillerState. Current tests:
 * <ul>
 * <li>Correct removal of information in {@link HunterKillerState#prepare(int)}.</li>
 * </ul>
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
public class HunterKillerStateTest {

	// region Constants

	private static final MapSetup testMap = new MapSetup(StringExtentions.format("B__M%nS___%n___I"));

	// endregion

	// region Properties

	private HunterKillerState state;

	private String[] playerNames = new String[] { "A", "B" };

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
		// Re-create the initial state we are testing with
		// Note that we indicate here that we don't want the players to be placed in random sections.
		state = HunterKillerStateFactory.generateInitialStateFromPremade(testMap, playerNames, "nonRandomSections");
	}

	@After
	public void tearDown() throws Exception {
		state = null;
	}

	// endregion

	// region Test methods

	/**
	 * Tests the {@link HunterKillerState#prepare(int) prepare} method, that strips the State of information not
	 * available to the currently active player.
	 * This method tests the following:
	 * <ul>
	 * <li>That all Units not in the player's field-of-view are removed from the map-content.</li>
	 * <li>That all Units not in the player's field-of-view are removed from the object-array.</li>
	 * </ul>
	 */
	@Test
	public void testPrepare() {
		// Create the state we want to test
		state = HunterKillerStateFactory.generateInitialStateFromPremade(testMap, playerNames, "nonRandomSections");
		// Create a copy of the state
		HunterKillerState copiedState = state.copy();
		Map copiedMap = copiedState.getMap();

		// MapContent before prepare-method:
		// B _ _ M _ _ _ _
		// S _ _ _ _ _ _ _
		// _ _ _ I _ _ _ _
		// _ _ _ _ I _ _ _
		// _ _ _ _ _ _ _ S
		// _ _ _ _ M _ _ B

		// The test here is that the Soldier unit that is just above/below the Structure, should not be visible for the
		// opposing team (note that an Infected has a FoV-range of 4).
		MapLocation location = new MapLocation(7, 4);
		int oppoSoldierID = copiedMap.getUnitAtLocation(location)
										.getID();

		// Prepare the state for player 0 (top-left because we forced the sections to not be assigned randomly)
		copiedState.prepare(0);

		// MapContent after prepare-method:
		// B _ _ M _ _ _ _
		// S _ _ _ _ _ _ _
		// _ _ _ I _ _ _ _
		// _ _ _ _ I _ _ _
		// _ _ _ _ _ _ _ _
		// _ _ _ _ M _ _ B

		// Make sure the unit is gone from the content
		GameObject[][] post_Content = copiedMap.getMapContent();
		assertTrue(post_Content[copiedMap.toPosition(location)][Constants.MAP_INTERNAL_UNIT_INDEX] == null);

		// Make sure the unit is gone from the objects
		Array<GameObject> post_Objects = copiedMap.getObjects();
		assertTrue(post_Objects.get(oppoSoldierID) == null);
	}

	// endregion

}
