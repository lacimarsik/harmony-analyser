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
		String currentChordTones = Chordanal.getStringOfTones(harmony2);
		String previousChordTones = Chordanal.getStringOfTones(harmony1);
		Harmony harmony_1 = Chordanal.createHarmonyFromRelativeTones(previousChordTones);
		Harmony harmony_2 = Chordanal.createHarmonyFromRelativeTones(currentChordTones);
		if ((harmony_1 != null) && (harmony_2 != null)) {
			DatabaseTable roots1 = Harmanal.getRoots(harmony_1);
			DatabaseTable roots2 = Harmanal.getRoots(harmony_2);
			if ((roots1 != null) && (roots2 != null) && !roots1.isEmpty() && !roots2.isEmpty()) {
				List<String> rootKeys1 = roots1.getAllKeys().get(0);
				List<String> rootKeys2 = roots2.getAllKeys().get(0);
				Scanner sc1 = new Scanner(rootKeys1.get(2));
				String tone1 = "";
				String tone2 = "";
				String tone3 = "";
				if (sc1.hasNext()) {
					tone1 = sc1.next();
				}
				if (sc1.hasNext()) {
					tone2 = sc1.next();
				}
				if (sc1.hasNext()) {
					tone3 = sc1.next();
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
				Scanner sc2 = new Scanner(rootKeys2.get(2));
				String tone2_1 = "";
				String tone2_2 = "";
				String tone2_3 = "";
				if (sc2.hasNext()) {
					tone2_1 = sc2.next();
				}
				if (sc2.hasNext()) {
					tone2_2 = sc2.next();
				}
				if (sc2.hasNext()) {
					tone2_3 = sc2.next();
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
