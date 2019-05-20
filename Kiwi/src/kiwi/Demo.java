package kiwi;

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
		
		
//		Mixer.Info mixer_info = AudioSystem.getMixerInfo()[16];
//		Mixer mixer = AudioSystem.getMixer(mixer_info);
//		DataLine.Info line_info = (DataLine.Info)mixer.getTargetLineInfo()[0];
//		
//		try {			
//			TargetDataLine line = (TargetDataLine) mixer.getLine(line_info);
//			line.open();
//			line.start();
//			
//			AudioFormat format = line.getFormat();
//			
//			new Thread(() -> {
//				while(true) {
//					byte[] buffer = new byte[format.getFrameSize()];
//					int b = line.read(buffer, 0, buffer.length);
//					if(b > 0) {
//						for(int j = 0; j < buffer.length; j ++)
//							System.out.print(buffer[j] + ",");
//						System.out.println();
//					}
//				}
//			}).start();
//			
//		} catch (LineUnavailableException lue) {
//			lue.printStackTrace();
//		}	
		
	}
}
