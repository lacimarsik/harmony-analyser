package org.harmony_analyser.application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
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
	private BarChart<String, Number> barChart;

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
		DataChart dataChart = createSelectedVisualization(plugin1, "/mnt/work/school/mff/Ostrava/1_EXPERIMENTS/SecondHandSongsDataset/1_MSD_SHS_Preparation/392783.wav"/*browse.getSelectionModel().getSelectedItem().getValue().toString()*/);

		final CategoryAxis xAxis = new CategoryAxis();
		final NumberAxis yAxis = new NumberAxis();
		barChart.setTitle(dataChart.title);
		xAxis.setLabel(dataChart.xLabel);
		yAxis.setLabel(dataChart.yLabel);
		barChart.getData().clear();
		barChart.getData().addAll(dataChart.series1);
	}

	DataChart createSelectedVisualization(ChoiceBox plugin, String inputFile) throws AudioAnalyser.LoadFailedException, AudioAnalyser.IncorrectInputException, AudioAnalyser.OutputNotReady, AudioAnalyser.ParseOutputError, IOException, Chroma.WrongChromaSize, AudioAnalyser.OutputAlreadyExists {
		String pluginKey = "chord_analyser:average_chord_complexity_distance"; //plugin.getSelectionModel().getSelectedItem().toString();

		try {
			audioAnalyser.runAnalysis(inputFile, pluginKey, false, false);
		} catch (AudioAnalyser.OutputAlreadyExists e) {
			System.out.println("Output already exists. Continuing.");
		}

		return audioAnalyser.createDataChart(inputFile, pluginKey);
	}
}
