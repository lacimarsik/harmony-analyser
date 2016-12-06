package org.harmony_analyser.jharmonyanalyser.plugins;

import org.harmony_analyser.jharmonyanalyser.plugins.chordanal_plugins.AverageChordComplexityDistancePlugin;
import org.harmony_analyser.jharmonyanalyser.services.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.whenNew;

/**
 * Unit tests for AnalysisPluginFactory class
 */

@SuppressWarnings("UnusedAssignment")
@RunWith(PowerMockRunner.class)
@PrepareForTest(AnalysisPluginFactory.class)
public class AnalysisPluginFactoryTest {
	private AnalysisPluginFactory analysisPluginFactory;

	@Before
	public void setUp() {
		analysisPluginFactory = new AnalysisPluginFactory();
	}

	@Test
	public void shouldCreateAnalysisPlugin() throws Exception {
		AverageChordComplexityDistancePlugin transitionComplexityPlugin = mock(AverageChordComplexityDistancePlugin.class);
		whenNew(AverageChordComplexityDistancePlugin.class).withNoArguments().thenReturn(transitionComplexityPlugin);

		Analysis analysisPlugin = analysisPluginFactory.createPlugin("chord_analyser:average_chord_complexity_distance");

		verifyNew(AverageChordComplexityDistancePlugin.class).withNoArguments();
	}
}
