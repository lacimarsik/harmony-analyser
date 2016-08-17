package org.harmony_analyser.application;

import javax.swing.*;
import java.awt.*;

class DrawPanel extends JPanel {

	public DrawPanel() {
		setBorder(BorderFactory.createLineBorder(Color.black));
	}

	public Dimension getPreferredSize() {
		return new Dimension(250,200);
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		// Draw Text
		g.drawString("This is my custom Panel!",10,20);
	}
}