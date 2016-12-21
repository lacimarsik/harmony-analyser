package org.harmony_analyser.application;

import javafx.collections.*;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.control.*;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.scene.input.MouseEvent;

import org.harmony_analyser.jharmonyanalyser.chord_analyser.Chord;
import org.harmony_analyser.jharmonyanalyser.services.*;
import org.harmony_analyser.jharmonyanalyser.chord_analyser.*;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiUnavailableException;

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
	private ToggleButton record1;
	@FXML
	private Button play1;
	@FXML
	private ToggleButton record2;
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
	private TextArea complexity1;
	@FXML
	private TextArea complexity2;
	@FXML
	private TextArea ccdList;
	@FXML
	private TextArea functions2;
	@FXML
	private ListView<String> midiList;
	@FXML
	private TextArea ccd;
	@FXML
	private TextArea tps;
	@FXML
	private TextArea midiDeviceInfo;

	private MidiHandler midiHandler;
	private Chord chord1, chord2 = Chord.EMPTY_CHORD;

	@Override // This method is called by the FXMLLoader when initialization is complete
	public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
		midiHandler = new MidiHandler();
		midiHandler.initialize(
			MidiHandler.EMPTY_SEQUENCER,
			MidiHandler.EMPTY_SYNTHESIZER,
			MidiHandler.EMPTY_MIDI_DEVICE,
			MidiHandler.EMPTY_MIDI_DEVICE,
			MidiHandler.EMPTY_MIDI_DECODER
		);
		searchMidi();

		pitchesRelative1.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
			if (!newPropertyValue) {
				chord1 = Chordanal.createHarmonyFromRelativeTones(pitchesRelative1.getText());
				analyseHarmony(chord1, pitchesRelative1, pitchesAbsolute1, name1, structure1, functions1, complexity1);
				play1.setDisable(false);
				analyseTransition(chord1, chord2, ccdList, ccd, tps);
			}
		});

		// TODO: Redundant
		pitchesRelative2.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
			if (!newPropertyValue) {
				chord2 = Chordanal.createHarmonyFromRelativeTones(pitchesRelative2.getText());
				analyseHarmony(chord2, pitchesRelative2, pitchesAbsolute2, name2, structure2, functions2, complexity2);
				play2.setDisable(false);
				analyseTransition(chord1, chord2, ccdList, ccd, tps);
			}
		});

		// TODO: Redundant
		pitchesAbsolute1.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
			if (!newPropertyValue) {
				chord1 = Chordanal.createHarmonyFromTones(pitchesAbsolute1.getText());
				analyseHarmony(chord1, pitchesRelative1, pitchesAbsolute1, name1, structure1, functions1, complexity1);
				play1.setDisable(false);
				analyseTransition(chord1, chord2, ccdList, ccd, tps);
			}
		});

		// TODO: Redundant
		pitchesAbsolute2.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
			if (!newPropertyValue) {
				chord2 = Chordanal.createHarmonyFromTones(pitchesAbsolute2.getText());
				analyseHarmony(chord2, pitchesRelative2, pitchesAbsolute2, name2, structure2, functions2, complexity2);
				play2.setDisable(false);
				analyseTransition(chord1, chord2, ccdList, ccd, tps);
			}
		});
	}

	public void searchMidi() {
		ObservableList<String> inputDevices = FXCollections.observableArrayList(midiHandler.getInputDeviceList());
		midiList.setItems(inputDevices);

		record1.setDisable(false);
		record2.setDisable(false);
		record1.setSelected(false);
		record2.setSelected(false);
		play1.setDisable(true);
		play2.setDisable(true);
	}

	@FXML
	void searchForMidiDevices(ActionEvent event) {
		searchMidi();
	}

	@FXML
	void selectMidiDevice(MouseEvent event) {
		MidiDevice selectedDevice = midiHandler.getMidiDevice(midiList.getSelectionModel().getSelectedItem());

		midiDeviceInfo.setText("device: " + selectedDevice.getDeviceInfo().getName() +
			"\nvendor: " + selectedDevice.getDeviceInfo().getVendor().substring(0, selectedDevice.getDeviceInfo().getVendor().indexOf(" "))+
			"\ndescription: " + selectedDevice.getDeviceInfo().getDescription().substring(0, selectedDevice.getDeviceInfo().getDescription().indexOf(",")));

		midiHandler.close();
		midiHandler.initialize(
			MidiHandler.EMPTY_SEQUENCER,
			MidiHandler.EMPTY_SYNTHESIZER,
			selectedDevice,
			MidiHandler.EMPTY_MIDI_DEVICE,
			MidiHandler.EMPTY_MIDI_DECODER
		);
		midiHandler.connectInputSynthesizer();
	}

	@FXML
	void capture1(ActionEvent event) {
		try {
			if (record1.isSelected()) {
				midiHandler.connectInputDecoder();
				record2.setDisable(true);
			} else {
				midiHandler.closeInputDevice();
				chord1 = midiHandler.getBufferHarmony();
				analyseHarmony(chord1, pitchesRelative1, pitchesAbsolute1, name1, structure1, functions1, complexity1);
				play1.setDisable(false);
				analyseTransition(chord1, chord2, ccdList, ccd, tps);
				midiHandler.closeDecoder();
				midiHandler.openInputDevice();
				midiHandler.connectInputSynthesizer();
				record2.setDisable(false);
			}
		} catch (MidiUnavailableException e) {
			e.printStackTrace();
		}
	}

	// TODO: Redundant
	@FXML
	void capture2(ActionEvent event) {
		try {
			if (record2.isSelected()) {
				midiHandler.connectInputDecoder();
				record1.setDisable(true);
			} else {
				midiHandler.closeInputDevice();
				chord2 = midiHandler.getBufferHarmony();
				analyseHarmony(chord2, pitchesRelative2, pitchesAbsolute2, name2, structure2, functions2, complexity2);
				play2.setDisable(false);
				analyseTransition(chord1, chord2, ccdList, ccd, tps);
				midiHandler.closeDecoder();
				midiHandler.openInputDevice();
				midiHandler.connectInputSynthesizer();
				record1.setDisable(false);
			}
		} catch (MidiUnavailableException e) {
			e.printStackTrace();
		}
	}

	@FXML
	void playChord1(ActionEvent event) {
		midiHandler.play(chord1);
	}

	@FXML
	void playChord2(ActionEvent event) {
		midiHandler.play(chord2);
	}


	private void analyseHarmony(Chord chord, TextField txtRelative, TextField txtAbsolute, TextArea txtName, TextArea txtStructure, TextArea txtFunction, TextArea txtComplexity) {
		txtRelative.setText(chord.getToneNamesMapped());
		txtAbsolute.setText(chord.getToneNames());
		txtName.setText(Chordanal.getHarmonyAbbreviation(chord));
		txtStructure.setText(listToString(Chordanal.getHarmonyNamesRelative(chord)));
		txtFunction.setText(listToString(Harmanal.getRootsFormatted(chord)));
		txtComplexity.setText(Integer.toString(Harmanal.getHarmonyComplexity(chord)));
	}

	private void analyseTransition(Chord chord1, Chord chord2, TextArea txtTransition, TextArea txtTransitionComplexity, TextArea tpsDistance) {
		txtTransition.setText(listToString(Harmanal.getTransitionsFormatted(chord1, chord2)));
		txtTransitionComplexity.setText(Integer.toString(Harmanal.getTransitionComplexity(chord1, chord2)));

		// Get chord roots and keys
		Tone root1 = Chordanal.getRootTone(chord1);
		Tone root2 = Chordanal.getRootTone(chord2);
		Key key1 = new Key(root1.getNumber(), 0);
		Key key2 = new Key(root1.getNumber(), 0);
		tpsDistance.setText(Float.toString(TonalPitchSpace.getTPSDistance(chord1, root1, key1, chord2, root2, key2, false)));
	}

	/* Helpers */

	private String listToString(List<String> list) {
		String result = "";
		for (int i = 0; i < list.size(); i++) {
			if (i < list.size()-1) {
				result += list.get(i) + "\n";
			} else {
				result += list.get(i);
			}
		}
		return result;
	}
}

