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

public class Horizon extends Effect {

	//store recent high values for right and lefts
	double[] peaksLeft = new double[Source.SAMPLES/4];
	double[] peaksRight = new double[Source.SAMPLES/4];

	//values necessary for scaling
	double root; //actual value
	double divider = 12.5; //constant value for reducing scale
	double scale; //value dependent on window size

	//values needed to make a circle
	double topXValue; //x pos value for top circle point
	double topYValue; //y pos value for top circle point
	double botXValue; //y pos value for bottom circle point
	double botYValue; //y pos value for bottom circle point

	//points storage to draw the shape
	double points[][] = new double[2][510];

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

	BufferedImage backgroundImage; 
	TexturePaint back;

	//constructor
	public Horizon() {
		super("Horizon");
		
		//load background image
		try {
			backgroundImage = ImageIO.read(new File(".\\bin\\_kiwi\\core\\effect\\effects\\resources\\HorizonBackground.png"));
		}catch(IOException ex) {
			System.out.println("Couldn't find file");
		}

	}

	//operations needed to render the effect visually
	@Override
	public void onRender(RenderContext context) {
		//toggle antialising
		context.g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		//create a render path to make the shape
		GeneralPath shape= new GeneralPath();

		//sets paint to the appropriate image and size
		back = new TexturePaint(backgroundImage, new Rectangle(0,0, context.canvas_w, context.canvas_h));
		context.g2D.setPaint(back);

		//begin drawing the shape
		shape.moveTo(points[0][0], points[1][0]+context.canvas_h/2);	

		//draw the rest of the points
		for (int k = 1; k < 510; k++) {
			shape.lineTo(points[0][k], points[1][k]+context.canvas_h/2);
		}
		
		//finish the shape
		shape.closePath();
		context.g2D.fill(shape);
	}

	public void onUpdate(UpdateContext context) {

		//set speed of movement based on canvas size
		speed = context.canvas_h/50;

		//set scale value based on height
		scale = context.canvas_h/divider;

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


			//calculate x and y position for the top arc
			topXValue = (i-1)*context.canvas_w/252;
			topYValue = -peaksLeft[i];

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


			//calculate x and y position for the top arc
			botXValue = (i-1)*context.canvas_w/252;
			botYValue = peaksRight[i];

			//store the points into the point array to be rendered later
			points[0][i] = (int)topXValue;
			points[1][i] = (int)topYValue-2;
			points[0][509-i] = (int)botXValue;
			points[1][509-i] = (int)botYValue+2;
		}						
	}
}
