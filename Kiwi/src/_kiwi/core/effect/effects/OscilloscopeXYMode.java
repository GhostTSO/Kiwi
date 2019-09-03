package _kiwi.core.effect.effects;

import java.awt.Color;

import _kiwi.core.effect.Effect;
import _kiwi.util.Util;

public class OscilloscopeXYMode extends Effect {

	int[] pointsVert = new int[600];
	int[] pointsHor = new int[600];
	int counter = 0;
	
	public OscilloscopeXYMode() {
		super("Oscilloscope XY Mode");
		// TODO Auto-generated constructor stub
		background = new Color(0f,0f,0f,.8f);
		
	}

	
	public void render(RenderContext context) {
		
		Color myColor = new Color(0,0,0,.8f);
		context.g2D.setColor(myColor);
		context.g2D.fillRect(0, 0, context.canvas_w, context.canvas_h);
		for(int i = 0; i < pointsVert.length-1; i++) {
		myColor = new Color(0,255,0);
		context.g2D.setColor(myColor);
		
		switch(i%4) {
			case 0:
				context.g2D.fillOval(pointsHor[i]-10+context.canvas_w/2, pointsVert[i]+context.canvas_h/2-16, 8, 8);
				break;
			case 1:
				context.g2D.fillOval(pointsHor[i]-10+context.canvas_w/2, -pointsVert[i]+context.canvas_h/2-16, 8, 8);
				break;
			case 2:
				context.g2D.fillOval(-pointsHor[i]-10+context.canvas_w/2, -pointsVert[i]+context.canvas_h/2-16, 8, 8);
				break;
			case 3:
				context.g2D.fillOval(-pointsHor[i]-10+context.canvas_w/2, pointsVert[i]+context.canvas_h/2-16, 8, 8);
				break;
		}
		//context.g2D.fillOval(pointsHor[i]-10+context.canvas_w/2, pointsVert[i]+context.canvas_h/2-10, 5, 5);
		
//		context.g2D.drawLine(pointsHor[i]-10+context.canvas_w/2, pointsVert[i]+context.canvas_h/2-5, pointsHor[i+1]+context.canvas_w/2-10, pointsVert[i+1]-5+context.canvas_h/2);
		}
//		context.g2D.drawLine(pointsHor[pointsHor.length-1]+context.canvas_w/2-5, pointsVert[pointsVert.length-2]+context.canvas_h/2-5, pointsHor[pointsHor.length-1]-5+context.canvas_w/2,pointsVert[pointsVert.length-2]+context.canvas_h/2-5);
		
	}
	 
	public void onUpdate(UpdateContext context) {
		double horizontalVector;
		double verticalVector;
		
		verticalVector = context.stereo_r[0]; //Util.map(context.stereo_l[0], 0, 25000, 0, context.canvas_h/2);
		horizontalVector = context.stereo_l[0];//Util.map(context.stereo_r[0], 0, 25000, 0, context.canvas_w/2);
		
		//System.out.println(verticalVector);
		
		pointsVert[counter] = (int)verticalVector/10;
		pointsHor[counter] = (int)horizontalVector/10;
		
		counter++;
		if(counter >= pointsVert.length)
			counter = 0;
	}
	
}
