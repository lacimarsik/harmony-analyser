package org.harmony_analyser.jharmonyanalyser.filters;

import org.harmony_analyser.application.visualizations.VisualizationData;
import org.harmony_analyser.jharmonyanalyser.chroma_analyser.Chroma;
import org.harmony_analyser.jharmonyanalyser.services.*;

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
 * Filter to convert outputs: create a time series from a timestamp-based text file containting a XY-plot in the way that the plot lines remains the same
 */

/*
 * TimeSeriesFilter
 *
 * - requires: Time series in the form timestamp: value
 * - creates a time series with a fixed sampling rate
 */

@SuppressWarnings("SameParameterValue")

public class TimeSeriesFilter extends AnalysisFilter {
	private float samplingRate;

	public TimeSeriesFilter() {
		key = "filters:time_series";
		name = "Timestamp to time series filter";
		description = "Takes 'timestamp: value' time series, and transforms it into fixed sample-rate time series preserving the lines";

		inputFileSuffixes = new ArrayList<>();
		inputFileSuffixes.add(""); // no suffix, arbitrary input file is allowed
		inputFileExtension = ""; // no extension, arbitrary input file is allowed

		outputFileSuffix = ""; // no suffix, will replace the input file

		parameters = new HashMap<>();
		parameters.put("samplingRate", (float) 100);

		setParameters();
	}

	/**
	 * Filters the result text file, creating a fixed sampling rate time series
	 */

	public String analyse(String inputFile, boolean force) throws IOException, AudioAnalyser.IncorrectInputException, Chroma.WrongChromaSize, AudioAnalyser.OutputAlreadyExists {
		String result = super.analyse(inputFile, force);

		List<String> inputFileLinesList = Files.readAllLines(new File(inputFile).toPath(), Charset.defaultCharset());
		List<Float> inputFileTimestampList = new ArrayList<>();
		List<Float> inputFileValuesList = new ArrayList<>();

		// 1. Get timestamps from the input file
		inputFileTimestampList.addAll(inputFileLinesList.stream().map(AudioAnalysisHelper::getTimestampFromLine).collect(Collectors.toList()));

		// 2. Get values from the input file
		inputFileValuesList.addAll(inputFileLinesList.stream().map(AudioAnalysisHelper::getFloatFromLine).collect(Collectors.toList()));

		// 3. Iterate over timestamps and values, creating time series values
		List<Float> outputTimestampList = new ArrayList<>();
		List<Float> outputValuesList = new ArrayList<>();
		float previousTimestamp, previousValue, timestamp;
		previousTimestamp = inputFileTimestampList.get(0);
		previousValue = inputFileValuesList.get(0);
		float sampleLength = 1 / samplingRate;
		int index = 0;
		for (Float value : inputFileValuesList) {
			if (index == 0) {
				index++;
				continue;
			}
			timestamp = inputFileTimestampList.get(index);

			// Find out difference between timestamps and values
			float timestampDifference = timestamp - previousTimestamp;
			float valueDifference = value - previousValue;
			verboseLog("timestampDifference: " + timestampDifference);
			verboseLog("valueDifference: " + valueDifference);
			if (timestampDifference > sampleLength) {
				// CASE 1: Timestamp difference greater than sample length
				float newTimestamp = previousTimestamp;
				float newValue;
				verboseLog("Starting with timestamp: " + newTimestamp);
				int sampleIndex = 0;
				float ratio = sampleLength / timestampDifference;
				// iteratively create samples from the slope defined by successive points
				while (newTimestamp < timestamp) {
					newTimestamp += sampleLength;
					sampleIndex++;
					newValue = previousValue + (sampleIndex * ratio * valueDifference);
					outputTimestampList.add(newTimestamp);
					outputValuesList.add(newValue);
				}

				// bump previous timestamp-value and continue
				previousTimestamp = timestamp;
				previousValue = value;
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
		for (Float value : outputValuesList) {
			timestamp = outputTimestampList.get(index);
			out.write(timestamp + ": " + value + "\n");
			index++;
		}
		out.close();

		return result;
	}

	protected void setParameters() {
		samplingRate = parameters.get("samplingRate");
	}

	public VisualizationData getDataFromOutput(String outputFile) {
		return VisualizationData.EMPTY_VISUALIZATION_DATA; // Return null object
	}
}