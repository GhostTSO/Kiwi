package _kiwi;

import java.util.Arrays;

import _kiwi.util.Util;
import kiwi.util.Version;

public class Kiwi {
	public static final Version
		VERSION = new Version("Kiwi", 2, 0, 1);
	
	public static void main(String[] args) {
		System.out.println(VERSION);
		
		double[]
			a = { 1},
			b = {10};	
		
		System.out.println(Arrays.toString(Util.lerp(a, b, .5)));
		
		
		
		//Engine.init();		
	}
}
