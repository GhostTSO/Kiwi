package kiwi.core.effect;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.RenderingHints;

import kiwi.core.source.Source;

public class SpaceStation extends Effect{
	
	
	
	public SpaceStation() {
		super("Space Station");
	}
	
	@Override
	public void render(RenderContext context, boolean scaling) {
		
		context.g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		Color myColor = new Color(0,0,0);
		context.g2D.setColor(myColor);
		context.g2D.setStroke(new BasicStroke(context.canvas_w/20));
		
		context.g2D.clearRect(
				0,
				0,
				context.canvas_w,
				context.canvas_h
				);			
		

		
		double root;
		double logNum = Math.log(50);
		double wSpacing = (double)context.canvas_w/20;
		double hSpacing = (double)context.canvas_w/20*Math.tan(45);
		double scale;
		if(scaling == true) {
			scale = context.canvas_h/200;}
			else {
			scale = context.canvas_h/3;}
		double average = 0;
		
	
		
		for(int i = 0; i < 5; i ++) {
			myColor = new Color(i*50,i*50,255);
			context.g2D.setColor(myColor);
			average = 0;
			for(int j = 0; j < 25; j++) {
				if(scaling == true) {
					root = context.stereo_l[(25*i)+j].re;
				}else {
					average += scale*(Math.log(context.stereo_l[(25*i)+j].re))/logNum;
				}
				
			}
			average /= 25;

			if(average < 20) {
				average = 20;
			}
			if(i < 2) {
			context.g2D.drawLine(
					(int) -(average*Math.cos(45)),
					(int) (context.canvas_h-(2*hSpacing) + (hSpacing*(i)) + average*Math.sin(45)),
					(int) (average*Math.cos(45)),
					(int) (context.canvas_h-(2*hSpacing) + (hSpacing*(i)) - average*Math.sin(45)));
			}else if(i > 2) {
			context.g2D.drawLine(
					(int) (wSpacing*(i-2)),
					(int) (context.canvas_h),
					(int) (wSpacing*(i-2)+average*Math.cos(45)),
					(int) ((context.canvas_h) - average*Math.sin(45))
					);
			
			}else {
			context.g2D.drawLine(
					0,
					context.canvas_h,
					(int) (average*Math.cos(45)),
					(int) (context.canvas_h - average*Math.sin(45))
					);
					
		}
			
			
			average = 0;
			for(int j = 0; j < 25; j++) {
				if(scaling == true) {
					root = scale*context.stereo_r[(25*i)+j].re;
				}else {
					average += scale*(Math.log(context.stereo_r[(25*i)+j].re))/logNum;
				}
				
			}
			average /= 25;

			if(average < 20) {
			average = 20;
			}
			if(i < 2) {
			context.g2D.drawLine(
					(int) (context.canvas_w+average*Math.cos(45)),
					(int) (context.canvas_h-(2*hSpacing) + (hSpacing*(i)) + average*Math.sin(45)),
					(int) (context.canvas_w-average*Math.cos(45)),
					(int) (context.canvas_h-(2*hSpacing) + (hSpacing*(i)) - average*Math.sin(45)));
			}else if(i > 2) {
			context.g2D.drawLine(
					(int) (context.canvas_w-wSpacing*(i-2)),
					(int) (context.canvas_h),
					(int) (context.canvas_w-(wSpacing*(i-2)+average*Math.cos(45))),
					(int) ((context.canvas_h) - average*Math.sin(45))
					);
			
			}else {
			context.g2D.drawLine(
					context.canvas_w,
					context.canvas_h,
					(int) (context.canvas_w-average*Math.cos(45)),
					(int) (context.canvas_h - average*Math.sin(45))
					);
					
		}
			
		
			
	}
}

	@Override
	public void render(RenderContext context) {
		
	}
	
	@Override
	public void update(UpdateContext context) {
		
	}

}
