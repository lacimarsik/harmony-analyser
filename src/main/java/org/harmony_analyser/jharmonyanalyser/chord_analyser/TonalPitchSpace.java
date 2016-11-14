package org.harmony_analyser.jharmonyanalyser.chord_analyser;

import org.harmony_analyser.jharmonyanalyser.services.AudioAnalysisHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to handle computation of Fred Lerdahl's Tonal Pitch Space (TPS) chord distance
 * for more information on TPS: http://www.oupcanada.com/catalog/9780195178296.html
 */

public class TonalPitchSpace {
	private Chord octaveLevel;
	private Chord fifthsLevel;
	private Chord triadicLevel;
	private Chord diatonicLevel;
	private final Chord chromaticLevel;

	private TonalPitchSpace() {
		this.chromaticLevel = new Key(0, Chordanal.CHROMATIC).getScaleHarmony();
	}

	TonalPitchSpace(Chord octaveLevel, Chord fifthsLevel, Chord triadicLevel, Chord diatonicLevel) {
		this();
		this.octaveLevel = octaveLevel;
		this.fifthsLevel = fifthsLevel;
		this.triadicLevel = triadicLevel;
		this.diatonicLevel = diatonicLevel;
	}

	private TonalPitchSpace(Chord chord, Key contextKey) {
		this();

		// Set octave level to the harmony1 root
		this.octaveLevel = Chordanal.createHarmonyFromRelativeTones(Chordanal.getRootTone(chord).getNameMapped());

		// Set fifths level to the harmony1 root and fifth (if present)
		this.fifthsLevel = Chordanal.createHarmonyFromRelativeTones(Chordanal.getRootTone(chord).getNameMapped() + " " + Chordanal.getFifthToneFromHarmony(chord).getNameMapped());

		// Set triadic level to the chord itself
		this.triadicLevel = chord;

		// Set diatonic level to the common Key + any of the above level
		this.diatonicLevel = contextKey.getScaleHarmony();
		List<Tone> tonesToAdd = new ArrayList<>();
		for (Tone level3tone : this.triadicLevel.tones) {
			for (Tone level4tone : this.diatonicLevel.tones) {
				boolean found = false;
				if (level3tone.getNumberMapped() == level4tone.getNumberMapped()) {
					found = true;
				}
				if (!found) {
					tonesToAdd.add(level3tone);
				}
			}
		}
		for (Tone toneToAdd : tonesToAdd) {
			this.diatonicLevel.addTone(toneToAdd);
		}
	}

	void plot() {
		plotLevel(octaveLevel);
		plotLevel(fifthsLevel);
		plotLevel(triadicLevel);
		plotLevel(diatonicLevel);
		plotLevel(chromaticLevel);
	}

	private void plotLevel(Chord level) {
		for (int i = 0; i < 12; i++ ) {
			boolean found = false;
			for (Tone tone : level.tones) {
				if ((tone.getNumberMapped()) == i) {
					found = true;
				}
			}
			if (found) {
				switch (i) {
					case 10:
						System.out.print("a");
						break;
					case 11:
						System.out.print("b");
						break;
					default:
						System.out.print(i);
				}
			} else {
				System.out.print(" ");
			}
		}
		System.out.println();
	}

	private static Chord getNonCommonHarmonyOnLevel(Chord levelChord1, Chord levelChord2) {
		List<Tone> nonCommonTones = new ArrayList<>();
		nonCommonTones.addAll(levelChord1.subtractTones(levelChord2));
		nonCommonTones.addAll(levelChord2.subtractTones(levelChord1));

		return new Chord(nonCommonTones);
	}

	/* Static methods */

	/**
	 * Calculates the distance between the chord roots on the Circle of Fifths (without maj/min mode)
	 */
	static int getRootDistance(Tone root1, Tone root2) {
		return Chordanal.getDistanceOnCircleOfFifths(root1, root2);
	}

