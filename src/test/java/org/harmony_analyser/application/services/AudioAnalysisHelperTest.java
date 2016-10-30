package org.harmony_analyser.application.services;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for AudioAnalyserHelper class
 */

public class AudioAnalysisHelperTest {
	private float[] chroma;
	private float[] chromaCalculated;
	private float[] chromaExpected;
	private int[] chordCalculated;
	private String line;

	@Before
	public void setUp() {
		chroma = new float[]{ 1f, 0.3f, 0f, 0f, 1f, 0f, 0f, 1f, 0f, 0f, 0f, 0f };
	}

	@Test
	public void shouldAverageChroma() {
		chromaExpected = new float[]{ 0.5f, 0.15f, 0f, 0f, 0.5f, 0f, 0f, 0.5f, 0f, 0f, 0f, 0f };

		chromaCalculated = AudioAnalysisHelper.averageChroma(chroma, 2);
		assertArrayEquals(chromaCalculated, chromaExpected, (float) 0);
	}

	@Test
	public void shouldFilterChroma() {
		chromaExpected = new float[]{ 1f, 0f, 0f, 0f, 1f, 0f, 0f, 1f, 0f, 0f, 0f, 0f };

		chromaCalculated = AudioAnalysisHelper.filterChroma(chroma, (float) 0.5);
		assertArrayEquals(chromaCalculated, chromaExpected, (float) 0);
	}

	@Test
	public void shouldCreateBinaryChord() {
		int[] chordExpected = new int[]{1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0};

		chordCalculated = AudioAnalysisHelper.createBinaryChord(chroma, 3);
		assertArrayEquals(chordCalculated, chordExpected);
	}

	@Test
	public void shouldGetNumberOfTones() {
		chordCalculated = new int[]{ 1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0 };

		assertEquals(3, AudioAnalysisHelper.getNumberOfTones(chordCalculated));
	}

	@Test
	public void shouldGetTimestampFromLine() {
		line = "0.0123: Cm";

		assertEquals((float) 0.0123, AudioAnalysisHelper.getTimestampFromLine(line), 0);
	}

	@Test
	public void shouldGetLabelFromLine() {
		line = "0.0123: Cm";

		assertEquals("Cm", AudioAnalysisHelper.getLabelFromLine(line));
	}

	@Test
	public void shouldGetChromaFromLine() throws AudioAnalyser.IncorrectInputException {
		chromaExpected = new float[]{ 0.12f, 1.23f, 0.56f, 0f, 0f, 0f, 1.22f, 2.3f, 5.64f, 9.1f, 3.51f, 0.12f };
		line = "0.0123: 0.12 1.23 0.56 0.0 0.0 0.0 1.22 2.30 5.64 9.10 3.51 0.12";

		chromaCalculated = AudioAnalysisHelper.getChromaFromLine(line);
		assertArrayEquals(chromaCalculated, chromaExpected, (float) 0);
	}

	@Test
	public void shouldShiftChroma() {
		chromaExpected = new float[]{ 0.3f, 0f, 0f, 1f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1f };

		chromaCalculated = AudioAnalysisHelper.shiftChroma(chroma, 1);
		assertArrayEquals(chromaCalculated, chromaExpected, (float) 0);
	}
}