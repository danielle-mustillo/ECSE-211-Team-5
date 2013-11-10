package utilities;

/**
 * 
 * The class contains helper methods for finding the principle angle & the minimum Angle
 * The code is modified from the Odometer given in Lab 4
 * 
 * @author Riley
 *
 */
public class Angle {

	/**
	 * Ensures angle is always in [0, 2PI)
	 * @param angle to convert
	 * @return
	 */
	public static double principleAngle(double angle) {		
		if (angle < 0.0) {
			angle = 2*Math.PI + (angle % (2*Math.PI));
		}
		
		return angle % (2*Math.PI);
	}
	/**
	 * Returns the minimum angle
	 * @param a Angle 1
	 * @param b Angle 2
	 * @return
	 */
	public static double minimumAngle(double a, double b) {
		double d = principleAngle(b - a);
		
		if (d < Math.PI)
			return d;
		else
			return d - 2*Math.PI;
	}
}
