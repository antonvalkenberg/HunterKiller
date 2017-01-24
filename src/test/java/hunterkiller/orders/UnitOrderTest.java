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
import net.codepoke.ai.challenge.hunterkiller.Map;
import net.codepoke.ai.challenge.hunterkiller.MapLocation;
import net.codepoke.ai.challenge.hunterkiller.MapSetup;
import net.codepoke.ai.challenge.hunterkiller.Player;
import net.codepoke.ai.challenge.hunterkiller.enums.Direction;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.mapfeature.Door;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Infected;
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
 * <li>Rotation of a Unit</li>
 * <li>Move of a Unit</li>
 * <li>Move of a Unit through/into a Door</li>
 * <li>Invalid move of a Unit</li>
 * <li>Basic Soldier Attack</li>
 * <li>Special Soldier Attack</li>
 * <li>Special Medic Attack</li>
 * <li>Failure when ordering a Special Attack of an Infected</li>
 * <li>Trigger of Infected Special Attack when a Unit is killed by an Infected</li>
 * </ul>
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
public class UnitOrderTest {

	// region Constants

	private static final MapSetup testMap = new MapSetup(String.format("B_S%n___"));

	private static final MapSetup testMapDoor = new MapSetup(String.format("B___%n_S__%n█D██%n█__█"));

	private static final MapSetup testMapBlocked = new MapSetup(String.format("B_S%n__S"));

	private static final MapSetup testMapAttack = new MapSetup(String.format("BS"));

	private static final MapSetup testMapSpecialSoldier = new MapSetup(String.format("B__%n__S"));

	private static final MapSetup testMapSpecialMedic = new MapSetup(String.format("BS_M%n____"));

