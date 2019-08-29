package _kiwi.core.effect;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.RenderingHints;

import _kiwi.core.source.Source;

public class Circularity extends Effect {

	//store recent high values for right and lefts
	double[] peaksLeft = new double[Source.SAMPLES/4];
	double[] peaksRight = new double[Source.SAMPLES/4];

	//values necessary for scaling
	double root; //actual value
	double divider = 25; //constant value for reducing scale
	double scale; //value dependent on window size

	//values needed to make a circle
	float degree; //current amount of degrees in the circle
	double topXValue; //x pos value for top circle point
	double topYValue; //y pos value for top circle point
	double botXValue; //y pos value for bottom circle point
	double botYValue; //y pos value for bottom circle point

	//diameter of the circle
	int circleWidth;

	//speed that the circle collapses
	double speed;

	//storage for position before
	int lastTopX;
	int lastTopY;
	int lastBotX;
	int lastBotY;


	//contstructor
	public Circularity() {
		super("Circularity");
	}

	//operations needed to render the effect visually
	@Override
	public void onRender(RenderContext context) {

		speed = context.canvas_h/100;

		//toggle antialising
		context.g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		//fill background to black
		context.g2D.setColor(Color.BLACK);
		context.g2D.setStroke(new BasicStroke((int) speed));
		context.g2D.fillRect(
				0,
				0,
				context.canvas_w,
				context.canvas_h
				);			

		//set scale value based on height
		scale = context.canvas_h/divider;

		//color value
		Color myColor;

		//setting the circle width
		circleWidth = context.canvas_h/50;



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

			//calculate current degrees
			degree = (float)(i*Math.PI/256.0);

			//calculate x and y position for the top arc
			topXValue = (context.canvas_w/8*Math.cos(degree)+ peaksLeft[i]*Math.cos(degree))+context.canvas_w/2;
			topYValue = (context.canvas_w/8*Math.sin(degree)+ peaksLeft[i]*Math.sin(degree))+context.canvas_h/2;

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


			//set the color to make gradient effect
			myColor= new Color(255-i,i, 255);
			context.g2D.setColor(myColor);

			//calculate x and y position for the top arc
			botXValue = (context.canvas_w/8*Math.cos(-degree)+ peaksRight[i]*Math.cos(-degree))+context.canvas_w/2;
			botYValue = (context.canvas_w/8*Math.sin(-degree)+ peaksRight[i]*Math.sin(-degree))+context.canvas_h/2;

			//as long as its not the first of last value draw line this way
			if(i != 0 && i != Source.SAMPLES/4-1) {

				//draw line from current top point to current last
				context.g2D.drawLine(	lastTopX, 
						lastTopY, 
						(int)topXValue, 
						(int)topYValue);

				//draw line from current bottom point to last bottom point
				context.g2D.drawLine(	lastBotX, 
						lastBotY, 
						(int)botXValue, 
						(int)botYValue);

			}
			else //first value draw this way
				if(i == 0){
					context.g2D.drawLine((int)topXValue, (int)topYValue, (int)botXValue, (int)botYValue);
				}else //last value needs to be drawn this way
				{
					//connect the last top and bottom points
					context.g2D.drawLine((int)topXValue, (int)topYValue, (int)botXValue, (int)botYValue);

					//draw line from current top point to current last
					context.g2D.drawLine(	lastBotX, 
							lastBotY, 
							(int)botXValue, 
							(int)botYValue);

					//draw line from current bottom point to current last
					context.g2D.drawLine(	lastTopX, 
							lastTopY, 
							(int)topXValue, 
							(int)topYValue);
				}

			//set last values to equal current for next cycle in for loop
			lastTopX = (int) topXValue;
			lastTopY = (int) topYValue;
			lastBotY = (int) botYValue;
			lastBotX = (int) botXValue;

		}			



	}
}
