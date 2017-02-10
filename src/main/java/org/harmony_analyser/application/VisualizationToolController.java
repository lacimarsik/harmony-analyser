package org.harmony_analyser.application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.*;
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

	private void performSelectedVisualization(ChoiceBox plugin, Pane parentPane, String inputFile) throws AudioAnalyser.LoadFailedException, AudioAnalyser.IncorrectInputException, AudioAnalyser.OutputNotReady, AudioAnalyser.ParseOutputError, IOException, Chroma.WrongChromaSize, AudioAnalyser.OutputAlreadyExists {
		String pluginKey = plugin.getSelectionModel().getSelectedItem().toString();

		audioAnalyser.runAnalysis(inputFile, pluginKey, false, false));

		createGraph(parentPane, inputFile, pluginKey);
	}

	private void createGraph(Pane parentPane, String inputFile, String pluginKey) throws AudioAnalyser.LoadFailedException, AudioAnalyser.OutputNotReady, IOException, AudioAnalyser.ParseOutputError {
		parentPane.getChildren().removeAll();
		DataChart dataChart = audioAnalyser.createDataChart(inputFile, pluginKey);
		dataChart.setPreferredSize(parentPane.getPreferredSize());
		dataChart.setBounds(parentPane.getBounds());
		parentPane.add(dataChart);
	}
}
