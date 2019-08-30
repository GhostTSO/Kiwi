package _kiwi.util;

import java.awt.Toolkit;

public class Util {
	public static final int
		FULLSCREEN_W = Toolkit.getDefaultToolkit().getScreenSize().width ,
		FULLSCREEN_H = Toolkit.getDefaultToolkit().getScreenSize().height;
	
	public static enum Hint {
		LIN, LOG, TANH
	}
	
	public static double map(Hint hint, double val, double min, double max, double ramp) {
		switch(hint) {
			case LIN:  return map     (val, min, max   );
			case LOG:  return map_loge(val, min, max, ramp);
			case TANH: return map_tanh(val, min, max, ramp);
		}
		return val;
	}
	
	public static final double map(double val, double min, double max) {
		return (val - min) / (max - min);
	}
	
	public static final double map(double val, double min1, double max1, double min2, double max2) {
		return Util.map(val, min1, max1) * (max2 - min2) + min2;
	}
	
	public static final double map_tanh(double val, double min, double max, double ramp) {
		return Math.tanh((val - min) /(max - min) * ramp);
	}
	
	public static final double map_tanh(double val, double min1, double max1, double min2, double max2, double ramp) {
		return Util.map_tanh(val, min1, max1, ramp) * (max2 - min2) + min2;
	}
	
	public static final double map_loge(double val, double min, double max, double ramp) {
		return 1f / (1f + Math.exp(- (val - min) / (max - min) * ramp)) * 2f - 1f;
	}
	
	public static final double map_loge(double val, double min1, double max1, double min2, double max2, double ramp) {
		return Util.map_loge(val, min1, max1, ramp) * (max2 - min2) + min2;
	}
	
	public static final double log(double x, double e) {
		return (double)(Math.log(x) / Math.log(e));
	}
	
	public static final double min(double[] x) {
		double min = x[0];
		for(int i = 1; i < x.length; i ++)
			if(x[i] < min)
				min = x[i];
		return min;
	}
	
	public static final double max(double[] x) {
		double max = x[0];
		for(int i = 1; i < x.length; i ++)
			if(x[i] > max)
				max = x[i];
		return max;
	}
	
	public static final double clamp(double val) {
		return clamp(val, 0, 1);
	}
	
	public static final double clamp(double val, double min, double max) {
		if(val < min)
			val = min;
		if(val > max)
			val = max;
		return val;
	}
	
	public static final double[] lerp(double[] a, double[] b, double t) {
		int 
			n = Math.min(
					a.length,
					b.length
					);
		double[] 
				lerp = new double[n];
		for(int i = 0; i < n; i ++)
			lerp[i] = (b[i] - a[i]) * t + a[i];
		return lerp;
	}
	
	/**
	 * Hi Pass Filter<br>
	 * Copies values from one array to a new array, zeroing any values lower than the threshold
	 * @param x the signal array
	 * @param t the signal threshold
	 * @return the filtered signal
	 */
	public static final double[] hpf(double[] x, double t) {
		double[] y = new double[x.length];
		for(int i = 0; i < x.length; i ++)
			if(x[i] > t) y[i] = x[i];
		return y;
	}
	
	/**
	 * Lo Pass Filter<br>
	 * Copies values from one array to a new array, zeroing any values higher than the threshold
	 * @param x the signal array
	 * @param t the signal threshold
	 * @return the filtered signal
	 */
	public static final double[] lpf(double[] x, double t) {
		double[] y = new double[x.length];
		for(int i = 0; i < x.length; i ++)
			if(x[i] < t) y[i] = x[i];
		return y;
	}
	
	/**
	 * Fast Fourier Transform
	 * @param x the signal array
	 */
	public static final void fft(double[] x) {
		Complex[] y = new Complex[x.length];
		for(int i = 0; i < x.length; i ++)
			y[i] = new Complex(x[i]);
		fft(y);
		for(int i = 0; i < y.length; i ++) {
			y[i].re /= y.length;
			y[i].im /= x.length;
			x[i] = y[i].mag();
		}
	}
	
	/**
	 * Fast Fourier Transform
	 * @param x the signal array
	 */
	public static final void fft(Complex[] x) {
		int n = x.length;

        // bit reversal permutation
        int shift = 1 + Integer.numberOfLeadingZeros(n);
        for (int k = 0; k < n; k++) {
            int j = Integer.reverse(k) >>> shift;
            if (j > k) {
                Complex temp = x[j];
                x[j] = x[k];
                x[k] = temp;
            }
        }

        // butterfly updates
        for (int L = 2; L <= n; L = L + L) {
            for (int k = 0; k < L/2; k ++) {
                double kth = -2 * k * Math.PI / L;
                Complex w = new Complex(
                		Math.cos(kth), 
                		Math.sin(kth)
                		);
                for (int j = 0; j < n/L; j++) {
                    Complex tao = w.mul(x[j*L + k + L/2]);
                    x[j*L + k + L/2] = x[j*L + k].sub(tao); 
                    x[j*L + k]       = x[j*L + k].add(tao);
                }
            }
        }        
	}	
}
