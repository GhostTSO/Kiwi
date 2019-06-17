package kiwi.core.effect;

import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.RenderingHints;

import kiwi.core.Effect;
import kiwi.core.Source;
import kiwi.core.render.RenderContext;
import kiwi.core.update.UpdateContext;
import kiwi.math.Objects3D;

public class Shift extends Effect{

	public float[][] prism = new float[8][3];
	public float[][] shiftPrism = new float[8][3];
	public float xRotation = 0;
	public float yRotation = 0;
	public float zRotation = 0;
	
	public float xSpeed = .002f;
	public float ySpeed = .001f;
	public float zSpeed = .003f;
	
	public float[] yValueRight = new float[8];
	public float[] yValueLeft = new float[8];
	
	
	public float[][] xMatrix = { 	{1, 				0,					0					},
            						{0, (float)Math.cos(xRotation), (float)-Math.sin(xRotation)	},
            						{0, (float)Math.sin(xRotation), (float)Math.cos(xRotation)}	};

	public float[][] yMatrix = { 	{(float)Math.cos(yRotation), 0, (float)-Math.sin(yRotation)	},
            						{0,                      	 1,                       0		},
            						{(float)Math.sin(yRotation), 0, (float)Math.cos(yRotation)}	};

	public float[][] zMatrix = { 	{(float)Math.cos(zRotation), (float)-Math.sin(zRotation), 0	},
            						{(float)Math.sin(zRotation), (float)Math.cos(zRotation),  0	},
            						{				0,                     0,                 1}};

	public float[][] projection = 	{{1,0,0},
									{0,1,0}};
	
	public Shift() {
		super("Shift");
	}

