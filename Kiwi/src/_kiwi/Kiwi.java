package _kiwi;

import _kiwi.core.Debug;
import _kiwi.core.Engine;
import kiwi.util.Version;

public class Kiwi {
	public static final Version
		VERSION = new Version("Kiwi", 2, 1, 0);
	
	public static void main(String[] args) {
		Debug.out.log(VERSION);
		Engine.init();
	}
}