	/**
	 * Calculates the distance between the context keys on the Circle of Fifths (without maj/min mode)
	 */
	static int getKeyDistance(Key key1, Key key2) {
		return Chordanal.getDistanceOnCircleOfFifths(key1.getRoot(), key2.getRoot());
	}

	/**
	 * Calculates the number of non-common pitch classes between the basic space of chord1 and basic space of chord2
	 * - basic space of harmony is its space on levels (a) - (d) in TPS
	 * - commonKey sets the (d) level for comparison in TPS
	 */
	static float getNonCommonPitchClassesDistance(Chord chord1, Chord chord2, Key commonKey, boolean verbose) {
		TonalPitchSpace tps1 = new TonalPitchSpace(chord1, commonKey);
		if (verbose) tps1.plot();
		TonalPitchSpace tps2 = new TonalPitchSpace(chord2, commonKey);
		if (verbose) tps2.plot();

		// level-by-level finding out the non-common pitch classes
		Chord nonCommonChordOctaveLevel = getNonCommonHarmonyOnLevel(tps1.octaveLevel, tps2.octaveLevel);
		Chord nonCommonChordFifthsLevel = getNonCommonHarmonyOnLevel(tps1.fifthsLevel, tps2.fifthsLevel);
		Chord nonCommonChordTriadicLevel = getNonCommonHarmonyOnLevel(tps1.triadicLevel, tps2.triadicLevel);
		Chord nonCommonChordDiatonicLevel = getNonCommonHarmonyOnLevel(tps1.diatonicLevel, tps2.diatonicLevel);

		if (verbose) {
			AudioAnalysisHelper.logHarmony(nonCommonChordOctaveLevel, "Non-common tones Octave Level");
			AudioAnalysisHelper.logHarmony(nonCommonChordFifthsLevel, "Non-common tones Fifths Level");
			AudioAnalysisHelper.logHarmony(nonCommonChordTriadicLevel, "Non-common tones Triadic Level");
			AudioAnalysisHelper.logHarmony(nonCommonChordDiatonicLevel, "Non-common tones Diatonic Level");
		}
		int countNonCommonTones = 0;
		countNonCommonTones += nonCommonChordOctaveLevel.tones.size();
		countNonCommonTones += nonCommonChordFifthsLevel.tones.size();
		countNonCommonTones += nonCommonChordTriadicLevel.tones.size();
		countNonCommonTones += nonCommonChordDiatonicLevel.tones.size();

		return (float) countNonCommonTones;
	}

	/**
	 * Calculates the TPS distance between
	 * chord1 (with the root tone root1, in the context of key key1)
	 * and
	 * chord2 (with the root tone root2, in the context of key key2)
	 * - keys are 'context' keys, and need to be provided by a key-finding algorithm
	 * - roots are chord labels without maj/min mode, and need to be provided by a chord-estimation algorithm
	 */
	public static float getTPSDistance(Chord chord1, Tone root1, Key key1, Chord chord2, Tone root2, Key key2, boolean verbose) {
		// XXX: Use symmetric version inspired by:
		// De Haas et al.: TONAL PITCH STEP DISTANCE: A SIMILARITY MEASURE FOR CHORD PROGRESSIONS
		// Rocher et al.: A SURVEY OF CHORD DISTANCES WITH COMPARISON FOR CHORD ANALYSIS
		float distanceXY = getKeyDistance(key1, key2) + getRootDistance(root1, root2) + getNonCommonPitchClassesDistance(chord1, chord2, key1, verbose);
		float distanceYX = getKeyDistance(key2, key1) + getRootDistance(root2, root1) + getNonCommonPitchClassesDistance(chord2, chord1, key2, verbose);
		if (verbose) System.out.println("distanceXY: " + distanceXY + ", distanceYX: " + distanceYX + ", (distanceXY + distanceYX / 2): " + (distanceXY + distanceYX) / 2);
		return (distanceXY + distanceYX) / 2;
	}
}
