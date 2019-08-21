package kiwi.core.effect;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.RenderingHints;

import kiwi.math.Objects3D;

public class Shift extends Effect{

	public float[][] prism = new float[8][3];
	public static float[][] shiftPrism = new float[8][3];
	public static float[][] shiftedCollection = new float[128][3];
 	public float xRotation = 0.100f;
	public float yRotation = 0.01f;
	public float zRotation = 0.10f;

	public boolean xFlip = true;
	public boolean yFlip = true;
	public boolean zFlip = true;
	
	public float xSpeed = .001f;
	public float ySpeed = .005f;
	public float zSpeed = .0015f;

	public static float[] lastAverages = new float[16];
	
	
	
	public float[][] xMatrix = { 	{1.0f, 				0.0f,					0.0f					},
            						{0.0f, (float)Math.cos(xRotation), (float)-Math.sin(xRotation)	},
            						{0.0f, (float)Math.sin(xRotation), (float)Math.cos(xRotation)}	};

	public float[][] yMatrix = { 	{(float)Math.cos(yRotation), 0, (float)-Math.sin(yRotation)	},
            						{0.0f,                      	 1.0f,                       0.0f		},
            						{(float)Math.sin(yRotation), 0.0f, (float)Math.cos(yRotation)}	};

	public float[][] zMatrix = { 	{(float)Math.cos(zRotation), (float)-Math.sin(zRotation), 0.0f	},
            						{(float)Math.sin(zRotation), (float)Math.cos(zRotation),  0.0f	},
            						{				0.0f,                     0.0f,                 1.0f}};

	public float[][] projection = 	{{1.0f,0.0f,0.0f},
									{0.0f,1.0f,0.0f}};
	
	public Shift() {
		super("Shift");
	}

