package harmanal;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

// TODO: Complete Harmony complexity (Complexity of a single tone and single tone + added tone)
// TODO: Complete Transition complexity (Complexity of non-diatonic modulations)
// TODO: Create AudioAnalyser - class for analysis of audio using Vamp plugins + Chordanal + Harmanal (Harmanal plugins)
// TODO: Seperate Model (Chordanal + Harmanal) from Analysis (Harmony Analyser)

/**
 * Class to handle all functional complexity and transition complexity
 * 
 * version 1.0
 */

@SuppressWarnings("SameParameterValue")

class Harmanal {

	/* Roots finding */

	/**
	 * Gets function roots table for a harmony
	 */

	static DatabaseTable getRoots(Harmony harmony) {
		Key key;
		DatabaseTable result = new DatabaseTable();

		// try all Major keys
		for (int i = 0; i < 12; i++) {
			key = new Key(i,Chordanal.MAJOR);
			result.addAll(getRoots(harmony,key));
		}

		// try all Minor keys
		for (int i = 0; i < 12; i++) {
			key = new Key(i,Chordanal.MINOR);
			result.addAll(getRoots(harmony,key));
		}
		return result;
	}

	/**
	 * Gets function roots table for a harmony for specific key
	 */

	private static DatabaseTable getRoots(Harmony harmony, Key key) {
		DatabaseTable result = new DatabaseTable();
		DatabaseTable rows;

		if ((rows = getRoots(harmony,key.getTonic(),Chordanal.TONIC,key)) != null) {
			result.addAll(rows);
		}
		if ((rows = getRoots(harmony,key.getSubdominant(),Chordanal.SUBDOMINANT,key)) != null) {
			result.addAll(rows);
		}
		if ((rows = getRoots(harmony,key.getDominant(),Chordanal.DOMINANT,key)) != null) {
			result.addAll(rows);
		}
		return result;
	}

	/**
	 * Gets function roots table for a harmony for specific key and function
	 */

	private static DatabaseTable getRoots(Harmony harmony, Harmony function, int functionSign, Key key) {
		DatabaseTable result = new DatabaseTable();
		Harmony common;

		common = harmony.getCommonTones(function);

		if (common.tones.size() == 3) {
			result.add(Chordanal.getKeyName(key) + "," + Chordanal.functionNameTable.getFirstInValue(Chordanal.functionTable.getFirstInValue(Integer.toString(functionSign))) + "," + common.getToneNamesMapped() + ";" + getHarmonyComplexity(harmony, Chordanal.createHarmonyFromRelativeTones(common.getToneNamesMapped()),key));
		} else if (common.tones.size() == 2) {
			Tone rootTone = Chordanal.getRootTone(function);
			if (rootTone == null) {
				return null;
			}
			if ((common.tones.get(0).getNumberMapped() == rootTone.getNumberMapped()) || (common.tones.get(1).getNumberMapped() == rootTone.getNumberMapped())) {
				result.add(Chordanal.getKeyName(key) + "," + Chordanal.functionNameTable.getFirstInValue(Chordanal.functionTable.getFirstInValue(Integer.toString(functionSign))) + "," + common.getToneNamesMapped() + ";" + getHarmonyComplexity(harmony, Chordanal.createHarmonyFromRelativeTones(common.getToneNamesMapped()),key));
			}
		}
		return result;
	}

	/**
	 * Gets function roots table for a harmony, sorted by distance and formatted
	 */

	static List<String> getRootsFormatted(Harmony harmony) {
		List<String> result = new ArrayList<>();
		DatabaseTable roots = getRoots(harmony).sortByValueByFirstNumeric();

		if (!roots.isEmpty()) {
			for (List<String> key : roots.getAllKeys()) {
				result.addAll(roots.getValues(key.get(0), key.get(1)).stream()
					.map(value -> key.get(0) + " (" + key.get(1) + ")\nroot: " + key.get(2) + " steps: " + value)
					.collect(Collectors.toList()));
			}
		}
		return result;
	}

	/* Harmony derivation and complexity */

	/**
	 * Gets the derivation from the root to the harmony in a given key
	 */

	static List<String> getHarmonyDerivation(Harmony harmony, Harmony root, Key key) {
		return getHarmonyDerivation(root, harmony.subtractTones(root), key);
	}

	/**
	 * Gets the derivation from the root to the harmony in a given key, specifying the order of adding tones
	 */

