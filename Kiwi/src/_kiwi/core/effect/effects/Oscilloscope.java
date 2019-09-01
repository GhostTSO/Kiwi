package _kiwi.core.effect.effects;

import java.awt.Color;

import _kiwi.core.effect.Effect;
import _kiwi.util.Util;

public class Oscilloscope extends Effect {

	int[] pointsVert = new int[480];
	int[] pointsHor = new int[480];
	int counter = 0;
	
	public Oscilloscope() {
		super("Oscilloscope");
		// TODO Auto-generated constructor stub
		background = new Color(0f,0f,0f,.01f);
	}

	
	public void render(RenderContext context) {
		
		double horizontalVector = 0;
		double verticalVector = 0;
		
		for(int i = 0; i < 1024; i++) {
			horizontalVector += context.stereo_l[i];
			verticalVector += context.stereo_r[i];
		}
		
		verticalVector = Util.map(verticalVector, 0, 25000, 0, context.canvas_h);
		horizontalVector = Util.map(horizontalVector, 0, 25000, 0, context.canvas_w);
		
		//System.out.println(verticalVector);
		
		pointsVert[counter] = (int)verticalVector;
		pointsHor[counter] = (int)horizontalVector;
		
		Color myColor = new Color(0,0,0,.1f);
		context.g2D.setColor(myColor);
		context.g2D.fillRect(0, 0, context.canvas_w, context.canvas_h);
		for(int i = 0; i < pointsVert.length-1; i++) {
		myColor = new Color(0,255,0);
		context.g2D.setColor(myColor);
		context.g2D.fillOval((i*context.canvas_w/(pointsVert.length-1))-5, pointsVert[i]-10, 10, 10);
		
		context.g2D.drawLine(i*context.canvas_w/(pointsVert.length-1), pointsVert[i]-5, (i+1)*context.canvas_w/(pointsVert.length-1), pointsVert[i+1]-5);
		
		
		myColor = new Color(0,0,255);
		context.g2D.setColor(myColor);
		context.g2D.fillOval(pointsHor[i]-10, (i*context.canvas_h/(pointsHor.length-1))-5,  10, 10);
		
		context.g2D.drawLine(pointsHor[i]-5,i*context.canvas_h/(pointsHor.length-1), pointsHor[i+1]-5, (i+1)*context.canvas_h/(pointsHor.length-1));
		
		}
		myColor = new Color(0,255,0);
		context.g2D.setColor(myColor);
		
		context.g2D.drawLine(context.canvas_w-5, pointsVert[pointsVert.length-1]-5, context.canvas_w-5, pointsVert[pointsVert.length-1]-5);
		
		myColor = new Color(0,0,255);
		context.g2D.setColor(myColor);
		
		context.g2D.drawLine(pointsHor[pointsHor.length-1]-5, context.canvas_h-5, pointsHor[pointsHor.length-1]-5,context.canvas_h-5);
		
		
		counter++;
		if(counter >= pointsVert.length)
			counter = 0;
		
	}
	
}