	@Override
	public void render(RenderContext context, boolean scaling) {
		
		float averageTotal = 0;

		float average = 0;
		double scale;
		if(scaling == true) {
			scale = context.canvas_h/200;}
			else {
			scale = context.canvas_h/3;}
		float logNum = (float)Math.log(50);
		float shift = context.canvas_w/40f;
		float width = context.canvas_w/20f;


		this.xMatrix = new float[][] 	{{1.0f, 				0.0f,					0.0f					},
		  	{0.0f, (float)Math.cos(xRotation), (float)-Math.sin(xRotation)},
		  	{0.0f, (float)Math.sin(xRotation), (float)Math.cos(xRotation)}};

		this.yMatrix = 	new float[][] 	{ 	{(float)Math.cos(yRotation), 0.0f, (float)-Math.sin(yRotation)	},
			{0.0f,                      	 1.0f,                       0.0f		},
			{(float)Math.sin(yRotation), 0.0f, (float)Math.cos(yRotation)}	};
			
		this.zMatrix = new float[][] 	{{(float)Math.cos(zRotation), (float)-Math.sin(zRotation), 0.0f	},
			{(float)Math.sin(zRotation), (float)Math.cos(zRotation),  0.0f	},
			{				0.0f,                     0.0f,                 1.0f}};


			for(int i = 0; i < 8; i++) {
				average = 0;
				for(int j = 0; j < 25; j++) {
					if(scaling == true) {
						average += scale*context.stereo_r[i*25+j].re;
					}else {
						average += scale*(Math.log(context.stereo_r[i*25+j].re))/logNum;
					}
					
				}
			
				average /= 25;
			
				average = (lastAverages[7-i] + average)/2.0f;
				
				if(average < 20 || Float.isNaN(average) ) {
					average = 20;
				}
				
				lastAverages[7-i] = average;
				
				
			
				float spacing = i*1.5f*-width;
				float left = (spacing)-shift-1.5f*width/2;
				float right = (spacing)+shift-1.5f*width/2;
			
			
			prism[0]= new float[] {right,average,width};	
			prism[1]= new float[] {left,average,width};
			prism[2]= new float[] {left,average,-width};
			prism[3]= new float[] {right,average,-width};
			prism[4]= new float[] {right,-average,width};	
			prism[5]= new float[] {left,-average,width};
			prism[6]= new float[] {left,-average,-width};
			prism[7]= new float[] {right,-average,-width};

			


			for(int k = 0; k < prism.length;k++) {
				shiftPrism[k] = Objects3D.matrixMultiply(prism[k], xMatrix);
				shiftPrism[k] = Objects3D.matrixMultiply(shiftPrism[k], yMatrix);
				shiftPrism[k] = Objects3D.matrixMultiply(shiftPrism[k], zMatrix);
				shiftedCollection[(7-i)*8+k] = shiftPrism[k];
				//System.out.format("Position %d: %f,%f,%f%n", i, shiftPrism[i][0],shiftPrism[i][1],shiftPrism[i][2]);
			}
			
			
		 }
			

		for(int i = 0; i < 8; i++) {
			average = 0;
			for(int j = 0; j < 25; j++) {
				if(scaling == true) {
					average += scale*context.stereo_l[i*25+j].re;
				}else {
					average += scale*(Math.log(context.stereo_l[i*25+j].re))/logNum;
				}
			}

			average /= 25;

			average = (lastAverages[i+8] + average)/2;
			
			if(average < 20 || Float.isNaN(average) ) {
				average = 20;
			}

			
			lastAverages[i+8] = average;
			
			

		    float spacing = i*1.5f*width;
			float left = (spacing)-shift+1.5f*width/2;
			float right = (spacing)+shift+1.5f*width/2;

		prism[0]= new float[] {right,average,width};	
		prism[1]= new float[] {left,average,width};
		prism[2]= new float[] {left,average,-width};
		prism[3]= new float[] {right,average,-width};
		prism[4]= new float[] {right,-average,width};	
		prism[5]= new float[] {left,-average,width};
		prism[6]= new float[] {left,-average,-width};
		prism[7]= new float[] {right,-average,-width};




		for(int k = 0; k < prism.length;k++) {
			shiftPrism[k] = Objects3D.matrixMultiply(prism[k], xMatrix);
			shiftPrism[k] = Objects3D.matrixMultiply(shiftPrism[k], yMatrix);
			shiftPrism[k] = Objects3D.matrixMultiply(shiftPrism[k], zMatrix);
			shiftedCollection[i*8+k+64] = shiftPrism[k];
			//System.out.format("Position %d: %f,%f,%f%n", i, shiftPrism[i][0],shiftPrism[i][1],shiftPrism[i][2]);
		}

		}

				for(int i = 0; i < lastAverages.length; i++) {
					averageTotal += lastAverages[i];
				}

				
				
				
				xRotation += xSpeed;
				yRotation += ySpeed;
				zRotation += zSpeed;
				
				if(xRotation> .5) {
					xFlip = true;
				}
				
				if(yRotation > .5) {
					yFlip = true;
				}
				
				if(zRotation > .5) {
					zFlip = true;
				}
				if(xRotation < -.5) {
					xFlip = false;
				}
				
				if(yRotation < -.5) {
					yFlip = false;
				}
				
				if(zRotation < -.5) {
					zFlip = false;
				}
				
					if(xFlip  == false) {
					xSpeed = averageTotal * .0006f * context.dt;
					} else {
						xSpeed = averageTotal * -.0006f * context.dt;	
					}
					if(yFlip == false) {
					ySpeed = averageTotal * .00012f * context.dt;
					}else {
					ySpeed = averageTotal * -.00012f * context.dt;
					}
					if(zFlip == false) {
					zSpeed = averageTotal * .0003f * context.dt;
					}else {
					zSpeed = averageTotal * -.0003f * context.dt;
					}
		
		context.g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		Color myColor = new Color(122, 180, 255);
		context.g2D.setColor(myColor);
		context.g2D.setStroke(new BasicStroke(3));
		
		context.g2D.fillRect(
				0,
				0,
				context.canvas_w,
				context.canvas_h
				);			
		
		
		
		

		
		if(xRotation > 0) {
			if(yRotation > 0) {
				drawLeftUp(context);
			}else {
				drawRightUp(context);
			}
		}else {
			if(yRotation > 0) {
				drawLeftDown(context);
			}else {
				drawRightDown(context);
			}
		}
		
		
		
	}

	
	
