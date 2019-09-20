package _kiwi.core.effect.effects;

import java.awt.Color;

import _kiwi.core.effect.Effect;

public class Oscilloscope extends Effect {

	int[] pointsLeft = new int[600];
	int[] pointsRight = new int[600];
	int counter = 0;
	
	public Oscilloscope() {
		super("Oscilloscope");
		// TODO Auto-generated constructor stub
		background = new Color(0f,0f,0f,.01f);
		
	}

	
	public void render(RenderContext context) {
		
		Color myColor = new Color(0,0,0,.1f);
		context.g2D.setColor(myColor);
		context.g2D.fillRect(0, 0, context.canvas_w, context.canvas_h);
		for(int i = 0; i < pointsLeft.length-1; i++) {
		myColor = new Color(0,255,0);
		context.g2D.setColor(myColor);
		context.g2D.fillOval((i*context.canvas_w/(pointsLeft.length-1))-5, pointsLeft[i]-10, 10, 10);
		
		context.g2D.drawLine(i*context.canvas_w/(pointsLeft.length-1), pointsLeft[i]-5, (i+1)*context.canvas_w/(pointsLeft.length-1), pointsLeft[i+1]-5);
		
		
		myColor = new Color(0,0,255);
		context.g2D.setColor(myColor);
		context.g2D.fillOval((i*context.canvas_w/(pointsLeft.length-1))-5, pointsRight[i]-10, 10, 10);
		
		context.g2D.drawLine(i*context.canvas_w/(pointsLeft.length-1), context.canvas_h - pointsRight[i]-5, (i+1)*context.canvas_w/(pointsLeft.length-1), context.canvas_h - pointsRight[i-1]-5);
		
		}
		myColor = new Color(0,255,0);
		context.g2D.setColor(myColor);
		
		context.g2D.drawLine(context.canvas_w-5, pointsLeft[pointsLeft.length-1]-5, context.canvas_w-5, pointsLeft[pointsLeft.length-1]-5);
		
		myColor = new Color(0,0,255);
		context.g2D.setColor(myColor);
		
		context.g2D.drawLine(pointsRight[pointsRight.length-1]-5, context.canvas_h-5, pointsRight[pointsRight.length-1]-5,context.canvas_h-5);
		
	}
	
	public void onUpdate(UpdateContext context) {
		double left = 0;
		double right= 0;
		
		for(int i = 0; i < 1024; i++) {
			left += context.stereo_l[i];
			right += context.stereo_r[i];
		}
		
		left = ((left - 0) * (context.canvas_h-0))/(500000 - 0);
		right = ((right - 0) * (context.canvas_h-0))/(500000 - 0);
		
//		System.out.println("Vert vect: " +verticalVector + " Window Height: " + context.canvas_h + " Difference: " + (context.canvas_h-verticalVector));
		
		pointsLeft[counter] = (int)left;
		pointsRight[counter] = (int)right;
		
		counter++;
		if(counter >= pointsLeft.length)
			counter = 0;
	}
	
}
