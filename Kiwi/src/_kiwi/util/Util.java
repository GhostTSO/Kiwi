package _kiwi.util;

import java.awt.Toolkit;

public class Util {
	public static final int
		FULLSCREEN_W = Toolkit.getDefaultToolkit().getScreenSize().width ,
		FULLSCREEN_H = Toolkit.getDefaultToolkit().getScreenSize().height;	
	
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
	
	public static final double clamp(double x) {
		return clamp(x, 0, 1);
	}
	
	public static final double clamp(double x, double a, double b) {
		if(x < a)
			x = a;
		if(x > b)
			x = b;
		return x;
	}
	
	public static final double map(double x, double a, double b) {
		return (x - a) / (b - a);
	}
	
	public static final double map(double x, double a, double b, double c, double d) {
		return Util.map(x, a, b) * (d - c) + c;
	}
	
	public static final double map_tanh(double x, double a, double b, double r) {
		return Math.tanh((x - a) /(b - a) * r);
	}
	
	public static final double map_tanh(double x, double a, double b, double c, double d, double r) {
		return Util.map_tanh(x, a, b, r) * (d - c) + c;
	}
	
	public static final double map_loge(double x, double a, double b, double e) {
		return 1f / (1f + Math.exp(- (x - a) / (b - a) * e)) * 2f - 1f;
	}
	
	public static final double map_loge(double x, double a, double b, double c, double d, double e) {
		return Util.map_loge(x, a, b, e) * (d - c) + c;
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
	
	public static final double[] hpf(double[] x, double t) {
		double[] y = new double[x.length];
		for(int i = 0; i < x.length; i ++)
			if(x[i] > t) y[i] = x[i];
		return y;
	}
	
	public static final double[] lpf(double[] x, double t) {
		double[] y = new double[x.length];
		for(int i = 0; i < x.length; i ++)
			if(x[i] < t) y[i] = x[i];
		return y;
	}
	
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
