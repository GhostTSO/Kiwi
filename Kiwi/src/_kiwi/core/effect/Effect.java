package _kiwi.core.effect;

import java.awt.Color;
import java.awt.Point;
import java.util.LinkedList;
import java.util.List;

import _kiwi.core.Renderable;
import _kiwi.core.Updateable;

public class Effect implements Renderable, Updateable {
	public static final Color
		TRANSPARENT_BLACK = new Color(0f, 0f, 0f, 0f),
		TRANSPARENT_WHITE = new Color(1f, 1f, 1f, 0f),
		DEFAULT_BACKGROUND = Color.BLACK,
		DEFAULT_FOREGROUND = Color.WHITE;
	public final String
		name;
	protected Color
		background = DEFAULT_BACKGROUND,
		foreground = DEFAULT_FOREGROUND;
	
	/**
	 * Constructor with name
	 * @param name the name of the Effect
	 */
	public Effect(String name) {
		this.name = name;
	}
	
	/**
	 * Render Effect
	 */
	@Override
	public void render(RenderContext context) {
		context = context.push();
		//render background
		context.g2D.setColor(background);
		context.g2D.fillRect(
				0, 0,
				context.canvas_w,
				context.canvas_h
				);
		//render foreground
		context.g2D.setColor(foreground);
		context.g2D.drawRect(
				0, 0,
				context.canvas_w,
				context.canvas_h
				);
		//render effect
		onRender(context);
		context = context.pull();
	}

	/**
	 * Update Effect
	 */
	@Override
	public void update(UpdateContext context) {
		context = context.push();
		onUpdate(context);
		context = context.pull();
	}
	
	public void onInit() { }
	public void onExit() { }
	public void onAttach() { }
	public void onDetach() { }
	public void onRender(RenderContext context) { }
	public void onUpdate(UpdateContext context) { }	
	
	public static final List<Effect> getAvailableEffects() {
		LinkedList<Effect> effects = new LinkedList<>();
		effects.add(new _kiwi.core.effect.effects.StereoWaveform());
		effects.add(new _kiwi.core.effect.effects.Drift());
		effects.add(new _kiwi.core.effect.effects.Circularity());
		effects.add(new _kiwi.core.effect.effects.Cardioid());
		effects.add(new _kiwi.core.effect.effects.Horizon());
		effects.add(new _kiwi.core.effect.effects.Oscilloscope());
		effects.add(new _kiwi.core.effect.effects.OscilloscopeXYMode());
		effects.add(new _kiwi.core.effect.effects.Bloxels());
		effects.add(new _kiwi.core.effect.effects.MountainValleys());
		effects.add(new _kiwi.core.effect.effects.InvertedCircularity());
		return effects;
	}
	
	public void onMouseMoved(Point mouse) { }	
	public void onWheelMoved(float wheel) { }	
	public void onKeyDnAction(int key) { }	
	public void onKeyUpAction(int key) { }	
	public void onBtnDnAction(Point mouse, int btn) { }	
	public void onBtnUpAction(Point mouse, int btn) { }
}
