package harmanal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class to encapsulate all harmonies
 */

public class Harmony {
	
	public List<Tone> tones;
	
	/**
	 * Creates Harmony from integer array. Can be unsorted and with duplicities.
	 */
	
	Harmony(int[] numberArray) {	
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
	
		tones = new ArrayList<Tone>();
		for (int i = 0; i < numberArrayCorrectedLength.length; i++) {
			tones.add(new Tone(numberArrayCorrectedLength[i]));
		}
	}
	
	/**
	 * Creates Harmony from integer array. Can alse be unsorted and with duplicities.
	 */
	
	Harmony(List<Tone> tones) {
		this.tones = tones;
	}
	
	public String[] getIntervals() {
		String[] result;
		if (tones.size() == 1) {
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
	
	public String getToneNames() {
		return Chordanal.getHarmonyToneNames(this);
	}
	
	public String getToneNamesMapped() {
		return Chordanal.getHarmonyToneNamesMapped(this);
	}
	
	public Harmony inversionUp() {
		ArrayList<Tone> newTones = new ArrayList<Tone>();
		
		newTones.addAll(tones);
		newTones.remove(0);
		if ((tones.get(0).getNumber()+12) > 127) {
			return null;
		}
		newTones.add(new Tone(tones.get(0).getNumber()+12));
		
		return new Harmony(newTones);
	}
	
	public Harmony inversionDown() {
		ArrayList<Tone> newTones = new ArrayList<Tone>();
		
		if ((tones.get(tones.size()-1).getNumber()-12) < 0) {
			return null;
		}
		newTones.add(new Tone(tones.get(tones.size()-1).getNumber()-12));
		newTones.addAll(tones);
		newTones.remove(newTones.size()-1);
		
		return new Harmony(newTones);
	}
	
	public Harmony getSubHarmony(int[] toneIndexes) {
		ArrayList<Tone> newTones = new ArrayList<Tone>();
		for (int i = 0; i < toneIndexes.length; i++) {
			newTones.add(tones.get(toneIndexes[i]));
		}
		return new Harmony(newTones);
	}
	
	public Harmony getCommonTones(Harmony otherHarmony) {
		ArrayList<Tone> found = new ArrayList<Tone>();
		for (Tone tone1 : tones) {
			for (Tone tone2 : otherHarmony.tones) {
				if (tone1.getNameMapped().equals(tone2.getNameMapped())) {
					
					Tone tone = Chordanal.createToneFromRelativeName(tone1.getNameMapped());
					if (tone == null) {
						return null;
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
		
		return new Harmony(found);
	}
	
	public List<Tone> subtractTones(Harmony otherHarmony) {
		
		List<Tone> added = new ArrayList<Tone>();
		
		for (Tone tone : tones) {
			if (!otherHarmony.containsMapped(tone)) {
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
	
	public boolean containsMapped(Tone tone) {
		for (Tone harmonyTone : tones) {
			if (harmonyTone.getNumberMapped() == tone.getNumberMapped()) {
				return true;
			}
		}
		return false;
	}
	
	public boolean containsMapped(Harmony harmony) {
		for (Tone tone : harmony.tones) {
			if (!containsMapped(tone)) {
				return false;
			}
		}
		return true;
	}
	
}
