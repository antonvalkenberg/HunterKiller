package net.codepoke.ai.challenge.hunterkiller.orders;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.codepoke.ai.challenge.hunterkiller.HunterKillerAction;
import net.codepoke.ai.challenge.hunterkiller.HunterKillerState;

/**
 * Class representing a null move. No action is taken. Does not serve much purpose other than
 * implementing the {@link State} interface.
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NullMove
		extends HunterKillerAction {

	/**
	 * Constructor.
	 * 
	 * @param state
	 */
	public NullMove(HunterKillerState state) {
		super(state);
	}

}
