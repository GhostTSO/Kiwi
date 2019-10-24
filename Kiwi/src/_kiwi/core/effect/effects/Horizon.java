package _kiwi.core.effect.effects;

import java.awt.Color;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import _kiwi.core.effect.Effect;
import _kiwi.core.source.Source;

public class Horizon extends Effect {

	//store recent high values for right and lefts
	double[] peaksLeft = new double[510];
	double[] peaksRight = new double[510];

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

	double peak=100;
	int counter = 0;

	//storage for position before
	int lastTopX;
	int lastTopY;
	int lastBotX;
	int lastBotY;

	BufferedImage backgroundImage; 
	//	TexturePaint back;

	//constructor
	public Horizon() {
		super("Horizon");

		//load background image
		try {
			backgroundImage = ImageIO.read(new File(".\\bin\\_kiwi\\core\\effect\\effects\\resources\\HorizonBackground.png"));
			//			back = new TexturePaint(backgroundImage, new Rectangle(0,0, 1920, 1080));
		}catch(IOException ex) {
			System.out.println("Couldn't find file");
		}

	}

	//operations needed to render the effect visually
	@Override
	public void onRender(RenderContext context) {
		//toggle antialising
		context.g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);


		//create a render path for the black areas of the image
		GeneralPath top = new GeneralPath();
		GeneralPath bottom = new GeneralPath();


		//draws background to the appropriate image and size
		context.g2D.drawImage(backgroundImage, 0,0,context.canvas_w,context.canvas_h, 0,0, backgroundImage.getWidth(), backgroundImage.getHeight(), null);
		context.g2D.setColor(new Color(0f,0f,0f,1f));

		//begin drawing the shape
		top.moveTo(points[0][0], points[1][0]);
		bottom.moveTo(points[0][509], points[1][509]);


		//draw the rest of the points
		for (int k = 1; k < 255; k++) {
			top.lineTo(points[0][k], points[1][k]/2+context.canvas_h/2);
			bottom.lineTo(points[0][509-k], points[1][509-k]/2+context.canvas_h/2);

		}


		for(int i = 0; i < 10; i++) {
			top.lineTo((10*context.canvas_w)/(10-i), 0);
			bottom.lineTo((10*context.canvas_w)/(10-i), context.canvas_h);
		}

		top.lineTo(0, 0);
		bottom.lineTo(0, context.canvas_h);


		//finish the shape
		top.closePath();
		bottom.closePath();
		context.g2D.fill(top);
		context.g2D.fill(bottom);
	}

	public void onUpdate(UpdateContext context) {


		switch(context.hint) {

		case LIN:
			speed = context.canvas_h/50;
			break;
		case LOG:
			speed = context.canvas_h/50;
			scale = context.canvas_h/divider;
			break;
		case TANH:
			speed = context.canvas_h/50;
			scale = context.canvas_h/divider;
			break;
		}


		//set speed of movement based on canvas size


		//set scale value based on height




		//for loop to go through 1/4 of the samples
		for(int i = 0; i < 255; i ++) {

			//if the value is worth checking then we find a value
			if(context.stereo_l[i+3*Source.SAMPLES/4] > 1) {

				switch(context.hint) {

				case LIN:

					if(context.stereo_l[i+3*Source.SAMPLES/4] > peak){
						peak = context.stereo_l[i+3*Source.SAMPLES/4];
						counter = 0;
					}
					//find the new value
					root = ((context.stereo_l[i+3*Source.SAMPLES/4] - 0) * (context.canvas_h-0))/(peak - 0);
					break;
				case LOG:
					//find the new value
					root = scale*(Math.log(context.stereo_l[i+3*Source.SAMPLES/4]));
					break;
				case TANH:
					//find the new value
					root = scale*(Math.tanh(context.stereo_l[i+3*Source.SAMPLES/4]));	
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


			//calculate x and y position for the top arc
			topXValue = (i-1)*context.canvas_w/252;
			topYValue = -peaksLeft[i];

			//if the value is worth checking
			if(context.stereo_r[i+3*Source.SAMPLES/4] > 1) {

				switch(context.hint) {

				case LIN:
					if(context.stereo_r[i+3*Source.SAMPLES/4] > peak){
						peak = context.stereo_r[i+3*Source.SAMPLES/4];
						counter = 0;
					}
					//find the new value
					root = ((context.stereo_r[i+3*Source.SAMPLES/4] - 0) * (context.canvas_h-0))/(peak - 0);
					break;
				case LOG:
					//find the new value
					root = scale*(Math.log(context.stereo_r[i+3*Source.SAMPLES/4]));
					break;
				case TANH:
					//find the new value
					root = scale*(Math.tanh(context.stereo_r[i+3*Source.SAMPLES/4]));	
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


			//calculate x and y position for the top arc
			botXValue = (i-1)*context.canvas_w/252;
			botYValue = peaksRight[i];

			//store the points into the point array to be rendered later
			points[0][i] = (int)topXValue;
			points[1][i] = (int)topYValue-2;
			points[0][509-i] = (int)botXValue;
			points[1][509-i] = (int)botYValue+2;

			counter++;

			if(counter > 100) {
				if(peak > 100) {
					counter = 0;
					peak *= .95;
				}
			}
		}						
	}
}
