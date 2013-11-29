package hardwareAbstraction;

import utilities.Settings;

/**
 * Handles movement of claw/grabber & center ultrasonic mechanism up and down.
 * <p>
 * The following mechanism makes sure the robot only has one state at a time.
 * The robot will only go to different states once.
 * 
 * @author Riley
 * @author Danielle
 * 
 */
public class Forklift {
	static NXTRemoteMotor lift = Settings.liftMotor;
	static int liftHeight = 15; // 15 cm upwards. Should be ok
	static int scanHeight = 10; // 10 cm upwards.
	static int scanHeightLow = 9; // 9 cm upwards. Has been tested.
	private static double radius = 1; // radius of "spool". Has not been tested.
	public static ForkliftState state = ForkliftState.GROUND; // sensor starts
																// on the
																// ground.

	/**
	 * Based on the passed state, this method will set the old height and new
	 * height and the new state. Then it will call {@link changeHeight}
	 * 
	 * @param s
	 *            new state
	 * @return the time to make the change as calculated by {@link changeHeight}
	 */
	public static synchronized int setHeight(ForkliftState s) {
		int height;
		int oldHeight;

		// Thread thread = new Thread();
		if (state == ForkliftState.GROUND)
			oldHeight = 0;
		else if (state == ForkliftState.LIFT_HEIGHT)
			oldHeight = liftHeight;
		else if (state == ForkliftState.SCAN_HEIGHT)
			oldHeight = scanHeight;
		else
			oldHeight = scanHeightLow;

		if (s == ForkliftState.LIFT_HEIGHT) {
			height = liftHeight;
		} else if (s == ForkliftState.SCAN_HEIGHT) {
			height = scanHeight;
		} else if (s == ForkliftState.SCAN_HEIGHT_LOW) {
			height = scanHeightLow;
		} else {
			height = 0;
		}

		state = s;
		return changeHeight(height, oldHeight);
	}

	/**
	 * Changes the height of the forklift returns an approximation of the time
	 * it will take
	 * 
	 * @param newHeight
	 * @return
	 */
	private static int changeHeight(int newHeight, int oldHeight) {
		int rotation = convertDistanceToAngle(newHeight);
		int naptime = Math.abs(newHeight - oldHeight) * 400;
		lift.setAcceleration(1000);
		lift.setSpeed(200);
		lift.rotateTo(-rotation, true);
		return naptime;
	}

	/**
	 * This method turns a distance into an angle for the spool to turn. Takes
	 * as parameter the distance you want to lift. It is essential the radius of
	 * spool be calibrated. The radius is the radius of the "spool" the string
	 * winds onto. The formula used is: d = 2*pi*radius*(angle)/360 ==> angle =
	 * 360 * d / (2*pi*radius).
	 * 
	 * @param distance
	 * @return
	 */
	private static int convertDistanceToAngle(int distance) {
		return (int) ((distance * 180) / (Math.PI * radius));
	}

	/**
	 * Each state corresponds to a height for a function of the
	 * grabber/ultrasonic mechanism.
	 * 
	 * @author Danielle
	 * 
	 */
	public enum ForkliftState {
		GROUND, SCAN_HEIGHT, LIFT_HEIGHT, SCAN_HEIGHT_LOW;
	}
}
