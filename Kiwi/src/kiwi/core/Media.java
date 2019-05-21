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

import kiwi.math.Vector;

public class Media {
	public static final AudioFormat
		FORMAT = new AudioFormat(
				Encoding.PCM_SIGNED,
				 48000,
				 16,
				 2,
				 4, 
				 48000, 
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
			Vector[] l_channel,
			Vector[] r_channel
			) {
		int s = Math.min(
				l_channel.length,
				r_channel.length
				);		
		byte[] b = new byte[s * 4];
		line.read( b, 0, b.length);		
		for(int i = 0; i < s; i ++) {
			int j = i * 4;
			l_channel[i].X = (short)((b[j + 0] << 8) | (b[j + 1] & 0xff)); l_channel[i].Y = 0;
			r_channel[i].X = (short)((b[j + 2] << 8) | (b[j + 3] & 0xff)); l_channel[i].Y = 0;
		}
	}
	
	public void poll(
			Vector[] channel
			) {
		int s = channel.length;
		byte[] b = new byte[s * 2];
		line.read( b, 0, b.length);
		for(int i = 0; i < s; i ++) {
			int j = i * 2;
			channel[i].X = (short)((b[j + 0] << 8) | (b[j + 1] & 0xff)); channel[i].Y = 0;
		}		
	}
	
	public static final List<Media> getAvailableMedia() {
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
