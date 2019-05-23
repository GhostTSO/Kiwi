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
	private static final float
		AMPLITUDE_RAMP = 64f,
		AMPLITUDE_VELOCITY_UP =  1f,
		AMPLITUDE_VELOCITY_DN = .3f,
		FREQUENCY_VELOCITY = .2f,
		BRIGHTNESS_FLOOR = .4f,
		BRIGHTNESS_VELOCITY_UP =  1f,
		BRIGHTNESS_VELOCITY_DN = .15f;
	
	private final hz_band[]
		hz_band = new hz_band[] {
			new hz_band(new Color( 255,   0,   0),    0,    60), //
			new hz_band(new Color(   0,   0, 255),   60,   250), // lo
			new hz_band(new Color(   0, 255,   0),  250,   500), // lo-mid
			new hz_band(new Color( 255,   0,   0),  500,  2000), //    mid
			new hz_band(new Color(   0, 255,   0), 2000,  4000), // hi-mid
			new hz_band(new Color(   0,   0, 255), 4000,  6000), // hi
			new hz_band(new Color(   0, 255,   0), 6000, 20000)  //
		};
	private float
		max_brightness,
		cur_brightness;
	
	public Continuum() {
		super("Continuum");
	}

	@Override
	public void render(RenderContext context) {
		
		Graphics2D g2D = (Graphics2D)context.g2D.create();
		
		g2D.setRenderingHint(
				RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON
				);		
		g2D.setColor(Color.BLACK);
		g2D.fillRect(
				0,
				0,
				context.canvas_w,
				context.canvas_h
				);
		g2D.setComposite(RGBA_COMPOSITE);
		
		
		int
			radius1 = Util.min(
					context.canvas_w / 2,
					context.canvas_h / 2
					),
			radius2 = radius1 * 3 / 5,
			radius3 = radius2 * 1 / 4,
			i = 0;
		
		g2D.setStroke(new BasicStroke(radius3));
		
		
		for(hz_band hz_band: hz_band) {
			g2D.setColor(
					new Color(
						(int)(hz_band.rgba.getRed()   * this.cur_brightness * (1f - BRIGHTNESS_FLOOR) + hz_band.rgba.getRed()   * BRIGHTNESS_FLOOR),
						(int)(hz_band.rgba.getGreen() * this.cur_brightness * (1f - BRIGHTNESS_FLOOR) + hz_band.rgba.getGreen() * BRIGHTNESS_FLOOR),
						(int)(hz_band.rgba.getBlue()  * this.cur_brightness * (1f - BRIGHTNESS_FLOOR) + hz_band.rgba.getBlue()  * BRIGHTNESS_FLOOR)
					));
			
			
			float
				amp = hz_band.cur_amp * radius3 * 1.3f,
				rot = 360f / this.hz_band.length,
				ang = (rot * i) - (rot / 2) + (rot * hz_band.cur_frq),
				dx = amp * Util.cos(Util.toRadians(ang)),
				dy = amp * Util.sin(Util.toRadians(ang));
			g2D.drawOval(
					(int)(context.canvas_w / 2 + dx - radius2),
					(int)(context.canvas_h / 2 + dy - radius2),
					(int)(2 * radius2),
					(int)(2 * radius2)		
					);
			i ++;				
		}
		
		g2D.dispose();
	}

	@Override
	public void update(UpdateContext context) {
		this.max_brightness = 0f;
		for(hz_band hz_band: hz_band) {
			hz_band.update(context);
			if(hz_band.cur_amp> this.max_brightness)
				this.max_brightness = hz_band.cur_amp;
		}
		
		this.max_brightness = (float)Math.sqrt(this.max_brightness);
		
		if(this.cur_brightness < this.max_brightness)
			this.cur_brightness += (this.max_brightness - this.cur_brightness) * BRIGHTNESS_VELOCITY_UP;
		if(this.cur_brightness > this.max_brightness)
			this.cur_brightness += (this.max_brightness - this.cur_brightness) * BRIGHTNESS_VELOCITY_DN;

		if(this.cur_brightness < 0f)
			this.cur_brightness = 0f;
		if(this.cur_brightness > 1f)
			this.cur_brightness = 1f;
	}
	
	private static class hz_band implements Updateable {
		protected final Color
			rgba;
		protected final float
			min_hz,
			max_hz;
		
		protected float
			max_amp,
			max_frq,
			cur_amp,
			cur_frq;
		
		public hz_band(Color rgba, float min_hz, float max_hz) {
			this.rgba = rgba;
			this.min_hz = min_hz;
			this.max_hz = max_hz;
			this.max_frq = .5f;
			this.cur_frq = .5f; 
		}		

		@Override
		public void update(UpdateContext context) {
			int 
				a = Source.hzToIndex(this.min_hz),
				b = Source.hzToIndex(this.max_hz);			
			this.max_amp = 0;
			for(int i = a; i < b; i ++)
				if(context.mono[i].re > this.max_amp) {
					this.max_amp = context.mono[i].re;
					this.max_frq = Source.indexToHz(i);
					
					this.max_amp = Util.map_loge(this.max_amp, 0, Short.MAX_VALUE, AMPLITUDE_RAMP);
					this.max_frq = Util.map(this.max_frq, this.min_hz, this.max_hz);
				}
			
			if(this.cur_amp < this.max_amp)
				this.cur_amp += (this.max_amp - this.cur_amp) * AMPLITUDE_VELOCITY_UP;
			if(this.cur_amp > this.max_amp)
				this.cur_amp += (this.max_amp - this.cur_amp) * AMPLITUDE_VELOCITY_DN;
			
			if(this.cur_frq != this.max_frq)
				this.cur_frq += (this.max_frq - this.cur_frq) * FREQUENCY_VELOCITY;
		}		
	}
	
	private static final CompositeContext
		RGBA_COMPOSITE_CONTEXT = new CompositeContext() {
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
		RGBA_COMPOSITE = (model1, model2, hints) -> {
			return RGBA_COMPOSITE_CONTEXT;
		};
		
}
