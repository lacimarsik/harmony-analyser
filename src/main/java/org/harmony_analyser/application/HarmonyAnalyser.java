package org.harmony_analyser.application;

import org.harmony_analyser.application.services.*;
import org.harmony_analyser.chordanal.*;
import org.harmony_analyser.plugins.AnalysisPlugin;

import javax.sound.midi.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.util.*;

/**
 * GUI for HarmonyAnalyser
 */

class HarmonyAnalyser extends JFrame {
	private JTabbedPane tabbedPane;
	private JButton selectMidiButton;
	private JTextPane midiSelectionPane;
	private JButton playButtonOne;
	private JButton playButtonTwo;
	private JTextPane transitionPane;
	private JButton loadPluginsButton;
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
	private JTextPane transitionComplexityPane;
	private JTextPane consolePane;
	private JTextField inputFolderTextField;
	private JPanel rootPanel;
	private JCheckBox captureMIDICheckBoxOne;
	private JCheckBox captureMIDICheckBoxTwo;
	private JButton browseButton;
	private JButton extractChromasButton;
	private JButton segmentTrackButton;
	private JButton analyzeComplexityButton;
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
	private JLabel transitionComplexityLabel;
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
	private JLabel stepOneLabel;
	private JLabel stepThreeLabel;
	private JLabel stepTwoButton;
	private JLabel consoleOutputLabel;
	private JScrollPane consoleScrollPane;
	private JLabel batchProcessingLabel;
	private JButton buttonComplexity;
	private JFileChooser fileChooser;

	private Harmony harmony1,harmony2 = null;

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
		pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		setTitle("Harmony Analyser");

		/* Chord Transition Tool - Initialization */

		// Obtain information about all the installed input MIDI devices
		final MidiHandler midiHandler = new MidiHandler();
		String[] inputDevices = midiHandler.getInputDeviceList();
		midiList.setListData(inputDevices);

		midiList.addListSelectionListener(listSelectionEvent -> selectMidiButton.setEnabled(true));

		selectMidiButton.addActionListener(actionEvent -> {
			MidiDevice selectedDevice = midiHandler.getMidiDevice(midiList.getSelectedValue());
			midiSelectionPane.setText("device: " + selectedDevice.getDeviceInfo().getName() +
				"\nvendor: " + selectedDevice.getDeviceInfo().getVendor().substring(0, selectedDevice.getDeviceInfo().getVendor().indexOf(" "))+
				"\ndescription: " + selectedDevice.getDeviceInfo().getDescription().substring(0, selectedDevice.getDeviceInfo().getDescription().indexOf(",")));

			midiHandler.close();
			midiHandler.initialize(null, null, selectedDevice, null, null);
			midiHandler.connectInputSynthesizer();

			captureMIDICheckBoxOne.setEnabled(true);
			captureMIDICheckBoxTwo.setEnabled(true);
			captureMIDICheckBoxOne.setSelected(false);
			captureMIDICheckBoxTwo.setSelected(false);
			playButtonOne.setEnabled(false);
			playButtonTwo.setEnabled(false);
		});