	private static List<String> getHarmonyDerivation(Harmony root, List<Tone> added, Key key) {
		List<String> result = new ArrayList<>();
		Harmony phraseForm = Chordanal.createHarmonyFromRelativeTones(root.getToneNamesMapped());
		if (phraseForm == null) {
			return null;
		}

		result.add(root.getToneNamesMapped());

		boolean special;
		Tone rootCompletionTone;
		for (Tone tone : added) {
			if (key.getScaleHarmony().containsMapped(tone)) {
				// ADD operator step

				special = false;
				if ((rootCompletionTone = getRootCompletionTone(root, key)) != null) {
					if ((root.tones.size() == 2) && (tone.getNumberMapped() == rootCompletionTone.getNumberMapped())) {
						special = true;
					}
				}
				if (special) {
					// special situation - adding tone which belongs to root - for free

					phraseForm.tones.add(tone);
				} else {
					phraseForm.tones.add(tone);
					result.add(phraseForm.getToneNamesMapped());
				}
			} else {
				// ADD + CHROMATIZE operators steps

				// ADD
				Tone diatonicTone = new Tone(tone.getNumber()-1);
				special = false;
				for (Tone rootTone : root.tones) {
					if (diatonicTone.getNumberMapped() == rootTone.getNumberMapped()) {
						special = true;
					}
				}
				if ((rootCompletionTone = getRootCompletionTone(root, key)) != null) {
					if (diatonicTone.getNumberMapped() == rootCompletionTone.getNumberMapped()) {
						special = true;
					}
				}
				if (special) {
					// special situation - one semitone down is one of the tones of root - need to chromatize from up to down
					
					diatonicTone.chromatizeUp();
					diatonicTone.chromatizeUp();
				}
				
				phraseForm.tones.add(diatonicTone);
				result.add(phraseForm.getToneNamesMapped());

				// CHROMATIZE

				if (special) {
					phraseForm.tones.get(phraseForm.tones.size()-1).chromatizeDown();
				} else {
					phraseForm.tones.get(phraseForm.tones.size()-1).chromatizeUp();
				}
				result.add(phraseForm.getToneNamesMapped());
			}
		}
		return result;
	}

	/**
	 * Gets list of all possible derivations from the root to the harmony in a given key
	 */

	private static List<List<String>> getHarmonyDerivations(Harmony harmony, Harmony root, Key key) {
		List<List<String>> result = new ArrayList<>();
		List<Tone> blank = new ArrayList<>();
		List<Tone> added = harmony.subtractTones(root);
		List<List<Tone>> permutations = new ArrayList<>();

		permutateListOfTones(blank, added, permutations);

		result.addAll(permutations.stream().map(list -> getHarmonyDerivation(root, list, key)).collect(Collectors.toList()));
		return result;
	}

	/**
	 * Gets the harmony complexity of the harmony from the root in a given key
	 */

	static int getHarmonyComplexity(Harmony harmony, Harmony root, Key key) {
		return getHarmonyDerivation(harmony,root,key).size()-1;
	}

	/**
	 * Gets the harmony complexity for the harmony or -1 if no roots were found
	 */

	static int getHarmonyComplexity(Harmony harmony) {
		DatabaseTable roots = getRoots(harmony).sortByValueByFirstNumeric();
		if (roots.isEmpty()) {
			return -1;
		} else {
			return Integer.parseInt(roots.getAllValues().get(0).get(0));
		}
	}

	/* Transition complexity */

	/**
	 * Gets the common roots table for two harmonies
	 */

	static DatabaseTable getCommonRoots(Harmony harmony1, Harmony harmony2) {
		DatabaseTable roots1 = getRoots(harmony1).sortByValueByFirstNumeric();
		DatabaseTable roots2 = getRoots(harmony2).sortByValueByFirstNumeric();

		DatabaseTable rootsTemporary = roots1.naturalJoinByFirstAndSecond(roots2);

		List<String> commonRootsRows = rootsTemporary.getAll();
		DatabaseTable result = new DatabaseTable();
		for (String row : commonRootsRows) {
			String[] keyValue = row.split(";");
			String[] key = keyValue[0].split(",");
			String[] value = keyValue[1].split(",");
			Harmony firstOption = Chordanal.createHarmonyFromRelativeTones(key[2]);
			Harmony secondOption = Chordanal.createHarmonyFromRelativeTones(key[3]);
			if (firstOption == null || secondOption == null) {
				return null;
			}
			if (firstOption.containsMapped(secondOption)) {
				result.add(key[0] + "," + key[1] + "," + key[3] + ";" + value[0] + "," + value[1]);
			} else if (secondOption.containsMapped(firstOption)) {
				result.add(key[0] + "," + key[1] + "," + key[2] + ";" + value[0] + "," + value[1]);
			}
		}

		return result;
	}

