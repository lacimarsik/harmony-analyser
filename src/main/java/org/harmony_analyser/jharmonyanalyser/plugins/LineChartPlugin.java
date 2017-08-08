package org.harmony_analyser.jharmonyanalyser.plugins;

import org.harmony_analyser.jharmonyanalyser.services.*;
import org.harmony_analyser.application.visualizations.VisualizationData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class for plugins that support line chart visualization
 */

@SuppressWarnings("SameParameterValue")

public abstract class LineChartPlugin extends AnalysisPlugin {
	public VisualizationData getDataFromOutput(String inputWavFile) throws IOException, AudioAnalyser.OutputNotReady, AudioAnalyser.ParseOutputError, AudioAnalyser.IncorrectInputException, AudioAnalyser.OutputAlreadyExists {
		VisualizationData data = super.prepareVisualizationData();
		List<Float> timestamps = new ArrayList<>();
		List<Float> values = new ArrayList<>();
		List<String> linesList = readOutputFile(inputWavFile);

		float timestamp, value;

		/* Plugin-specific parsing of the result */
		try {
			for (String line : linesList) {
				timestamp = AudioAnalysisHelper.getTimestampFromLine(line);
				value = Float.parseFloat(AudioAnalysisHelper.getLabelFromLine(line));
				timestamps.add(timestamp);
				values.add(value);
			}
		} catch (NumberFormatException e) {
			throw new AudioAnalyser.ParseOutputError("Output did not have the required fields");
		}
		data.setTimestamps(timestamps);
		data.setValues(values);
		return data;
	}
}