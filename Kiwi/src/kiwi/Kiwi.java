package kiwi;

import kiwi.core.Engine;
import kiwi.util.Version;

public class Kiwi {	
	public static final Version
		VERSION = new Version("Kiwi", 1, 1, 3);
	
	public static void main(String[] args) {
		System.out.println(VERSION);
		
		Engine engine = new Engine();
		engine.init();
	}	
	
}
