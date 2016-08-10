package harmanal;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Class to handle all static naming and structure analysis
 * 
 * version 1.2
 */

class Chordanal {
	/* static data arrays */

	final static int MAJOR = 0;
	final static int MINOR = 1;

	final static int TONIC = 0;
	final static int SUBDOMINANT = 1;
	final static int DOMINANT = 2;

	final static DatabaseTable tonesNames;

	final static DatabaseTable functionTable; // function id | function abbreviation
	final static DatabaseTable functionNameTable; // function abbreviation | function name

	final static DatabaseTable scaleTable; // distances in the scale | scale id
	final private static DatabaseTable scaleNameTable; // scale id | scale type name

	final private static DatabaseTable intervalTable; // distance | list of abbreviations
	final private static DatabaseTable intervalCharacter; // distance | character
	final private static DatabaseTable intervalNameTable; // list of abbreviations | list of names

	final private static DatabaseTable triadTable; // tuple of distances | list of abbreviations
	final private static DatabaseTable triadCharacter; // tuple of distances | character
	final private static DatabaseTable triadNameTable; // list of abbreviations | list of names

	final private static DatabaseTable tetraTable; // triad of distances | list of abbreviations
	final private static DatabaseTable tetraCharacter; // triad of distances | character
	final private static DatabaseTable tetraNameTable; // list of abbreviations | list of names

