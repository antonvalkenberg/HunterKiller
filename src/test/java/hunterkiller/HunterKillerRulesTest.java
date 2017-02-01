package hunterkiller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import net.codepoke.ai.GameRules.Result;
import net.codepoke.ai.challenge.hunterkiller.Constants;
import net.codepoke.ai.challenge.hunterkiller.HunterKillerAction;
import net.codepoke.ai.challenge.hunterkiller.HunterKillerRules;
import net.codepoke.ai.challenge.hunterkiller.HunterKillerState;
import net.codepoke.ai.challenge.hunterkiller.HunterKillerStateFactory;
import net.codepoke.ai.challenge.hunterkiller.MapLocation;
import net.codepoke.ai.challenge.hunterkiller.MapSetup;
import net.codepoke.ai.challenge.hunterkiller.Player;
import net.codepoke.ai.challenge.hunterkiller.StringExtentions;
import net.codepoke.ai.challenge.hunterkiller.enums.Direction;
import net.codepoke.ai.challenge.hunterkiller.enums.UnitOrderType;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Unit;
import net.codepoke.ai.challenge.hunterkiller.orders.UnitOrder;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * This class tests the rules of the HunterKiller game, that are not already tackled by tests in other classes.
 * Currently tests:
 * <ul>
 * <li>Death of a Unit and subsequent removal.</li>
 * <li>Scoring points by killing a Unit.</li>
 * </ul>
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
public class HunterKillerRulesTest {

	// region Constants

	private static final MapSetup testMapAttack = new MapSetup(StringExtentions.format("BS"));

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
		// Re-create the initial state we are testing with.
		// Note that we indicate here that we don't want the players to be placed in random sections.
		state = HunterKillerStateFactory.generateInitialStateFromPremade(testMapAttack, playerNames, "nonRandomSections");
	}

	@After
	public void tearDown() throws Exception {
		state = null;
	}

	// endregion

	// region

	/**
	 * Tests a Unit reaching 0 or less health points, and any effects that should result from this. This method tests
	 * the following things:
	 * <ul>
	 * <li>That the Unit is removed from the Map, and it's player's squad.</li>
	 * <li>That the player that ordered the attack that killed the Unit is awarded the correct score.</li>
	 * </ul>
	 */
	@Test
	public void testUnitDeath() {
		// Situation before attack:
		// B S _ _
		// _ _ S B

		// Get the player who's turn it is
		Player activePlayer = state.getPlayer(state.getActivePlayerID());
		// Save the current score they have
		int pre_ActivePlayerScore = activePlayer.getScore();

		// Get the Unit that we want to be attacking with
		Unit unit = state.getMap()
							.getUnitAtLocation(new MapLocation(1, 0));
		// Make sure it is facing the correct way to be attacking, because Units created on the Map at the start of a
		// game are facing NORTH by default.
		state.getMap()
				.getUnitAtLocation(new MapLocation(1, 0))
				.setOrientation(Direction.EAST);
		// Update the Unit's field-of-view because we just changed it
		unit.updateFieldOfView(state.getMap()
									.getFieldOfView(unit));

		// Determine the location we want to attack
		MapLocation targetLocation = new MapLocation(2, 1);
		// Set the health of the Unit at the target location so that it dies to the attack we'll be making
		Unit targetUnit = state.getMap()
								.getUnitAtLocation(targetLocation);
		targetUnit.reduceHP(targetUnit.getHpCurrent() - Constants.SOLDIER_ATTACK_DAMAGE);

		// Create an order to attack the target location
		HunterKillerAction attackAction = new HunterKillerAction(state);
		UnitOrder order = new UnitOrder(unit, UnitOrderType.ATTACK, targetLocation);
		attackAction.addOrder(order);

		// Make the game logic execute the action
		Result result = gameRules.handle(state, attackAction);

		// Make sure the order did not fail
		assertEquals(0, result.getExplanation()
								.length());

		// Make sure there is no Unit anymore on the target location
		assertTrue(state.getMap()
						.getUnitAtLocation(targetLocation) == null);

		// Update the score of the player that ordered the attack
		int post_ActivePlayerScore = state.getPlayer(activePlayer.getID())
											.getScore();
		// Make sure the player was awarded the correct score amount
		assertEquals(Constants.SOLDIER_SCORE, (post_ActivePlayerScore - pre_ActivePlayerScore));
	}

	// endregion

}
