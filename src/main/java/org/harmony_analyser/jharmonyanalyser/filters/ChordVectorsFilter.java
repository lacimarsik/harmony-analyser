package org.harmony_analyser.jharmonyanalyser.filters;

import org.harmony_analyser.jharmonyanalyser.chord_analyser.*;
import org.harmony_analyser.jharmonyanalyser.chroma_analyser.Chroma;
import org.harmony_analyser.jharmonyanalyser.plugins.LineChartPlugin;
import org.harmony_analyser.jharmonyanalyser.services.AudioAnalyser;
import org.harmony_analyser.jharmonyanalyser.services.AudioAnalysisHelper;

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
 * Filter to convert chord output: Creates a time series of 12-dimensional boolean vectors (representing chords) from the Vamp Chordino output
 */

/*
 * ChordVectorsFilter
 *
 * - requires: Chordino (tones and labels) output
 * - creates a time series of 12-dimensional boolean vectors
 */

@SuppressWarnings("SameParameterValue")
public class ChordVectorsFilter extends LineChartPlugin {
	public ChordVectorsFilter() {
		key = "filters:chord_vectors";
		name = "Chord Vectors";
		description = "Converts Chordino output to a time series of 12-dimensional boolean vectors of chords";

		inputFileSuffixes = new ArrayList<>();
		inputFileSuffixes.add("-chordino-tones");
		inputFileSuffixes.add("-chordino-labels");
		inputFileSuffixes.add("-key");
		inputFileExtension = ".txt";

		outputFileSuffix = "-chord-vectors";
		outputFileExtension = ".txt";

		parameters = new HashMap<>();

		setParameters();
	}

	public String analyse(String inputFile, boolean force) throws IOException, AudioAnalyser.IncorrectInputException, AudioAnalyser.OutputAlreadyExists, Chroma.WrongChromaSize {
		String result = super.analyse(inputFile, force);

		BufferedWriter out = new BufferedWriter(new FileWriter(outputFile));

		// 0. Pre-process Chordino chord tones output so it contains only 1 timestamp and Chord object
		// prepares:
		// chordList (list of Chord models)
		// chordTimestampList (timestamps related to chordList)
		List<String> preProcessLinesList = Files.readAllLines(new File(inputFiles.get(0)).toPath(), Charset.defaultCharset());
		List<Float> preProcessTimestampList = new ArrayList<>();
		preProcessTimestampList.addAll(preProcessLinesList.stream().map(AudioAnalysisHelper::getTimestampFromLineContainingTimestampAndLength).collect(Collectors.toList()));
		List<Float> preProcessTonesList = new ArrayList<>();
		preProcessTonesList.addAll(preProcessLinesList.stream().map(AudioAnalysisHelper::getFloatFromLine).collect(Collectors.toList()));
		List<Chord> chordList = new ArrayList<>();
		List<Float> chordTimestampList = new ArrayList<>();
		// also get chordLabel timestamps - for checking timestamps only
		List<String> chordLabelLinesList = Files.readAllLines(new File(inputFiles.get(1)).toPath(), Charset.defaultCharset());
		List<Float> chordLabelTimestampList = new ArrayList<>();
		chordLabelTimestampList.addAll(chordLabelLinesList.stream().map(AudioAnalysisHelper::getTimestampFromLine).collect(Collectors.toList()));
		// also key timestamps - for checking timestamps only
		List<String> keyLinesList = Files.readAllLines(new File(inputFiles.get(2)).toPath(), Charset.defaultCharset());
		List<Float> keyTimestampList = new ArrayList<>();
		keyTimestampList.addAll(keyLinesList.stream().map(AudioAnalysisHelper::getTimestampFromLine).collect(Collectors.toList()));

		int lineIndex = 0;
		float previousTimestamp = preProcessTimestampList.get(0);
		Chord harmony = new Chord(new ArrayList<>());
		// iterate over all timestamps and while it is the same, add tones to a newly created chord
		for (float timestamp : preProcessTimestampList) {
			if (timestamp == previousTimestamp) {
				Tone tone = new Tone(Math.round(preProcessTonesList.get(lineIndex)));
				harmony.addTone(tone);
			} else {
				chordList.add(harmony);
				chordTimestampList.add(previousTimestamp);
				harmony = new Chord(new ArrayList<>());
			}
			previousTimestamp = timestamp;
			lineIndex++;
		}

		// If there is no present timestamp 0.0, automatically fix: Add 0.0 timestamp with EMPTY_CHORD
		if (chordTimestampList.get(0) > 0.000000001) {
			chordTimestampList.add(0, 0.0f);
			chordList.add(0, Chord.EMPTY_CHORD);
		}

		// Check for the last timestamp - make sure that the the latest timestamp is used
		// (check keys whether there is a later timestamp than in the chord file)
		// Automatically fix: Add (latest timestamp) to the end of the file, with "(unknown)" key
		float latestChordTimestamp = chordTimestampList.get(chordTimestampList.size() - 1);
		float latestChordLabelTimestamp = chordLabelTimestampList.get(chordLabelTimestampList.size() - 1);
		float latestKeyTimestamp = keyTimestampList.get(keyTimestampList.size() - 1);
		float latestTimestamp = (latestChordLabelTimestamp > latestKeyTimestamp) ? latestChordLabelTimestamp : latestKeyTimestamp;
		if (latestTimestamp > latestChordTimestamp) {
			chordTimestampList.add(latestTimestamp);
			chordList.add(Chord.EMPTY_CHORD);
		}

		int chordIndex = 0;
		float chordTimestamp;
		Chord chord;

		// 2. Iterate over both chord label and chord array - getting labels from chord label output and printing the vector
		for (int i = 0; i < chordList.size(); i++) {
			// check whether we are still within the array
			if ((chordIndex > chordTimestampList.size() - 1) || (chordIndex > chordList.size() - 1)) {
				break;
			}
			// Get chord and its timestamp
			chordTimestamp = chordTimestampList.get(chordIndex);
			chord = chordList.get(chordIndex);
			// Write the chord vector of boolean to the output
			out.write(chordTimestamp + ": " + chord.getStringVector() + "\n");
			chordIndex++;
		}

		out.close();

		return result;
	}

	@Override
	protected void setParameters() {
		// No parameters present for this plugin
	}
}
