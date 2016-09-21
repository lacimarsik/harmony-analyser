package org.harmony_analyser.chromanal;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for Chromanal class
 */

@SuppressWarnings("ConstantConditions")

public class ChromanalTest {
	private Chroma chroma1, chroma2;

	@Before
	public void setUp() throws Chroma.WrongChromaSize {
		chroma1 = new Chroma(new float[]{ 1f, 0f, 0f, 0f, 0.5f, 0f, 0f, 1f, 0f, 0f, 0f, 0f }); // C E G
		chroma2 = new Chroma(new float[]{ 1f, 0f, 0.5f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f }); // C D G
	}

	@Test
	public void shouldGetChromaComplexitySimple() {
		// E: 0.5 => 0
		// D: 0 => 0.5
		// Sum of Delta: 1
		assertEquals(Chromanal.getChromaComplexitySimple(chroma1, chroma2), 1.0, 0);
	}

	@Test
	public void shouldGetChromaComplexityTonal() throws Chroma.WrongChromaSize {
		// TODO Fix as it looks wrong
		assertEquals(Chromanal.getChromaComplexityTonal(chroma1, chroma2), 0.0, 0.0);
	}
}
