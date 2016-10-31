package org.harmony_analyser.plugins.chordanal_plugins;

import org.harmony_analyser.application.services.*;
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
		pluginKey = "chordanal:tonal_distance";
		pluginName = "TPS Distance";

		inputFileSuffixes = new ArrayList<>();
		inputFileSuffixes.add("-chordino-labels");
		inputFileSuffixes.add("-key");
		inputFileExtension = ".txt";

		outputFileSuffix = "-tps-distance";

		parameters = new HashMap<>();

		setParameters();
	}

	public String analyse(String inputFileWav, boolean force, boolean verbose) throws IOException, AudioAnalyser.IncorrectInputException, OutputAlreadyExists, Chroma.WrongChromaSize {
		String result = super.analyse(inputFileWav, force, verbose);
		String outputFile = inputFileWav + outputFileSuffix + ".txt";
		List<String> inputFiles = new ArrayList<>();
		for (String suffix : inputFileSuffixes) {
			String inputFileName = inputFileWav + suffix + inputFileExtension;
			inputFiles.add(inputFileName);
		}

		// 1. Get timestamps from the chords and keys file
		List<String> chordsLinesList = Files.readAllLines(new File(inputFiles.get(0)).toPath(), Charset.defaultCharset());
		List<Float> chordsTimestampList = new ArrayList<>();
		List<String> keysLinesList = Files.readAllLines(new File(inputFiles.get(1)).toPath(), Charset.defaultCharset());
		List<Float> keysTimestampList = new ArrayList<>();
		chordsTimestampList.addAll(chordsLinesList.stream().map(AudioAnalysisHelper::getTimestampFromLine).collect(Collectors.toList()));
		keysTimestampList.addAll(keysLinesList.stream().map(AudioAnalysisHelper::getTimestampFromLine).collect(Collectors.toList()));

		int chordIndex = 0;
		float chordTimestamp;
		int keyIndex = 0;
		float keyTimestamp;
		String chord;
		keyTimestamp = keysTimestampList.get(0);

		// 2. Iterate over chords, deriving TPS distnaces
		for (String line : chordsLinesList) {
			chordTimestamp = chordsTimestampList.get(chordIndex);

			if (keyTimestamp > chordTimestamp) {
				// Go to the next key timestamp
				keyIndex++;
				if (keyIndex > keysTimestampList.size()-1) {
					break;
				}
				keyTimestamp = keysTimestampList.get(keyIndex);
			}

			// Get chord from the current line
			chord = AudioAnalysisHelper.getLabelFromLine(line);
			System.out.println(chord);
			chordIndex++;
		}

		BufferedWriter out = new BufferedWriter(new FileWriter(outputFile));

		out.write("DONE");
		out.close();

		return result;
	}

	@Override
	protected void setParameters() {
		// No parameters present for this plugin
	}
}
