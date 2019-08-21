package kiwi.core;

import kiwi.core.Renderable.RenderContext;
import kiwi.math.Complex;

public interface Updateable {
	public void update(UpdateContext context);
	
	public static class UpdateContext {
		public final Complex[]
			stereo_l,
			stereo_r,
			mono;
		public int
			canvas_w,
			canvas_h;
		public float
			dt;
		
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

	void render(RenderContext context, boolean scaling);
}
