package org.harmony_analyser.application;

import org.harmony_analyser.application.services.*;
import org.harmony_analyser.plugins.*;
import org.harmony_analyser.plugins.vamp_plugins.*;
import org.vamp_plugins.PluginLoader;

import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

@SuppressWarnings({"SameParameterValue", "UnusedParameters"})

public class SegmentationDrawPanel extends DrawPanel {
	private List<Float> timestamps;
	private List<String> labels;

	public SegmentationDrawPanel(String inputFile) throws AudioAnalyser.LoadFailedException, AnalysisPlugin.OutputNotReady, IOException, PluginLoader.LoadFailedException, CannotVisualize {
		super();
		timestamps = new ArrayList<>();
		labels = new ArrayList<>();
		getData(inputFile);
	}

	/* Public / Package methods */

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		drawChordSegmentation(g);
	}

	void getData(String inputFile) throws IOException, AudioAnalyser.LoadFailedException, AnalysisPlugin.OutputNotReady, PluginLoader.LoadFailedException, CannotVisualize {
		List<String> linesList = new ChordinoPlugin().getResultForInputFile(inputFile);

		/* Plugin-specific parsing of the result */
		float timestamp;
		String label;

		try {
			for (String line : linesList) {
				timestamp = AudioAnalyser.getTimestampFromLine(line);
				label = AudioAnalyser.getLabelFromLine(line);
				if (label.equals("")) {
					throw new CannotVisualize("Output did not have the required fields");
				}
				timestamps.add(timestamp);
				labels.add(label);
			}
		} catch (NumberFormatException e) {
			throw new CannotVisualize("Output did not have the required fields");
		}
	}

	/* Private methods */

	/* Complet analysis */

	private void drawChordSegmentation(Graphics g) {
		for (Color color : palette) {
			drawSegment(g, 0.077, color);
		}
		cursor.setLocation(0, 0);
	}

	/* Analysis components */
}
