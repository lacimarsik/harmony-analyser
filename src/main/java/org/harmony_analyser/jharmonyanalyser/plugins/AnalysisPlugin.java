package org.harmony_analyser.jharmonyanalyser.plugins;

import org.harmony_analyser.jharmonyanalyser.services.*;
import org.harmony_analyser.application.visualizations.VisualizationData;

import java.io.*;

/**
 * Abstract class for low and high-level audio analysis plugin
 */

@SuppressWarnings("SameParameterValue")

public abstract class AnalysisPlugin extends Analysis {
	protected VisualizationData prepareVisualizationData() {
		VisualizationData visualizationData = new VisualizationData();
		visualizationData.setPluginName(name);
		return visualizationData;
	}

	public abstract VisualizationData getDataFromOutput(String outputFile) throws IOException, AudioAnalyser.OutputNotReady, AudioAnalyser.ParseOutputError;
}
