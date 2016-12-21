package org.harmony_analyser.application;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import org.harmony_analyser.jharmonyanalyser.services.*;
import org.harmony_analyser.application.visualizations.*;
import org.harmony_analyser.jharmonyanalyser.chord_analyser.*;
import org.harmony_analyser.jharmonyanalyser.chroma_analyser.Chroma;

import javax.sound.midi.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.util.List;
import javafx.embed.swing.*;

/**
 * GUI for HarmonyAnalyser
 */

@SuppressWarnings("ConstantConditions")
class HarmonyAnalyser extends JFrame {
	private JTabbedPane tabbedPane;
	private JButton selectMidiButton;
	private JTextPane midiSelectionPane;
	private JButton playButtonOne;
	private JButton playButtonTwo;
	private JTextPane transitionPane;
	private JButton listPluginsButton;
	private JList<String> midiList;
	private JTextPane nameOnePane;
	private JTextPane nameTwoPane;
	private JTextPane structureOnePane;
	private JTextPane structureTwoPane;
	private JTextPane functionOnePane;
	private JTextPane functionTwoPane;
	private JTextPane relativeInputPaneOne;
	private JTextPane relativeInputPaneTwo;
	private JTextPane absoluteInputPaneOne;
	private JTextPane absoluteInputPaneTwo;
	private JTextPane chordComplexityPaneOne;
	private JTextPane chordComplexityPaneTwo;
	private JTextPane chordComplexityDistancePane;
	private JTextPane consoleTextPane;
	private JTextField inputFolderTextField;
	private JPanel rootPanel;
	private JCheckBox captureMIDICheckBoxOne;
	private JCheckBox captureMIDICheckBoxTwo;
	private JButton browseButton;
	private JButton extractChromasButton;
	private JButton extractChordLabelsButton;
	private JButton analyzeACCDButton;
	private JButton buttonNNLS;
	private JButton buttonChordino;
	private JScrollPane midiListScrollPane;
	private JPanel chordTransitionToolPanel;
	private JLabel midiListLabel;
	private JLabel chordSelectionLabel;
	private JLabel chordOneLabel;
	private JLabel chordTwoLabel;
	private JLabel midiInputLabel;
	private JLabel relativeInputLabel;
	private JLabel absoluteInputLabel;
	private JLabel nameLabel;
	private JLabel structureLabel;
	private JLabel functionLabel;
	private JLabel complexityLabel;
	private JScrollPane NameOneScrollPane;
	private JScrollPane nameTwoScrollPane;
	private JScrollPane structureOneScrollPane;
	private JScrollPane structureTwoScrollPane;
	private JScrollPane functionOneScrollPane;
	private JScrollPane functionTwoScrollPane;
	private JLabel resultsLabel;
	private JLabel chordComplexityDistanceLabel;
	private JScrollPane transitionScrollPane;
	private JLabel transitionLabel;
	private JScrollPane midiSelectionScrollPane;
	private JScrollPane relativeInputScrollPaneOne;
	private JScrollPane relativeInputScrollPaneTwo;
	private JScrollPane absoluteInputScrollPaneTwo;
	private JScrollPane absoluteInputScrollPaneOne;
	private JScrollPane chordComplexityScrollPaneOne;
	private JScrollPane chordComplexityScrollPaneTwo;
	private JScrollPane transitionComplexityScrollPane;
	private JPanel audioAnalysisPanel;
	private JLabel inputFolderLabel;
	private JLabel vampBatchLabel;
	private JLabel chordBatchLabel;
	private JLabel consoleOutputLabel;
	private JScrollPane consoleScrollPane;
	private JLabel batchProcessingLabel;
	private JButton accdButton;
	private JLabel pluginSettingsLabel;
	private JPanel visualizationPanel;
	private JLabel nnlsChromaVampLabel;
	private JLabel chordinoVampLabel;
	private JLabel transitionComplexityAudioLabel;
	private JLabel selectFileLabel;
	private JTextField selectFileTextField;
	private JButton browseButtonVisualization;
	private JComboBox<String> comboBoxOne;
	private JLabel selectPluginOneLabel;
	private JComboBox<String> comboBoxThree;
	private JLabel selectPluginTwoLabel;
	private JLabel selectPluginThreeLabel;
	private JButton runAnalysisButton;
	private JComboBox<String> comboBoxTwo;
	private JPanel drawPanel1;
	private JPanel drawPanel2;
	private JPanel drawPanel3;
	private JTextPane visualizationConsoleTextPane;
	private JScrollPane visualizationConsoleScrollPane;
	private JButton chromaSimpleButton;
	private JButton chromaTonalButton;
	private JButton chromaTransitionsSimpleButton;
	private JButton chromaTransitionsTonalButton;
	private JButton searchAgainButton;
	private JButton buttonKeyDetector;
	private JButton extractKeyButton;
	private JLabel keyDetectorVampLabel;
	private JButton extractChordTonesFromButton;
	private JButton ccdButton;
	private JButton tpsButton;
	private JLabel chromaSimpleLabel;
	private JLabel chromaTonalLabel;
	private JLabel chordAnalyserLabel;
	private JLabel chromaAnalyserLabel;
	private JLabel ccdLabel;
	private JButton analyseCCDButton;
	private JLabel chromaBatchLabel;
	private JLabel tpsLabel;
	private JButton analyseTPSButton;
	private JButton timeSeriesFilterButton;
	private JLabel timeSeriesFilterLabel;
	private JLabel filtersLabel;
	private JLabel filtersSettingsLabel;
	private JButton timeSeriesFilterSettingsButton;
	private JLabel extensionToFilterLabel;
	private JTextField extensionToFilterTextField;
	private JPanel chordTransitionTool;
	private JPanel audioAnalysisTool;
	private JPanel visualizationTool;
	private JFileChooser fileChooser;
	private final JFXPanel chordTransitionToolJFXPanel;
	private final JFXPanel audioAnalysisToolJFXPanel;
	private final JFXPanel visualizationToolJFXPanel;

