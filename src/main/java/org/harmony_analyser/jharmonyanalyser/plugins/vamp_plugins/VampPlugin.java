package org.harmony_analyser.jharmonyanalyser.plugins.vamp_plugins;

import org.harmony_analyser.jharmonyanalyser.services.AudioAnalyser;
import org.harmony_analyser.jharmonyanalyser.chroma_analyser.Chroma;
import org.harmony_analyser.jharmonyanalyser.plugins.*;
import org.harmony_analyser.jharmonyanalyser.services.AudioConverter;
import org.vamp_plugins.*;

import java.io.*;
import java.util.*;
import javax.sound.sampled.*;

/*
 * Generic Vamp plugin wrapper class using JVamp wrappers
 * http://www.vamp-plugins.org/
 * https://code.soundsoftware.ac.uk/projects/jvamp
 *
 * - extract from https://code.soundsoftware.ac.uk/projects/vamp-plugin-sdk
 *
 * Plugin - base class for a Vamp plugin
 *
 * PluginLoader - singleton class to obtain a concrete plugin based on:
 *  - key ("plugin_set:name_of_plugin")
 *  - sample rate
 *  - adapter flag, one or more of
 *  -- ADAPT_INPUT_DOMAIN(0x01), plugin will adapt the sample rate if necessary
 *  -- ADAPT_CHANNEL_COUNT(0x02), plugin will adapt the # of channels if necessary
 *  -- ADAPT_BUFFER_SIZE(0x04), plugin will adapt the buffer size itself
 *  -- ADAPT_ALL(0xff); all meaningful adaptations
 *
 * getIdentifier() - unique identifier of the plugin
 *
 * channel - one (or possibly more) audio channels that the plugin will analyse the data from
 *
 * initialise(# of channels, step size, block size) - initializes the plugin
 *
 * process(float[][] inputBuffers, RealTime timestamp)
 *  - multiple calls to process, each time passed a block size # of samples, stepping by step size # of samples
 *  -- step size is a step between the first samples of the block
 *  - inputBuffers[0][blockSize-1]] - the last sample from the block on the first channel
 *  - returns FeatureSet
 *  - last block may be partial (filled with zeros)!
 *
 * getInputDomain() - the type of the input
 *  - time domain: classical PCM samples audio, float amplitude samples
 *  - frequency domain: already done fourier transform on each block of input, float amplitude&phase samples
 *    in the function of frequency. Usually size step smaller than block size because of the discontinuities
 *    after the FFT
 *
 * getRemainingFeatures() - after all the process calls, gathers any remaining features
 *
 * getPreferredBlockSize(), getPreferredStepSize() - if doesn't matter, returning 0
 *
 * FeatureSet
 * - Map: key: id of output, value: FeatureList (List of Features)
 * - Each feature: optional timestamp and 0+ values (perhaps with labels)
 *
 * getOutputDescriptors() - returns a vector of OutputDescriptors
 *
 * OutputDescriptor
 * - OutputDescriptor with id i describes output with id i
 * - identifier, name, description (same as for plugin)
 * - unit (if applicable)
 * - binCount - number of values after timestamp in Feature
 * - binNames - labels for these values, if applicable
 * - minValue, maxValue - extents of the Feature values
 * - sampleType - describes how often the Features are made
 * -- OneSamplePerStep - for each sample of the input, the Features have no timestamp
 * -- FixedSampleRate - the Features might or might not have a timestamp, if not it can be calculated
 * -- VariableSampleRate - the Features must have a timestamp
 * - sampleRate - number of Features per second
 * - isQuantized, quantizeStep - if the output values are only e.g. integers
 *
 * Feature
 * - hasTimestamp
 * - hasDuration
 *
 * getParameterDescriptors() - returns a vector of ParameterDescriptors
 *
 * ParameterDescriptor
 * - identified by identifier
 * - getParameter(), setParameter()
 * - values of the parameters are always floats
 * - minValue, maxValue
 * - can be quantized as well, the quantized steps can be even names: valueNames
 * - have to be set before the plugin is initialized, after they can not be changed!
 * - by setting parameters, some of the properties of plugin can be changed, like
 * -- sampleRate, sampleType, binCount, minValue, maxValue, preferredStepSize, preferredBlockSize
 *
 * Programs
 * - predefined set of parameters that work well for certain sorts of tasks
 * - getProgramNames, getCurrentProgram, selectProgram
 */

