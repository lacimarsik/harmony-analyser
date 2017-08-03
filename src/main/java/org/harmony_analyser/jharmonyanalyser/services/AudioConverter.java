package org.harmony_analyser.jharmonyanalyser.services;

import net.bramp.ffmpeg.*;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import java.io.File;
import java.io.IOException;

/**
 * Class to convert audio files to achieve unified analysis
 */

public class AudioConverter {
	private String convertedSuffix = "-temp.wav";

	public AudioConverter() { }

	public String convertTo16BitSignedLE(String filename) throws IOException {
		File file = new File(filename);
		if (!file.exists()) {
			return "";
		}
		FFmpeg ffmpeg = new FFmpeg("ffmpeg");
		FFprobe ffprobe = new FFprobe("ffprobe");

		FFmpegBuilder builder = new FFmpegBuilder()
			.setInput(filename)
			.overrideOutputFiles(true)
			.addOutput(filename + convertedSuffix)
			.setFormat("wav")
			.setAudioChannels(1)
			.setAudioSampleRate(48_000)
			.setAudioBitRate(32768)
			.setStrict(FFmpegBuilder.Strict.EXPERIMENTAL)
			.done();

		FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
		executor.createJob(builder).run();

		return filename + convertedSuffix;
	}

	/* Public / Package methods */
}