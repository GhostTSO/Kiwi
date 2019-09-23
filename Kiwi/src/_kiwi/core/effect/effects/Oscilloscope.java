package _kiwi.core.effect.effects;

import java.awt.BasicStroke;
import java.awt.Color;

import _kiwi.core.effect.Effect;
import kiwi.core.source.Source;

public class Oscilloscope extends Effect {

	int peak = 50;
	int[] pointsLeft = new int[600];
	int[] pointsRight = new int[600];
	int counter = 0;
	int repeat = 0;
	
	public Oscilloscope() {
		super("Oscilloscope");
		// TODO Auto-generated constructor stub
		background = new Color(0f,0f,0f,.01f);
		
	}

	
	public void render(RenderContext context) {
		
		Color myColor = new Color(0,0,0,.1f);
		context.g2D.setColor(myColor);
		context.g2D.fillRect(0, 0, context.canvas_w, context.canvas_h);
		myColor = new Color(255,0,0);
		context.g2D.setColor(myColor);
		context.g2D.drawLine(0, context.canvas_h/2, context.canvas_w, context.canvas_h/2);
		myColor = new Color(0,255,0);
		context.g2D.setColor(myColor);
		

		context.g2D.setStroke(new BasicStroke((2*context.canvas_w/Source.SAMPLES)+1));
		for(int i = 0; i < pointsLeft.length-1; i++) {
		
		//context.g2D.fillOval((i*context.canvas_w/(pointsLeft.length-1))-5, pointsLeft[i]-10, 10, 10);
		
		context.g2D.drawLine(i*context.canvas_w/(pointsLeft.length-1), pointsLeft[i]/2+5, (i+1)*context.canvas_w/(pointsLeft.length-1), pointsLeft[i+1]/2+5);
		
		//context.g2D.fillOval((i*context.canvas_w/(pointsRight.length-1))-5, context.canvas_h- pointsRight[i]-10, 10, 10);
		
		context.g2D.drawLine(i*context.canvas_w/(pointsRight.length-1), context.canvas_h- pointsRight[i]/2-5, (i+1)*context.canvas_w/(pointsRight.length-1),context.canvas_h- pointsRight[i+1]/2-5);
		
		context.g2D.drawLine(i*context.canvas_w/(pointsRight.length-1), context.canvas_h/2-(pointsRight[i]-pointsLeft[i])/2, (i+1)*context.canvas_w/(pointsRight.length-1), context.canvas_h/2-(pointsRight[i+1]-pointsLeft[i+1])/2);
		
		}
		
//		System.out.println(Integer.toString(pointsLeft[counter]));
//		myColor = new Color(0,255,0);
//		context.g2D.setColor(myColor);
//		
//		context.g2D.drawLine(context.canvas_w-5, pointsLeft[pointsLeft.length-1]-5, context.canvas_w-5, pointsLeft[pointsLeft.length-1]-5);
//		
//		myColor = new Color(0,0,255);
//		context.g2D.setColor(myColor);
//		
//		context.g2D.drawLine(pointsRight[pointsRight.length-1]-5, context.canvas_h-5, pointsRight[pointsRight.length-1]-5,context.canvas_h-5);
		
		
	}
	
	public void onUpdate(UpdateContext context) {
		double left = 0;
		double right= 0;
		
		for(int i = 0; i < 1024; i++) {
			left += context.stereo_l[i];
			right += context.stereo_r[i];
		}
		
		if(left > peak) {
			peak = (int) left;
			repeat = 0;
		}else {
			repeat++;
		}
		
		if(right > peak) {
			peak = (int) right;
			repeat = 0;
		}else {
			repeat++;
		}
		
		left = ((left - 0) * (context.canvas_h-0))/(peak - 0);
		right = ((right - 0) * (context.canvas_h-0))/(peak - 0);
		
		
		
//		System.out.println(peak);
		
//		System.out.println("Vert vect: " +verticalVector + " Window Height: " + context.canvas_h + " Difference: " + (context.canvas_h-verticalVector));
		
		pointsLeft[counter] = (int)left;
		pointsRight[counter] = (int)right;
		
		if(repeat > 100) {
			if(peak > 100) {
			peak -= 100;
			}
			repeat = 0;
		}
		
		counter++;
		if(counter >= pointsLeft.length)
			counter = 0;
	}
	
}
