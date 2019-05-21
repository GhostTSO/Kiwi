package kiwi.core;
import java.awt.BasicStroke;
import java.awt.Color;
import java.util.LinkedList;
import java.util.List;

import kiwi.core.render.RenderContext;
import kiwi.core.render.Renderable;
import kiwi.core.update.UpdateContext;
import kiwi.core.update.Updateable;

public abstract class Style implements Renderable, Updateable {
	public String
		name;
	
	public static List<Style> getAvailableStyles() {
		List<Style> list = new LinkedList<>();
		list.add(new StandardStyle());
		return list;
	}
	
	public static class StandardStyle extends Style {
		
		public StandardStyle() {
			this.name = "Standard";
		}
		
		@Override
		public void render(RenderContext context) {
			context.g2D.setColor(Color.BLACK);
			context.g2D.setStroke(new BasicStroke(2));
			context.g2D.fillRect(
					0,
					0,
					context.canvas_w,
					context.canvas_h
					);			
				
			for(int i = 0; i < context.samples; i ++) {
				context.g2D.setColor(Color.WHITE);	
				context.g2D.drawLine(
						i,
						(int)(context.canvas_h/4+(context.l_channel[i].re)),
						i,
						(int)(context.canvas_h/4-(context.r_channel[i].re))
						);		
				
				context.g2D.setColor(Color.WHITE);
				context.g2D.drawLine(
						i,
						(int)(3*context.canvas_h/4+(context.r_channel[i].re)),
						i,
						(int)(3*context.canvas_h/4-(context.r_channel[i].re))
						);	
				
			}
		}
		
		@Override
		public void update(UpdateContext context) {
			
		}
	}

}
