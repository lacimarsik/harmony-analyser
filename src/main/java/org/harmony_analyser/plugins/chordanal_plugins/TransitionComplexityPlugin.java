package org.harmony_analyser.plugins.chordanal_plugins;

import org.harmony_analyser.application.services.AudioAnalyser;
import org.harmony_analyser.application.services.AudioAnalysisHelper;
import org.harmony_analyser.application.visualizations.VisualizationData;
import org.harmony_analyser.chromanal.Chroma;
import org.harmony_analyser.plugins.*;
import org.harmony_analyser.chordanal.*;
import org.vamp_plugins.PluginLoader;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Plugin for high-level audio analysis using chroma / chord transcription input, based on Chordanal model
 */

/*
 * TransitionComplexityPlugin
 *
 * - requires: chroma, segmentation
 * - Averages chroma in each segment
 * - Selects the tones with the biggest activation, based on audibleThreshold
 * - Names the chord using Chordanal
 * - derives transition complexities for each tuple of subsequent chords
 * - calculates average transition complexity, average chord complexity, and relative transition complexity
 *
 * parameters
 * - threshold for audible tones (< 0.05 yields 4+ tones in each chord, > 0.1 yields to 2 and less tones in each chord)
 * -- preferred: 0.07
 * - maximum number of chord tones - used to simplify computation which is exponential to the number of chord tones
 * -- preferred: 4.0
 * - maximal complexity - is assigned when transition complexity cannot be found, as a penalization constant
 * -- preferred: 7.0
 */

@SuppressWarnings("SameParameterValue")

public class TransitionComplexityPlugin extends AnalysisPlugin {
	private static float audibleThreshold = (float) 0.07; // used to filter chroma activations that we consider not audible
	private static int maximumNumberOfChordTones = 4; // used to limit number of tones we work with in chord
	private static int maximalComplexity = 7; // used to assign a maximal value for 2 chords that have no common root

	private final static int NUMBER_OUTPUTS = 3;

	public TransitionComplexityPlugin() {
		pluginKey = "harmanal:transition_complexity";
		pluginName = "Transition Complexity";

		inputFileSuffixes = new ArrayList<>();
		inputFileSuffixes.add("-chromas.txt");
		inputFileSuffixes.add("-segmentation.txt");

		outputFileSuffix = "-report.txt";

		parameters = new HashMap<>();
		parameters.put("audibleThreshold", (float) 0.07);
		parameters.put("maximumNumberOfChordTones", (float) 4.0);
		parameters.put("maximalComplexity", (float) 7.0);

		setParameters();
	}

	/**
	 * Analyzes the song: converts chroma + segmentation information to harmony complexity descriptors
	 *
	 * @param inputFile [String] name of the WAV audio file
	 *    These additional files are expected in the folder
	 *    - chromaFile: name of the file containing chroma information (suffix: -chromas.txt)
	 *    - segmentationFile: name of the file containing segmentation information (suffix: -segmentation.txt)
	 */

	public String analyse(String inputFile, boolean force) throws IOException, IncorrectInputException, OutputAlreadyExists, Chroma.WrongChromaSize {
		String result = super.analyse(inputFile, force);
		String outputFile = inputFile + outputFileSuffix;

		String chromaFile = inputFile + "-chromas.txt";
		String segmentationFile = inputFile + "-segmentation.txt";

		result += "Chroma file: " + chromaFile + "\n";
		result += "Segmentation file: " + segmentationFile + "\n";
		result += "Output: " + outputFile + "\n";

		List<String> chromaLinesList = Files.readAllLines(new File(chromaFile).toPath(), Charset.defaultCharset());
		List<String> segmentationLinesList = Files.readAllLines(new File(segmentationFile).toPath(), Charset.defaultCharset());
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

		// 3. Iterate over chord progression, deriving chord and transition complexities
		for (int[] chord : chordProgression) {
			// sum number of all tones for final averages
			int numberTones = AudioAnalysisHelper.getNumberOfTones(chord);
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
					out.write("transition: NO COMMON ROOTS (maximal complexity: " + maximalComplexity + ")\n");
					transitionComplexity = maximalComplexity;
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
		"Average Chord Complexity (ACC): " + ahc + "\n" +
		"Relative Transition Complexity (RTC): " + rtc + "\n";

		out.write(results);
		result += results;
		out.close();

		return result;
	}

	public VisualizationData getDataFromOutput(String outputFile) throws IOException, AudioAnalyser.LoadFailedException, AnalysisPlugin.OutputNotReady, ParseOutputError, PluginLoader.LoadFailedException {
		VisualizationData data = super.getDataFromOutput(outputFile);
		List<Float> values = new ArrayList<>();
		List<String> labels = new ArrayList<>();
		List<String> linesList = readOutputFile(outputFile);

		/* Plugin-specific parsing of the result */
		// get last NUMBER_OUTPUTS lines
		List<String> tail = linesList.subList(Math.max(linesList.size() - NUMBER_OUTPUTS, 0), linesList.size());
		for (String line : tail) {
			Scanner sc = new Scanner(line).useDelimiter("\\s*\\:\\s*");
			labels.add(sc.next());
			if (sc.hasNextFloat()) {
				values.add(sc.nextFloat());
			} else {
				throw new ParseOutputError("Output did not have the required fields");
			}
			sc.close();
		}
		data.setValues(values);
		data.setLabels(labels);
		return data;
	}

	protected void setParameters() {
		audibleThreshold = parameters.get("audibleThreshold");
		maximumNumberOfChordTones = Math.round(parameters.get("maximumNumberOfChordTones"));
		maximalComplexity = Math.round(parameters.get("maximalComplexity"));
	}
}
