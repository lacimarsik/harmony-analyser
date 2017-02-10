package org.harmony_analyser.application.visualizations;

/**
 * Factory to create DataCharts on demand
 */

public class DataChartFactory {
	private final String[] ALL_VISUALIZATIONS = new String[] {
		"nnls-chroma:chordino-labels",
		"nnls-chroma:chordino-tones",
		"qm-vamp-plugins:qm-keydetector",
		"chord_analyser:average_chord_complexity_distance",
		"chord_analyser:chord_complexity_distance",
		"chord_analyser:tps_distance",
		"chroma_analyser:simple_difference",
		"chroma_analyser:complexity_difference",
	};

	public String[] getAllVisualizations() {
		return ALL_VISUALIZATIONS;
	}

	public DataChart createDataChart(String pluginKey, VisualizationData visualizationData) {
		switch (pluginKey) {
			case "nnls-chroma:chordino-labels":
			case "nnls-chroma:chordino-tones":
			case "qm-vamp-plugins:qm-keydetector":
				return new SegmentationDataChart(visualizationData);
			case "chord_analyser:average_chord_complexity_distance":
				return new AveragesDataChart(visualizationData);
			case "chord_analyser:chord_complexity_distance":
			case "chroma_analyser:simple_difference":
			case "chroma_analyser:complexity_difference":
			case "chord_analyser:tps_distance":
				return new LineDataChart(visualizationData);
			default:
				return new EmptyDataChart(VisualizationData.EMPTY_VISUALIZATION_DATA);
		}
	}
}
