package org.harmony_analyser.application.services;

import org.harmony_analyser.chordanal.*;

import java.io.File;
import java.util.Vector;
import javax.sound.midi.*;

/**
 * Class to handle all the MIDI related events
 */

@SuppressWarnings("SameParameterValue")

public class MidiHandler {
	public static final int TOGETHER = 0;
	public static final int SEPARATE = 1;

	private static final int LONG = 0;
	private static final int SHORT = 1;
	private MidiDevice inputDevice;
	private MidiDecoder decoder;
	private Sequencer sequencer;
	private Synthesizer synthesizer;
	private MidiDevice outputDevice;
	private MidiChannel[] channels;
	private Instrument[] instruments;
	private int playMode = 0;
	private int instrument = 0;
	private int channel = 0;
	private int volume = 100;
	private int length = 0;

	public static final MidiDevice EMPTY_MIDI_DEVICE = null; // Null value is enforced by the interface
	public static final Sequencer EMPTY_SEQUENCER = null; // Null value is enforced by the interface
	public static final Synthesizer EMPTY_SYNTHESIZER = null; // Null value is enforced by the interface
	public static final MidiDecoder EMPTY_MIDI_DECODER = null; // Null value is enforced by the interface

	/* Public / Package methods */

	public int getInstrument() {
		return instrument;
	}

	public void setInstrument(int instrument) {
		this.instrument = instrument;
	}

	public int getChannel() {
		return channel;
	}

	public void setChannel(int channel) {
		this.channel = channel;
	}

	public int getVolume() {
		return volume;
	}

