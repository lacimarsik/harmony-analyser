package org.harmony_analyser.chromanal;

import org.harmony_analyser.application.services.AudioAnalysisHelper;
import org.harmony_analyser.chordanal.*;

import java.util.List;
import java.util.Scanner;

@SuppressWarnings("FieldCanBeLocal")

public class Chromanal {
	/* Exceptions */

	static final int CHROMA_LENGTH = 12;
	private final static float audibleThreshold = (float) 0.07;
	private final static int maximumNumberOfChordTones = 4;

	/* Public / Package methods */

	public static float getChromaComplexitySimple(Chroma chroma1, Chroma chroma2) {
		float sum = 0;
		for (int i = 0; i < CHROMA_LENGTH; i++) {
			sum += Math.abs(chroma1.values[i] - chroma2.values[i]);
		}
		return sum;
	}

	public static float getChromaComplexityTonal(Chroma chroma1, Chroma chroma2) throws Chroma.WrongChromaSize {
		float[] chromaVector1 = AudioAnalysisHelper.filterChroma(chroma1.values, audibleThreshold);
		int[] harmony1 = AudioAnalysisHelper.createBinaryChord(chromaVector1, maximumNumberOfChordTones);
		float[] chromaVector2 = AudioAnalysisHelper.filterChroma(chroma2.values, audibleThreshold);
		int[] harmony2 = AudioAnalysisHelper.createBinaryChord(chromaVector2, maximumNumberOfChordTones);

		// create chords using Chordanal
		String currentChordTones = Chordanal.getStringOfTones(harmony1);
		String previousChordTones = Chordanal.getStringOfTones(harmony2);
		Harmony harmony_1 = Chordanal.createHarmonyFromRelativeTones(previousChordTones);
		Harmony harmony_2 = Chordanal.createHarmonyFromRelativeTones(currentChordTones);
		if ((harmony_1 != null) && (harmony_2 != null)) {
			List<String> roots1 = Harmanal.getRootsFormatted(harmony_1);
			List<String> roots2 = Harmanal.getRootsFormatted(harmony_2);
			if ((roots1 != null) && (roots2 != null) && (roots1.size() > 0) && (roots2.size() > 0)) {
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
					if (tone1c != null) {
						chromaVector1[tone1c.getNumberMapped()] = 0;
					}
				}
				if (!tone2.equals("")) {
					Tone tone2c = Chordanal.createToneFromRelativeName(tone2);
					if (tone2c != null) {
						chromaVector1[tone2c.getNumberMapped()] = 0;
					}
				}
				if (!tone3.equals("")) {
					Tone tone3c = Chordanal.createToneFromRelativeName(tone3);
					if (tone3c != null) {
						chromaVector1[tone3c.getNumberMapped()] = 0;
					}
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
					if (tone1c != null) {
						chromaVector2[tone1c.getNumberMapped()] = 0;
					}
				}
				if (!tone2_2.equals("")) {
					Tone tone2c = Chordanal.createToneFromRelativeName(tone2_2);
					if (tone2c != null) {
						chromaVector2[tone2c.getNumberMapped()] = 0;
					}
				}
				if (!tone2_3.equals("")) {
					Tone tone3c = Chordanal.createToneFromRelativeName(tone2_3);
					if (tone3c != null) {
						chromaVector2[tone3c.getNumberMapped()] = 0;
					}
				}
			}
		}

		return getChromaComplexitySimple(new Chroma(chromaVector1), new Chroma(chromaVector2));
	}
}
