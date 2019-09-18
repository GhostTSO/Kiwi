package _kiwi.core.effect.effects;

import java.awt.BasicStroke;
import java.awt.Color;

import _kiwi.core.effect.Effect;
import kiwi.math.Objects3D;

public class MountainValleys extends Effect{

	public MountainValleys() {
		super("Mountains & Valleys");
		fillColors();
	}

	public float[][] points = new float[512][3];
	public Color[] colors = new Color[512];

	public float divider = 2.1f;

	public float xRotation = (float) -1.508;
	public float yRotation = 0.0f;
	public float zRotation = 0.0f;
	public float[][] projection = 	{{1.0f, 0.0f, 0.0f},
									{0.0f,1.0f,0.0f},
									{0.0f,0.0f,1.0f}};

	float xMoveSpeed = 0.0001f;

	public float[][] xMatrix = { 	{1.0f, 				0.0f,					0.0f					},
			{0.0f, (float)Math.cos(xRotation), (float)-Math.sin(xRotation)	},
			{0.0f, (float)Math.sin(xRotation), (float)Math.cos(xRotation)}	};

	public float[][] yMatrix = { 	{(float)Math.cos(yRotation), 0, (float)-Math.sin(yRotation)	},
			{0.0f,                      	 1.0f,                       0.0f		},
			{(float)Math.sin(yRotation), 0.0f, (float)Math.cos(yRotation)}	};

	public float[][] zMatrix = { 	{(float)Math.cos(zRotation), (float)-Math.sin(zRotation), 0.0f	},
			{(float)Math.sin(zRotation), (float)Math.cos(zRotation),  0.0f	},
			{				0.0f,                     0.0f,                 1.0f}};



	public void render(RenderContext context) {

		Color myColor = new Color(0,0,0);
		context.g2D.setColor(myColor);
		context.g2D.fillRect(0, 0, context.canvas_w, context.canvas_h);
		context.g2D.setStroke(new BasicStroke(3));

		
	

//		myColor = new Color(255,255,255);
	//	context.g2D.setColor(myColor);

		for(int i = 0; i < 15; i++) {
			for(int j = 0; j < 31; j++) {

				myColor = new Color( i*j/2, i*15, 210-(i*j/2));
				context.g2D.setColor(myColor);
				//				context.g2D.drawLine((int) points[32*i+j][0], (int) points[32*i+j][1], (int) points[32*i+j+1][0],(int) points[32*i+j+1][1]);
				//				context.g2D.drawLine((int) points[32*i+j][0], (int) points[32*i+j][1], (int) points[32*i+j+32][0],(int) points[32*i+j+32][1]);
				//				context.g2D.drawLine((int) points[32*i+j][0], (int) points[32*i+j][1], (int) points[32*i+j+33][0],(int) points[32*i+j+33][1]);
				//				context.g2D.drawLine((int) points[15*i+j][0], (int) points[15*i+j][1], (int) points[15*i+j+32][0],(int) points[15*i+j+32][1]);
				//				context.g2D.drawLine((int) points[15*i+j][0], (int) points[15*i+j][1], (int) points[15*i+j+33][0],(int) points[15*i+j+33][1]);


				context.g2D.fillPolygon( 	new int[] {(int) points[32*i+j][0], (int) points[32*i+j+1][0], (int) points[32*i+j+32][0]},
						new int[] {(int) points[32*i+j][1]  + 3*context.canvas_h/4, (int) points[32*i+j+1][1]  + 3*context.canvas_h/4, (int) points[32*i+j+32][1]  + 3*context.canvas_h/4}, 3);
				context.g2D.fillPolygon( 	new int[] {(int) points[32*i+j+1][0], (int) points[32*i+j+32][0], (int) points[32*i+j+33][0]},
						new int[] {(int) points[32*i+j+1][1] + 3*context.canvas_h/4, (int) points[32*i+j+32][1]  + 3*context.canvas_h/4, (int) points[32*i+j+33][1]  + 3*context.canvas_h/4}, 3);
			}
		}




	}

	public void update(UpdateContext context) {
		for(int i = 0; i < 32; i++) {
			for(int j = 0; j < 16; j++) {
				points[j*32+i][0] = (i*context.canvas_w/31);
				points[j*32+i][1] = (j*context.canvas_h/5 + context.canvas_h);

				if(i < 16) {
					
					int value = (int) (100*Math.log(context.stereo_l[i*32+j]));
					
					if(context.stereo_l[i*32+j] > 1) {
						points[j*32+i][2] = value;
					}else {
						points[j*32+i][2] = 0;
					}
				}else {
					
					int value = (int) (100*Math.log(context.stereo_r[i*32+j]));
					
					if(context.stereo_r[i*32+j] > 1) {
						points[j*32+i][2] = value;
					}else {
						points[j*32+i][2] = 0;
					}
				}

				points[j*32+i] = Objects3D.matrixMultiply(points[j*32+i], xMatrix);
				points[j*32+i] = Objects3D.matrixMultiply(points[j*32+i], yMatrix);
				points[j*32+i] = Objects3D.matrixMultiply(points[j*32+i], zMatrix);
				points[j*32+i] = Objects3D.matrixMultiply(points[j*32+i], projection);
			}

		}
	}


	public void fillColors() {
		Color myColor = new Color(0,0,0);
		for(int i = 0; i< 512; i++) {
			myColor = new Color((int) (Math.random()*255),(int) (Math.random()*255),(int) (Math.random()*255));
			colors[i] = myColor;
		}
	}


}
