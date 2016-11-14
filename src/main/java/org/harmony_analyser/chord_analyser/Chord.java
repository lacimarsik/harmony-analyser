package org.harmony_analyser.chord_analyser;

import java.util.*;

/**
 * Class to encapsulate all chords
 */

@SuppressWarnings("CanBeFinal")

public class Chord {
	public List<Tone> tones;

	public static final Chord EMPTY_CHORD = new Chord(new ArrayList<>());

	/**
	 * Creates Chord from integer array. Can be unsorted and with duplicities.
	 */

	public Chord(int[] numberArray) {
		Arrays.sort(numberArray);
		int numberArrayNoDuplicates[] = new int[numberArray.length];
		numberArrayNoDuplicates[0] = numberArray[0];
		int k = 1;
		for (int i = 0; i < numberArray.length - 1; i++) {
			if(numberArray[i+1] != (numberArray[i])) {
				numberArrayNoDuplicates[k] = numberArray[i + 1];
				k++;
			}
		}
		int[] numberArrayCorrectedLength = Arrays.copyOf(numberArrayNoDuplicates, k);

		tones = new ArrayList<>();
		for (int number : numberArrayCorrectedLength) {
			tones.add(new Tone(number));
		}
	}

	/**
	 * Creates Chord from tones collection. Should be without duplicities.
	 */

	public Chord(List<Tone> tones) {
		this.tones = tones;
	}

	/* Public / Package methods */

	public String getToneNames() {
		return Chordanal.getHarmonyToneNames(this);
	}

	public String getToneNamesMapped() {
		return Chordanal.getHarmonyToneNamesMapped(this);
	}

	String[] getIntervals() {
		String[] result;

		if (tones.size() <= 1) {
			result = new String[1];
			result[0] = "0";
			return result;
		} else {
			result = new String[tones.size()-1];
		}
		for (int i = 1; i < tones.size(); i++) {
			result[i-1] = Integer.toString(tones.get(i).getNumber() - tones.get(0).getNumber());
		}
		return result;
	}

	Chord inversionUp() {
		ArrayList<Tone> newTones = new ArrayList<>();

		newTones.addAll(tones);
		newTones.remove(0);
		if ((tones.get(0).getNumber()+12) > 127) {
			return Chord.EMPTY_CHORD;
		}
		newTones.add(new Tone(tones.get(0).getNumber()+12));

		return new Chord(newTones);
	}

	Chord inversionDown() {
		ArrayList<Tone> newTones = new ArrayList<>();

		if ((tones.get(tones.size() - 1).getNumber() - 12) < 0) {
			return Chord.EMPTY_CHORD;
		}
		newTones.add(new Tone(tones.get(tones.size()-1).getNumber()-12));
		newTones.addAll(tones);
		newTones.remove(newTones.size()-1);

		return new Chord(newTones);
	}

	Chord getSubHarmony(int[] toneIndexes) {
		ArrayList<Tone> newTones = new ArrayList<>();
		for (int index : toneIndexes) {
			newTones.add(tones.get(toneIndexes[index]));
		}
		return new Chord(newTones);
	}

	Chord getCommonTones(Chord otherChord) {
		ArrayList<Tone> found = new ArrayList<>();
		for (Tone tone1 : tones) {
			for (Tone tone2 : otherChord.tones) {
				if (tone1.getNameMapped().equals(tone2.getNameMapped())) {
					Tone tone = Chordanal.createToneFromRelativeName(tone1.getNameMapped());
					if (tone.equals(Tone.EMPTY_TONE)) {
						return Chord.EMPTY_CHORD;
					} else {
						boolean alreadyInFound = false;
						for (Tone inFound : found) {
							if (tone.getNameMapped().equals(inFound.getNameMapped())) {
								alreadyInFound = true;
							}
						}
						if (!alreadyInFound) {
							found.add(tone);
						}
					}
					break;
				}
			}
		}

		return new Chord(found);
	}

	List<Tone> subtractTones(Chord otherChord) {
		List<Tone> added = new ArrayList<>();

		for (Tone tone : tones) {
			if (!otherChord.containsMapped(tone)) {
				boolean alreadyInAdded = false;
				for (Tone inAdded : added) {
					if (tone.getNameMapped().equals(inAdded.getNameMapped())) {
						alreadyInAdded = true;
					}
				}
				if (!alreadyInAdded) {
					added.add(tone);
				}
			}
		}
		return added;
	}

	boolean containsMapped(Tone tone) {
		for (Tone harmonyTone : tones) {
			if (harmonyTone.getNumberMapped() == tone.getNumberMapped()) {
				return true;
			}
		}
		return false;
	}

	boolean containsMapped(Chord chord) {
		for (Tone tone : chord.tones) {
			if (!containsMapped(tone)) {
				return false;
			}
		}
		return true;
	}

	public void addTone(Tone tone) {
		this.tones.add(tone);
	}
}
