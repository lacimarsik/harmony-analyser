package org.harmony_analyser.jharmonyanalyser.filters;

import org.harmony_analyser.application.visualizations.VisualizationData;
import org.harmony_analyser.jharmonyanalyser.chroma_analyser.Chroma;
import org.harmony_analyser.jharmonyanalyser.services.AudioAnalyser;
import org.harmony_analyser.jharmonyanalyser.services.AudioAnalysisHelper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Filter to convert outputs: create a time series from timestamp-based text file containing single points or vectors, in the way that the value is copied until the next timestamp
 */

/*
 * TimeSeriesFilter
 *
 * - requires: Time series in the form timestamp: value
 * - creates a time series with a fixed sampling rate
 */

@SuppressWarnings("SameParameterValue")

public class FlatTimeSeriesFilter extends AnalysisFilter {
	private float samplingRate;
	private int vectorSize;

	public FlatTimeSeriesFilter() {
		key = "filters:flat_time_series";
		name = "Timestamp to flat time series filter";
		description = "Takes 'timestamp: value/vector' time series, and transforms it into fixed sample rate values by copying";

		inputFileSuffixes = new ArrayList<>();
		inputFileSuffixes.add(""); // no suffix, arbitrary input file is allowed
		inputFileExtension = ""; // no extension, arbitrary input file is allowed

		outputFileSuffix = ""; // no suffix, will replace the input file

		parameters = new HashMap<>();
		parameters.put("samplingRate", (float) 100);
		parameters.put("vectorSize", (float) 12);

		setParameters();
	}

	/**
	 * Filters the result text file, creating a fixed sampling rate time series
	 */

	public String analyse(String inputFile, boolean force, boolean verbose) throws IOException, AudioAnalyser.IncorrectInputException, Chroma.WrongChromaSize, AudioAnalyser.OutputAlreadyExists {
		String result = super.analyse(inputFile, force, verbose);

		String outputFileVerbose = inputFile + outputFileSuffix + "-verbose" + ".txt";
		BufferedWriter outVerbose = new BufferedWriter(new FileWriter(outputFileVerbose));

		List<String> inputFileLinesList = Files.readAllLines(new File(inputFile).toPath(), Charset.defaultCharset());
		List<Float> inputFileTimestampList = new ArrayList<>();
		List<float[]> inputFileValuesList = new ArrayList<>();

		// 1. Get timestamps from the input file
		inputFileTimestampList.addAll(inputFileLinesList.stream().map(AudioAnalysisHelper::getTimestampFromLine).collect(Collectors.toList()));

		// 2. Get values from the input file
		for (String value : inputFileLinesList) {
			float[] floatArray = AudioAnalysisHelper.getFloatArrayFromLine(value, vectorSize);
			inputFileValuesList.add(floatArray);
		}

		// 3. Iterate over timestamps and values, creating time series values
		List<Float> outputTimestampList = new ArrayList<>();
		List<float[]> outputValuesList = new ArrayList<>();
		float[] previousValue;
		float previousTimestamp, timestamp;
		previousTimestamp = inputFileTimestampList.get(0);
		previousValue = inputFileValuesList.get(0);
		float sampleLength = 1 / samplingRate;
		int index = 0;
		for (float[] floatArray : inputFileValuesList) {
			if (index == 0) {
				index++;
				continue;
			}
			timestamp = inputFileTimestampList.get(index);

			// Find out difference between timestamps
			float timestampDifference = timestamp - previousTimestamp;
			outVerbose.write("timestampDifference: " + timestampDifference + "\n");
			if (timestampDifference > sampleLength) {
				// CASE 1: Timestamp difference greater than sample length
				float newTimestamp = previousTimestamp;
				float[] newValue;
				outVerbose.write("STARTING WITH timestamp: " + newTimestamp + "\n");
				int sampleIndex = 0;
				float ratio = sampleLength / timestampDifference;
				// iteratively create samples from the slope defined by successive points
				while (newTimestamp < timestamp) {
					newTimestamp += sampleLength;
					sampleIndex++;
					newValue = previousValue;
					outputTimestampList.add(newTimestamp);
					outputValuesList.add(newValue);
				}

				// bump previous timestamp-value and continue
				previousTimestamp = timestamp;
				previousValue = floatArray;
				index++;
			} else {
				// CASE 2: Timestamp difference lower than sample length
				// Omit the current timestamp-value pair and continue with the next one, leaving previous timestamp-value pair
				index++;
			}
		}

		// 4. Rewrite input file using new timestamps and values
		index = 0;
		BufferedWriter out = new BufferedWriter(new FileWriter(inputFile));
		for (float[] value : outputValuesList) {
			timestamp = outputTimestampList.get(index);
			String resultArray = "";
			for (int i = 0; i < vectorSize; i++) {
				resultArray += value[i];
			}
			out.write(timestamp + ": " + resultArray + "\n");
			index++;
		}
		out.close();
		outVerbose.close();

		return result;
	}

	protected void setParameters() {
		samplingRate = parameters.get("samplingRate");
		vectorSize = Math.round(parameters.get("vectorSize"));
	}

	public VisualizationData getDataFromOutput(String outputFile) {
		return VisualizationData.EMPTY_VISUALIZATION_DATA; // Return null object
	}
}