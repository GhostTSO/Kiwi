package _kiwi.core.effect;

import java.awt.Color;

import _kiwi.core.source.Source;
import _kiwi.util.Util;

public class StereoWaveform extends Effect {
	protected double
		stereo_min = 2,
		stereo_max = 10,
		stereo_ramp = 16;
	protected Color
		stereo_l_color = Color.WHITE,
		stereo_r_color = Color.GRAY ;
	
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
			h = Math.max((double)context.canvas_h / 2                 , 1);
		for(int i = 0; i < Source.SAMPLES / 2; i ++) {
			double
				x = (Source.SAMPLES / 1.65 - i),
				l = Util.map_loge(stereo_l[i] / x, 0, stereo_max, 0, 3 * h / 4, stereo_ramp) + 1,
				r = Util.map_loge(stereo_r[i] / x, 0, stereo_max, 0, 3 * h / 4, stereo_ramp) + 1;
			
			context.g2D.setColor(stereo_l_color);
			context.g2D.fillRect((int)(i * w), (int)(h - l), (int)w, (int)l);
			context.g2D.setColor(stereo_r_color);
			context.g2D.fillRect((int)(i * w), (int)(h    ), (int)w, (int)r);
		}			
	}
}