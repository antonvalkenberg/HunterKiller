package net.codepoke.ai.challenge.hunterkiller;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.codepoke.ai.challenge.hunterkiller.enums.GameMode;
import net.codepoke.ai.challenge.hunterkiller.enums.MapType;
import net.codepoke.ai.network.MatchRequest;

/**
 * Create a request for a match of HunterKiller with specific parameters
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
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

}
