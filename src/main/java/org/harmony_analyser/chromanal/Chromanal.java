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

	public static float getChromaComplexityTonal(Chroma chroma1, Chroma chroma2, boolean verbose) throws Chroma.WrongChromaSize {
		if (verbose) {
			System.out.println("Chroma 1:");
			for (float value : chroma1.values) {
				System.out.print(value + " ");
			}
			System.out.println();

			System.out.println("Chroma 2:");
			for (float value : chroma2.values) {
				System.out.print(value + " ");
			}
			System.out.println();
		}

		float[] chromaVector1 = AudioAnalysisHelper.filterChroma(chroma1.values, audibleThreshold);

		if (verbose) {
			System.out.println("Chroma 1:");
			for (float f : chromaVector1) {
				System.out.print(f + " ");
			}
			System.out.println();
		}

		int[] harmony1 = AudioAnalysisHelper.createBinaryChord(chromaVector1, maximumNumberOfChordTones);

		if (verbose) {
			System.out.println("Chroma 1 (check):");
			for (float f : chromaVector1) {
				System.out.print(f + " ");
			}
			System.out.println();
		}

		float[] chromaVector2 = AudioAnalysisHelper.filterChroma(chroma2.values, audibleThreshold);

		if (verbose) {
			System.out.println("Chroma 2:");
			for (float f : chromaVector2) {
				System.out.print(f + " ");
			}
			System.out.println();
		}

		int[] harmony2 = AudioAnalysisHelper.createBinaryChord(chromaVector2, maximumNumberOfChordTones);

		if (verbose) {
			System.out.println("Chroma 2 (check):");
			for (float f : chromaVector2) {
				System.out.print(f + " ");
			}
			System.out.println();
		}

		// create chords using Chordanal
		String currentChordTones = Chordanal.getStringOfTones(harmony2);
		String previousChordTones = Chordanal.getStringOfTones(harmony1);
		Harmony harmony_1 = Chordanal.createHarmonyFromRelativeTones(previousChordTones);
		Harmony harmony_2 = Chordanal.createHarmonyFromRelativeTones(currentChordTones);

		// subtract root tones from the chords
		if (verbose) {
			System.out.println("First chord:");
		}
		Harmony rootHarmony1 = Harmanal.getRootHarmony(harmony_1);
		for (Tone tone : rootHarmony1.tones) {
			if (verbose) {
				System.out.print(tone.getNameMapped() + " ");
			}
			chromaVector1[tone.getNumberMapped()] = 0;
		}
		Harmony rootHarmony2 = Harmanal.getRootHarmony(harmony_2);
		if (verbose) {
			System.out.println("Second chord:");
		}
		for (Tone tone : rootHarmony2.tones) {
			if (verbose) {
				System.out.print(tone.getNameMapped() + " ");
			}
			chromaVector2[tone.getNumberMapped()] = 0;
		}
		if (verbose) {
			System.out.println("Chroma 1:");
			for (float f : chromaVector1) {
				System.out.print(f + " ");
			}
			System.out.println();
		}
		if (verbose) {
			System.out.println("Chroma 2:");
			for (float f : chromaVector2) {
				System.out.print(f + " ");
			}
			System.out.println();
		}
		return getChromaComplexitySimple(new Chroma(chromaVector1), new Chroma(chromaVector2));
	}
}
