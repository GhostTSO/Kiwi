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
		context.g2D.drawImage(backgroundImage, 0,0,context.canvas_w,context.canvas_h, 0,0, backgroundImage.getWidth(), backgroundImage.getHeight(), null);
		context.g2D.setColor(myColor);

		GeneralPath top = new GeneralPath();
		GeneralPath bottom = new GeneralPath();


		top.moveTo(0, 0);
		top.lineTo(0, context.canvas_h/2 - pointsLeft[counter]);
		bottom.moveTo(0, context.canvas_h/2 );
		bottom.lineTo(0, context.canvas_h/2 +pointsRight[counter]);


		for(int i = 1; i <600; i++) {
			top.lineTo(i*context.canvas_w/(pointsLeft.length-1), context.canvas_h/2 - pointsLeft[(i+counter)%599]/2);
			bottom.lineTo(i*context.canvas_w/(pointsLeft.length-1), context.canvas_h/2+ pointsRight[(i+counter)%599]/2);
		}

		
		top.lineTo(context.canvas_w, 0);
		bottom.lineTo(context.canvas_w, context.canvas_h);
		top.lineTo(0, 0);
		bottom.lineTo(0, context.canvas_h);


		top.closePath();
		bottom.closePath();
		
		context.g2D.fill(top);
		context.g2D.fill(bottom);


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

		pointsLeft[counter] = (int)left;
		pointsRight[counter] = (int)right;

		if(repeat > 100) {
			if(peak > 100) {
				peak *= .95;
			}
			repeat = 0;
		}

		counter--;
		if(counter < 0)
			counter = 599;
	}

}
