package org.harmony_analyser.chordanal;

/**
 * Class to handle computation of Fred Lerdahl's Tonal Pitch Space (TPS) chord distance
 * for more information on TPS: http://www.oupcanada.com/catalog/9780195178296.html
 */

public class TonalPitchSpace {
	/* Public / Package methods */

	/**
	 * Calculates the distance between the context keys on the Circle of Fifths (without maj/min mode)
	 */
	private int getKeyDistance(Key key1, Key key2) {
		return 0;
	}

	/**
	 * Calculates the distance between the chord roots on the Circle of Fifths (without maj/min mode)
	 */
	private int getRootDistance(Tone root1, Tone root2) {
		return 0;
	}

	/**
	 * Calculates the number of non-common pitch classes between the basic space of harmony1 and basic space of harmony2
	 * - basic space of harmony is its space on levels (a) - (d) in TPS
	 * - commonKey sets the (d) level for comparison in TPS
	 */
	private int getNonCommonPitchClassesDistance(Harmony harmony1, Harmony harmony2, Key commonKey) {
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
	public int getTPSDistance(Harmony harmony1, Tone root1, Key key1, Harmony harmony2, Tone root2, Key key2) {
		return getKeyDistance(key1, key2) + getRootDistance(root1, root2) + getNonCommonPitchClassesDistance(harmony1, harmony2, key1);
	}
}