	private Chord chord1, chord2 = Chord.EMPTY_CHORD;
	private final MidiHandler midiHandler;
	private final AudioAnalyser audioAnalyser;

	/* Public / Package methods */

	/**
	 * Launch the application.
	 */

	public static void main(String[] args) {
		new HarmonyAnalyser();
	}

	/* Private methods */

	/**
	 * Initialize the application.
	 */

	private HarmonyAnalyser() {
		/* GUI - Initialization */

		setContentPane(rootPanel);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Chord Analyser 1.2-beta");
		pack();
		setVisible(true);

		chordTransitionToolJFXPanel = new JFXPanel();
		audioAnalysisToolJFXPanel = new JFXPanel();
		visualizationToolJFXPanel = new JFXPanel();
		System.setProperty( "javafx.userAgentStylesheetUrl", "CASPIAN" );
		Platform.runLater(() -> {
			Parent chordTransitionToolRoot, audioAnalysisToolRoot, visualizationToolRoot;
			try {
				ClassLoader classLoader = getClass().getClassLoader();
				chordTransitionToolRoot = FXMLLoader.load(classLoader.getResource("ChordTransitionTool.fxml"));
				audioAnalysisToolRoot = FXMLLoader.load(classLoader.getResource("AudioAnalysisTool.fxml"));
				visualizationToolRoot = FXMLLoader.load(classLoader.getResource("VisualizationTool.fxml"));
				Scene chordTransitionToolScene = new Scene(chordTransitionToolRoot);
				Scene audioAnalysisToolScene = new Scene(audioAnalysisToolRoot);
				Scene visualizationToolScene = new Scene(visualizationToolRoot);
				chordTransitionToolJFXPanel.setScene(chordTransitionToolScene);
				audioAnalysisToolJFXPanel.setScene(audioAnalysisToolScene);
				visualizationToolJFXPanel.setScene(visualizationToolScene);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		});

		chordTransitionTool.removeAll();
		chordTransitionTool.setLayout(new GridLayout());
		chordTransitionTool.add(chordTransitionToolJFXPanel);
		chordTransitionTool.revalidate();

		audioAnalysisTool.removeAll();
		audioAnalysisTool.setLayout(new GridLayout());
		audioAnalysisTool.add(audioAnalysisToolJFXPanel);
		audioAnalysisTool.revalidate();

		visualizationTool.removeAll();
		visualizationTool.setLayout(new GridLayout());
		visualizationTool.add(visualizationToolJFXPanel);
		visualizationTool.revalidate();

		/* Services and Visualizations - Initialization */

		AnalysisFactory analysisFactory = new AnalysisFactory();
		DrawPanelFactory drawPanelFactory = new DrawPanelFactory();
		audioAnalyser = new AudioAnalyser(analysisFactory, drawPanelFactory);
		midiHandler = new MidiHandler();
		midiHandler.initialize(
			MidiHandler.EMPTY_SEQUENCER,
			MidiHandler.EMPTY_SYNTHESIZER,
			MidiHandler.EMPTY_MIDI_DEVICE,
			MidiHandler.EMPTY_MIDI_DEVICE,
			MidiHandler.EMPTY_MIDI_DECODER
		);

		/* Chord Transition Tool - Initialization */

		searchForMidiDevices();

		selectMidiButton.addActionListener(actionEvent -> {
			MidiDevice selectedDevice = midiHandler.getMidiDevice(midiList.getSelectedValue());
			midiSelectionPane.setText("device: " + selectedDevice.getDeviceInfo().getName() +
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

			captureMIDICheckBoxOne.setEnabled(true);
			captureMIDICheckBoxTwo.setEnabled(true);
			captureMIDICheckBoxOne.setSelected(false);
			captureMIDICheckBoxTwo.setSelected(false);
			playButtonOne.setEnabled(false);
			playButtonTwo.setEnabled(false);
		});

		searchAgainButton.addActionListener(actionEvent -> searchForMidiDevices());

		captureMIDICheckBoxOne.addActionListener(actionEvent -> {
			try {
				if (captureMIDICheckBoxOne.isSelected()) {
					midiHandler.connectInputDecoder();
					captureMIDICheckBoxTwo.setEnabled(false);
				} else {
					midiHandler.closeInputDevice();
					chord1 = midiHandler.getBufferHarmony();
					analyzeHarmony(chord1, relativeInputPaneOne, absoluteInputPaneOne, nameOnePane, structureOnePane, functionOnePane, chordComplexityPaneOne);
					playButtonOne.setEnabled(true);
					analyzeTransition(chord1, chord2, transitionPane, chordComplexityDistancePane);
					midiHandler.closeDecoder();
					midiHandler.openInputDevice();
					midiHandler.connectInputSynthesizer();
					captureMIDICheckBoxTwo.setEnabled(true);
				}
			} catch (MidiUnavailableException e) {
				e.printStackTrace();
			}
		});

		captureMIDICheckBoxTwo.addActionListener(actionEvent -> {
			try {
				if (captureMIDICheckBoxTwo.isSelected()) {
					midiHandler.connectInputDecoder();
					captureMIDICheckBoxOne.setEnabled(false);
				} else {
					midiHandler.closeInputDevice();
					chord2 = midiHandler.getBufferHarmony();
					analyzeHarmony(chord2, relativeInputPaneTwo, absoluteInputPaneTwo, nameTwoPane, structureTwoPane, functionTwoPane, chordComplexityPaneTwo);
					playButtonTwo.setEnabled(true);
					analyzeTransition(chord1, chord2, transitionPane, chordComplexityDistancePane);
					midiHandler.closeDecoder();
					midiHandler.openInputDevice();
					midiHandler.connectInputSynthesizer();
					captureMIDICheckBoxOne.setEnabled(true);
				}
			} catch (MidiUnavailableException e) {
				e.printStackTrace();
			}
		});

		relativeInputPaneOne.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				super.focusLost(e);
				chord1 = Chordanal.createHarmonyFromRelativeTones(relativeInputPaneOne.getText());
				analyzeHarmony(chord1, relativeInputPaneOne, absoluteInputPaneOne, nameOnePane, structureOnePane, functionOnePane, chordComplexityPaneOne);
				playButtonOne.setEnabled(true);
					analyzeTransition(chord1, chord2, transitionPane, chordComplexityDistancePane);
			}
		});

