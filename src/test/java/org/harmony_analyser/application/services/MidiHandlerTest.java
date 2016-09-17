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
		String[] deviceList = midiHandler.getInputDeviceList();
		assert(deviceList[0].equals("No MIDI devices found") || (deviceList[0].contains("hw")));
	}
}
