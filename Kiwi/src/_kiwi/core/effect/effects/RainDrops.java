package _kiwi.core.effect.effects;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;

import _kiwi.core.effect.Effect;
import _kiwi.core.source.Source;

public class RainDrops extends Effect {

	//store recent high values for right and lefts
	double[] peaksLeft = new double[Source.SAMPLES/4];
	double[] peaksRight = new double[Source.SAMPLES/4];

	//values necessary for scaling
	double root; //actual value
	double divider = 100; //constant value for reducing scale
	double scale; //value dependent on window size

	//values needed to make a circle
	float degree; //current amount of degrees in the circle
	double topXValue; //x pos value for top circle point
	double topYValue; //y pos value for top circle point
	double botXValue; //y pos value for bottom circle point
	double botYValue; //y pos value for bottom circle point

	//points storage to draw the shape
	double pointsTopMiddle[][] = new double[2][85];
	double pointsTopLeft[][] = new double[2][85];
	double pointsTopRight[][] = new double[2][85];
	double pointsBotMiddle[][] = new double[2][85];
	double pointsBotRight[][] = new double[2][85];
	double pointsBotLeft[][] = new double[2][85];
	double height[] = new double[6]; 

	//diameter of the circle
	int circleWidth;

	//speed that the circle collapses
	double speed;

	//storage for position before
	int lastTopX;
	int lastTopY;
	int lastBotX;
	int lastBotY;

	//variable to store color state
	double colorCounter1 = 120;
	double colorCounter2 = 60;
	double colorCounter3 = 20;

	//Variable to flip to control color speed
	double colorSpeed1;
	double colorSpeed2;
	double colorSpeed3;



	//constructor
	public RainDrops() {
		super("Rain Drops");
		colorSpeed1 = Math.random()*.5;
		colorSpeed2 = Math.random()*.8;
		colorSpeed3 = Math.random()*.25;
		
		colorCounter1 = Math.random() * 200;
		colorCounter2 = Math.random() * 200;
		colorCounter3 = Math.random() * 200;
	}

