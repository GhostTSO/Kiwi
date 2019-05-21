package kiwi.util;

import java.io.File;
import java.util.TreeMap;

public class Config {
	protected final TreeMap<String, String>
		map = new TreeMap<>();
	
	public Config() {
		//do nothing
	}
	
	public Config(Object... o) {
		int n = o.length - (o.length & 1);
		for(int i = 0; i < n; i += 2) {
			int 
				a = i + 0,
				b = i + 1;
			this.set(o[a], o[b]);
		}			
	}
	
	public void set(Object key, Object obj) {
		this.map.put(
				key != null ? key.toString() : null,
				obj != null ? obj.toString() : null
				);
	}
	
	public String get(String key, String alt) {
		String str = this.map.get(key);
		return str != null ? str : alt;
	}
	
	public int getInt(String key, int alt) {
		return Util.stringToInt(this.get(key, null), alt);
	}
	
	public long getLong(String key, long alt) {
		return Util.stringToLong(this.get(key, null), alt);
	}
	
	public float getFloat(String key, float alt) {
		return Util.stringToFloat(this.get(key, null), alt);
	}
	
	public double getDouble(String key, double alt) {
		return Util.stringToDouble(this.get(key, null), alt);
	}
	
	public boolean getBoolean(String key, boolean alt) {
		return Util.stringToBoolean(this.get(key, null), alt);
	}
	
	public void save(File file) {
		
	}
	
	public void load(File file) {
		
	}
}
