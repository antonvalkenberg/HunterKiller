/**
 * 
 */
package hunterkiller.orders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;

import net.codepoke.ai.GameRules.Result;
import net.codepoke.ai.challenge.hunterkiller.HunterKillerAction;
import net.codepoke.ai.challenge.hunterkiller.HunterKillerRules;
import net.codepoke.ai.challenge.hunterkiller.HunterKillerState;
import net.codepoke.ai.challenge.hunterkiller.HunterKillerStateFactory;
import net.codepoke.ai.challenge.hunterkiller.Map;
import net.codepoke.ai.challenge.hunterkiller.MapLocation;
import net.codepoke.ai.challenge.hunterkiller.MapSetup;
import net.codepoke.ai.challenge.hunterkiller.Player;
import net.codepoke.ai.challenge.hunterkiller.enums.Direction;
import net.codepoke.ai.challenge.hunterkiller.enums.UnitOrderType;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.mapfeature.Door;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Soldier;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Unit;
import net.codepoke.ai.challenge.hunterkiller.orders.UnitOrder;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests orders for Units. Current tests:
 * <ul>
 * 
 * </ul>
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
public class UnitOrderTest {

	// region Constants

	private static final MapSetup testMap = new MapSetup(String.format("B_S%n___"));

	private static final MapSetup testMapDoor = new MapSetup(String.format("B___%n_S__%n█D██%n█__█"));

