package org.harmony_analyser.application.visualizations;

import org.harmony_analyser.application.services.AudioAnalyser;
import org.harmony_analyser.plugins.AnalysisPlugin;
import org.vamp_plugins.PluginLoader;

import java.io.IOException;

/**
 * Factory to create Draw panels on demand
 */

@SuppressWarnings("SameParameterValue")

public class DrawPanelFactory {
	private final String[] ALL_VISUALIZATIONS = new String[] {
		// Visual plugins
		"nnls-chroma:chordino",
		"harmanal:transition_complexity",
		"chromanal:chroma_complexity_simple",
		"chromanal:chroma_complexity_tonal",
		// Static visualizations
		"chord_palette"
	};

	public String[] getAllVisualizations() {
		return ALL_VISUALIZATIONS;
	}

	public DrawPanel createDrawPanel(String pluginKey, VisualizationData visualizationData) throws IOException, AudioAnalyser.LoadFailedException, AnalysisPlugin.OutputNotReady, PluginLoader.LoadFailedException {
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
				return null;
		}
	}
}

