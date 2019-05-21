package kiwi.core.effect;

import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;

import kiwi.core.Effect;
import kiwi.core.Source;
import kiwi.core.render.RenderContext;
import kiwi.core.update.UpdateContext;

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
		
		double l_root;
		double r_root;
		double l_normal = Math.pow(context.stereo_l[Source.SAMPLES/2].re, 1/2.75);
		double r_normal = Math.pow(context.stereo_r[Source.SAMPLES/2].re, 1/2.75);
		
		double heightMultiplier = (double)context.canvas_h/20;
		double canvasSpacing = (double)context.canvas_w/(Source.SAMPLES/2);
		Color myColor;;
		int colorCounter = 0;
		for(int i = 0; i < Source.SAMPLES/4; i ++) {
			myColor= new Color(colorCounter,255, 0);
			l_root = Math.pow(context.stereo_l[i+3*Source.SAMPLES/4].re, 1/2.75);
			r_root = Math.pow(context.stereo_r[i].re, 1/2.75);
			context.g2D.setColor(myColor);	
			
			if(heightMultiplier*(l_root-l_normal) > peaksLeft[i]) {
				peaksLeft[i] = (heightMultiplier*(l_root-l_normal));
			}
			else {
				peaksLeft[i]-=5;
			}
			
			if(heightMultiplier*(r_root-r_normal) > peaksRight[i]) {
				peaksRight[i] = (heightMultiplier*(r_root-r_normal));
			}
			else {
				peaksRight[i]-=5;
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
			myColor= new Color(255-colorCounter,0, 255);
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
			
			colorCounter++;
		}
	}

	
	@Override
	public void update(UpdateContext context) {
		
	}

}
