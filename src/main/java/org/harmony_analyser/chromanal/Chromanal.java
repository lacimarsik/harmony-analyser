package org.harmony_analyser.chromanal;

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

	/* Private methods */
}
