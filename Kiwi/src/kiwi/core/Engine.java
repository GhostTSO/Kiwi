package kiwi.core;

import java.awt.CheckboxMenuItem;
import java.awt.Graphics2D;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.util.List;

import kiwi.Kiwi;
import kiwi.core.Renderable.RenderContext;
import kiwi.core.Updateable.UpdateContext;
import kiwi.core.effect.Effect;
import kiwi.core.source.Source;
import kiwi.math.Complex;
import kiwi.util.Util;

public class Engine {
	public boolean
		debug = false;
	public int
		window_w = 640,
		window_h = 480;
	public String
		window_title = "" + Kiwi.VERSION;
	public boolean
		window_fullscreen;
	public int
		effect_fps = 60,
		effect_tps = 60,
		source_poll_rate = 60;

	protected Canvas
		canvas = new Canvas(this);
	protected Window
		window = new Window(this);
	
	protected Thread
		effect_thread,
		source_thread;	
	protected float
		volume = 1f;
	protected long
		fps,
		tps;

	protected List<Source>
		sources;
	protected List<Effect>
		effects;	
	protected Effect
		effect;		
	
	protected final Complex[]
		stereo_l_buffer1 = new Complex[Source.SAMPLES],
		stereo_r_buffer1 = new Complex[Source.SAMPLES],
		mono_buffer1 	 = new Complex[Source.SAMPLES],
		stereo_l_buffer2 = new Complex[Source.SAMPLES],
		stereo_r_buffer2 = new Complex[Source.SAMPLES],
		mono_buffer2 	 = new Complex[Source.SAMPLES],
		stereo_l 		 = new Complex[Source.SAMPLES],
		stereo_r 		 = new Complex[Source.SAMPLES],
		mono 			 = new Complex[Source.SAMPLES];
	
	protected final RenderContext
		render_context = new RenderContext(
				this.stereo_l,
				this.stereo_r,
				this.mono
				);
	protected final UpdateContext
		update_context = new UpdateContext(
				this.stereo_l,
				this.stereo_r,
				this.mono
				);
	protected BufferStrategy
		buffer_strategy;
	
	public Engine() {
		for(int i = 0; i < Source.SAMPLES; i ++) {
			stereo_l_buffer1[i] = new Complex();
			stereo_r_buffer1[i] = new Complex();
			mono_buffer1[i] 	= new Complex();
			stereo_l_buffer2[i] = new Complex();
			stereo_r_buffer2[i] = new Complex();
			mono_buffer2[i] 	= new Complex();
			stereo_l[i] 		= new Complex();
			stereo_r[i] 		= new Complex();
			mono[i] 			= new Complex();
		}
	}
	
	public void init() {		
		this.effect_thread = new Thread(this.run_effect);
		this.source_thread = new Thread(this.run_source);
		this.effect_thread.start();	
		this.source_thread.start();
	}
	
	public void exit() {		
		this.effect_thread.interrupt();
		this.source_thread.interrupt();
	}		
	
	public void onInit() {		
		this.effects = Effect.getAvailableEffects();
		this.sources = Source.getAvailableSources();		
		this.window.onInit();
		this.canvas.onInit();			
	}
	
	private final void onExit() {
		this.canvas.onExit();
		this.window.onExit();
	}
	
	protected final long
		one_second = 1000000000L;	
	protected final Runnable 
		run_effect = () -> {
			try {
				this.onInit();
				long
					f_time = effect_fps > 0 ? one_second / effect_fps : 0,
					t_time = effect_tps > 0 ? one_second / effect_tps : 0,
					f_elapsed = 0,
					t_elapsed = 0,
					elapsed = 0,
					f_ct = 0,
					t_ct = 0,
					t = System.nanoTime();
				while(!Thread.interrupted()) {
					long dt = - t + (t = System.nanoTime());
					f_elapsed += dt;
					t_elapsed += dt;
					elapsed += dt;
					if(t_elapsed >= t_time) {
						this.update((float)t_elapsed / one_second);
						t_elapsed = 0;
						t_ct ++;
					}
					if(f_elapsed >= f_time) {
						this.render((float)f_elapsed / one_second);
						f_elapsed = 0;
						f_ct ++;
					}
					if(elapsed >= one_second) {
						this.fps = f_ct;
						this.tps = t_ct;
						elapsed = 0;
						f_ct = 0;
						t_ct = 0;
					}
				}
			} catch(Exception e) {
				e.printStackTrace();
			} finally {
				this.onExit();
			}
		},
		run_source = () -> {
			try {
				long
					poll_time = source_poll_rate > 0 ? one_second / source_poll_rate : 0,
					elapsed = 0,
					t = System.nanoTime();
					while(!Thread.interrupted()) {
						elapsed -= t - (t = System.nanoTime());
						if(elapsed >= poll_time) {
							this.poll();
							elapsed = 0;
						}
					}
			} catch(Exception e) {
				e.printStackTrace();
			}
		};
		
	public void update(float dt) {	
		if(this.effect != null) {
			for(int i = 0; i < Source.SAMPLES; i ++) {
				this.stereo_l[i].set(this.stereo_l_buffer2[i]);
				this.stereo_r[i].set(this.stereo_r_buffer2[i]);
				this.mono[i].set(this.mono_buffer2[i]);
			}
			this.update_context.dt = dt;
			this.update_context.canvas_w = this.canvas.component.getWidth() ;
			this.update_context.canvas_h = this.canvas.component.getHeight();
			
			this.effect.update(this.update_context);
		}
	}
	
