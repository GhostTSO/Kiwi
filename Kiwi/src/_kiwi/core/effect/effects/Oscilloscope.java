package _kiwi.core.effect.effects;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import _kiwi.core.effect.Effect;
import kiwi.core.source.Source;

public class Oscilloscope extends Effect {

	int peak = 50;
	int[] pointsLeft = new int[600];
	int[] pointsRight = new int[600];
	int counter = 0;
	int repeat = 0;

	BufferedImage backgroundImage; 

	public Oscilloscope() {
		super("Oscilloscope");
		// TODO Auto-generated constructor stub
		try {
			backgroundImage = ImageIO.read(new File(".\\bin\\_kiwi\\core\\effect\\effects\\resources\\OscilloscopeBackground.png"));
		}catch(IOException ex) {
			System.out.println("Couldn't find file");
		}

	}


	public void render(RenderContext context) {

		Color myColor = new Color(0,0,0);
		//context.g2D.setColor(myColor);
		//context.g2D.fillRect(0, 0, context.canvas_w, context.canvas_h);
		context.g2D.drawImage(backgroundImage, 0,0,context.canvas_w,context.canvas_h, 0,0, backgroundImage.getWidth(), backgroundImage.getHeight(), null);
		//		myColor = new Color(255,0,0);
		//		context.g2D.setColor(myColor);
		//		context.g2D.drawLine(0, context.canvas_h/2, context.canvas_w, context.canvas_h/2);
		//		myColor = new Color(0,255,0);
		context.g2D.setColor(myColor);

		GeneralPath fill = new GeneralPath();


		context.g2D.setStroke(new BasicStroke((2*context.canvas_w/Source.SAMPLES)+1));

		fill.moveTo(0, pointsLeft[counter]);


		for(int i = 1; i <600; i++) {


			fill.lineTo(i*context.canvas_w/(pointsLeft.length-1), pointsLeft[(i+counter)%599]/2+5);

//			context.g2D.drawLine(i*context.canvas_w/(pointsLeft.length-1), pointsLeft[(i+counter)%599]/2+5, (i+1)*context.canvas_w/(pointsLeft.length-1), pointsLeft[(i+counter)%599+1]/2+5);

			//			context.g2D.drawLine(i*context.canvas_w/(pointsRight.length-1), context.canvas_h- pointsRight[(i+counter)%599]/2-5, (i+1)*context.canvas_w/(pointsRight.length-1),context.canvas_h- pointsRight[(i+counter)%599+1]/2-5);

			//			context.g2D.drawLine(i*context.canvas_w/(pointsRight.length-1), context.canvas_h/2-(pointsRight[(i+counter)%599]-pointsLeft[(i+counter)%599])/2, (i+1)*context.canvas_w/(pointsRight.length-1), context.canvas_h/2-(pointsRight[(i+counter)%599+1]-pointsLeft[(i+counter)%599+1])/2);

		}

		for(int i = 599; i > -1; i--) {


			fill.lineTo(i*context.canvas_w/(pointsLeft.length-1), context.canvas_h- pointsRight[(i+counter)%599]/2-5);

//			context.g2D.drawLine(i*context.canvas_w/(pointsLeft.length-1), pointsLeft[(i+counter)%599]/2+5, (i+1)*context.canvas_w/(pointsLeft.length-1), pointsLeft[(i+counter)%599+1]/2+5);

			//			context.g2D.drawLine(i*context.canvas_w/(pointsRight.length-1), context.canvas_h- pointsRight[(i+counter)%599]/2-5, (i+1)*context.canvas_w/(pointsRight.length-1),context.canvas_h- pointsRight[(i+counter)%599+1]/2-5);

			//			context.g2D.drawLine(i*context.canvas_w/(pointsRight.length-1), context.canvas_h/2-(pointsRight[(i+counter)%599]-pointsLeft[(i+counter)%599])/2, (i+1)*context.canvas_w/(pointsRight.length-1), context.canvas_h/2-(pointsRight[(i+counter)%599+1]-pointsLeft[(i+counter)%599+1])/2);

		}
		
		fill.moveTo(0, pointsRight[counter]);

//		context.g2D.drawLine((599-counter)*context.canvas_w/(pointsLeft.length-1), pointsLeft[599]/2+5, ((599-counter)+1)*context.canvas_w/(pointsLeft.length-1), pointsLeft[0]/2+5);
//		context.g2D.drawLine((599-counter)*context.canvas_w/(pointsRight.length-1), context.canvas_h- pointsRight[599]/2-5, ((599-counter)+1)*context.canvas_w/(pointsRight.length-1),context.canvas_h- pointsRight[0]/2-5);
//		context.g2D.drawLine((599-counter)*context.canvas_w/(pointsRight.length-1), context.canvas_h/2-(pointsRight[599]-pointsLeft[598])/2, ((599-counter)+1)*context.canvas_w/(pointsRight.length-1), context.canvas_h/2-(pointsRight[0]-pointsLeft[0])/2);


		//draws right of counter
		//		for(int i = counter+1; i < 599; i++) {
		//		context.g2D.drawLine((i-counter)*context.canvas_w/(pointsLeft.length-1), pointsLeft[i]/2+5, ((i-counter)+1)*context.canvas_w/(pointsLeft.length-1), pointsLeft[i+1]/2+5);
		//		context.g2D.drawLine((i-counter)*context.canvas_w/(pointsRight.length-1), context.canvas_h- pointsRight[i]/2-5, ((i-counter)+1)*context.canvas_w/(pointsRight.length-1),context.canvas_h- pointsRight[i+1]/2-5);
		//		context.g2D.drawLine((i-counter)*context.canvas_w/(pointsRight.length-1), context.canvas_h/2-(pointsRight[i]-pointsLeft[i])/2, ((i-counter)+1)*context.canvas_w/(pointsRight.length-1), context.canvas_h/2-(pointsRight[i+1]-pointsLeft[i+1])/2);
		//		}
		//		
		//		myColor = new Color(0,255,0);
		//		context.g2D.setColor(myColor);
		//		
		//		int h = 0;
		//		for(int i = 599 - counter; i < 599; i++) {
		//			context.g2D.drawLine((i)*context.canvas_w/(pointsLeft.length-1), pointsLeft[h]/2+5, ((i)+1)*context.canvas_w/(pointsLeft.length-1), pointsLeft[h+1]/2+5);
		//			context.g2D.drawLine((i)*context.canvas_w/(pointsRight.length-1), context.canvas_h- pointsRight[h]/2-5, ((i)+1)*context.canvas_w/(pointsRight.length-1),context.canvas_h- pointsRight[h+1]/2-5);
		//			context.g2D.drawLine((i)*context.canvas_w/(pointsRight.length-1), context.canvas_h/2-(pointsRight[h]-pointsLeft[h])/2, ((i)+1)*context.canvas_w/(pointsRight.length-1), context.canvas_h/2-(pointsRight[h+1]-pointsLeft[h+1])/2);
		//			h++;
		//			}
		//		context.g2D.drawLine((598-h)*context.canvas_w/(pointsLeft.length-1), pointsLeft[599]/2+5, ((598-h)+1)*context.canvas_w/(pointsLeft.length-1), pointsLeft[0]/2+5);
		//		context.g2D.drawLine((598-h)*context.canvas_w/(pointsRight.length-1), context.canvas_h- pointsRight[599]/2-5, ((598-h)+1)*context.canvas_w/(pointsRight.length-1),context.canvas_h- pointsRight[0]/2-5);
		//		context.g2D.drawLine((598-h)*context.canvas_w/(pointsRight.length-1), context.canvas_h/2-(pointsRight[599]-pointsLeft[598])/2, ((598-h)+1)*context.canvas_w/(pointsRight.length-1), context.canvas_h/2-(pointsRight[0]-pointsLeft[0])/2);


		fill.closePath();
		
		context.g2D.fill(fill);



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
				peak *= .95;
			}
			System.out.println(peak);
			repeat = 0;
		}

		counter--;
		if(counter < 0)
			counter = 599;
	}

}
