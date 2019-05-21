package kiwi.math;

public class Vector {
	public float
		X,
		Y;
	
	public Vector(float x, float y) {
		this.X = x;
		this.Y = y;
	}	
	
	
	public int x() {
		return (int)X;
	}
	
	public int y() {
		return (int)Y;
	}
	
	private static float[]
		cos = new float[0],
		sin = new float[0];
	private static int
		m = 0;
	
	public static final void fft(Vector[] channel) {
		int n = channel.length;
		if((n & (n - 1)) != 0)
			throw new IllegalArgumentException();
		if(m != n) {
			cos = new float[n / 2];
			sin = new float[n / 2];
			for(int i = 0; i < n ; i ++) {
				cos[i] = (float)Math.cos(2 * Math.PI * i / n);
				sin[i] = (float)Math.sin(2 * Math.PI * i / n);
			}
			m = n;
		}
		int levels = 31 - Integer.numberOfLeadingZeros(n);
		for (int i = 0; i < n; i++) {
			int j = Integer.reverse(i) >>> (32 - levels);
			if (j > i) {
				float temp_x = channel[i].X;
				channel[i].X = channel[j].X;
				channel[j].X = temp_x;
				
				float temp_y = channel[i].Y;
				channel[i].Y = channel[j].Y;
				channel[j].Y = temp_y;
			}
		}
		for(int s = 2; s <= n; s <<= 1) {
			int
				half = s / 2,
				step = n / s;
			for(int i = 0; i < n; i += s) {
				for(int j = i, k= 0; j < i + half; j ++, k += step) {
					int l = j + half;
					float
						x =  channel[l].X * cos[k] + channel[l].Y * sin[k],
						y = -channel[l].X * sin[k] + channel[l].Y * cos[k];
					channel[l].X = channel[j].X - x;
					channel[l].Y = channel[j].Y - y;
					channel[j].X += x;
					channel[j].Y += y;
				}
				if(s == n)
					break;
			}
		}
		for(int i = 0; i < n ; i ++) {
			float
	    		mag = (float)Math.sqrt(
	    				channel[i].X * channel[i].X + 
	    				channel[i].Y * channel[i].Y
	    				),
	    		ang = (float)Math.atan2(
	    				channel[i].Y,
	    				channel[i].X
	    				);
	    	channel[i].X = mag;
	    	channel[i].Y = ang;
		}
	}
}
