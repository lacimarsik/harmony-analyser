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
 * Filter to convert key output: Creates a time series of 12-dimensional boolean vectors (representing keys in time) from the Key Detector output
 */

/*
 * KeyVectorsFilter
 *
 * - requires: Key detector output
 * - creates a time series of 12-dimensional boolean vectors
 */

@SuppressWarnings("SameParameterValue")
public class KeyVectorsFilter extends LineChartPlugin {
	public KeyVectorsFilter() {
		key = "filters:key_vectors";
		name = "Key Vectors";
		description = "Converts Key detector output to a time series of 12-dimensional boolean vectors of keys";

		inputFileSuffixes = new ArrayList<>();
		inputFileSuffixes.add("-key");
		inputFileSuffixes.add("-chordino-labels");
		inputFileExtension = ".txt";

		outputFileSuffix = "-key-vectors";
		outputFileExtension = ".txt";

		parameters = new HashMap<>();

		setParameters();
	}

	public String analyse(String inputFile, boolean force) throws IOException, AudioAnalyser.IncorrectInputException, AudioAnalyser.OutputAlreadyExists, Chroma.WrongChromaSize {
		String result = super.analyse(inputFile, force);

		BufferedWriter out = new BufferedWriter(new FileWriter(outputFile));

		List<String> keyLinesList = Files.readAllLines(new File(inputFiles.get(0)).toPath(), Charset.defaultCharset());
		List<String> keyList = new ArrayList<>();
		List<Float> keyTimestampList = new ArrayList<>();
		List<String> chordLinesList = Files.readAllLines(new File(inputFiles.get(1)).toPath(), Charset.defaultCharset());
		List<Float> chordTimestampList = new ArrayList<>();
		keyTimestampList.addAll(keyLinesList.stream().map(AudioAnalysisHelper::getTimestampFromLine).collect(Collectors.toList()));
		keyList.addAll(keyLinesList.stream().map(AudioAnalysisHelper::getLabelFromLine).collect(Collectors.toList()));
		chordTimestampList.addAll(chordLinesList.stream().map(AudioAnalysisHelper::getTimestampFromLine).collect(Collectors.toList()));

		// If there is no present timestamp 0.0, automatically fix: Add 0.0 timestamp with "(unknown)" key
		if (keyTimestampList.get(0) > 0.000000001) {
			keyTimestampList.add(0, 0.0f);
			keyList.add(0, "(unknown)");
		}

		// Check for the last timestamp - make sure that the the latest timestamp is used
		// (check chords whether there is a later timestamp than in the key file)
		// Automatically fix: Add (latest timestamp) to the end of the file, with "(unknown)" key
		float latestKeyTimestamp = keyTimestampList.get(keyTimestampList.size() - 1);
		float latestChordTimestamp = chordTimestampList.get(chordTimestampList.size() - 1);
		if (latestChordTimestamp > latestKeyTimestamp) {
			keyTimestampList.add(latestChordTimestamp);
			keyList.add("(unknown)");
		}

		float keyTimestamp;
		int keyIndex = 0;
		Key key;
		Key previousKey = Key.EMPTY_KEY;

		// Iterate over keys, parse the key and convert it to the key vector
		for (String label : keyList) {
			if ((keyIndex > keyTimestampList.size() - 1) || (keyIndex > keyList.size() - 1)) {
				break;
			}
			if ((keyIndex > keyTimestampList.size() - 1) || (keyIndex > keyList.size() - 1)) {
				break;
			}
			keyTimestamp = keyTimestampList.get(keyIndex);

			// Get key from Key Detector label
			key = Chordanal.createKeyFromName(keyList.get(keyIndex));

			if (key == Key.EMPTY_KEY) {
				System.out.println("SKIP (key not parsed)\n\n");
			} else {
				out.write(keyTimestamp + ": " + key.getStringVector() + "\n");
			}

			keyIndex++;
		}

		out.close();

		return result;
	}

	@Override
	protected void setParameters() {
		// No parameters present for this plugin
	}
}
