package org.harmony_analyser.application.services;

import org.harmony_analyser.chromanal.Chroma;
import org.harmony_analyser.plugins.*;
import org.junit.*;
import static org.mockito.Mockito.*;

import java.io.*;
import static org.junit.Assert.assertEquals;

/**
 * Unit tests for AudioAnalyser class
 */

@SuppressWarnings("ConstantConditions")

public class AudioAnalyserTest {
	private AudioAnalyser audioAnalyser;
	private String wrongInputFile;
	private File testWavFile;
	private String resultFile;

	@Before
	public void setUp() throws Exception {
		wrongInputFile = "wrongfile";
		ClassLoader classLoader = getClass().getClassLoader();
		testWavFile = new File(classLoader.getResource("test.wav").getPath());
	}

	@Test(expected = AnalysisPlugin.IncorrectInputException.class)
	public void shouldThrowExceptionOnWrongFile() throws IOException, AudioAnalyser.LoadFailedException, AnalysisPlugin.IncorrectInputException, AnalysisPlugin.OutputAlreadyExists, Chroma.WrongChromaSize {
		AnalysisPluginFactory analysisPluginFactory = new AnalysisPluginFactory();
		audioAnalyser = new AudioAnalyser(analysisPluginFactory);
		audioAnalyser.runAnalysis(wrongInputFile.toString(), "harmanal:transition_complexity", true);
	}

	@Test
	public void shouldCallPluginAnalyse() throws IOException, AudioAnalyser.LoadFailedException, AnalysisPlugin.IncorrectInputException, AnalysisPlugin.OutputAlreadyExists, Chroma.WrongChromaSize {
		AnalysisPlugin analysisPlugin = mock(AnalysisPlugin.class);
		when(analysisPlugin.analyse(testWavFile.toString(), true)).thenReturn("Done!");

		AnalysisPluginFactory analysisPluginFactory = new AnalysisPluginFactory() {
			public AnalysisPlugin createPlugin(String pluginKey) {
				return analysisPlugin;
			}
		};
		audioAnalyser = new AudioAnalyser(analysisPluginFactory);

		assertEquals("Done!", audioAnalyser.runAnalysis(testWavFile.toString(), "harmanal:transition_complexity", true));
	}
}
