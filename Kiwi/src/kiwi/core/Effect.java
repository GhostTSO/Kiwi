package kiwi.core;
import java.awt.BasicStroke;
import java.awt.Color;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import kiwi.core.render.RenderContext;
import kiwi.core.render.Renderable;
import kiwi.core.update.UpdateContext;
import kiwi.core.update.Updateable;
import kiwi.util.Util;

public abstract class Effect implements Renderable, Updateable {
	public final String
		name;
	
	public Effect(String name) {
		this.name = name;
	}
	
	public static List<Effect> getAvailableEffects() {
		List<Effect> list = new LinkedList<>();
		list.add(new StereoWaveform());
		return list;
	}
	
	public static class StereoWaveform extends Effect {
		
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
	
	
	
	public static class Rings extends Effect {
		private Map<Integer, Color>
			rings = new TreeMap<>();
		
		
		public Rings() {
			super("Rings");
		}

		@Override
		public void render(RenderContext context) {
			context.g2D.setColor(Color.BLACK);
			context.g2D.fillRect(
					0,
					0,
					context.canvas_w,
					context.canvas_h
					);
			
			int r = Util.max(
					context.canvas_w,
					context.canvas_h
					) * 3 / 4,
				s = r / 16;
			
			context.g2D.setStroke(new BasicStroke(s));
			
			this.rings.forEach((sample, color) -> {
				context.g2D.setColor(color);
				
				context.g2D.drawOval(
						context.canvas_w - r,
						context.canvas_h - r,
						r + r,
						r + r
						);
			});			
		}

		@Override
		public void update(UpdateContext context) {
			
		}		
	}

}
