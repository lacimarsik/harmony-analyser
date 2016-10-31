package org.harmony_analyser.plugins.chordanal_plugins;

import org.harmony_analyser.application.services.*;
import org.harmony_analyser.chordanal.*;
import org.harmony_analyser.chromanal.Chroma;
import org.harmony_analyser.plugins.LineChartPlugin;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Plugin for high-level audio analysis using chroma / chord transcription input, based on Fred Lerdahl's Tonal Pitch Space (TPS)
 * for more information on TPS: http://www.oupcanada.com/catalog/9780195178296.html
 */

/*
 * TPSDistancePlugin
 *
 * - requires: chord estimates, key estimates
 * - calculates TPS distance for each pair of subsequent chords
 *
 * parameters
 * - no parameters present
 */

@SuppressWarnings("SameParameterValue")

public class TPSDistancePlugin extends LineChartPlugin {
	public TPSDistancePlugin() {
		pluginKey = "chordanal:tps_distance";
		pluginName = "TPS Distance";

		inputFileSuffixes = new ArrayList<>();
		inputFileSuffixes.add("-chordino-labels");
		inputFileSuffixes.add("-chordino-tones");
		inputFileSuffixes.add("-key");
		inputFileExtension = ".txt";

		outputFileSuffix = "-tps-distance";

		parameters = new HashMap<>();

		setParameters();
	}