	public void setVolume(int volume) {
		this.volume = volume;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getPlayMode() {
		return playMode;
	}

	public void setPlayMode(int playMode) {
		this.playMode = playMode;
	}

	public void playMidi(String inputFile) {
		try {
			File file = new File("resources/" + inputFile);
			Sequence sequence = MidiSystem.getSequence(file);
			sequencer.setSequence(sequence);
			sequencer.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Initializes the MidiHandler with the custom Sequencer, Synthesizer, Input and Output devices or null values for default
	 */

	public void initialize(Sequencer sequencer, Synthesizer synthesizer, MidiDevice inputDevice, MidiDevice outputDevice, MidiDecoder decoder) {
		try {
			if (sequencer != MidiHandler.EMPTY_SEQUENCER) {
				this.sequencer = sequencer;
			} else {
				this.sequencer = MidiSystem.getSequencer();
			}
			this.sequencer.open();

			if (synthesizer != MidiHandler.EMPTY_SYNTHESIZER) {
				this.synthesizer = synthesizer;
			} else {
				this.synthesizer = MidiSystem.getSynthesizer();
			}
			this.synthesizer.open();
			channels = this.synthesizer.getChannels();
			instruments = this.synthesizer.getDefaultSoundbank().getInstruments();

			if (inputDevice != MidiHandler.EMPTY_MIDI_DEVICE) {
				this.inputDevice = inputDevice;
				this.inputDevice.open();
			}

			if (outputDevice != MidiHandler.EMPTY_MIDI_DEVICE) {
				this.outputDevice = outputDevice;
				this.outputDevice.open();
			}

			if (decoder != MidiHandler.EMPTY_MIDI_DECODER) {
				this.decoder = decoder;
			} else {
				this.decoder = new MidiDecoder();
			}
		} catch (MidiUnavailableException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get list of MIDI input devices
	 */

	public String[] getInputDeviceList() {
		MidiDevice device;
		MidiDevice.Info[] infoMidiDevices = MidiSystem.getMidiDeviceInfo();
		Vector<MidiDevice.Info> inputDeviceInfos = new Vector<>();

		for (MidiDevice.Info info : infoMidiDevices) {
			try {
				device = MidiSystem.getMidiDevice(info);
				if (device.getClass().getSimpleName().endsWith("InDevice")) {
					inputDeviceInfos.add(info);
				}
			} catch (MidiUnavailableException e) {
				e.printStackTrace();
			}
		}

		String[] result;
		if (inputDeviceInfos.size() == 0) {
			result = new String[1];
			result[0] = "No MIDI devices found";
			return result;
		}

		result = new String[inputDeviceInfos.size()];
		for (int i = 0; i < inputDeviceInfos.size(); i++) {
			result[i] = inputDeviceInfos.get(i).getName();
		}
		return result;
	}

	/**
	 * Get a MIDI device based on its name 
	 */

	public MidiDevice getMidiDevice(String name) {
		MidiDevice.Info[] infoMidiDevices = MidiSystem.getMidiDeviceInfo();

		for (MidiDevice.Info info : infoMidiDevices) {
			try {
				if (info.getName().equals(name) && isInputDevice(MidiSystem.getMidiDevice(info))) {
					return MidiSystem.getMidiDevice(info);
				}
			} catch (MidiUnavailableException e) {
				e.printStackTrace();
			}
		}
		return MidiHandler.EMPTY_MIDI_DEVICE;
	}

	/**
	 * Connects the input to the synthesizer
	 */

	public void connectInputSynthesizer() {
		try {
			inputDevice.getTransmitter().setReceiver(synthesizer.getReceiver());
		} catch (MidiUnavailableException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Connects the input to the decoder
	 */

	public void connectInputDecoder() {
		try {
			inputDevice.getTransmitter().setReceiver(decoder);
		} catch (MidiUnavailableException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Closes all possible devices used in the application
	 */

	public void close() {
		if (!inputDevice.equals(MidiHandler.EMPTY_MIDI_DEVICE)) {
			if (inputDevice.isOpen()) {
				inputDevice.close();
			}
		}
		if (!synthesizer.equals(MidiHandler.EMPTY_SYNTHESIZER)) {
			if (synthesizer.isOpen()) {
				synthesizer.close();
			}
		}
		if (!sequencer.equals(MidiHandler.EMPTY_SEQUENCER)) {
			if (sequencer.isOpen()) {
				sequencer.close();
			}
		}
		if (!outputDevice.equals(MidiHandler.EMPTY_MIDI_DEVICE)) {
			if (outputDevice.isOpen()) {
				outputDevice.close();
			}
		}
		if (!decoder.equals(MidiHandler.EMPTY_MIDI_DECODER)) {
			if (decoder.isOpen()) {
				decoder.close();
			}
		}
	}

	public Harmony getBufferHarmony() {
		String buffer = decoder.getBuffer();
		if (buffer.equals("")) {
			return Harmony.EMPTY_HARMONY;
		}
		String[] stringArray = buffer.split(" ");
		int[] intArray = new int[stringArray.length];
		for (int i = 0; i < stringArray.length; i++) {
			intArray[i] = Integer.parseInt(stringArray[i]);
		}

		return new Harmony(intArray);
	}

	public void openInputDevice() throws MidiUnavailableException {
		this.inputDevice.open();
	}

	public void closeInputDevice() {
		this.inputDevice.close();
	}

	public void closeDecoder() {
		this.decoder.close();
	}

	public void play(Harmony harmony) {
		synthesizer.loadInstrument(instruments[instrument]);
		harmony.tones.forEach(this::play);
	}

	void play(Key key) {
		synthesizer.loadInstrument(instruments[instrument]);
		for (int i : key.getScale()) {
			play(new Tone(i));
		}
	}

	/* Private methods */

	private void play(Tone tone) {
		synthesizer.loadInstrument(instruments[instrument]);
		channels[channel].noteOn(tone.getNumber(), tone.getVolume());

		if (length == SHORT) {
			channels[channel].noteOff(tone.getNumber());
		}
	}

	/**
	 * Finds out if a MIDI device is Input device
	 */

	private boolean isInputDevice(MidiDevice device) {
		return device.getClass().getSimpleName().endsWith("InDevice");
	}
}

/**
 * Subclass to receive and decode MIDI events from device
 */

class MidiDecoder implements Receiver {
	private String buffer = "";

	/* Public / Package methods */

	public void close() {
		buffer = "";
	}

	public void send(MidiMessage message, long lTimeStamp) {
		if (message instanceof ShortMessage) {
			if ((((ShortMessage)message).getCommand() == 0x90) && (((ShortMessage)message).getData2() != 0)) {
				buffer += (((ShortMessage)message).getData1() + " ");
			}
		}
	}

	boolean isOpen() {
		return (buffer.length() != 0);
	}

	String getBuffer() {
		return buffer;
	}
}
