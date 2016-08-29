package org.harmony_analyser.chromanal;

import org.harmony_analyser.chordanal.Chordanal;
import org.harmony_analyser.chordanal.Harmanal;
import org.harmony_analyser.chordanal.Harmony;
import org.harmony_analyser.chordanal.Tone;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Chromanal {
	/* Exceptions */

	static final int CHROMA_LENGTH = 12;

	/* Public / Package methods */

	public static float getChromaComplexitySimple(Chroma chroma1, Chroma chroma2) {
		float sum = 0;
		for (int i = 0; i < CHROMA_LENGTH; i++) {
			sum += Math.abs(chroma1.values[i] - chroma2.values[i]);
		}
		return sum;
	}

	public static float getChromaComplexityTonal(Chroma chroma1, Chroma chroma2) throws Chroma.WrongChromaSize {
		float complexity = (float) 0.0;
		float[] chromaVector1 = filterChroma(chroma1.values);
		int[] harmony1 = createBinaryChord(chromaVector1);
		float[] chromaVector2 = filterChroma(chroma2.values);
		int[] harmony2 = createBinaryChord(chromaVector2);

		// create chords using Chordanal
		String currentChordTones = Chordanal.getStringOfTones(harmony1);
		String previousChordTones = Chordanal.getStringOfTones(harmony2);
		Harmony harmony_1 = Chordanal.createHarmonyFromRelativeTones(previousChordTones);
		Harmony harmony_2 = Chordanal.createHarmonyFromRelativeTones(currentChordTones);
		if ((harmony_1 != null) && (harmony_2 != null)) {
			List<String> roots1 = Harmanal.getRootsFormatted(harmony_1);
			List<String> roots2 = Harmanal.getRootsFormatted(harmony_2);
			if ((roots1 != null) && (roots2 != null)) {
				Scanner sc1 = new Scanner(roots1.get(0));
				Scanner sc2 = new Scanner(roots2.get(0));

				sc1.nextLine();
				sc1.next();
				String tone1 = sc1.next();
				String tone2 = sc1.next();
				String tone3 = sc1.next();
				if (tone2.equals("steps:")) {
					tone2 = "";
				}
				if (tone3.equals("steps:")) {
					tone3 = "";
				}

				if (!tone1.equals("")) {
					Tone tone1c = Chordanal.createToneFromRelativeName(tone1);
					chromaVector1[tone1c.getNumberMapped()] = 0;
				}
				if (!tone2.equals("")) {
					Tone tone2c = Chordanal.createToneFromRelativeName(tone2);
					chromaVector1[tone2c.getNumberMapped()] = 0;
				}
				if (!tone3.equals("")) {
					Tone tone3c = Chordanal.createToneFromRelativeName(tone3);
					chromaVector1[tone3c.getNumberMapped()] = 0;
				}

				sc2.nextLine();
				sc2.next();
				String tone2_1 = sc2.next();
				String tone2_2 = sc2.next();
				String tone2_3 = sc2.next();
				if (tone2_2.equals("steps:")) {
					tone2_2 = "";
				}
				if (tone2_3.equals("steps:")) {
					tone2_3 = "";
				}
				if (!tone2_1.equals("")) {
					Tone tone1c = Chordanal.createToneFromRelativeName(tone2_1);
					chromaVector2[tone1c.getNumberMapped()] = 0;
				}
				if (!tone2_2.equals("")) {
					Tone tone2c = Chordanal.createToneFromRelativeName(tone2_2);
					chromaVector2[tone2c.getNumberMapped()] = 0;
				}
				if (!tone2_3.equals("")) {
					Tone tone3c = Chordanal.createToneFromRelativeName(tone2_3);
					chromaVector2[tone3c.getNumberMapped()] = 0;
				}
			}
		}

		return getChromaComplexitySimple(new Chroma(chromaVector1), new Chroma(chromaVector2));
	}

	/* Private methods */

	private static final double audibleThreshold = 0.07;

	// filters chroma using AUDIBLE_THRESHOLD, setting values below the threshold to 0
	private static float[] filterChroma(float[] chroma) {
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

	private static int maximumNumberOfChordTones = 4;

	// Creates binary representation of a chord, taking MAXIMUM_NUMBER_OF_CHORD_TONES tones with the maximum activation from chroma
	private static int[] createBinaryChord(float[] chroma) {
		int[] result = new int[12];
		Arrays.fill(result, 0);
		float max;
		int id;
		for (int g = 0; g < maximumNumberOfChordTones; g++) {
			max = 0;
			id = 0;
			for (int i = 0; i < chroma.length; i++) {
				if (chroma[i] > max) {
					id = i;
					max = chroma[i];
				}
			}
			if (chroma[id] > 0) {
				result[id] = 1;
			}
			chroma[id] = (float) 0;
		}
		return result;
	}
}
