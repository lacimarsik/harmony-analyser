package org.harmony_analyser.jharmonyanalyser.services;

import net.bramp.ffmpeg.*;
import net.bramp.ffmpeg.builder.FFmpegBuilder;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.io.*;

/**
 * Class to orchestrate all levels of audio analysis, using available plugins and visualizations
 */

@SuppressWarnings("SameParameterValue")
public class AudioConverter {

	public AudioConverter() { }

	public void convertTo16BitSignedLE(String filename) throws UnsupportedAudioFileException {
		File file = new File(filename);
		if (!file.exists())
			return;
		try {
			FFmpeg ffmpeg = new FFmpeg("/usr/bin/ffmpeg");
			FFprobe ffprobe = new FFprobe("/usr/bin/ffprobe");

			FFmpegBuilder builder = new FFmpegBuilder()
				.setInput(filename)     // Filename, or a FFmpegProbeResult
				.overrideOutputFiles(true) // Override the output if it exists
				.addOutput(filename + "test.wav")   // Filename for the destination
				.setFormat("wav")        // Format is inferred from filename, or can be set
				.setAudioChannels(1)         // Mono audio
				.setAudioSampleRate(48_000)  // at 48KHz
				.setAudioBitRate(32768)      // at 32 kbit/s
				.setStrict(FFmpegBuilder.Strict.EXPERIMENTAL) // Allow FFmpeg to use experimental specs
				.done();

			FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
			executor.createJob(builder).run();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/* Public / Package methods */
}