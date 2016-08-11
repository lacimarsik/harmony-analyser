package org.harmony_analyser.application.services;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for MidiHandler class
 */

public class MidiHandlerTest {
	private MidiHandler midiHandler;

	@Before
	public void setUp() {
		midiHandler = new MidiHandler();
	}

	@Test
	public void shouldGetInputDeviceList() {
		midiHandler.getInputDeviceList();
	}
}