		absoluteInputPaneOne.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				super.focusLost(e);
				chord1 = Chordanal.createHarmonyFromTones(absoluteInputPaneOne.getText());
				analyzeHarmony(chord1, relativeInputPaneOne, absoluteInputPaneOne, nameOnePane, structureOnePane, functionOnePane, chordComplexityPaneOne);
				playButtonOne.setEnabled(true);
				analyzeTransition(chord1, chord2, transitionPane, chordComplexityDistancePane);
			}
		});

		relativeInputPaneTwo.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				super.focusLost(e);
				chord2 = Chordanal.createHarmonyFromRelativeTones(relativeInputPaneTwo.getText());
				analyzeHarmony(chord2, relativeInputPaneTwo, absoluteInputPaneTwo, nameTwoPane, structureTwoPane, functionTwoPane, chordComplexityPaneTwo);
				playButtonTwo.setEnabled(true);
				analyzeTransition(chord1, chord2, transitionPane, chordComplexityDistancePane);
			}
		});

		absoluteInputPaneTwo.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				super.focusLost(e);
				chord2 = Chordanal.createHarmonyFromTones(absoluteInputPaneTwo.getText());
				analyzeHarmony(chord2, relativeInputPaneTwo, absoluteInputPaneTwo, nameTwoPane, structureTwoPane, functionTwoPane, chordComplexityPaneTwo);
				playButtonTwo.setEnabled(true);
				analyzeTransition(chord1, chord2, transitionPane, chordComplexityDistancePane);
			}
		});

		playButtonOne.addActionListener(actionEvent -> midiHandler.play(chord1));

		playButtonTwo.addActionListener(actionEvent -> midiHandler.play(chord2));

		/* Audio Analysis Tool - Initialization */

		consoleTextPane.setText(consoleTextPane.getText() + "\n");

		listPluginsButton.addActionListener(actionEvent -> {
			try {
				consoleTextPane.setText(consoleTextPane.getText() + audioAnalyser.printPlugins());
			} catch (Exception e) {
				consoleTextPane.setText(e.getMessage());
			}
		});

		buttonNNLS.addActionListener(actionEvent -> {
			try {
				consoleTextPane.setText(consoleTextPane.getText() + audioAnalyser.printParameters("nnls-chroma:nnls-chroma"));
			} catch (AudioAnalyser.LoadFailedException e) {
				e.printStackTrace();
			}
		});

		buttonChordino.addActionListener(actionEvent -> {
			try {
				consoleTextPane.setText(consoleTextPane.getText() + audioAnalyser.printParameters("nnls-chroma:chordino-labels"));
			} catch (AudioAnalyser.LoadFailedException e) {
				e.printStackTrace();
			}
		});

		buttonKeyDetector.addActionListener(actionEvent -> {
			try {
				consoleTextPane.setText(consoleTextPane.getText() + audioAnalyser.printParameters("qm-vamp-plugins:qm-keydetector"));
			} catch (AudioAnalyser.LoadFailedException e) {
				e.printStackTrace();
			}
		});

		ccdButton.addActionListener(actionEvent -> {
			try {
				consoleTextPane.setText(consoleTextPane.getText() + audioAnalyser.printParameters("chord_analyser:chord_complexity_distance"));
			} catch (AudioAnalyser.LoadFailedException e) {
				e.printStackTrace();
			}
		});

		accdButton.addActionListener(actionEvent -> {
			try {
				consoleTextPane.setText(consoleTextPane.getText() + audioAnalyser.printParameters("chord_analyser:average_chord_complexity_distance"));
			} catch (AudioAnalyser.LoadFailedException e) {
				e.printStackTrace();
			}
		});

		tpsButton.addActionListener(actionEvent -> {
			try {
				consoleTextPane.setText(consoleTextPane.getText() + audioAnalyser.printParameters("chord_analyser:tps_distance"));
			} catch (AudioAnalyser.LoadFailedException e) {
				e.printStackTrace();
			}
		});

		chromaSimpleButton.addActionListener(actionEvent -> {
			try {
				consoleTextPane.setText(consoleTextPane.getText() + audioAnalyser.printParameters("chroma_analyser:chroma_complexity_simple"));
			} catch (AudioAnalyser.LoadFailedException e) {
				e.printStackTrace();
			}
		});

		chromaTonalButton.addActionListener(actionEvent -> {
			try {
				consoleTextPane.setText(consoleTextPane.getText() + audioAnalyser.printParameters("chroma_analyser:complexity_difference"));
			} catch (AudioAnalyser.LoadFailedException e) {
				e.printStackTrace();
			}
		});

		timeSeriesFilterSettingsButton.addActionListener(actionEvent -> {
			try {
				consoleTextPane.setText(consoleTextPane.getText() + audioAnalyser.printParameters("filters:time_series"));
			} catch (AudioAnalyser.LoadFailedException e) {
				e.printStackTrace();
			}
		});

		browseButton.addActionListener(actionEvent -> {
			fileChooser = new JFileChooser();
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			if (fileChooser.showOpenDialog(rootPanel) == JFileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();
				consoleTextPane.setText(consoleTextPane.getText() + "\n\n> Selected directory: " + file.getAbsolutePath());
				inputFolderTextField.setText(file.getAbsolutePath());
			}
		});

		extractChromasButton.addActionListener(actionEvent -> analyzeFolder(consoleTextPane, inputFolderTextField, "nnls-chroma:nnls-chroma", ".wav"));

		extractChordLabelsButton.addActionListener(actionEvent -> analyzeFolder(consoleTextPane, inputFolderTextField, "nnls-chroma:chordino-labels", ".wav"));

		extractChordTonesFromButton.addActionListener(actionEvent -> analyzeFolder(consoleTextPane, inputFolderTextField, "nnls-chroma:chordino-tones", ".wav"));

		extractKeyButton.addActionListener(actionEvent -> analyzeFolder(consoleTextPane, inputFolderTextField, "qm-vamp-plugins:qm-keydetector", ".wav"));

		analyseCCDButton.addActionListener(actionEvent -> {
			analyzeFolder(consoleTextPane, inputFolderTextField, "nnls-chroma:nnls-chroma", ".wav");
			analyzeFolder(consoleTextPane, inputFolderTextField, "nnls-chroma:chordino-labels", ".wav");
			analyzeFolder(consoleTextPane, inputFolderTextField, "chord_analyser:chord_complexity_distance", ".wav");
		});

		analyzeACCDButton.addActionListener(actionEvent -> {
			analyzeFolder(consoleTextPane, inputFolderTextField, "nnls-chroma:nnls-chroma", ".wav");
			analyzeFolder(consoleTextPane, inputFolderTextField, "nnls-chroma:chordino-labels", ".wav");
			analyzeFolder(consoleTextPane, inputFolderTextField, "chord_analyser:average_chord_complexity_distance", ".wav");
		});

		analyseTPSButton.addActionListener(actionEvent -> {
			analyzeFolder(consoleTextPane, inputFolderTextField, "nnls-chroma:nnls-chroma", ".wav");
			analyzeFolder(consoleTextPane, inputFolderTextField, "nnls-chroma:chordino-tones", ".wav");
			analyzeFolder(consoleTextPane, inputFolderTextField, "chord_analyser:tps_distance", ".wav");
		});

		chromaTransitionsSimpleButton.addActionListener(actionEvent -> {
			analyzeFolder(consoleTextPane, inputFolderTextField, "nnls-chroma:nnls-chroma", ".wav");
			analyzeFolder(consoleTextPane, inputFolderTextField, "chroma_analyser:simple_difference", ".wav");
		});

		chromaTransitionsTonalButton.addActionListener(actionEvent -> {
			analyzeFolder(consoleTextPane, inputFolderTextField, "nnls-chroma:nnls-chroma", ".wav");
			analyzeFolder(consoleTextPane, inputFolderTextField, "chroma_analyser:complexity_difference", ".wav");
		});

		timeSeriesFilterButton.addActionListener(actionEvent -> {
			String extension = extensionToFilterTextField.getText();
			if (extension.equals("")) {
				consoleTextPane.setText(consoleTextPane.getText() + "\n\n> Extension for filtering not specified. Please enter extension.");
			} else {
				analyzeFolder(consoleTextPane, inputFolderTextField, "filters:time_series", extension);
			}
		});

		/* Visualization Tool - Initialization */

		visualizationConsoleTextPane.setText(visualizationConsoleTextPane.getText() + "\n");

		String[] visualizationPlugins = audioAnalyser.getAllVisualizations();
		for (String pluginName : visualizationPlugins) {
			comboBoxOne.addItem(pluginName);
		}
		for (String pluginName : visualizationPlugins) {
			comboBoxTwo.addItem(pluginName);
		}
		for (String pluginName : visualizationPlugins) {
			comboBoxThree.addItem(pluginName);
		}

		browseButtonVisualization.addActionListener(actionEvent -> {
			fileChooser = new JFileChooser();
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			if (fileChooser.showOpenDialog(rootPanel) == JFileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();
				visualizationConsoleTextPane.setText(visualizationConsoleTextPane.getText() + "\n\n> Selected file: " + file.getAbsolutePath());
				selectFileTextField.setText(file.getAbsolutePath());
			}
		});

		runAnalysisButton.addActionListener(actionEvent -> {
			visualizationConsoleTextPane.setText(visualizationConsoleTextPane.getText() + "\n> Running analysis ...");
			if (selectFileTextField.getText().equals("")) {
				visualizationConsoleTextPane.setText(visualizationConsoleTextPane.getText() + "\nERROR: Input file not specifed!");
				return;
			}

			try {
				File file = new File(selectFileTextField.getText());

				performSelectedVisualization(comboBoxOne, drawPanel1, file.toString(), visualizationConsoleTextPane);
				performSelectedVisualization(comboBoxTwo, drawPanel2, file.toString(), visualizationConsoleTextPane);
				performSelectedVisualization(comboBoxThree, drawPanel3, file.toString(), visualizationConsoleTextPane);
			} catch (AudioAnalyser.IncorrectInputException | AudioAnalyser.LoadFailedException | AudioAnalyser.OutputNotReady | IOException e) {
				visualizationConsoleTextPane.setText(visualizationConsoleTextPane.getText() + "\nERROR: " + e.getMessage());
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	/* Chord Transition Tool - Handling methods */

	// Obtain information about all the installed input MIDI devices
	private void searchForMidiDevices() {
		String[] inputDevices = midiHandler.getInputDeviceList();
		if (inputDevices[0].equals("No MIDI devices found")) {
			midiList.setEnabled(false);
		} else {
			midiList.addListSelectionListener(listSelectionEvent -> selectMidiButton.setEnabled(true));
			midiList.setEnabled(true);
		}
		midiList.setListData(inputDevices);
	}

	private void analyzeHarmony(Chord chord, JTextPane txtRelative, JTextPane txtAbsolute, JTextPane txtName, JTextPane txtStructure, JTextPane txtFunction, JTextPane txtComplexity) {
		txtRelative.setText(chord.getToneNamesMapped());
		txtRelative.setCaretPosition(0);
		txtAbsolute.setText(chord.getToneNames());
		txtAbsolute.setCaretPosition(0);
		txtName.setText(Chordanal.getHarmonyAbbreviation(chord));
		txtName.setCaretPosition(0);
		txtStructure.setText(listToString(Chordanal.getHarmonyNamesRelative(chord)));
		txtStructure.setCaretPosition(0);
		txtFunction.setText(listToString(Harmanal.getRootsFormatted(chord)));
		txtFunction.setCaretPosition(0);
		txtComplexity.setText(Integer.toString(Harmanal.getHarmonyComplexity(chord)));
		txtComplexity.setCaretPosition(0);
	}

	private void analyzeTransition(Chord chord1, Chord chord2, JTextPane txtTransition, JTextPane txtTransitionComplexity) {
		txtTransition.setText(listToString(Harmanal.getTransitionsFormatted(chord1, chord2)));
		txtTransition.setCaretPosition(0);
		txtTransitionComplexity.setText(Integer.toString(Harmanal.getTransitionComplexity(chord1, chord2)));
	}

	/* Audio Analysis Tool - Handling methods */

	private void analyzeFolder(JTextPane consolePane, JTextField inputFolderTextField, String pluginKey, String suffixAndExtension) {
		try {
			consolePane.setText(consolePane.getText() + "\n\n> Analyzing input folder using plugin: " + pluginKey);
			if (inputFolderTextField.getText().equals("")) {
				consolePane.setText(consolePane.getText() + "\nERROR: Input folder not specifed!");
				return;
			}
			Path startPath = Paths.get(inputFolderTextField.getText());
			Files.walkFileTree(startPath, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
					consolePane.setText(consolePane.getText() + "\nDir: " + dir.toString());
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
					if (file.toString().endsWith(suffixAndExtension)) {
						consolePane.setText(consolePane.getText() + "\nProcessing: " + file.toString() + "\n");

						try {
							String analysisResult = audioAnalyser.runAnalysis(file.toString(), pluginKey, true, false);
							consolePane.setText(consolePane.getText() + "\n" + analysisResult);
						} catch (AudioAnalyser.IncorrectInputException | AudioAnalyser.LoadFailedException e) {
							consolePane.setText(consolePane.getText() + "\nERROR: " + e.getMessage());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFileFailed(Path file, IOException e) {
					return FileVisitResult.CONTINUE;
				}
			});
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	/* Visualization Tool - Handling methods */

	private void performSelectedVisualization(JComboBox comboBox, JPanel parentPanel, String inputFile, JTextPane consoleTextPane) throws AudioAnalyser.LoadFailedException, AudioAnalyser.IncorrectInputException, AudioAnalyser.OutputNotReady, IOException, Chroma.WrongChromaSize, AudioAnalyser.ParseOutputError {
		String pluginKey = comboBox.getSelectedItem().toString();

		try {
			consoleTextPane.setText(consoleTextPane.getText() + audioAnalyser.runAnalysis(inputFile, pluginKey, false, false));
		} catch (AudioAnalyser.OutputAlreadyExists e) {
			consoleTextPane.setText(consoleTextPane.getText() + "\nINFO: " + e.getMessage());
		}
		createGraph(parentPanel, inputFile, pluginKey);
	}

	private void createGraph(JPanel parentPanel, String inputFile, String pluginKey) throws AudioAnalyser.LoadFailedException, AudioAnalyser.OutputNotReady, IOException, AudioAnalyser.ParseOutputError {
		parentPanel.removeAll();
		parentPanel.setLayout(new GridLayout());
		DrawPanel drawPanel = audioAnalyser.createDrawPanel(inputFile, pluginKey);
		drawPanel.setPreferredSize(parentPanel.getPreferredSize());
		drawPanel.setBounds(parentPanel.getBounds());
		parentPanel.add(drawPanel);
		parentPanel.revalidate();
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
