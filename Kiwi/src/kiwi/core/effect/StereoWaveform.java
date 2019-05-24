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
			
			double canvasSpacing = (double)context.canvas_w/(Source.SAMPLES/2);
			
			double l_root;
			double r_root;
			double logNum = Math.log(50);
			double scale = context.canvas_h/8;
				
			for(int i = 0; i < Source.SAMPLES/2; i ++) {
				
				if(context.stereo_l[i+Source.SAMPLES/2].re > 1) {
				l_root = Math.log(context.stereo_l[i+Source.SAMPLES/2].re)/logNum;
				}else {
					l_root = 0;
				}
				if(context.stereo_r[i+Source.SAMPLES/2].re > 1) {
					r_root = Math.log(context.stereo_r[i+Source.SAMPLES/2].re)/logNum;
					}else {
						r_root = 0;
					}
				context.g2D.setColor(Color.WHITE);	
				context.g2D.drawLine(
						(int) (canvasSpacing*i),
						(int)(context.canvas_h/4+scale*(l_root)),
						(int) (canvasSpacing*i),
						(int)(context.canvas_h/4-scale*(l_root))
						);		
				
				context.g2D.setColor(Color.WHITE);
				context.g2D.drawLine(
						(int) (canvasSpacing*i),
						(int)(3*context.canvas_h/4+scale*(r_root)),
						(int) (canvasSpacing*i),
						(int)(3*context.canvas_h/4-scale*(r_root))
						);	
				
			}
		}

		
		@Override
		public void update(UpdateContext context) {
			
		}
	}