package org.harmony_analyser.jharmonyanalyser.services;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import javax.sound.sampled.*;
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
			File f = new File(filename);
			AudioInputStream stream = AudioSystem.getAudioInputStream(f);
			AudioFormat format = stream.getFormat();
			AudioFormat audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 32, 2, 6, 44100, false);
			AudioInputStream fixedAudioInputStream = AudioSystem.getAudioInputStream(audioFormat, stream);
			AudioSystem.write(fixedAudioInputStream, AudioFileFormat.Type.WAVE, new File(file + "-converted.wav"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/* Public / Package methods */

}

/* Code courtesy of coweb.cc.gatech.edu */

public class JavaSound {
	private static final int BUFFER_SIZE = 16384;//no reasoning on the size,
	private static final boolean DEBUG = true;
	private String errStr;
	public byte[] buffer;//should probably be private
	public AudioFileFormat audioFileFormat;//should probably be private
	private Playback playback; //class variable?  two playing at once?

	public byte[] getBuffer()
	{
		return buffer;
	}

	public AudioFileFormat getAudioFileFormat()
	{
		return audioFileFormat;
	}

	public byte[] asArray()
	{
		return getBuffer();
	}

	public void setBuffer(byte[] newBuffer)
	{
		buffer = newBuffer;
	}

	public void setAudioFileFormat(AudioFileFormat newAudioFileFormat)
	{
		audioFileFormat = newAudioFileFormat;
	}

	public JavaSound()
	{
		buffer = new byte[BUFFER_SIZE];
	}

	public JavaSound(int numSeconds) {
		// Make a new wave file at 22.05K sampling, 16 bits, 1 channel, signed, smallEndian
		AudioFormat audioFormat = new AudioFormat(22050, 16, 1, true, false);

		// We were using the value: numSeconds * 44100 * 16 * 1 sec * 2 * samples/sec/channel * bits/sample * channels = 2*bits
		// but it should be 22050 * 1 * numSeconds * 2 samples/sec/channel * channels * sec * bytes/sample = bytes
		int lengthInBytes = 22050 * 1* numSeconds * 2;

		// lengthInFrames = lengthInBytes/frameSizeInBytes
		// note : frame size is number of bytes required to contain one sample from each channel: channels * samples/channel * bytes/sample
		audioFileFormat = new AudioFileFormat(AudioFileFormat.Type.WAVE, audioFormat, lengthInBytes/(2));
		buffer = new byte[lengthInBytes];
	}

	public JavaSound(String fileName)
	{
		loadFromFile(fileName);
	}

	public AudioInputStream makeAIS() {
		AudioFileFormat.Type fileType = audioFileFormat.getType();
		ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
		int frameSize = audioFileFormat.getFormat().getFrameSize();

		AudioInputStream audioInputStream =
				new AudioInputStream(bais, audioFileFormat.getFormat(),
						buffer.length/frameSize);
		return audioInputStream;
	}

	public void printError(String msg)
	{
		printError(msg, null);
	}

	public void printError(String msg, Exception e) {
		if ((errStr = msg) != null)
		{
			System.err.println(errStr);
			if(e != null) {
				e.printStackTrace();
			}
			System.exit(1);
		}
	}

	public void writeToFile(String fileName) {
		AudioInputStream audioInputStream = makeAIS();
		AudioFileFormat.Type type = audioFileFormat.getType();

		try {
			audioInputStream.reset();
		} catch(Exception e) {
			printError("Unable to reset the Audio stream", e);
		}

		File file = new File(fileName);
		try {
			if(AudioSystem.write(audioInputStream, type, file) == -1) {
				throw new IOException("Problems writing to file");
			}
		}
		catch(Exception e) {
			printError("Problems writing to file" + fileName, e);
		}

		try {
			audioInputStream.close();
		} catch(Exception e) {
			printError("Unable to close the Audio stream");
		}

	}


	public void loadFromFile(String fileName) {
		File file = new File(fileName);

		if(file == null) {
			printError("that file doesn't exist");
		}
		AudioInputStream audioInputStream;
		try {
			audioInputStream = AudioSystem.getAudioInputStream(file);
		} catch(Exception e) {
			printError("Unable to create Audio Stream from file " + fileName + "The file type is probably unsupported." + "Try a WAV, AU, or AIFF file." , e);
			return;
		}


		if(audioInputStream.getFrameLength()>Integer.MAX_VALUE)
		{
			printError("The sound is too long.");
		}
		int bufferSize = (int)audioInputStream.getFrameLength() *
				audioInputStream.getFormat().getFrameSize();
		if(audioInputStream.getFrameLength()>Integer.MAX_VALUE)
			printError("the sound is too long.");

		buffer = new byte[bufferSize];

		int numBytesRead = 0;
		int offset = 0;

		//read all the bytes into the buffer
		while(true)
		{
			try
			{
				numBytesRead =
						audioInputStream.read(buffer, offset, bufferSize);
				if(numBytesRead == -1)//no more data
					break;
				else
					offset += numBytesRead;
			}//try
			catch(Exception e)
			{
				printError("Problems reading the input stream", e);
			}//catch
		}//while

		if(fileName.endsWith(".wav"))
		{
			audioFileFormat =
					new AudioFileFormat(AudioFileFormat.Type.WAVE,
							audioInputStream.getFormat(),
							(int)audioInputStream.getFrameLength());
		}// if wav
		else if(fileName.endsWith(".au"))
		{
			audioFileFormat =
					new AudioFileFormat(AudioFileFormat.Type.AU,
							audioInputStream.getFormat(),
							(int)audioInputStream.getFrameLength());
		}// else if au
		else if (fileName.endsWith(".aif")||fileName.endsWith(".aiff"))
		{
			audioFileFormat =
					new AudioFileFormat(AudioFileFormat.Type.AIFF,
							audioInputStream.getFormat(),
							(int)audioInputStream.getFrameLength());
		}//else if aif
		else
		{
			printError("Unsupported file type");
		}//else unsupported

	}//loadFromFile(String)

	/*
	 *playing... oh let me count the ways
	 */
	public void play()
	{
		playback = new Playback();
		playback.start();
	}

	public void blockingPlay()
	{
		playback = new Playback();
		playback.start();
		while(playback.isAlive()){;}//wait until the sound is done playing
		playback = null;
	}

	//look into changing rate to a float, duration to an int
	public void playAtRateDur(double rate, double durInFrames)
	{
		double newBufferSize =
				durInFrames*(double)getAudioFileFormat().getFormat().getFrameSize();
		if(newBufferSize > Integer.MAX_VALUE)
		{
			printError("The given duration in frames, " + durInFrames +
					"is too large.  Try something less than" +
					Integer.MAX_VALUE/
							getAudioFileFormat().getFormat().getFrameSize());
		}
		playAtRateInRange(rate, 0, (int)durInFrames-1, false);
	}

	//look into changing rate to a float, duration to an int
	public void blockingPlayAtRateDur(double rate, double durInFrames)
	{
		double newBufferSize =
				durInFrames*(double)getAudioFileFormat().getFormat().getFrameSize();
		if(newBufferSize > Integer.MAX_VALUE)
		{
			printError("The given duration in frames, " + durInFrames +
					"is too large.  Try something less than: " +
					Integer.MAX_VALUE/
							getAudioFileFormat().getFormat().getFrameSize());
		}
		playAtRateInRange(rate, 0, (int)durInFrames-1, true);

	}

	public void playAtRateInRange(double rate, int startFrame, int endFrame)
	{
		playAtRateInRange(rate, startFrame, endFrame, false);
	}

	public void blockingPlayAtRateInRange(double rate, int startFrame,
										  int endFrame)
	{
		playAtRateInRange(rate, startFrame, endFrame, true);
	}

	//the big granddaddy of them all!
	public void playAtRateInRange(double rate, int startFrame, int endFrame,
								  boolean isBlocking)
	{

		//before we get started, lets try to check for some obvious errors.
		//maybe we can avoid any array out of bounds exceptions :-D
		if(endFrame > getAudioFileFormat().getFrameLength())
		{
			printError("You are trying to play to frame: " + endFrame +
					".  The sound is only " +
					getAudioFileFormat().getFrameLength() +
					" frames long.");
		}
		if(startFrame < 0)
		{
			printError("You cannot start playing at frame " + startFrame +
					".  Choose 0 to start at the begining.");
		}
		if(endFrame < startFrame)
		{
			printError("You cannot start playing at frame " + startFrame +
					" and end playing at frame " + endFrame + ".  " +
					"The start frame must be before the end frame.");
		}

		if(DEBUG)
		{
			System.out.println("playAtRateInRange(" + rate + ", " +
					startFrame + ", " + endFrame + ", " +
					isBlocking + ")");
			System.out.println("\t(length of sound = " +
					getAudioFileFormat().getFrameLength() + ")");
		}
		//we want to save the current buffer and audioFileFormat
		//so we can return to them when we're finished.
		byte[] oldBuffer = getBuffer();

		//and we want to save the current audioformat so we can return when done
		AudioFileFormat oldAFF = getAudioFileFormat();

		//just to make the code look nicer
		int frameSize = getAudioFileFormat().getFormat().getFrameSize();
		int durInFrames = (endFrame - startFrame) + 1;
		if(DEBUG)
			System.out.println("\tdurInFrames = " + durInFrames);

		//we want to make a new buffer, only as long as we need
		int newBufferSize = durInFrames * frameSize;

		byte[] newBuffer = new byte[newBufferSize];
		for(int i = 0; i <  newBufferSize; i++)
		{
			newBuffer[i] = oldBuffer[(startFrame*frameSize) + i];
		}



		//now we want to make a new audioFormat with the same information
		//except a shorter rate, and a shorter length
		//	AudioFormat newFormat = new AudioFormat();
		//first we have to make sure all our values are small enough
		//for the java sound constructors
		double newSampleRate = oldAFF.getFormat().getSampleRate() * rate;
		if(newSampleRate > Float.MAX_VALUE)
		{
			printError("The sample rate, "+rate+", is too large.  The max" +
					"value that we can handle is: " +
					Float.MAX_VALUE/oldAFF.getFormat().getSampleRate());
		}
		double newFrameRate = oldAFF.getFormat().getFrameRate() * rate;
		if(newFrameRate > Float.MAX_VALUE)
		{
			printError("The frame rate, "+rate+", is too large.  The max" +
					"value that we can handle is: " +
					Float.MAX_VALUE/oldAFF.getFormat().getFrameRate());
		}

		//now we can actually create the new audio format
		AudioFormat newAF =
				new AudioFormat(oldAFF.getFormat().getEncoding(),
						(float)newSampleRate,
						oldAFF.getFormat().getSampleSizeInBits(),
						oldAFF.getFormat().getChannels(),
						oldAFF.getFormat().getFrameSize(),
						(float)newFrameRate,
						oldAFF.getFormat().isBigEndian());

		AudioFileFormat newAFF =
				new AudioFileFormat(oldAFF.getType(), newAF, durInFrames);

		setBuffer(newBuffer);
		setAudioFileFormat(newAFF);
		playback = new Playback();
		playback.start();

		if(isBlocking)
			while(playback.isAlive()){;}//wait until we're done

		setBuffer(oldBuffer);//restore the buffer
		setAudioFileFormat(oldAFF);//restore the file format
	}


	/*
	 *accessing sound information
	 */
	public byte[] getFrame(int frameNum)
	{
		if(frameNum > getAudioFileFormat().getFrameLength())
		{
			printError("That frame, number "+frameNum+", does not exist. "+
					"There are only "+
					getAudioFileFormat().getFrameLength() +
					" frames in the entire sound");
		}

		int frameSize = getAudioFileFormat().getFormat().getFrameSize();
		byte[] theFrame = new byte[frameSize];
		for (int i = 0; i < frameSize; i++)
		{
			theFrame[i] = getBuffer()[frameNum*frameSize+i];
		}
		return theFrame;
	}

	public void setFrame(int frameNum, byte[] theFrame)
	{
		if(frameNum > getAudioFileFormat().getFrameLength())
		{
			printError("That frame, number "+frameNum+", does not exist. "+
					"There are only " +
					getAudioFileFormat().getFrameLength()+
					" frames in the entire sound");
		}
		int frameSize = getAudioFileFormat().getFormat().getFrameSize();
		if(frameSize != theFrame.length)
			printError("Frame size doesn't match, line 383");
		for(int i = 0; i < frameSize; i++)
		{
			buffer[frameNum*frameSize+i] = theFrame[i];
		}
	}

	public int getLengthInFrames()
	{
		return getAudioFileFormat().getFrameLength();
	}

	public int getLeftSample(int frameNum)
	{
		//before we get started, lets make sure that frame exists
		if(frameNum > getAudioFileFormat().getFrameLength())
		{
			printError("You are trying to access the sample at frame: " +
					frameNum + ", but there are only " +
					getAudioFileFormat().getFrameLength() +
					" frames in the file!");
		}

		AudioFormat format = getAudioFileFormat().getFormat();
		int channels;
		if((channels = format.getChannels()) == 1)
		{
			printError("Only stereo sounds have different right and left" +
					"samples.  You are using a mono sound, try " +
					"getSample(" + frameNum + ") instead");
		}
		return getSample(frameNum);
	}
	public int getRightSample(int frameNum)
	{
		//again, before we get started, lets make sure that frame exists
		if(frameNum > getAudioFileFormat().getFrameLength())
		{
			printError("You are trying to access the sample at frame: " +
					frameNum + ", but there are only " +
					getAudioFileFormat().getFrameLength() +
					" frames in the file!");
		}

		AudioFormat format = getAudioFileFormat().getFormat();
		int channels;
		if((channels = format.getChannels())==2)
		{
			printError("Only stereo sounds have different right and left" +
					"samples.  You are using a mono sound, try " +
					"getSample("+frameNum+") instead");
			return -1;
		}
		int sampleSizeInBits = format.getSampleSizeInBits();
		boolean isBigEndian = format.isBigEndian();

		byte[] theFrame = getFrame(frameNum);

		if(format.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED))
		{
			if(sampleSizeInBits == 8)//8 bits == 1 byte
				return theFrame[1];
			if(sampleSizeInBits == 16)
				return bytesToInt16(theFrame, 2, isBigEndian);
			if(sampleSizeInBits == 24)
				return bytesToInt24(theFrame, 3, isBigEndian);
			if(sampleSizeInBits == 32)
				return bytesToInt32(theFrame, 4, isBigEndian);
		}
		else
		{
			//i don't know do alaw and ulaw make stereo/mono distinctions?
			//i can't find any conversion tools that seem to think they have
			//more than one sample per frame
			return getSample(frameNum);
		}
		return -1;
	}

	//same as getLeftSample
	public int getSample(int frameNum)
	{
		//again, before we get started, lets make sure that frame exists
		if(frameNum > getAudioFileFormat().getFrameLength())
		{
			printError("You are trying to access the sample at frame: "
					+ frameNum + ", but there are only " +
					getAudioFileFormat().getFrameLength() +
					" frames in the file!");
		}


		AudioFormat format = getAudioFileFormat().getFormat();
		int sampleSizeInBits = format.getSampleSizeInBits();
		boolean isBigEndian = format.isBigEndian();

		byte[] theFrame = getFrame(frameNum);

		if(format.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED))
		{
			//since we're always returning the left sample,
			//we don't care if we're mono or stereo, left is
			//always first in the frame
			if(sampleSizeInBits == 8)//8 bits == 1 byte
				return theFrame[0];
			if(sampleSizeInBits == 16)
				return bytesToInt16(theFrame, 0, isBigEndian);
			if(sampleSizeInBits == 24)
				return bytesToInt24(theFrame, 0, isBigEndian);
			if(sampleSizeInBits == 32)
				return bytesToInt32(theFrame, 0, isBigEndian);
		}
		if(format.getEncoding().equals(AudioFormat.Encoding.ALAW))
		{
			return alaw2linear(buffer[0]);
		}
		if(format.getEncoding().equals(AudioFormat.Encoding.ULAW))
		{
			return ulaw2linear(buffer[0]);
		}
		if(format.getEncoding().equals(AudioFormat.Encoding.PCM_UNSIGNED))
		{
			//probably can just convert this to PCM_SIGNED, i'll get back
			//to it later, but at this time its not a top priority, probably
			//all of our sounds will be signed pcm data.  the old javasound
			//file just assumed this to be the case.
			printError("PCM_UNSIGNED data is not currently supported");
			return -1;
		}
		else
		{
			printError("unsupported audio encoding: " +
					format.getEncoding() +
					".  Currently only PCM, ALAW and ULAW are supported");
			return -1;
		}
	}//getSample(int)


	/*
	 * editing the sound
	 */
	public void setSample(int frameNum, int sample)
	{
		AudioFormat format = getAudioFileFormat().getFormat();
		int sampleSizeInBits = format.getSampleSizeInBits();
		boolean isBigEndian = format.isBigEndian();

		byte[] theFrame = getFrame(frameNum);

		if(format.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED))
		{
			//since we're always going to se the left sample,
			//we don't care if its mono or stereo, left is
			//always first in the frame
			if(sampleSizeInBits == 8)//8 bits = 1 byte = first cell in array
				theFrame[0] = (byte)sample;
			if(sampleSizeInBits == 16)//2 bytes, first 2 cells in array
			{
				intToBytes16(sample, theFrame, 0, isBigEndian);
				setFrame(frameNum, theFrame);
			}
			if(sampleSizeInBits == 24)
			{
				intToBytes24(sample, theFrame, 0, isBigEndian);
				setFrame(frameNum, theFrame);
			}
			if(sampleSizeInBits == 32)
			{
				intToBytes32(sample, theFrame, 0, isBigEndian);
				setFrame(frameNum, theFrame);
			}
		}//if format == PCM_SIGNED
		else if(format.getEncoding().equals(AudioFormat.Encoding.ALAW))
		{;}
		else if(format.getEncoding().equals(AudioFormat.Encoding.ULAW))
		{;}
		else if(format.getEncoding().equals(AudioFormat.Encoding.PCM_UNSIGNED))
		{;}
		else
		{;}
	}//setSample(int, int)


    /*
     * conversion tools from tritonus (http://www.tritonus.org)
     */

    /*
     *      TConversionTool.java
     */

    /*
     *  Copyright (c) 1999,2000 by Florian Bomers <florian@bome.com>
     *  Copyright (c) 2000 by Matthias Pfisterer <matthias.pfisterer@gmx.de>
     *
     *
     *   This program is free software; you can redistribute it and/or modify
     *   it under the terms of the GNU Library General Public License as published
     *   by the Free Software Foundation; either version 2 of the License, or
     *   (at your option) any later version.
     *
     *   This program is distributed in the hope that it will be useful,
     *   but WITHOUT ANY WARRANTY; without even the implied warranty of
     *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
     *   GNU Library General Public License for more details.
     *
     *   You should have received a copy of the GNU Library General Public
     *   License along with this program; if not, write to the Free Software
     *   Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
     *
     */

	/**
	 * Converts 2 successive bytes starting at <code>byteOffset</code> in
	 * <code>buffer</code> to a signed integer sample with 16bit range.
	 * <p>
	 * For little endian, buffer[byteOffset] is interpreted as low byte,
	 * whereas it is interpreted as high byte in big endian.
	 * <p> This is a reference function.
	 */
	public static int bytesToInt16( byte [] buffer, int byteOffset,
									boolean bigEndian)
	{
		return bigEndian?
				((buffer[byteOffset]<<8) | (buffer[byteOffset+1] & 0xFF)):

				((buffer[byteOffset+1]<<8) | (buffer[byteOffset] & 0xFF));
	}

	/**
	 * Converts 3 successive bytes starting at <code>byteOffset</code> in
	 * <code>buffer</code> to a signed integer sample with 24bit range.
	 * <p>
	 * For little endian, buffer[byteOffset] is interpreted as lowest byte,
	 * whereas it is interpreted as highest byte in big endian.
	 * <p> This is a reference function.
	 */
	public static int bytesToInt24( byte [] buffer, int byteOffset,
									boolean bigEndian)
	{
		return bigEndian?
				((buffer[byteOffset]<<16) // let Java handle sign-bit
						| ((buffer[byteOffset+1] & 0xFF)<<8) // inhibit sign-bit handling
						| (buffer[byteOffset+2] & 0xFF)):

				((buffer[byteOffset+2]<<16) // let Java handle sign-bit
						| ((buffer[byteOffset+1] & 0xFF)<<8) // inhibit sign-bit handling
						| (buffer[byteOffset] & 0xFF));
	}

	/**
	 * Converts a 4 successive bytes starting at <code>byteOffset</code> in
	 * <code>buffer</code> to a signed 32bit integer sample.
	 * <p>
	 * For little endian, buffer[byteOffset] is interpreted as lowest byte,
	 * whereas it is interpreted as highest byte in big endian.
	 * <p> This is a reference function.
	 */
	public static int bytesToInt32( byte [] buffer, int byteOffset,
									boolean bigEndian)
	{
		return bigEndian?
				((buffer[byteOffset]<<24) // let Java handle sign-bit
						| ((buffer[byteOffset+1] & 0xFF)<<16) // inhibit sign-bit handling
						| ((buffer[byteOffset+2] & 0xFF)<<8) // inhibit sign-bit handling
						| (buffer[byteOffset+3] & 0xFF)):

				((buffer[byteOffset+3]<<24) // let Java handle sign-bit
						| ((buffer[byteOffset+2] & 0xFF)<<16) // inhibit sign-bit handling
						| ((buffer[byteOffset+1] & 0xFF)<<8) // inhibit sign-bit handling
						| (buffer[byteOffset] & 0xFF));
	}

	/* u-law to linear conversion table */
	private static short [] u2l = {
			-32124, -31100, -30076, -29052, -28028, -27004, -25980, -24956,
			-23932, -22908, -21884, -20860, -19836, -18812, -17788, -16764,
			-15996, -15484, -14972, -14460, -13948, -13436, -12924, -12412,
			-11900, -11388, -10876, -10364, -9852, -9340, -8828, -8316,
			-7932, -7676, -7420, -7164, -6908, -6652, -6396, -6140,
			-5884, -5628, -5372, -5116, -4860, -4604, -4348, -4092,
			-3900, -3772, -3644, -3516, -3388, -3260, -3132, -3004,
			-2876, -2748, -2620, -2492, -2364, -2236, -2108, -1980,
			-1884, -1820, -1756, -1692, -1628, -1564, -1500, -1436,
			-1372, -1308, -1244, -1180, -1116, -1052, -988, -924,
			-876, -844, -812, -780, -748, -716, -684, -652,
			-620, -588, -556, -524, -492, -460, -428, -396,
			-372, -356, -340, -324, -308, -292, -276, -260,
			-244, -228, -212, -196, -180, -164, -148, -132,
			-120, -112, -104, -96, -88, -80, -72, -64,
			-56, -48, -40, -32, -24, -16, -8, 0,
			32124, 31100, 30076, 29052, 28028, 27004, 25980, 24956,
			23932, 22908, 21884, 20860, 19836, 18812, 17788, 16764,
			15996, 15484, 14972, 14460, 13948, 13436, 12924, 12412,
			11900, 11388, 10876, 10364, 9852, 9340, 8828, 8316,
			7932, 7676, 7420, 7164, 6908, 6652, 6396, 6140,
			5884, 5628, 5372, 5116, 4860, 4604, 4348, 4092,
			3900, 3772, 3644, 3516, 3388, 3260, 3132, 3004,
			2876, 2748, 2620, 2492, 2364, 2236, 2108, 1980,
			1884, 1820, 1756, 1692, 1628, 1564, 1500, 1436,
			1372, 1308, 1244, 1180, 1116, 1052, 988, 924,
			876, 844, 812, 780, 748, 716, 684, 652,
			620, 588, 556, 524, 492, 460, 428, 396,
			372, 356, 340, 324, 308, 292, 276, 260,
			244, 228, 212, 196, 180, 164, 148, 132,
			120, 112, 104, 96, 88, 80, 72, 64,
			56, 48, 40, 32, 24, 16, 8, 0
	};
	public static short ulaw2linear( byte ulawbyte)
	{
		return u2l[ulawbyte & 0xFF];
	}

    /*
     * This source code is a product of Sun Microsystems, Inc. and is provided
     * for unrestricted use.  Users may copy or modify this source code without
     * charge.
     *
     * linear2alaw() - Convert a 16-bit linear PCM value to 8-bit A-law
     *
     * linear2alaw() accepts an 16-bit integer and encodes it as A-law data.
     *
     *              Linear Input Code       Compressed Code
     *      ------------------------        ---------------
     *      0000000wxyza                    000wxyz
     *      0000001wxyza                    001wxyz
     *      000001wxyzab                    010wxyz
     *      00001wxyzabc                    011wxyz
     *      0001wxyzabcd                    100wxyz
     *      001wxyzabcde                    101wxyz
     *      01wxyzabcdef                    110wxyz
     *      1wxyzabcdefg                    111wxyz
     *
     * For further information see John C. Bellamy's Digital Telephony, 1982,
     * John Wiley & Sons, pps 98-111 and 472-476.
     */

	/*
	 * conversion table alaw to linear
	 */
	private static short [] a2l = {
			-5504, -5248, -6016, -5760, -4480, -4224, -4992, -4736,
			-7552, -7296, -8064, -7808, -6528, -6272, -7040, -6784,
			-2752, -2624, -3008, -2880, -2240, -2112, -2496, -2368,
			-3776, -3648, -4032, -3904, -3264, -3136, -3520, -3392,
			-22016, -20992, -24064, -23040, -17920, -16896, -19968, -18944,
			-30208, -29184, -32256, -31232, -26112, -25088, -28160, -27136,
			-11008, -10496, -12032, -11520, -8960, -8448, -9984, -9472,
			-15104, -14592, -16128, -15616, -13056, -12544, -14080, -13568,
			-344, -328, -376, -360, -280, -264, -312, -296,
			-472, -456, -504, -488, -408, -392, -440, -424,
			-88, -72, -120, -104, -24, -8, -56, -40,
			-216, -200, -248, -232, -152, -136, -184, -168,
			-1376, -1312, -1504, -1440, -1120, -1056, -1248, -1184,
			-1888, -1824, -2016, -1952, -1632, -1568, -1760, -1696,
			-688, -656, -752, -720, -560, -528, -624, -592,
			-944, -912, -1008, -976, -816, -784, -880, -848,
			5504, 5248, 6016, 5760, 4480, 4224, 4992, 4736,
			7552, 7296, 8064, 7808, 6528, 6272, 7040, 6784,
			2752, 2624, 3008, 2880, 2240, 2112, 2496, 2368,
			3776, 3648, 4032, 3904, 3264, 3136, 3520, 3392,
			22016, 20992, 24064, 23040, 17920, 16896, 19968, 18944,
			30208, 29184, 32256, 31232, 26112, 25088, 28160, 27136,
			11008, 10496, 12032, 11520, 8960, 8448, 9984, 9472,
			15104, 14592, 16128, 15616, 13056, 12544, 14080, 13568,
			344, 328, 376, 360, 280, 264, 312, 296,
			472, 456, 504, 488, 408, 392, 440, 424,
			88, 72, 120, 104, 24, 8, 56, 40,
			216, 200, 248, 232, 152, 136, 184, 168,
			1376, 1312, 1504, 1440, 1120, 1056, 1248, 1184,
			1888, 1824, 2016, 1952, 1632, 1568, 1760, 1696,
			688, 656, 752, 720, 560, 528, 624, 592,
			944, 912, 1008, 976, 816, 784, 880, 848
	};

	public static short alaw2linear( byte ulawbyte)
	{
		return a2l[ulawbyte & 0xFF];
	}

	/**
	 * Converts a 16 bit sample of type <code>int</code> to 2 bytes in an array.
	 * <code>sample</code> is interpreted as signed (as Java does).
	 * <p>
	 * For little endian, buffer[byteOffset] is filled with low byte of sample,
	 * and buffer[byteOffset+1] is filled with high byte of sample + sign bit.
	 * <p> For big endian, this is reversed.
	 * <p> Before calling this function, it should be assured that
	 * <code>sample</code> is in the 16bit range - it will not be clipped.
	 * <p> This is a reference function.
	 */
	public static void intToBytes16( int sample, byte [] buffer, int byteOffset,
									 boolean bigEndian)
	{
		if (bigEndian)
		{
			buffer[byteOffset++]=( byte ) (sample >> 8);
			buffer[byteOffset]=( byte ) (sample & 0xFF);
		}
		else
		{
			buffer[byteOffset++]=( byte ) (sample & 0xFF);
			buffer[byteOffset]=( byte ) (sample >> 8);
		}
	}

	/**
	 * Converts a 24 bit sample of type <code>int</code> to 3 bytes in an array.
	 * <code>sample</code> is interpreted as signed (as Java does).
	 * <p>
	 * For little endian, buffer[byteOffset] is filled with low byte of sample,
	 * and buffer[byteOffset+2] is filled with the high byte of sample + sign bit.
	 * <p> For big endian, this is reversed.
	 * <p> Before calling this function, it should be assured that
	 * <code>sample</code> is in the 24bit range - it will not be clipped.
	 * <p> This is a reference function.
	 */
	public static void intToBytes24( int sample, byte [] buffer, int byteOffset,
									 boolean bigEndian)
	{
		if (bigEndian)
		{
			buffer[byteOffset++]=( byte ) (sample >> 16);
			buffer[byteOffset++]=( byte ) ((sample >>> 8) & 0xFF);
			buffer[byteOffset]=( byte ) (sample & 0xFF);
		}
		else
		{
			buffer[byteOffset++]=( byte ) (sample & 0xFF);
			buffer[byteOffset++]=( byte ) ((sample >>> 8) & 0xFF);
			buffer[byteOffset]=( byte ) (sample >> 16);
		}
	}

	/**
	 * Converts a 32 bit sample of type <code>int</code> to 4 bytes in an array.
	 * <code>sample</code> is interpreted as signed (as Java does).
	 * <p>
	 * For little endian, buffer[byteOffset] is filled with lowest byte of
	 * sample, and buffer[byteOffset+3] is filled with the high byte of
	 * sample + sign bit.
	 * <p> For big endian, this is reversed.
	 * <p> This is a reference function.
	 */
	public static void intToBytes32( int sample, byte [] buffer, int byteOffset,
									 boolean bigEndian)
	{
		if (bigEndian)
		{
			buffer[byteOffset++]=( byte ) (sample >> 24);
			buffer[byteOffset++]=( byte ) ((sample >>> 16) & 0xFF);
			buffer[byteOffset++]=( byte ) ((sample >>> 8) & 0xFF);
			buffer[byteOffset]=( byte ) (sample & 0xFF);
		}
		else
		{
			buffer[byteOffset++]=( byte ) (sample & 0xFF);
			buffer[byteOffset++]=( byte ) ((sample >>> 8) & 0xFF);
			buffer[byteOffset++]=( byte ) ((sample >>> 16) & 0xFF);
			buffer[byteOffset]=( byte ) (sample >> 24);
		}
	}



	public int getLength()
	{
		return buffer.length;
	}

	public int getChannels()
	{
		return getAudioFileFormat().getFormat().getChannels();
	}




	/*
	 *strings
	 */
	public String toString()
	{
		return getAudioFileFormat().getFormat().toString();
	}

	public String justATaste()
	{
		return "Sorry, justATaste is not implemented at this time.";
	}

	public String justABufferTaste(byte[] b)
	{
		return "Sorry, justABufferTaste is not implemented at this time.";
	}


	public class Playback extends Thread
	{
		SourceDataLine line;
		boolean playing = true;

		private void shutDown(String message, Exception e)
		{
			if ((errStr = message) != null)
			{
				System.err.println(errStr);
				e.printStackTrace();
			}
			playing = false;
		}

		public void run()
		{

			//get something to play
			AudioInputStream audioInputStream = makeAIS();
			if(audioInputStream == null)
			{
				shutDown("There is no input stream to play", null);
				return;
			}

			//reset stream to the begining
			try
			{
				audioInputStream.reset();
			}
			catch(Exception e)
			{
				shutDown("Problems resetting the stream\n", e);
				return;
			}

			//define the required attributes for the line
			//make sure a compatible line is supported

			DataLine.Info info = new DataLine.Info(SourceDataLine.class,
					audioFileFormat.getFormat());
			if(!AudioSystem.isLineSupported(info))
			{
				shutDown("Line matching " + info + "not supported.", null);
				return;
			}

			//get and open the source data line for playback
			try
			{
				line = (SourceDataLine) AudioSystem.getLine(info);
				line.open(audioFileFormat.getFormat(), BUFFER_SIZE);
			}
			catch(LineUnavailableException e)
			{
				shutDown("Unable to open the line: ", e);
				return;
			}

			//play back the captured data
			int frameSizeInBytes = audioFileFormat.getFormat().getFrameSize();
			int bufferLengthInBytes = line.getBufferSize();
			int bufferLengthInFrames = bufferLengthInBytes / frameSizeInBytes;
			//question.. the sun file has the following 2 lines:
			//int bufferLengthInFrames = line.getBufferSize() / 8;
			//int bufferLengthInBytes =
			//	bufferLengthInFrames * frameSizeInBytes;
			// but, (SourceDataLine).getBufferSize() claims to return the size
			// of the buffer in bytes.
			byte[] data = new byte[bufferLengthInBytes];
			int numBytesRead = 0;

			//start the source data line
			line.start();

			while(playing)
			{
				try
				{
					if((numBytesRead = audioInputStream.read(data)) == -1)
					{
						break;//end of audioInputStream
					}
					int numBytesRemaining = numBytesRead;
					while(numBytesRemaining > 0)
					{
						numBytesRemaining -=
								line.write(data, 0, numBytesRemaining);
					}//while
				}//try
				catch(Exception e)
				{
					shutDown("Error during playback: ", e);
					break;
				}//catch
			}//while

			//we reached the end of the stream. let the data play out, then
			//stop and close the line.
			if(playing)
				line.drain();

			line.stop();
			line.close();
			line = null;
			shutDown(null, null);
			if(DEBUG)
				System.out.println("exiting run method");
			return;
		}//run()

	}//end class Playback

	public static void main(String args[])
	{


	/*
	 * 16 bit, stereo, big endian (pretty short)
	 */
		JavaSound mysound1 =
				new JavaSound("/Users/ellie/Desktop/ellie/JavaSoundDemo/audio/22-new.aif");
		System.out.println("new file format: " + mysound1);

	/*
	 * 16 bit, mono, little endian (also pretty short)
	 */
		JavaSound mysound2 =
				new JavaSound("/Users/ellie/Desktop/ellie/JavaSoundDemo/audio/1-welcome.wav");
		System.out.println("new file format: " + mysound2);



	/*
	 * testing compatibility with old file, getFrame, getSample, etc.
	 * now we're 0-indexed... is this good? i hope so....
	 */
		OldJavaSound oldSound = new OldJavaSound();
		oldSound.loadFromFile("/Users/ellie/Desktop/ellie/JavaSoundDemo/audio/1-welcome.wav");
		System.out.println("old file format: " + oldSound.audioFormat);

		System.out.println("\nnew file frame 0: " + mysound2.getFrame(0));
		System.out.println("\tnew file sample 0: " + mysound2.getSample(0));
		System.out.println("old file frame 1: " + oldSound.getFrame(1));
		System.out.println("\told file sample 1: " + oldSound.getSample(1));

		System.out.println("\new file frame 1: " + mysound2.getFrame(1));
		System.out.println("\tnew file sample 1: " + mysound2.getSample(1));
		System.out.println("old file frame 2: " + oldSound.getFrame(2));
		System.out.println("\told file sample 2: " + oldSound.getSample(2));

		System.out.println("\nnew file frame 2: " + mysound2.getFrame(2));
		System.out.println("\tnew file sample 2: "+mysound2.getSample(2));
		System.out.println("old file frame 3: " + oldSound.getFrame(3));
		System.out.println("\told file sample 3: "+oldSound.getSample(3));

		System.out.println("\nnew file set sample 2: 14");
		mysound2.setSample(2, 14);
		System.out.println("\tchecking value: " + mysound2.getSample(2));


	/*
	 * general blocking play, stereo
	 */
		System.out.println("\nblocking play:  stereo");
		mysound1.blockingPlay();

	/*
	 * testing playAtRateDur
	 */
		System.out.println("\nblocking - double the rate");
		mysound1.blockingPlayAtRateDur
				(2, mysound1.getAudioFileFormat().getFrameLength());

		System.out.println("\nblocking - back to the original sound");
		mysound1.blockingPlay();

		System.out.println("\nblocking - half the duration");
		mysound1.blockingPlayAtRateDur
				(1, mysound1.getAudioFileFormat().getFrameLength()/2);

		System.out.println("\nnon-blocking - back to original sound");
		mysound1.play();

		System.out.println("\nblocking - half the rate");
		mysound1.blockingPlayAtRateDur
				(.5, mysound1.getAudioFileFormat().getFrameLength());

		System.out.println("\nblocking - only the middle ");
		mysound1.blockingPlayAtRateInRange(1, 35811, 71623);


	/*
	 * test for a really long sound ~2.5 minutes
	 *
	 System.out.println("\ncreating a new sound:  big yellow taxi");
	 JavaSound longSound =
	 new JavaSound("/Users/ellie/Desktop/ellie/Big Yellow Taxi.wav");

	 System.out.println("\n blocking - long wav");
	 longSound.blockingPlay();
	*/

		System.out.println("\nexiting main");
		System.exit(0);

	}//main

}//end class JavaSound