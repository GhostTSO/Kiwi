package kiwi.math;

public class Complex {
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
    
    public Complex set(Complex x) {
    	this.re = x.re;
    	this.im = x.im;
    	return this;
    }
    
    public Complex set(float re, float im) {
    	this.re = re;
    	this.im = im;
    	return this;
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
}