	@Override
	public void render(RenderContext context) {
		context.g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		Color myColor = new Color(0,0,0);
		context.g2D.setColor(myColor);
		context.g2D.setStroke(new BasicStroke(3));
		
		context.g2D.clearRect(
				0,
				0,
				context.canvas_w,
				context.canvas_h
				);			
		
		
		float average = 0;
		float scale = context.canvas_h/3;;
		double logNum = Math.log(50);
		float shift = context.canvas_w/40;
		float width = context.canvas_w/20;
		
		
		this.xMatrix = new float[][] 	{{1, 				0,					0					},
		  	{0, (float)Math.cos(xRotation), (float)-Math.sin(xRotation)},
		  	{0, (float)Math.sin(xRotation), (float)Math.cos(xRotation)}};

		this.yMatrix = 	new float[][] 	{ 	{(float)Math.cos(yRotation), 0, (float)-Math.sin(yRotation)	},
			{0,                      	 1,                       0		},
			{(float)Math.sin(yRotation), 0, (float)Math.cos(yRotation)}	};
			
		this.zMatrix = new float[][] 	{{(float)Math.cos(zRotation), (float)-Math.sin(zRotation), 0	},
			{(float)Math.sin(zRotation), (float)Math.cos(zRotation),  0	},
			{				0,                     0,                 1}};
		
		
			for(int i = 0; i < 8; i++) {
				average = 0;
				for(int j = 0; j < 25; j++) {
					average += scale*(Math.log(context.stereo_r[i*25+j].re))/logNum;
				}
			
				average /= 25;
			
				if(average < 20) {
					average = 20;
				}
			
				yValueRight[i] = average;
			
			
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
				//System.out.format("Position %d: %f,%f,%f%n", i, shiftPrism[i][0],shiftPrism[i][1],shiftPrism[i][2]);
			}
			
//			float[] result = Objects3D.matrixMultiply(new float[] {100,50,25}, new float[][] {{1,2,3},{4,5,6},{7,8,9}});
//			
//			String test =  String.format("%f,%f,%f", result[0], result[1], result[2]);
//			
//			context.g2D.drawString(test, 50, 50);
			
//			myColor = new Color(255,0,0);
//			context.g2D.setColor(myColor);
//			context.g2D.drawLine((int)shiftPrism[0][0]+context.canvas_w/2, (int)shiftPrism[0][1]+context.canvas_h/2, (int)shiftPrism[1][0]+context.canvas_w/2, (int)shiftPrism[1][1]+context.canvas_h/2);
//			context.g2D.drawLine((int)shiftPrism[1][0]+context.canvas_w/2, (int)shiftPrism[1][1]+context.canvas_h/2, (int)shiftPrism[2][0]+context.canvas_w/2, (int)shiftPrism[2][1]+context.canvas_h/2);
//			context.g2D.drawLine((int)shiftPrism[2][0]+context.canvas_w/2, (int)shiftPrism[2][1]+context.canvas_h/2, (int)shiftPrism[3][0]+context.canvas_w/2, (int)shiftPrism[3][1]+context.canvas_h/2);
//			context.g2D.drawLine((int)shiftPrism[3][0]+context.canvas_w/2, (int)shiftPrism[3][1]+context.canvas_h/2, (int)shiftPrism[0][0]+context.canvas_w/2, (int)shiftPrism[0][1]+context.canvas_h/2);
//			
//			myColor = new Color(0,255,0);
//			context.g2D.setColor(myColor);
//			context.g2D.drawLine((int)shiftPrism[4][0]+context.canvas_w/2, (int)shiftPrism[4][1]+context.canvas_h/2, (int)shiftPrism[5][0]+context.canvas_w/2, (int)shiftPrism[5][1]+context.canvas_h/2);
//			context.g2D.drawLine((int)shiftPrism[5][0]+context.canvas_w/2, (int)shiftPrism[5][1]+context.canvas_h/2, (int)shiftPrism[6][0]+context.canvas_w/2, (int)shiftPrism[6][1]+context.canvas_h/2);
//			context.g2D.drawLine((int)shiftPrism[6][0]+context.canvas_w/2, (int)shiftPrism[6][1]+context.canvas_h/2, (int)shiftPrism[7][0]+context.canvas_w/2, (int)shiftPrism[7][1]+context.canvas_h/2);
//			context.g2D.drawLine((int)shiftPrism[7][0]+context.canvas_w/2, (int)shiftPrism[7][1]+context.canvas_h/2, (int)shiftPrism[4][0]+context.canvas_w/2, (int)shiftPrism[4][1]+context.canvas_h/2);
//			
//			myColor = new Color(0,0,255);
//			context.g2D.setColor(myColor);
//			context.g2D.drawLine((int)shiftPrism[0][0]+context.canvas_w/2, (int)shiftPrism[0][1]+context.canvas_h/2, (int)shiftPrism[4][0]+context.canvas_w/2, (int)shiftPrism[4][1]+context.canvas_h/2);
//			context.g2D.drawLine((int)shiftPrism[1][0]+context.canvas_w/2, (int)shiftPrism[1][1]+context.canvas_h/2, (int)shiftPrism[5][0]+context.canvas_w/2, (int)shiftPrism[5][1]+context.canvas_h/2);
//			context.g2D.drawLine((int)shiftPrism[2][0]+context.canvas_w/2, (int)shiftPrism[2][1]+context.canvas_h/2, (int)shiftPrism[6][0]+context.canvas_w/2, (int)shiftPrism[6][1]+context.canvas_h/2);
//			context.g2D.drawLine((int)shiftPrism[3][0]+context.canvas_w/2, (int)shiftPrism[3][1]+context.canvas_h/2, (int)shiftPrism[7][0]+context.canvas_w/2, (int)shiftPrism[7][1]+context.canvas_h/2);
		
			
			myColor = new Color(200,200,0);
			context.g2D.setColor(myColor);
			int xCenter = context.canvas_w/2;
			int yCenter = context.canvas_h/2;
			
			int[] bottomX = {(int)shiftPrism[0][0]+xCenter, (int)shiftPrism[1][0]+xCenter, (int)shiftPrism[2][0]+xCenter, (int)shiftPrism[3][0]+xCenter};
			int[] bottomY = {(int)shiftPrism[0][1]+yCenter, (int)shiftPrism[1][1]+yCenter, (int)shiftPrism[2][1]+yCenter, (int)shiftPrism[3][1]+yCenter};
			
			int[] topX = {(int)shiftPrism[4][0]+xCenter, (int)shiftPrism[5][0]+xCenter, (int)shiftPrism[6][0]+xCenter, (int)shiftPrism[7][0]+xCenter};
			int[] topY = {(int)shiftPrism[4][1]+yCenter, (int)shiftPrism[5][1]+yCenter, (int)shiftPrism[6][1]+yCenter, (int)shiftPrism[7][1]+yCenter};
			
			int[] frontX = {(int)shiftPrism[0][0]+xCenter, (int)shiftPrism[1][0]+xCenter, (int)shiftPrism[5][0]+xCenter, (int)shiftPrism[4][0]+xCenter};
			int[] frontY = {(int)shiftPrism[0][1]+yCenter, (int)shiftPrism[1][1]+yCenter, (int)shiftPrism[5][1]+yCenter, (int)shiftPrism[4][1]+yCenter};
			
			int[] backX = {(int)shiftPrism[2][0]+xCenter, (int)shiftPrism[3][0]+xCenter, (int)shiftPrism[7][0]+xCenter, (int)shiftPrism[6][0]+xCenter};
			int[] backY = {(int)shiftPrism[2][1]+yCenter, (int)shiftPrism[3][1]+yCenter, (int)shiftPrism[7][1]+yCenter, (int)shiftPrism[6][1]+yCenter};
			
			int[] leftX = {(int)shiftPrism[1][0]+xCenter, (int)shiftPrism[2][0]+xCenter, (int)shiftPrism[6][0]+xCenter, (int)shiftPrism[5][0]+xCenter};
			int[] leftY = {(int)shiftPrism[1][1]+yCenter, (int)shiftPrism[2][1]+yCenter, (int)shiftPrism[6][1]+yCenter, (int)shiftPrism[5][1]+yCenter};
			
			int[] rightX = {(int)shiftPrism[0][0]+xCenter, (int)shiftPrism[3][0]+xCenter, (int)shiftPrism[7][0]+xCenter, (int)shiftPrism[4][0]+xCenter};
			int[] rightY = {(int)shiftPrism[0][1]+yCenter, (int)shiftPrism[3][1]+yCenter, (int)shiftPrism[7][1]+yCenter, (int)shiftPrism[4][1]+yCenter};
			
			
			context.g2D.fillPolygon(backX, backY, 4);
			context.g2D.fillPolygon(frontX, frontY, 4);
			context.g2D.fillPolygon(leftX, leftY, 4);
			context.g2D.fillPolygon(rightX, rightY, 4);
			
			myColor = new Color(100,100,0);
			context.g2D.setColor(myColor);
			context.g2D.fillPolygon(bottomX, bottomY, 4);
			context.g2D.fillPolygon(topX, topY, 4);
			
		 }
		
		
		for(int i = 0; i < 8; i++) {
			average = 0;
			for(int j = 0; j < 25; j++) {
				average += scale*(Math.log(context.stereo_l[i*25+j].re))/logNum;
			}
		
			average /= 25;
		
			if(average < 20) {
				average = 20;
			}
		
			yValueLeft[i] = average;
		
		
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
			//System.out.format("Position %d: %f,%f,%f%n", i, shiftPrism[i][0],shiftPrism[i][1],shiftPrism[i][2]);
		}
		
//		float[] result = Objects3D.matrixMultiply(new float[] {100,50,25}, new float[][] {{1,2,3},{4,5,6},{7,8,9}});
//		
//		String test =  String.format("%f,%f,%f", result[0], result[1], result[2]);
//		
//		context.g2D.drawString(test, 50, 50);
		
//		myColor = new Color(255,0,0);
//		context.g2D.setColor(myColor);
//		context.g2D.drawLine((int)shiftPrism[0][0]+context.canvas_w/2, (int)shiftPrism[0][1]+context.canvas_h/2, (int)shiftPrism[1][0]+context.canvas_w/2, (int)shiftPrism[1][1]+context.canvas_h/2);
//		context.g2D.drawLine((int)shiftPrism[1][0]+context.canvas_w/2, (int)shiftPrism[1][1]+context.canvas_h/2, (int)shiftPrism[2][0]+context.canvas_w/2, (int)shiftPrism[2][1]+context.canvas_h/2);
//		context.g2D.drawLine((int)shiftPrism[2][0]+context.canvas_w/2, (int)shiftPrism[2][1]+context.canvas_h/2, (int)shiftPrism[3][0]+context.canvas_w/2, (int)shiftPrism[3][1]+context.canvas_h/2);
//		context.g2D.drawLine((int)shiftPrism[3][0]+context.canvas_w/2, (int)shiftPrism[3][1]+context.canvas_h/2, (int)shiftPrism[0][0]+context.canvas_w/2, (int)shiftPrism[0][1]+context.canvas_h/2);
//		
//		myColor = new Color(0,255,0);
//		context.g2D.setColor(myColor);
//		context.g2D.drawLine((int)shiftPrism[4][0]+context.canvas_w/2, (int)shiftPrism[4][1]+context.canvas_h/2, (int)shiftPrism[5][0]+context.canvas_w/2, (int)shiftPrism[5][1]+context.canvas_h/2);
//		context.g2D.drawLine((int)shiftPrism[5][0]+context.canvas_w/2, (int)shiftPrism[5][1]+context.canvas_h/2, (int)shiftPrism[6][0]+context.canvas_w/2, (int)shiftPrism[6][1]+context.canvas_h/2);
//		context.g2D.drawLine((int)shiftPrism[6][0]+context.canvas_w/2, (int)shiftPrism[6][1]+context.canvas_h/2, (int)shiftPrism[7][0]+context.canvas_w/2, (int)shiftPrism[7][1]+context.canvas_h/2);
//		context.g2D.drawLine((int)shiftPrism[7][0]+context.canvas_w/2, (int)shiftPrism[7][1]+context.canvas_h/2, (int)shiftPrism[4][0]+context.canvas_w/2, (int)shiftPrism[4][1]+context.canvas_h/2);
//		
//		myColor = new Color(0,0,255);
//		context.g2D.setColor(myColor);
//		context.g2D.drawLine((int)shiftPrism[0][0]+context.canvas_w/2, (int)shiftPrism[0][1]+context.canvas_h/2, (int)shiftPrism[4][0]+context.canvas_w/2, (int)shiftPrism[4][1]+context.canvas_h/2);
//		context.g2D.drawLine((int)shiftPrism[1][0]+context.canvas_w/2, (int)shiftPrism[1][1]+context.canvas_h/2, (int)shiftPrism[5][0]+context.canvas_w/2, (int)shiftPrism[5][1]+context.canvas_h/2);
//		context.g2D.drawLine((int)shiftPrism[2][0]+context.canvas_w/2, (int)shiftPrism[2][1]+context.canvas_h/2, (int)shiftPrism[6][0]+context.canvas_w/2, (int)shiftPrism[6][1]+context.canvas_h/2);
//		context.g2D.drawLine((int)shiftPrism[3][0]+context.canvas_w/2, (int)shiftPrism[3][1]+context.canvas_h/2, (int)shiftPrism[7][0]+context.canvas_w/2, (int)shiftPrism[7][1]+context.canvas_h/2);
	
