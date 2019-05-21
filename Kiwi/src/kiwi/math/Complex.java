package kiwi.math;

public class Complex {
    public double
    	re,
    	im;
 
    public Complex() {
        this(0, 0);
    }
 
    public Complex(double r, double i) {
        re = r;
        im = i;
    }
 
    public Complex add(Complex b) {
        return new Complex(this.re + b.re, this.im + b.im);
    }
 
    public Complex sub(Complex b) {
        return new Complex(this.re - b.re, this.im - b.im);
    }
 
    public Complex mul(Complex b) {
        return new Complex(
        		this.re * b.re - this.im * b.im,
                this.re * b.im + this.im * b.re
                );
    }
 
    @Override
    public String toString() {
        return String.format("(%f,%f)", re, im);
    }
    
	public static Complex[] fft(short[] channel) {
    	Complex[] x = new Complex[channel.length];
    	for(int i = 0; i < channel.length; i ++)
    		x[i] = new Complex(channel[i], 0);
    	fft(x);
    	for(int i = 0; i < x.length; i ++) {
    		double
	    		mag = Math.sqrt( x[i].re * x[i].re + x[i].im * x[i].im) / (channel.length),
	    		ang = Math.atan2(x[i].im , x[i].re);
	    	x[i].re = mag;
	    	x[i].im = ang;
    	}
    	return x;
	}
	
	public static void fft(Complex[] x) {
		int n = x.length;
        if (Integer.highestOneBit(n) != n) {
            throw new RuntimeException("n is not a power of 2");
        }

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
        for (int L = 2; L <= n; L = L+L) {
            for (int k = 0; k < L/2; k++) {
                double kth = -2 * k * Math.PI / L;
                Complex w = new Complex(Math.cos(kth), Math.sin(kth));
                for (int j = 0; j < n/L; j++) {
                    Complex tao = w.mul(x[j*L + k + L/2]);
                    x[j*L + k + L/2] = x[j*L + k].sub(tao); 
                    x[j*L + k]       = x[j*L + k].add(tao); 
                }
            }
        }
	}
    
//    public static Complex[] fft(Complex[] x) {    	
//    	int n = x.length;
//
//        // base case
//        if (n == 1) return new Complex[] { x[0] };
//
//        // radix 2 Cooley-Tukey FFT
//        if (n % 2 != 0) {
//            throw new IllegalArgumentException("n is not a power of 2");
//        }
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
    
    
//    public static int bitReverse(int n, int bits) {
//		int reversedN = n;
//		int count = bits - 1;
//
//		n >>= 1;
//		while (n > 0) {
//			reversedN = (reversedN << 1) | (n & 1);
//			count--;
//			n >>= 1;
//		}
//
//		return ((reversedN << count) & ((1 << bits) - 1));
//	}	
//	
//	public static Complex[] fft(short[] channel) {
//	
//		Complex[] buffer = new Complex[channel.length];
//		
//		for(int i = 0; i < buffer.length; i++) {
//			buffer[i] = new Complex((double) channel[i], 0.0);
//		}
//		
//		int bits = (int) (Math.log(buffer.length) / Math.log(2));
//		for (int j = 1; j < buffer.length / 2; j++) {	
//			int swapPos = bitReverse(j, bits);
//			Complex temp = buffer[j];
//			buffer[j] = buffer[swapPos];
//			buffer[swapPos] = temp;
//		}
//	
//		for (int N = 2; N <= buffer.length; N <<= 1) {
//			for (int i = 0; i < buffer.length; i += N) {
//				for (int k = 0; k < N / 2; k++) {
//	
//	              	int evenIndex = i + k;
//	              	int oddIndex = i + k + (N / 2);
//	              	Complex even = buffer[evenIndex];
//	              	Complex odd = buffer[oddIndex];
//	
//	              	double term = (-2 * Math.PI * k) / (double) N;
//	              	Complex exp = (new Complex(Math.cos(term), Math.sin(term)).mul(odd));
//	
//	              	buffer[evenIndex] = even.add(exp);
//	              	buffer[oddIndex] = even.sub(exp);
//	          	}
//	      	}
//	  	}
//	  
//	  	for(int i = 0; i < buffer.length; i ++) {
//	  		double
//	  			R = Math.sqrt(buffer[i].re * buffer[i].re + buffer[i].im * buffer[i].im),
//	  			Θ = Math.atan2(buffer[i].im, buffer[i].re);
//	  		buffer[i].re = R;
//	  		buffer[i].im = Θ;
//	  	}
//	  
//  		return buffer;
//	}
}
