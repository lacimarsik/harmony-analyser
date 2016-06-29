package harmanal;

import javax.sound.midi.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

public class HarmonyAnalyser {
	private JTabbedPane tabbedPane1;
	private JButton selectButton;
	private JTextPane textPane1;
	private JButton captureButton;
	private JButton captureButton1;
	private JButton playButton;
	private JButton playButton1;
	private JTextPane textPane2;
	private JButton loadPluginsButton;
	private JButton extractChromasButton;
	private JList list1;
	private JTextPane textPane4;
	private JTextPane textPane5;
	private JTextPane textPane6;
	private JTextPane textPane7;
	private JTextPane textPane8;
	private JTextPane textPane9;
	private JTextPane textPane10;
	private JTextPane textPane11;
	private JTextPane textPane12;
	private JTextPane textPane13;
	private JTextPane textPane14;
	private JTextPane textPane15;
	private JTextPane textPane16;
	private JTextPane textPane3;
	private JTextField textField8;

	private NNLSPlugin nnls;
	private Harmony harmony1,harmony2 = null;

	public HarmonyAnalyser() {
		/* Chord Transition Tool - Initialization */

		// Obtain information about all the installed input MIDI devices
		final MidiHandler midiHandler = new MidiHandler();
		String[] inputDevices = midiHandler.getInputDeviceList();
		list1.setListData(inputDevices);

		list1.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent listSelectionEvent) {
				selectButton.setEnabled(true);
			}
		});

		selectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				MidiDevice selectedDevice = midiHandler.getMidiDevice(list1.getSelectedValue().toString());
				textPane1.setText("device: " + selectedDevice.getDeviceInfo().getName() +
					"\nvendor: " + selectedDevice.getDeviceInfo().getVendor().substring(0, selectedDevice.getDeviceInfo().getVendor().indexOf(" "))+
					"\ndescription: " + selectedDevice.getDeviceInfo().getDescription().substring(0, selectedDevice.getDeviceInfo().getDescription().indexOf(",")));

				midiHandler.close();
				midiHandler.initialize(null, null, selectedDevice, null, null);
				midiHandler.connectInputSynthesizer();

				captureButton.setEnabled(true);
				captureButton1.setEnabled(true);
				captureButton.setSelected(false);
				captureButton1.setSelected(false);
				playButton.setEnabled(false);
				playButton1.setEnabled(false);
			}
		});

		captureButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				try {
					if (captureButton.isSelected()) {
						midiHandler.connectInputDecoder();
						captureButton1.setEnabled(false);
					} else {
						midiHandler.inputDevice.close();
						harmony1 =  midiHandler.getBufferHarmony();
						if (harmony1 != null) {
							analyzeHarmony(harmony1,textPane10,textPane12,textPane4,textPane6,textPane8,textPane14);
							playButton.setEnabled(true);
							if (harmony2 != null) {
								analyzeTransition(harmony1,harmony2,textPane2,textPane16);
							}
						}
						midiHandler.decoder.close();
						midiHandler.inputDevice.open();
						midiHandler.connectInputSynthesizer();
						captureButton1.setEnabled(true);
					}
				} catch (MidiUnavailableException e) {
					e.printStackTrace();
				}
			}
		});

		captureButton1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				try {
					if (captureButton1.isSelected()) {
						midiHandler.connectInputDecoder();
						captureButton.setEnabled(false);
					} else {
						midiHandler.inputDevice.close();
						harmony2 = midiHandler.getBufferHarmony();
						if (harmony2 != null) {
							analyzeHarmony(harmony2,textPane11,textPane13,textPane5,textPane7,textPane9,textPane15);
							playButton1.setEnabled(true);
							if (harmony1 != null) {
								analyzeTransition(harmony1,harmony2,textPane2,textPane16);
							}
						}
						midiHandler.decoder.close();
						midiHandler.inputDevice.open();
						midiHandler.connectInputSynthesizer();
						captureButton.setEnabled(true);
					}
				} catch (MidiUnavailableException e) {
					e.printStackTrace();
				}
			}
		});

		textPane10.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				super.focusLost(e);
				harmony1 = Chordanal.createHarmonyFromRelativeTones(textPane10.getText());
				if (harmony1 != null) {
					analyzeHarmony(harmony1,textPane10,textPane12,textPane4,textPane6,textPane8,textPane14);
					playButton.setEnabled(true);
					if (harmony2 != null) {
						analyzeTransition(harmony1,harmony2,textPane2,textPane16);
					}
				} else {
					textPane10.setText("");
					textPane12.setText("");
				}
			}
		});

		textPane12.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				super.focusLost(e);
				harmony1 = Chordanal.createHarmonyFromTones(textPane12.getText());
				if (harmony1 != null) {
					analyzeHarmony(harmony1,textPane10,textPane12,textPane4,textPane6,textPane8,textPane14);
					playButton.setEnabled(true);
					if (harmony2 != null) {
						analyzeTransition(harmony1,harmony2,textPane2,textPane16);
					}
				} else {
					textPane10.setText("");
					textPane12.setText("");
				}
			}
		});

		textPane11.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				super.focusLost(e);
				harmony2 = Chordanal.createHarmonyFromRelativeTones(textPane11.getText());
				if (harmony2 != null) {
					analyzeHarmony(harmony2,textPane11,textPane13,textPane5,textPane7,textPane9,textPane15);
					playButton1.setEnabled(true);
					if (harmony1 != null) {
						analyzeTransition(harmony1,harmony2,textPane2,textPane16);
					}
				} else {
					textPane11.setText("");
					textPane13.setText("");
				}
			}
		});

		textPane13.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				super.focusLost(e);
				harmony2 = Chordanal.createHarmonyFromTones(textPane11.getText());
				if (harmony2 != null) {
					analyzeHarmony(harmony2,textPane11,textPane13,textPane5,textPane7,textPane9,textPane15);
					playButton1.setEnabled(true);
					if (harmony1 != null) {
						analyzeTransition(harmony1,harmony2,textPane2,textPane16);
					}
				} else {
					textPane11.setText("");
					textPane13.setText("");
				}
			}
		});

		playButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				midiHandler.play(harmony1);
			}
		});

		playButton1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				midiHandler.play(harmony2);
			}
		});

		/* Audio Analysis Tool - Initialization */

		loadPluginsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				nnls = new NNLSPlugin();
			}
		});

		extractChromasButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				try {
					Path startPath = Paths.get(textField8.getText());
					Files.walkFileTree(startPath, new SimpleFileVisitor<Path>() {
						@Override
						public FileVisitResult preVisitDirectory(Path dir,
								BasicFileAttributes attrs) {
							System.out.println("Dir: " + dir.toString());
							return FileVisitResult.CONTINUE;
						}

						@Override
						public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {

							if (file.toString().endsWith(".wav")) {
								System.out.println("Analyzing: " + file.toString());
								nnls = new NNLSPlugin();
								nnls.analyze(file.toString(), file.toString() + "-chromas.txt");
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
