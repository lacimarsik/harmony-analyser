package harmanal;

/**
 * Class to encapsulate all tones
 */

@SuppressWarnings("CanBeFinal")

class Tone {
	final static int DEFAULT_VOLUME = 100;

	private int number;
	private int volume;

	Tone(int number, int volume) {
		this.number = number;
		this.volume = volume;
	}

	Tone(int number) {
		this(number, DEFAULT_VOLUME);
	}

	int getNumber() {
		return this.number;
	}

	int getNumberMapped() {
		return this.number % 12;
	}

	int getVolume() {
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
