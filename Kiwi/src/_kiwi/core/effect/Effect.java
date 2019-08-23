package _kiwi.core.effect;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;

import _kiwi.core.Renderable;
import _kiwi.core.Updateable;

public class Effect implements Renderable, Updateable {
	public static final Color
		DEFAULT_BACKGROUND = Color.BLACK,
		DEFAULT_FOREGROUND = Color.WHITE;
	public final String
		name;
	protected boolean
		renderBackground = true,
		renderForeground = true;
	protected Color
		background = DEFAULT_BACKGROUND,
		foreground = DEFAULT_FOREGROUND;
	
	public Effect(String name) {
		this.name = name;
	}
	

	@Override
	public void render(RenderContext context) {
		context = context.push();
		context.g2D.setColor(background);
		context.g2D.fillRect(
				0, 0,
				context.canvas_w,
				context.canvas_h
				);
		context.g2D.setColor(foreground);
		context.g2D.drawRect(
				0, 0,
				context.canvas_w,
				context.canvas_h
				);
		onRender(context);
		context = context.pull();
	}

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
		effects.add(new StereoWaveform());
		effects.add(new Continuum());
		return effects;
	}
}
