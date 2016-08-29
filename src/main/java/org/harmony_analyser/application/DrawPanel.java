package org.harmony_analyser.application;

import org.harmony_analyser.application.services.AudioAnalyser;
import org.harmony_analyser.plugins.AnalysisPlugin;
import org.vamp_plugins.PluginLoader;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;

@SuppressWarnings("SameParameterValue")

public abstract class DrawPanel extends JPanel {
	final Point cursor; // cursor of drawing (moves from left to right on the canvas)
	static final java.util.List<Color> palette;
	private static final int paletteStepCount = 2; // how many steps in one color space (R / G / B)

	static {
		palette = new ArrayList<>();
		for (int r = 0; r < paletteStepCount; r++) palette.add(new Color(r * 255 / paletteStepCount, 255, 0));
		for (int g = paletteStepCount; g > 0; g--) palette.add(new Color(255, g * 255 / paletteStepCount, 0));
		for (int b = 0; b < paletteStepCount; b++) palette.add(new Color(255, 0, b*255 / paletteStepCount));
		for (int r = paletteStepCount; r>0; r--) palette.add(new Color(r * 255 / paletteStepCount, 0, 255));
		for (int g = 0; g < paletteStepCount; g++) palette.add(new Color(0, g * 255 / paletteStepCount, 255));
		for (int b = paletteStepCount; b>0; b--) palette.add(new Color(0, 255, b * 255 / paletteStepCount));
		palette.remove(palette.size() - 1);
		palette.add(new Color(0, 0, 0));
		palette.add(new Color(150, 150, 150));
	}

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

	abstract void getData(String inputFile) throws IOException, AnalysisPlugin.OutputNotReady, CannotVisualize, PluginLoader.LoadFailedException, AudioAnalyser.LoadFailedException;

	/* Analysis Components */

	/**
	 * Draws segment of relative length from the client length, using the moving cursor
	 * @param g [Graphics] main Graphics object
	 * @param length [double] relative length, e.g. 0.2 for 20%-filled segment
	 * @param color [Color] color of the segment
	 */
	void drawSegment(Graphics g, double length, Color color) {
		int widthInPixels = (int) ((double) this.getWidth() * length);

		g.setColor(color);
		g.fillRect((int) cursor.getX(), (int) cursor.getY(), widthInPixels, this.getHeight());
		cursor.move((int) cursor.getX() + widthInPixels, (int) cursor.getY());
	}
}