	//operations needed to render the effect visually
	@Override
	public void onRender(RenderContext context) {

		//create a render path to make the shape
		GeneralPath circle1 = new GeneralPath();
		GeneralPath circle2 = new GeneralPath();
		GeneralPath circle3 = new GeneralPath();
		GeneralPath circle4 = new GeneralPath();
		GeneralPath circle5 = new GeneralPath();
		GeneralPath circle6 = new GeneralPath();

		//set speed of movement based on canvas size
		speed = context.canvas_h/100;

		//toggle antialising
		context.g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		//set scale value based on height
		scale = context.canvas_h/divider;

		//setting the circle width
		circleWidth = context.canvas_h/500;

		//for loop to go through 1/4 of the samples
		for(int i = 0; i < Source.SAMPLES/4; i ++) {

			//if the value is worth checking then we find a value
			if(context.stereo_l[i+3*Source.SAMPLES/4] > 1) {

				//find the new value
				root = scale*(Math.log(context.stereo_l[i+3*Source.SAMPLES/4]));

				//if the new value is greater than the old we store it
				if(root > peaksLeft[i]) {
					peaksLeft[i] = root;
				}else //shrink the circle if there is room to shrink
					if(peaksLeft[i] > speed){
						peaksLeft[i] -= speed;
					}else //set to default value 0
					{
						peaksLeft[i] = 0;
					}
			}else //shrink the circle
				if(peaksLeft[i] > speed){
					peaksLeft[i] -= speed;
				}else //set to default value 0
				{
					peaksLeft[i] = 0;
				}
			height[2*(i/86)+1] += peaksLeft[i]; 
			
			//calculate current degrees
			degree = (float)(i*Math.PI/42.5);

			//calculate x and y position for the top arc
			topXValue = (context.canvas_w/20*Math.cos(degree)+ peaksLeft[i]*Math.cos(degree));
			topYValue = (context.canvas_w/20*Math.sin(degree)+ peaksLeft[i]*Math.sin(degree));

			//if the value is worth checking
			if(context.stereo_r[i+3*Source.SAMPLES/4] > 1) {

				//find the new value
				root = scale*(Math.log(context.stereo_r[i+3*Source.SAMPLES/4]));

				//if the new value is greater than the old we store it
				if(root > peaksRight[i]) {
					peaksRight[i] = root;
				}else //shrink the circle if there is room to shrink
					if(peaksRight[i] > speed){
						peaksRight[i] -= speed;
					}else //set to default value 0
					{
						peaksRight[i] = 0;
					}
			}else //shrink the circle if there is room to shrink
				if(peaksRight[i] > speed){
					peaksRight[i] -= speed;
				}else //set to default value 0
				{
					peaksRight[i] = 0;
				}
			height[2*(i/86)] += peaksRight[i]; 

			//calculate x and y position for the top arc
			botXValue = (context.canvas_w/20*Math.cos(-degree)+ peaksRight[i]*Math.cos(-degree));
			botYValue = (context.canvas_w/20*Math.sin(-degree)+ peaksRight[i]*Math.sin(-degree));

			
			if(i<85) {
				
				pointsTopRight[0][i] = (int)topXValue;
				pointsTopRight[1][i] = (int)topYValue;
				pointsBotRight[0][i] = (int)botXValue;
				pointsBotRight[1][i] = (int)botYValue;

			}else if(i < 170) {
				pointsTopLeft[0][i%85] = (int)topXValue;
				pointsTopLeft[1][i%85] = (int)topYValue;
				pointsBotLeft[0][i%85] = (int)botXValue;
				pointsBotLeft[1][i%85] = (int)botYValue;
			}else {
				pointsTopMiddle[0][i%85] = (int)topXValue;
				pointsTopMiddle[1][i%85] = (int)topYValue;
				pointsBotMiddle[0][i%85] = (int)botXValue;
				pointsBotMiddle[1][i%85] = (int)botYValue;
			}
			
		}						



			height[0] /= 5;
			height[1] /= 5;
			height[2] /= 5;
			height[3] /= 5;
			height[4] /= 5;
			height[5] /= 5;

			
		
		
		//oscillate color 1 speed
		if(colorCounter1 > 250) {
			colorSpeed1 *= -1;
		}else if(colorCounter1 < 20) {
			colorSpeed1 *= -1;
			colorCounter1 = 20;
		}

		//oscillate color 2 speed
		if(colorCounter2 > 250) {
			colorSpeed2 *= -1;
		}else if(colorCounter2 < 20) {
			colorSpeed2 *= -1;
			colorCounter2 = 20;
		}

		//oscillate color 3 speed
		if(colorCounter3 > 250) {
			colorSpeed3 *= -1;
		}else if(colorCounter3 < 20) {
			colorSpeed3 *= -1;
			colorCounter3 = 20;
		}

		//increment colors
		colorCounter1 +=colorSpeed1;
		colorCounter2 += colorSpeed2;
		colorCounter3 += colorSpeed3;

		//create our colors
		Color color1 = new Color((int)colorCounter3, (int)(255-colorCounter2), (int)colorCounter1);
		Color color2 = new Color((int)colorCounter2,(int)(colorCounter3), (int)255);
		Color color3 = new Color(255,(int)(255-colorCounter1), (int)colorCounter3);
		Color color4 = new Color((int)(255-colorCounter1),255, (int)(255-colorCounter3));
		
		
		//color gradient for the background
		GradientPaint gp4 = new GradientPaint(0, 0, 
				color4, context.canvas_w, context.canvas_h, color3, true);
		context.g2D.setPaint(gp4);
		context.g2D.fillRect(0, 0, context.canvas_w, context.canvas_h);
		
		//color gradient for circle
		gp4 = new GradientPaint(0, 0, 
				color1, context.canvas_w/5, context.canvas_h, color2, true);
		context.g2D.setPaint(gp4);
		
		//begin drawing the shape
		circle1.moveTo(pointsTopMiddle[0][0]+(3*context.canvas_w/7), pointsTopMiddle[1][0]+height[0]);		
		circle2.moveTo(pointsBotMiddle[0][0]+(4*context.canvas_w/7), pointsBotMiddle[1][0]-height[1]+context.canvas_h);	
		circle3.moveTo(pointsTopLeft[0][0]+(context.canvas_w/7), pointsTopLeft[1][0]+height[2]);	
		circle4.moveTo(pointsBotLeft[0][0]+(2*context.canvas_w/7), pointsBotLeft[1][0]-height[3]+context.canvas_h);	
		circle5.moveTo(pointsTopRight[0][0]+(5*context.canvas_w/7), pointsTopRight[1][0]+height[4]);	
		circle6.moveTo(pointsBotRight[0][0]+(6*context.canvas_w/7), pointsBotRight[1][0]-height[5]+context.canvas_h);	

		//draw the rest of the points
		for (int k = 1; k < 85; k++) {
			circle1.lineTo(pointsTopMiddle[0][k]+(3*context.canvas_w/7), pointsTopMiddle[1][k]+height[0]);
			circle2.lineTo(pointsBotMiddle[0][k]+(4*context.canvas_w/7), pointsBotMiddle[1][k]-height[1]+context.canvas_h);
			circle3.lineTo(pointsTopLeft[0][k]+(context.canvas_w/7), pointsTopLeft[1][k]+height[2]);
			circle4.lineTo(pointsBotLeft[0][k]+(2*context.canvas_w/7), pointsBotLeft[1][k]-height[3]+context.canvas_h);
			circle5.lineTo(pointsTopRight[0][k]+(5*context.canvas_w/7), pointsTopRight[1][k]+height[4]);
			circle6.lineTo(pointsBotRight[0][k]+(6*context.canvas_w/7), pointsBotRight[1][k]-height[5]+context.canvas_h);
		}

		//finish the shape
		circle1.closePath();
		circle2.closePath();
		circle3.closePath();
		circle4.closePath();
		circle5.closePath();
		circle6.closePath();
		context.g2D.fill(circle1);
		context.g2D.fill(circle2);
		context.g2D.fill(circle3);
		context.g2D.fill(circle4);
		context.g2D.fill(circle5);
		context.g2D.fill(circle6);
	}
}