	public void render(float dt) {
		if(this.effect != null) {
			if(this.buffer_strategy == null || this.buffer_strategy.contentsLost()) {
				this.canvas.component.createBufferStrategy(2);
				this.buffer_strategy = this.canvas.component.getBufferStrategy();
			}
			
			this.render_context.g2D = (Graphics2D)this.buffer_strategy.getDrawGraphics();
			
			this.render_context.dt = dt;
			this.render_context.canvas_w = this.canvas.component.getWidth() ;
			this.render_context.canvas_h = this.canvas.component.getHeight();
			
			this.effect.render(this.render_context);
			
			this.render_context.g2D.dispose();
			this.buffer_strategy.show();
		}
	}	
	
	public void poll() {
		if(this.sources != null) {
			for(int i = 0; i < Source.SAMPLES; i ++) {
				this.stereo_l_buffer1[i].set(0f, 0f);
				this.stereo_r_buffer1[i].set(0f, 0f);
				this.mono_buffer1[i].set(0f, 0f);
			}
			int n = 0;
			for(Source source: this.sources) {
				if(source.line.isOpen()) {
					source.poll();
					for(int i = 0; i < Source.SAMPLES; i ++) {
						this.stereo_l_buffer1[i].re += source.stereo_l[i].re;
						this.stereo_l_buffer1[i].im += source.stereo_l[i].im;
						this.stereo_r_buffer1[i].re += source.stereo_r[i].re;
						this.stereo_r_buffer1[i].im += source.stereo_r[i].im;
					}
					n ++;
				}
			}
			for(int i = 0; i < Source.SAMPLES; i ++) {
				this.stereo_l_buffer1[i].re /= n;
				this.stereo_l_buffer1[i].im /= n;
				this.stereo_r_buffer1[i].re /= n;
				this.stereo_r_buffer1[i].im /= n;
				this.mono_buffer1[i].re = (this.stereo_l_buffer1[i].re + this.stereo_r_buffer1[i].re) / 2;
				this.mono_buffer1[i].im = (this.stereo_l_buffer1[i].im + this.stereo_r_buffer1[i].im) / 2;
			}
			Util.fft(this.stereo_l_buffer1);
			Util.fft(this.stereo_r_buffer1);
			Util.fft(this.mono_buffer1);
			for(int i = 0; i < Source.SAMPLES; i ++) {
				this.stereo_l_buffer2[i].set(this.stereo_l_buffer1[i].re * volume, this.stereo_l_buffer1[i].im);
				this.stereo_r_buffer2[i].set(this.stereo_r_buffer1[i].re * volume, this.stereo_r_buffer1[i].im);
				this.mono_buffer2[i].set(this.mono_buffer1[i].re * volume, this.mono_buffer1[i].im);
			}
		}
	}	
	
	public static class Canvas {	
		protected java.awt.Canvas
			component = new java.awt.Canvas();
		protected Engine
			parent;
		
		public Canvas(Engine parent) {
			this.parent = parent;
		}
		
		
		public void onInit() {
			
		}
		
		public void onExit() {
			
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
		
		protected int
			w,
			h;
		protected String
			title;
		protected boolean
			fullscreen;
		
		protected Engine
			parent;
		
		public Window(Engine parent) {
			this.parent = parent;
			this.mb.add(m1);
			this.mb.add(m2);
			this.mb.add(m3);
			this.component.setMenuBar(this.mb);
			this.component.add(this.parent.canvas.component);
			
			this.component.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent we) {
					parent.exit();
				}
			});
		}
		
		public void onInit() {
			this.w = this.parent.window_w;
			this.h = this.parent.window_h;
			this.title = this.parent.window_title;
			this.fullscreen = this.parent.window_fullscreen;
			
			this.component.dispose();			
			this.m1.removeAll();
			this.m2.removeAll();
			this.m3.removeAll();
			
			for(Effect effect: this.parent.effects) {
				MenuItem mi = new MenuItem(effect.name);
				mi.addActionListener((ae) -> {
					this.parent.effect = effect;
				});
				this.m1.add(mi);
			}
			for(Source source: this.parent.sources) {
				CheckboxMenuItem cbmi = new CheckboxMenuItem(source.name);
				cbmi.addItemListener((ie) -> {
					if(cbmi.getState())
						source.start();
					else
						source.close();
				});
				this.m2.add(cbmi);
			}
			int v = 25;
			for(int i = 25; i <= 500; i += v) {
				MenuItem mi = new MenuItem(i + "%");
				float volume = i / 100f;
				mi.addActionListener((ae) -> {
					parent.volume = volume;
				});
				if(i >= 100)
					v = 50;
				if(i >= 200)
					v = 100;
				m3.add(mi);
			}
			
			int
				w = this.w,
				h = this.h;
			if(this.fullscreen) {
				w = Util.FULLSCREEN_W;
				h = Util.FULLSCREEN_H;
			}
			this.component.setSize(
					w,
					h
					);
			this.component.setTitle(
					this.title
					);
			this.component.setUndecorated(
					this.fullscreen
					);
			
			this.component.setLocationRelativeTo(null);
			this.component.setVisible(true);
			
		}
		
		public void onExit() {
			this.component.dispose();
		}
	}
}
