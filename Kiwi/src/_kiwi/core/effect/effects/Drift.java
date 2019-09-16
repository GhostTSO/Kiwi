package _kiwi.core.effect.effects;

import java.awt.Color;
import java.util.Random;

import _kiwi.core.Renderable;
import _kiwi.core.Updateable;
import _kiwi.core.effect.Effect;
import _kiwi.util.Util;

public class Drift extends Effect {
	public static final double
		VX = .05,
		VY = .05,
		VR = .05;
	public static final Random
		RANDOM = new Random();
	public final Particle[]
		particles = new Particle[16];

	public Drift() {
		super("Drift");
		this.background = new Color(0f, 0f, 0f, .08f);
		for(int i = 0; i < particles.length; i ++)
			particles[i] = new Particle();
	}
	
	@Override
	public void onRender(RenderContext context) {
		for(int i = 0; i < particles.length; i ++)
			particles[i].render(context);
	}
	
	@Override
	public void onUpdate(UpdateContext context) {
		for(int i = 0; i < particles.length; i ++)
			particles[i].update(context);
	}	
	
	private static class Particle implements Renderable, Updateable {
		public double
			x,
			y,
			r,
			vx,
			vy,
			vr;
		
		public Particle() {
			x = RANDOM.nextDouble();
			y = RANDOM.nextDouble();
			r = RANDOM.nextDouble();
			vx = (RANDOM.nextDouble() * 2 - 1) * VX;
			vy = (RANDOM.nextDouble() * 2 - 1) * VY;
			vr =  RANDOM.nextDouble()          * VR;
		}

		@Override
		public void update(UpdateContext context) {
			double
				max = Util.map_loge(Util.max(context.mono), 0, 4096, 16);
			x += vx * context.dt * max;
			y += vy * context.dt * max;
			r += vr * context.dt * max;
			if(x < 0) x += 1;
			if(x > 1) x -= 1;
			if(y < 0) y += 1;
			if(y > 1) y -= 1;
			if(r < 0 || r > 1) 
				vr *= -1;
		}

		@Override
		public void render(RenderContext context) {
			int
				x = (int)(this.x * context.canvas_w),
				y = (int)(this.y * context.canvas_h),
				r = (int)(this.r * Math.min(
						context.canvas_w, 
						context.canvas_h
						));
			context.g2D.setColor(Color.WHITE);
			context.g2D.drawOval(
					x - r,
					y - r,
					2 * r,
					2 * r
					);					
		}
	}
}
