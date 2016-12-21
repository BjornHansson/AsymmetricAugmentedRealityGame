package opencv;

public class Utility {
	/**
	 * Calculate angle difference
	 * 
	 * @param a
	 * @param b
	 * @return the difference
	 */
	public static float angleDifference(float a, float b) {
		float diff = a - b;
		if (diff < -180)
			diff += 360;
		if (diff > 180)
			diff -= 360;
		return diff;
	}
}
