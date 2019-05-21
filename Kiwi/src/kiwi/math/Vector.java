package kiwi.math;

public class Vector {
	public float
		X,
		Y;
	
	public Vector(float x, float y) {
		this.X = x;
		this.Y = y;
	}	
	
	
	public int x() {
		return (int)X;
	}
	
	public int y() {
		return (int)Y;
	}
}
