package org.harmony_analyser.chord_analyser;

/**
 * Class to encapsulate all tones
 */

@SuppressWarnings("CanBeFinal")

public class Tone {
	final static int DEFAULT_VOLUME = 100;

	private int number;
	private int volume;

	public static final Tone EMPTY_TONE = new Tone(0, 0);

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

	public int getNumberMapped() {
		return this.number % 12;
	}

	void chromatizeUp() {
		this.number++;
	}

	void chromatizeDown() {
		this.number--;
	}

	void fifthUp() {
		this.number += 7;
	}

	void fifthDown() {
		this.number -= 7;
	}

	String getName() {
		return Chordanal.getToneName(this);
	}

	public String getNameMapped() {
		return Chordanal.getToneNameMapped(this);
	}

	Tone duplicate() {
		return new Tone(this.number, this.volume);
	}
}
