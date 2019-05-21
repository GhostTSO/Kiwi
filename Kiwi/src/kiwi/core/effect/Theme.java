package kiwi.core.effect;

import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics2D;

import kiwi.math.Complex;

public class Theme {

	public static final void standardView (Canvas canvas, Graphics2D g2D, Complex[] l_channel, Complex[] r_channel, int s) {
		g2D.setColor(Color.BLACK);
		g2D.setStroke(new BasicStroke(2));
		g2D.fillRect(
				0,
				0,
				canvas.getWidth(),
				canvas.getHeight()
				);
		
			
		for(int i = 0; i < l_channel.length; i ++) {
			g2D.setColor(Color.WHITE);	
			g2D.drawLine(
					i,
					(int)(canvas.getHeight()/4+(l_channel[i].re)),
					i,
					(int)(canvas.getHeight()/4-(l_channel[i].re))
					);		
			
			g2D.setColor(Color.WHITE);
			g2D.drawLine(
					i,
					(int)(3*canvas.getHeight()/4+(r_channel[i].re)),
					i,
					(int)(3*canvas.getHeight()/4-(r_channel[i].re))
					);	
			
		}
		
	}


	public static final void smoothView (Canvas canvas, Graphics2D g2D, Complex[] l_channel, Complex[] r_channel, Complex[] last_l_channel,  Complex[] last_r_channel, int s) {
		g2D.setColor(Color.BLACK);
		g2D.setStroke(new BasicStroke(2));
		g2D.fillRect(
				0,
				0,
				canvas.getWidth(),
				canvas.getHeight()
				);
		
			
		for(int i = 0; i < l_channel.length; i ++) {
			g2D.setColor(Color.WHITE);	
			g2D.drawLine(
					i,
					(int)(canvas.getHeight()/4+((l_channel[i].re + last_l_channel[i].re)/100)),
					i,
					(int)(canvas.getHeight()/4-(l_channel[i].re + last_l_channel[i].re)/100)
					);	
			
			g2D.setColor(Color.WHITE);
			g2D.drawLine(
					i,
					(int)(3*canvas.getHeight()/4+(l_channel[i].re + last_l_channel[i].re)/100),
					i,
					(int)(3*canvas.getHeight()/4-(l_channel[i].re + last_l_channel[i].re)/100)
					);	
			
		}
		
	}
	
}
