package main.java.net.codepoke.ai.challenge.hunterkiller.enums;

import main.java.net.codepoke.ai.challenge.hunterkiller.FourPatch;

/**
 * Defines a list of pre-made Maps (limited to the top-left quadrant since this is copied).
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
public enum PremadeMaps {
	//@formatter:off
	TEST("", 0, new FourPatch("", 0, 0));
	
	//@formatter:on
	String name;
	int numPlayers;
	public FourPatch fourPatch;

	private PremadeMaps(String name, int numOfPlayers, FourPatch fourPatch) {
		this.name = name;
		this.numPlayers = numOfPlayers;
		this.fourPatch = fourPatch;
	}

}
