package kiwi.util;

import kiwi.math.Complex;

public class Util {	
	public static int bitReverse(int n, int bits) {
        int reversedN = n;
        int count = bits - 1;
 
        n >>= 1;
        while (n > 0) {
            reversedN = (reversedN << 1) | (n & 1);
            count--;
            n >>= 1;
        }
 
        return ((reversedN << count) & ((1 << bits) - 1));
    }
 
    public static Complex[] fft(short[] channel) {
 
    	Complex[] buffer = new Complex[channel.length];
    	
    	for(int i = 0; i < buffer.length; i++) {
    		buffer[i] = new Complex((double) channel[i], 0.0);
    	}
    	
        int bits = (int) (Math.log(buffer.length) / Math.log(2));
        for (int j = 1; j < buffer.length / 2; j++) {
 
            int swapPos = bitReverse(j, bits);
            Complex temp = buffer[j];
            buffer[j] = buffer[swapPos];
            buffer[swapPos] = temp;
        }
 
        for (int N = 2; N <= buffer.length; N <<= 1) {
            for (int i = 0; i < buffer.length; i += N) {
                for (int k = 0; k < N / 2; k++) {
 
                    int evenIndex = i + k;
                    int oddIndex = i + k + (N / 2);
                    Complex even = buffer[evenIndex];
                    Complex odd = buffer[oddIndex];
 
                    double term = (-2 * Math.PI * k) / (double) N;
                    Complex exp = (new Complex(Math.cos(term), Math.sin(term)).mul(odd));
 
                    buffer[evenIndex] = even.add(exp);
                    buffer[oddIndex] = even.sub(exp);
                }
            }
        }
        
        for(int i = 0; i < buffer.length; i ++) {
        	double
        		R = Math.sqrt(buffer[i].re * buffer[i].re + buffer[i].im * buffer[i].im),
        		Θ = Math.atan2(buffer[i].im, buffer[i].re);
        	buffer[i].re = R;
        	buffer[i].im = Θ;
        }
        
        return buffer;
    }
	
//	private static float[]
//		cos = new float[0],
//		sin = new float[0];
//	private static int
//		m = 0;
//	
//	public static final void fft(Vector[] channel) {
//		int n = channel.length;
//		if((n & (n - 1)) != 0)
//			throw new IllegalArgumentException();
//		if(m != n) {
//			cos = new float[n / 2];
//			sin = new float[n / 2];
//			for(int i = 0; i < n ; i ++) {
//				cos[i] = (float)Math.cos(2 * Math.PI * i / n);
//				sin[i] = (float)Math.sin(2 * Math.PI * i / n);
//			}
//			m = n;
//		}
//		for (int i = 0; i < n; i++) {
//			int j = Integer.reverse(i) >>> (32 - levels);
//			if (j > i) {
//				double temp = real[i];
//				real[i] = real[j];
//				real[j] = temp;
//				temp = imag[i];
//				imag[i] = imag[j];
//				imag[j] = temp;
//			}
//		}
//		for(int s = 2; s <= n; s <<= 1) {
//			int
//				half = s / 2,
//				step = n / s;
//			for(int i = 0; i < n; i += s) {
//				for(int j = i, k= 0; j < i + half; j ++, k += step) {
//					int l = j + half;
//					float
//						x =  channel[l].X * cos[k] + channel[l].Y * sin[k],
//						y = -channel[l].X * sin[k] + channel[l].Y * cos[k];
//					channel[l].X = channel[j].X - x;
//					channel[l].Y = channel[j].Y - y;
//					channel[j].X += x;
//					channel[j].Y += y;
//				}
//				if(s == n)
//					break;
//			}
//		}			
//	}
    
//    public static Complex[] fft(Complex[] x) {
//    	int n = x.length;
//    	
//        if (n == 1) return new Complex[] { x[0] };
//
//        // fft of even terms
//        Complex[] even = new Complex[n/2];
//        for (int k = 0; k < n/2; k++) {
//            even[k] = x[2*k];
//        }
//        Complex[] q = fft(even);
//
//        // fft of odd terms
//        Complex[] odd  = even;  // reuse the array
//        for (int k = 0; k < n/2; k++) {
//            odd[k] = x[2*k + 1];
//        }
//        Complex[] r = fft(odd);
//
//        // combine
//        Complex[] y = new Complex[n];
//        for (int k = 0; k < n/2; k++) {
//            double kth = -2 * k * Math.PI / n;
//            Complex wk = new Complex(Math.cos(kth), Math.sin(kth));
//            y[k]       = q[k].add(wk.mul(r[k]));
//            y[k + n/2] = q[k].sub(wk.mul(r[k]));
//        }
//        return y;
//    }
}
