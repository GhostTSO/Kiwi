package _kiwi.core;

import java.awt.CheckboxMenuItem;
import java.awt.Graphics2D;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.util.List;

import _kiwi.Kiwi;
import _kiwi.core.effect.Effect;
import _kiwi.core.source.Source;
import _kiwi.util.Util;
import _kiwi.util.Util.Hint;

public class Engine implements Renderable, Updateable, Runnable {
	//engine constructor
	protected static final Engine
		INSTANCE = new Engine();
	
	//version string
	public static final String
		WINDOW_TITLE = "" + Kiwi.VERSION;
	
	//window settings
	public static final int
		WINDOW_W = 640,
		WINDOW_H = 480,
		THREAD_FPS = 60,
		THREAD_TPS = 60,
		THREAD_SYNC_MIN = 0,
		THREAD_SYNC_MAX = 2;
	
	//memory space for sound data
	protected final double[]
		stereo_l = new double[Source.SAMPLES],
		stereo_r = new double[Source.SAMPLES],
		mono	 = new double[Source.SAMPLES];	
	
	//initialize and fetch list of effects
	protected List<Effect>
		effects = Effect.getAvailableEffects();
	
	//finds and makes a list of readable audio sources
	protected List<Source>
		sources = Source.getAvailableSources();	
	
	//initialize canvas
	protected final Canvas
		canvas = new Canvas(this);
	
	//initialize window
	protected final Window
		window = new Window(this);	
	
	//volume scaling
	protected double 
		volume = 1f;
	
	//current effect holder
	protected Effect
		effect;
	
	//program thread
	protected Thread
		thread;
	
	//program running state
	protected boolean
		running;
	
	//counters
	protected long
		fps,
		tps;
	
	/**
	 * Constructor is private to guarantee only one {@link Engine#INSTANCE instance}
	 */
	private Engine() { }	
	
	/**
	 * Static method to start thread
	 */
	public static void init() {
		INSTANCE.thread = new Thread(INSTANCE);
		INSTANCE.running = true;
		INSTANCE.thread.start();
	}
	
	/**
	 * Static method to stop thread
	 */
	public static void exit() {
		INSTANCE.running = false;
	}
	
	/**
	 * Called at the thread start
	 */
	public void onInit() {
		window.onInit();
		canvas.onInit();
		if(effect != null)
			effect.onInit();
	}
	
	/**
	 * Called at thread stop
	 */
	public void onExit() {
		if(effect != null)
			effect.onExit();
		canvas.onExit();
		window.onExit();
	}
	
	/**
	 * Create a {@link _kiwi.core.Renderable.RenderContext RenderContext} and render
	 * @param t elapsed time in seconds
	 * @param dt  delta time in seconds
	 */
	private void render(double t, double dt) {
		canvas.render(this, t, dt);
	}
	
	/**
	 * Create a {@link _kiwi.core.Updateable.UpdateContext UpdateContext} and update
	 * @param t elapsed time in seconds
	 * @param dt  delta time in seconds
	 */
	private void update(double t, double dt) {
		canvas.update(this, t, dt);
	}
	
	@Override
	/**
	 * Render to the canvas the appropriate effect{@link _kiwi.core.effect.Effect effect}
	 */
	public void render(RenderContext context) {
		if(effect != null)
			effect.render(context);
	}
	
	@Override
	/**
	 * Update sounds information into the buffers{@link _kiwi.core.effect.Effect effect}
	 */
	public void update(UpdateContext context) {
		//poll input
		Input.poll();
		//check source to rebuild channels
		boolean push = false;
		for(Source source: sources) {
			source.update(context);
			if(source.isPush())
				push = true;
		}
		//if channels need to be rebuilt, rebuild them
		if(push) {
			//zero out channels
			for(int i = 0; i < Source.SAMPLES; i ++) {
				stereo_l[i] = 0.0;
				stereo_r[i] = 0.0;
			}
			//fill channels
			int n = 0;
			for(Source source: sources)
				if(source.isOpen()) {
					for(int i = 0; i < Source.SAMPLES; i ++) {
						stereo_l[i] += source.stereo_l[i];
						stereo_r[i] += source.stereo_r[i];
					}
					n ++;
				}
			for(int i = 0; i < Source.SAMPLES; i ++) {
				stereo_l[i] = stereo_l[i] * volume / n;
				stereo_r[i] = stereo_r[i] * volume / n;
				mono[i]     = (long)(stereo_l[i] + stereo_r[i]) / 2;
			}
			//apply fourier transform
			Util.fft(stereo_l);
			Util.fft(stereo_r);
			Util.fft(mono);
		}
		//update effect
		if(effect != null)
			effect.update(context);
	}
	
