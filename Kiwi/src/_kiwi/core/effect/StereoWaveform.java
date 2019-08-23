package _kiwi.core.effect;

import java.awt.Color;

import _kiwi.core.source.Source;
import _kiwi.util.Util;

public class StereoWaveform extends Effect {
	protected double
		stereo_min = 2,
		stereo_max = 10,
		stereo_ramp = 16;
	protected double[]
		stereo_l_color_min = {1.0, 0.0, 0.0, 0.5},
		stereo_l_color_max = {1.0, 1.0, 0.0, 1.0},
		stereo_r_color_min = {0.0, 0.0, 1.0, 0.5},
		stereo_r_color_max = {0.0, 1.0, 1.0, 1.0};
	
	public StereoWaveform() {
		super("Stereo Waveform");
		background = new Color(0f, 0f, 0f, .1f);
	}
	
	@Override
	public void onRender(RenderContext context) {
		double[] 
				stereo_l = Util.hpf(context.stereo_l, stereo_min),
				stereo_r = Util.hpf(context.stereo_r, stereo_min);
		double
			w = Math.max((double)context.canvas_w * 2 / Source.SAMPLES, 1),
			h = Math.max((double)context.canvas_h / 2                 , 1)    , 
			max_l = Util.max(stereo_l),
			max_r = Util.max(stereo_r);

		double[]
				lerp0 = Util.lerp(
						stereo_l_color_min,
						stereo_l_color_max,
						Util.clamp(Util.map(max_l, 0, 4000))
						),
				lerp1 = Util.lerp(
						stereo_r_color_min,
						stereo_r_color_max,
						Util.clamp(Util.map(max_r, 0, 4000))
						);
		Color
				color0 = new Color(
						(float)lerp0[0],
						(float)lerp0[1],
						(float)lerp0[2],
						(float)lerp0[3]
						),
				color1 = new Color(
						(float)lerp1[0],
						(float)lerp1[1],
						(float)lerp1[2],
						(float)lerp1[3]
						);			
		for(int i = 0; i < Source.SAMPLES / 2; i += 6) {
			double
				x = (Source.SAMPLES / 1.65 - i),
				l = Util.map_loge(stereo_l[i] / x, 0, stereo_max, stereo_ramp),
				r = Util.map_loge(stereo_r[i] / x, 0, stereo_max, stereo_ramp),
				lh = l * 3 * h / 4 + 1,
				rh = r * 3 * h / 4 + 1;
			
					
			context.g2D.setColor(color0);
			context.g2D.fillRect((int)(i * w), (int)(h - lh), (int)(w * 6), (int)lh);
			context.g2D.setColor(color1);
			context.g2D.fillRect((int)(i * w), (int)(h     ), (int)(w * 6), (int)rh);
		}			
	}
}