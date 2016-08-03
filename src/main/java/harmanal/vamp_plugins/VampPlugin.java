package harmanal.vamp_plugins;

import java.io.*;
import java.util.*;
import javax.sound.sampled.*;
import org.vamp_plugins.*;

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
 *  -- ADAPT_INPUT_DOMAIN(0x01), plugin will adapt the sample rate if neccessary
 *  -- ADAPT_CHANNEL_COUNT(0x02), plugin will adapt the # of channels if neccessary
 *  -- ADAPT_BUFFER_SIZE(0x04), plugin will adapt the buffer size itself
 *  -- ADAPT_ALL(0xff); all meaningful adaptations
 *
 * getIdentifier() - unique identifier of the plugin
 *
 * channel - one (or possibly more) audio channels that the plugin will analyze the data from
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

public class VampPlugin {
	public String pluginKey;
	public int outputNumber;

	public Map<String, Float> parameters;
	public int adapterFlag = PluginLoader.AdapterFlags.ADAPT_ALL;

	public int defaultRate = 44100;
	public int blockSize = 16384;

	protected Plugin p;
	protected static PluginLoader loader;

	static {
		loader = PluginLoader.getInstance();
	}

	public static final String[] WRAPPED_PLUGINS = new String[] {
		"nnls-chroma:nnls-chroma",
		"nnls-chroma:chordino"
	};

	public static String printPlugins() {
		String result = new String();
		String[] plugins = loader.listPlugins();
		result += "\n\n> VAMP Plugins loaded successfully\n";
		result += "> Installed plugins (" + plugins.length + "):\n";
		for (int i = 0; i < plugins.length; ++i) {
			result += i + ": " + plugins[i] + "\n";
		}
		return result;
	}

	public static String printWrappedPlugins() {
		String result = new String();
		String[] plugins = loader.listPlugins();
		List<String> wrappedPlugins = new ArrayList<String>();
		for (int i = 0; i < plugins.length; ++i) {
			for (String wrapped_plugin : WRAPPED_PLUGINS) {
				if (plugins[i].equals(wrapped_plugin)) {
					wrappedPlugins.add(i + ": " + plugins[i] + "\n");
				}
			}
		}
		result += "\n> Implemented plugins (" + wrappedPlugins.size() + "):\n";
		for (String s : wrappedPlugins) {
			result += s;
		}
		return result;
	}

	public String printParameters() {
		String result = new String();

		result += "\n> Parameters for " + p.getName() + "\n";
		result += "identifier: " + p.getIdentifier() + "\n";
		result += "description: " + p.getDescription() + "\n";
		result += "version: " + p.getPluginVersion() + "\n";
		Plugin.InputDomain domain = p.getInputDomain();
		if (domain == Plugin.InputDomain.TIME_DOMAIN) {
			result += "This is a time-domain p\n";
		} else {
			result += "This is a frequency-domain p\n";
		}
		ParameterDescriptor[] params = p.getParameterDescriptors();
		result += "Plugin has " + params.length + " parameters\n";
		for (int i = 0; i < params.length; ++i) {
			result += i + ": " + params[i].identifier + " (" + params[i].name + ") SET TO: " + p.getParameter(params[i].identifier) + "\n";
		}
		String[] progs = p.getPrograms();
		result += "Plugin has " + progs.length + " program(s)\n";
		for (int i = 0; i < progs.length; ++i) {
			result += i + ": " + progs[i] + "\n";
		}
		OutputDescriptor[] outputs = p.getOutputDescriptors();
		result += "Plugin has " + outputs.length + " outputNumber(s)\n";
		for (int i = 0; i < outputs.length; ++i) {
			result += i + ": " + outputs[i].identifier + " (sample type: " + outputs[i].sampleType + ")\n";
		}
		return result;
	}

	/*
	 * Analyze audio using Vamp plugin. Courtesy of https://code.soundsoftware.ac.uk/projects/jvamp/repository/entry/host/host.java
	 */
	public String analyze(String inputFile, String outputFile) {
		String result = new String();

		try {
			File f = new File(inputFile);
			AudioInputStream stream = AudioSystem.getAudioInputStream(f);
			AudioFormat format = stream.getFormat();

			PrintStream out = new PrintStream(new FileOutputStream(outputFile, true));

			if (format.getSampleSizeInBits() != 16 || format.getEncoding() != AudioFormat.Encoding.PCM_SIGNED || format.isBigEndian()) {
				String errorMessage = "ERROR: Only 16-bit signed little-endian PCM files supported\n";
				result += errorMessage;
				return result;
			}

			float rate = format.getFrameRate();
			int channels = format.getChannels();
			int bytesPerFrame = format.getFrameSize();

			result += "Wav file: " + f.getName() + "\n";
			result += "Sample rate: " + rate + "\n";
			result += "Channels: " + channels + "\n";
			result += "Bytes per frame: " + bytesPerFrame + "\n";
			result += "Output: " + this.p.getOutputDescriptors()[outputNumber].name + "\n";

			p = loader.loadPlugin(pluginKey, rate, adapterFlag);
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
					RealTime timestamp = RealTime.frame2RealTime(block * blockSize, (int)(rate + 0.5));
					Map<Integer, List<Feature>> features = p.process(buffers, timestamp);
					printFeatures(timestamp, outputNumber, features, out);
				}

				++block;
			}
			Map<Integer, List<Feature>> features = p.getRemainingFeatures();
			RealTime timestamp = RealTime.frame2RealTime (block * blockSize, (int)(rate + 0.5));
			printFeatures(timestamp, outputNumber, features, out);

			stream.close();
			p.dispose();
		} catch (UnsupportedAudioFileException e) {
			result += e.getMessage();
			e.printStackTrace();
		} catch (IOException e) {
			result += e.getMessage();
			e.printStackTrace();
		} catch (PluginLoader.LoadFailedException e) {
			result += e.getMessage();
			e.printStackTrace();
		} finally {
			return result;
		}
	}

	protected void setParameters() {
		for (Map.Entry<String, Float> entry : parameters.entrySet()) {
			p.setParameter(entry.getKey(), entry.getValue());
		}
	}

	private static int readBlock(AudioFormat format, AudioInputStream stream, float[][] buffers) throws java.io.IOException {
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

	private static void printFeatures(RealTime frameTime, Integer output, Map<Integer, List<Feature>> features, PrintStream out) throws IOException {
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
			for (float v : f.values) {
				out.print(" " + v);
			}
			out.print(" " + f.label);
			out.println("");
		}
	}
}