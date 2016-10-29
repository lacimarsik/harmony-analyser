package org.harmony_analyser.application.visualizations;

/**
 * Factory to create Draw panels on demand
 */

@SuppressWarnings("SameParameterValue")

public class DrawPanelFactory {
	private final String[] ALL_VISUALIZATIONS = new String[] {
		// Visual plugins
		"nnls-chroma:chordino",
		"chordanal:harmonic_complexity",
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
			case "nnls-chroma:chordino":
				return new SegmentationDrawPanel(visualizationData);
			case "chord_palette":
				return new PaletteDrawPanel(visualizationData);
			case "harmanal:transition_complexity":
				return new ComplexityChartDrawPanel(visualizationData);
			case "chromanal:chroma_complexity_simple":
			case "chromanal:chroma_complexity_tonal":
				return new ChromaDrawPanel(visualizationData);
			default:
				return new EmptyDrawPanel(VisualizationData.EMPTY_VISUALIZATION_DATA);
		}
	}
}

