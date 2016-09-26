package org.harmony_analyser.application.visualizations;

import org.harmony_analyser.plugins.AnalysisPluginFactory;
import org.junit.runner.RunWith;
import org.junit.Before;
import org.junit.Test;
import org.junit.runners.Parameterized;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.whenNew;

/**
 * Unit tests for DrawPanelFactory class
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest(DrawPanelFactory.class)
@PowerMockRunnerDelegate(value = Parameterized.class)
public class DrawPanelFactoryTest {
	private String pluginKey;
	private static DrawPanelFactory drawPanelFactory = new DrawPanelFactory();
	private AnalysisPluginFactory analysisPluginFactory;
	private VisualizationData visualizationData;

	@Before
	public void setUp() {
		analysisPluginFactory = new AnalysisPluginFactory();
		visualizationData = VisualizationData.VOID_VISUALIZATION_DATA;
	}

	public DrawPanelFactoryTest(String pluginKey) {
		this.pluginKey = pluginKey;
	}

	@Parameterized.Parameters
	public static Collection plugins() {
		return Arrays.asList(drawPanelFactory.getAllVisualizations());
	}

	@Test
	public void shouldCreateDrawPanel() throws Exception {
		PaletteDrawPanel paletteDrawPanel = mock(PaletteDrawPanel.class);
		ChromaDrawPanel chromaDrawPanel = mock(ChromaDrawPanel.class);
		SegmentationDrawPanel segmenatationDrawPanel = mock(SegmentationDrawPanel.class);
		ComplexityChartDrawPanel complexityChartDrawPanel = mock(ComplexityChartDrawPanel.class);

		whenNew(PaletteDrawPanel.class).withArguments(visualizationData).thenReturn(paletteDrawPanel);
		whenNew(ChromaDrawPanel.class).withArguments(visualizationData, "Simple").thenReturn(chromaDrawPanel);
		whenNew(ChromaDrawPanel.class).withArguments(visualizationData, "Tonal").thenReturn(chromaDrawPanel);
		whenNew(SegmentationDrawPanel.class).withArguments(visualizationData).thenReturn(segmenatationDrawPanel);
		whenNew(ComplexityChartDrawPanel.class).withArguments(visualizationData).thenReturn(complexityChartDrawPanel);

		DrawPanel drawPanelNew = drawPanelFactory.createDrawPanel(pluginKey, visualizationData);

		assertTrue(drawPanelNew != null);
	}
}
