package kiwi.core;

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

public class Media {
	public static final int
		SAMPLERATE = 44000 / 2;
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
	
	public Media(String name, TargetDataLine line) {
		this.name = name;
		this.line = line;
	}
	
	public void enable(boolean enable) {
		if(enable) {
			try {
				this.line.open(FORMAT);
				this.line.start();
			} catch (LineUnavailableException lue) {
				lue.printStackTrace();
			}
		} else {
			this.line.close();
			this.line.flush();
		}
	}
	
	public void poll(
			Complex[] l_channel,
			Complex[] r_channel
			) {
		int s = Math.min(
				l_channel.length,
				r_channel.length
				);		
		byte[] b = new byte[s * 4];
		line.read( b, 0, b.length);		
		for(int i = 0; i < s; i ++) {
			int j = i * 4;
			l_channel[i].re = (short)((b[j + 0] << 8) | (b[j + 1] & 0xff)); l_channel[i].im = 0;
			r_channel[i].re = (short)((b[j + 2] << 8) | (b[j + 3] & 0xff)); r_channel[i].im = 0;
		}
	}
	
	public static final List<Media> getAvailableMedias() {
		List<Media> media = new LinkedList<>();
		for(Mixer.Info mixer_info: AudioSystem.getMixerInfo()) {
			Mixer mixer = AudioSystem.getMixer(mixer_info);
			for(Line.Info line_info: mixer.getTargetLineInfo()) 
				if(line_info.getLineClass().isAssignableFrom(TargetDataLine.class))
					try {
						media.add(new Media(mixer_info.getName(), (TargetDataLine)mixer.getLine(line_info)));
					} catch (LineUnavailableException lue) {
					}
		}
		return media;
	}
}
