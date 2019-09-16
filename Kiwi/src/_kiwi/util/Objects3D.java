package _kiwi.util;

public class Objects3D {

	
	public static float[] matrixMultiply(float[] point, float[][] field) {
		
		if(point.length != field.length) {
			System.out.println("Inproper Inputs to Multiply");
			return null;
		}
		
		float[] result = new float[point.length];
		for(int i = 0; i < field.length; i ++) {
			for(int j = 0; j < field.length; j++) {
				result[i] += (float)(point[j] * field[j][i]);
			}
		}
		
		return result;
	}
	
}
 