	static {
		/* Initialization */

		tonesNames = new DatabaseTable();

		tonesNames.add("0;C;B#;Dbb");
		tonesNames.add("1;C#;Db;B##");
		tonesNames.add("2;D;C##;Ebb");
		tonesNames.add("3;D#;Eb;Fbb");
		tonesNames.add("4;E;Fb;D##");
		tonesNames.add("5;F;E#;Gbb");
		tonesNames.add("6;F#;Gb;E##");
		tonesNames.add("7;G;F##;Abb");
		tonesNames.add("8;G#;Ab");
		tonesNames.add("9;A;G##;Bbb");
		tonesNames.add("10;A#;Bb;Cbb");
		tonesNames.add("11;B;Cb;A##");

		functionTable = new DatabaseTable();
		functionTable.add("0;T");
		functionTable.add("1;S");
		functionTable.add("2;D");

		functionNameTable = new DatabaseTable();
		functionNameTable.add("T;Tonic");
		functionNameTable.add("S;Subdominant");
		functionNameTable.add("D;Dominant");

		scaleTable = new DatabaseTable();
		scaleTable.add("2,4,5,7,9,11;0");
		scaleTable.add("2,3,5,7,8,10;1");

		scaleNameTable = new DatabaseTable();
		scaleNameTable.add("0;major");
		scaleNameTable.add("1;minor");

		intervalTable = new DatabaseTable();
		intervalTable.add("0;P1,d2");
		intervalTable.add("1;m2,A1,dd3");
		intervalTable.add("2;M2,DA1,d3");
		intervalTable.add("3;m3,A2,dd4");
		intervalTable.add("4;M3,DA2,d4");
		intervalTable.add("5;P4,A3,dd5");
		intervalTable.add("6;A4,d5");
		intervalTable.add("7;P5,DA4,d6");
		intervalTable.add("8;m6,A5,dd7");
		intervalTable.add("9;M6,DA5,d7");
		intervalTable.add("10;m7,A6,dd8");
		intervalTable.add("11;M7,DA6,d8");
		intervalTable.add("12;P8,A7");

		intervalCharacter = new DatabaseTable();
		intervalCharacter.add("0;consonant");
		intervalCharacter.add("1;sharp dissonant");
		intervalCharacter.add("2;mild dissonant");
		intervalCharacter.add("3;consonant");
		intervalCharacter.add("4;consonant");
		intervalCharacter.add("5;consonant");
		intervalCharacter.add("6;dissonant");
		intervalCharacter.add("7;consonant");
		intervalCharacter.add("8;consonant");
		intervalCharacter.add("9;consonant");
		intervalCharacter.add("10;mild dissonant");
		intervalCharacter.add("11;sharp dissonant");
		intervalCharacter.add("12;consonant");

		intervalNameTable = new DatabaseTable();
		intervalNameTable.add("P;perfect");
		intervalNameTable.add("m;minor");
		intervalNameTable.add("M;major");
		intervalNameTable.add("A;augmented");
		intervalNameTable.add("d;diminished");
		intervalNameTable.add("DA;double augmented");
		intervalNameTable.add("dd;double diminished");
		intervalNameTable.add("A4,d5;tritone");
		intervalNameTable.add("1;unisone");
		intervalNameTable.add("2;second");
		intervalNameTable.add("3;third");
		intervalNameTable.add("4;fourth");
		intervalNameTable.add("5;fifth");
		intervalNameTable.add("6;sixth");
		intervalNameTable.add("7;seventh");
		intervalNameTable.add("8;octave");

		triadTable = new DatabaseTable();
		triadTable.add("3,6;dim5");
		triadTable.add("3,7;min5");
		triadTable.add("4,7;maj5");
		triadTable.add("4,8;aug5,aug6,aug6-4");
		triadTable.add("3,9;dim6");
		triadTable.add("4,9;min6");
		triadTable.add("3,8;maj6");
		triadTable.add("6,9;dim6-4");
		triadTable.add("5,8;min6-4");
		triadTable.add("5,9;maj6-4");

		triadCharacter = new DatabaseTable();
		triadCharacter.add("3,6;dissonant");
		triadCharacter.add("3,7;consonant");
		triadCharacter.add("4,7;consonant");
		triadCharacter.add("4,8;dissonant");
		triadCharacter.add("3,9;dissonant");
		triadCharacter.add("4,9;consonant");
		triadCharacter.add("3,8;consonant");
		triadCharacter.add("6,9;dissonant");
		triadCharacter.add("5,8;consonant");
		triadCharacter.add("5,9;consonant");

		triadNameTable = new DatabaseTable();
		triadNameTable.add("dim;diminished");
		triadNameTable.add("aug;augmented");
		triadNameTable.add("min;minor");
		triadNameTable.add("maj;major");
		triadNameTable.add("5;triad");
		triadNameTable.add("6;sixth chord");
		triadNameTable.add("6-4;six-four chord");

		tetraTable = new DatabaseTable();
		tetraTable.add("4,7,10;dom7");
		tetraTable.add("3,6,8;dom6-5");
		tetraTable.add("3,5,9;dom4-3");
		tetraTable.add("2,6,9;dom2");
		tetraTable.add("3,6,9;dim7,dim6-5,dim4-3,dim2");
		tetraTable.add("3,6,10;dim-min7");
		tetraTable.add("3,7,9;dim-min6-5");
		tetraTable.add("4,6,9;dim-min4-3");
		tetraTable.add("2,5,8;dim-min2");

		tetraCharacter = new DatabaseTable();
		tetraCharacter.add("4,7,10;dominant");
		tetraCharacter.add("3,6,8;dominant");
		tetraCharacter.add("3,5,9;dominant");
		tetraCharacter.add("2,6,9;dominant");
		tetraCharacter.add("3,6,9;diminished");
		tetraCharacter.add("3,6,10;diminished");
		tetraCharacter.add("3,7,9;diminished");
		tetraCharacter.add("4,6,9;diminished");
		tetraCharacter.add("2,5,8;diminished");

		tetraNameTable = new DatabaseTable();
		tetraNameTable.add("dom;dominant");
		tetraNameTable.add("dim;diminished");
		tetraNameTable.add("dim-min;diminished minor");
		tetraNameTable.add("7;seventh chord");
		tetraNameTable.add("6-5;six-five chord");
		tetraNameTable.add("4-3;four-three chord");
		tetraNameTable.add("2;second chord");
	}

	/* Factory methods */

