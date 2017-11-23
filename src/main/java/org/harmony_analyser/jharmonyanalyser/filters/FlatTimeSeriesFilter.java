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
import java.util.*;
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
		inputFileExtension = ".txt"; //

		outputFileSuffix = "-flat";
		outputFileExtension = ".txt";

		parameters = new HashMap<>();
		parameters.put("samplingRate", (float) 10);

		setParameters();
	}

	/**
	 * Filters the result text file, creating a fixed sampling rate time series
	 */

	public String analyse(String inputFile, boolean force) throws IOException, AudioAnalyser.IncorrectInputException, Chroma.WrongChromaSize, AudioAnalyser.OutputAlreadyExists {
		String result = super.analyse(inputFile, force);
		List<String> inputFileLinesList = Files.readAllLines(new File(inputFile).toPath(), Charset.defaultCharset());
		List<Float> inputFileTimestampList = new ArrayList<>();
		List<ArrayList<Float>> inputFileValuesList = new ArrayList<>();

		// 1. Get timestamps from the input file
		inputFileTimestampList.addAll(inputFileLinesList.stream().map(AudioAnalysisHelper::getTimestampFromLine).collect(Collectors.toList()));

		// 2. Get values from the input file
		for (String value : inputFileLinesList) {
			ArrayList<Float> floatArray = AudioAnalysisHelper.getFloatArrayFromLine(value);
			inputFileValuesList.add(floatArray);
		}

		// 3. Iterate over timestamps and values, creating time series values
		List<Float> outputTimestampList = new ArrayList<>();
		List<ArrayList<Float>> outputValuesList = new ArrayList<>();
		ArrayList<Float> refValue, oldRefValueToWrite, oldRefValue = new ArrayList<>();
		float refTimestamp;
		oldRefValue.addAll(inputFileValuesList.get(0));
		float sampleLength = 1 / samplingRate;
		int index = 0;

		float lastTimestamp = inputFileTimestampList.get(inputFileTimestampList.size() - 1);
		for (float sampleTimestamp = 0.0f; sampleTimestamp <= lastTimestamp; sampleTimestamp += sampleLength) {
			refTimestamp = inputFileTimestampList.get(index);
			refValue = inputFileValuesList.get(index);

			// Find out difference between sampleLength timestamp and reference timestamp
			if (sampleTimestamp <= refTimestamp) {
				// CASE 1: sampleLength timestamp is lower or equal to the reference timestamp
				// write the current timestamp and value
				outputTimestampList.add(new Float(sampleTimestamp));

				oldRefValueToWrite = new ArrayList<>();
				oldRefValueToWrite.addAll(oldRefValue);
				outputValuesList.add(oldRefValueToWrite);
			} else {
				// CASE 2: sampleLength timestamp is greater than the reference timestamp
				// Do not bump sampleTimestamp, bump the next reference timestamp-value pair, saving the previous value
				oldRefValue = new ArrayList<>();
				oldRefValue.addAll(refValue);
				index++;
				sampleTimestamp -= sampleLength;
			}
		}

		// 4. Rewrite input file using new timestamps and values
		index = 0;
		float timestamp;
		BufferedWriter out = new BufferedWriter(new FileWriter(outputFile));
		for (ArrayList<Float> value : outputValuesList) {
			timestamp = outputTimestampList.get(index);
			String resultArray = "";
			for (int i = 0; i < value.size(); i++) {
				resultArray += Float.toString(value.get(i)) + " ";
			}
			out.write(timestamp + ": " + resultArray + "\n");
			index++;
		}
		out.close();

		return result;
	}

	protected void setParameters() {
		samplingRate = parameters.get("samplingRate");
	}

	public VisualizationData getDataFromOutput(String inputWavFile) {
		return VisualizationData.EMPTY_VISUALIZATION_DATA; // Return null object
	}
}