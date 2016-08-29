package org.harmony_analyser.application;

import org.harmony_analyser.application.services.AudioAnalyser;
import org.harmony_analyser.plugins.AnalysisPlugin;

import java.awt.*;
import java.io.IOException;

@SuppressWarnings({"SameParameterValue", "UnusedParameters"})

public class SegmentationDrawPanel extends DrawPanel {
	public SegmentationDrawPanel(String inputFile) throws AudioAnalyser.LoadFailedException, AnalysisPlugin.OutputNotReady, IOException {
		super();
		getData(inputFile);
	}

	/* Public / Package methods */

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		drawChordSegmentation(g);
	}

	void getData(String inputFile) throws IOException, AudioAnalyser.LoadFailedException, AnalysisPlugin.OutputNotReady {
	}

	/* Private methods */

	/* Complet analysis */

	private void drawChordSegmentation(Graphics g) {
		drawSegment(g, 0.2, Color.CYAN);
		drawSegment(g, 0.2, Color.RED);
		drawSegment(g, 0.2, Color.YELLOW);
		drawSegment(g, 0.2, Color.BLUE);
		drawSegment(g, 0.2, Color.BLACK);
		cursor.setLocation(0, 0);
	}

	/* Analysis components */

	/**
	 * Draws segment of relative length from the client length, using the moving cursor
	 * @param g [Graphics] main Graphics object
	 * @param length [double] relative length, e.g. 0.2 for 20%-filled segment
	 * @param color [Color] color of the segment
	 */
	private void drawSegment(Graphics g, double length, Color color) {
		int widthInPixels = (int) ((double) this.getWidth() * length);

		g.setColor(color);
		g.fillRect((int) cursor.getX(), (int) cursor.getY(), widthInPixels, this.getHeight());
		cursor.move((int) cursor.getX() + widthInPixels, (int) cursor.getY());
	}
}
