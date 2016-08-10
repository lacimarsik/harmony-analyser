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

	public Tone(int number) {
		this(number, DEFAULT_VOLUME);
	}

	public int getNumber() {
		return this.number;
	}

	int getNumberMapped() {
		return this.number % 12;
	}

	public int getVolume() {
		return this.volume;
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
