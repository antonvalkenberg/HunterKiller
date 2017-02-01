package net.codepoke.ai.challenge.hunterkiller;

import java.util.regex.Pattern;

public class StringExtentions {

	private static Pattern pattern = Pattern.compile("%([a-m]|[o-z])");

	public static String format(final String format, final Object... args) {
		String[] split = pattern.split(format);
		final StringBuffer msg = new StringBuffer();
		for (int pos = 0; pos < split.length - 1; ++pos) {
			msg.append(split[pos]);
			msg.append(args[pos].toString());
		}
		msg.append(split[split.length - 1]);
		String formatted = msg.toString();
		return formatted.replace("%n", FourPatch.NEWLINE_SEPARATOR);
	}
}
