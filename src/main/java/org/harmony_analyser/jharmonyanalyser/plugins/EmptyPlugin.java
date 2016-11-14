package org.harmony_analyser.jharmonyanalyser.plugins;

import org.harmony_analyser.application.visualizations.VisualizationData;

class EmptyPlugin extends AnalysisPlugin {
	protected void setParameters() { /* Do nothing */ }

	@Override
	public String analyse(String inputFile, boolean force, boolean verbose) {
		return "";
	}

	public VisualizationData getDataFromOutput(String outputFile) {
		return VisualizationData.EMPTY_VISUALIZATION_DATA; // Return null object
	}
}
