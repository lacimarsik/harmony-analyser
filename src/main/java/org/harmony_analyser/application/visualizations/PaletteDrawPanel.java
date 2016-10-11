package org.harmony_analyser.application.visualizations;

import org.harmony_analyser.chordanal.*;
import java.awt.*;

@SuppressWarnings({"SameParameterValue", "UnusedParameters"})

class PaletteDrawPanel extends DrawPanel {
	PaletteDrawPanel(VisualizationData visualizationData) {
		super(visualizationData);
	}

	/* Public / Package methods */

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		drawPalette(g);
	}

	/* Private methods */

	/* Complet analysis */

	private void drawPalette(Graphics g) {
		for (Color color : palette) {
			drawSegment(g, 0.077, color);
		}
		Harmony chromaticScale = Chordanal.createHarmonyFromRelativeTones("C C# D D# E F F# G G# A A# B");
		if (chromaticScale != null) {
			cursor.setLocation(0, 0);
			int i = 0;
			for (Color color : palette) {
				if (i >= 12) {
					drawNote(g, "N", 0.077, Color.WHITE);
				} else {
					drawNote(g, chromaticScale.tones.get(i).getNameMapped(), 0.077, Color.WHITE);
				}
				i++;
			}
			cursor.setLocation(0, 0);
		}
	}

	/* Analysis components */

	/**
	 * Draws note for the palette
	 * @param g [Graphics] main Graphics object
	 * @param length [double] relative length, e.g. 0.2 for 20% of width
	 * @param color [Color] font color
	 */
	private void drawNote(Graphics g, String note, double length, Color color) {
		int widthInPixels = (int) ((double) this.getWidth() * length);
		int heightInPixels = (int) ((double) this.getHeight() / 2);

		cursor.move((int) (cursor.getX() + (widthInPixels / 2)), (int) cursor.getY());
		g.setColor(color);
		g.drawString(note, (int) cursor.getX(), heightInPixels);
		cursor.move((int) (cursor.getX() + (widthInPixels / 2)), heightInPixels);
	}
}
