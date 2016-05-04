package harmanal;

import java.io.*;
import java.util.*;
import javax.sound.sampled.*;

import org.vamp_plugins.*;

/*
 * Notes: Vamp plugins using JVamp wrappers
 * 
 * - extracts made from Vamp SDK documentation
 * 
 * Plugin - base class for a Vamp plugin
 * PluginLoader - singleton class to obtain a concrete plugin based on:
 *  - key ("plugin_set:name_of_plugin")
 *  - sample rate
 *  - adapter flag, one or more of
 *  -- ADAPT_INPUT_DOMAIN(0x01), plugin will adapt the sample rate if neccessary
 *  -- ADAPT_CHANNEL_COUNT(0x02), plugin will adapt the # of channels if neccessary
 *  -- ADAPT_BUFFER_SIZE(0x04), plugin will adapt the buffer size itself
 *  -- ADAPT_ALL(0xff); all meaningful adaptations
 * getIdentifier() - unique identifier of the plugin
 * channel - one (or possibly more) audio channels that the plugin will analyze the data from
 * initialise(# of channels, step size, block size) - initializes the plugin
 * process(float[][] inputBuffers, RealTime timestamp)
 *  - multiple calls to process, each time passed a block size # of samples, stepping by step size # of samples
 *  -- step size is a step between the first samples of the block
 *  - inputBuffers[0][blockSize-1]] - the last sample from the block on the first channel
 *  - returns FeatureSet
 *  - last block may be partial (filled with zeros)!
 * getInputDomain() - the type of the input
 *  - time domain: classical PCM samples audio, float amplitude samples
 *  - frequency domain: already done fourier transform on each block of input, float amplitude&phase samples
 *    in the function of frequency. Usually size step smaller than block size because of the discontinuities
 *    after the FFT
 * getRemainingFeatures() - after all the process calls, gathers any remaining features
 * getPreferredBlockSize(), getPreferredStepSize() - if doesn't matter, returning 0
 * FeatureSet
 * - Map: key: id of output, value: FeatureList (List of Features)
 * - Each feature: optional timestamp and 0+ values (perhaps with labels)
 * getOutputDescriptors() - returns a vector of OutputDescriptors
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
 * Feature
 * - hasTimestamp
 * - hasDuration
 * getParameterDescriptors() - returns a vector of ParameterDescriptors
 * ParameterDescriptor
 * - identified by identifier
 * - getParameter(), setParameter()
 * - values of the parameters are always floats
 * - minValue, maxValue
 * - can be quantized as well, the quantized steps can be even names: valueNames
 * - have to be set before the plugin is initialized, after they can not be changed!
 * - by setting parameters, some of the properties of plugin can be changed, like
 * -- sampleRate, sampleType, binCount, minValue, maxValue, preferredStepSize, preferredBlockSize
 * Programs
 * - predefined set of parameters that work well for certain sorts of tasks
 * - getProgramNames, getCurrentProgram, selectProgram
 * 
 * NNLS Chroma Plugin
 * 
 * - spectral frame-wise input -> log-frequency spectrum
 * - first there are 3 bins per semitone, bins 2, 5, 8 correspond to semitones
 * - tuning so that out-tuned input is well-analyzed as well
 * - spectral whitening, NNLS approximate transcription - spectrum mapped to 12 bins
 * parameters
 * - use NNLS chroma transcription: on or off (for linear spectral mapping)
 * -- preferred: on
 * - spectral roll-on: removing the low-frequency noise, useful for quiet recordings
 * -- preferred: 1.0%
 * - tuning mode: global or local, how to find out the tuning - locally or globally
 * -- preferred: global
 * - spectral whitening: 0.0-1.0 defines how much is the log-frequency spectrum whitened
 * -- preferred: 1.0
 * - spectral shape: 0.5-0.9 - shape of a note (amplitude has a decreasing pattern) 
 * - preferred: 0.7
 * outputs
 * - log-frequency spectrum (3 bins per semitone)
 * - tuned log-frequency spectrum
 * - semitone spectrum
 * - bass chromagram (12-dimensional)
 * - chromagram (12-dimensional)
 * - chromagram and bass chromagram
 * - consonance estimate
 * 
 */

/**
 * Wrapper for NNLS Chroma VAMP plugin
 */

public class NNLSPlugin {
	
	Plugin plugin;

	public NNLSPlugin() {
		
		try {
			System.out.println("Plugin crash course started");
			
			int sampleRate = 44100;
			int adapterFlag = 0xff;
			
			plugin = PluginLoader.getInstance().loadPlugin("nnls-chroma:nnls-chroma", sampleRate, adapterFlag);
			
			System.out.println("Plugin " + plugin.getName() + " loaded");
			
			plugin.setParameter("useNNLS", 1);
			plugin.setParameter("rollon", 1);
			plugin.setParameter("tuningMode", 0);
			plugin.setParameter("whitening", 1);
			plugin.setParameter("s", (float) 0.7);
			plugin.setParameter("chromanormalize", 0);
			
			System.out.println("All parameters set.");
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}
	
	public void analyze(String inputFile, String outputFile) {
		
		try {
			// set before analyzing!
			int sampleRate = 22050;
			//int sampleRate = 44100;
			
			
			File fileIn = new File(/*"resources/" + */inputFile);

			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(fileIn);
			
			System.out.println("Wav file opened.");
			
			System.out.println("Sample rate: " + audioInputStream.getFormat().getSampleRate());
			System.out.println("Sample size: " + audioInputStream.getFormat().getSampleSizeInBits());
			System.out.println("Channels: " + audioInputStream.getFormat().getChannels());
			
			int channels = audioInputStream.getFormat().getChannels();
			
			int stepSize = 16384;
			int blockSize = 16384;
			
			if (plugin.initialise(channels, stepSize, blockSize)) {
				System.out.println("Initialized");
			} else {
				throw new VampPluginUsageFailedException("Plugin " + plugin.getName() + " failed to initialize.");
			}
			
			
			int bytesPerFrame = audioInputStream.getFormat().getFrameSize();
			int numBytes = 16384 * bytesPerFrame; 
			byte[] audioBytes = new byte[numBytes];
			int numBytesRead = 0;
			
			int output = 3;
			System.out.println("Output: " + plugin.getOutputDescriptors()[3].name + ".");
			
			List<Feature> featureList;
			
			FileWriter fstream = new FileWriter(/*"resources/" + */outputFile);
			
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
				out.write(feature.timestamp + ": ");
				for (int j = 0; j < feature.values.length; j++) {
					out.write(feature.values[j] + " ");
				}
				out.write("\n");
			}

			audioInputStream.close();

			out.close();

			plugin.dispose();
		} catch (UnsupportedAudioFileException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		} catch (VampPluginUsageFailedException e) {

			e.printStackTrace();
		}
	}
	
}

class VampPluginUsageFailedException extends Exception {

	private static final long serialVersionUID = 1L; 
	
	VampPluginUsageFailedException(String message) {
		super(message);
	}
	
};
