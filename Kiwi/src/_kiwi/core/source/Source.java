package _kiwi.core.source;

import java.util.LinkedList;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;

import _kiwi.core.Updateable;

public class Source implements Updateable {
	public static final int
		SAMPLERATE 	=  44100,
		SAMPLES     =  1024;
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
	protected final TargetDataLine
		line;	
	protected double[]
		stereo_l_buffer,
		stereo_r_buffer;
	public final double[]
		stereo_l = new double[SAMPLES],
		stereo_r = new double[SAMPLES];
	protected boolean
		poll,
		push,
		open;
	
	public Source(String name, TargetDataLine line) {
		this.name = name;
		this.line = line;		
	}

	@Override
	public void update(UpdateContext context) {
		if(open) {
			poll();
			if(poll)
				push();
		}
	}
	
	public void poll() {
		int 
			samples = Math.min(line.available() / 4, SAMPLES);	
		if(samples > 0) {
			byte[]
					sample_buffer = new byte[samples * 4];
			stereo_l_buffer = new double[samples];
			stereo_r_buffer = new double[samples];
			line.read(sample_buffer, 0, sample_buffer.length);			
			for(int i = 0; i < samples; i ++) {			
				int sample_index = i * 4;
				short
					stereo_l_sample = (short)((sample_buffer[sample_index + 0] << 8) | (sample_buffer[sample_index + 1] & 0xff)),
					stereo_r_sample = (short)((sample_buffer[sample_index + 2] << 8) | (sample_buffer[sample_index + 3] & 0xff));
				stereo_l_buffer[i] = stereo_l_sample;
				stereo_r_buffer[i] = stereo_r_sample;
			}
			poll = true;
		} else {
			stereo_l_buffer = new double[0];
			stereo_r_buffer = new double[0];
			poll = false;
		}
	}
	
	public void push() {
		push(
				stereo_l_buffer,
				stereo_r_buffer
				);
	}
	
	public void push(
			double[] stereo_l_buffer,
			double[] stereo_r_buffer
			) {
		int samples = Math.min(
				stereo_l_buffer.length,
				stereo_r_buffer.length
				);
		if(samples > 0 && samples <= SAMPLES) {
			for(int i = 0; i < SAMPLES - samples; i ++) {
				stereo_l[i] = stereo_l[i + samples];
				stereo_r[i] = stereo_r[i + samples];
			}
			for(int i = 0; i < samples; i ++) {
				stereo_l[SAMPLES - samples + i] = stereo_l_buffer[i];
				stereo_r[SAMPLES - samples + i] = stereo_r_buffer[i];
			}
			push = true;
		} else
			push = false;
	}
	
	public void open() {
		if(!open) {
			try {
				line.open(FORMAT);
				line.start();
			} catch (LineUnavailableException lue) {
				lue.printStackTrace();
			}
			open = true;
		}
	}
	
	public void stop() {
		if( open) {
			open = false;
			line.close();
			line.flush();
			poll = false;
			push = false;
		}
	}
	
	public boolean isPoll() {
		return poll;
	}
	
	public boolean isPush() {
		return push;
	}
	
	public boolean isOpen() {
		return open;
	}
	
	public static double indexToHz(int index) {
		if(index < 0)
			index = 0;
		if(index >= SAMPLES / 2)
			index = SAMPLES / 2;
		return index * SAMPLERATE / SAMPLES;
	}
	
	public static int hzToIndex(double hz) {
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
