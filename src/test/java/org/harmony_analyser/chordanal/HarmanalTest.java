package org.harmony_analyser.chordanal;

import org.junit.*;
import static org.junit.Assert.assertEquals;

/**
 * Unit tests for Harmanal class
 */

@SuppressWarnings("ConstantConditions")

public class HarmanalTest {
	private Key key1;

	@Before
	public void setUp() {
		key1 = new Key(0, Chordanal.MAJOR);
	}

	@Test
	public void shouldGetRootsForHarmony() {
		assertEquals(Harmanal.getRoots(Chordanal.createHarmonyFromTones("C4 E4 G4")).getAll().get(0), "C major,Tonic,C E G ;0");
		assertEquals(Harmanal.getRoots(Chordanal.createHarmonyFromTones("C4 E4 G4")).getAll().get(1), "F major,Dominant,C E G ;0");
		assertEquals(Harmanal.getRoots(Chordanal.createHarmonyFromTones("C4 E4 G4")).getAll().get(2), "G major,Subdominant,C E G ;0");
		assertEquals(Harmanal.getRoots(Chordanal.createHarmonyFromTones("C4 E4 G4")).getAll().get(3), "C minor,Tonic,C G ;2");
		assertEquals(Harmanal.getRoots(Chordanal.createHarmonyFromTones("C4 E4 G4")).getAll().get(4), "E minor,Tonic,E G ;1");
		assertEquals(Harmanal.getRoots(Chordanal.createHarmonyFromTones("C4 E4 G4")).getAll().get(5), "F minor,Dominant,C G ;2");
		assertEquals(Harmanal.getRoots(Chordanal.createHarmonyFromTones("C4 E4 G4")).getAll().get(6), "G minor,Subdominant,C G ;2");
		assertEquals(Harmanal.getRoots(Chordanal.createHarmonyFromTones("C4 E4 G4")).getAll().get(7), "A minor,Dominant,E G ;1");
		assertEquals(Harmanal.getRoots(Chordanal.createHarmonyFromTones("C4 E4 G4")).getAll().get(8), "B minor,Subdominant,E G ;2");
	}

	@Test
	public void shouldGetCommonRootsForHarmonies() {
		assertEquals(Harmanal.getCommonRootsByKey(Chordanal.createHarmonyFromTones("C4 E4 G4"), Chordanal.createHarmonyFromTones("F4 A4 C4")).getAll().get(0), "C major,Tonic,Subdominant,C E G ,C F A ;0,0");
		assertEquals(Harmanal.getCommonRootsByKey(Chordanal.createHarmonyFromTones("C4 E4 G4"), Chordanal.createHarmonyFromTones("F4 A4 C4")).getAll().get(1), "F major,Dominant,Tonic,C E G ,C F A ;0,0");
		assertEquals(Harmanal.getCommonRootsByKey(Chordanal.createHarmonyFromTones("C4 E4 G4"), Chordanal.createHarmonyFromTones("F4 A4 C4")).getAll().get(2), "E minor,Tonic,Subdominant,E G ,C A ;1,2");
		assertEquals(Harmanal.getCommonRootsByKey(Chordanal.createHarmonyFromTones("C4 E4 G4"), Chordanal.createHarmonyFromTones("F4 A4 C4")).getAll().get(3), "A minor,Dominant,Tonic,E G ,C A ;1,1");
		assertEquals(Harmanal.getCommonRootsByKey(Chordanal.createHarmonyFromTones("C4 E4 G4"), Chordanal.createHarmonyFromTones("F4 A4 C4")).getAll().get(4), "C minor,Tonic,Subdominant,C G ,C F ;2,2");
		assertEquals(Harmanal.getCommonRootsByKey(Chordanal.createHarmonyFromTones("C4 E4 G4"), Chordanal.createHarmonyFromTones("F4 A4 C4")).getAll().get(5), "F minor,Dominant,Tonic,C G ,C F ;2,2");

		assertEquals(Harmanal.getCommonRoots(Chordanal.createHarmonyFromTones("C4 E4 G4"), Chordanal.createHarmonyFromTones("C4 E4 G#4")).getAll().get(0), "C major,Tonic,C E ;0,2");
		assertEquals(Harmanal.getCommonRoots(Chordanal.createHarmonyFromTones("C4 E4 G4"), Chordanal.createHarmonyFromTones("C4 E4 G#4")).getAll().get(1), "F major,Dominant,C E ;0,2");
		assertEquals(Harmanal.getCommonRoots(Chordanal.createHarmonyFromTones("C4 E4 G4"), Chordanal.createHarmonyFromTones("C4 E4 G#4")).getAll().get(2), "G major,Subdominant,C E ;0,2");
	}

