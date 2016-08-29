package org.harmony_analyser.application;

import org.harmony_analyser.application.services.AudioAnalyser;
import org.harmony_analyser.plugins.AnalysisPlugin;

import javax.swing.*;
import java.awt.*;
import java.io.*;

public abstract class DrawPanel extends JPanel {
	final Point cursor; // cursor of drawing (moves from left to right on the canvas)

	DrawPanel() {
		setBorder(BorderFactory.createLineBorder(Color.black));
		cursor = new Point();
		cursor.setLocation(0, 0);
	}

	/* Exceptions */

	public class CannotVisualize extends Exception {
		CannotVisualize(String message) {
			super(message);
		}
	}

	/* Public / Package methods */

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
	}

	abstract void getData(String inputFile) throws IOException, AudioAnalyser.LoadFailedException, AnalysisPlugin.OutputNotReady, CannotVisualize;
}

