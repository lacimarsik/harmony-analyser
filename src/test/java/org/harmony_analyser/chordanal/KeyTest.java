package org.harmony_analyser.chordanal;

import org.harmony_analyser.chordanal.Chordanal;
import org.harmony_analyser.chordanal.Key;
import org.junit.*;
import static org.junit.Assert.assertEquals;

/**
 * Unit tests for Key class
 */

public class KeyTest {
	private Key key1, key2;

	@Before
	public void setUp() {
		key1 = new Key(0, Chordanal.MAJOR);
		key2 = new Key(0, Chordanal.MINOR);
	}

	@Test
	public void shouldGetHarmonicFunctions() {
		assertEquals(key1.getTonic().getToneNamesMapped(), "C E G ");
		assertEquals(key1.getSubdominant().getToneNamesMapped(), "C F A ");
		assertEquals(key2.getDominant().getToneNamesMapped(), "D G A# ");
	}
}