	public static void drawLeftUp(RenderContext context){
		
		Color myColor = new Color(122,50,255);
		context.g2D.setColor(myColor);
		int xCenter = context.canvas_w/2;
		int yCenter = context.canvas_h/2;
		
		int[] topX;
		int[] topY;
		int[] frontX;
		int[] frontY;
		int[] leftX;
		int[] leftY;
		
		for(int i = 15; i > -1; i--){
		
			topX = new int[]{(int)shiftedCollection[(i*8)+4][0]+xCenter, (int)shiftedCollection[(i*8)+5][0]+xCenter, (int)shiftedCollection[(i*8)+6][0]+xCenter, (int)shiftedCollection[(i*8)+7][0]+xCenter};
			topY = new int[]{(int)shiftedCollection[(i*8)+4][1]+yCenter, (int)shiftedCollection[(i*8)+5][1]+yCenter, (int)shiftedCollection[(i*8)+6][1]+yCenter, (int)shiftedCollection[(i*8)+7][1]+yCenter};
			
			frontX = new int[]{(int)shiftedCollection[(i*8)+0][0]+xCenter, (int)shiftedCollection[(i*8)+1][0]+xCenter, (int)shiftedCollection[(i*8)+5][0]+xCenter, (int)shiftedCollection[(i*8)+4][0]+xCenter};
			frontY = new int[]{(int)shiftedCollection[(i*8)+0][1]+yCenter, (int)shiftedCollection[(i*8)+1][1]+yCenter, (int)shiftedCollection[(i*8)+5][1]+yCenter, (int)shiftedCollection[(i*8)+4][1]+yCenter};
					
			leftX = new int[]{(int)shiftedCollection[(i*8)+1][0]+xCenter, (int)shiftedCollection[(i*8)+2][0]+xCenter, (int)shiftedCollection[(i*8)+6][0]+xCenter, (int)shiftedCollection[(i*8)+5][0]+xCenter};
			leftY = new int[]{(int)shiftedCollection[(i*8)+1][1]+yCenter, (int)shiftedCollection[(i*8)+2][1]+yCenter, (int)shiftedCollection[(i*8)+6][1]+yCenter, (int)shiftedCollection[(i*8)+5][1]+yCenter};
		
			if(lastAverages[i]/2 > 180) {
				
				
				myColor = new Color(180, 0,100);
				context.g2D.setColor(myColor);
				context.g2D.fillPolygon(leftX, leftY, 4);
				myColor = new Color(230, 0, 50);
				context.g2D.setColor(myColor);
				context.g2D.fillPolygon(frontX, frontY, 4);
				myColor = new Color(255, 0, 0);
				context.g2D.setColor(myColor);
				context.g2D.fillPolygon(topX, topY, 4);

				
			}else {
				myColor = new Color(180, 180-(int)lastAverages[i]/2,100);
				context.g2D.setColor(myColor);
				context.g2D.fillPolygon(leftX, leftY, 4);
				myColor = new Color(230, 180-(int)lastAverages[i]/2, 50);
				context.g2D.setColor(myColor);
				context.g2D.fillPolygon(frontX, frontY, 4);
				myColor = new Color(255, 180-(int)lastAverages[i]/2, 0);
				context.g2D.setColor(myColor);
				context.g2D.fillPolygon(topX, topY, 4);
			}
		

		}
		
	}
	