/**
 * Wrapper for abstract VampPlugin inspired by JVamp host
 *
 * https://github.com/c4dm/jvamp/blob/master/host/host.java
 */

abstract class VampPlugin extends AnalysisPlugin {
	final static PluginLoader loader;
	final int adapterFlag = PluginLoader.AdapterFlags.ADAPT_ALL;
	final int defaultRate = 44100;
	Plugin p;
	int outputNumber;
	String outputType;
	int blockSize;

	static {
		loader = PluginLoader.getInstance();
	}

	/* Public / Package methods */

	public String printParameters() {
		String result = super.printParameters();

		result += "\n> VAMP - specific parameters for " + p.getName() + "\n";
		result += "description: " + p.getDescription() + "\n";
		result += "version: " + p.getPluginVersion() + "\n";
		Plugin.InputDomain domain = p.getInputDomain();
		if (domain == Plugin.InputDomain.TIME_DOMAIN) {
			result += "This is a time-domain plugin\n";
		} else {
			result += "This is a frequency-domain plugin\n";
		}
		ParameterDescriptor[] params = p.getParameterDescriptors();
		result += "Plugin has " + params.length + " parameters\n";
		for (int i = 0; i < params.length; ++i) {
			result += i + ": " + params[i].identifier + " (" + params[i].name + ") SET TO: " + p.getParameter(params[i].identifier) + "\n";
		}
		String[] programs = p.getPrograms();
		result += "Plugin has " + programs.length + " program(s)\n";
		for (int i = 0; i < programs.length; ++i) {
			result += i + ": " + programs[i] + "\n";
		}
		OutputDescriptor[] outputs = p.getOutputDescriptors();
		result += "Plugin has " + outputs.length + " outputNumber(s)\n";
		for (int i = 0; i < outputs.length; ++i) {
			result += i + ": " + outputs[i].identifier + " (sample type: " + outputs[i].sampleType + ")\n";
		}
		result += "Plugin has OutputType: " + this.outputType + "\n";
		return result;
	}

	/**
	 * Analyze audio using Vamp plugin. Courtesy of https://code.soundsoftware.ac.uk/projects/jvamp/repository/entry/host/host.java
	 *
	 * @param inputFile [String] name of the WAV audio file
	 */

