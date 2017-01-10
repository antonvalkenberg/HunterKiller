package net.codepoke.ai.challenge.hunterkiller;

/**
 * Represents an exception that HunterKiller cannot recover from.
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
@SuppressWarnings("serial")
public class HunterKillerException
		extends RuntimeException {

	public HunterKillerException(String message) {
		super(message);
	}
}