	/**
	 * Gets the common roots table for two harmonies grouped by the key 
	 */

	static DatabaseTable getCommonRootsByKey(Harmony harmony1, Harmony harmony2) {
		DatabaseTable roots1 = getRoots(harmony1).sortByValueByFirstNumeric();
		DatabaseTable roots2 = getRoots(harmony2).sortByValueByFirstNumeric();

		return roots1.naturalJoinByFirst(roots2);
	}

	/**
	 * Gets the common ancestors table for two harmonies
	 */

	static DatabaseTable getCommonAncestors(Harmony harmony1, Harmony harmony2) {
		DatabaseTable commonRoots = getCommonRoots(harmony1, harmony2);
		if (commonRoots == null) {
			return null;
		}
		List<String> commonRootsRows = commonRoots.getAll();
		if (commonRootsRows == null) {
			return null;
		}

		DatabaseTable result = new DatabaseTable();

		for (String row : commonRootsRows) {
			String[] keyValue = row.split(";");
			String[] key = keyValue[0].split(",");

			Harmony root = Chordanal.createHarmonyFromRelativeTones(key[2]);
			Key commonKey = Chordanal.createKeyFromName(key[0]);

			List<String> rightDerivation1 = null, rightDerivation2 = null;
			int closestAncestor = 0;
			for (List<String> derivation1 : getHarmonyDerivations(harmony1, root, commonKey)) {
				for (List<String> derivation2 : getHarmonyDerivations(harmony2, root, commonKey)) {
					int commonMovesNumber;
					if (derivation1.size() < derivation2.size()) {
						commonMovesNumber = derivation1.size();
					} else {
						commonMovesNumber = derivation2.size();
					}
					int indexOfCommonAncestor = -1;
					for (int i = 0; i < commonMovesNumber; i++) {
						if (!derivation1.get(i).equals(derivation2.get(i))) {
							break;
						}
						indexOfCommonAncestor++;
					}
					if (indexOfCommonAncestor >= closestAncestor) {
						closestAncestor = indexOfCommonAncestor;
						rightDerivation1 = derivation1;
						rightDerivation2 = derivation2;
					}
				}
			}
			if (rightDerivation1 == null) {
				return null;
			}
			result.add(key[0] + "," + key[1] + "," + rightDerivation1.get(closestAncestor) + ";" + (rightDerivation1.size() - closestAncestor -1) + "," + (rightDerivation2.size() - closestAncestor -1));
		}
		return result;
	}

	/**
	 * Gets the transitions table for two harmonies
	 */

	static DatabaseTable getTransitions(Harmony harmony1, Harmony harmony2) {
		DatabaseTable result = new DatabaseTable();

		// Transition between T/S/D
		DatabaseTable commonKeys = getCommonRootsByKey(harmony1, harmony2);
		List<List<String>> keys1 = commonKeys.getAllKeys();
		List<List<String>> values1 = commonKeys.getAllValues();
		for (int i = 0; i < keys1.size(); i++) {
			result.add(keys1.get(i).get(0) + "," + keys1.get(i).get(1) + "," + Integer.parseInt(values1.get(i).get(0)) + "," + keys1.get(i).get(2) + "," + Integer.parseInt(values1.get(i).get(1)) + ";" + (Integer.parseInt(values1.get(i).get(0)) + Integer.parseInt(values1.get(i).get(1))));
		}

		// Transition amongst T/S/D
		DatabaseTable commonAncestors = getCommonAncestors(harmony1, harmony2);
		if (commonAncestors == null) {
			return null;
		}
		List<List<String>> keys2 = commonAncestors.getAllKeys();
		List<List<String>> values2 = commonAncestors.getAllValues();
		for (int i = 0; i < keys2.size(); i++) {
			result.add(keys2.get(i).get(0) + "," + keys2.get(i).get(1) + "," + Integer.parseInt(values2.get(i).get(0)) + "," + keys2.get(i).get(1) + "," + Integer.parseInt(values2.get(i).get(1)) + ";" + (Integer.parseInt(values2.get(i).get(0)) + Integer.parseInt(values2.get(i).get(1))));
		}

		return result;
	}

