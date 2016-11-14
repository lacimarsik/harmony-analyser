package org.harmony_analyser.chroma_analyser;

/**
 * Class to encapsulate Chromas
 */

@SuppressWarnings({"SameParameterValue", "WeakerAccess"})

public class Chroma {
	public final float[] values;

	public static final Chroma EMPTY_CHROMA = new Chroma();

	/* Exceptions */

	public class WrongChromaSize extends Exception {
		public WrongChromaSize(String message) {
			super(message);
		}
	}

	public Chroma() {
		this.values = new float[0];
	}

	public Chroma(float[] chroma) throws WrongChromaSize {
		this.values = new float[Chromanal.CHROMA_LENGTH];
		if (chroma.length != Chromanal.CHROMA_LENGTH) {
			throw new WrongChromaSize("Wrong Chroma size");
		}
		System.arraycopy(chroma, 0, values, 0, Chromanal.CHROMA_LENGTH - 1);
	}

	/* Public / Package methods */

	/* Private methods */
}
