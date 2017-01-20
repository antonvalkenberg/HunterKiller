package hunterkiller.orders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;

import net.codepoke.ai.GameRules.Result;
import net.codepoke.ai.challenge.hunterkiller.Constants;
import net.codepoke.ai.challenge.hunterkiller.HunterKillerAction;
import net.codepoke.ai.challenge.hunterkiller.HunterKillerRules;
import net.codepoke.ai.challenge.hunterkiller.HunterKillerState;
import net.codepoke.ai.challenge.hunterkiller.HunterKillerStateFactory;
import net.codepoke.ai.challenge.hunterkiller.MapLocation;
import net.codepoke.ai.challenge.hunterkiller.MapSetup;
import net.codepoke.ai.challenge.hunterkiller.Player;
import net.codepoke.ai.challenge.hunterkiller.enums.UnitType;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.mapfeature.Structure;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Infected;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Unit;
import net.codepoke.ai.challenge.hunterkiller.orders.StructureOrder;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * This class tests orders for a Structure. Current tests:
 * <ul>
 * <li>Correct spawn (infected)</li>
 * <li>Faulty spawn due to insufficient funds</li>
 * <li>Faulty spawn due to occupied location</li>
 * </ul>
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
public class StructureOrderTest {

	// region Constants

	private static final MapSetup testMap = new MapSetup(String.format("B__%n___"));

	private static final MapSetup failMap = new MapSetup(String.format("B__%nS__"));

	// endregion

	// region Properties

	private HunterKillerState state;

	private HunterKillerRules gameRules = new HunterKillerRules();

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
	 * Test that a unit is correctly spawned at the spawn location, that the player is taxed for the cost and that the
	 * new unit affects the player's field of view.
	 */
	@Test
	public void testSpawn() {
		// Set some values of things before the order
		Player activePlayer = state.getPlayer(state.getActivePlayerID());
		int beforePlayerResource = activePlayer.getResource();
		int beforePlayerSquadSize = activePlayer.getUnitIDs().size;
		Structure base = (Structure) state.getMap()
											.getObject(activePlayer.getCommandCenterID());
		MapLocation spawnLocation = base.getSpawnLocation();

		// Create a base-order to spawn an infected for the active player
		HunterKillerAction spawnInfectedAction = new HunterKillerAction(state);
		StructureOrder order = base.spawn(UnitType.Infected);
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
		assertEquals(beforePlayerResource - Constants.INFECTED_SPAWN_COST, activePlayer.getResource());
		// Check that the player has an extra squad member
		assertEquals(beforePlayerSquadSize + 1, activePlayer.getUnitIDs().size);

		// Check that there is an infected on the map, in the correct location
		assertTrue(state.getMap()
						.getUnitAtLocation(spawnLocation) instanceof Infected);
		// Check that the infected on the spawn location belongs to the formerly active player
		assertEquals(activePlayer.getID(), state.getMap()
												.getUnitAtLocation(spawnLocation)
												.getControllingPlayerID());

		// Get the newly spawned unit
		Unit spawnedUnit = state.getMap()
								.getUnitAtLocation(spawnLocation);
		// Get the current combined FoV for the player
		HashSet<MapLocation> playerFoV = activePlayer.getCombinedFieldOfView(state.getMap());

		// Check that each location that the new Infected can see, is also in the player's current combined FoV.
		for (MapLocation location : state.getMap()
											.getFieldOfView(spawnedUnit)) {
			assertTrue(playerFoV.contains(location));
		}
	}

	/**
	 * Test that a spawn order fails if the player does not have enough resources.
	 */
	@Test
	public void testFailSpawnLowResource() {
		// Set some values of things before the order
		Player activePlayer = state.getPlayer(state.getActivePlayerID());
		Structure base = (Structure) state.getMap()
											.getObject(activePlayer.getCommandCenterID());
		MapLocation spawnLocation = base.getSpawnLocation();

		// Create a base-order to spawn an infected for the active player
		HunterKillerAction spawnInfectedAction = new HunterKillerAction(state);
		StructureOrder order = base.spawn(UnitType.Infected);
		spawnInfectedAction.addOrder(order);

		// Now set the player's resource to an amount that is not enough to spawn an infected unit
		activePlayer.setResource(0);

		// Make the game logic execute the action
		Result result = gameRules.handle(state, spawnInfectedAction);

		// Check that there is a failure message
		assertTrue(result.getExplanation()
							.length() > 0);

		// Check that no unit is at the spawn location
		assertTrue(state.getMap()
						.getUnitAtLocation(spawnLocation) == null);
	}

	/**
	 * Test that a spawn order fails if the spawn location is currently occupied by a Unit.
	 */
	@Test
	public void testFailSpawnLocationOccupied() {
		// Re-create the State with the map that is setup to fail.
		state = HunterKillerStateFactory.generateInitialStateFromPremade(failMap, playerNames, "nonRandomSections");

		// Set some values of things before the order
		Player activePlayer = state.getPlayer(state.getActivePlayerID());
		int pre_PlayerSquadSize = activePlayer.getUnitIDs().size;
		int pre_PlayerResources = activePlayer.getResource();
		Structure base = (Structure) state.getMap()
											.getObject(activePlayer.getCommandCenterID());

		// Create a base-order to spawn an infected for the active player
		HunterKillerAction spawnInfectedAction = new HunterKillerAction(state);
		StructureOrder order = base.spawn(UnitType.Infected);
		spawnInfectedAction.addOrder(order);

		// Make the game logic execute the action
		Result result = gameRules.handle(state, spawnInfectedAction);

		// Refresh player object
		activePlayer = state.getPlayer(activePlayer.getID());

		// Check that there is a failure message
		assertTrue(result.getExplanation()
							.length() > 0);
		// Check that nothing has actually been spawned
		assertEquals(pre_PlayerSquadSize, activePlayer.getUnitIDs().size);
		assertEquals(pre_PlayerResources, activePlayer.getResource());
	}

	// endregion

}
