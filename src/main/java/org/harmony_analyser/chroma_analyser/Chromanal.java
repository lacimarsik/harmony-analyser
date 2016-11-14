package org.harmony_analyser.chroma_analyser;

import org.harmony_analyser.application.services.AudioAnalysisHelper;
import org.harmony_analyser.chord_analyser.*;

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

	public static float getChromaComplexityTonal(Chroma chroma1, Chroma chroma2, boolean verbose) throws Chroma.WrongChromaSize {
		if (verbose) AudioAnalysisHelper.logChromaFloatArray(chroma1.values, "Chroma 1");
		if (verbose) AudioAnalysisHelper.logChromaFloatArray(chroma2.values, "Chroma 2");

		float[] chromaVector1 = AudioAnalysisHelper.filterChroma(chroma1.values, audibleThreshold);

		if (verbose) AudioAnalysisHelper.logChromaFloatArray(chromaVector1, "Filtered Chroma 1");

		int[] harmony1 = AudioAnalysisHelper.createBinaryChord(chromaVector1, maximumNumberOfChordTones);

		float[] chromaVector2 = AudioAnalysisHelper.filterChroma(chroma2.values, audibleThreshold);

		if (verbose) AudioAnalysisHelper.logChromaFloatArray(chromaVector1, "Filtered Chroma 2");

		int[] harmony2 = AudioAnalysisHelper.createBinaryChord(chromaVector2, maximumNumberOfChordTones);

		// create chords using Chordanal
		String currentChordTones = Chordanal.getStringOfTones(harmony2);
		String previousChordTones = Chordanal.getStringOfTones(harmony1);
		Chord chord_1 = Chordanal.createHarmonyFromRelativeTones(previousChordTones);
		Chord chord_2 = Chordanal.createHarmonyFromRelativeTones(currentChordTones);

		// get common roots for both harmonies
		DatabaseTable commonRoots = Harmanal.getCommonRoots(chord_1, chord_2);
		if (!commonRoots.equals(DatabaseTable.EMPTY_RESULT) && !commonRoots.isEmpty()) {
			String rootTones = commonRoots.getAllKeys().get(0).get(commonRoots.getAllKeys().get(0).size() - 1);
			Chord commonRootChord = Chordanal.createHarmonyFromRelativeTones(rootTones);
			for (Tone tone : commonRootChord.tones) {
				chromaVector1[tone.getNumberMapped()] = 0;
			}
			for (Tone tone : commonRootChord.tones) {
				chromaVector2[tone.getNumberMapped()] = 0;
			}
			if (verbose) AudioAnalysisHelper.logHarmony(commonRootChord, "Common Root Chord");
		}

		if (verbose) AudioAnalysisHelper.logChromaFloatArray(chroma1.values, "Final Chroma 1");
		if (verbose) AudioAnalysisHelper.logChromaFloatArray(chroma2.values, "Final Chroma 2");

		return getChromaComplexitySimple(new Chroma(chromaVector1), new Chroma(chromaVector2));
	}
}