	static Tone createToneFromName(String absoluteToneName) {
		String note;
		int octave;
		if (absoluteToneName.length() == 2) {
			if (Character.isDigit(absoluteToneName.charAt(1))) {
				note = absoluteToneName.substring(0,1);
				octave = Integer.parseInt(absoluteToneName.substring(1,2));
			} else {
				return null;
			}
		} else {
			if (absoluteToneName.length() == 3) {
				if (Character.isDigit(absoluteToneName.charAt(2))) {
					if ((absoluteToneName.charAt(1) == '-') && (absoluteToneName.charAt(2) == '1')) {
						note = absoluteToneName.substring(0,1);
						octave = Integer.parseInt(absoluteToneName.substring(1,3));
					} else {
						note = absoluteToneName.substring(0,2);
						octave = Integer.parseInt(absoluteToneName.substring(2,3));
					}
				} else {
					return null;
				}
			} else if (absoluteToneName.length() == 4) {
				if ((absoluteToneName.charAt(2) == '-') && (absoluteToneName.charAt(3) == '1')) {
					note = absoluteToneName.substring(0,2);
					octave = Integer.parseInt(absoluteToneName.substring(2,4));
				} else {
					return null;
				}
			} else {
				return null;
			}
		}

		boolean isValid = false;
		int relativePitch = 0;
		for (int i = 0; i < 12; i++) {	
			if (tonesNames.getValues(Integer.toString(i)).contains(note)) {
				isValid = true;
				relativePitch = i; 
				break;
			}
		}
		if (!isValid) {
			return null;
		}
		int number = (octave +1) * 12 + relativePitch;
		if ((number > 127) || (number < 0)) {
			return null;
		} else {
			return new Tone(number,Tone.DEFAULT_VOLUME);
		}
	}

	static Tone createToneFromRelativeName(String relativeName) {
		Tone tone = createToneFromName(relativeName + "3");
		if (tone == null) {
			return null;
		} else {
			return createToneFromName(relativeName + "3");
		}
	}

	static Harmony createHarmonyFromTones(String absoluteNames) {
		if (!checkAbsoluteNames(absoluteNames)) {
			
			return null;
		}

		String[] namesArray = absoluteNames.split(" ");
		int[] numberArray = new int[namesArray.length];
		for (int i = 0; i < namesArray.length; i++) {
			Tone tone = Chordanal.createToneFromName(namesArray[i]);
			if (tone == null) {
				return null;
			} else {
				numberArray[i] = tone.getNumber();
			}
		}
		return new Harmony(numberArray);
	}

	static Harmony createHarmonyFromRelativeTones(String relativeNames) {
		if (!checkRelativeNames(relativeNames)) {
			return null;
		}

		String absoluteNames = "";
		String[] namesArray = relativeNames.split(" ");

		for (String name : namesArray) {
			Tone tone = createToneFromRelativeName(name);
			if (tone == null) {
				return null;
			}
			absoluteNames += tone.getName() + " ";
		}
		Harmony harmony = createHarmonyFromTones(absoluteNames);
		if (harmony == null) {
			return null;
		} else {
			return createHarmonyFromTones(absoluteNames);
		}
	}

	static Key createKeyFromName(String name) {
		String[] keyParts = name.split(" ");

		return new Key(Integer.parseInt(tonesNames.getFirstInKey(keyParts[0])),Integer.parseInt(scaleNameTable.getFirstInKey(keyParts[1])));
	}

	/* Analyzing and naming Tones */

	static String getToneName(Tone tone) {
		if ((tone.getNumber() > 127) || (tone.getNumber() < 0)) {
			return "";
		}
		int	note = tone.getNumber() % 12;
		int	octave = tone.getNumber() / 12;
		return tonesNames.getFirstInValue(Integer.toString(note)) + (octave - 1);
	}

	static String getToneNameMapped(Tone tone) {
		if ((tone.getNumber() > 127) || (tone.getNumber() < 0)) {
			return "";
		}
		int	note = tone.getNumber() % 12;
		return tonesNames.getFirstInValue(Integer.toString(note));
	}

	/* Analyzing and naming Harmonies */

	static String getHarmonyToneNames(Harmony harmony) {
		String result = "";
		for (Tone tone : harmony.tones) {
			result += getToneName(tone) + " ";
		}
		return result;
	}

	static String getHarmonyToneNamesMapped(Harmony harmony) {
		ArrayList<String> mappedNamesWithDuplicates = harmony.tones.stream().map(Tone::getNameMapped).collect(Collectors.toCollection(ArrayList::new));

		String result = "";
		for (int i = 0; i < 12; i++) {
			for (String mappedName : mappedNamesWithDuplicates) {
				if (tonesNames.getFirstInValue(Integer.toString(i)).equals(mappedName)) {
					result += tonesNames.getFirstInValue(Integer.toString(i)) + " ";
					break;
				}
			}
		}

		return result;
	}

