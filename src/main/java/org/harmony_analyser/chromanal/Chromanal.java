package org.harmony_analyser.chromanal;

import org.harmony_analyser.application.services.AudioAnalysisHelper;
import org.harmony_analyser.chordanal.*;

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
		// get common roots for both harmonies
		DatabaseTable commonRoots = Harmanal.getCommonRoots(harmony_1, harmony_2);
		if (!commonRoots.equals(DatabaseTable.EMPTY_RESULT) && !commonRoots.isEmpty()) {
			String rootTones = commonRoots.getAllKeys().get(0).get(commonRoots.getAllKeys().get(0).size() - 1);
			Harmony commonRootHarmony = Chordanal.createHarmonyFromRelativeTones(rootTones);
			for (Tone tone : commonRootHarmony.tones) {
				chromaVector1[tone.getNumberMapped()] = 0;
			}
			for (Tone tone : commonRootHarmony.tones) {
				chromaVector2[tone.getNumberMapped()] = 0;
			}
		}

		return getChromaComplexitySimple(new Chroma(chromaVector1), new Chroma(chromaVector2));
	}
}
