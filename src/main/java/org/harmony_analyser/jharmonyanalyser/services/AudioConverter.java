package org.harmony_analyser.jharmonyanalyser.services;


import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Class to orchestrate all levels of audio analysis, using available plugins and visualizations
 */

@SuppressWarnings("SameParameterValue")
public class AudioConverter {
	private float frameRate;
	private int bytesPerFrame;

	public AudioConverter(float frameRate, int bytesPerFrame) {
		this.frameRate = frameRate;
		this.bytesPerFrame = bytesPerFrame;
	}

	public void convertTo16BitSignedLE(String filename) {
		File file = new File(filename);
		if (!file.exists())
			return;
		try {
			long fileSize = file.length();
			int frameSize = 1024;
			long numFrames = fileSize / frameSize;
			AudioFormat audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 24, 2, bytesPerFrame, frameRate, false);
			AudioInputStream audioInputStream = new AudioInputStream(new FileInputStream(file), audioFormat, numFrames);
			AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, new File(file + "-converted.wav"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/* Public / Package methods */

}
