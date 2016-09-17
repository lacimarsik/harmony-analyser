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
	public final String[] VISUAL_PLUGINS = new String[] {
		"nnls-chroma:chordino",
		"harmanal:transition_complexity",
		"chromanal:chroma_complexity_simple",
		"chromanal:chroma_complexity_tonal"
	};

	public final String[] STATIC_VISUALIZATIONS = new String[] {
		"chord_palette"
	};

	public DrawPanel createDrawPanel(String inputFile, String pluginKey) throws IOException, AudioAnalyser.LoadFailedException, AnalysisPlugin.OutputNotReady, DrawPanel.CannotVisualize, PluginLoader.LoadFailedException {
		switch (pluginKey) {
			case "nnls-chroma:chordino":
				return new SegmentationDrawPanel(inputFile);
			case "chord_palette":
				return new PaletteDrawPanel(inputFile);
			case "harmanal:transition_complexity":
				return new ComplexityChartDrawPanel(inputFile);
			case "chromanal:chroma_complexity_simple":
				return new ChromaDrawPanel(inputFile, "Simple");
			case "chromanal:chroma_complexity_tonal":
				return new ChromaDrawPanel(inputFile, "Tonal");
			default:
				return null;
		}
	}
}

