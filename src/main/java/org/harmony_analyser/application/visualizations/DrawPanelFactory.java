package org.harmony_analyser.application.visualizations;

/**
 * Factory to create Draw panels on demand
 */

@SuppressWarnings("SameParameterValue")

public class DrawPanelFactory {
	private final String[] ALL_VISUALIZATIONS = new String[] {
		// Visual plugins
		"nnls-chroma:chordino-labels",
		"nnls-chroma:chordino-tones",
		"qm-vamp-plugins:qm-keydetector",
		"chord_analyser:average_chord_complexity_distance",
		"chord_analyser:chord_complexity_distance",
		"chord_analyser:tps_distance",
		"chroma_analyser:simple_difference",
		"chroma_analyser:tonal_difference",
		// Static visualizations
		"chord_analyser:chord_palette"
	};

	public String[] getAllVisualizations() {
		return ALL_VISUALIZATIONS;
	}

	public DrawPanel createDrawPanel(String pluginKey, VisualizationData visualizationData) {
		switch (pluginKey) {
			case "nnls-chroma:chordino-labels":
			case "nnls-chroma:chordino-tones":
			case "qm-vamp-plugins:qm-keydetector":
				return new SegmentationDrawPanel(visualizationData);
			case "chord_analyser:chord_palette":
				return new PaletteDrawPanel(visualizationData);
			case "chord_analyser:harmonic_complexity":
				return new ComplexityChartDrawPanel(visualizationData);
			case "chord_analyser:chord_complexity_distance":
			case "chroma_analyser:simple_difference":
			case "chroma_analyser:tonal_difference":
			case "chord_analyser:tps_distance":
				return new LineChartDrawPanel(visualizationData);
			default:
				return new EmptyDrawPanel(VisualizationData.EMPTY_VISUALIZATION_DATA);
		}
	}
}

