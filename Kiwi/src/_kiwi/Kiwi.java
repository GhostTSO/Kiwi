package _kiwi;

import _kiwi.core.Debug;
import _kiwi.core.Engine;
import kiwi.util.Version;

public class Kiwi {
	public static final Version
		VERSION = new Version("Kiwi", 2, 1, 0);
	
	public static void main(String[] args) {
		//set the minimum logging priority at any time
		Debug.setLevel(Debug.DEBUG);
		
		//indirect call to the default logger
		Debug.log(Debug.DEBUG, VERSION);
		
		//direct call to the default logger
		Debug.out.log(VERSION);
		
		
		Engine.init();
	}
}