	static List<String> getHarmonyAbbreviationsRelative(Harmony harmony) {
		List<String> result = new ArrayList<>();

		if (harmony.tones.size() <= 2) {
			// interval or unisone

			return intervalTable.getValues(harmony.getIntervals()[0]);
		} else if ((harmony.tones.size() == 3) || (harmony.tones.size() == 4)) {
			// triad or tetrachord

			if (harmony.tones.size() == 3) {
				if (!triadTable.getValues(harmony.getIntervals()[0], harmony.getIntervals()[1]).isEmpty()) {
					result.addAll(triadTable.getValues(harmony.getIntervals()[0], harmony.getIntervals()[1]));
				}
			} else {
				if (!tetraTable.getValues(harmony.getIntervals()[0], harmony.getIntervals()[1], harmony.getIntervals()[2]).isEmpty()) {
					result.addAll(tetraTable.getValues(harmony.getIntervals()[0], harmony.getIntervals()[1], harmony.getIntervals()[2]));
				}
			}
			result.add(getHarmonyAbbreviationIntervals(harmony));

			return result;
		} else {
			// 5+ tones

			result.add(getHarmonyAbbreviationIntervals(harmony));

			return result;
		}
	}

	static String getHarmonyAbbreviationIntervals(Harmony harmony) {
		String result = "";
		for (int i = 0; i < harmony.getIntervals().length; i++) {
			if (i < harmony.getIntervals().length-1) {
				if (!intervalTable.getFirstInValue(harmony.getIntervals()[i]).equals("")) {
					result += intervalTable.getFirstInValue(harmony.getIntervals()[i]) + ",";
				} else {
					return "";
				}
			} else {
				if (!intervalTable.getFirstInValue(harmony.getIntervals()[i]).equals("")) {
					result += intervalTable.getFirstInValue(harmony.getIntervals()[i]);
				} else {
					return "";
				}
			}
		}
		return result;
	}

	static String getHarmonyAbbreviationRelative(Harmony harmony) {
		if (!getHarmonyAbbreviationsRelative(harmony).isEmpty()) {
			return getHarmonyAbbreviationsRelative(harmony).get(0);
		} else {
			return "";
		}
	}

	private static boolean isInterval(Harmony harmony) {
		return harmony.getIntervals().length == 1;
	}

	private static boolean isUnisone(Harmony harmony) {
		return harmony.getIntervals().length == 1 && getHarmonyAbbreviationRelative(harmony).equals("P1");
	}

	private static boolean isIntervalAbbreviation(Harmony harmony) {
		return harmony.getIntervals().length != 1 && getHarmonyAbbreviationRelative(harmony).contains(",");
	}

	private static boolean isStructureUnknown(Harmony harmony) {
		return getHarmonyAbbreviationRelative(harmony).equals("");
	}

	static String getHarmonyAbbreviation(Harmony harmony) {
		boolean interval = isInterval(harmony);
		boolean unisone = isUnisone(harmony);
		boolean intervalAbbreviation = isIntervalAbbreviation(harmony);
		boolean unknownStructure = isStructureUnknown(harmony);

		if (unknownStructure) {
			return "";
		}
		Tone rootTone = getRootTone(harmony);
		if (rootTone == null) {
			return "";
		}
		if (interval) {
			if (unisone) {
				return rootTone.getNameMapped() + " (" + getHarmonyAbbreviationRelative(harmony) + ")";
			} else {
				return rootTone.getNameMapped() + "->" + getHarmonyAbbreviationRelative(harmony);
			}
		} else if (intervalAbbreviation) {
			return rootTone.getNameMapped() + "->" + getHarmonyAbbreviationRelative(harmony);
		} else {
			return rootTone.getNameMapped() + getHarmonyAbbreviationRelative(harmony);
		}
	}

