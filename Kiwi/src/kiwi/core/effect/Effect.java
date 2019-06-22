package kiwi.core.effect;
import java.util.LinkedList;
import java.util.List;

import kiwi.core.Renderable;
import kiwi.core.Updateable;

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
		list.add(new SquareSideStereoWave());
		list.add(new Continuum());
		list.add(new Circularity());
		list.add(new InvertedCircularity());
		list.add(new Singularity());
		list.add(new SpaceStation());
		list.add(new Shift());
		list.add(new Test());
		return list;
	}
}
