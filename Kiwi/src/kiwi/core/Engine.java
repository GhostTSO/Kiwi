package kiwi.core;

import java.awt.Canvas;
import java.awt.CheckboxMenuItem;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.util.List;

import kiwi.Kiwi;
import kiwi.core.input.Input;
import kiwi.core.render.RenderContext;
import kiwi.core.update.UpdateContext;
import kiwi.math.Complex;
import kiwi.util.Config;
import kiwi.util.Util;

public class Engine implements Runnable {
	public static final int
		FULLSCREEN_W = Toolkit.getDefaultToolkit().getScreenSize().width,
		FULLSCREEN_H = Toolkit.getDefaultToolkit().getScreenSize().height;
	private static final Engine
		ENGINE = new Engine();
	
	protected boolean
		debug,
		fullscreen;
	protected int
		thread_fps,
		thread_tps;
	protected int
		window_w,
		window_h;
	protected String
		window_title = "" + Kiwi.VERSION;
	
	protected Config
		config = new Config(
				DEBUG, (this.debug = false),
				FULLSCREEN, (this.fullscreen = false),
				THREAD_FPS, (this.thread_fps = 60),
				THREAD_TPS, (this.thread_tps = 60),
				WINDOW_W, (this.window_w = 640),
				WINDOW_H, (this.window_h = 480)
				);
	protected Thread
		thread;
	protected long
		fps,
		tps;	

	protected List<Source>
		sources;
	protected List<Effect>
		effects;	
	protected Effect
		effect;
	
	protected Frame
		window;
	protected Canvas
		canvas;
	protected Input
		input;
	protected MenuBar
		menubar;
	
	protected final Complex[]
		l_channel = new Complex[Source.SAMPLES],
		r_channel = new Complex[Source.SAMPLES];

	protected final UpdateContext
		update_context = new UpdateContext(
				this.l_channel,
				this.r_channel
				);
	protected final RenderContext
		render_context = new RenderContext(
				this.l_channel,
				this.r_channel
				);
	protected BufferStrategy
		buffer_strategy;
	
	public Engine() {
		for(int i = 0; i < Source.SAMPLES; i ++) {
			l_channel[i] = new Complex();
			r_channel[i] = new Complex();
		}
	}
	
	public static final Config getConfig() {
		return ENGINE.config;
	}
	
	public static synchronized final void init() {
		ENGINE._init();
	}
		
	public static synchronized final void exit() {
		ENGINE._exit();
	}
	
	private synchronized final void _init() {
		this.thread = new Thread(this);
		this.thread.start();
	}
	
	private synchronized final void _exit() {
		this.thread.interrupt();
	}	
	
