package _kiwi;

import _kiwi.core.Engine;
import kiwi.util.Version;

public class Kiwi {
	public static final Version
		VERSION = new Version("Kiwi", 2, 0, 0);
	
	public static void main(String[] args) {
		System.out.println(VERSION);
		Engine.init();		
	}
}
