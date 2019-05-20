package kiwi;

import java.awt.Canvas;
import java.awt.Frame;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import kiwi.util.Util;


public class Kiwi {
	public static final Version
		VERSION = new Version("Kiwi", 0, 0, 1);

	public static boolean
		FULLSCREEN = false;
	public static int
		WINDOW_W = 640,
		WINDOW_H = 480;
	public static int
		FPS = 60;	
	
	private static boolean
		init,
		loop;
	
	private static Frame
		window;
	private static Canvas
		canvas;
	
	
	public static void main(String[] args) {
		System.out.println(Kiwi.VERSION);
		Kiwi.init();
		Kiwi.loop();
		Kiwi.exit();
	}
	
	public static final void init() {
		if(!init) {
			window = new  Frame();
			canvas = new Canvas();
			
			window.add(canvas);
			
			int
				window_w = WINDOW_W,
				window_h = WINDOW_H;
			
			if(FULLSCREEN) {
				window_w = Util.PRIMARY_DISPLAY_W;
				window_h = Util.PRIMARY_DISPLAY_H;
				window.setUndecorated(true);
			}
			
			window.setSize(
					window_w,
					window_h
					);
			window.setLocationRelativeTo(null);
			window.setVisible(true);
			

			window.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent we) {
					Kiwi.stop();
				}
			});
			canvas.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent ke) {
					if(ke.getKeyCode() == KeyEvent.VK_ESCAPE)
						Kiwi.stop();
				}
			});
			
			init = true;
		}
	}
	
	private static final long
		ONE_SECOND = 1000000000L;
	public static final void loop() {
		if(!loop) {
			loop = true;
			try {
				long
					f_time = FPS > 0 ? ONE_SECOND / FPS : 0,
					t_time = ONE_SECOND,
					f_elapsed = 0,
					t_elapsed = 0,
					fps = 0,
					t1 = System.nanoTime();
				while(loop) {
					long
						t2 = System.nanoTime(),
						dt = t2 - t1;
					f_elapsed += dt;
					t_elapsed += dt;					
					if(f_elapsed >= f_time) {
						f_elapsed = 0;
						update();
						render();
						fps ++;
					}					
					if(t_elapsed >= t_time) {
						System.out.println(fps);
						t_elapsed = 0;
						fps = 0;
					}
					t1 = t2;
				}
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public static final void stop() {
		System.out.println("Stop");
		if( loop) {
			loop = false;
		}
	}
	
	public static final void exit() {
		if( init) {
			window.dispose();
		}
	}
	
	public static final void render() {
		
	}
	
	public static final void update() {
		
	}	
	
	public static class Version {
		public final String
			VERSION_NAME;
		public final int
			VERSION_ID,
			RELEASE_ID,
			PATCH_ID;
		
		public Version(
				String version_name,
				int version_id,
				int release_id,
				int patch_id
				) {
			this.VERSION_NAME = version_name;
			this.VERSION_ID = version_id;
			this.RELEASE_ID = release_id;
			this.PATCH_ID = patch_id;
		}
		
		@Override
		public String toString() {
			return
					this.VERSION_NAME + " " +
					this.VERSION_ID + "." +
					this.RELEASE_ID + "." +
					this.PATCH_ID;
		}
	}
}
