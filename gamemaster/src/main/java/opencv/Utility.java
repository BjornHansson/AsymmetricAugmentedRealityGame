package opencv;

public class Utility {

	public static float angleDifference(float a, float b) {
		float diff = a - b;
		if (diff < -180)
			diff += 360;
		if (diff > 180)
			diff -= 360;
		return diff;
	}

}
