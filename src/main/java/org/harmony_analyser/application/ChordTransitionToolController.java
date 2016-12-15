package org.harmony_analyser.application;

import javafx.fxml.*;
import javafx.scene.control.*;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for Chord Transition Tool Events
 */

public class ChordTransitionToolController implements Initializable {
	@FXML
	private Button midiListRefresh;

	@FXML
	private ToggleButton chordMode;

	@FXML
	private ToggleButton chromaMode;

	@FXML
	private Slider keyboardSensitivity;

	@FXML
	private TextField SensitivityValue;

	@FXML
	private Button record1;

	@FXML
	private Button play1;

	@FXML
	private Button record2;

	@FXML
	private Button play2;

	@FXML
	private TextArea name1;

	@FXML
	private TextArea name2;

	@FXML
	private TextField pitchesRelative1;

	@FXML
	private TextField pitchesRelative2;

	@FXML
	private TextField pitchesAbsolute1;

	@FXML
	private TextField pitchesAbsolute2;

	@FXML
	private TextArea functions1;

	@FXML
	private TextArea structure1;

	@FXML
	private TextArea structure2;

	@FXML
	private TextArea ccdList;

	@FXML
	private TextArea functions2;

	@FXML
	private TextArea midiList;

	@FXML
	private TextArea ccd;

	@FXML
	private TextArea tps;

	@FXML
	private TextArea tonnetz;

	@Override // This method is called by the FXMLLoader when initialization is complete
	public void initialize(URL fxmlFileLocation, ResourceBundle resources) {

	}
}