	private final void onInit() {
		this.debug = this.config.getBoolean(DEBUG, this.debug);
		this.fullscreen = this.config.getBoolean(FULLSCREEN, this.fullscreen);
		this.thread_fps = this.config.getInt(THREAD_FPS, this.thread_fps);
		this.thread_tps = this.config.getInt(THREAD_TPS, this.thread_tps);
		this.window_w = this.config.getInt(WINDOW_W, this.window_w);
		this.window_h = this.config.getInt(WINDOW_H, this.window_h);	
		this.window_title = this.config.get(WINDOW_TITLE, this.window_title);
		
		this.sources = Source.getAvailableSources();
		this.effects = Effect.getAvailableEffects();
		
		if(effects.size() > 0)
			this.effect = this.effects.get(0);
		
		this.window = new  Frame();
		this.canvas = new Canvas();
		this.input  = new Input();
		this.menubar = new MenuBar();

		this.window.setUndecorated(this.fullscreen);
		this.window.setSize(
				this.fullscreen ? FULLSCREEN_W : this.window_w,
				this.fullscreen ? FULLSCREEN_H : this.window_h
				);		
		this.window.setTitle(this.window_title);
		
		this.window.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent we) {
				Engine.exit();
			}
		});
		
		this.canvas.setFocusable(true);
		this.canvas.setIgnoreRepaint(true);
		this.canvas.setFocusTraversalKeysEnabled(false);
		
		this.canvas.addKeyListener(this.input);
		this.canvas.addMouseListener(this.input);
		this.canvas.addMouseWheelListener(this.input);
		this.canvas.addMouseMotionListener(this.input);		
		
		this.window.add(canvas);
		
		Menu
			m1 = new Menu("Effect"),
			m2 = new Menu("Source");
		
		for(Effect _effect: this.effects) {
			MenuItem 
				m1_i = new MenuItem(_effect.name);
			m1_i.addActionListener((ae) -> {
				effect = _effect;
			});
			m1.add(m1_i);
		}
		
		for(Source _source: this.sources) {
			CheckboxMenuItem 
				m2_i = new CheckboxMenuItem(_source.name);
			m2_i.addItemListener((ie) -> {
				if(m2_i.getState())
					_source.start();
				else
					_source.close();
			});
			m2.add(m2_i);
		}
		
		this.menubar.add(m1);
		this.menubar.add(m2);		

		this.window.setLocationRelativeTo(null);
		this.window.setMenuBar(this.menubar);
		this.window.setVisible(true);
	}
	
	private final void onExit() {
		for(Source source: this.sources)
			source.close();
		this.window.dispose();
	}
	
	private final void onUpdate() {
		if(this.effect != null) {
			for(int i = 0; i < Source.SAMPLES; i ++) {
				this.l_channel[i].re = 0; this.l_channel[i].im = 0;
				this.r_channel[i].re = 0; this.r_channel[i].im = 0;
			}
			int n = 0;
			for(Source _source: this.sources) {
				if(_source.line.isOpen()) {
					_source.poll();
					for(int i = 0; i < Source.SAMPLES; i ++) {
						this.l_channel[i].re += _source.l_channel[i].re;
						this.l_channel[i].im += _source.l_channel[i].im;
						this.r_channel[i].re += _source.r_channel[i].re;
						this.r_channel[i].im += _source.r_channel[i].im;
					}
					n ++;
				}
			}
			for(int i = 0; i < Source.SAMPLES; i ++) {
				this.l_channel[i].re /= n;
				this.l_channel[i].im /= n;
				this.r_channel[i].re /= n;
				this.r_channel[i].im /= n;
			}
			Complex.fft(this.l_channel);
			Complex.fft(this.r_channel);
			
			this.update_context.canvas_w = this.canvas.getWidth();
			this.update_context.canvas_h = this.canvas.getHeight();
			
			this.effect.update(this.update_context);
		}
	}
	
	private static final Color
		debug_background = new Color(0f, 0f, 0f, .5f),
		debug_foreground = new Color(1f, 1f, 1f, .5f);
	private static final Font
		debug_font = new Font("Courier New", Font.PLAIN, 10);
	
	private final void onRender() {
		if(this.effect != null) {
			if(this.buffer_strategy == null || this.buffer_strategy.contentsLost()) {
				this.canvas.createBufferStrategy(2);
				this.buffer_strategy = this.canvas.getBufferStrategy();
			}
			this.render_context.g2D = (Graphics2D)this.buffer_strategy.getDrawGraphics();
			this.render_context.canvas_w = this.canvas.getWidth();
			this.render_context.canvas_h = this.canvas.getHeight();
			
			this.effect.render(this.render_context);
			
			if(this.debug) { 
				int
					padding_t = 2,
					padding_l = 2,
					padding_b = 2,
					padding_r = 2;
				this.render_context.g2D.setFont(debug_font);
				FontMetrics fm = this.render_context.g2D.getFontMetrics();
				String[]
						debug_info = {
							"Debug",
							"  FPS: " + this.fps + (this.thread_fps > 0 ? " / " + this.thread_fps + " " + (int)(100 * this.fps / this.thread_fps) + "%" : ""),
							"  TPS: " + this.tps + (this.thread_tps > 0 ? " / " + this.thread_tps + " " + (int)(100 * this.tps / this.thread_tps) + "%" : ""),
							" ΔFPS: ~ " + (1f / this.fps) + "s",
							" ΔTPS: ~ " + (1f / this.tps) + "s"						
						};
				int 
						debug_info_w = 0,
						debug_info_h = fm.getHeight() * debug_info.length;
				for(int i = 0; i < debug_info.length; i ++)
					debug_info_w = Util.max(debug_info_w, fm.stringWidth(debug_info[i]));
				debug_info_w += padding_l + padding_r;
				debug_info_h += padding_t + padding_b;
				
				this.render_context.g2D.setColor(debug_background);			
				this.render_context.g2D.fillRect(
							0,
							0,
							debug_info_w,
							debug_info_h
						);		
				this.render_context.g2D.setColor(debug_foreground);
				for(int i = 0; i < debug_info.length; i ++)
					this.render_context.g2D.drawString(debug_info[i], padding_l, padding_t + fm.getAscent() + fm.getHeight() * i);
			}
			
			this.render_context.g2D.dispose();
			this.buffer_strategy.show();
		}
	}
	
	private static final long
		ONE_SECOND = 1000000000L;

	@Override
	public void run() {
		this.onInit();
		try {
			long
				f_time = this.thread_fps > 0 ? ONE_SECOND / this.thread_fps : 0,
				t_time = this.thread_tps > 0 ? ONE_SECOND / this.thread_tps : 0,
				f_elapsed = 0,
				t_elapsed = 0,
				elapsed = 0,
				f_ct = 0,
				t_ct = 0,
				t = System.nanoTime();
			this.onUpdate();
			this.onRender();
			while(!Thread.interrupted()) {
				long dt = - t + (t = System.nanoTime());
				f_elapsed += dt;
				t_elapsed += dt;
				elapsed += dt;				
				if(t_elapsed >= t_time) {
					this.onUpdate();
					t_elapsed= 0;
					t_ct ++;
				}
				if(f_elapsed >= f_time) {
					this.onRender();
					f_elapsed = 0;
					f_ct ++;
				}
				if(elapsed >= ONE_SECOND) {
					this.fps = f_ct;
					this.tps = t_ct;
					elapsed = 0;
					f_ct = 0;
					t_ct = 0;
				}
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		this.onExit();
	}
	
	
	public static final String
		DEBUG = "debug",
		FULLSCREEN = "fullscreen",
		THREAD_FPS = "thread-fps",
		THREAD_TPS = "thread_tps",
		WINDOW_W = "window-w",
		WINDOW_H = "window-h",
		WINDOW_TITLE = "window-title";
}
