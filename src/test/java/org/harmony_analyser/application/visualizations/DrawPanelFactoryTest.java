package org.harmony_analyser.application.visualizations;

import org.harmony_analyser.application.services.AudioAnalyser;
import org.harmony_analyser.plugins.*;
import org.vamp_plugins.PluginLoader;
import org.junit.runner.RunWith;
import org.junit.Before;
import org.junit.Test;
import java.io.*;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.whenNew;

/**
 * Unit tests for DrawPanelFactory class
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest(DrawPanelFactory.class)
public class DrawPanelFactoryTest {
	private DrawPanelFactory drawPanelFactory;
	private File testReport;

	@Before
	public void setUp() {
		drawPanelFactory = new DrawPanelFactory();
		ClassLoader classLoader = getClass().getClassLoader();
		testReport = new File(classLoader.getResource("test-reportFixture.txt").getFile());
	}

	@Test
	public void shouldCreateDrawPanel() throws Exception {
		SegmentationDrawPanel segmentationDrawPanel = mock(SegmentationDrawPanel.class);
		whenNew(SegmentationDrawPanel.class).withArguments(testReport.toString()).thenReturn(segmentationDrawPanel);

		DrawPanel drawPanel = drawPanelFactory.createDrawPanel(testReport.toString(), "nnls-chroma:chordino");

		verifyNew(SegmentationDrawPanel.class).withArguments(testReport.toString());
	}
}
