package org.harmony_analyser.chordanal;

import org.junit.*;
import static org.junit.Assert.assertEquals;

/**
 * Unit tests for Harmony class
 */

public class HarmonyTest {
	private Harmony harmony1, harmony2, harmony3, harmony4, harmony5, harmony6;

	@Before
	public void setUp() {
		int[] array1 = {60};
		int[] array2 = {60, 64};
		int[] array3 = {60, 64, 67};
		int[] array4 = {60, 64, 67, 70};
		int[] array5 = {60, 64, 67, 70, 72};
		int[] array6 = {65, 69, 72};
		harmony1 = new Harmony(array1);
		harmony2 = new Harmony(array2);
		harmony3 = new Harmony(array3);
		harmony4 = new Harmony(array4);
		harmony5 = new Harmony(array5);
		harmony6 = new Harmony(array6);
	}

	@Test
	public void shouldGetTonesAndIntervals() {
		assertEquals(harmony1.getToneNames(), "C4 ");
		assertEquals(harmony1.getToneNamesMapped(), "C ");
		assertEquals(harmony2.getToneNames(), "C4 E4 ");
		assertEquals(harmony2.getToneNamesMapped(), "C E ");
		assertEquals(harmony3.getToneNames(), "C4 E4 G4 ");
		assertEquals(harmony3.getToneNamesMapped(), "C E G ");
		assertEquals(harmony4.getToneNames(), "C4 E4 G4 A#4 ");
		assertEquals(harmony4.getToneNamesMapped(), "C E G A# ");
		assertEquals(harmony5.getToneNames(), "C4 E4 G4 A#4 C5 ");
		assertEquals(harmony5.getToneNamesMapped(), "C E G A# ");
		assertEquals(harmony6.getToneNames(), "F4 A4 C5 ");
		assertEquals(harmony6.getToneNamesMapped(), "C F A ");

		assertEquals(harmony3.getIntervals()[0], "4");
		assertEquals(harmony3.getIntervals()[1], "7");
	}

	@Test
	public void shouldCreateInversions() {
		assertEquals(harmony3.inversionUp().getToneNames(), "E4 G4 C5 ");
		assertEquals(harmony3.inversionDown().getToneNames(), "G3 C4 E4 ");
	}

	@Test
	public void containsMappedShouldCheckInclusion() {
		assert(harmony3.containsMapped(new Tone(60)));
		assert(harmony3.containsMapped(harmony2));
	}

	@Test
	public void shouldGetCommonTones() {
		assertEquals(harmony3.getCommonTones(harmony6).getToneNamesMapped(), "C ");
	}
}