	/**
	 * Gets the transitions table for two harmonies, sorted by distance and formatted
	 */

	static List<String> getTransitionsFormatted(Harmony harmony1, Harmony harmony2) {
		List<String> result = new ArrayList<>();

		DatabaseTable unsortedTransitions = getTransitions(harmony1,harmony2);
		if (unsortedTransitions == null) {
			return null;
		}
		DatabaseTable transitions = unsortedTransitions.sortByValueByFirstNumeric();
		List<List<String>> keys = transitions.getAllKeys();
		List<List<String>> values = transitions.getAllValues();

		for (int i = 0; i < keys.size(); i++) {
			result.add(keys.get(i).get(0) + ": " + keys.get(i).get(1) + "->" + keys.get(i).get(3) + " steps: " + values.get(i).get(0));
		}
		return result;
	}

	/**
	 * Gets the transition complexity for two harmonies or -1 if no common keys were found
	 */

	static int getTransitionComplexity(Harmony harmony1, Harmony harmony2) {
		DatabaseTable unsortedTransitions = getTransitions(harmony1,harmony2);
		if (unsortedTransitions == null) {
			return -1;
		}
		DatabaseTable transitions = unsortedTransitions.sortByValueByFirstNumeric();

		if (transitions.isEmpty()) {
			return -1;
		} else {
			return Integer.parseInt(transitions.getAllValues().get(0).get(0));
		}
	}

	/* Private methods */

	/**
	 * Fills all permutation from startList (empty list) to endList (list of Tones) into result
	 */

	private static void permutateListOfTones(List<Tone> startList, List<Tone> endList, List<List<Tone>> result) {
		if (endList.size() <= 1) {
			List<Tone> permResult = new ArrayList<>();
			permResult.addAll(startList);
			permResult.addAll(endList);
			result.add(permResult);
		} else {
			for (int i = 0; i < endList.size(); i++) {
				List<Tone> newEndList = new ArrayList<>();
				for ( int j = 0; j < i; j++ ) newEndList.add(endList.get(j));
				for ( int j = i+1; j < endList.size(); j++ ) newEndList.add(endList.get(j));

				List<Tone> newStartList = new ArrayList<>();
				newStartList.addAll(startList);
				newStartList.add(endList.get(i));

				permutateListOfTones(newStartList, newEndList, result);
			}
		}
	}

	/**
	 * Gets the remaining tone of the function root if it was not complete
	 */

	private static Tone getRootCompletionTone(Harmony root, Key key) {
		if (root.tones.size() != 2) {
			return null;
		}
		Tone result = null;
		if (key.getTonic().containsMapped(root)) {
			for (Tone functionTone : key.getTonic().tones) {
				if (!root.containsMapped(functionTone)) {
					result = functionTone;
				}
			}
		}
		if (key.getSubdominant().containsMapped(root)) {
			for (Tone functionTone : key.getSubdominant().tones) {
				if (!root.containsMapped(functionTone)) {
					result = functionTone;
				}
			}
		}
		if (key.getDominant().containsMapped(root)) {
			for (Tone functionTone : key.getDominant().tones) {
				if (!root.containsMapped(functionTone)) {
					result = functionTone;
				}
			}
		}
		return result;
	}

	/* Audio Analysis */
	// TODO: Move to a service

	private static final float AUDIBLE_THRESHOLD = (float) 0.07; // used to filter chroma activations that we consider not audible
	private static final int MAXIMUM_NUMBER_OF_CHORD_TONES = 4; // used to limit number of tones we work with in chord
	private static final int MAXIMAL_COMPLEXITY = 7; // used to assign a maximal value for 2 chords that have no common root

	// gets timestamp from the first word in the line, before ':'
	private static float getTimestampFromLine(String line) {
		String stringTimestamp = line.substring(0, line.lastIndexOf(':'));
		return Float.parseFloat(stringTimestamp);
	}

	// averages multiple chromas from vector of their sum into one chroma
	private static float[] averageChroma(float[] chromaSums, int countChromas) {
		float[] resultChroma = new float[12];
		for (int i = 0; i < chromaSums.length; i++) {
			resultChroma[i] = chromaSums[i] / countChromas;
		}
		return resultChroma;
	}

	// filters chroma using AUDIBLE_THRESHOLD, setting values below the threshold to 0
	private static float[] filterChroma(float[] chroma) {
		float[] resultChroma = new float[12];
		for (int i = 0; i < chroma.length; i++) {
			if (chroma[i] < AUDIBLE_THRESHOLD) {
				resultChroma[i] = 0;
			} else {
				resultChroma[i] = chroma[i];
			}
		}
		return resultChroma;
	}

