package org.harmony_analyser.jharmonyanalyser.plugins.vamp_plugins;

import org.harmony_analyser.jharmonyanalyser.services.*;
import org.harmony_analyser.application.visualizations.VisualizationData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper for abstract VampPlugins that provide segmentation information (chord / key, etc.)
 */

abstract class SegmentationVampPlugin extends VampPlugin {
	public VisualizationData getDataFromOutput(String inputWavFile) throws IOException, AudioAnalyser.OutputNotReady, AudioAnalyser.ParseOutputError, AudioAnalyser.IncorrectInputException, AudioAnalyser.OutputAlreadyExists {
		VisualizationData data = super.prepareVisualizationData();
		List<Float> timestamps = new ArrayList<>();
		List<String> labels = new ArrayList<>();
		List<String> linesList = readOutputFile(inputWavFile);

		/* Plugin-specific parsing of the result */
		float timestamp;
		String label;

		try {
			for (String line : linesList) {
				timestamp = AudioAnalysisHelper.getTimestampFromLine(line);
				label = AudioAnalysisHelper.getLabelFromLine(line);
				if (label.equals("")) {
					throw new AudioAnalyser.ParseOutputError("Output did not have the required fields");
				}
				timestamps.add(timestamp);
				labels.add(label);
			}
		} catch (NumberFormatException e) {
			throw new AudioAnalyser.ParseOutputError("Output did not have the required fields");
		}
		data.setTimestamps(timestamps);
		data.setLabels(labels);
		return data;
	}
}