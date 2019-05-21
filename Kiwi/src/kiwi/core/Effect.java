package kiwi.core;
import java.util.LinkedList;
import java.util.List;

import kiwi.core.effect.SideStereoWaveForm;
import kiwi.core.effect.StereoWaveform;
import kiwi.core.render.Renderable;
import kiwi.core.update.Updateable;

public abstract class Effect implements Renderable, Updateable {
	public final String
		name;
	
	public Effect(String name) {
		this.name = name;
	}
	
	public static List<Effect> getAvailableEffects() {
		List<Effect> list = new LinkedList<>();
		list.add(new StereoWaveform());
		list.add(new SideStereoWaveForm());
		return list;
	}
}