	// Creates binary representation of a chord, taking MAXIMUM_NUMBER_OF_CHORD_TONES tones with the maximum activation from chroma
	private static int[] createBinaryChord(float[] chroma) {
		int[] result = new int[12];
		Arrays.fill(result, 0);
		float max;
		int id;
		for (int g = 0; g < MAXIMUM_NUMBER_OF_CHORD_TONES; g++) {
			max = 0;
			id = 0;
			for (int i = 0; i < chroma.length; i++) {
				if (chroma[i] > max) {
					id = i;
					max = chroma[i];
				}
			}
			if (chroma[id] > 0) {
				result[id] = 1;
			}
			chroma[id] = (float) 0;
		}
		return result;
	}

	// Read chroma information from the line of String
	private static float[] getChromaFromLine(String line) throws IncorrectInput {
		float[] result = new float[12];
		Scanner sc = new Scanner(line);
		sc.next(); // skip timestamp
		for (int i = 0; i < 12; i++) {
			if (sc.hasNextFloat()) {
				result[i] = sc.nextFloat();
			} else {
				throw new IncorrectInput("Chroma information is invalid.");
			}
		}
		return result;
	}

	// Get number of tones from the binary representation of a chord
	private static int getNumberOfTones(int[] chord) {
		int result = 0;
		for (int tonePresence : chord) {
			if (tonePresence == 1) {
				result++;
			}
		}
		return result;
	}

	// Get String of tone names from binary chord representation, using Chordanal
	// TODO: Move to Chordanal
	private static String getStringOfTones(int[] chord) {
		String result = "";
		for (int i = 0; i < chord.length; i++) {
			if (chord[i] == 1) {
				result += Chordanal.tonesNames.getFirstInValue(Integer.toString(i)) + " ";
			}
		}
		return result;
	}

	// Shifts chroma a step semitones up
	private static float[] shiftChroma(float[] chroma, int step) {
		float[] result = new float[12];
		if (step < 0) {
			step = 12 - step;
		}
		for (int i = 0; i < 12; i++) {
			result[i] = chroma[(i + step) % 12];
		}
		return result;
	}

	/**
	 * Analyzes the song
	 *
	 * @param chromaFile [String] name of the file containing chroma information
	 * @param segmentationFile [String] name of the file containing segmentation information
	 * @param reportFile [String] name of the file to write a report
	 */