	static List<String> getHarmonyNamesRelative(Harmony harmony) {
		List<String> result, abbreviations;
		String type,harmonyStructure,typeName,harmonyStructureName;

		result = new ArrayList<>();
		abbreviations = getHarmonyAbbreviationsRelative(harmony);
		if (abbreviations.isEmpty()) {
			return new ArrayList<>();
		}

		if (harmony.tones.size() <= 2) {
			// interval or unisone

			for (String abbreviation : abbreviations) {
				if (abbreviation.equals("")) {
					return new ArrayList<>();
				}
				type = abbreviation.substring(0,abbreviation.length()-1);
				harmonyStructure = abbreviation.substring(abbreviation.length()-1);

				typeName = intervalNameTable.getFirstInValue(type);
				harmonyStructureName = intervalNameTable.getFirstInValue(harmonyStructure);		
				if ((typeName.equals("")) || (harmonyStructureName.equals(""))) {
					return new ArrayList<>();
				} else {
					result.add(typeName + " " + harmonyStructureName);
				}
			}
			return result;
		} else if ((harmony.tones.size() == 3 || harmony.tones.size() == 4)) {
			// triad or tetrachord

			boolean intervalAbbreviation = false;
			for (String abbreviation : abbreviations) {
				
				if (abbreviation.equals("")) {
					return new ArrayList<>();
				}
				if (abbreviation.contains(",")) {
					intervalAbbreviation = true;
				}
				if (intervalAbbreviation) {
					result.add(getHarmonyNameIntervals(harmony));
				} else {
					int i = 0;
					while (!Character.isDigit(abbreviation.charAt(i))) {
						i++;
					}

					type = abbreviation.substring(0,i);
					harmonyStructure = abbreviation.substring(i);
					if (harmony.tones.size() == 3) {
						typeName = triadNameTable.getFirstInValue(type);
						harmonyStructureName = triadNameTable.getFirstInValue(harmonyStructure);
					} else {
						typeName = tetraNameTable.getFirstInValue(type);
						harmonyStructureName = tetraNameTable.getFirstInValue(harmonyStructure);
					}
					if ((typeName.equals("")) || (harmonyStructureName.equals(""))) {
						return new ArrayList<>();
					} else {
						result.add(typeName + " " + harmonyStructureName);
					}
				}
			}
			return result;

		} else {
			// 5+ tones

			result.add(getHarmonyNameIntervals(harmony));
			return result;
		}
	}

	static String getHarmonyNameIntervals(Harmony harmony) {
		String type,harmonyStructure,typeName,harmonyStructureName;
		
		String abbreviation = getHarmonyAbbreviationIntervals(harmony);
		if (abbreviation.equals("")) {
			return "";
		}
		String result = "";
		for (int i = 0; i < harmony.getIntervals().length; i++) {
			if (i == harmony.getIntervals().length-1) {
				type = abbreviation.substring(0,abbreviation.length()-1);
				harmonyStructure = abbreviation.substring(abbreviation.length()-1);
			} else {
				type = abbreviation.substring(0,abbreviation.indexOf(",")-1);
				harmonyStructure = abbreviation.substring(abbreviation.indexOf(",")-1,abbreviation.indexOf(","));
			}
			typeName = intervalNameTable.getFirstInValue(type);
			harmonyStructureName = intervalNameTable.getFirstInValue(harmonyStructure);		
			if ((harmonyStructureName.equals("")) || (typeName.equals(""))) {
				return "";
			}
			if (i < harmony.getIntervals().length-1) {
				result += typeName + " " + harmonyStructureName + ",";
			} else {
				result += typeName + " " + harmonyStructureName;
			}
			abbreviation = abbreviation.substring(abbreviation.indexOf(",")+1);
		}
		return result;
	}

	static String getHarmonyNameRelative(Harmony harmony) {
		if (!getHarmonyNamesRelative(harmony).isEmpty()) {
			return getHarmonyNamesRelative(harmony).get(0);
		} else {
			return "";
		}
	}