		myColor = new Color(200,200,0);
		context.g2D.setColor(myColor);
		int xCenter = context.canvas_w/2;
		int yCenter = context.canvas_h/2;
		
		int[] bottomX = {(int)shiftPrism[0][0]+xCenter, (int)shiftPrism[1][0]+xCenter, (int)shiftPrism[2][0]+xCenter, (int)shiftPrism[3][0]+xCenter};
		int[] bottomY = {(int)shiftPrism[0][1]+yCenter, (int)shiftPrism[1][1]+yCenter, (int)shiftPrism[2][1]+yCenter, (int)shiftPrism[3][1]+yCenter};
		
		int[] topX = {(int)shiftPrism[4][0]+xCenter, (int)shiftPrism[5][0]+xCenter, (int)shiftPrism[6][0]+xCenter, (int)shiftPrism[7][0]+xCenter};
		int[] topY = {(int)shiftPrism[4][1]+yCenter, (int)shiftPrism[5][1]+yCenter, (int)shiftPrism[6][1]+yCenter, (int)shiftPrism[7][1]+yCenter};
		
		int[] frontX = {(int)shiftPrism[0][0]+xCenter, (int)shiftPrism[1][0]+xCenter, (int)shiftPrism[5][0]+xCenter, (int)shiftPrism[4][0]+xCenter};
		int[] frontY = {(int)shiftPrism[0][1]+yCenter, (int)shiftPrism[1][1]+yCenter, (int)shiftPrism[5][1]+yCenter, (int)shiftPrism[4][1]+yCenter};
		
