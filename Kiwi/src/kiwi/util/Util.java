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
 
    public static Complex[] fft(short[] channelInfo) {
 
    	Complex[] buffer = new Complex[channelInfo.length];
    	
    	for(int i = 0; i < buffer.length; i++) {
    		buffer[i] = new Complex((double) channelInfo[i], 0.0);
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
        
        return buffer;
    }
}
