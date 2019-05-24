package kiwi.core.effect;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.RenderingHints;

import kiwi.core.Effect;
import kiwi.core.Source;
import kiwi.core.render.RenderContext;
import kiwi.core.update.UpdateContext;
public class Circularity extends Effect{
	
	double[] peaksLeft = new double[Source.SAMPLES/4];
	double[] peaksRight = new double[Source.SAMPLES/4];
	public Circularity() {
		super("Circularity");
	}
	
	@Override
	public void render(RenderContext context) {
		
		context.g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		context.g2D.setColor(Color.BLACK);
		context.g2D.setStroke(new BasicStroke((context.canvas_w/Source.SAMPLES)+8));
		context.g2D.fillRect(
				0,
				0,
				context.canvas_w,
				context.canvas_h
				);			
		
		double root;
		double logNum = Math.log(50);
		double scale = context.canvas_h/4;
		Color myColor;
		
		int circleWidth = context.canvas_h/50;
		
		float degree;
		double cosValue;
		double sinValue;
		
		for(int i = 0; i < Source.SAMPLES/4; i ++) {
			myColor= new Color(i,0, 255);
			context.g2D.setColor(myColor);	
			
			if(context.stereo_l[i+3*Source.SAMPLES/4].re > 1) {
				root = scale*(Math.log(context.stereo_l[i+3*Source.SAMPLES/4].re))/logNum;
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
			
			if(context.stereo_l[i+1].re > 1) {
				root = scale*(Math.log(context.stereo_l[i+1].re))/logNum;
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
			
			degree = (float)(i*Math.PI/256.0);
			
				
			
			cosValue = context.canvas_w/8*Math.cos(degree)+ peaksLeft[i]*Math.cos(degree);
			sinValue = context.canvas_w/8*Math.sin(degree)+ peaksLeft[i]*Math.sin(degree);
			
			context.g2D.fillOval(
					(int)(context.canvas_w/2+cosValue),
					(int)(context.canvas_h/2+sinValue),
					circleWidth,
					circleWidth
					);	
			
			myColor= new Color(255-i,0, 255);
			context.g2D.setColor(myColor);
			
			cosValue = -context.canvas_w/8*Math.cos(degree)- peaksRight[i]*Math.cos(degree);
			sinValue = -context.canvas_w/8*Math.sin(degree)- peaksRight[i]*Math.sin(degree);
			
			context.g2D.fillOval(
					(int)(context.canvas_w/2+cosValue),
					(int)(context.canvas_h/2+sinValue),
					circleWidth,
					circleWidth
					);	
			
		}
	}

	
	@Override
	public void update(UpdateContext context) {
		
	}

}
