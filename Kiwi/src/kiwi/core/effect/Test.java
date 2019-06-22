package kiwi.core.effect;

import java.awt.Color;

public class Test extends Effect {
	float
		x,
		y,
		vx = 16,
		vy = 16;

	public Test() {
		super("Test");
	}

	@Override
	public void render(RenderContext context) {
		context.g2D.setColor(Color.BLACK);
		context.g2D.fillRect(
				0, 
				0,
				context.canvas_w,
				context.canvas_h
				);
		context.g2D.setColor(Color.WHITE);
		context.g2D.fillOval(
				(int)x - 8,
				(int)y - 8,
				16,
				16
				);
	}

	@Override
	public void update(UpdateContext context) {
		x += vx * context.dt;
		y += vy * context.dt;
		if(x < 0 || x > context.canvas_w) {
			if(x > context.canvas_w)
				x = context.canvas_w;
			if(x < 0) 
				x = 0f;
			vx = -vx;
		}
		if(y < 0 || y > context.canvas_h) {
			if(y > context.canvas_h)
				y = context.canvas_h;
			if(y < 0) 
				y = 0f;
			vy = -vy;
		}
	}
	
}
