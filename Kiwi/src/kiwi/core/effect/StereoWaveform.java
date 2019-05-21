package kiwi.core.effect;

import java.awt.BasicStroke;
import java.awt.Color;

import kiwi.core.Effect;
import kiwi.core.Source;
import kiwi.core.render.RenderContext;
import kiwi.core.update.UpdateContext;

public class StereoWaveform extends Effect {
		
		public StereoWaveform() {
			super("Stereo Waveform");
		}
		
		@Override
		public void render(RenderContext context) {
			context.g2D.setColor(Color.BLACK);
			context.g2D.setStroke(new BasicStroke(2));
			context.g2D.fillRect(
					0,
					0,
					context.canvas_w,
					context.canvas_h
					);			
			
			double l_root;
			double r_root;
			double l_normal = Math.pow(context.l_channel[Source.SAMPLES/2].re, 1/2.75);
			double r_normal = Math.pow(context.r_channel[Source.SAMPLES/2].re, 1/2.75);
				
			for(int i = 0; i < Source.SAMPLES/2; i ++) {
				
				l_root = Math.pow(context.l_channel[i+Source.SAMPLES/2].re, 1/2.75);
				r_root = Math.pow(context.r_channel[i+Source.SAMPLES/2].re, 1/2.75);
				context.g2D.setColor(Color.WHITE);	
				context.g2D.drawLine(
						i*2,
						(int)(context.canvas_h/4+25*(l_root-l_normal)),
						i*2,
						(int)(context.canvas_h/4-25*(l_root-l_normal))
						);		
				
				context.g2D.setColor(Color.WHITE);
				context.g2D.drawLine(
						i*2,
						(int)(3*context.canvas_h/4+25*(r_root)),
						i*2,
						(int)(3*context.canvas_h/4-25*(r_root))
						);	
				
			}
		}

		
		@Override
		public void update(UpdateContext context) {
			
		}
	}