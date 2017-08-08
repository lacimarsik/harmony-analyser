package org.harmony_analyser.jharmonyanalyser.plugins;

import org.harmony_analyser.jharmonyanalyser.services.AudioAnalyser;
import org.harmony_analyser.jharmonyanalyser.plugins.vamp_plugins.*;

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
	public void shouldThrowForIncorrectInputFiles() throws AudioAnalyser.IncorrectInputException, AudioAnalyser.OutputAlreadyExists {
		chordino.checkInputFiles(inputFile);
	}
}
