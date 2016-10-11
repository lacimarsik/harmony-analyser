package org.harmony_analyser.plugins;

import org.harmony_analyser.plugins.chordanal_plugins.TransitionComplexityPlugin;
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
		TransitionComplexityPlugin transitionComplexityPlugin = mock(TransitionComplexityPlugin.class);
		whenNew(TransitionComplexityPlugin.class).withNoArguments().thenReturn(transitionComplexityPlugin);

		AnalysisPlugin analysisPlugin = analysisPluginFactory.createPlugin("harmanal:transition_complexity");

		verifyNew(TransitionComplexityPlugin.class).withNoArguments();
	}
}