	@Test
	public void shouldGetCommonAncestorsForHarmonies() {
		assertEquals(Harmanal.getCommonAncestors(Chordanal.createHarmonyFromTones("C4 E4 A#4"), Chordanal.createHarmonyFromTones("C4 E4 G#4 A#4")).getAll().get(0), "F major,Dominant,C E A# ;0,2");
		assertEquals(Harmanal.getCommonAncestors(Chordanal.createHarmonyFromTones("C4 E4 A#4"), Chordanal.createHarmonyFromTones("C4 E4 G#4 A#4")).getAll().get(1), "C major,Tonic,C E A# ;0,2");
		assertEquals(Harmanal.getCommonAncestors(Chordanal.createHarmonyFromTones("C4 E4 A#4"), Chordanal.createHarmonyFromTones("C4 E4 G#4 A#4")).getAll().get(2), "G major,Subdominant,C E A# ;0,2");
	}

	@Test
	public void shouldGetDerivationForHarmony() {
		assertEquals(Harmanal.getHarmonyDerivation(Chordanal.createHarmonyFromTones("C2 E2 F#2 G#2"), Chordanal.createHarmonyFromTones("C2 E2"), key1).get(0), "C E ");
		assertEquals(Harmanal.getHarmonyDerivation(Chordanal.createHarmonyFromTones("C2 E2 F#2 G#2"), Chordanal.createHarmonyFromTones("C2 E2"), key1).get(1), "C E F ");
		assertEquals(Harmanal.getHarmonyDerivation(Chordanal.createHarmonyFromTones("C2 E2 F#2 G#2"), Chordanal.createHarmonyFromTones("C2 E2"), key1).get(2), "C E F# ");
		assertEquals(Harmanal.getHarmonyDerivation(Chordanal.createHarmonyFromTones("C2 E2 F#2 G#2"), Chordanal.createHarmonyFromTones("C2 E2"), key1).get(3), "C E F# A ");
		assertEquals(Harmanal.getHarmonyDerivation(Chordanal.createHarmonyFromTones("C2 E2 F#2 G#2"), Chordanal.createHarmonyFromTones("C2 E2"), key1).get(4), "C E F# G# ");
	}

	@Test
	public void shouldGetComplexityForHarmony() {
		assertEquals(Harmanal.getHarmonyComplexity(Chordanal.createHarmonyFromTones("C2 E2 F#2 G#2"), Chordanal.createHarmonyFromTones("C2 E2"), key1), 4);
	}

	@Test
	public void shouldGetTransitionsForHarmonies() {
		assertEquals(Harmanal.getTransitions(Chordanal.createHarmonyFromTones("C2 E2 G2"), Chordanal.createHarmonyFromTones("F2 A2 C2")).getAll().get(0), "C major,Tonic,0,Subdominant,0;0");
		assertEquals(Harmanal.getTransitions(Chordanal.createHarmonyFromTones("C2 E2 G2"), Chordanal.createHarmonyFromTones("F2 A2 C2")).getAll().get(1), "F major,Dominant,0,Tonic,0;0");
		assertEquals(Harmanal.getTransitions(Chordanal.createHarmonyFromTones("C2 E2 G2"), Chordanal.createHarmonyFromTones("F2 A2 C2")).getAll().get(2), "E minor,Tonic,1,Subdominant,2;3");
		assertEquals(Harmanal.getTransitions(Chordanal.createHarmonyFromTones("C2 E2 G2"), Chordanal.createHarmonyFromTones("F2 A2 C2")).getAll().get(3), "A minor,Dominant,1,Tonic,1;2");
		assertEquals(Harmanal.getTransitions(Chordanal.createHarmonyFromTones("C2 E2 G2"), Chordanal.createHarmonyFromTones("F2 A2 C2")).getAll().get(4), "C minor,Tonic,2,Subdominant,2;4");
		assertEquals(Harmanal.getTransitions(Chordanal.createHarmonyFromTones("C2 E2 G2"), Chordanal.createHarmonyFromTones("F2 A2 C2")).getAll().get(5), "F minor,Dominant,2,Tonic,2;4");
	}

	@Test
	public void shouldGetTransitionComplexityForHarmonies() {
		assertEquals(Harmanal.getTransitionComplexity(Chordanal.createHarmonyFromTones("C2 E2 G2"), Chordanal.createHarmonyFromTones("F2 A2 C2 C#2")), 2);
	}
}
