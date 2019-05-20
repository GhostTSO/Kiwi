package kiwi;

import java.nio.ByteBuffer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

public class Demo {
	
	public static void main(String[] args) {		
		int i = 0;
		for(Mixer.Info mixer_info: AudioSystem.getMixerInfo()) {
			System.out.println("" + i + ":" + mixer_info);			
			Mixer mixer = AudioSystem.getMixer(mixer_info);
			System.out.println("  Source");
			for(Line.Info line_info: mixer.getSourceLineInfo())
				if(line_info.getLineClass().isAssignableFrom(SourceDataLine.class)) {
					System.out.println("    " + line_info);
					DataLine.Info data_line_info = (DataLine.Info)line_info;
					for(AudioFormat format: data_line_info.getFormats())
						System.out.println("      " + format);
				}
			System.out.println("  Target");
			for(Line.Info line_info: mixer.getTargetLineInfo())
				if(line_info.getLineClass().isAssignableFrom(TargetDataLine.class)) {
					System.out.println("    " + line_info);
					DataLine.Info data_line_info = (DataLine.Info)line_info;
					for(AudioFormat format: data_line_info.getFormats())
						System.out.println("      " + format);
				}
			i ++;
		}
		
		
		Mixer.Info mixer_info = AudioSystem.getMixerInfo()[16];
		Mixer mixer = AudioSystem.getMixer(mixer_info);
		DataLine.Info line_info = (DataLine.Info)mixer.getTargetLineInfo()[0];
		
		try {		
			AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 48000, 16, 2, 4, 60, true);
			TargetDataLine line = (TargetDataLine) mixer.getLine(line_info);
			line.open(format);
			line.start();
			
			System.out.println(format);
			
			new Thread(() -> {
				ByteBuffer b2s = ByteBuffer.allocate(2);
				while(true) {
					byte[] buffer = new byte[4];					
					int b = line.read(buffer, 0, buffer.length);
					
					b2s.clear();
					b2s.put(buffer[0]);
					b2s.put(buffer[1]);
					b2s.flip();
					short l = b2s.getShort();
					
					b2s.clear();
					b2s.put(buffer[2]);
					b2s.put(buffer[3]);
					b2s.flip();
					short r = b2s.getShort();
					
					if(b > 0) {
						System.out.println("L: " + l);
						System.out.println("R: " + r);
					}
				}
			}).start();
			
		} catch (LineUnavailableException lue) {
			lue.printStackTrace();
		}	
		
	}
}
