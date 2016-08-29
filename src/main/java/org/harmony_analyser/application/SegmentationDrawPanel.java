package org.harmony_analyser.application;

import org.harmony_analyser.application.services.*;
import org.harmony_analyser.chordanal.*;
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
		float maximalTimestamp = timestamps.get(timestamps.size() - 1);

		cursor.setLocation(0, 0);
		float previousTimestamp = timestamps.get(0);
		float segmentSize;
		String relativeToneName;
		int i = 0;
		for (float timestamp : timestamps) {
			relativeToneName = labels.get(i).substring(0, Math.min(labels.get(i).length(), 2));
			segmentSize = ((timestamp - previousTimestamp) / maximalTimestamp);
			drawSegment(g, segmentSize, getColorForTone(relativeToneName));
			previousTimestamp = timestamp;
			i++;
		}
		cursor.setLocation(0, 0);
	}

	/* Analysis components */

	private Color getColorForTone(String relativeToneName) {
		String notAllowedCharacters = "mda/";
		if ((relativeToneName.length() == 2) && ((Character.isDigit(relativeToneName.charAt(1)) || (notAllowedCharacters.contains(relativeToneName.substring(1,1)))))) {
			relativeToneName = relativeToneName.substring(0,1);
		}

		Tone tone = Chordanal.createToneFromRelativeName(relativeToneName);
		if (tone == null) {
			return palette.get(12);
		} else {
			return palette.get(tone.getNumberMapped());
		}
	}
}
