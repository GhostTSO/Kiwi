package kiwi.core.update;

import kiwi.math.Complex;

public class UpdateContext {
	public final Complex[]
		l_channel,
		r_channel;
	public int
		canvas_w,
		canvas_h;
	
	public UpdateContext(
			Complex[] l_channel,
			Complex[] r_channel
			) {
		this.l_channel = l_channel;
		this.r_channel = r_channel;
	}
}
