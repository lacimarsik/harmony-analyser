package org.harmony_analyser.jharmonyanalyser.chord_analyser;

import org.junit.*;
import static org.junit.Assert.assertEquals;

/**
 * Unit tests for Chord class
 */

public class ChordTest {
	private Chord chord1, chord2, chord3, chord4, chord5, chord6;

	@Before
	public void setUp() {
		int[] array1 = {60};
		int[] array2 = {60, 64};
		int[] array3 = {60, 64, 67};
		int[] array4 = {60, 64, 67, 70};
		int[] array5 = {60, 64, 67, 70, 72};
		int[] array6 = {65, 69, 72};
		chord1 = new Chord(array1);
		chord2 = new Chord(array2);
		chord3 = new Chord(array3);
		chord4 = new Chord(array4);
		chord5 = new Chord(array5);
		chord6 = new Chord(array6);
	}

	@Test
	public void shouldGetTonesAndIntervals() {
		assertEquals(chord1.getToneNames(), "C4 ");
		assertEquals(chord1.getToneNamesMapped(), "C ");
		assertEquals(chord2.getToneNames(), "C4 E4 ");
		assertEquals(chord2.getToneNamesMapped(), "C E ");
		assertEquals(chord3.getToneNames(), "C4 E4 G4 ");
		assertEquals(chord3.getToneNamesMapped(), "C E G ");
		assertEquals(chord4.getToneNames(), "C4 E4 G4 A#4 ");
		assertEquals(chord4.getToneNamesMapped(), "C E G A# ");
		assertEquals(chord5.getToneNames(), "C4 E4 G4 A#4 C5 ");
		assertEquals(chord5.getToneNamesMapped(), "C E G A# ");
		assertEquals(chord6.getToneNames(), "F4 A4 C5 ");
		assertEquals(chord6.getToneNamesMapped(), "C F A ");

		assertEquals(chord3.getIntervals()[0], "4");
		assertEquals(chord3.getIntervals()[1], "7");
	}

	@Test
	public void shouldCreateInversions() {
		assertEquals(chord3.inversionUp().getToneNames(), "E4 G4 C5 ");
		assertEquals(chord3.inversionDown().getToneNames(), "G3 C4 E4 ");
	}

	@Test
	public void containsMappedShouldCheckInclusion() {
		assert(chord3.containsMapped(new Tone(60)));
		assert(chord3.containsMapped(chord2));
	}

	@Test
	public void shouldGetCommonTones() {
		assertEquals(chord3.getCommonTones(chord6).getToneNamesMapped(), "C ");
	}

	@Test
	public void shouldAddTone() {
		chord3.addTone(Chordanal.createToneFromRelativeName("A"));
		assertEquals(chord3.getToneNamesMapped(), "C E G A ");
	}
}
