package org.harmony_analyser.application.services;

/**
 * Class to contain all relevant helper functions for audio analysis
 */
public class AudioAnalysisHelper {
	/* FILE READING */

	// gets timestamp from the first word in the line, before ':'
	public static float getTimestampFromLine(String line) {
		String stringTimestamp = line.substring(0, line.lastIndexOf(':'));
		return Float.parseFloat(stringTimestamp);
	}

	// gets String label for the line, after ':'
	public static String getLabelFromLine(String line) {
		return line.substring(line.lastIndexOf(':') + 2);
	}
}