		int[] backX = {(int)shiftPrism[2][0]+xCenter, (int)shiftPrism[3][0]+xCenter, (int)shiftPrism[7][0]+xCenter, (int)shiftPrism[6][0]+xCenter};
		int[] backY = {(int)shiftPrism[2][1]+yCenter, (int)shiftPrism[3][1]+yCenter, (int)shiftPrism[7][1]+yCenter, (int)shiftPrism[6][1]+yCenter};
		
		int[] leftX = {(int)shiftPrism[1][0]+xCenter, (int)shiftPrism[2][0]+xCenter, (int)shiftPrism[6][0]+xCenter, (int)shiftPrism[5][0]+xCenter};
		int[] leftY = {(int)shiftPrism[1][1]+yCenter, (int)shiftPrism[2][1]+yCenter, (int)shiftPrism[6][1]+yCenter, (int)shiftPrism[5][1]+yCenter};
		
		int[] rightX = {(int)shiftPrism[0][0]+xCenter, (int)shiftPrism[3][0]+xCenter, (int)shiftPrism[7][0]+xCenter, (int)shiftPrism[4][0]+xCenter};
		int[] rightY = {(int)shiftPrism[0][1]+yCenter, (int)shiftPrism[3][1]+yCenter, (int)shiftPrism[7][1]+yCenter, (int)shiftPrism[4][1]+yCenter};
		
		
		context.g2D.fillPolygon(backX, backY, 4);
		context.g2D.fillPolygon(frontX, frontY, 4);
		context.g2D.fillPolygon(leftX, leftY, 4);
		context.g2D.fillPolygon(rightX, rightY, 4);
		
		myColor = new Color(100,100,0);
		context.g2D.setColor(myColor);
		context.g2D.fillPolygon(bottomX, bottomY, 4);
		context.g2D.fillPolygon(topX, topY, 4);
		
	 }
		
		xRotation += xSpeed;
		yRotation += ySpeed;
		zRotation += zSpeed;
		
		if(xRotation> .5 || xRotation < -.5) {
			xSpeed *= -1;
		}
		
		if(yRotation> .5 || yRotation < -.5) {
			ySpeed *= -1;
		}
		
		if(zRotation > .5 || zRotation < -.5) {
			zSpeed *= -1;
		}
		
	}

	
	
	@Override
	public void update(UpdateContext context) {
		// TODO Auto-generated method stub
		
	}
	
	
}
