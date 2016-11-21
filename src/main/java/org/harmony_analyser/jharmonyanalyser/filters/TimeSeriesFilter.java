package org.harmony_analyser.jharmonyanalyser.filters;

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
 * Plugin for high-level audio analysis using chroma / chord transcription input, based on Chordanal model
 */

/*
 * TimeSeriesFilter
 *
 * - requires: Time series in the form timestamp: value
 * - creates a time series with a fixed sampling rate
 */

@SuppressWarnings("SameParameterValue")

public class TimeSeriesFilter extends TextFileFilter {
	private float samplingRate;

	public TimeSeriesFilter() {
		filterKey = "filters:time_series";
		filterName = "Timestamp to fixed-rate time series filter";

		parameters = new HashMap<>();
		parameters.put("samplingRate", (float) 100);

		setParameters();
	}

	/**
	 * Filters the result text file, creating a fixed sampling rate time series
	 */

	public String filter(String inputTextFile) throws IOException, AudioAnalyser.IncorrectInputException {
		String result = super.filter(inputTextFile);

		List<String> inputFileLinesList = Files.readAllLines(new File(inputTextFile).toPath(), Charset.defaultCharset());
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
		int index = 0;
		for (Float value : inputFileValuesList) {
			if (index == 0) {
				index++;
				continue;
			}
			timestamp = inputFileTimestampList.get(index);

			// TODO: Change
			outputTimestampList.add(timestamp);
			outputValuesList.add(value);

			previousTimestamp = timestamp;
			previousValue = value;
			index++;
		}

		// 4. Rewrite input file using new timestamps and values
		BufferedWriter out = new BufferedWriter(new FileWriter(inputTextFile));
		for (Float value : outputValuesList) {
			timestamp = outputTimestampList.get(index);
			out.write(timestamp + ": " + value + "\n");
		}
		out.close();

		return result;
	}

	protected void setParameters() {
		samplingRate = parameters.get("samplingRate");
	}
}