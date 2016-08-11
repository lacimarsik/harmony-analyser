package org.harmony_analyser.plugins.chordanal_plugins;

import org.harmony_analyser.plugins.*;
import org.harmony_analyser.chordanal.*;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Plugin for high-level audio analysis using chroma / chord transcription input, based on Chordanal model
 */

public class HarmanalPlugin extends AnalysisPlugin {
	private static final float AUDIBLE_THRESHOLD = (float) 0.07; // used to filter chroma activations that we consider not audible
	private static final int MAXIMUM_NUMBER_OF_CHORD_TONES = 4; // used to limit number of tones we work with in chord
	private static final int MAXIMAL_COMPLEXITY = 7; // used to assign a maximal value for 2 chords that have no common root

	static {
		inputFileExtensions = new ArrayList<>();
		inputFileExtensions.add("-chromas.txt");
		inputFileExtensions.add("-segmentation.txt");
	}

	/**
	 * Analyzes the song: converts chroma + segmentation information to harmony complexity descriptors
	 *
	 * @param inputFiles [List<String>]
	 *    chromaFile: name of the file containing chroma information (suffix: -chromas.txt)
	 *    segmentationFile: name of the file containing segmentation information (suffix: -segmentation.txt)
	 * @param outputFile [String] name of the file to write a report (recommended suffix: -report.txt)
	 */

	public String analyse(List<String> inputFiles, String outputFile) throws IOException, IncorrectInput {
		String result = "";

		checkInputFiles(inputFiles);
		String chromaFile = inputFiles.get(0);
		String segmentationFile = inputFiles.get(1);

		result += "Chroma file: " + chromaFile + "\n";
		result += "Segmentation file: " + segmentationFile + "\n";
		result += "Output: " + outputFile + "\n";

		List<String> chromaLinesList = Files.readAllLines(new File(chromaFile).toPath(), Charset.defaultCharset());
		List<String> segmentationLinesList = Files.readAllLines(new File(segmentationFile).toPath(), Charset.defaultCharset());
		List<Float> segmentationTimestampList = new ArrayList<>();

		// 1. Get timestamps from the segmentation file
		segmentationTimestampList.addAll(segmentationLinesList.stream().map(this::getTimestampFromLine).collect(Collectors.toList()));

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
		BufferedWriter out = new BufferedWriter(new FileWriter(outputFile));

		// 3. Iterate over chord progression, deriving chord and transition complexities
		for (int[] chord : chordProgression) {
			// sum number of all tones for final averages
			int numberTones = getNumberOfTones(chord);
			sumOfAllTones += numberTones;

			// get timestamp of this transition
			timestamp = timestampList.get(chordProgression.indexOf(chord));
			out.write(timestamp + ":\n");

			// create chords using Chordanal
			String currentChordTones = Chordanal.getStringOfTones(chord);
			String previousChordTones = Chordanal.getStringOfTones(previousChord);
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
				int transitionComplexity = Harmanal.getTransitionComplexity(harmony1, harmony2);
				if (transitionComplexity == -1) {
					out.write("transition: NO COMMON ROOTS (maximal complexity: " + MAXIMAL_COMPLEXITY + ")\n");
					transitionComplexity = MAXIMAL_COMPLEXITY;
				} else {
					List<String> transitionsFormatted = Harmanal.getTransitionsFormatted(harmony1, harmony2);
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
				int chordComplexity = Harmanal.getHarmonyComplexity(harmony2);
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

		String results = "Average Transition Complexity (ATC): " + atc + "\n" +
		"Average Harmony Complexity (ACH): " + ahc + "\n" +
		"Relative Transition Complexity (RTC): " + rtc + "\n";

		out.write(results);
		result += results;
		out.close();

		return result;
	}

	/* Private methods */

	// gets timestamp from the first word in the line, before ':'
	private float getTimestampFromLine(String line) {
		String stringTimestamp = line.substring(0, line.lastIndexOf(':'));
		return Float.parseFloat(stringTimestamp);
	}

	// averages multiple chromas from vector of their sum into one chroma
	private float[] averageChroma(float[] chromaSums, int countChromas) {
		float[] resultChroma = new float[12];
		for (int i = 0; i < chromaSums.length; i++) {
			resultChroma[i] = chromaSums[i] / countChromas;
		}
		return resultChroma;
	}

	// filters chroma using AUDIBLE_THRESHOLD, setting values below the threshold to 0
	private float[] filterChroma(float[] chroma) {
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
	private int[] createBinaryChord(float[] chroma) {
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
	private float[] getChromaFromLine(String line) throws IncorrectInput {
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
	private int getNumberOfTones(int[] chord) {
		int result = 0;
		for (int tonePresence : chord) {
			if (tonePresence == 1) {
				result++;
			}
		}
		return result;
	}

	// Shifts chroma a step semitones up
	private float[] shiftChroma(float[] chroma, int step) {
		float[] result = new float[12];
		if (step < 0) {
			step = 12 - step;
		}
		for (int i = 0; i < 12; i++) {
			result[i] = chroma[(i + step) % 12];
		}
		return result;
	}
}
