package org.harmony_analyser.application;

import javax.swing.*;
import java.awt.*;

public class DrawPanel extends JPanel {
	final Point cursor; // cursor of drawing (moves from left to right on the canvas)

	DrawPanel() {
		setBorder(BorderFactory.createLineBorder(Color.black));
		cursor = new Point();
		cursor.setLocation(0, 0);
	}

	/* Public / Package methods */

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
	}
}

