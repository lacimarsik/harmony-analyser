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
		if ((harmony_1 != null) && (harmony_2 != null)) {
			// subtract root tones from the chords
			Harmony rootHarmony1 = Harmanal.getRootHarmony(harmony_1);
			if (rootHarmony1 != null) {
				for (Tone tone : rootHarmony1.tones) {
					chromaVector1[tone.getNumberMapped()] = 0;
				}
			}
			Harmony rootHarmony2 = Harmanal.getRootHarmony(harmony_2);
			if (rootHarmony2 != null) {
				for (Tone tone : rootHarmony2.tones) {
					chromaVector2[tone.getNumberMapped()] = 0;
				}
			}
		}

		return getChromaComplexitySimple(new Chroma(chromaVector1), new Chroma(chromaVector2));
	}
}
