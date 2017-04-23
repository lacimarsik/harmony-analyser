package org.harmony_analyser.jharmonyanalyser.chord_analyser;

import java.util.List;

/**
 * Class to encapsulate all keys
 */

@SuppressWarnings("CanBeFinal")

public class Key {
	int root;
	int keyType;

	public static final Key EMPTY_KEY = new Key(0, 0);

	public Key(int root, int keyType) {
		this.root = root;
		this.keyType = keyType;
	}

	/* Public / Package methods */

	public int[] getScale() {
		int[] result;
		if (keyType == Chordanal.CHROMATIC) {
			result = new int[13];
		} else {
			result = new int[7];
		}
		List<String> scaleIntervals = Chordanal.getScaleIntervals(keyType);

		int tone = 0;
		result[tone] = root;
		tone++;
		for (String interval : scaleIntervals) {
			result[tone] = (root + Integer.parseInt(interval)) % 12;
			tone++;
		}
		return result;
	}

	public String getStringVector() {
		String result = "";
		for (int i = 0; i < 12; i++) {
			String boolValue = "0";
			for (Tone tone : getScaleHarmony().tones) {
				if (tone.getNumberMapped() == i) {
					boolValue = "1";
				}
			}
			result += boolValue + " ";
		}
		return result;
	}

	Chord getScaleHarmony() {
		int[] tones = getScale();
		for (int i = 0; i < tones.length; i++) {
			tones[i] = tones[i] + 60;
		}
		return new Chord(tones);
	}

	Chord getTonic() {
		int[] tonic = new int[3];
		tonic[0] = getScale()[0];
		tonic[1] = getScale()[2];
		tonic[2] = getScale()[4];
		return new Chord(tonic);
	}

	Chord getSubdominant() {
		int[] subdominant = new int[3];
		subdominant[0] = getScale()[0];
		subdominant[1] = getScale()[3];
		subdominant[2] = getScale()[5];
		return new Chord(subdominant);
	}

	Chord getDominant() {
		int[] dominant = new int[3];
		dominant[0] = getScale()[1];
		dominant[1] = getScale()[4];
		dominant[2] = getScale()[6];
		return new Chord(dominant);
	}

	Tone getRoot() {
		return new Tone(this.root);
	}
}
