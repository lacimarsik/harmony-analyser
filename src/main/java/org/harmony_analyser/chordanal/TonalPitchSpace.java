package org.harmony_analyser.chordanal;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to handle computation of Fred Lerdahl's Tonal Pitch Space (TPS) chord distance
 * for more information on TPS: http://www.oupcanada.com/catalog/9780195178296.html
 */

class TonalPitchSpace {
	private Harmony octaveLevel;
	private Harmony fifthsLevel;
	private Harmony triadicLevel;
	private Harmony diatonicLevel;
	private Harmony chromaticLevel;

	private TonalPitchSpace() {
		this.chromaticLevel = new Key(0, Chordanal.CHROMATIC).getScaleHarmony();
	}

	TonalPitchSpace(Harmony octaveLevel, Harmony fifthsLevel, Harmony triadicLevel, Harmony diatonicLevel) {
		this();
		this.octaveLevel = octaveLevel;
		this.fifthsLevel = fifthsLevel;
		this.triadicLevel = triadicLevel;
		this.diatonicLevel = diatonicLevel;
	}

	private TonalPitchSpace(Harmony harmony, Key contextKey) {
		this();

		// Set octave level to the harmony1 root
		this.octaveLevel = Chordanal.createHarmonyFromRelativeTones(Chordanal.getRootTone(harmony).getNameMapped());

		// Set fifths level to the harmony1 root and fifth (if present)
		this.fifthsLevel = Chordanal.createHarmonyFromRelativeTones(Chordanal.getRootTone(harmony).getNameMapped() + " " + Chordanal.getFifthToneFromHarmony(harmony).getNameMapped());

		// Set triadic level to the harmony itself
		this.triadicLevel = harmony;

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

	private void plotLevel(Harmony level) {
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
	 * Calculates the number of non-common pitch classes between the basic space of harmony1 and basic space of harmony2
	 * - basic space of harmony is its space on levels (a) - (d) in TPS
	 * - commonKey sets the (d) level for comparison in TPS
	 */
	static float getNonCommonPitchClassesDistance(Harmony harmony1, Harmony harmony2, Key commonKey) {
		TonalPitchSpace tps1 = new TonalPitchSpace(harmony1, commonKey);
		tps1.plot();
		TonalPitchSpace tps2 = new TonalPitchSpace(harmony2, commonKey);
		tps2.plot();

		return 0;
	}

	/**
	 * Calculates the TPS distance between
	 * harmony1 (with the root tone root1, in the context of key key1)
	 * and
	 * harmony2 (with the root tone root2, in the context of key key2)
	 * - keys are 'context' keys, and need to be provided by a key-finding algorithm
	 * - roots are chord labels without maj/min mode, and need to be provided by a chord-estimation algorithm
	 */
	public static float getTPSDistance(Harmony harmony1, Tone root1, Key key1, Harmony harmony2, Tone root2, Key key2) {
		return getKeyDistance(key1, key2) + getRootDistance(root1, root2) + getNonCommonPitchClassesDistance(harmony1, harmony2, key1);
	}
}