	private static final MapSetup testMapSpecialInfected = new MapSetup(String.format("BI"));

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
		state = null;
	}

	// endregion

	// region Test methods

	// region Rotation

	/**
	 * Tests a UnitOrder involving the rotation of a Unit. This method tests the following things:
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
		Unit unit = (Unit) state.getMap()
								.getObject(activePlayer.getUnitIDs()
														.get(0));

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
		UnitOrder order = unit.rotate(true);
		rotateAction.addOrder(order);

		// Make the game logic execute the action
		Result result = gameRules.handle(state, rotateAction);

		// Make sure the order did not fail
		assertEquals(0, result.getExplanation()
								.length());

		// Refresh unit reference
		unit = (Unit) state.getMap()
							.getObject(activePlayer.getUnitIDs()
													.get(0));
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
													.getCombinedFieldOfView(state.getMap());
		for (MapLocation location : post_UnitFoV) {
			assertTrue(post_PlayerFoV.contains(location));
		}
	}

	// endregion

	// region Movement

	/**
	 * Tests a basic move order. This method tests the following things:
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
		MapLocation pre_UnitLocation = new MapLocation(2, 0);
		MapLocation post_UnitLocation = new MapLocation(2, 1);
		// Select the unit
		Unit unit = state.getMap()
							.getUnitAtLocation(pre_UnitLocation);

		// Situation before movement:
		// B _ S _ _ _
		// _ _ _ _ _ _
		// _ _ _ _ _ _
		// _ _ _ S _ B

		// Create an order to move the unit
		HunterKillerAction moveAction = new HunterKillerAction(state);
		UnitOrder order = unit.move(Direction.SOUTH, state.getMap());
		moveAction.addOrder(order);

		// Make the game logic execute the action
		Result result = gameRules.handle(state, moveAction);

		// Situation after movement:
		// B _ _ _ _ _
		// _ _ S _ _ _
		// _ _ _ _ _ _
		// _ _ _ S _ B

		// Make sure the order did not fail
		assertEquals(0, result.getExplanation()
								.length());

		// Refresh the unit reference
		unit = state.getMap()
					.getUnitAtLocation(post_UnitLocation);
		HashSet<MapLocation> post_UnitFoV = unit.getFieldOfView();

		// Check that the unit is at the target location
		assertTrue(post_UnitLocation.equals(unit.getLocation()));
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
													.getCombinedFieldOfView(state.getMap());
		for (MapLocation location : post_UnitFoV) {
			assertTrue(post_PlayerFoV.contains(location));
		}
	}

	/**
	 * Tests a UnitOrder where a unit moves into a door, which should open the door.
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
		MapLocation unitLocation = new MapLocation(1, 1);
		MapLocation doorLocation = new MapLocation(1, 2);

		// Units created in the initial state are facing NORTH, but we want to test here with it facing SOUTH, so change
		Unit unit = state.getMap()
							.getUnitAtLocation(unitLocation);
		unit.setOrientation(Direction.SOUTH);
		HashSet<MapLocation> pre_UnitFoV = unit.getFieldOfView();
		// Get the door that we want to check.
		Door door = (Door) state.getMap()
								.getFeatureAtLocation(doorLocation);

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
		UnitOrder order = unit.move(Direction.SOUTH, state.getMap());
		moveAction.addOrder(order);

		// Make the game logic execute the action
		Result result = gameRules.handle(state, moveAction);

		// Situation after movement:
		// B _ _ _ _ _ _ _
		// _ _ _ _ _ _ _ _
		// █ S █ █ █ █ _ █
		// █ _ _ █ █ _ _ █
		// █ _ _ █ █ _ _ █
		// █ _ █ █ █ █ D █
		// _ _ _ _ _ _ S _
		// _ _ _ _ _ _ _ B

		// Make sure the order did not fail
		assertEquals(0, result.getExplanation()
								.length());

		// Check if the door opened
		door = (Door) state.getMap()
							.getFeatureAtLocation(doorLocation);
		assertTrue(door.isOpen());
		assertEquals(Constants.DOOR_OPEN_ROUNDS, door.getOpenTimer());

		// Refresh unit reference
		unit = state.getMap()
					.getUnitAtLocation(doorLocation);
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
													.getCombinedFieldOfView(state.getMap());
		for (MapLocation location : post_UnitFoV) {
			assertTrue(post_PlayerFoV.contains(location));
		}
	}

	/**
	 * Tests a UnitOrder that fails because the target location blocked by another Unit.
	 * This method tests the following things:
	 * <ul>
	 * <li>The UnitOrder generated a failure.</li>
	 * <li>The Unit has not moved.</li>
	 * </ul>
	 */
	@Test
	public void testFailMovementBlocked() {
		// Re-create the map using the door setup
		state = HunterKillerStateFactory.generateInitialStateFromPremade(testMapBlocked, playerNames, "nonRandomSections");

		MapLocation pre_UnitLocation = new MapLocation(2, 0);
		MapLocation targetLocation = new MapLocation(2, 1);

		Unit unit = state.getMap()
							.getUnitAtLocation(pre_UnitLocation);
		int unitID = unit.getID();
		Unit unitAtTarget = state.getMap()
									.getUnitAtLocation(targetLocation);
		int unitAtTargetID = unitAtTarget.getID();

		// Situation before movement:
		// B _ S _ _ _
		// _ _ S _ _ _
		// _ _ _ S _ _
		// _ _ _ S _ B

		// Create an order to move the unit
		HunterKillerAction moveAction = new HunterKillerAction(state);
		UnitOrder order = unit.move(Direction.SOUTH, state.getMap());
		moveAction.addOrder(order);

		// Make the game logic execute the action
		Result result = gameRules.handle(state, moveAction);

		// Check that there is a fail message
		assertTrue(result.getExplanation()
							.length() > 0);

		// Check that there is still a unit on the location
		assertTrue(state.getMap()
						.getUnitAtLocation(pre_UnitLocation) != null);

		unit = state.getMap()
					.getUnitAtLocation(pre_UnitLocation);
		unitAtTarget = state.getMap()
							.getUnitAtLocation(targetLocation);

		// Check that it is still the same unit at the location (and the target location)
		assertEquals(unitID, unit.getID());
		assertEquals(unitAtTargetID, unitAtTarget.getID());
	}

	// endregion

	// region Attacks

	/**
	 * Tests a UnitOrder that represents a basic attack of a Soldier. This method tests the following things:
	 * <ul>
	 * <li>If the UnitOrder does not generate any failures.</li>
	 * <li>If the Unit on the target location lost health.</li>
	 * <li>If the correct amount of health was lost.</li>
	 * </ul>
	 */
	@Test
	public void testAttack() {
		// Re-create the map using the smaller map for attacking
		state = HunterKillerStateFactory.generateInitialStateFromPremade(testMapAttack, playerNames, "nonRandomSections");

		MapLocation unitLocation = new MapLocation(1, 0);
		MapLocation targetLocation = new MapLocation(2, 1);

		// Units created in the initial state are facing NORTH, but we want to test here with it facing EAST, so change
		Unit unit = state.getMap()
							.getUnitAtLocation(unitLocation);
		unit.setOrientation(Direction.EAST);
		// Make sure the map updates the field-of-view, because we changed a unit's orientation.
		// Note: this is normally handled by the HunterKillerRules if executed through an order, but we skipped that.
		state.getMap()
				.updateFieldOfView();

		// Get some data about our target
		int pre_TargetUnitHealth = state.getMap()
										.getUnitAtLocation(targetLocation)
										.getHpCurrent();

		// Situation before attack:
		// B S _ _
		// _ _ S B

		// Create an order to attack the target location
		HunterKillerAction attackAction = new HunterKillerAction(state);
		UnitOrder order = unit.attack(targetLocation, false);
		attackAction.addOrder(order);

		// Make the game logic execute the action
		Result result = gameRules.handle(state, attackAction);

		// Make sure the order did not fail
		assertEquals(0, result.getExplanation()
								.length());

		// Refresh target's data
		int post_TargetUnitHealth = state.getMap()
											.getUnitAtLocation(targetLocation)
											.getHpCurrent();
		// Make sure health was lost, and the correct amount
		assertTrue(post_TargetUnitHealth < pre_TargetUnitHealth);
		assertEquals(Constants.SOLDIER_ATTACK_DAMAGE, pre_TargetUnitHealth - post_TargetUnitHealth);
	}

	// endregion

	// region Special Attacks

	/**
	 * Tests a UnitOrder that orders the special attack of a Soldier (3x3 grenade). This method tests the following
	 * things:
	 * <ul>
	 * <li>If the UnitOrder does not generate any failures.</li>
	 * <li>If all GameObjects in the area-of-effect that can be damaged lose the correct amount of health.</li>
	 * <li>If all GameObjects in the area-of-effect that cannot be damaged have their health remain the same.</li>
	 * <li>If the Soldier's cooldown is started.</li>
	 * </ul>
	 */
	@Test
	public void testSpecialAttackSoldier() {
		// Re-create the map using the map for the soldier's special attack
		state = HunterKillerStateFactory.generateInitialStateFromPremade(testMapSpecialSoldier, playerNames, "nonRandomSections");

		MapLocation unitLocation = new MapLocation(2, 1);
		MapLocation targetLocation = new MapLocation(4, 2);
		MapLocation targetUnitLocation = new MapLocation(3, 2);
		MapLocation targetBaseLocation = new MapLocation(5, 3);

		// Units created in the initial state are facing NORTH, but we want to test here with it facing EAST, so change
		Unit unit = state.getMap()
							.getUnitAtLocation(unitLocation);
		unit.setOrientation(Direction.EAST);
		// Make sure the map updates the field-of-view, because we changed a unit's orientation.
		// Note: this is normally handled by the HunterKillerRules if executed through an order, but we skipped that.
		state.getMap()
				.updateFieldOfView();

		// Get some data about our targets
		int pre_TargetUnitHealth = state.getMap()
										.getUnitAtLocation(targetUnitLocation)
										.getHpCurrent();
		int pre_TargetBaseHealth = state.getMap()
										.getFeatureAtLocation(targetBaseLocation)
										.getHpCurrent();
		int pre_TargetFloorHealth = state.getMap()
											.getFeatureAtLocation(targetLocation)
											.getHpCurrent();

		// Situation before attack:
		// B _ _ _ _ _
		// _ _ S _ _ _
		// _ _ _ S _ _
		// _ _ _ _ _ B

		// Create an order to attack the target location
		HunterKillerAction specialAttackAction = new HunterKillerAction(state);
		UnitOrder order = unit.attack(targetLocation, true);
		specialAttackAction.addOrder(order);

		// Make the game logic execute the action
		Result result = gameRules.handle(state, specialAttackAction);

		// Make sure the order did not fail
		assertEquals(0, result.getExplanation()
								.length());

		// Update the status of the objects
		unit = state.getMap()
					.getUnitAtLocation(unitLocation);
		int post_TargetUnitHealth = state.getMap()
											.getUnitAtLocation(targetUnitLocation)
											.getHpCurrent();
		int post_TargetBaseHealth = state.getMap()
											.getFeatureAtLocation(targetBaseLocation)
											.getHpCurrent();
		int post_TargetFloorHealth = state.getMap()
											.getFeatureAtLocation(targetLocation)
											.getHpCurrent();

		// Make sure health was lost, and the correct amount
		assertTrue(post_TargetUnitHealth < pre_TargetUnitHealth);
		assertEquals(Constants.SOLDIER_SPECIAL_DAMAGE, pre_TargetUnitHealth - post_TargetUnitHealth);
		assertTrue(post_TargetBaseHealth < pre_TargetBaseHealth);
		assertEquals(Constants.SOLDIER_SPECIAL_DAMAGE, pre_TargetBaseHealth - post_TargetBaseHealth);

		// Make sure health remains the same for indestructable objects
		assertEquals(pre_TargetFloorHealth, post_TargetFloorHealth);

		// Make sure the unit that attack has it's special on cooldown
		assertTrue(unit.getSpecialAttackCooldown() > 0);
	}

	/**
	 * Tests a UnitOrder that orders the special attack of a Medic (heal). This method tests the following things:
	 * <ul>
	 * <li>If the UnitOrder does not generate any failures.</li>
	 * <li>If The Unit on the targeted location gained the correct amount of health.</li>
	 * <li>If the Medic's cooldown is started.</li>
	 * </ul>
	 */
	@Test
	public void testSpecialAttackMedic() {
		// Re-create the map using the map for the medic's special attack
		state = HunterKillerStateFactory.generateInitialStateFromPremade(testMapSpecialMedic, playerNames, "nonRandomSections");

		MapLocation unitLocation = new MapLocation(3, 0);
		MapLocation targetLocation = new MapLocation(1, 0);

		// Get the Medic we want to give the order to
		Unit unit = state.getMap()
							.getUnitAtLocation(unitLocation);
		Unit affectedUnit = state.getMap()
									.getUnitAtLocation(targetLocation);

		// Set the Soldier's health to a lower amount, so we can see the effect of the heal
		affectedUnit.reduceHP(affectedUnit.getHpCurrent() - 1);
		int pre_TargetUnitHealth = affectedUnit.getHpCurrent();

		// Situation before attack:
		// B S _ M _ _ _ _
		// _ _ _ _ _ _ _ _
		// _ _ _ _ _ _ _ _
		// _ _ _ _ M _ S B

		// Create an order to attack the target location
		HunterKillerAction specialAttackAction = new HunterKillerAction(state);
		UnitOrder order = unit.attack(targetLocation, true);
		specialAttackAction.addOrder(order);

		// Make the game logic execute the action
		Result result = gameRules.handle(state, specialAttackAction);

		// Make sure the order did not fail
		assertEquals(0, result.getExplanation()
								.length());

		// Update the status of the objects
		affectedUnit = state.getMap()
							.getUnitAtLocation(targetLocation);
		int post_TargetUnitHealth = affectedUnit.getHpCurrent();

		// Make sure health was gained, and the correct amount
		assertTrue(post_TargetUnitHealth > pre_TargetUnitHealth);
		assertEquals(Constants.MEDIC_SPECIAL_HEAL, post_TargetUnitHealth - pre_TargetUnitHealth);

		// Make sure the unit that attack has it's special on cooldown
		assertTrue(unit.getSpecialAttackCooldown() > 0);
	}

	/**
	 * Tests a UnitOrder that orders the special attack of an Infected (spawn Infected). This order should fail, because
	 * an Infected's special attack cannot be ordered (or at least, it doesn't do anything when ordered), because it
	 * triggers automatically when an Infected kills a Unit. This method tests the following things:
	 * <ul>
	 * <li>That the UnitOrder is ignored by the system (creates a fail message)</li>
	 * <li>That the Unit at the targetLocation remains controlled by the same Player</li>
	 * </ul>
	 */
	@Test
	public void testFailSpecialAttackInfected() {
		// Re-create the map using the map for the infected's special attack
		state = HunterKillerStateFactory.generateInitialStateFromPremade(testMapSpecialInfected, playerNames, "nonRandomSections");
		Map map = state.getMap();

		MapLocation unitLocation = new MapLocation(1, 0);
		MapLocation targetLocation = new MapLocation(2, 1);

		// Get the Infected we want to give the order to
		Unit unit = state.getMap()
							.getUnitAtLocation(unitLocation);
		// Move the unit south, since it has an attack range of only 1
		unitLocation = new MapLocation(1, 1);
		map.move(unitLocation, unit, new StringBuilder());

		// Save the other player's ID
		int pre_TargetUnitPlayerID = state.getMap()
											.getUnitAtLocation(targetLocation)
											.getControllingPlayerID();

		// Situation before attack:
		// B _ _ _
		// _ I I B

		// Create an order to attack the target location
		HunterKillerAction specialAttackAction = new HunterKillerAction(state);
		UnitOrder order = unit.attack(targetLocation, true);
		specialAttackAction.addOrder(order);

		// Make the game logic execute the action
		Result result = gameRules.handle(state, specialAttackAction);

		// Make sure the order was failed (ignored)
		assertTrue(result.getExplanation()
							.length() > 0);

		// Make sure the targeted Infected is still owned/controlled by the same player as before
		int post_TargetUnitPlayerID = state.getMap()
											.getUnitAtLocation(targetLocation)
											.getControllingPlayerID();
		assertTrue(pre_TargetUnitPlayerID == post_TargetUnitPlayerID);
	}

	/**
	 * Tests that the triggered ability of an Infected, that goes off when it kills a Unit, is handled correctly. This
	 * method tests the following things:
	 * <ul>
	 * <li>That an Infected is spawned on the location of the killed Unit</li>
	 * <li>That the spawned Infected is assigned to the correct Player</li>
	 * <li>That the Infected's cooldown is started</li>
	 * </ul>
	 */
	@Test
	public void testInfectedTrigger() {
		// Re-create the map using the map for the infected's special attack
		state = HunterKillerStateFactory.generateInitialStateFromPremade(testMapSpecialInfected, playerNames, "nonRandomSections");
		Map map = state.getMap();

		MapLocation unitLocation = new MapLocation(1, 0);
		MapLocation targetLocation = new MapLocation(2, 1);

		// Get the acting player
		Player actingPlayer = state.getPlayer(state.getActivePlayerID());
		// Get the Infected we want to give the order to
		Unit unit = map.getUnitAtLocation(unitLocation);
		// Move the unit south, since it has an attack range of only 1
		unitLocation = new MapLocation(1, 1);
		map.move(unitLocation, unit, new StringBuilder());

		// Get the unit at the target location
		Unit tempUnit = map.getUnitAtLocation(targetLocation);
		// We need to replace this unit, because an infected's special won't trigger off of another Infected unit
		map.unregisterGameObject(tempUnit);
		// Create a new Soldier, with just enough health to die to an Infected's attack
		Soldier pre_TargetUnit = new Soldier(0, targetLocation, Constants.SOLDIER_MAX_HP, Constants.INFECTED_ATTACK_DAMAGE, Direction.WEST,
												Constants.SOLDIER_FOV_RANGE, Constants.SOLDIER_FOV_ANGLE, Constants.SOLDIER_ATTACK_RANGE,
												Constants.SOLDIER_ATTACK_DAMAGE, Constants.SOLDIER_COOLDOWN, Constants.SOLDIER_SPAWN_COST,
												Constants.SOLDIER_SCORE);
		map.registerGameObject(pre_TargetUnit);
		map.place(targetLocation, pre_TargetUnit);

		// Situation before attack:
		// B _ _ _
		// _ I S B

		// Create an order to attack the target location
		HunterKillerAction attackAction = new HunterKillerAction(state);
		UnitOrder order = unit.attack(targetLocation, false);
		attackAction.addOrder(order);

		// Make the game logic execute the action
		Result result = gameRules.handle(state, attackAction);

		// Make sure the order did not fail
		assertEquals(0, result.getExplanation()
								.length());

		// Update the status of the objects
		actingPlayer = state.getPlayer(actingPlayer.getID());
		unit = state.getMap()
					.getUnitAtLocation(unitLocation);
		Unit post_TargetUnit = state.getMap()
									.getUnitAtLocation(targetLocation);

		// Make sure the Unit is an Infected
		assertTrue(post_TargetUnit instanceof Infected);
		// Make sure the Unit at the target location belongs to the same player
		assertEquals(actingPlayer.getID(), post_TargetUnit.getControllingPlayerID());
		// Make sure the Infected's cooldown has started
		assertTrue(unit.getSpecialAttackCooldown() > 0);
	}

	// endregion

	// endregion

}
