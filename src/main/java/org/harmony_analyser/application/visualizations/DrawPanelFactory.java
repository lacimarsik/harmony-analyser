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
		"chordanal:harmonic_complexity",
		"chordanal:tonal_distance",
		"chordanal:tps_distance",
		"chromanal:chroma_complexity_simple",
		"chromanal:chroma_complexity_tonal",
		// Static visualizations
		"chord_palette"
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
			case "chord_palette":
				return new PaletteDrawPanel(visualizationData);
			case "chordanal:harmonic_complexity":
				return new ComplexityChartDrawPanel(visualizationData);
			case "chordanal:tonal_distance":
			case "chromanal:chroma_complexity_simple":
			case "chromanal:chroma_complexity_tonal":
				return new LineChartDrawPanel(visualizationData);
			case "chordanal:tps_distance":
				return new EmptyDrawPanel(VisualizationData.EMPTY_VISUALIZATION_DATA);
			default:
				return new EmptyDrawPanel(VisualizationData.EMPTY_VISUALIZATION_DATA);
		}
	}
}