	static String getHarmonyName(Harmony harmony) {
		boolean interval = isInterval(harmony);
		boolean unisone = isUnisone(harmony);
		boolean intervalAbbreviation = isIntervalAbbreviation(harmony);
		boolean unknownStructure = isStructureUnknown(harmony);

		if (unknownStructure) {
			return "";
		}
		Tone rootTone = getRootTone(harmony);
		if (rootTone == null) {
			return "";
		}
		if (interval) {
			if (unisone) {
				return rootTone.getNameMapped() + " (" + getHarmonyNameRelative(harmony) + ")";
			} else {
				return rootTone.getNameMapped() + "->" + getHarmonyNameRelative(harmony);
			}
		} else if (intervalAbbreviation) {
			return rootTone.getNameMapped() + "->" + getHarmonyNameRelative(harmony);
		} else {
			return rootTone.getNameMapped() + " " + getHarmonyNameRelative(harmony);
		}
	}

	static String getHarmonyCharacter(Harmony harmony) {
		if (harmony.tones.size() <= 2) {
			// interval or unisone

			return intervalCharacter.getFirstInValue(harmony.getIntervals()[0]);
		} else if (harmony.tones.size() == 3) {
			// triad
			
			return triadCharacter.getFirstInValue(harmony.getIntervals()[0],harmony.getIntervals()[1]);
		} else if (harmony.tones.size() == 4) {
			// tetrachord

			return tetraCharacter.getFirstInValue(harmony.getIntervals()[0],harmony.getIntervals()[1],harmony.getIntervals()[2]);
		} else {
			// 5+ tones

			return "";
		}
	}

	static Tone getRootTone(Harmony harmony) {
		String harmonyStructure;
		if (harmony.tones.size() <= 2) {
			// interval or unisone

			return harmony.tones.get(0);

		} else if ((harmony.tones.size() == 3) || (harmony.tones.size() == 4)) {
			// triad or tetrachord

			boolean intervalAbbreviation = false;
			boolean unknownStructure = false;

			harmonyStructure = getHarmonyAbbreviationRelative(harmony);

			if (harmonyStructure.equals("")) {
				unknownStructure = true;
			}

			if (harmonyStructure.contains(",")) {
				intervalAbbreviation = true;
			}
			if ((intervalAbbreviation) || (unknownStructure)) {
				return harmony.tones.get(0);
			}

			int i = 0;
			while (!Character.isDigit(harmonyStructure.charAt(i))) {
				i++;
			}
			harmonyStructure = harmonyStructure.substring(i);

			if (harmony.tones.size() == 3) {
				switch (harmonyStructure) {
					case "5":
						return harmony.tones.get(0);
					case "6":
						return harmony.tones.get(2);
					case "6-4":
						return harmony.tones.get(1);
				}
			} else {
				switch (harmonyStructure) {
					case "7":
						return harmony.tones.get(0);
					case "6-5":
						return harmony.tones.get(3);
					case "4-3":
						return harmony.tones.get(2);
					case "2":
						return harmony.tones.get(1);
				}
			}
			return null;

		} else {
			return harmony.tones.get(0);
		}
	}

	/* Analyzing and naming keys */

	static String getKeyAbbreviation(Key key) {
		return tonesNames.getFirstInValue(Integer.toString(key.root)) + scaleNameTable.getFirstInValue(Integer.toString(key.keyType));
	}

	static String getKeyName(Key key) {
		return tonesNames.getFirstInValue(Integer.toString(key.root)) + " " + scaleNameTable.getFirstInValue(Integer.toString(key.keyType));
	}

	static String getKeyScale(Key key) {
		int[] scale = key.getScale();
		String result = "";
		for (int tone : scale) {
			result += tonesNames.getFirstInValue(Integer.toString(tone)) + " ";
		}
		return result;
	}

	/* Private methods */

	private static boolean checkRelativeNames(String names) {
		String[] namesArray = names.split(" ");
		boolean validNoteName;
		for (String name : namesArray) {
			validNoteName = false;
			for (int j = 0; j < 12; j++) {
				if (name.equals(tonesNames.getFirstInValue(Integer.toString(j)))) {
					validNoteName = true;
				}
			}
			if (!validNoteName) {
				return false;
			}
		}
		return true;
	}

	private static boolean checkAbsoluteNames(String names) {
		String[] namesArray = names.split(" ");
		for (String name : namesArray) {
			if (Chordanal.createToneFromName(name) == null) {
				return false;
			}
		}
		return true;
	}
}