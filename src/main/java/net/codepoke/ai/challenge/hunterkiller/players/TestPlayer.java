package net.codepoke.ai.challenge.hunterkiller.players;

import net.codepoke.ai.challenge.hunterkiller.HunterKillerAction;
import net.codepoke.ai.challenge.hunterkiller.HunterKillerState;
import net.codepoke.ai.challenge.hunterkiller.Player;

/**
 * A player to test with, does mostly random things.
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
public class TestPlayer
		extends Player {

	/**
	 * @param id
	 * @param base
	 */
	public TestPlayer(int id, String name) {
		super(id, name);
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.codepoke.ai.challenge.hunterkiller.Player#act(main.java.net.codepoke.ai.challenge
	 * .hunterkiller.HunterKillerState)
	 */
	@Override
	public HunterKillerAction act(HunterKillerState state) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Player copy() {
		// TODO Auto-generated method stub
		return null;
	}

}
