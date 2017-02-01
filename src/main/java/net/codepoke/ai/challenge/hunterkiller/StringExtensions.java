package net.codepoke.ai.challenge.hunterkiller;

import com.badlogic.gdx.utils.Array;

public class StringExtensions {

	private final static Array<Character> PATTERNS = Array.with('d', 's');

	public static String format(String format, final Object... args) {

		format = format.replace("%n", FourPatch.NEWLINE_SEPARATOR);

		final StringBuffer msg = new StringBuffer();
		int pos = 0, lastMatch = 0, idx = 0;

		for (; pos < format.length() - 1 && idx < args.length; pos++) {

			if (format.charAt(pos) != '%')
				continue;

			if (!PATTERNS.contains(format.charAt(pos + 1), false))
				continue;

			// Append the substring between matchers
			if (pos - lastMatch > 0)
				msg.append(format.substring(lastMatch, pos));

			// Append the target
			msg.append(args[idx++]);
			lastMatch = pos + 2; // Skip past the match
		}

		if (format.length() - lastMatch > 0)
			msg.append(format.substring(lastMatch, format.length()));

		return msg.toString();
	}
}
