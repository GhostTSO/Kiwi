package kiwi.util;

public class Util {
	
	public static final float map(float x, float a, float b) {
		return (x - a) / (b - a);
	}
	
	public static final float map(float x, float a, float b, float c, float d) {
		return Util.map(x, a, b) * (d - c) + c;
	}
	
	public static final float map_tanh(float x, float a, float b, float r) {
		return Util.tanh((x - a) /(b - a) * r);
	}
	
	public static final float map_tanh(float x, float a, float b, float c, float d, float r) {
		return Util.map_tanh(x, a, b, r) * (d - c) + c;
	}
	
	public static final float map_loge(float x, float a, float b, float e) {
		return 1f / (1f + Util.exp(- (x - a) / (b - a) * e)) * 2f - 1f;
	}
	
	public static final float map_loge(float x, float a, float b, float c, float d, float e) {
		return Util.map_loge(x, a, b, e) * (d - c) + c;
	}
	
	public static final float exp(float x) {
		return (float)Math.exp(x);
	}
	
	public static final float log(float x) {
		return (float)Math.log(x);
	}
	
	public static final float log(float x, float e) {
		return (float)(Math.log(x) / Math.log(e));
	}
	
	public static final float sin(float x) {
		return (float)java.lang.Math.sin(x);
	}
	
	public static final float cos(float x) {
		return (float)java.lang.Math.cos(x);
	}
	
	public static final float tan(float x) {
		return (float)java.lang.Math.tan(x);
	}
	
	public static final float asin(float x) {
		return (float)Math.asin(x);
	}
	
	public static final float acos(float x) {
		return (float)Math.acos(x);
	}
	
	public static final float atan(float x) {
		return (float)Math.atan(x);
	}
	
	public static final float sinh(float x) {
		return (float)Math.sinh(x);
	}
	
	public static final float cosh(float x) {
		return (float)Math.cosh(x);
	}
	
	public static final float tanh(float x) {
		return (float)Math.tanh(x);
	}
	
	public static final float toDegrees(float Θ) {
		return (float)java.lang.Math.toDegrees(Θ);
	}
	
	public static final float toRadians(float Θ) {
		return (float)java.lang.Math.toRadians(Θ);
	}
	
	public static final int min(int a, int b) {
		return a <= b ? a : b;
	}
	
	public static final float min(float a, float b) {
		return a <= b ? a : b;
	}
	
	public static final int max(int a, int b) {
		return a >= b ? a : b;
	}
	
	public static final float max(float a, float b) {
		return a >= b ? a : b;
	}
	
	public static final int stringToInt(String str) {
		return stringToInt(str, 0);
	}
	
	public static final int stringToInt(String str, int alt) {
		try {
			return Integer.parseInt(str);
		} catch(Exception ex) {
			return alt;
		}
	}
	
	public static final long stringToLong(String str) {
		return stringToLong(str, 0L);
	}
	
	public static final long stringToLong(String str, long alt) {
		try {
			return Long.parseLong(str);
		} catch(Exception ex) {
			return alt;
		}
	}
	
	public static final float stringToFloat(String str) {
		return stringToFloat(str, 0f);
	}
	
	public static final float stringToFloat(String str, float alt) {
		try {
			return Float.parseFloat(str);
		} catch(Exception ex) {
			return alt;
		}
	}
	
	public static final double stringToDouble(String str) {
		return stringToDouble(str, 0.);
	}
	
	public static final double stringToDouble(String str, double alt) {
		try {
			return Double.parseDouble(str);
		} catch(Exception ex) {
			return alt;
		}
	}
	
	public static final boolean stringToBoolean(String str) {
		return stringToBoolean(str, false);
	}
	
	public static final boolean stringToBoolean(String str, boolean alt) {
		try {
			return Boolean.parseBoolean(str);
		} catch(Exception ex) {
			return alt;
		}
	}
}
