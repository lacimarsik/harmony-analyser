package org.harmony_analyser.application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import org.harmony_analyser.application.visualizations.DataChart;
import org.harmony_analyser.application.visualizations.DataChartFactory;
import org.harmony_analyser.application.visualizations.DrawPanelFactory;
import org.harmony_analyser.jharmonyanalyser.chroma_analyser.Chroma;
import org.harmony_analyser.jharmonyanalyser.services.AnalysisFactory;
import org.harmony_analyser.jharmonyanalyser.services.AudioAnalyser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for Visualization Tool Events
 */

public class VisualizationToolController implements Initializable {
	@FXML
	private StackPane browsePane;

	@FXML
	private TreeView<File> browse;

	@FXML
	private ChoiceBox<String> plugin1;

	@FXML
	private ChoiceBox<String> plugin2;

	@FXML
	private ChoiceBox<String> plugin3;

	@FXML
	private Button analyse1;

	@FXML
	private Button analyse2;

	@FXML
	private Button analyse3;

	@FXML
	private Button analyseAll;

	@FXML
	private Pane pane1;

	@FXML
	private BarChart<String, Number> barChart1;

	@FXML
	private BarChart<String, Number> barChart2;

	@FXML
	private BarChart<String, Number> barChart3;

	@FXML
	private LineChart<Number, Number> lineChart1;

	@FXML
	private LineChart<Number, Number> lineChart2;

	@FXML
	private LineChart<Number, Number> lineChart3;

	@FXML
	private AreaChart<String, Number> areaChart1;

	@FXML
	private AreaChart<String, Number> areaChart2;

	@FXML
	private AreaChart<String, Number> areaChart3;

	private AudioAnalyser audioAnalyser;

	@Override // This method is called by the FXMLLoader when initialization is complete
	public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
		//initialize AudioAnalyser
		AnalysisFactory analysisFactory = new AnalysisFactory();
		DataChartFactory dataChartFactory = new DataChartFactory();
		DrawPanelFactory drawPanelFactory = new DrawPanelFactory();
		audioAnalyser = new AudioAnalyser(analysisFactory, dataChartFactory, drawPanelFactory);

		// create the tree view
		// TODO: Check unchecked assignments
		browse = TreeViewBuilder.buildFileSystemBrowser();
		browsePane.getChildren().add(browse);

		ObservableList<String> visualizationPlugins = FXCollections.observableArrayList(audioAnalyser.getAllVisualizations());
		plugin1.setItems(visualizationPlugins);
		plugin2.setItems(visualizationPlugins);
		plugin3.setItems(visualizationPlugins);
	}

	@FXML
	void analyse1Clicked(ActionEvent event) throws AudioAnalyser.OutputNotReady, AudioAnalyser.IncorrectInputException, IOException, AudioAnalyser.LoadFailedException, AudioAnalyser.OutputAlreadyExists, AudioAnalyser.ParseOutputError, Chroma.WrongChromaSize {
		DataChart dataChart = createSelectedVisualization(plugin1, browse.getSelectionModel().getSelectedItem().getValue().toString());
		showChart(dataChart, barChart1, lineChart1, areaChart1);
	}

	@FXML
	void analyse2Clicked(ActionEvent event) throws AudioAnalyser.OutputNotReady, AudioAnalyser.IncorrectInputException, IOException, AudioAnalyser.LoadFailedException, AudioAnalyser.OutputAlreadyExists, AudioAnalyser.ParseOutputError, Chroma.WrongChromaSize {
		DataChart dataChart = createSelectedVisualization(plugin2, browse.getSelectionModel().getSelectedItem().getValue().toString());
		showChart(dataChart, barChart2, lineChart2, areaChart2);
	}

	@FXML
	void analyse3Clicked(ActionEvent event) throws AudioAnalyser.OutputNotReady, AudioAnalyser.IncorrectInputException, IOException, AudioAnalyser.LoadFailedException, AudioAnalyser.OutputAlreadyExists, AudioAnalyser.ParseOutputError, Chroma.WrongChromaSize {
		DataChart dataChart = createSelectedVisualization(plugin3, browse.getSelectionModel().getSelectedItem().getValue().toString());
		showChart(dataChart, barChart3, lineChart3, areaChart3);
	}

	@FXML
	void analyseAllClicked(ActionEvent event) throws AudioAnalyser.OutputNotReady, AudioAnalyser.IncorrectInputException, IOException, AudioAnalyser.LoadFailedException, AudioAnalyser.OutputAlreadyExists, AudioAnalyser.ParseOutputError, Chroma.WrongChromaSize {
		analyse1Clicked(event);
		analyse2Clicked(event);
		analyse3Clicked(event);
	}

	DataChart createSelectedVisualization(ChoiceBox plugin, String inputFile) throws AudioAnalyser.LoadFailedException, AudioAnalyser.IncorrectInputException, AudioAnalyser.OutputNotReady, AudioAnalyser.ParseOutputError, IOException, Chroma.WrongChromaSize, AudioAnalyser.OutputAlreadyExists {
		String pluginKey = plugin.getSelectionModel().getSelectedItem().toString();
		try {
			audioAnalyser.runAnalysis(inputFile, pluginKey, false, false);
		} catch (AudioAnalyser.OutputAlreadyExists e) {
			System.out.println("Output already exists. Continuing.");
		}

		return audioAnalyser.createDataChart(inputFile, pluginKey);
	}

	void showChart(DataChart dataChart, BarChart<String, Number> barChart, LineChart<Number, Number> lineChart, AreaChart<String, Number> areaChart) {
		switch(dataChart.type) {
			case "bar":
				lineChart.setVisible(false);
				areaChart.setVisible(false);
				barChart.setVisible(true);

				barChart.setTitle(dataChart.title);
				barChart.getXAxis().setLabel(dataChart.xLabel);
				barChart.getYAxis().setLabel(dataChart.yLabel);
				barChart.getData().clear();
				barChart.getData().addAll(dataChart.series1);
				break;
			case "line":
				barChart.setVisible(false);
				areaChart.setVisible(false);
				lineChart.setVisible(true);

				lineChart.setTitle(dataChart.title);
				lineChart.getXAxis().setLabel(dataChart.xLabel);
				lineChart.getYAxis().setLabel(dataChart.yLabel);
				lineChart.getData().clear();
				lineChart.getData().addAll(dataChart.series1);
				break;
			case "area":
				barChart.setVisible(false);
				lineChart.setVisible(false);
				areaChart.setVisible(true);

				areaChart.setTitle(dataChart.title);
				areaChart.getXAxis().setLabel(dataChart.xLabel);
				areaChart.getYAxis().setLabel(dataChart.yLabel);
				areaChart.getData().clear();
				areaChart.getData().addAll(
					dataChart.series1,
					dataChart.series2,
					dataChart.series3,
					dataChart.series4,
					dataChart.series5,
					dataChart.series6,
					dataChart.series7,
					dataChart.series8,
					dataChart.series9,
					dataChart.series10,
					dataChart.series11,
					dataChart.series12,
					dataChart.series13
				);
				break;
		}
	}
}
