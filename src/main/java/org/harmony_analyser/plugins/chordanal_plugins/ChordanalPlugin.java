package org.harmony_analyser.plugins.chordanal_plugins;

import org.harmony_analyser.application.services.AudioAnalysisHelper;
import org.harmony_analyser.chordanal.*;
import org.harmony_analyser.chromanal.Chroma;
import org.harmony_analyser.plugins.LineChartPlugin;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Abstract class for chordanal plugins
 */

@SuppressWarnings("SameParameterValue")

abstract class ChordanalPlugin extends LineChartPlugin {
	private static float audibleThreshold = (float) 0.07; // used to filter chroma activations that we consider not audible
	private static int maximumNumberOfChordTones = 4; // used to limit number of tones we work with in chord
	private static int maximalComplexity = 7; // used to assign a maximal value for 2 chords that have no common root

	/**
	 * Analyzes the song: converts chroma + segmentation information to harmony complexity descriptors
	 *
	 * @param inputFileWav [String] name of the WAV audio file
	 *    These additional files are expected in the folder
	 *    - chromaFile: name of the file containing chroma information (suffix: -chromas.txt)
	 *    - segmentationFile: name of the file containing segmentation information (suffix: -segmentation.txt)
	 */

	public String analyse(String inputFileWav, boolean force, boolean verbose) throws IOException, IncorrectInputException, OutputAlreadyExists, Chroma.WrongChromaSize {
		String result = super.analyse(inputFileWav, force, verbose);
		String outputFile = inputFileWav + outputFileSuffix + ".txt";
		String outputFileVerbose = inputFileWav + outputFileSuffix + ".txt";
		List<String> inputFiles = new ArrayList<>();
		for (String suffix : inputFileSuffixes) {
			String inputFileName = inputFileWav + suffix + inputFileExtension;
			inputFiles.add(inputFileName);
		}

		List<String> chromaLinesList = Files.readAllLines(new File(inputFiles.get(0)).toPath(), Charset.defaultCharset());
		List<String> segmentationLinesList = Files.readAllLines(new File(inputFiles.get(1)).toPath(), Charset.defaultCharset());
		List<Float> segmentationTimestampList = new ArrayList<>();

		// 1. Get timestamps from the segmentation file
		segmentationTimestampList.addAll(segmentationLinesList.stream().map(AudioAnalysisHelper::getTimestampFromLine).collect(Collectors.toList()));

		float chromaTimestamp;
		float[] chroma;
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
			chromaTimestamp = AudioAnalysisHelper.getTimestampFromLine(line);

			if (chromaTimestamp > segmentationTimestamp) {
				// Go to the next segmentation timestamp
				segmentationIndex++;
				if (segmentationIndex > segmentationTimestampList.size()-1) {
					break;
				}
				timestampList.add(segmentationTimestamp);
				segmentationTimestamp = segmentationTimestampList.get(segmentationIndex);

				// Average chromas in the previous block, use AUDIBLE_THRESHOLD to filter non-audible activations
				chromaVector = AudioAnalysisHelper.filterChroma(AudioAnalysisHelper.averageChroma(chromaSums, countChromasForAveraging), audibleThreshold);
				Arrays.fill(chromaSums, (float) 0);
				countChromasForAveraging = 0;

				// Create a binary chord representation from chroma
				// XXX: Take MAXIMUM_NUMBER_OF_CHORD_TONES tones with the maximum activation
				harmony = AudioAnalysisHelper.createBinaryChord(chromaVector, maximumNumberOfChordTones);

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
		BufferedWriter outVerbose = new BufferedWriter(new FileWriter(outputFileVerbose));

		// 3. Iterate over chord progression, deriving chord and transition complexities
		for (int[] chord : chordProgression) {
			// sum number of all tones for final averages
			int numberTones = AudioAnalysisHelper.getNumberOfTones(chord);
			sumOfAllTones += numberTones;

			// get timestamp of this transition
			timestamp = timestampList.get(chordProgression.indexOf(chord));
			if (verbose) {
				outVerbose.write(timestamp + ":\n");
			}

			// create chords using Chordanal
			String currentChordTones = Chordanal.getStringOfTones(chord);
			String previousChordTones = Chordanal.getStringOfTones(previousChord);
			Harmony harmony1 = Chordanal.createHarmonyFromRelativeTones(previousChordTones);
			Harmony harmony2 = Chordanal.createHarmonyFromRelativeTones(currentChordTones);

			if (harmony1.equals(Harmony.EMPTY_HARMONY) || harmony2.equals(Harmony.EMPTY_HARMONY)) {
				if (verbose) {
					outVerbose.write("SKIP (one or both chords were not assigned)\n\n");
				}
			} else {
				// Print chord names to output
				String harmonyName1 = Chordanal.getHarmonyName(harmony1);
				String harmonyName2 = Chordanal.getHarmonyName(harmony2);

				if (verbose) {
					outVerbose.write(previousChordTones + "-> " + currentChordTones + "\n");
					outVerbose.write(harmonyName1 + "-> " + harmonyName2 + "\n");
				}

				// Get transition complexity using Harmanal
				int transitionComplexity = Harmanal.getTransitionComplexity(harmony1, harmony2);
				if (transitionComplexity == -1) {
					if (verbose) {
						outVerbose.write("transition: NO COMMON ROOTS (maximal complexity: " + maximalComplexity + ")\n");
					}
					transitionComplexity = maximalComplexity;
				} else {
					List<String> transitionsFormatted = Harmanal.getTransitionsFormatted(harmony1, harmony2);
					String transitionFormatted;
					if (transitionsFormatted.size() == 0) {
						transitionFormatted = "(not found)";
					} else {
						transitionFormatted = transitionsFormatted.get(0);
					}
					if (verbose) {
						outVerbose.write("transition: " + transitionFormatted + "\n");
					}
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
				if (verbose) {
					outVerbose.write("transition complexity: " + transitionComplexity + "\n\n");
				}
				result += this.getTransitionOutput(timestamp, transitionComplexity);
			}

			// Set previous chord
			previousChord = chord.clone();
		}

		float hc = (float) sumTransitionComplexities  / (float) transitionComplexityList.size();
		float acc = (float) sumChordComplexities  / (float) chordComplexityList.size();
		float rtd = (float) sumTransitionComplexities / (float) sumOfAllTones;

		result += this.getFinalResult(hc, acc, rtd);
		out.write(result);

		out.close();
		outVerbose.close();

		return result;
	}

	protected void setParameters() {
		audibleThreshold = parameters.get("audibleThreshold");
		maximumNumberOfChordTones = Math.round(parameters.get("maximumNumberOfChordTones"));
		maximalComplexity = Math.round(parameters.get("maximalComplexity"));
	}

	protected abstract String getTransitionOutput(float timestamp, int transitionComplexity);

	protected abstract String getFinalResult(float hc, float acc, float rtd);
}