		captureMIDICheckBoxOne.addActionListener(actionEvent -> {
			try {
				if (captureMIDICheckBoxOne.isSelected()) {
					midiHandler.connectInputDecoder();
					captureMIDICheckBoxTwo.setEnabled(false);
				} else {
					midiHandler.closeInputDevice();
					harmony1 = midiHandler.getBufferHarmony();
					if (harmony1 != null) {
						analyzeHarmony(harmony1, relativeInputPaneOne, absoluteInputPaneOne, nameOnePane, structureOnePane, functionOnePane, chordComplexityPaneOne);
						playButtonOne.setEnabled(true);
						if (harmony2 != null) {
							analyzeTransition(harmony1,harmony2, transitionPane, transitionComplexityPane);
						}
					}
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
					harmony2 = midiHandler.getBufferHarmony();
					if (harmony2 != null) {
						analyzeHarmony(harmony2, relativeInputPaneTwo, absoluteInputPaneTwo, nameTwoPane, structureTwoPane, functionTwoPane, chordComplexityPaneTwo);
						playButtonTwo.setEnabled(true);
						if (harmony1 != null) {
							analyzeTransition(harmony1,harmony2, transitionPane, transitionComplexityPane);
						}
					}
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
				harmony1 = Chordanal.createHarmonyFromRelativeTones(relativeInputPaneOne.getText());
				if (harmony1 != null) {
					analyzeHarmony(harmony1, relativeInputPaneOne, absoluteInputPaneOne, nameOnePane, structureOnePane, functionOnePane, chordComplexityPaneOne);
					playButtonOne.setEnabled(true);
					if (harmony2 != null) {
						analyzeTransition(harmony1,harmony2, transitionPane, transitionComplexityPane);
					}
				} else {
					relativeInputPaneOne.setText("");
					absoluteInputPaneOne.setText("");
				}
			}
		});

		absoluteInputPaneOne.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				super.focusLost(e);
				harmony1 = Chordanal.createHarmonyFromTones(absoluteInputPaneOne.getText());
				if (harmony1 != null) {
					analyzeHarmony(harmony1, relativeInputPaneOne, absoluteInputPaneOne, nameOnePane, structureOnePane, functionOnePane, chordComplexityPaneOne);
					playButtonOne.setEnabled(true);
					if (harmony2 != null) {
						analyzeTransition(harmony1,harmony2, transitionPane, transitionComplexityPane);
					}
				} else {
					relativeInputPaneOne.setText("");
					absoluteInputPaneOne.setText("");
				}
			}
		});

		relativeInputPaneTwo.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				super.focusLost(e);
				harmony2 = Chordanal.createHarmonyFromRelativeTones(relativeInputPaneTwo.getText());
				if (harmony2 != null) {
					analyzeHarmony(harmony2, relativeInputPaneTwo, absoluteInputPaneTwo, nameTwoPane, structureTwoPane, functionTwoPane, chordComplexityPaneTwo);
					playButtonTwo.setEnabled(true);
					if (harmony1 != null) {
						analyzeTransition(harmony1,harmony2, transitionPane, transitionComplexityPane);
					}
				} else {
					relativeInputPaneTwo.setText("");
					absoluteInputPaneTwo.setText("");
				}
			}
		});

		absoluteInputPaneTwo.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				super.focusLost(e);
				harmony2 = Chordanal.createHarmonyFromTones(absoluteInputPaneTwo.getText());
				if (harmony2 != null) {
					analyzeHarmony(harmony2, relativeInputPaneTwo, absoluteInputPaneTwo, nameTwoPane, structureTwoPane, functionTwoPane, chordComplexityPaneTwo);
					playButtonTwo.setEnabled(true);
					if (harmony1 != null) {
						analyzeTransition(harmony1,harmony2, transitionPane, transitionComplexityPane);
					}
				} else {
					relativeInputPaneTwo.setText("");
					absoluteInputPaneTwo.setText("");
				}
			}
		});

		playButtonOne.addActionListener(actionEvent -> midiHandler.play(harmony1));

		playButtonTwo.addActionListener(actionEvent -> midiHandler.play(harmony2));

		/* Audio Analysis Tool - Initialization */

		loadPluginsButton.addActionListener(actionEvent -> {
			try {
				consolePane.setText(consolePane.getText() + AudioAnalyser.printPlugins());
			} catch (Exception e) {
				consolePane.setText(e.getMessage());
			}
		});

		buttonNNLS.addActionListener(actionEvent -> {
			try {
				consolePane.setText(consolePane.getText() + AudioAnalyser.printParameters("nnls-chroma:nnls-chroma"));
			} catch (AudioAnalyser.LoadFailedException e) {
				e.printStackTrace();
			}
		});

		buttonChordino.addActionListener(actionEvent -> {
			try {
				consolePane.setText(consolePane.getText() + AudioAnalyser.printParameters("nnls-chroma:chordino"));
			} catch (AudioAnalyser.LoadFailedException e) {
				e.printStackTrace();
			}
		});

		buttonComplexity.addActionListener(actionEvent -> {
			try {
				consolePane.setText(consolePane.getText() + AudioAnalyser.printParameters("harmanal:transition_complexity"));
			} catch (AudioAnalyser.LoadFailedException e) {
				e.printStackTrace();
			}
		});

		browseButton.addActionListener(actionEvent -> {
			fileChooser = new JFileChooser();
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			if (fileChooser.showOpenDialog(rootPanel) == JFileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();
				consolePane.setText(consolePane.getText() + "\n\n> Selected directory: " + file.getAbsolutePath());
				inputFolderTextField.setText(file.getAbsolutePath());
			}
		});

		extractChromasButton.addActionListener(actionEvent -> {
		try {
			consolePane.setText(consolePane.getText() + "\n\n> Step 1: Extracting chromas from audio files");
			Path startPath = Paths.get(inputFolderTextField.getText());
			Files.walkFileTree(startPath, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult preVisitDirectory(Path dir,
					BasicFileAttributes attrs) {
					consolePane.setText(consolePane.getText() + "\nDir: " + dir.toString());
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {

					if (file.toString().endsWith(".wav")) {
						consolePane.setText(consolePane.getText() + "\nAnalyzing: " + file.toString() + "\n");
						try {
							List<String> inputFiles = new ArrayList<>();
							inputFiles.add(file.toString());
							String analysisResult = new AudioAnalyser().runAnalysis(inputFiles, file.toString() + "-chromas.txt", "nnls-chroma:nnls-chroma");
							consolePane.setText(consolePane.getText() + "\n" + analysisResult);
						} catch (AnalysisPlugin.IncorrectInputException | AudioAnalyser.LoadFailedException e) {
							consolePane.setText(consolePane.getText() + "ERROR: " + e.getMessage());
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
		});

		segmentTrackButton.addActionListener(actionEvent -> {
			try {
				consolePane.setText(consolePane.getText() + "\n\n> Step 2: Segmenting audio tracks from given chroma files");
				Path startPath = Paths.get(inputFolderTextField.getText());
				Files.walkFileTree(startPath, new SimpleFileVisitor<Path>() {
					@Override
					public FileVisitResult preVisitDirectory(Path dir,
						BasicFileAttributes attrs) {
						consolePane.setText(consolePane.getText() + "\nDir: " + dir.toString());
						return FileVisitResult.CONTINUE;
					}

					@Override
					public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
						if (file.toString().endsWith(".wav")) {
							consolePane.setText(consolePane.getText() + "\nSegmenting: " + file.toString() + "\n");
							try {
								List<String> inputFiles = new ArrayList<>();
								inputFiles.add(file.toString());
								String analysisResult = new AudioAnalyser().runAnalysis(inputFiles, file.toString() + "-segmentation.txt", "nnls-chroma:chordino");
								consolePane.setText(consolePane.getText() + "\n" + analysisResult);
							} catch (AnalysisPlugin.IncorrectInputException | AudioAnalyser.LoadFailedException e) {
								consolePane.setText(consolePane.getText() + "ERROR: " + e.getMessage());
							} catch (Exception e) {
								e.printStackTrace();
							}

							consolePane.setText(consolePane.getText() + "\nSegmentation saved in: " + file.toString() + "-segmentation.txt\n");
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
		});

		analyzeComplexityButton.addActionListener(actionEvent -> {
			try {
				consolePane.setText(consolePane.getText() + "\n\n> Step 3: Analyzing complexity for audio files");
				Path startPath = Paths.get(inputFolderTextField.getText());
				Files.walkFileTree(startPath, new SimpleFileVisitor<Path>() {
					@Override
					public FileVisitResult preVisitDirectory(Path dir,
							BasicFileAttributes attrs) {
						consolePane.setText(consolePane.getText() + "\nDir: " + dir.toString());
						return FileVisitResult.CONTINUE;
					}

					@Override
					public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
						if (file.toString().endsWith(".wav")) {
							consolePane.setText(consolePane.getText() + "\nAnalyzing: " + file.toString() + "\n");

							List<String> inputFiles = new ArrayList<>();
							inputFiles.add(file.toString() + "-chromas.txt");
							inputFiles.add(file.toString() + "-segmentation.txt");
							try {
								String analysisResult = new AudioAnalyser().runAnalysis(inputFiles, file.toString() + "-report.txt", "harmanal:transition_complexity");
								consolePane.setText(consolePane.getText() + "\n" + analysisResult);
							} catch (AnalysisPlugin.IncorrectInputException | AudioAnalyser.LoadFailedException e) {
								consolePane.setText(consolePane.getText() + "ERROR: " + e.getMessage());
							} catch (Exception e) {
								e.printStackTrace();
							}

							consolePane.setText(consolePane.getText() + "\nReport saved in: " + file.toString() + "-report.txt\n");
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
		});
	}

	/* Chord Transition Tool - Handling methods */

	private void analyzeHarmony(Harmony harmony, JTextPane txtRelative, JTextPane txtAbsolute, JTextPane txtName, JTextPane txtStructure, JTextPane txtFunction, JTextPane txtComplexity) {
		txtRelative.setText(harmony.getToneNamesMapped());
		txtRelative.setCaretPosition(0);
		txtAbsolute.setText(harmony.getToneNames());
		txtAbsolute.setCaretPosition(0);
		txtName.setText(Chordanal.getHarmonyAbbreviation(harmony));
		txtName.setCaretPosition(0);
		txtStructure.setText(listToString(Chordanal.getHarmonyNamesRelative(harmony)));
		txtStructure.setCaretPosition(0);
		txtFunction.setText(listToString(Harmanal.getRootsFormatted(harmony)));
		txtFunction.setCaretPosition(0);
		txtComplexity.setText(Integer.toString(Harmanal.getHarmonyComplexity(harmony)));
		txtComplexity.setCaretPosition(0);
	}

	private void analyzeTransition(Harmony harmony1, Harmony harmony2, JTextPane txtTransition, JTextPane txtTransitionComplexity) {
		txtTransition.setText(listToString(Harmanal.getTransitionsFormatted(harmony1,harmony2)));
		txtTransition.setCaretPosition(0);
		txtTransitionComplexity.setText(Integer.toString(Harmanal.getTransitionComplexity(harmony1, harmony2)));
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
