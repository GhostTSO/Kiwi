package kiwi.core.effect;

import java.awt.BasicStroke;
import java.awt.Color;

import kiwi.core.source.Source;

public class SideStereoWaveForm extends Effect{
	
	
	public SideStereoWaveForm() {
		super("Side Stereo Waveform");
	}
	
	@Override
	public void render(RenderContext context) {
		context.g2D.setColor(Color.BLACK);
		context.g2D.setStroke(new BasicStroke((context.canvas_w/Source.SAMPLES)+1));
		context.g2D.fillRect(
				0,
				0,
				context.canvas_w,
				context.canvas_h
				);			
		
		double canvasSpacing = (double)context.canvas_w/(Source.SAMPLES/2);
		double root;
		double logNum = Math.log(50);
		double scale = context.canvas_h/3;
		
		for(int i = 0; i < Source.SAMPLES/4; i ++) {
			if(context.stereo_l[i+3*Source.SAMPLES/4].re > 1) {
			root = scale*(Math.log(context.stereo_l[i+3*Source.SAMPLES/4].re)/logNum);
			}else {
				root = 0;
			}
			context.g2D.setColor(Color.MAGENTA);	
			context.g2D.drawLine(
					(int)(canvasSpacing*i),
					(int)(context.canvas_h/2+root),
					(int)(canvasSpacing*i),
					(int)(context.canvas_h/2-root)
					);		
			if(context.stereo_r[i+1].re > 1) {
			root = scale*(Math.log(context.stereo_r[i+1].re)/logNum);
			}else {
				root = 0;
			}
			context.g2D.setColor(Color.CYAN);
			context.g2D.drawLine(
					(int)(canvasSpacing*i)+context.canvas_w/2,
					(int)(context.canvas_h/2+root),
					(int)(canvasSpacing*i)+context.canvas_w/2,
					(int)(context.canvas_h/2-root)
					);	
			
		}
	}

	
	@Override
	public void update(UpdateContext context) {
		
	}

}
