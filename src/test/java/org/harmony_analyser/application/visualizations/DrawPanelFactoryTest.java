package org.harmony_analyser.application.visualizations;

import org.junit.runner.RunWith;
import org.junit.Before;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.whenNew;

/**
 * Unit tests for DrawPanelFactory class
 */

@SuppressWarnings("UnusedAssignment")
@RunWith(PowerMockRunner.class)
@PrepareForTest(DrawPanelFactory.class)
public class DrawPanelFactoryTest {
	private DrawPanelFactory drawPanelFactory;
	private VisualizationData visualizationData;

	@Before
	public void setUp() {
		drawPanelFactory = new DrawPanelFactory();
		visualizationData = VisualizationData.EMPTY_VISUALIZATION_DATA;
	}

	@Test
	public void shouldCreateDrawPanel() throws Exception {
		SegmentationDrawPanel segmentationDrawPanel = mock(SegmentationDrawPanel.class);
		whenNew(SegmentationDrawPanel.class).withArguments(visualizationData).thenReturn(segmentationDrawPanel);

		DrawPanel drawPanel = drawPanelFactory.createDrawPanel("nnls-chroma:chordino", visualizationData);

		verifyNew(SegmentationDrawPanel.class).withArguments(visualizationData);
	}
}
