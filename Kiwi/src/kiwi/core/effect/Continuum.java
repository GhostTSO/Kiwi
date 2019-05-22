package kiwi.core.effect;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.CompositeContext;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

import kiwi.core.Effect;
import kiwi.core.Source;
import kiwi.core.render.RenderContext;
import kiwi.core.update.UpdateContext;
import kiwi.core.update.Updateable;
import kiwi.util.Util;

public class Continuum extends Effect {
	private final hz_band[]
		hz_band;
	private float
		ramp;
	
	public Continuum() {
		super("Continuum");		
		hz_band = new hz_band[] {
			new hz_band(255,   0,   0,    0,    60), //
			new hz_band(  0,   0, 255,   60,   250), // lo
			new hz_band(  0, 255,   0,  250,   500), // lo-mid
			new hz_band(255,   0,   0,  500,  2000), //    mid
			new hz_band(  0, 255,   0, 2000,  4000), // hi-mid
			new hz_band(  0,   0, 255, 4000,  6000), // hi
			new hz_band(  0, 255, 128, 6000, 20000)  //
		};
	}

	@Override
	public void render(RenderContext context) {
		
		Graphics2D g2D = (Graphics2D)context.g2D.create();
		
		g2D.setColor(Color.BLACK);
		g2D.fillRect(
				0,
				0,
				context.canvas_w,
				context.canvas_h
				);
		

		g2D.setComposite(hz_composite);
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		int rad = Util.min(
					context.canvas_w / 2,
					context.canvas_h / 2
					) * 3 / 5;
		this.ramp = rad / 16f; 
		g2D.setStroke(new BasicStroke(rad / 4f));
		
		for(int i = 0; i < this.hz_band.length; i ++) {
			g2D.setColor(hz_band[i].rgba);
			float
				amp = Util.map_tanh(
						hz_band[i].amp,
						0, Short.MAX_VALUE,
						0, 3 * rad / 5,
						64f
						),
				ang = 360f / hz_band.length * i - (360f / hz_band.length / 2) + (360f / hz_band.length * hz_band[i].frq / (hz_band[i].max_hz - hz_band[i].min_hz)),
				dx = amp * Util.cos(Util.toRadians(ang)),
				dy = amp * Util.sin(Util.toRadians(ang));
						
			g2D.drawOval(
					(int)(context.canvas_w / 2 + dx - rad),
					(int)(context.canvas_h / 2 + dy - rad),
					(int)(2 * rad),
					(int)(2 * rad)		
					);
		}
		
		g2D.dispose();
	}

	@Override
	public void update(UpdateContext context) {
		for(int i = 0; i < hz_band.length; i ++)
			hz_band[i].update(context);
	}
	
	private class hz_band implements Updateable {
		protected Color
			rgba;
		protected float
			min_hz,
			max_hz;
		
		public hz_band(
				int r,
				int g,
				int b,
				float min_hz,
				float max_hz
				) {
			this.rgba = new Color(
					r,
					g,
					b
					);
			this.min_hz = min_hz;
			this.max_hz = max_hz;
			this.frq = (max_hz + min_hz) / 2;
		}

		protected float
			amp,
			frq;

		@Override
		public void update(UpdateContext context) {
			int
				a = Source.hzToIndex(this.min_hz),
				b = Source.hzToIndex(this.max_hz);
			for(int i = a; i < b; i ++) {
				if(context.mono[i].re > amp) {
					amp = context.mono[i].re;
					if(Source.indexToHz(i) < frq)
						frq -= (max_hz - min_hz) / 16f;
					if(Source.indexToHz(i) > frq)
						frq += (max_hz - min_hz) / 16f;
				} else if(amp > ramp)
					amp -= ramp;
				else
					amp = 0f;
			}			
		}
	}
	
	private static final CompositeContext
		hz_composite_context = new CompositeContext() {
			@Override
			public void dispose() {
				//do nothing
			}

			@Override
			public void compose(Raster src, Raster dst, WritableRaster out) {
				int
					w = Util.min(
							src.getWidth(), 
							dst.getWidth()
							),
					h = Util.min(
							src.getHeight(),
							dst.getHeight()
							);
				
				int[]
						srcPixel = new int[4],
						dstPixel = new int[4],
						srcPixels = new int[w],
						dstPixels = new int[w];
				for(int y = 0; y < h; y ++) {
					src.getDataElements(0, y, w, 1, srcPixels);
					dst.getDataElements(0, y, w, 1, dstPixels);
					for(int x = 0; x < w; x ++) {
						srcPixel[0] = (srcPixels[x] >> 16) & 0xFF;
	                    srcPixel[1] = (srcPixels[x] >>  8) & 0xFF;
	                    srcPixel[2] = (srcPixels[x]      ) & 0xFF;
	                    srcPixel[3] = (srcPixels[x] >> 24) & 0xFF;
	                    
	                    dstPixel[0] = (dstPixels[x] >> 16) & 0xFF;
	                    dstPixel[1] = (dstPixels[x] >>  8) & 0xFF;
	                    dstPixel[2] = (dstPixels[x]      ) & 0xFF;
	                    dstPixel[3] = (dstPixels[x] >> 24) & 0xFF;
	                    
	                    dstPixels[x] = 
	                    		((Math.max(srcPixel[0], dstPixel[0]) & 0xFF) << 16) |
	                    		((Math.max(srcPixel[1], dstPixel[1]) & 0xFF) <<  8) |
	                    		((Math.max(srcPixel[2], dstPixel[2]) & 0xFF)      ) |
	                    		((Math.max(srcPixel[3], dstPixel[3]) & 0xFF) << 24);
					}
					out.setDataElements(0, y, w, 1, dstPixels);
				}				
			}		
		};
	
	private static final Composite
		hz_composite = (a, b, c) -> {
			return hz_composite_context;
		};
		
}
