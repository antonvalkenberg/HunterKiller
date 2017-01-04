/**
 * 
 */
package hunterkiller.orders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;

import net.codepoke.ai.GameRules.Result;
import net.codepoke.ai.challenge.hunterkiller.FourPatch;
import net.codepoke.ai.challenge.hunterkiller.HunterKillerAction;
import net.codepoke.ai.challenge.hunterkiller.HunterKillerRules;
import net.codepoke.ai.challenge.hunterkiller.HunterKillerState;
import net.codepoke.ai.challenge.hunterkiller.HunterKillerStateFactory;
import net.codepoke.ai.challenge.hunterkiller.MapLocation;
import net.codepoke.ai.challenge.hunterkiller.MapSetup;
import net.codepoke.ai.challenge.hunterkiller.Player;
import net.codepoke.ai.challenge.hunterkiller.enums.BaseOrderType;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Infected;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Unit;
import net.codepoke.ai.challenge.hunterkiller.orders.BaseOrder;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * This class tests the Base
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
public class BaseOrderTest {

	// region Constants

	private static final MapSetup testMap = new MapSetup(String.format("B__%s___", FourPatch.NEWLINE_SEPARATOR));

	// endregion

	// region Properties

	private HunterKillerState state;

	private HunterKillerRules gameRules = new HunterKillerRules();

	// endregion

	// region Setup methods

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
		// Re-create the inital state we are testing with
		String[] playerNames = new String[] { "A", "B" };
		state = HunterKillerStateFactory.generateInitialStateFromPremade(testMap, playerNames, "");
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	// endregion

	// region Test methods

	@Test
	public void testSpawnInfected() {
		// Set some values of things before the order
		Player activePlayer = state.getPlayer(state.getActivePlayerID());
		int beforePlayerResource = activePlayer.getResource();
		int beforePlayerSquadSize = activePlayer.getSquad()
												.size();
		MapLocation spawnLocation = activePlayer.getBase()
												.getSpawnLocation();
		int beforeFOVLocations = activePlayer.getCombinedFieldOfView()
												.size();

		// Create a base-order to spawn an infected for the active player
		HunterKillerAction spawnInfectedAction = new HunterKillerAction(state);
		BaseOrder order = new BaseOrder(activePlayer.getBase(), BaseOrderType.SPAWN_INFECTED, 0);
		spawnInfectedAction.addOrder(order);

		// Make the game logic execute the action
		Result result = gameRules.handle(state, spawnInfectedAction);

		// Check that the action was accepted
		assertTrue(result.isAccepted());
		// Check that the action did not result in a finished state
		assertTrue(!result.isFinished());
		// Check that the failure explanations are empty
		assertEquals("", result.getExplanation());

		// Check that the player's resource was reduced
		assertEquals(beforePlayerResource - Infected.INFECTED_SPAWN_COST, activePlayer.getResource());
		// Check that the player has an extra squad member
		assertEquals(beforePlayerSquadSize + 1, activePlayer.getSquad()
															.size());

		// Check that there is an infected on the map, in the correct location
		assertTrue(state.getMap()
						.getUnitAtLocation(spawnLocation) instanceof Infected);
		// Check that the infected on the spawn location belongs to the formerly active player
		assertEquals(activePlayer.getID(), state.getMap()
												.getUnitAtLocation(spawnLocation)
												.getSquadPlayerID());

		// Get the newly spawned unit
		Unit spawnedUnit = state.getMap()
								.getUnitAtLocation(spawnLocation);
		// Get the current combined FoV for the player
		HashSet<MapLocation> playerFoV = activePlayer.getCombinedFieldOfView();

		// Check that each location that the new Infected can see, is also in the player's current combined FoV.
		for (MapLocation location : state.getMap()
											.getFieldOfView(spawnedUnit)) {
			assertTrue(playerFoV.contains(location));
		}
	}

	@Test
	public void testSpawnMedic() {
		// TODO Test spawning of Medic
	}

	@Test
	public void testSpawnSoldier() {
		// TODO Test spawning of Soldier
	}

	// endregion
}
