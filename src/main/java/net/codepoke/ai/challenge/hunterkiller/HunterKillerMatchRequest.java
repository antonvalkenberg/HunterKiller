package net.codepoke.ai.challenge.hunterkiller;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.codepoke.ai.challenge.hunterkiller.enums.GameMode;
import net.codepoke.ai.challenge.hunterkiller.enums.MapType;
import net.codepoke.ai.network.MatchRequest;

import com.badlogic.gdx.utils.Array;

/**
 * Create a request for a match of HunterKiller with specific parameters
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class HunterKillerMatchRequest
		extends MatchRequest {

	/**
	 * The type of map to create a request for.
	 */
	public MapType mapType;

	/**
	 * The mode of the game to create a request for.
	 */
	public GameMode gameType;

	/**
	 * The name of the map to create a request for.
	 */
	public String mapName;

	/**
	 * Any other options the HunterKiller game should have.
	 */
	public String options;

	/**
	 * Creates a new request for a HunterKiller match.
	 * 
	 * @param botUID
	 *            The unique identifier of your bot.
	 * @param training
	 *            Whether or not the results of the match should affect your bot's ELO-rating.
	 */
	public HunterKillerMatchRequest(String botUID, boolean training) {
		super(HunterKillerConstants.GAME_NAME, botUID, training);
	}

	/**
	 * Another MatchRequest is compatible with this HunterKillerMatchRequest if the other request is a
	 * {@link HunterKillerMatchRequest}, they are compatible {@link MatchRequest}s, and all of the following statements
	 * are true:
	 * <ul>
	 * <li>Both requests specify the same MapType, if any.</li>
	 * <li>Both requests specify the same GameMode, if any.</li>
	 * <li>Both requests specify the same map name, if any.</li>
	 * <li>Both requests specify the same options, if any.</li>
	 * </ul>
	 * 
	 * If the other MatchRequest is not a {@link HunterKillerMatchRequest}, compatibility is decided by
	 * {@link MatchRequest#isCompatible(MatchRequest, String, String, Array)}.
	 */
	@Override
	public boolean isCompatible(MatchRequest other, String otherBotName, String myBotName, Array<String> alreadyConnectedPlayers) {
		// Check if the requests are compatible on a MatchRequest level
		boolean superCompatible = super.isCompatible(other, otherBotName, myBotName, alreadyConnectedPlayers);

		// Check if the other request is not a HunterKillerMatchRequest
		if (!(other instanceof HunterKillerMatchRequest)) {
			return superCompatible;
		}

		// It's a HKMR, so check if our special settings are the same
		HunterKillerMatchRequest otherHKMR = (HunterKillerMatchRequest) other;

		// Reject the other request if it has a value set where we also have a value, but the values are not the same.
		if (otherHKMR.mapType != null && mapType != null && otherHKMR.mapType != mapType) {
			return false;
		}
		if (otherHKMR.gameType != null && gameType != null && otherHKMR.gameType != gameType) {
			return false;
		}
		if (otherHKMR.mapName != null && mapName != null && !otherHKMR.mapName.equals(mapName)) {
			return false;
		}
		if (otherHKMR.options != null && options != null && !otherHKMR.options.equals(options)) {
			return false;
		}

		// None of the check for HunterKiller specific settings failed, so return the base compatibility
		return superCompatible;
	}

	@Override
	public void mergeRequest(String myBotName, MatchRequest other) {
		super.mergeRequest(myBotName, other);

		// Check if the other request is a HunterKillerMatchRequest, if not then we are done
		if (!(other instanceof HunterKillerMatchRequest))
			return;

		// It's a HKMR, so check if any options from the other request are more restrictive
		HunterKillerMatchRequest otherHKMR = (HunterKillerMatchRequest) other;

		// If we do not have a value set, where the other request does, copy it.
		if (mapType == null && otherHKMR.mapType != null) {
			mapType = otherHKMR.mapType;
		}
		if (gameType == null && otherHKMR.gameType != null) {
			gameType = otherHKMR.gameType;
		}
		if (mapName == null && otherHKMR.mapName != null) {
			mapName = otherHKMR.mapName;
		}
		if (options == null && otherHKMR.options != null) {
			options = otherHKMR.options;
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("HunterKillerMatchRequest with settings:");
		sb.append(" MapType-'");
		sb.append(mapType == null ? "n.a.'" : mapType.getFileFlag() + "'");
		sb.append(" GameMode-'");
		sb.append(gameType == null ? "n.a.'" : gameType.getFileFlag() + "'");
		sb.append(" MapName-'");
		sb.append(mapName == null ? "n.a.'" : mapName + "'");
		sb.append(" Options-'");
		sb.append(options == null ? "n.a.'." : options + "'.");
		return sb.toString();
	}
}
