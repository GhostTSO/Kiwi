package _kiwi.core;

import kiwi.util.Copyable;

public interface Updateable {
	public void update(UpdateContext context);
	
	public static class UpdateContext implements Copyable<UpdateContext> {
		public int
			canvas_w,
			canvas_h;
		public double[]
				stereo_l,
				stereo_r,
				mono;
		public double
			t, dt;
		
		private UpdateContext
			parent;
		
		public UpdateContext push() {
			UpdateContext copy = this.copy();
			copy.parent = this;
			return copy;
		}
		
		public UpdateContext pull() {
			if(parent != null) {
				
				return this.parent;
			}
			return this;
		}

		@Override
		public UpdateContext copy() {
			UpdateContext copy = new UpdateContext();
			copy.canvas_w = this.canvas_w;
			copy.canvas_h = this.canvas_h;
			copy.stereo_l = this.stereo_l;
			copy.stereo_r = this.stereo_r;
			copy.mono = this.mono;
			copy.t  = this.t ;
			copy.dt = this.dt;
			return copy;
		}
	}
}