	public static void drawRightUp(RenderContext context){
		
		Color myColor = new Color(122,50,255);
		context.g2D.setColor(myColor);
		int xCenter = context.canvas_w/2;
		int yCenter = context.canvas_h/2;
		
		int[] topX;
		int[] topY;
		
		int[] frontX;
		int[] frontY;
		
		int[] rightX;
		int[] rightY;
		
		for(int i = 0; i < 16; i++) {
	
		topX = new int[] {(int)shiftedCollection[(i*8)+4][0]+xCenter, (int)shiftedCollection[(i*8)+5][0]+xCenter, (int)shiftedCollection[(i*8)+6][0]+xCenter, (int)shiftedCollection[(i*8)+7][0]+xCenter};
		topY = new int[] {(int)shiftedCollection[(i*8)+4][1]+yCenter, (int)shiftedCollection[(i*8)+5][1]+yCenter, (int)shiftedCollection[(i*8)+6][1]+yCenter, (int)shiftedCollection[(i*8)+7][1]+yCenter};
		
		frontX = new int[] {(int)shiftedCollection[(i*8)+0][0]+xCenter, (int)shiftedCollection[(i*8)+1][0]+xCenter, (int)shiftedCollection[(i*8)+5][0]+xCenter, (int)shiftedCollection[(i*8)+4][0]+xCenter};
		frontY = new int[] {(int)shiftedCollection[(i*8)+0][1]+yCenter, (int)shiftedCollection[(i*8)+1][1]+yCenter, (int)shiftedCollection[(i*8)+5][1]+yCenter, (int)shiftedCollection[(i*8)+4][1]+yCenter};
				
		rightX = new int[] {(int)shiftedCollection[(i*8)+0][0]+xCenter, (int)shiftedCollection[(i*8)+3][0]+xCenter, (int)shiftedCollection[(i*8)+7][0]+xCenter, (int)shiftedCollection[(i*8)+4][0]+xCenter};
		rightY = new int[] {(int)shiftedCollection[(i*8)+0][1]+yCenter, (int)shiftedCollection[(i*8)+3][1]+yCenter, (int)shiftedCollection[(i*8)+7][1]+yCenter, (int)shiftedCollection[(i*8)+4][1]+yCenter};
		

		if(lastAverages[i]/2 > 180) {
			
			
			myColor = new Color(180, 0,100);
			context.g2D.setColor(myColor);
			context.g2D.fillPolygon(rightX, rightY, 4);
			myColor = new Color(230, 0, 50);
			context.g2D.setColor(myColor);
			context.g2D.fillPolygon(frontX, frontY, 4);
			myColor = new Color(255, 0, 0);
			context.g2D.setColor(myColor);
			context.g2D.fillPolygon(topX, topY, 4);

			
		}else {
			myColor = new Color(180, 180-(int)lastAverages[i]/2,100);
			context.g2D.setColor(myColor);
			context.g2D.fillPolygon(rightX, rightY, 4);
			myColor = new Color(230, 180-(int)lastAverages[i]/2, 50);
			context.g2D.setColor(myColor);
			context.g2D.fillPolygon(frontX, frontY, 4);
			myColor = new Color(255, 180-(int)lastAverages[i]/2, 0);
			context.g2D.setColor(myColor);
			context.g2D.fillPolygon(topX, topY, 4);
		}
		

		}
		
	}

	public static void drawLeftDown(RenderContext context){
		
		Color myColor = new Color(122,50,255);
		context.g2D.setColor(myColor);
		int xCenter = context.canvas_w/2;
		int yCenter = context.canvas_h/2;
		
		int[] bottomX;
		int[] bottomY;
		int[] frontX;
		int[] frontY;
		int[] leftX;
		int[] leftY;
		
		for(int i = 15; i > -1; i--){
			bottomX = new int[] {(int)shiftedCollection[(i*8)+0][0]+xCenter, (int)shiftedCollection[(i*8)+1][0]+xCenter, (int)shiftedCollection[(i*8)+2][0]+xCenter, (int)shiftedCollection[(i*8)+3][0]+xCenter};
			bottomY = new int[] {(int)shiftedCollection[(i*8)+0][1]+yCenter, (int)shiftedCollection[(i*8)+1][1]+yCenter, (int)shiftedCollection[(i*8)+2][1]+yCenter, (int)shiftedCollection[(i*8)+3][1]+yCenter};
						
			frontX = new int[] {(int)shiftedCollection[(i*8)+0][0]+xCenter, (int)shiftedCollection[(i*8)+1][0]+xCenter, (int)shiftedCollection[(i*8)+5][0]+xCenter, (int)shiftedCollection[(i*8)+4][0]+xCenter};
			frontY = new int[] {(int)shiftedCollection[(i*8)+0][1]+yCenter, (int)shiftedCollection[(i*8)+1][1]+yCenter, (int)shiftedCollection[(i*8)+5][1]+yCenter, (int)shiftedCollection[(i*8)+4][1]+yCenter};
						
			leftX = new int[] {(int)shiftedCollection[(i*8)+1][0]+xCenter, (int)shiftedCollection[(i*8)+2][0]+xCenter, (int)shiftedCollection[(i*8)+6][0]+xCenter, (int)shiftedCollection[(i*8)+5][0]+xCenter};
			leftY = new int[] {(int)shiftedCollection[(i*8)+1][1]+yCenter, (int)shiftedCollection[(i*8)+2][1]+yCenter, (int)shiftedCollection[(i*8)+6][1]+yCenter, (int)shiftedCollection[(i*8)+5][1]+yCenter};
			
		
			if(lastAverages[i]/2 > 180) {
				
				
				myColor = new Color(180, 0,100);
				context.g2D.setColor(myColor);
				context.g2D.fillPolygon(leftX, leftY, 4);
				myColor = new Color(230, 0, 50);
				context.g2D.setColor(myColor);
				context.g2D.fillPolygon(frontX, frontY, 4);
				myColor = new Color(130, 0, 0);
				context.g2D.setColor(myColor);
				context.g2D.fillPolygon(bottomX, bottomY, 4);

				
			}else {
				myColor = new Color(180, 180-(int)lastAverages[i]/2,100);
				context.g2D.setColor(myColor);
				context.g2D.fillPolygon(leftX, leftY, 4);
				myColor = new Color(230, 180-(int)lastAverages[i]/2, 50);
				context.g2D.setColor(myColor);
				context.g2D.fillPolygon(frontX, frontY, 4);
				myColor = new Color(130, 180-(int)lastAverages[i]/2, 0);
				context.g2D.setColor(myColor);
				context.g2D.fillPolygon(bottomX, bottomY, 4);
			}
			
		
		}
		
	}