	public void onMouseMoved(Point mouse) {
		if(effect != null) effect.onMouseMoved(mouse);
	}
	
	public void onWheelMoved(float wheel) {
		if(effect != null) effect.onWheelMoved(wheel);
	}
	
	public void onKeyDnAction(int key) {
		if(effect != null) effect.onKeyDnAction(key);
	}
	
	public void onKeyUpAction(int key) {
		if(effect != null) effect.onKeyUpAction(key);
	}
	
	public void onBtnDnAction(Point mouse, int btn) {
		if(effect != null) effect.onBtnDnAction(mouse, btn);
	}
	
	public void onBtnUpAction(Point mouse, int btn) {
		if(effect != null) effect.onBtnUpAction(mouse, btn);
	}	
	
	//time sizes
	private static final long
		ONE_MILLIS = 1000000   ,
		ONE_SECOND = 1000000000;

	@Override
	/**
	 * Application loop
	 */
	public void run() {
		try {
			//init
			onInit();
			long
				f_time = THREAD_FPS > 0 ? ONE_SECOND / THREAD_FPS : 0, 	// time per render in nanoseconds
				t_time = THREAD_TPS > 0 ? ONE_SECOND / THREAD_TPS : 0, 	// time per update in nanoseconds
				f_elapsed = 0, 											//default value
				t_elapsed = 0, 											//default value
				elapsed = 0, 											//default value
				f_ct = 0, 												//default value
				t_ct = 0, 												//default value
				t = System.nanoTime();									//current system time in nanoseconds
				
				//while running boolean is true
				while(running) {
					//calculates change in time and increments tick and frame counter
					long dt = - t + (t = System.nanoTime());
					f_elapsed += dt;
					t_elapsed += dt;
					elapsed += dt;
					//if time to update, then update
					if(t_elapsed >= t_time) {
						update((double)t / ONE_SECOND, (double)t_elapsed / ONE_SECOND);
						t_elapsed = 0;
						t_ct ++;
					}
					//if time to render, then render
					if(f_elapsed >= f_time) {
						render((double)t / ONE_SECOND, (double)f_elapsed / ONE_SECOND);
						f_elapsed = 0;
						f_ct ++;
					}	
					//if elapsed time > 1 second, cache and reset counters
					if(elapsed >= ONE_SECOND) {
						Debug.out.log("FPS: " + f_ct);
						Debug.out.log("TPS: " + t_ct);
						elapsed = 0;
						fps = f_ct;
						tps = t_ct;
						f_ct = 0;
						t_ct = 0;
					}
					//how much time to next cycle
					long sync = Math.min(
							t_time - t_elapsed,
							f_time - f_elapsed
							) / ONE_MILLIS - 1;
					//sleep until next cycle
					if(sync < THREAD_SYNC_MIN) sync = THREAD_SYNC_MIN;
					if(sync > THREAD_SYNC_MAX) sync = THREAD_SYNC_MAX;
					if(sync > 0) Thread.sleep(sync);
				}
		} catch(Exception ex) {
			ex.printStackTrace();
		} finally {
			//exit
			onExit();
		}
	}
	
	public static class Canvas {
		
		//initialize drawing canvas
		protected final java.awt.Canvas
			component = new java.awt.Canvas();
		
		//initialize render context
		protected RenderContext
			render_context = new RenderContext();
		
		//initialize update context
		protected UpdateContext
			update_context = new UpdateContext();
		
		//stored engine variable
		protected Engine
			parent;
		