	static void analyzeSong(String chromaFile, String segmentationFile, String reportFile) throws IOException, IncorrectInput {
		List<String> chromaLinesList = Files.readAllLines(new File(chromaFile).toPath(), Charset.defaultCharset());
		List<String> segmentationLinesList = Files.readAllLines(new File(segmentationFile).toPath(), Charset.defaultCharset());
		List<Float> segmentationTimestampList = new ArrayList<>();

		// 1. Get timestamps from the segmentation file
		segmentationTimestampList.addAll(segmentationLinesList.stream().map(Harmanal::getTimestampFromLine).collect(Collectors.toList()));

		float chromaTimestamp;
		float[] chroma ;
		float[] chromaSums = new float[12];
		Arrays.fill(chromaSums, (float) 0);
		float[] chromaVector;
		int[] harmony;
		List<int[]> chordProgression = new ArrayList<>();
		List<Float> timestampList = new ArrayList<>();
		int countChromasForAveraging = 0;
		int segmentationIndex = 0;
		float segmentationTimestamp;
		segmentationTimestamp = segmentationTimestampList.get(0);

		// 2. Iterate over chromas, transforming them into chord progression
		for (String line : chromaLinesList) {
			chromaTimestamp = getTimestampFromLine(line);

			if (chromaTimestamp > segmentationTimestamp) {
				// Go to the next segmentation timestamp
				segmentationIndex++;
				if (segmentationIndex > segmentationTimestampList.size()-1) {
					break;
				}
				timestampList.add(segmentationTimestamp);
				segmentationTimestamp = segmentationTimestampList.get(segmentationIndex);

				// Average chromas in the previous block, use AUDIBLE_THRESHOLD to filter non-audible activations
				chromaVector = filterChroma(averageChroma(chromaSums, countChromasForAveraging));
				Arrays.fill(chromaSums, (float) 0);
				countChromasForAveraging = 0;

				// Create a binary chord representation from chroma
				// XXX: Take MAXIMUM_NUMBER_OF_CHORD_TONES tones with the maximum activation
				harmony = createBinaryChord(chromaVector);

				// Add created harmony to the list of chord progressions
				chordProgression.add(harmony);
			}

			// Get chroma from the current line
			chroma = getChromaFromLine(line);

			// Shift chroma for proper alignment for analysis
			// XXX: chromas from NNLS Chroma Vamp plugin start from A, chroma for Chordanal are starting from C)
			chroma = shiftChroma(chroma, 3);

			// Add values into array for averages
			for (int i = 0; i < chromaSums.length; i++) {
				chromaSums[i] = chromaSums[i] + chroma[i];
				countChromasForAveraging++;
			}
		}

		int[] previousChord = new int[12];
		List<Integer> transitionComplexityList = new ArrayList<>();
		List<Integer> chordComplexityList = new ArrayList<>();
		int sumTransitionComplexities = 0;
		int sumChordComplexities = 0;
		int maximalTransitionComplexity = 0;
		int maximalChordComplexity = 0;
		int sumOfAllTones = 0;
		float timestamp;
		BufferedWriter out = new BufferedWriter(new FileWriter(reportFile));

		// 3. Iterate over chord progression, deriving chord and transition complexities
		for (int[] chord : chordProgression) {
			// sum number of all tones for final averages
			int numberTones = getNumberOfTones(chord);
			sumOfAllTones += numberTones;

			// get timestamp of this transition
			timestamp = timestampList.get(chordProgression.indexOf(chord));
			out.write(timestamp + ":\n");

			// create chords using Chordanal
			String currentChordTones = getStringOfTones(chord);
			String previousChordTones = getStringOfTones(previousChord);
			Harmony harmony1 = Chordanal.createHarmonyFromRelativeTones(previousChordTones);
			Harmony harmony2 = Chordanal.createHarmonyFromRelativeTones(currentChordTones);

			if ((harmony1 == null) || (harmony2 == null)) {
				out.write("SKIP (one or both chords were not assigned)\n\n");
			} else {
				// Print chord names to output
				String harmonyName1 = Chordanal.getHarmonyName(harmony1);
				String harmonyName2 = Chordanal.getHarmonyName(harmony2);

				out.write(previousChordTones + "-> " + currentChordTones + "\n");
				out.write(harmonyName1 + "-> " + harmonyName2 + "\n");

				// Get transition complexity using Harmanal
				int transitionComplexity = getTransitionComplexity(harmony1, harmony2);
				if (transitionComplexity == -1) {
					out.write("transition: NO COMMON ROOTS (maximal complexity: " + MAXIMAL_COMPLEXITY + ")\n");
					transitionComplexity = MAXIMAL_COMPLEXITY;
				} else {
					List<String> transitionsFormatted = getTransitionsFormatted(harmony1, harmony2);
					String transitionFormatted;
					if (transitionsFormatted == null) {
						transitionFormatted = "(not found)";
					} else {
						transitionFormatted = transitionsFormatted.get(0);
					}
					out.write("transition: " + transitionFormatted + "\n");
				}
				transitionComplexityList.add(transitionComplexity);

				// Get and store chord complexity using Harmanal
				int chordComplexity = getHarmonyComplexity(harmony2);
				chordComplexityList.add(chordComplexity);

				// Sum up complexities for averages
				sumTransitionComplexities += transitionComplexity;
				sumChordComplexities += chordComplexity;

				// Assign maximums
				if (transitionComplexity > maximalTransitionComplexity) {
					maximalTransitionComplexity = transitionComplexity;
				}
				if (chordComplexity > maximalChordComplexity) {
					maximalChordComplexity = chordComplexity;
				}
				out.write("transition complexity: " + transitionComplexity + "\n\n");
			}

			// Set previous chord
			previousChord = chord.clone();
		}

		float atc = (float) sumTransitionComplexities  / (float) transitionComplexityList.size();
		float ahc = (float) sumChordComplexities  / (float) chordComplexityList.size();
		float rtc = (float) sumTransitionComplexities / (float) sumOfAllTones;

		out.write("Average Transition Complexity (ATC): " + atc + "\n");
		out.write("Average Harmony Complexity (ACH): " + ahc + "\n");
		out.write("Relative Transition Complexity (RTC): " + rtc + "\n");
		out.close();
	}

	static class IncorrectInput extends Exception {
		IncorrectInput(String message) {
			super(message);
		}
	}
}
