package net.codepoke.ai.challenge.hunterkiller.enums;

import lombok.Getter;

/**
 * Represents the various types of maps in HunterKiller.
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
public enum MapType {
	Default("default"),
	Open("open"),
	Narrow("narrow");

	@Getter
	String fileFlag;

	private MapType(String fileFlag) {
		this.fileFlag = fileFlag;
	}
}
