package org.harmony_analyser.application.services;

import org.harmony_analyser.chromanal.Chroma;
import org.harmony_analyser.plugins.AnalysisPlugin;
import org.junit.*;

import java.io.File;
import java.io.IOException;

/**
 * Unit tests for AudioAnalyser class
 */

public class AudioAnalyserTest {
	private AudioAnalyser audioAnalyser;
	private File testWavFile, testReportFixture;
	private String inputFile;
	private String resultFile;

	// XXX: Until Mockito is added as dependency and mocks are in practice, expect the fail scenario rather than proper call

	@Before
	public void setUp() {
		audioAnalyser = new AudioAnalyser();
		inputFile = "wrongfile";
	}

	@Test(expected = AnalysisPlugin.IncorrectInputException.class)
	public void shouldRunAnalysis() throws IOException, AudioAnalyser.LoadFailedException, AnalysisPlugin.IncorrectInputException, AnalysisPlugin.OutputAlreadyExists, Chroma.WrongChromaSize {
		audioAnalyser.runAnalysis(inputFile, "harmanal:transition_complexity", true);
	}
}
