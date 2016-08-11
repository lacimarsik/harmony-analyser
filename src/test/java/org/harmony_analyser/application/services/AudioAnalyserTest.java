package org.harmony_analyser.application.services;

import org.harmony_analyser.plugins.AnalysisPlugin;
import org.junit.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Unit tests for AudioAnalyser class
 */

public class AudioAnalyserTest {
	private AudioAnalyser audioAnalyser;
	private File testWavFile, testReportFixture;
	private List<String> inputFiles;
	private String resultFile;

	// XXX: Until Mockito is added as dependency and mocks are in practice, expect the fail scenario rather than proper call

	@Before
	public void setUp() {
		audioAnalyser = new AudioAnalyser();
		inputFiles = new ArrayList<>();
		inputFiles.add("test.wav-chromas.txt");
		inputFiles.add("wrongfile");
		resultFile = "result.txt";
	}

	@Test(expected = AnalysisPlugin.IncorrectInputException.class)
	public void shouldRunAnalysis() throws AudioAnalyser.LoadFailedException, AnalysisPlugin.IncorrectInputException, IOException {
		audioAnalyser.runAnalysis(inputFiles, resultFile, "harmanal:transition_complexity");
	}
}
