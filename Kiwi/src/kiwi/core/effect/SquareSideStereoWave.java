package kiwi.core.effect;

import java.awt.BasicStroke;
import java.awt.Color;

import kiwi.core.source.Source;

public class SquareSideStereoWave extends Effect{
	
	double[] peaksLeft = new double[Source.SAMPLES/4];
	double[] peaksRight = new double[Source.SAMPLES/4];
	public SquareSideStereoWave() {
		super("Waves");
	}
	
	@Override
	public void render(RenderContext context) {
		context.g2D.setColor(Color.BLACK);
		context.g2D.setStroke(new BasicStroke((context.canvas_w/Source.SAMPLES)+5));
		context.g2D.fillRect(
				0,
				0,
				context.canvas_w,
				context.canvas_h
				);			
		
		double root;
		double logNum = Math.log(50);
		double scale = context.canvas_h/3;
		double canvasSpacing = (double)context.canvas_w/(Source.SAMPLES/2);
		Color myColor;
		
		
		for(int i = 0; i < Source.SAMPLES/4; i ++) {
			myColor= new Color(i,255, 0);
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
			
			context.g2D.drawRect(
					(int)(canvasSpacing*i),
					(int)(context.canvas_h/2+peaksLeft[i]),
					4,
					4
					);		
			context.g2D.drawRect(
					(int)(canvasSpacing*i),
					(int)(context.canvas_h/2-peaksLeft[i]),
					4,
					4
					);
			
			if(context.stereo_r[i+1].re > 1) {
				root = scale*(Math.log(context.stereo_r[i+1].re))/logNum;
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
			
			
			
			myColor= new Color(255-i,0, 255);
			context.g2D.setColor(myColor);	
			context.g2D.drawRect(
					(int)(canvasSpacing*i)+context.canvas_w/2,
					(int)(context.canvas_h/2+peaksRight[i]),
					4,
					4
					);	
			context.g2D.drawRect(
					(int)(canvasSpacing*i)+context.canvas_w/2,
					(int)(context.canvas_h/2-peaksRight[i]),
					4,
					4
					);	
		}
	}
	

	
	@Override
	public void update(UpdateContext context) {
		
	}

}
