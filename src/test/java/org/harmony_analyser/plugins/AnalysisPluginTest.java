package org.harmony_analyser.plugins;

import org.harmony_analyser.plugins.vamp_plugins.*;

import org.junit.*;
import java.util.*;

/**
 * Unit tests for AnalysisPlugin class
 */

public class AnalysisPluginTest {
	private ChordinoPlugin chordino;
	private List<String> inputFiles;

	@Before
	public void setUp() throws Exception {
		chordino = new ChordinoPlugin();
		inputFiles = new ArrayList<>();
		inputFiles.add("test.mp3");
	}

	@Test(expected = AnalysisPlugin.IncorrectInputException.class)
	public void shouldCheckInputFiles() throws AnalysisPlugin.IncorrectInputException {
		chordino.checkInputFiles(inputFiles);
	}
}
