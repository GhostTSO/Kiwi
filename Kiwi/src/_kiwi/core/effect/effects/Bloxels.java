package _kiwi.core.effect.effects;

import java.awt.Color;
import _kiwi.core.effect.Effect;


public class Bloxels extends Effect {

	//storage for averages for the render cycle
	int[] averages = new int[12];
	
	public Bloxels() {
		super("Bloxels");
		background = new Color(0f, 0f, 0f, .1f);
	}

	@Override
	public void onRender(RenderContext context) {

		//initialized color
		Color myColor;
		
		//loop to draw the averages left to right
		for(int i = 0; i < 12; i++) {
			//loop to draw the averages height
			for(int j = 0; j < averages[i]; j++) {
				//change color
				if(j < 6) {
				myColor = new Color((j*50), 0, 255-(j*50));
				} 
				//terminate inner loop if value is 7 to prevent drawing off screen
				else if( j >= 7) {
					break;
				}
				//this is only when counter equals 6, this is to prevent from generating a negative color value
				else {
					myColor = new Color(255, 0, 0);
				}
				
				//set the context to the color and draw the round rectangles
				context.g2D.setColor(myColor);
				context.g2D.fillRoundRect((i*context.canvas_w/12) + 2, 
						((j)*context.canvas_h/6)+5, context.canvas_w/13, context.canvas_h/7, 40, context.canvas_h/10);
				
			}
		}
		
		
	}


	@Override 
	public void onUpdate(UpdateContext context) {
		//sum values
		int sumLeft = 0;
		int sumRight = 0;
		
		//loop through 12 values (6 right + 6 left)
		for(int i = 0; i < 6; i++) {
			
			//sum 1/12 of the data 
			for(int j = 0; j < context.stereo_l.length/12; j++) {
				sumLeft += Math.log(context.stereo_l[(i*context.stereo_l.length/12)+j]);
				sumRight+= Math.log(context.stereo_r[(i*context.stereo_l.length/12)+j]);
			}
			
			//average the values
			averages[5-i] = sumLeft/(context.stereo_l.length/12);
			averages[6+i] = sumRight/(context.stereo_l.length/12);
			
			//reset the sum values
			sumLeft = 0;
			sumRight = 0;
		}
	}
}
