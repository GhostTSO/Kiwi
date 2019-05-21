package kiwi.core.update;

import kiwi.math.Complex;

public class UpdateContext {
	public final Complex[]
		stereo_l,
		stereo_r,
		mono;
	public int
		canvas_w,
		canvas_h;
	
	public UpdateContext(
			Complex[] l_channel,
			Complex[] r_channel,
			Complex[] mono_channel
			) {
		this.stereo_l = l_channel;
		this.stereo_r = r_channel;
		this.mono = mono_channel;
	}
}
