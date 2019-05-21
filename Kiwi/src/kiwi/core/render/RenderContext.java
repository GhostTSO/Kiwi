package kiwi.core.render;

import java.awt.Graphics2D;

import kiwi.math.Complex;

public class RenderContext {
	public Complex[]
		l_channel,
		r_channel;
	public int
		canvas_w,
		canvas_h,
		samples;
	public Graphics2D
		g2D;
}
