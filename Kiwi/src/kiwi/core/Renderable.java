package kiwi.core;

import java.awt.Graphics2D;

import kiwi.math.Complex;

public interface Renderable {
	public void render(RenderContext context);
	
	public static class RenderContext {
		public final Complex[]
			stereo_l,
			stereo_r,
			mono;
		public int
			canvas_w,
			canvas_h;
		public Graphics2D
			g2D;
		public float
			dt;
		
		public RenderContext(
				Complex[] stereo_l,
				Complex[] stereo_r,
				Complex[] mono
				) {
			this.stereo_l = stereo_l;
			this.stereo_r = stereo_r;
			this.mono = mono;
		}
	}
}
