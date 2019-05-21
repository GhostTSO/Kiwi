package kiwi;

public class Demo {	
	public static void main(String[] args) {
		
		
//		Mixer.Info mixer_info = AudioSystem.getMixerInfo()[16];
//		Mixer mixer = AudioSystem.getMixer(mixer_info);
//		DataLine.Info line_info = (DataLine.Info)mixer.getTargetLineInfo()[0];
//		
//		try {		
//			AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 48000, 16, 2, 4, 60, true);
//			TargetDataLine line = (TargetDataLine) mixer.getLine(line_info);
//			line.open(format);
//			line.start();
//			
//			System.out.println(format);
//			
//			int 
//				samples = 64;
//			byte[] 
//				buffer = new byte[samples * 4];
//			short[]
//				l_channel = new short[samples],
//				r_channel = new short[samples];
//			
//			while(true) {					
//				line.read(buffer, 0, buffer.length);					
//				for(int i = 0; i < samples; i ++) {
//					int j = i * 4;
//					l_channel[i] = (short)((buffer[j + 0] << 8) | (buffer[j + 1] & 0xff));
//					r_channel[i] = (short)((buffer[j + 2] << 8) | (buffer[j + 3] & 0xff));				
//					System.out.println(l_channel[i] + "," + r_channel[i]);
//				}
//			}
//			
//		} catch (LineUnavailableException lue) {
//			lue.printStackTrace();
//		}	
		
	}
}
