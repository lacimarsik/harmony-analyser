package org.harmony_analyser.application;

import javax.swing.*;
import java.awt.*;

import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.data.category.*;

class DrawPanel extends JPanel {
	private Point cursor; // cursor of drawing (moves from left to right on the canvas)

	/* Public / Package methods */

	public DrawPanel() {
		setBorder(BorderFactory.createLineBorder(Color.black));
		cursor = new Point();
		cursor.setLocation(0, 0);
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		/*g.setColor(Color.RED);
		drawSegment(g, 0.2, Color.CYAN);
		drawSegment(g, 0.2, Color.RED);
		drawSegment(g, 0.2, Color.YELLOW);
		drawSegment(g, 0.2, Color.BLUE);
		drawSegment(g, 0.2, Color.BLACK);*/
		int[] dv = new int[10];
		String[] dn = new String[10];
		for (int i = 0; i < 10; i++) {
			dv[i] = i;
			dn[i] = "Des " + i;
		}
		drawColumnChart(g, dv, dn, 10, "Test chart", "Descriptors", "Values", Color.BLACK);
	}

	/* Private methods */

	/**
	 * Draws segment of relative length from the client length, using the moving cursor
	 * @param g [Graphics] main Graphics object
	 * @param length [length] relative length, e.g. 0.2 for 20%-filled segment
	 */
	private void drawSegment(Graphics g, double length, Color color) {
		int widthInPixels = (int) ((double) this.getWidth() * length);

		g.setColor(color);
		g.fillRect((int) cursor.getX(), (int) cursor.getY(), widthInPixels, this.getHeight());
		cursor.move((int) cursor.getX() + widthInPixels, (int) cursor.getY());
	}

	/**
	 * Draws simple column chart using JFreeChart
	 * @param g [Graphics] main Graphics object
	 * @param descriptorValues [int[]] descriptor values to plot
	 * @param descriptorNames [String[]] names of descriptors
	 * @param maxY [int] maximum value of chart
	 * @param chartTitle [String] name of the chart
	 * @param xAxisTitle [String] name of X axis
	 * @param yAxisTitle [String] name of Y axis
	 * @param gridLinePaint [Color] color of the line paint
	 */
	private void drawColumnChart(Graphics g, int[] descriptorValues, String[] descriptorNames, int maxY, String chartTitle, String xAxisTitle, String yAxisTitle, Color gridLinePaint) {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for (int i = 0; i < descriptorValues.length; i++) {
			dataset.setValue(descriptorValues[i], "", descriptorNames[i]);
		}
		JFreeChart chart = ChartFactory.createBarChart(chartTitle, xAxisTitle, yAxisTitle,dataset, PlotOrientation.VERTICAL, true, true, true);
		CategoryPlot categoryPlot = chart.getCategoryPlot();
		categoryPlot.setRangeGridlinePaint(gridLinePaint);
		this.removeAll();
		ChartPanel chartPanel = new ChartPanel(chart);
		this.add(chartPanel, BorderLayout.CENTER);
		this.validate();
	}
}