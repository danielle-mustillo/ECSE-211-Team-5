package hardwareAbstraction;

import utilities.Settings;

/**
 * Controls the Ultrasonic motor. It keeps track of whether or not the
 * ultrasonic sensors are in the forward position or not. Uses the motor define
 * in the {@link Settings} file, Which is a {@link NXTRemoteMotor}.
 * <p>
 * It is assumed the side ultrasonic sensors will be facing forward when the
 * robot starts. Furthermore, this method will keep track of its position and
 * will not rotate twice in the same direction (ie two identical calls will not
 * have double rotation).
 */
public class UltrasonicMotor {
	/**
	 * Whether the Ultrasonic sensor face forward or not. default is true. Robot
	 * should start facing forward.
	 */
	public static boolean isForward = true;

	/**
	 * The default position is set to be with the two side ultrasonic sensors
	 * pointing away from the center of the robot. Therefore, it will be at an
	 * angle when viewed from the front of the robot. They rotate 90 degrees
	 */
	public static synchronized void setDefaultPosition() {
		isForward = false;
		Settings.ultrasonicMotor.rotateTo(90);
		while (Settings.ultrasonicMotor.isMoving()) {
			sleep(100);
		}
	}

	/**
	 * The forward position is set to be with the two side ultrasonic sensors
	 * pointing directly ahead, in line with the centre ultrasonic sensor.
	 * Therefore, it will be at an angle when viewed from the front of the
	 * robot.
	 */
	public static synchronized void setForwardPosition() {
		isForward = true;
		Settings.ultrasonicMotor.rotateTo(0);
		while (Settings.ultrasonicMotor.isMoving()) {
			sleep(100);
		}
	}

	/**
	 * Sleeps the thread for a specified time
	 * 
	 * @param time
	 *            time to sleep the thread
	 */
	private static void sleep(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			// no exception expected here.
		}
	}

}
