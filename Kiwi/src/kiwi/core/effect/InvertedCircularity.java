package kiwi.core.effect;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.RenderingHints;

import kiwi.core.source.Source;

public class InvertedCircularity extends Effect{
	
	double[] peaksLeft = new double[Source.SAMPLES/4];
	double[] peaksRight = new double[Source.SAMPLES/4];
	int degree = 0;
	int startingColor1 = 200;
	int startingColor2 = 100;
	int startingColor3 = 25;
	int colorIncrementer1 = 1;
	int colorIncrementer2 = 2;
	int colorIncrementer3 = 3;
	public InvertedCircularity() {
		super("Inverted Circularity");
	}
	
	@Override
	public void render(RenderContext context, boolean scaling) {
		
		context.g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		Color myColor = new Color(startingColor1/2, startingColor2/2, startingColor3/2);
		context.g2D.setColor(myColor);
		context.g2D.setStroke(new BasicStroke((context.canvas_w/Source.SAMPLES)+8));
		context.g2D.fillRect(
				0,
				0,
				context.canvas_w,
				context.canvas_h
				);			
		
		double root;
		double logNum = Math.log(50);
		double scale;
		if(scaling == true) {
			scale = context.canvas_h/200;}
			else {
			scale = context.canvas_h/3;}
		int circleWidth = context.canvas_h/35;
		
		float degree;
		double cosValue;
		double sinValue;
		
		
		
		for(int i = 0; i < Source.SAMPLES/4; i ++) {
			myColor= new Color(startingColor1, startingColor2, startingColor3);
			context.g2D.setColor(myColor);	
			
			if(context.stereo_l[i+3*Source.SAMPLES/4].re > 1) {
				if(scaling == true) {
					root = scale*context.stereo_l[i+3*Source.SAMPLES/4].re;
				}else {
					root = scale*(Math.log(context.stereo_l[i+3*Source.SAMPLES/4].re))/logNum;
				}
				if(root > peaksLeft[i]) {
					peaksLeft[i] = root;
				}else if(peaksLeft[i] > 4){
					peaksLeft[i] -= 4;
				}else {
					peaksLeft[i] = 0;
				}
			}else if(peaksLeft[i] > 4){
				peaksLeft[i] -= 4;
			}else {
				peaksLeft[i] = 0;
			}
			
			degree = (float)((this.degree+i*Math.PI)/256.0);
			
				
			
			cosValue = context.canvas_w/4*Math.cos(degree)- peaksLeft[i]*Math.cos(degree);
			sinValue = context.canvas_w/4*Math.sin(degree)- peaksLeft[i]*Math.sin(degree);
			
			context.g2D.fillOval(
					(int)(context.canvas_w/2+cosValue),
					(int)(context.canvas_h/2+sinValue),
					circleWidth,
					circleWidth
					);	
			
			if(context.stereo_r[i+1].re > 1) {
				
				if(scaling == true) {
					root = scale*context.stereo_r[i+1].re;
				}else {
					root = scale*(Math.log(context.stereo_r[i+1].re))/logNum;
				}
				
				if(root > peaksRight[i]) {
					peaksRight[i] = root;
				}else if(peaksRight[i] > 4){
					peaksRight[i] -= 4;
				}else {
					peaksRight[i] = 0;
				}
			}else if(peaksRight[i] > 4){
				peaksRight[i] -= 4;
			}else {
				peaksRight[i] = 0;
			}
			

			
			
			
			
			
			cosValue = -context.canvas_w/4*Math.cos(degree)+ peaksRight[i]*Math.cos(degree);
			sinValue = -context.canvas_w/4*Math.sin(degree)+ peaksRight[i]*Math.sin(degree);
			
			context.g2D.fillOval(
					(int)(context.canvas_w/2+cosValue),
					(int)(context.canvas_h/2+sinValue),
					circleWidth,
					circleWidth
					);	
			
		}
		
		this.startingColor1 += this.colorIncrementer1;
		this.startingColor2 += this.colorIncrementer2;
		this.startingColor3 += this.colorIncrementer3;
		if(this.startingColor1 > 254 || this.startingColor1 < 5) {
			this.colorIncrementer1*=-1;
		}
		if(this.startingColor2 > 253 || this.startingColor2 < 5) {
			this.colorIncrementer2*=-1;
		}
		if(this.startingColor3 > 252 || this.startingColor3 < 5) {
			this.colorIncrementer3*=-1;
		}
		this.degree++;
		if(this.degree > 359) {
			degree = 0;
		}
	}

	
	@Override
	public void render(RenderContext context) {
		
	}
	
	@Override
	public void update(UpdateContext context) {
		
	}

}
