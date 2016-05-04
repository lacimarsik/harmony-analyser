package harmanal;

/**
 * Class to encapsulate all tones
 */

public class Tone {
	
	final public static int DEFAULT_VOLUME = 100;
	
	private int number;
	private int volume;
	
	Tone(int number, int volume) {
		this.number = number;
		this.volume = volume;
	}
	
	Tone(int number) {
		this(number, DEFAULT_VOLUME);
	}
	
	public int getNumber() {
		return this.number;
	}
	
	public int getNumberMapped() {
		return this.number % 12;
	}
	
	public int getVolume() {
		return this.volume;
	}
	
	public void chromatizeUp() {
		this.number++;
	}
	
	public void chromatizeDown() {
		this.number--;
	}
	
	public String getName() {
		return Chordanal.getToneName(this);
	}
	
	public String getNameMapped() {
		return Chordanal.getToneNameMapped(this);
	}	

}
