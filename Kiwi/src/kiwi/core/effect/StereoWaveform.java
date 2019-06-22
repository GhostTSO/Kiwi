package kiwi.core.effect;

import java.awt.BasicStroke;
import java.awt.Color;

import kiwi.core.source.Source;

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
			double root;
			double logNum = Math.log(50);
			double scale = context.canvas_h/8;
				
			
			for(int i = 0; i < Source.SAMPLES/2; i ++) {
				
				if(context.stereo_l[i+Source.SAMPLES/2].re > 1) {
				root = Math.log(context.stereo_l[i+Source.SAMPLES/2].re)/logNum;
				}else {
					root = 0;
				}
				
				context.g2D.setColor(Color.WHITE);	
				context.g2D.drawLine(
						(int) (canvasSpacing*i),
						(int)(context.canvas_h/4+scale*(root)),
						(int) (canvasSpacing*i),
						(int)(context.canvas_h/4-scale*(root))
						);	
				
				if(context.stereo_r[i+Source.SAMPLES/2].re > 1) {
					root = Math.log(context.stereo_r[i+Source.SAMPLES/2].re)/logNum;
					}else {
						root = 0;
					}
					
				
				context.g2D.drawLine(
						(int) (canvasSpacing*i),
						(int)(3*context.canvas_h/4+scale*(root)),
						(int) (canvasSpacing*i),
						(int)(3*context.canvas_h/4-scale*(root))
						);	
				
			}
		}

		
		@Override
		public void update(UpdateContext context) {
			
		}
	}