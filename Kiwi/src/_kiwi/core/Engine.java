package _kiwi.core;

import java.awt.CheckboxMenuItem;
import java.awt.Graphics2D;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.util.List;

import _kiwi.Kiwi;
import _kiwi.core.effect.Effect;
import _kiwi.core.source.Source;
import _kiwi.util.Util;

public class Engine implements Renderable, Updateable, Runnable {
	public static final Engine
		INSTANCE = new Engine();
	public static final String
		WINDOW_TITLE = "" + Kiwi.VERSION;
	public static final int
		WINDOW_W = 640,
		WINDOW_H = 480,
		THREAD_FPS = 60,
		THREAD_TPS = 60,
		THREAD_SYNC = 0;
	
	protected final double[]
		stereo_l = new double[Source.SAMPLES],
		stereo_r = new double[Source.SAMPLES],
		mono	 = new double[Source.SAMPLES];	
	
	protected List<Effect>
		effects = Effect.getAvailableEffects();
	protected List<Source>
		sources = Source.getAvailableSources();	
	protected final Canvas
		canvas = new Canvas(this);
	protected final Window
		window = new Window(this);	
	protected double 
		volume = 1f;
	protected Effect
		effect;
	
	protected Thread
		thread;
	protected boolean
		running;
	
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
	}
	
	/**
	 * Called at thread stop
	 */
	public void onExit() {
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
	 * Render {@link _kiwi.core.effect.Effect effect}
	 */
	public void render(RenderContext context) {
		if(effect != null)
			effect.render(context);
	}
	
	@Override
	/**
	 * Update {@link _kiwi.core.effect.Effect effect}
	 */
	public void update(UpdateContext context) {
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
				f_time = THREAD_FPS > 0 ? ONE_SECOND / THREAD_FPS : 0, // time per render in nanoseconds
				t_time = THREAD_TPS > 0 ? ONE_SECOND / THREAD_TPS : 0, // time per update in nanoseconds
				f_elapsed = 0,
				t_elapsed = 0,
				elapsed = 0,
				f_ct = 0,
				t_ct = 0,
				t = System.nanoTime();
				while(running) {
					long dt = - t + (t = System.nanoTime());
					f_elapsed += dt;
					t_elapsed += dt;
					elapsed += dt;
					//if time to update, then update
					if(t_elapsed >= t_time) {
						update((double)t / ONE_SECOND, (double)t_elapsed / ONE_SECOND);
						t_elapsed -= t_time;
						t_ct ++;
					}
					//if time to render, then render
					if(f_elapsed >= f_time) {
						render((double)t / ONE_SECOND, (double)f_elapsed / ONE_SECOND);
						f_elapsed -= f_time;
						f_ct ++;
					}	
					//if elapsed time > 1 second, cache and reset counters
					if(elapsed >= ONE_SECOND) {
						System.out.println("FPS: " + f_ct);
						System.out.println("TPS: " + t_ct);
						elapsed -= ONE_SECOND;
						fps = f_ct;
						tps = t_ct;
						f_ct = 0;
						t_ct = 0;
					}
					//how much time to next cycle
					long sync = Math.min(
							t_time - t_elapsed,
							f_time - f_elapsed
							) / ONE_MILLIS;
					//sleep until next cycle
					if(sync > THREAD_SYNC)
						Thread.sleep(sync);
				}
		} catch(Exception ex) {
			ex.printStackTrace();
		} finally {
			//exit
			onExit();
		}
	}
	
	public static class Canvas {
		protected final java.awt.Canvas
			component = new java.awt.Canvas();
		protected RenderContext
			render_context = new RenderContext();
		protected UpdateContext
			update_context = new UpdateContext();
		
		protected Engine
			parent;
		
		public Canvas(Engine parent) {
			this.parent = parent;
			this.render_context.stereo_l = parent.stereo_l;
			this.render_context.stereo_r = parent.stereo_r;
			this.render_context.mono     = parent.mono;
			this.update_context.stereo_l = parent.stereo_l;
			this.update_context.stereo_r = parent.stereo_r;
			this.update_context.mono     = parent.mono;
		}
		
		public void onInit() { }
		
		public void onExit() { }
		
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
			m3 = new Menu("Volume");
		protected Engine
			parent;
		
		public Window(Engine parent) {
			this.parent = parent;
			mb.add(m1);
			mb.add(m2);
			mb.add(m3);
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
					parent.effect = effect;
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
