package kiwi.core.source;

import java.util.LinkedList;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;

import kiwi.math.Complex;

public class Source {
	public static final int
		SAMPLERATE 	= 48000,
		SAMPLES		= 1024;
	public static final AudioFormat
		FORMAT = new AudioFormat(
				Encoding.PCM_SIGNED,
				 SAMPLERATE,
				 16,
				 2,
				 4, 
				 SAMPLERATE,
				 true
				);
	
	public final String
		name;
	public final TargetDataLine
		line;
	
	public final Complex[]
		stereo_l = new Complex[SAMPLES],
		stereo_r = new Complex[SAMPLES];
	
	public Source(String name, TargetDataLine line) {
		this.name = name;
		this.line = line;
		for(int i = 0; i < SAMPLES; i ++) {
			stereo_l[i] = new Complex();
			stereo_r[i] = new Complex();
		}
	}
	
	public void poll() {
		byte[] b = new byte[SAMPLES * 4];
		line.read( b, 0, b.length);		
		for(int i = 0; i < SAMPLES; i ++) {
			int j = i * 4;
			stereo_l[i].re = (short)((b[j + 0] << 8) | (b[j + 1] & 0xff)); stereo_l[i].im = 0;
			stereo_r[i].re = (short)((b[j + 2] << 8) | (b[j + 3] & 0xff)); stereo_r[i].im = 0;
		}
	}
	
	public void start() {
		try {
			this.line.open(FORMAT);
			this.line.start();
		} catch (LineUnavailableException lue) {
			lue.printStackTrace();
		}
	}
	
	public void close() {
		this.line.close();
		this.line.flush();
	}
	
	public static float indexToHz(int index) {
		if(index < 0)
			index = 0;
		if(index >= SAMPLES / 2)
			index = SAMPLES / 2;
		return index * SAMPLERATE / SAMPLES;
	}
	
	public static int hzToIndex(float hz) {
		if(hz < 0)
			hz = 0;
		if(hz >= SAMPLERATE / 2)
			hz = SAMPLERATE / 2;
		return (int)(hz / SAMPLERATE * SAMPLES);
	}
	
	public static final List<Source> getAvailableSources() {
		List<Source> sources = new LinkedList<>();
		for(Mixer.Info mixer_info: AudioSystem.getMixerInfo()) {
			Mixer mixer = AudioSystem.getMixer(mixer_info);
			for(Line.Info line_info: mixer.getTargetLineInfo()) 
				if(line_info.getLineClass().isAssignableFrom(TargetDataLine.class))
					try {
						sources.add(new Source(mixer_info.getName(), (TargetDataLine)mixer.getLine(line_info)));
					} catch (LineUnavailableException lue) {
					}
		}
		return sources;
	}
}