	public String analyse(String inputFile, boolean force) throws IOException, AudioAnalyser.IncorrectInputException, AudioAnalyser.OutputAlreadyExists, Chroma.WrongChromaSize {
		String result = super.analyse(inputFile, force);
		try {
			File f = new File(inputFiles.get(0));
			AudioInputStream stream = AudioSystem.getAudioInputStream(f);
			AudioFormat format = stream.getFormat();
			PrintStream out = new PrintStream(new FileOutputStream(outputFile, false));
			boolean removeTempFile = false;

			if (format.getSampleSizeInBits() != 16 || format.getEncoding() != AudioFormat.Encoding.PCM_SIGNED || format.isBigEndian()) {
				result += "WARNING: Input is not 16-bit signed little-endian PCM file. Trying a conversion using ffmpeg\n";
				AudioConverter audioConverter = new AudioConverter();
				String newInputFile = audioConverter.convertTo16BitSignedLE(inputFile);
				result += "Created temporary file " + newInputFile + "\n";

				f = new File(newInputFile);
				stream = AudioSystem.getAudioInputStream(f);
				format = stream.getFormat();

				if (format.getSampleSizeInBits() != 16 || format.getEncoding() != AudioFormat.Encoding.PCM_SIGNED || format.isBigEndian()) {
					String errorMessage = "ERROR: Only 16-bit signed little-endian PCM files supported\n";
					result += errorMessage;
					return result;
				} else {
					removeTempFile = true;
				}
			}

			float frameRate = format.getFrameRate();
			int channels = format.getChannels();
			int bytesPerFrame = format.getFrameSize();

			result += "Wav file: " + f.getName() + "\n";
			result += "Sample rate: " + frameRate + "\n";
			result += "Channels: " + channels + "\n";
			result += "Bytes per frame: " + bytesPerFrame + "\n";
			result += "Output: " + this.p.getOutputDescriptors()[outputNumber].name + "\n";

			p = loader.loadPlugin(key, frameRate, adapterFlag);
			setParameters();

			boolean b = p.initialise(channels, blockSize, blockSize);
			if (!b) {
				String errorMessage = "Plugin initialise failed\n";
				result += errorMessage;
				return result;
			}

			float[][] buffers = new float[channels][blockSize];

			boolean done = false;
			boolean incomplete = false;
			int block = 0;

			while (!done) {
				for (int c = 0; c < channels; ++c) {
					for (int i = 0; i < blockSize; ++i) {
						buffers[c][i] = 0.0f;
					}
				}
				int read = readBlock(format, stream, buffers);
				if (read < 0) {
					done = true;
				} else {
					if (incomplete) {
						// An incomplete block is only OK if it's the
						// last one -- so if the previous block was
						// incomplete, we have trouble
						String errorMessage = "Audio file read incomplete! Short buffer detected at " + block * blockSize + "\n";
						result += errorMessage;
						return result;
					}

					incomplete = (read < buffers[0].length);
					RealTime timestamp = RealTime.frame2RealTime(block * blockSize, (int)(frameRate + 0.5));
					Map<Integer, List<Feature>> features = p.process(buffers, timestamp);
					printFeatures(timestamp, outputNumber, features, out);
				}

				++block;
			}
			Map<Integer, List<Feature>> features = p.getRemainingFeatures();
			RealTime timestamp = RealTime.frame2RealTime (block * blockSize, (int)(frameRate + 0.5));
			printFeatures(timestamp, outputNumber, features, out);

			stream.close();
			out.close();
			p.dispose();

			if (removeTempFile) {
				if (f.delete()){
					result += "Deleted temporary file\n";
				} else {
					result += "Deletion of temporary file failed, please clean up the excessive WAV files after processing\n";
				}
			}
		} catch (UnsupportedAudioFileException | IOException | PluginLoader.LoadFailedException e) {
			result += e.getMessage();
			e.printStackTrace();
		}
		return result;
	}

	protected void setParameters() {
		for (Map.Entry<String, Float> entry : parameters.entrySet()) {
			p.setParameter(entry.getKey(), entry.getValue());
		}
	}

	/* Private methods */

	private int readBlock(AudioFormat format, AudioInputStream stream, float[][] buffers) throws java.io.IOException {
		// 16-bit LE signed PCM only
		int channels = format.getChannels();
		byte[] raw = new byte[buffers[0].length * channels * 2];
		int read = stream.read(raw);
		if (read < 0) return read;
		int frames = read / (channels * 2);
		for (int i = 0; i < frames; ++i) {
			for (int c = 0; c < channels; ++c) {
				int ix = i * channels + c;
				int ival = (raw[ix*2] & 0xff) | (raw[ix*2 + 1] << 8);
				float fval = ival / 32768.0f;
				buffers[c][i] = fval;
			}
		}
		return frames;
	}

	private void printFeatures(RealTime frameTime, Integer output, Map<Integer, List<Feature>> features, PrintStream out) {
		if (!features.containsKey(output)) return;

		for (Feature f : features.get(output)) {
			if (f.hasTimestamp) {
				out.print(f.timestamp.toString());
			} else {
				out.print(frameTime.toString());
			}
			if (f.hasDuration) {
				out.print("," + f.duration);
			}
			out.print(":");
			if (Arrays.asList(OutputType.VALUE_ONLY, OutputType.VALUE_AND_LABEL).contains(this.outputType)) {
				for (float v : f.values) {
					out.print(" " + v);
				}
			}
			if (Arrays.asList(OutputType.LABEL_ONLY, OutputType.VALUE_AND_LABEL).contains(this.outputType)) {
				out.print(" " + f.label);
			}
			out.println("");
		}
	}

	class OutputType {
		static final String VALUE_ONLY = "value_only";
		static final String LABEL_ONLY = "label_only";
		static final String VALUE_AND_LABEL = "value_and_label";
	}
}