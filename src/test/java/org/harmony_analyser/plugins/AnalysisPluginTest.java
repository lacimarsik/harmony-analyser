package org.harmony_analyser.plugins;

import org.harmony_analyser.application.services.AudioAnalyser;
import org.harmony_analyser.plugins.vamp_plugins.*;

import org.junit.*;

/**
 * Unit tests for AnalysisPlugin class
 */

public class AnalysisPluginTest {
	private ChordinoLabelsPlugin chordino;
	private String inputFile;

	@Before
	public void setUp() throws Exception {
		chordino = new ChordinoLabelsPlugin();
		inputFile = "test.mp3";
	}

	@Test(expected = AudioAnalyser.IncorrectInputException.class)
	public void shouldThrowForIncorrectInputFiles() throws AudioAnalyser.IncorrectInputException, AnalysisPlugin.OutputAlreadyExists {
		chordino.checkInputFiles(inputFile, true);
	}
}
