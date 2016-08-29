package org.harmony_analyser.application;

import org.harmony_analyser.application.services.*;
import org.harmony_analyser.plugins.*;
import org.harmony_analyser.plugins.chordanal_plugins.*;
import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.data.category.DefaultCategoryDataset;

import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;

@SuppressWarnings({"SameParameterValue", "UnusedParameters"})

public class ComplexityChartDrawPanel extends DrawPanel {
	private final double[] descriptorValues;
	private final String[] descriptorDescriptions;

	public ComplexityChartDrawPanel(String inputFile) throws AudioAnalyser.LoadFailedException, AnalysisPlugin.OutputNotReady, IOException, CannotVisualize {
		super();
		descriptorValues = new double[3];
		descriptorDescriptions = new String[3];
		getData(inputFile);
	}

	/* Public / Package methods */

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		drawComplexityColumnGraph(g, descriptorValues[0], descriptorValues[1], descriptorValues[2]);
	}

	void getData(String inputFile) throws IOException, AudioAnalyser.LoadFailedException, AnalysisPlugin.OutputNotReady, CannotVisualize {
		List<String> linesList = new TransitionComplexityPlugin().getResultForInputFile(inputFile);

		/* Plugin-specific parsing of the result */
		Scanner sc = new Scanner(linesList.get(linesList.size() - 3));
		sc.skip(Pattern.compile("\\bAverage Transition Complexity \\(ATC\\):"));
		if (sc.hasNextFloat()) {
			descriptorValues[0] = (double) sc.nextFloat();
		} else {
			throw new CannotVisualize("Output did not have the required fields");
		}
		sc = new Scanner(linesList.get(linesList.size() - 2));
		sc.skip(Pattern.compile("\\bAverage Chord Complexity \\(ACC\\):"));
		if (sc.hasNextFloat()) {
			descriptorValues[1] = (double) sc.nextFloat();
		} else {
			throw new CannotVisualize("Output did not have the required fields");
		}
		sc = new Scanner(linesList.get(linesList.size() - 1));
		sc.skip(Pattern.compile("\\bRelative Transition Complexity \\(RTC\\):"));
		if (sc.hasNextFloat()) {
			descriptorValues[2] = (double) sc.nextFloat();
		} else {
			throw new CannotVisualize("Output did not have the required fields");
		}
	}

	/* Private methods */

	/* Complet analysis */

	private void drawComplexityColumnGraph(Graphics g, double atc, double ahc, double rtc) {
		descriptorValues[0] = atc;
		descriptorDescriptions[0] = "Avg. Transition Compl.";
		descriptorValues[1] = ahc;
		descriptorDescriptions[1] = "Avg. Chord Compl.";
		descriptorValues[2] = rtc;
		descriptorDescriptions[2] = "Rel. Transition Compl.";

		drawColumnChart(g, descriptorValues, descriptorDescriptions, "Complexity results", "", "", Color.BLACK);
	}

	/* Analysis components */

	/**
	 * Draws simple column chart using JFreeChart
	 * @param g [Graphics] main Graphics object
	 * @param descriptorValues [int[]] descriptor values to plot
	 * @param descriptorDescriptions [String[]] descriptions of descriptors
	 * @param chartTitle [String] name of the chart
	 * @param xAxisTitle [String] name of X axis
	 * @param yAxisTitle [String] name of Y axis
	 * @param gridLinePaint [Color] color of the line paint
	 */
	private void drawColumnChart(Graphics g, double[] descriptorValues, String[] descriptorDescriptions, String chartTitle, String xAxisTitle, String yAxisTitle, Color gridLinePaint) {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for (int i = 0; i < descriptorValues.length; i++) {
			dataset.setValue(descriptorValues[i], descriptorDescriptions[i], "");
		}
		JFreeChart chart = ChartFactory.createBarChart(chartTitle, xAxisTitle, yAxisTitle,dataset, PlotOrientation.VERTICAL, true, true, true);
		CategoryPlot categoryPlot = chart.getCategoryPlot();
		categoryPlot.setRangeGridlinePaint(gridLinePaint);
		categoryPlot.setBackgroundPaint(Color.WHITE);
		chart.getTitle().setFont(new Font("Sans", Font.PLAIN, 15));
		this.removeAll();
		ChartPanel chartPanel = new ChartPanel(chart, this.getWidth(), this.getHeight(), this.getWidth(), this.getHeight(), this.getWidth(), this.getHeight(), false, true, true, true, true, true);
		this.add(chartPanel);
		this.validate();
	}
}
