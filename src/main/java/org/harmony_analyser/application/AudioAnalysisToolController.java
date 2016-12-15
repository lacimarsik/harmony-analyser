package org.harmony_analyser.application;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

/**
 * Controller for Audio Analysis Tool Events
 */

public class AudioAnalysisToolController {
	@FXML
	private TreeView<?> browse;

	@FXML
	private TextArea console;

	@FXML
	private AnchorPane vampAvailable;

	@FXML
	private Label vampTitle;

	@FXML
	private Label vampDescription;

	@FXML
	private Button vampSettings;

	@FXML
	private Button vampAnalyse;

	@FXML
	private AnchorPane caAvailable;

	@FXML
	private Label caTitle;

	@FXML
	private Label caDescription;

	@FXML
	private Button caSettings;

	@FXML
	private Button caAnalyse;

	@FXML
	private AnchorPane chrAvailable;

	@FXML
	private Label chrTitle;

	@FXML
	private Label chrDescription;

	@FXML
	private Button chrSettings;

	@FXML
	private Button chrAnalyse;

	@FXML
	private AnchorPane ppAvailable;

	@FXML
	private Label ppTitle;

	@FXML
	private Label ppDescription;

	@FXML
	private Button ppSettings;

	@FXML
	private Button ppAnalyse;
}