package kiwi.core.effect;

import java.awt.BasicStroke;
import java.awt.Color;

import kiwi.core.Effect;
import kiwi.core.Source;
import kiwi.core.render.RenderContext;
import kiwi.core.update.UpdateContext;

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
		
		double l_root;
		double r_root;
		double l_normal = Math.pow(context.stereo_l[Source.SAMPLES/2].re, 1/2.75);
		double r_normal = Math.pow(context.stereo_r[Source.SAMPLES/2].re, 1/2.75);
		
		
		double heightMultiplier = (double)context.canvas_h/20;
		double canvasSpacing = (double)context.canvas_w/(Source.SAMPLES/2);
		
		for(int i = 0; i < Source.SAMPLES/4; i ++) {
			
			l_root = Math.pow(context.stereo_l[i+3*Source.SAMPLES/4].re, 1/2.75);
			r_root = Math.pow(context.stereo_r[i].re, 1/2.75);
			context.g2D.setColor(Color.MAGENTA);	
			context.g2D.drawLine(
					(int)(canvasSpacing*i),
					(int)(context.canvas_h/2+heightMultiplier*(l_root-l_normal)),
					(int)(canvasSpacing*i),
					(int)(context.canvas_h/2-heightMultiplier*(l_root-l_normal))
					);		
			
			context.g2D.setColor(Color.CYAN);
			context.g2D.drawLine(
					(int)(canvasSpacing*i)+context.canvas_w/2,
					(int)(context.canvas_h/2+heightMultiplier*(r_root-r_normal)),
					(int)(canvasSpacing*i)+context.canvas_w/2,
					(int)(context.canvas_h/2-heightMultiplier*(r_root-r_normal))
					);	
			
		}
	}

	
	@Override
	public void update(UpdateContext context) {
		
	}

}