	public String analyse(String inputFileWav, boolean force, boolean verbose) throws IOException, AudioAnalyser.IncorrectInputException, OutputAlreadyExists, Chroma.WrongChromaSize {
		String result = super.analyse(inputFileWav, force, verbose);
		String outputFile = inputFileWav + outputFileSuffix + ".txt";
		String outputFileVerbose = inputFileWav + outputFileSuffix + "-verbose" + ".txt";
		List<String> inputFiles = new ArrayList<>();
		for (String suffix : inputFileSuffixes) {
			String inputFileName = inputFileWav + suffix + inputFileExtension;
			inputFiles.add(inputFileName);
		}

		BufferedWriter out = new BufferedWriter(new FileWriter(outputFile));
		BufferedWriter outVerbose = new BufferedWriter(new FileWriter(outputFileVerbose));

		if (verbose) outVerbose.write("Preparing chords from Chordino analysis ...\n");

		// 0. Pre-process Chordino chord tones output so it contains only 1 timestamp and relative chord tone names
		// prepares:
		// chordList (list of Harmony models)
		// chordTimestampList (timestamps related to chordList)
		List<String> preProcessLinesList = Files.readAllLines(new File(inputFiles.get(1)).toPath(), Charset.defaultCharset());
		List<Float> preProcessTimestampList = new ArrayList<>();
		preProcessTimestampList.addAll(preProcessLinesList.stream().map(AudioAnalysisHelper::getTimestampFromLineContainingTimestampAndLength).collect(Collectors.toList()));
		List<Float> preProcessTonesList = new ArrayList<>();
		preProcessTonesList.addAll(preProcessLinesList.stream().map(AudioAnalysisHelper::getFloatFromLine).collect(Collectors.toList()));
		List<Harmony> chordList = new ArrayList<>();
		List<Float> chordTimestampList = new ArrayList<>();

		int lineIndex = 0;
		float previousTimestamp = preProcessTimestampList.get(0);
		Harmony harmony = new Harmony(new ArrayList<>());
		// iterate over all timestamps and while it is the same, add tones to a newly created chord
		for (float timestamp : preProcessTimestampList) {
			if (timestamp == previousTimestamp) {
				Tone tone = new Tone(Math.round(preProcessTonesList.get(lineIndex)));
				harmony.addTone(tone);
			} else {
				chordList.add(harmony);
				chordTimestampList.add(previousTimestamp);
				harmony = new Harmony(new ArrayList<>());
			}
			previousTimestamp = timestamp;
			lineIndex++;
		}

		if (verbose) outVerbose.write(chordList.size() + " chords prepared\n\n");

		if (verbose) outVerbose.write("Reading Chord labels and keys  ...\n");

		// 1. Get timestamps from the chord labels and keys file
		// prepares:
		// chordLabelList (list of chord labels)
		// chordLabelTimestampList (timestamps related to chordList)
		// keyList (list of keys)
		// keyTimestampList (timestamps related to keyList)
		List<String> chordLabelLinesList = Files.readAllLines(new File(inputFiles.get(0)).toPath(), Charset.defaultCharset());
		List<String> chordLabelList = new ArrayList<>();
		List<Float> chordLabelTimestampList = new ArrayList<>();
		List<String> keyLinesList = Files.readAllLines(new File(inputFiles.get(2)).toPath(), Charset.defaultCharset());
		List<String> keyList = new ArrayList<>();
		List<Float> keyTimestampList = new ArrayList<>();
		chordLabelList.addAll(chordLabelLinesList.stream().map(AudioAnalysisHelper::getLabelFromLine).collect(Collectors.toList()));
		chordLabelTimestampList.addAll(chordLabelLinesList.stream().map(AudioAnalysisHelper::getTimestampFromLine).collect(Collectors.toList()));
		keyList.addAll(keyLinesList.stream().map(AudioAnalysisHelper::getLabelFromLine).collect(Collectors.toList()));
		keyTimestampList.addAll(keyLinesList.stream().map(AudioAnalysisHelper::getTimestampFromLine).collect(Collectors.toList()));

		if (verbose) outVerbose.write(chordLabelList.size() + " chord labels and " + keyList.size() + " keys successfully read\n\n");

		int chordIndex = 0, chordLabelIndex = 0;
		float chordLabelTimestamp;
		float chordTimestamp;
		int keyIndex = 0;
		float keyTimestamp;
		String chordLabel;
		Harmony chord;
		Tone chordRoot;
		Key key;
		String previousChordLabel = "";
		Tone previousChordRoot = Tone.EMPTY_TONE;
		Harmony previousChord = Harmony.EMPTY_HARMONY;
		Key previousKey = Key.EMPTY_KEY;

		// 2. Iterate over both chord label and chord array, checking respective keys, and deriving TPS distnaces
		for (String label : chordLabelList) {
			if ((chordLabelIndex > chordLabelTimestampList.size() - 1) || (chordLabelIndex > chordLabelList.size() - 1)) {
				break;
			}
			if ((chordIndex > chordTimestampList.size() - 1) || (chordIndex > chordList.size() - 1)) {
				break;
			}
			chordLabelTimestamp = chordLabelTimestampList.get(chordLabelIndex);
			chordTimestamp = chordTimestampList.get(chordIndex);
			if (chordLabelTimestamp != chordTimestamp) {
				if (verbose) outVerbose.write("SKIP: Timestamp of chord and chord label did not match " + chordLabelTimestamp + " and " +  chordTimestamp + "\n");
				chordIndex--; // do not move chordIndex this iteration
			} else {
				// if next key timestamp is lower than currently examined chord timestamp
				if (((keyIndex + 1) < keyTimestampList.size()) && (keyTimestampList.get(keyIndex + 1) <= chordLabelTimestamp)) {
					// => go to the next key timestamp
					if (verbose) outVerbose.write("KEY CHANGE: Moving to next key label\n");
					keyIndex++;
				}

				// Get chord root from chord label and chord
				chordLabel = chordLabelList.get(chordLabelIndex);
				chordRoot = Chordanal.getRootToneFromChordLabel(chordLabel);
				chord = chordList.get(chordIndex);
				key = Chordanal.createKeyFromName(keyList.get(keyIndex));

				if ((chordRoot == Tone.EMPTY_TONE) || (previousChordRoot == Tone.EMPTY_TONE) || (chord == Harmony.EMPTY_HARMONY) || (previousChord == Harmony.EMPTY_HARMONY) || (key == Key.EMPTY_KEY) || (previousKey == Key.EMPTY_KEY)) {
					if (verbose) outVerbose.write("SKIP (one or both chords were not assigned)\n\n");
				} else {
					// Get TPS Distance of the two chords
					float tpsDistance = TonalPitchSpace.getTPSDistance(chord, chordRoot, key, previousChord, previousChordRoot, previousKey, verbose);
					if (verbose) outVerbose.write("chord: " + chordLabel + ", previousChord: " + previousChordLabel + ", distance: " + tpsDistance + "\n\n");
					out.write(chordLabelTimestamp + ": " + tpsDistance + "\n");
				}
				previousChord = chord;
				previousChordRoot = chordRoot;
				previousChordLabel = chordLabel;
				previousKey = key;
			}
			chordIndex++;
			chordLabelIndex++;
		}

		out.close();
		outVerbose.close();

		return result;
	}

	@Override
	protected void setParameters() {
		// No parameters present for this plugin
	}
}
