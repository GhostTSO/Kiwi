package kiwi;

import java.awt.Canvas;
import java.awt.CheckboxMenuItem;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.util.List;

import kiwi.core.Media;
import kiwi.core.Style;
import kiwi.core.render.RenderContext;
import kiwi.core.update.UpdateContext;
import kiwi.math.Complex;

public class Kiwi {
	public static final int
		PRIMARY_DISPLAY_W = Toolkit.getDefaultToolkit().getScreenSize().width,
		PRIMARY_DISPLAY_H = Toolkit.getDefaultToolkit().getScreenSize().height;
	public static final Version
		VERSION = new Version("Kiwi", 0, 0, 5);

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
	private static MenuBar
		menubar;
	
	private static Style
		style;
	private static List<Style>
		styles;
	private static List<Media>
		medias;
	
	
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

			menubar = new MenuBar();			
			
			styles = Style.getAvailableStyles();
			medias = Media.getAvailableMedias();
			
			Menu
				m1 = new Menu("Style"),
				m2 = new Menu("Media");
			
			for(Style style: Kiwi.styles) {
				MenuItem mi = new MenuItem(style.name);
				mi.addActionListener((ae) -> {
					Kiwi.style = style;
				});
				m1.add(mi);
			}
			
			for(Media media: Kiwi.medias) { 
				CheckboxMenuItem mi = new CheckboxMenuItem(media.name, false);
				mi.addItemListener((ie) -> {
					media.enable(mi.getState());
				});
				m2.add(mi);
			};
			
			menubar.add(m1);
			menubar.add(m2);
			
			window.setMenuBar(menubar);	
			
			l_channel = new Complex[samples];
			r_channel = new Complex[samples];
			_l_channel = new Complex[samples];
			_r_channel = new Complex[samples];
			
			for(int i = 0; i < samples; i ++) {
				l_channel[i] = new Complex();
				r_channel[i] = new Complex();
				_l_channel[i] = new Complex();
				_r_channel[i] = new Complex();
			}
			
			int
				window_w = WINDOW_W,
				window_h = WINDOW_H;
			
			if(FULLSCREEN) {
				window_w = PRIMARY_DISPLAY_W;
				window_h = PRIMARY_DISPLAY_H;
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
		if( loop) {
			loop = false;
		}
	}
	
	public static final void exit() {
		if( init) {
			window.dispose();
		}
	}	

	
	private static int
		samples = 512;
	private static Complex[]
		l_channel,
		r_channel,
		_l_channel,
		_r_channel;
	
	private static final UpdateContext
		update_context = new UpdateContext();
	public static final void update() {
		if(style != null) {
			int n = 0;
			for(int i = 0; i < samples; i ++) {
				l_channel[i].re = 0; l_channel[i].im = 0;
				r_channel[i].re = 0; r_channel[i].im = 0;
			}
			for(Media media: Kiwi.medias) {
				if(media.line.isOpen()) {
					media.poll(
							_l_channel,
							_r_channel
							);
					for(int i = 0; i < samples; i ++) {
						l_channel[i] = l_channel[i].add(_l_channel[i]);
						r_channel[i] = r_channel[i].add(_r_channel[i]);
					}
					n ++;					
				}
			}
			for(int i = 0; i < samples; i ++) {
				l_channel[i].re /= n; l_channel[i].im /= n;
				r_channel[i].re /= n; r_channel[i].im /= n;
			}
			Complex.fft(l_channel);
			Complex.fft(r_channel);
			
			update_context.samples = samples;
			update_context.l_channel = l_channel;
			update_context.r_channel = r_channel;
			update_context.canvas_w = canvas.getWidth();
			update_context.canvas_h = canvas.getHeight();
			
			style.update(update_context);
		}
	}
	

	private static BufferStrategy
		buffer_strategy;
	private static final RenderContext
		render_context = new RenderContext();
	public static final void render() {
		if(style != null) {
			if(buffer_strategy == null || buffer_strategy.contentsLost()) {
				canvas.createBufferStrategy(2);
				buffer_strategy = canvas.getBufferStrategy();
			}
			Graphics2D g2D = (Graphics2D)buffer_strategy.getDrawGraphics();
			
			render_context.g2D = g2D;			
			render_context.samples = samples;
			render_context.l_channel = l_channel;
			render_context.r_channel = r_channel;
			render_context.canvas_w = canvas.getWidth();
			render_context.canvas_h = canvas.getHeight();		
			
			style.render(render_context);			
			
			g2D.dispose();
			buffer_strategy.show();
		}
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
