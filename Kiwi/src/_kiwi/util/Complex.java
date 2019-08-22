package _kiwi.util;

public class Complex {
	public double
		re,
		im;
	
	public Complex() {
		//do nothing
	}	
	public Complex(Complex c) {
		this.re = c.re;
		this.im = c.im;
	}	
	public Complex(double re) {
		this.re = re;
		this.im = 0.;
	}
	public Complex(double re, double im) {
		this.re = re;
		this.im = im;
	}	
	
	//SET
	public Complex set(Complex c) {
		return set(c.re, c.im);
	}	
	public Complex set(double re) {
		return set(re, 0.);
	}
	public Complex set(double re, double im) {
		this.re = re;
		this.im = im;
		return this;
	}
	
	public double mag() {
		return Math.sqrt(re * re + im * im);
	}
	public double ang() {
		return Math.atan2(im, re);
	}
	
	//ADD
	public Complex add(Complex c) {
		return add(c.re, c.im);
	}	
	public Complex add(double re, double im) {
		return new Complex(this).madd(re, im);
	}	
	public Complex madd(Complex c) {
		return madd(c.re, c.im);
	}	
	public Complex madd(double re, double im) {
		this.re += re;
		this.im += im;
		return this;
	}
	
	//SUB
	public Complex sub(Complex c) {
		return sub(c.re, c.im);
	}
	public Complex sub(double re, double im) {
		return new Complex(this).msub(re, im);
	}
	public Complex msub(Complex c) {
		return msub(c.re, c.im);
	}	
	public Complex msub(double re, double im) {
		this.re -= re;
		this.im -= im;
		return this;
	}
	
	//MUL
	public Complex mul(Complex c) {
		return mul(c.re, c.im);
	}
	public Complex mul(double re, double im) {
		return new Complex(this).mmul(re, im);
	}
	public Complex mmul(Complex c) {
		return mmul(c.re, c.im);
	}	
	public Complex mmul(double re, double im) {
		double
			_re = this.re * re - this.im * im,
			_im = this.re * im + this.im * re;
		this.re = _re;
		this.im = _im;
		return this;
	}
	
	public String toString() {
		return "" + re + " + " + im + "i";
	}
}
