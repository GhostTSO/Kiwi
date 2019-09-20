package _kiwi.core;

import java.awt.Graphics2D;

import _kiwi.util.Util.Hint;
import kiwi.util.Copyable;

public interface Renderable {
	public void render(RenderContext context);
	
	public static class RenderContext implements Copyable<RenderContext> {
		public Graphics2D
			g2D;
		public int
			canvas_w,
			canvas_h;
		public double[]
			stereo_l,
			stereo_r,
			mono;
		public double
			t, dt;
		public Hint
			hint = Hint.LIN;
		
		private RenderContext
			parent;
		
		public RenderContext push() {
			RenderContext copy = this.copy();
			copy.parent = this;
			return copy;
		}
		
		public RenderContext pull() {
			if(parent != null) {
				this.g2D.dispose();
				return this.parent;
			}
			return this;
		}

		@Override
		public RenderContext copy() {
			RenderContext copy = new RenderContext();
			copy.g2D = (Graphics2D)this.g2D.create();
			copy.canvas_w = this.canvas_w;
			copy.canvas_h = this.canvas_h;
			copy.stereo_l = this.stereo_l;
			copy.stereo_r = this.stereo_r;
			copy.mono = this.mono;
			copy.hint = this.hint;
			copy.t  = this.t ;
			copy.dt = this.dt;
			return copy;
		}		
	}
}
