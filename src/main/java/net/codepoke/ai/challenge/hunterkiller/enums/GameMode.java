package net.codepoke.ai.challenge.hunterkiller.enums;

import lombok.Getter;

/**
 * Represents the different modes that the game of HunterKiller can be played in.
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
public enum GameMode {
	Capture("capture"),
	Killing("killing"),
	King_of_the_Hill("king"),
	Exploration("spacestation"),
	Deathmatch("squad");

	@Getter
	String fileFlag;

	private GameMode(String fileFlag) {
		this.fileFlag = fileFlag;
	}
}