		/**canvas constructor**/
		public Canvas(Engine parent) {
			this.parent = parent;
			this.render_context.stereo_l = parent.stereo_l;
			this.render_context.stereo_r = parent.stereo_r;
			this.render_context.mono     = parent.mono;
			this.update_context.stereo_l = parent.stereo_l;
			this.update_context.stereo_r = parent.stereo_r;
			this.update_context.mono     = parent.mono;
			
			this.component.setFocusable(true);
			this.component.setFocusTraversalKeysEnabled(false);
			
			this.component.addKeyListener(Input.INSTANCE);
			this.component.addMouseListener(Input.INSTANCE);
			this.component.addMouseWheelListener(Input.INSTANCE);
			this.component.addMouseMotionListener(Input.INSTANCE);
		}
		
		/**initialize method**/
		public void onInit() { }
		
		/**close method**/
		public void onExit() { }
		
		//buffered storage variable
		private BufferStrategy
			b;
		
		private void render(Renderable renderable, double t, double dt) {
			if(b == null || b.contentsLost()) {
				component.createBufferStrategy(2);
				b = component.getBufferStrategy();
			}
			
			render_context.g2D = (Graphics2D)b.getDrawGraphics();			
			render_context.canvas_w = component.getWidth() ;
			render_context.canvas_h = component.getHeight();
			render_context.t  = t ;
			render_context.dt = dt;
			
			renderable.render(render_context);
			
			render_context.g2D.dispose();
			b.show();
		}
		
		private void update(Updateable updateable, double t, double dt) {
			update_context.canvas_w = component.getWidth() ;
			update_context.canvas_h = component.getHeight();
			update_context.t  = t ;
			update_context.dt = dt;
			
			updateable.update(update_context);
		}
	}
	
	public static class Window {
		protected java.awt.Frame
			component = new java.awt.Frame();
		protected java.awt.MenuBar
			mb = new MenuBar();
		protected java.awt.Menu
			m1 = new Menu("Effect"),
			m2 = new Menu("Source"),
			m3 = new Menu("Volume"),
			m4 = new Menu("Scaling");
		protected Engine
			parent;
		
		public Window(Engine parent) {
			this.parent = parent;
			mb.add(m1);
			mb.add(m2);
			mb.add(m3);
			mb.add(m4);
			component.setMenuBar(mb);
			component.add(parent.canvas.component);
			component.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent we) {
					Engine.exit();
				}
			});
			
		}
		
		public void onInit() {
			for(Effect effect: parent.effects) {
				MenuItem mi = new MenuItem(effect.name);
				mi.addActionListener((ae) -> {
					if(parent.effect != null)
						parent.effect.onDetach();
					parent.effect = effect;
					if(parent.effect != null)
						parent.effect.onAttach();
				});
				m1.add(mi);
			}
			for(Source source: parent.sources) {
				CheckboxMenuItem cbmi = new CheckboxMenuItem(source.name, source.isOpen());				
				cbmi.addItemListener((ie) -> {
					if(cbmi.getState())
						source.open();
					else
						source.stop();
				});
				m2.add(cbmi);
			}
			int di = 25;
			for(int i = 25; i <= 500; i += di) {
				MenuItem mi = new MenuItem(i + "%");
				double volume = i / 100f;
				mi.addActionListener((ae) -> {
					parent.volume = volume;
				});
				if(i >= 100)
					di = 50;
				if(i >= 200)
					di = 100;
				m3.add(mi);
			}
			
			MenuItem
				m41 = new MenuItem("Lin"),
				m42 = new MenuItem("Log"),
				m43 = new MenuItem("Tanh");
			m41.addActionListener((ae) -> {
				parent.canvas.render_context.hint = Hint.LIN;
				parent.canvas.update_context.hint = Hint.LIN;
			});
			m42.addActionListener((ae) -> {
				parent.canvas.render_context.hint = Hint.LOG;
				parent.canvas.update_context.hint = Hint.LOG;		
			});
			m43.addActionListener((ae) -> {
				parent.canvas.render_context.hint = Hint.TANH;
				parent.canvas.update_context.hint = Hint.TANH;
			});
			
			m4.add(m41);
			m4.add(m42);
			m4.add(m43);
			
			component.setTitle(WINDOW_TITLE);
			component.setSize(
					WINDOW_W,
					WINDOW_H
					);
			component.setLocationRelativeTo(null);
			component.setVisible(true);
		}
		
		public void onExit() {
			component.dispose();
		}
	}
}