	private static final MapSetup failMapMoveBlocked = new MapSetup(String.format("B_S%n__S"));

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
		state = HunterKillerStateFactory.generateInitialStateFromPremade(testMap, playerNames, "nonRandomSections");
	}

	@After
	public void tearDown() throws Exception {
	}

	// endregion

	// region Test methods

	// region Rotation

	/**
	 * Tests a UnitOrder of the rotation category. This method tests the following things:
	 * <ul>
	 * <li>If the UnitOrder does not generate any failures.</li>
	 * <li>If the Unit's orientation changes.</li>
	 * <li>If the Unit's Field-of-View changes.</li>
	 * <li>If the Unit's Player's combined Field-of-View contains all locations that the unit has.</li>
	 * </ul>
	 */
	@Test
	public void testRotation() {
		Player activePlayer = state.getPlayer(state.getActivePlayerID());
		// Select the unit
		Unit unit = activePlayer.getSquad()
								.get(0);

		// Situation before rotation:
		// B _ S _ _ _
		// _ _ _ _ _ _
		// _ _ _ _ _ _
		// _ _ _ S _ B
		Direction pre_UnitOrientation = unit.getOrientation();
		HashSet<MapLocation> pre_UnitFoV = unit.getFieldOfView();
		// Make sure we are assuming the right things about our unit
		assertTrue(pre_UnitOrientation == Direction.NORTH);
		assertEquals(1, pre_UnitFoV.size());
		assertTrue(pre_UnitFoV.contains(new MapLocation(2, 0)));

		// Create an order to rotate the unit
		HunterKillerAction rotateAction = new HunterKillerAction(state);
		UnitOrder order = new UnitOrder(unit, UnitOrderType.ROTATE_EAST, 0);
		rotateAction.addOrder(order);

		// Make the game logic execute the action
		Result result = gameRules.handle(state, rotateAction);

		// Make sure the order did not fail
		assertEquals(0, result.getExplanation()
								.length());

		// Refresh unit reference
		unit = state.getPlayer(activePlayer.getID())
					.getSquad()
					.get(0);
		Direction post_UnitOrientation = unit.getOrientation();
		HashSet<MapLocation> post_UnitFoV = unit.getFieldOfView();

		// Check that the unit has the correct orientation
		assertTrue(post_UnitOrientation == Direction.EAST);
		// Check that the unit's field-of-view has changed
		assertEquals(6, post_UnitFoV.size());
		assertTrue(post_UnitFoV.contains(new MapLocation(2, 0)));
		assertTrue(post_UnitFoV.contains(new MapLocation(3, 0)));
		assertTrue(post_UnitFoV.contains(new MapLocation(4, 0)));
		assertTrue(post_UnitFoV.contains(new MapLocation(5, 0)));
		assertTrue(post_UnitFoV.contains(new MapLocation(3, 1)));
		assertTrue(post_UnitFoV.contains(new MapLocation(4, 1)));

		// Check that the player's combined field-of-view now also contains these locations
		HashSet<MapLocation> post_PlayerFoV = state.getPlayer(activePlayer.getID())
													.getCombinedFieldOfView();
		for (MapLocation location : post_UnitFoV) {
			assertTrue(post_PlayerFoV.contains(location));
		}
	}

	// endregion

	// region Movement

	/**
	 * Tests a UnitOrder of the movement category. This method tests the following things:
	 * <ul>
	 * <li>If the UnitOrder does not generate any failures.</li>
	 * <li>If the Unit reached the location.</li>
	 * <li>If there is no Unit left on the origin-location.</li>
	 * <li>If the Unit's Field-of-View changes.</li>
	 * <li>If the Unit's Player's combined Field-of-View contains all locations that the unit has.</li>
	 * </ul>
	 */
	@Test
	public void testMovement() {
		Player activePlayer = state.getPlayer(state.getActivePlayerID());
		// Select the unit
		Unit unit = activePlayer.getSquad()
								.get(0);

		// Situation before movement:
		// B _ S _ _ _
		// _ _ _ _ _ _
		// _ _ _ _ _ _
		// _ _ _ S _ B
		MapLocation pre_UnitLocation = unit.getLocation();

		// Create an order to move the unit
		HunterKillerAction moveAction = new HunterKillerAction(state);
		UnitOrder order = new UnitOrder(unit, UnitOrderType.MOVE_SOUTH, 0, new MapLocation(2, 1));
		moveAction.addOrder(order);

		// Make the game logic execute the action
		Result result = gameRules.handle(state, moveAction);

		// Make sure the order did not fail
		assertEquals(0, result.getExplanation()
								.length());

		// Situation after movement:
		// B _ _ _ _ _
		// _ _ S _ _ _
		// _ _ _ _ _ _
		// _ _ _ S _ B
		unit = state.getPlayer(activePlayer.getID())
					.getSquad()
					.get(0);
		MapLocation post_UnitLocation = unit.getLocation();
		HashSet<MapLocation> post_UnitFoV = unit.getFieldOfView();

		// Check that the unit is at the target location
		assertTrue(post_UnitLocation.equals(new MapLocation(2, 1)));
		// Check that there is no unit at the old location
		assertTrue(state.getMap()
						.getUnitAtLocation(pre_UnitLocation) == null);

		// Check that the unit's Field-of-View has changed
		assertTrue(post_UnitFoV.size() == 4);
		assertTrue(post_UnitFoV.contains(new MapLocation(2, 1)));
		assertTrue(post_UnitFoV.contains(new MapLocation(1, 0)));
		assertTrue(post_UnitFoV.contains(new MapLocation(2, 0)));
		assertTrue(post_UnitFoV.contains(new MapLocation(3, 0)));

		// Check that the player's combined field-of-view now also contains these locations
		HashSet<MapLocation> post_PlayerFoV = state.getPlayer(activePlayer.getID())
													.getCombinedFieldOfView();
		for (MapLocation location : post_UnitFoV) {
			assertTrue(post_PlayerFoV.contains(location));
		}
	}

	/**
	 * Tests a UnitOrder of the movement category, in which a unit moves into a door, which should open the door.
	 * This method tests the following:
	 * <ul>
	 * <li>If the UnitOrder does not generate any failures.</li>
	 * <li>The Door opens.</li>
	 * <li>The Unit's Field-of-View changes.</li>
	 * <li>The Unit's Player's combined Field-of-View changes.</li>
	 * </ul>
	 */
	@Test
	public void testMovementThroughDoor() {
		// Re-create the map using the door setup
		state = HunterKillerStateFactory.generateInitialStateFromPremade(testMapDoor, playerNames, "nonRandomSections");
		Player activePlayer = state.getPlayer(state.getActivePlayerID());
		// Units created in the initial state are facing NORTH, but we want to test here with it facing SOUTH, so change
		Unit unit = ((Unit) state.getMap()
									.getMapContent()[9][Map.INTERNAL_MAP_UNIT_INDEX]);
		unit.setOrientation(Direction.SOUTH);
		HashSet<MapLocation> pre_UnitFoV = unit.getFieldOfView();
		Door door = (Door) state.getMap()
								.getMapContent()[17][Map.INTERNAL_MAP_FEATURE_INDEX];

		// Situation before movement:
		// B _ _ _ _ _ _ _
		// _ S _ _ _ _ _ _
		// █ D █ █ █ █ _ █
		// █ _ _ █ █ _ _ █
		// █ _ _ █ █ _ _ █
		// █ _ █ █ █ █ D █
		// _ _ _ _ _ _ S _
		// _ _ _ _ _ _ _ B
		assertTrue(!door.isOpen());
		assertEquals(4, pre_UnitFoV.size());

		// Create an order to move the unit
		HunterKillerAction moveAction = new HunterKillerAction(state);
		UnitOrder order = new UnitOrder(unit, UnitOrderType.MOVE_SOUTH, 0, new MapLocation(1, 2));
		moveAction.addOrder(order);

		// Make the game logic execute the action
		Result result = gameRules.handle(state, moveAction);

		// Make sure the order did not fail
		assertEquals(0, result.getExplanation()
								.length());

		// Situation after movement:
		// B _ _ _ _ _ _ _
		// _ _ _ _ _ _ _ _
		// █ S █ █ █ █ _ █
		// █ _ _ █ █ _ _ █
		// █ _ _ █ █ _ _ █
		// █ _ █ █ █ █ D █
		// _ _ _ _ _ _ S _
		// _ _ _ _ _ _ _ B

		// Check if the door opened
		door = (Door) state.getMap()
							.getMapContent()[17][Map.INTERNAL_MAP_FEATURE_INDEX];
		assertTrue(door.isOpen());
		assertEquals(Door.DOOR_OPEN_ROUNDS, door.getOpenTimer());

		unit = state.getPlayer(activePlayer.getID())
					.getSquad()
					.get(0);
		HashSet<MapLocation> post_UnitFoV = unit.getFieldOfView();
		// Check that the unit's Field-of-View has changed
		assertTrue(post_UnitFoV.size() == 8);
		assertTrue(post_UnitFoV.contains(new MapLocation(1, 2)));
		assertTrue(post_UnitFoV.contains(new MapLocation(0, 3)));
		assertTrue(post_UnitFoV.contains(new MapLocation(1, 3)));
		assertTrue(post_UnitFoV.contains(new MapLocation(2, 3)));
		assertTrue(post_UnitFoV.contains(new MapLocation(0, 4)));
		assertTrue(post_UnitFoV.contains(new MapLocation(1, 4)));
		assertTrue(post_UnitFoV.contains(new MapLocation(2, 4)));
		assertTrue(post_UnitFoV.contains(new MapLocation(1, 5)));

		// Check that the player's combined Field-of-View contains the new locations
		HashSet<MapLocation> post_PlayerFoV = state.getPlayer(activePlayer.getID())
													.getCombinedFieldOfView();
		for (MapLocation location : post_UnitFoV) {
			assertTrue(post_PlayerFoV.contains(location));
		}
	}

	/**
	 * Tests a UnitOrder of the movement category, that fails because the target location blocked by another Unit.
	 * This method tests the following things:
	 * <ul>
	 * <li>The UnitOrder generated a failure.</li>
	 * <li>The Unit has not moved.</li>
	 * </ul>
	 */
	@Test
	public void testFailMovementBlocked() {
		// Re-create the map using the door setup
		state = HunterKillerStateFactory.generateInitialStateFromPremade(failMapMoveBlocked, playerNames, "nonRandomSections");
		Player activePlayer = state.getPlayer(state.getActivePlayerID());
		Unit unit = activePlayer.getSquad()
								.get(0);

		// Situation before movement:
		// B _ S _ _ _
		// _ _ S _ _ _
		// _ _ _ S _ _
		// _ _ _ S _ B
		MapLocation pre_UnitLocation = unit.getLocation();

		// Create an order to move the unit
		HunterKillerAction moveAction = new HunterKillerAction(state);
		UnitOrder order = new UnitOrder(unit, UnitOrderType.MOVE_SOUTH, 0, new MapLocation(2, 1));
		moveAction.addOrder(order);

		// Make the game logic execute the action
		Result result = gameRules.handle(state, moveAction);
		// Check that there is a fail message
		assertTrue(result.getExplanation()
							.length() > 0);

		// Check that the unit is still on the same location
		assertTrue(state.getMap()
						.getObjectLocation(unit.getID())
						.equals(pre_UnitLocation));
	}

	// endregion

	// region Attacks

	/**
	 * Tests a UnitOrder of the attack category. This methods tests the following things:
	 * <ul>
	 * <li>If the UnitOrder does not generate any failures.</li>
	 * <li>If the Unit on the target location lost health.</li>
	 * <li>If the right amount of health was lost.</li>
	 * </ul>
	 */
	@Test
	public void testAttack() {
		MapLocation targetLocation = new MapLocation(3, 3);
		Player activePlayer = state.getPlayer(state.getActivePlayerID());
		Unit unit = activePlayer.getSquad()
								.get(0);
		int pre_TargetUnitHealth = state.getMap()
										.getUnitAtLocation(targetLocation)
										.getHpCurrent();

		// Situation before attack:
		// B _ S _ _ _
		// _ _ _ _ _ _
		// _ _ _ _ _ _
		// _ _ _ S _ B

		// Create an order to attack location (3,3)
		HunterKillerAction attackAction = new HunterKillerAction(state);
		UnitOrder order = new UnitOrder(unit, UnitOrderType.ATTACK, 0, targetLocation);
		attackAction.addOrder(order);

		// Make the game logic execute the action
		Result result = gameRules.handle(state, attackAction);

		// Make sure the order did not fail
		assertEquals(0, result.getExplanation()
								.length());

		int post_TargetUnitHealth = state.getMap()
											.getUnitAtLocation(targetLocation)
											.getHpCurrent();
		// Make sure health was lost, and the correct amount
		assertTrue(post_TargetUnitHealth < pre_TargetUnitHealth);
		assertEquals(Soldier.SOLDIER_ATTACK_DAMAGE, pre_TargetUnitHealth - post_TargetUnitHealth);
	}

	// endregion

	// region Special Attacks

	// endregion

	// endregion

}
