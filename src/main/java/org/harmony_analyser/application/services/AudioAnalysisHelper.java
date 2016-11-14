package org.harmony_analyser.application.services;

import org.harmony_analyser.chord_analyser.*;

import java.util.Arrays;
import java.util.Scanner;

/**
 * Class to contain all relevant helper functions for audio analysis
 */
public class AudioAnalysisHelper {
	/* CHROMA HANDLING */

	// averages multiple chromas from vector of their sum into one chroma
	public static float[] averageChroma(float[] chromaSums, int countChromas) {
		float[] resultChroma = new float[12];
		for (int i = 0; i < chromaSums.length; i++) {
			resultChroma[i] = chromaSums[i] / countChromas;
		}
		return resultChroma;
	}

	// filters chroma using audibleThreshold, setting values below the threshold to 0
	public static float[] filterChroma(float[] chroma, float audibleThreshold) {
		float[] resultChroma = new float[12];
		for (int i = 0; i < chroma.length; i++) {
			if (chroma[i] < audibleThreshold) {
				resultChroma[i] = 0;
			} else {
				resultChroma[i] = chroma[i];
			}
		}
		return resultChroma;
	}

	// Creates binary representation of a chord, taking maximumNumberOfChordTones tones with the maximum activation from chroma
	public static int[] createBinaryChord(float[] chroma, int maximumNumberOfChordTones) {
		int[] result = new int[12];
		Arrays.fill(result, 0);
		// Make copy of a chroma not to erase original
		float[] chromaCopy = chroma.clone();
		float max;
		int id;
		for (int g = 0; g < maximumNumberOfChordTones; g++) {
			max = 0;
			id = 0;
			for (int i = 0; i < chroma.length; i++) {
				if (chromaCopy[i] > max) {
					id = i;
					max = chromaCopy[i];
				}
			}
			if (chromaCopy[id] > 0) {
				result[id] = 1;
			}
			chromaCopy[id] = (float) 0;
		}
		return result;
	}

	// Get number of tones from the binary representation of a chord
	public static int getNumberOfTones(int[] chord) {
		int result = 0;
		for (int tonePresence : chord) {
			if (tonePresence == 1) {
				result++;
			}
		}
		return result;
	}

	/* FILE READING */

	// gets timestamp from the first word in the line, before ':'
	public static float getTimestampFromLine(String line) {
		String stringTimestamp = line.substring(0, line.lastIndexOf(':'));
		return Float.parseFloat(stringTimestamp);
	}

	// gets timestamp from the first word in the line, before ',' (if line is formatted as "timestamp, length: label")
	public static float getTimestampFromLineContainingTimestampAndLength(String line) {
		String stringTimestamp = line.substring(0, line.lastIndexOf(','));
		return Float.parseFloat(stringTimestamp);
	}

	// gets String label from the line, after ':'
	public static String getLabelFromLine(String line) {
		return line.substring(line.lastIndexOf(':') + 2);
	}

	// gets float from the line, after ':'
	public static float getFloatFromLine(String line) { return Float.parseFloat(line.substring(line.lastIndexOf(':') + 2)); }

	// gets chroma information from the line of String
	public static float[] getChromaFromLine(String line) throws AudioAnalyser.IncorrectInputException {
		float[] result = new float[12];
		Scanner sc = new Scanner(line);
		sc.next(); // skip timestamp
		for (int i = 0; i < 12; i++) {
			if (sc.hasNextFloat()) {
				result[i] = sc.nextFloat();
			} else {
				throw new AudioAnalyser.IncorrectInputException("Chroma information is invalid.");
			}
		}
		return result;
	}

	// shifts chroma given number of semitones up
	public static float[] shiftChroma(float[] chroma, int step) {
		float[] result = new float[12];
		if (step < 0) {
			step = 12 - step;
		}
		for (int i = 0; i < 12; i++) {
			result[i] = chroma[(i + step) % 12];
		}
		return result;
	}

	/* LOGGING */

	// logs chroma float array to System.out with a given title
	public static void logChromaFloatArray(float[] chroma, String title) {
		System.out.println(title);
		for (float f : chroma) {
			System.out.print(f + " ");
		}
		System.out.println();
	}

	// logs Harmony to System.out with a given title
	public static void logHarmony(Harmony harmony, String title) {
		System.out.println(title);
		for (Tone tone : harmony.tones) {
			System.out.print(tone.getNameMapped() + " ");
		}
		System.out.println();
	}
}
