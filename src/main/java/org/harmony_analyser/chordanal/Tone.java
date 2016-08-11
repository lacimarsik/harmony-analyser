package org.harmony_analyser.chordanal;

/**
 * Class to encapsulate all tones
 */

@SuppressWarnings("CanBeFinal")

public class Tone {
	final static int DEFAULT_VOLUME = 100;

	private int number;
	private int volume;

	Tone(int number, int volume) {
		this.number = number;
		this.volume = volume;
	}

	/* Public / Package methods */

	public Tone(int number) {
		this(number, DEFAULT_VOLUME);
	}

	public int getNumber() {
		return this.number;
	}

	public int getVolume() {
		return this.volume;
	}

	int getNumberMapped() {
		return this.number % 12;
	}

	void chromatizeUp() {
		this.number++;
	}

	void chromatizeDown() {
		this.number--;
	}

	String getName() {
		return Chordanal.getToneName(this);
	}

	String getNameMapped() {
		return Chordanal.getToneNameMapped(this);
	}
}
