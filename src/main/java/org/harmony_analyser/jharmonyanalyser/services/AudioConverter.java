package org.harmony_analyser.jharmonyanalyser.services;

import net.bramp.ffmpeg.*;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import java.io.File;
import java.io.IOException;

/**
 * Class to convert audio files to achieve unified analysis
 */

public class AudioConverter {

	public AudioConverter() { }

	public void convertTo16BitSignedLE(String filename) {
		File file = new File(filename);
		if (!file.exists())
			return;
		try {
			FFmpeg ffmpeg = new FFmpeg("/usr/bin/ffmpeg");
			FFprobe ffprobe = new FFprobe("/usr/bin/ffprobe");

			FFmpegBuilder builder = new FFmpegBuilder()
				.setInput(filename)
				.overrideOutputFiles(true)
				.addOutput(filename + "test.wav")
				.setFormat("wav")
				.setAudioChannels(1)
				.setAudioSampleRate(48_000)
				.setAudioBitRate(32768)
				.setStrict(FFmpegBuilder.Strict.EXPERIMENTAL)
				.done();

			FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
			executor.createJob(builder).run();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/* Public / Package methods */
}