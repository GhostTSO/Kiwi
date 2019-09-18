package _kiwi.core.effect.effects;

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
	double divider = 25; //constant value for reducing scale
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

	//storage for position before
	int lastTopX;
	int lastTopY;
	int lastBotX;
	int lastBotY;

	//variable to store color state
//	double colorCounter1 = 120;
//	double colorCounter2 = 60;
//	double colorCounter3 = 20;

	//Variable to flip to control color speed
//	double colorSpeed1;
//	double colorSpeed2;
//	double colorSpeed3;

	BufferedImage backgroundImage; 
	BufferedImage foregroundImage; 
	TexturePaint backPaint;
	TexturePaint forePaint;
	

	//contstructor
	public Circularity() {
		super("Circularity");
//		colorSpeed1 = Math.random()*.5;
//		colorSpeed2 = Math.random()*.8;
//		colorSpeed3 = Math.random()*.25;
//		
//		colorCounter1 = Math.random() * 200;
//		colorCounter2 = Math.random() * 200;
//		colorCounter3 = Math.random() * 200;
		
		
		//load background image
				try {
					backgroundImage = ImageIO.read(new File(".\\bin\\_kiwi\\core\\effect\\effects\\resources\\CircularityBackground.png"));
					foregroundImage = ImageIO.read(new File(".\\bin\\_kiwi\\core\\effect\\effects\\resources\\CircularityForeground.png"));
					
				}catch(IOException ex) {
					System.out.println("Couldn't find file");
				}
		
				
	}

	//operations needed to render the effect visually
	@Override
	public void onRender(RenderContext context) {
		
		
		//create a render path to make the shape
		GeneralPath circle = new GeneralPath();
		
		backPaint = new TexturePaint(foregroundImage, new Rectangle(0,0,context.canvas_w,context.canvas_h));
		
		context.g2D.drawImage(backgroundImage, 0,0,context.canvas_w,context.canvas_h, 0,0, backgroundImage.getWidth(), backgroundImage.getHeight(), null);


		//toggle antialising
		context.g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		//create our colors
//		Color color1 = new Color((int)colorCounter3, (int)(255-colorCounter2), (int)colorCounter1);
//		Color color2 = new Color((int)colorCounter2,(int)(colorCounter3), (int)255);
//		Color color3 = new Color(255,(int)(255-colorCounter1), (int)colorCounter3);
//		Color color4 = new Color((int)(255-colorCounter1),255, (int)(255-colorCounter3));
		
		//color gradient for the background
		//GradientPaint gp4 = new GradientPaint(0, 0, 
		//		color4, context.canvas_w, context.canvas_h, color3, true);
		//context.g2D.setPaint(gp4);
		//context.g2D.setColor(new Color(0,0,0));
		//context.g2D.fillRect(0, 0, context.canvas_w, context.canvas_h);
		
		context.g2D.setPaint(backPaint);
		
		//color gradient for circle
		//gp4 = new GradientPaint(0, 0, 
		//		color1, context.canvas_w/5, context.canvas_h, color2, true);
		//context.g2D.setPaint(gp4);
		
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
		System.out.println(context.dt);
		
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

					//calculate current degrees
					degree = (float)(i*Math.PI/255.0);

					//calculate x and y position for the top arc
					topXValue = (context.canvas_w/6*Math.cos(degree)+ peaksLeft[i]*Math.cos(degree))+context.canvas_w/2;
					topYValue = (context.canvas_w/6*Math.sin(degree)+ peaksLeft[i]*Math.sin(degree))+context.canvas_h/2;

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
					botXValue = (context.canvas_w/6*Math.cos(-degree)+ peaksRight[i]*Math.cos(-degree))+context.canvas_w/2;
					botYValue = (context.canvas_w/6*Math.sin(-degree)+ peaksRight[i]*Math.sin(-degree))+context.canvas_h/2;

					//store the points into the point array to be rendered later
					points[0][i] = (int)topXValue;
					points[1][i] = (int)topYValue;
					points[0][509-i] = (int)botXValue;
					points[1][509-i] = (int)botYValue;
				}						

//				//oscillate color 1 speed
//				if(colorCounter1 > 250) {
//					colorSpeed1 *= -1;
//				}else if(colorCounter1 < 20) {
//					colorSpeed1 *= -1;
//					colorCounter1 = 20;
//				}
//
//				//oscillate color 2 speed
//				if(colorCounter2 > 250) {
//					colorSpeed2 *= -1;
//				}else if(colorCounter2 < 20) {
//					colorSpeed2 *= -1;
//					colorCounter2 = 20;
//				}
//
//				//oscillate color 3 speed
//				if(colorCounter3 > 250) {
//					colorSpeed3 *= -1;
//				}else if(colorCounter3 < 20) {
//					colorSpeed3 *= -1;
//					colorCounter3 = 20;
//				}
//
//				//increment colors
//				colorCounter1 +=colorSpeed1;
//				colorCounter2 += colorSpeed2;
//				colorCounter3 += colorSpeed3;
	}
}
