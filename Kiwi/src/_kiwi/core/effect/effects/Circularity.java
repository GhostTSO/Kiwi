package _kiwi.core.effect.effects;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import _kiwi.core.effect.Effect;
import _kiwi.core.source.Source;

public class Circularity extends Effect {

	//store recent high values for right and lefts
	double[] peaksLeft = new double[Source.SAMPLES/4];
	double[] peaksRight = new double[Source.SAMPLES/4];

	//values necessary for scaling
	double root; //actual value
	double divider = 40; //constant value for reducing scale
	double scale; //value dependent on window size

	//values needed to make a circle
	float degree; //current amount of degrees in the circle
	double topXValue; //x pos value for top circle point
	double topYValue; //y pos value for top circle point
	double botXValue; //y pos value for bottom circle point
	double botYValue; //y pos value for bottom circle point

	//points storage to draw the shape
	double points[][] = new double[2][510];

	//speed that the circle collapses
	double speed;
	
	double peak = 50;
	int repeat = 0;

	//storage for position before
	int lastTopX;
	int lastTopY;
	int lastBotX;
	int lastBotY;


	BufferedImage backgroundImage; 
	BufferedImage foregroundImage; 
	TexturePaint backPaint;
	
	float redColor = .3f;
	float blueColor = .6f;
	float greenColor = .2f;
	
	float redSpeed = .001f;
	float blueSpeed= .005f;
	float greenSpeed= .003f;
	
	
	//contstructor
	public Circularity() {
		super("Circularity");
		
		//load background image
				try {
					backgroundImage = ImageIO.read(new File(".\\bin\\_kiwi\\core\\effect\\effects\\resources\\CircularityBackground.png"));
					
				}catch(IOException ex) {
					System.out.println("Couldn't find file");
				}
				
	}

	//operations needed to render the effect visually
	@Override
	public void onRender(RenderContext context) {
		
		if(redColor > .95f) {
			redSpeed *= -1;
		}else if(redColor < .05f) {
			redSpeed *= -1;
		}
		
		if(blueColor > .8f) {
			blueSpeed *= -1;
		}else if(blueColor < .5f) {
			blueSpeed *= -1;
		}
		
		if(greenColor > .45f) {
			greenSpeed *= -1;
		}else if(greenColor < .13f) {
			greenSpeed *= -1;
		}
		
		redColor += redSpeed;
		blueColor += blueSpeed;
		greenColor += greenSpeed;
		
		//create a render path to make the shape
		GeneralPath circle = new GeneralPath();
		
		
		context.g2D.drawImage(backgroundImage, 0,0,context.canvas_w,context.canvas_h, 0,0, backgroundImage.getWidth(), backgroundImage.getHeight(), null);


		//toggle antialising
		context.g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		context.g2D.setColor(new Color(redColor,0f,1f-redColor,.5f));
		
		//begin drawing the shape
		circle.moveTo(points[0][0], points[1][0]);	
	

		//draw the rest of the points
		for (int k = 1; k < 510; k++) {
			circle.lineTo(points[0][k], points[1][k]);
		}

		//finish the shape
		circle.closePath();
		
		context.g2D.fill(circle);

	}
	
	public void onUpdate(UpdateContext context) {

		//set speed of movement based on canvas size
		speed = (60.0*context.dt)* (double) ( context.canvas_h/100.0);
		
		//set scale value based on height
				scale = context.canvas_h/divider;

				//for loop to go through 1/4 of the samples
				for(int i = 0; i < Source.SAMPLES/4; i ++) {

					//if the value is worth checking then we find a value
					if(context.stereo_l[i+3*Source.SAMPLES/4] > 1) {

						if(context.stereo_l[i+3*Source.SAMPLES/4] > peak){
							peak = context.stereo_l[i+3*Source.SAMPLES/4];
							repeat = 0;
						}
						switch(context.hint) {

						case LIN:
							
							//find the new value
							root = ((context.stereo_l[i+3*Source.SAMPLES/4] - 0) * (context.canvas_h-0))/(peak - 0);
							break;
						case LOG:
							
							//find the new value
							root = ((context.stereo_l[i+3*Source.SAMPLES/4] - 0) * (context.canvas_h-0))/(peak - 0);
							//find the new value
							root = 2*scale*(Math.log(root));
							break;
						case TANH:
							
							//find the new value
							root = ((context.stereo_l[i+3*Source.SAMPLES/4] - 0) * (1+1))/(peak - 0);
							
							//find the new value
							root = 25*scale*(Math.tanh(root));	
							break;
						}
						
						
						

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
					degree = (float)((i+128)*Math.PI/255.0);

					//calculate x and y position for the top arc
					topXValue = (context.canvas_w/6*Math.cos(degree)+ peaksLeft[i]*Math.cos(degree))+context.canvas_w/2;
					topYValue = (context.canvas_w/6*Math.sin(degree)+ peaksLeft[i]*Math.sin(degree))+3*context.canvas_h/5;

					//if the value is worth checking
					if(context.stereo_r[i+3*Source.SAMPLES/4] > 1) {

						//find the new value
						if(context.stereo_l[i+3*Source.SAMPLES/4] > peak){
							peak = context.stereo_l[i+3*Source.SAMPLES/4];
							repeat = 0;
						}
						
						switch(context.hint) {

						
						
						case LIN:
							
							//find the new value
							root = ((context.stereo_r[i+3*Source.SAMPLES/4] - 0) * (context.canvas_h-0))/(peak - 0);
							break;
						case LOG:
							
							root = ((context.stereo_r[i+3*Source.SAMPLES/4] - 0) * (context.canvas_h-0))/(peak - 0);
							//find the new value
							root = 2*scale*(Math.log(root));
							break;
						case TANH:
							
							//find the new value
							root = ((context.stereo_r[i+3*Source.SAMPLES/4] - 0) * (1.0+1.0))/(peak - 0);
							
							//find the new value
							root = 25*scale*(Math.tanh(root));		
							break;
						}

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


					degree = (float)((i-128)*Math.PI/255.0);
					
					//calculate x and y position for the top arc
					botXValue = (context.canvas_w/6*Math.cos(-degree)+ peaksRight[i]*Math.cos(-degree))+context.canvas_w/2;
					botYValue = (context.canvas_w/6*Math.sin(-degree)+ peaksRight[i]*Math.sin(-degree))+3*context.canvas_h/5;

					//store the points into the point array to be rendered later
					points[0][i] = (int)topXValue;
					points[1][i] = (int)topYValue;
					points[0][509-i] = (int)botXValue;
					points[1][509-i] = (int)botYValue;
				}						

				
				if(repeat > 100) {
					if(peak > 100) {
						peak *= .95;
						System.out.println(peak);
					}
					repeat = 0;
				}
				repeat++;
	}
}
