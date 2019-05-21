package kiwi.math;

public class Complex {
	private static float
		PI = (float)Math.PI;
    public float
    	re,
    	im;
 
    public Complex() {
        this.re = 0f;
        this.im = 0f;
    }
 
    public Complex(
    		float re,
    		float im
    		) {
        this.re = re;
        this.im = im;
    }
    
    public Complex add(Complex b) {
    	return new Complex(
    			this.re + b.re , 
    			this.im + b.im
    			);
    }
    
    public Complex sub(Complex b) {
    	return new Complex(
    			this.re - b.re,
    			this.im - b.im
    			);
    }
    
    public Complex mul(Complex b) {
    	return new Complex(
    			this.re * b.re - this.im * b.im,
    			this.re * b.im + this.im * b.re
    			);
    }
    
    public static float cos(float x) {
    	return (float)Math.cos(x);
    }
    
    public static float sin(float x) {
    	return (float)Math.sin(x);
    }
	
	public static void fft(Complex[] x) {
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
                float kth = -2 * k * PI / L;
                Complex w = new Complex(
                		cos(kth), 
                		sin(kth)
                		);
                for (int j = 0; j < n/L; j++) {
                    Complex tao = w.mul(x[j*L + k + L/2]);
                    x[j*L + k + L/2] = x[j*L + k].sub(tao); 
                    x[j*L + k]       = x[j*L + k].add(tao); 
                }
            }
        }
        
        for(int i = 0; i < x.length; i ++) {
    		float
	    		mag = (float)Math.sqrt( x[i].re * x[i].re + x[i].im * x[i].im) / n,
	    		ang = (float)Math.atan2(x[i].im , x[i].re);
	    	x[i].re = mag;
	    	x[i].im = ang;
    	}
	}
}
