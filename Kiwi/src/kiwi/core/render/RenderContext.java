package kiwi.core.render;

import java.awt.Graphics2D;

import kiwi.math.Complex;

public class RenderContext {
	public final Complex[]
		l_channel,
		r_channel;
	public final int
		samples;
	public int
		canvas_w,
		canvas_h;
	public Graphics2D
		g2D;
	
	public RenderContext(
			Complex[] l_channel,
			Complex[] r_channel,
			int samples
			) {
		this.l_channel = l_channel;
		this.r_channel = r_channel;
		this.samples = samples;
	}
}