	public static void drawRightDown(RenderContext context){
		
		Color myColor = new Color(122,50,255);
		context.g2D.setColor(myColor);
		int xCenter = context.canvas_w/2;
		int yCenter = context.canvas_h/2;
		
		int[] bottomX;
		int[] bottomY;
		int[] frontX;
		int[] frontY;
		int[] rightX;
		int[] rightY;
		
		for(int i = 0; i < 16; i++) {
			bottomX = new int[] {(int)shiftedCollection[(i*8)+0][0]+xCenter, (int)shiftedCollection[(i*8)+1][0]+xCenter, (int)shiftedCollection[(i*8)+2][0]+xCenter, (int)shiftedCollection[(i*8)+3][0]+xCenter};
			bottomY = new int[] {(int)shiftedCollection[(i*8)+0][1]+yCenter, (int)shiftedCollection[(i*8)+1][1]+yCenter, (int)shiftedCollection[(i*8)+2][1]+yCenter, (int)shiftedCollection[(i*8)+3][1]+yCenter};
			
			frontX = new int[] {(int)shiftedCollection[(i*8)+0][0]+xCenter, (int)shiftedCollection[(i*8)+1][0]+xCenter, (int)shiftedCollection[(i*8)+5][0]+xCenter, (int)shiftedCollection[(i*8)+4][0]+xCenter};
			frontY = new int[] {(int)shiftedCollection[(i*8)+0][1]+yCenter, (int)shiftedCollection[(i*8)+1][1]+yCenter, (int)shiftedCollection[(i*8)+5][1]+yCenter, (int)shiftedCollection[(i*8)+4][1]+yCenter};		
		
			rightX = new int[] {(int)shiftedCollection[(i*8)+0][0]+xCenter, (int)shiftedCollection[(i*8)+3][0]+xCenter, (int)shiftedCollection[(i*8)+7][0]+xCenter, (int)shiftedCollection[(i*8)+4][0]+xCenter};
			rightY = new int[] {(int)shiftedCollection[(i*8)+0][1]+yCenter, (int)shiftedCollection[(i*8)+3][1]+yCenter, (int)shiftedCollection[(i*8)+7][1]+yCenter, (int)shiftedCollection[(i*8)+4][1]+yCenter};
			
			if(lastAverages[i]/2 > 180) {
				
				
				myColor = new Color(180, 0,100);
				context.g2D.setColor(myColor);
				context.g2D.fillPolygon(rightX, rightY, 4);
				myColor = new Color(230, 0, 50);
				context.g2D.setColor(myColor);
				context.g2D.fillPolygon(frontX, frontY, 4);
				myColor = new Color(130, 0, 0);
				context.g2D.setColor(myColor);
				context.g2D.fillPolygon(bottomX, bottomY, 4);

				
			}else {
				myColor = new Color(180, 180-(int)lastAverages[i]/2,100);
				context.g2D.setColor(myColor);
				context.g2D.fillPolygon(rightX, rightY, 4);
				myColor = new Color(230, 180-(int)lastAverages[i]/2, 50);
				context.g2D.setColor(myColor);
				context.g2D.fillPolygon(frontX, frontY, 4);
				myColor = new Color(130, 180-(int)lastAverages[i]/2, 0);
				context.g2D.setColor(myColor);
				context.g2D.fillPolygon(bottomX, bottomY, 4);
			}


		
		}
		
	}
	
	@Override
	public void render(RenderContext context) {
		
	}

	
	@Override
	public void update(UpdateContext context) {
		// TODO Auto-generated method stub

	}
	
	
}
