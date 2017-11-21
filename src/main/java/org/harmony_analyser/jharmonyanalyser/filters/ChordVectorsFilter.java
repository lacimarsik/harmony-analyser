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
		inputFileSuffixes.add("-chordino-labels");
		inputFileSuffixes.add("-chordino-tones");
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

		// 0. Pre-process Chordino chord tones output so it contains only 1 timestamp and relative chord tone names
		// prepares:
		// chordList (list of Chord models)
		// chordTimestampList (timestamps related to chordList)
		List<String> preProcessLinesList = Files.readAllLines(new File(inputFiles.get(1)).toPath(), Charset.defaultCharset());
		List<Float> preProcessTimestampList = new ArrayList<>();
		preProcessTimestampList.addAll(preProcessLinesList.stream().map(AudioAnalysisHelper::getTimestampFromLineContainingTimestampAndLength).collect(Collectors.toList()));
		List<Float> preProcessTonesList = new ArrayList<>();
		preProcessTonesList.addAll(preProcessLinesList.stream().map(AudioAnalysisHelper::getFloatFromLine).collect(Collectors.toList()));
		List<Chord> chordList = new ArrayList<>();
		List<Float> chordTimestampList = new ArrayList<>();
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

		// 1. Get timestamps from the chord labels and keys file
		// prepares:
		// chordLabelList (list of chord labels)
		// chordLabelTimestampList (timestamps related to chordList)
		List<String> chordLabelLinesList = Files.readAllLines(new File(inputFiles.get(0)).toPath(), Charset.defaultCharset());
		List<String> chordLabelList = new ArrayList<>();
		List<Float> chordLabelTimestampList = new ArrayList<>();
		chordLabelList.addAll(chordLabelLinesList.stream().map(AudioAnalysisHelper::getLabelFromLine).collect(Collectors.toList()));
		chordLabelTimestampList.addAll(chordLabelLinesList.stream().map(AudioAnalysisHelper::getTimestampFromLine).collect(Collectors.toList()));


		// If there is no present timestamp 0.0, automatically fix: Add 0.0 timestamp with EMPTY_CHORD
		if (chordTimestampList.get(0) > 0.000000001) {
			chordTimestampList.add(0, 0.0f);
			chordList.add(0, Chord.EMPTY_CHORD);
			chordLabelTimestampList.add(0, 0.0f);
			chordLabelList.add(0, "N");
		}

		// Check for the last timestamp - make sure that the the latest timestamp is used
		// (check keys whether there is a later timestamp than in the chord file)
		// Automatically fix: Add (latest timestamp) to the end of the file, with "(unknown)" key
		float latestChordTimestamp = chordTimestampList.get(chordTimestampList.size() - 1);
		float latestKeyTimestamp = keyTimestampList.get(keyTimestampList.size() - 1);
		if (latestKeyTimestamp > latestChordTimestamp) {
			chordTimestampList.add(latestKeyTimestamp);
			chordList.add(Chord.EMPTY_CHORD);
			chordLabelTimestampList.add(latestKeyTimestamp);
			chordLabelList.add("N");
		}

		int chordIndex = 0, chordLabelIndex = 0;
		float chordLabelTimestamp;
		float chordTimestamp;
		int keyIndex = 0;
		keyTimestampList = new ArrayList<>();
		String chordLabel;
		Chord chord;
		Chord previousChord = Chord.EMPTY_CHORD;

		// 2. Iterate over both chord label and chord array - getting labels from chord label output and printing the vector
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
				chordIndex--; // do not move chordIndex this iteration
			} else {
				// if next key timestamp is lower than currently examined chord timestamp
				if (((keyIndex + 1) < keyTimestampList.size()) && (keyTimestampList.get(keyIndex + 1) <= chordLabelTimestamp)) {
					// => go to the next key timestamp
					keyIndex++;
				}

				// Get chord
				chord = chordList.get(chordIndex);

				if ((chord == Chord.EMPTY_CHORD) || (previousChord == Chord.EMPTY_CHORD)) {
					System.out.println("SKIP (one or both chords were not assigned)\n\n");
				} else {
					// Write the chord vector of boolean to the output
					out.write(chordLabelTimestamp + ": " + chord.getStringVector() + "\n");
				}
				previousChord = chord;
			}
			chordIndex++;
			chordLabelIndex++;
		}

		out.close();

		return result;
	}

	@Override
	protected void setParameters() {
		// No parameters present for this plugin
	}
}
