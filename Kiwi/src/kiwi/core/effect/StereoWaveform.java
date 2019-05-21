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
				
			for(int i = 0; i < Source.SAMPLES; i ++) {
				context.g2D.setColor(Color.WHITE);	
				context.g2D.drawLine(
						i,
						(int)(context.canvas_h/4+(context.l_channel[i].re)),
						i,
						(int)(context.canvas_h/4-(context.l_channel[i].re))
						);		
				
				context.g2D.setColor(Color.WHITE);
				context.g2D.drawLine(
						i,
						(int)(3*context.canvas_h/4+(context.r_channel[i].re)),
						i,
						(int)(3*context.canvas_h/4-(context.r_channel[i].re))
						);	
				
			}
		}

		
		@Override
		public void update(UpdateContext context) {
			
		}
	}