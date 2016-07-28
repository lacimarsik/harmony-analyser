package harmanal.vamp_plugins;

import java.io.*;
import java.util.*;
import javax.sound.sampled.*;
import org.vamp_plugins.*;

/*
 * Generic Vamp plugin class using JVamp wrappers
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
	Plugin plugin;

	int sampleRate;
	int adapterFlag = PluginLoader.AdapterFlags.ADAPT_ALL;
	int stepSize = 16384;
	int blockSize = 16384;

	int output;
	OutputType outputType;

	public static final String[] WRAPPED_PLUGINS = new String[] {
		"nnls-chroma:nnls-chroma",
		"nnls-chroma:chordino"
	};

	public static String getPlugins() {
		String result = new String();
		PluginLoader loader = PluginLoader.getInstance();
		String[] plugins = loader.listPlugins();
		result += "\n\n> VAMP Plugins loaded successfully\n";
		result += "> Installed plugins (" + plugins.length + "):\n";
		for (int i = 0; i < plugins.length; ++i) {
			result += i + ": " + plugins[i] + "\n";
		}
		return result;
	}

	public static String getWrappedPlugins() {
		String result = new String();
		PluginLoader loader = PluginLoader.getInstance();
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

	public String getParameters() {
		String result = new String();
		result += "\n> Parameters for " + plugin.getName() + "\n";
		result += "identifier: " + plugin.getIdentifier() + "\n";
		result += "description: " + plugin.getDescription() + "\n";
		result += "version: " + plugin.getPluginVersion() + "\n";
		Plugin.InputDomain domain = plugin.getInputDomain();
		if (domain == Plugin.InputDomain.TIME_DOMAIN) {
			result += "This is a time-domain plugin\n";
		} else {
			result += "This is a frequency-domain plugin\n";
		}
		ParameterDescriptor[] params = plugin.getParameterDescriptors();
		result += "Plugin has " + params.length + " parameters\n";
		for (int i = 0; i < params.length; ++i) {
			result += i + ": " + params[i].identifier + " (" + params[i].name + ") SET TO: " + plugin.getParameter(params[i].identifier) + "\n";
		}
		String[] progs = plugin.getPrograms();
		result += "Plugin has " + progs.length + " program(s)\n";
		for (int i = 0; i < progs.length; ++i) {
			result += i + ": " + progs[i] + "\n";
		}
		OutputDescriptor[] outputs = plugin.getOutputDescriptors();
		result += "Plugin has " + outputs.length + " output(s)\n";
		for (int i = 0; i < outputs.length; ++i) {
			result += i + ": " + outputs[i].identifier + " (sample type: " + outputs[i].sampleType + ")\n";
		}
		return result;
	}

	public void analyze(String inputFile, String outputFile) {
		try {
			File fileIn = new File(inputFile);

			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(fileIn);
			System.out.println("Wav file opened.");
			System.out.println("Sample rate: " + audioInputStream.getFormat().getSampleRate());
			System.out.println("Sample size: " + audioInputStream.getFormat().getSampleSizeInBits());
			System.out.println("Channels: " + audioInputStream.getFormat().getChannels());

			int channels = audioInputStream.getFormat().getChannels();

			if (plugin.initialise(channels, stepSize, blockSize)) {
				System.out.println("Initialized");
			} else {
				throw new VampPluginUsageFailedException("Plugin " + plugin.getName() + " failed to initialize.");
			}

			int bytesPerFrame = audioInputStream.getFormat().getFrameSize();
			int numBytes = blockSize * bytesPerFrame;
			byte[] audioBytes = new byte[numBytes];
			int numBytesRead = 0;

			System.out.println("Output: " + plugin.getOutputDescriptors()[output].name + ".");
			List<Feature> featureList;

			FileWriter fstream = new FileWriter(outputFile);
			BufferedWriter out = new BufferedWriter(fstream);

			float[][] inputBuffer = new float[channels][];
			for (int i = 0; i < channels; i++) {
				inputBuffer[i] = new float[blockSize];
			}

			RealTime timeStamp;
			int currentStep = 0;
			int actualChannel = 0;
			float sample = 0;

			while ((numBytesRead = audioInputStream.read(audioBytes)) != -1) {
				for (int i = 0; i < numBytesRead-1; i+=2) {
					sample = ((audioBytes[i] & 0xFF) | (audioBytes[i + 1] << 8)) / 32768.0F;

					inputBuffer[actualChannel][i / 4] = sample;
					if (actualChannel == 0) {
						actualChannel = 1;
					} else {
						actualChannel = 0;
					}
				}

				timeStamp = RealTime.frame2RealTime(currentStep * stepSize, sampleRate);
				plugin.process(inputBuffer, timeStamp).get(0);

				currentStep++;
			}
			featureList = plugin.getRemainingFeatures().get(output);

			for (Iterator<Feature> iterator = featureList.iterator(); iterator.hasNext();) {
				Feature feature = (Feature) iterator.next();
				if (outputType == OutputType.ARRAY) {
					out.write(feature.timestamp + ": ");
					for (int j = 0; j < feature.values.length; j++) {
						out.write(feature.values[j] + " ");
					}
				} else if (outputType == OutputType.LABEL) {
					out.write(feature.timestamp + ",");
					out.write("\"");
					out.write(feature.label);
					out.write("\"");
				} else {
					throw new UnsupportedValueType("Plugin " + plugin.getName() + " failed to analyse - unsupported value type.");
				}
				out.write("\n");
			}
			audioInputStream.close();
			out.close();
			plugin.dispose();
		} catch (UnsupportedValueType e) {
			e.printStackTrace();
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (VampPluginUsageFailedException e) {
			e.printStackTrace();
		}
	}
}

enum OutputType {
	ARRAY, LABEL
}

class VampPluginUsageFailedException extends Exception {
	private static final long serialVersionUID = 1L;

	VampPluginUsageFailedException(String message) {
		super(message);
	}
};

class UnsupportedValueType extends Exception {
	private static final long serialVersionUID = 1L;

	UnsupportedValueType(String message) {
		super(message);
	}
};
