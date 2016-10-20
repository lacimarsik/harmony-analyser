package org.harmony_analyser.application.visualizations;

import java.awt.*;

@SuppressWarnings("SameParameterValue")
class EmptyDrawPanel extends DrawPanel {
	EmptyDrawPanel(VisualizationData visualizationData) {
		super(visualizationData);
	}

	/* Public / Package methods */

	public void paintComponent(Graphics g) { /* do nothing */ }
}
