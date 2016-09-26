package org.harmony_analyser.plugins;

import org.harmony_analyser.application.visualizations.VisualizationData;

/**
 * Null object
 */

class VoidPlugin extends AnalysisPlugin {
	protected void setParameters() { /* Do nothing */ }

	@Override
	public String analyse(String inputFile, boolean force) {
		return "";
	}

	public VisualizationData getDataFromOutput(String outputFile) {
		return VisualizationData.VOID_VISUALIZATION_DATA; // Return null object
	}
}
