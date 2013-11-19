package hardwareAbstraction;

import utilities.Settings;

public class UltrasonicMotor {
	public static boolean isForward = false;

	/**
	 * The default position is set to be with the two side ultrasonic sensors
	 * pointing away from the center of the robot. Therefore, it will be at an
	 * angle when viewed from the front of the robot.
	 */
	public static void setDefaultPosition() {
		isForward = false;
		try {
			Settings.ultrasonicMotor.setSpeed(200);
			Settings.ultrasonicMotor.rotate(45);
		} catch (ArrayIndexOutOfBoundsException e) {
		}
	}
	
	/**
	 * The forward position is set to be with the two side ultrasonic sensors
	 * pointing directly ahead, in line with the centre ultrasonic sensor. Therefore, it will be at an
	 * angle when viewed from the front of the robot.
	 */
	public static void setForwardPosition() {
		isForward = true;
		try {
			Settings.ultrasonicMotor.setSpeed(200);
			Settings.ultrasonicMotor.rotate(-45);
		} catch (ArrayIndexOutOfBoundsException e) {
		}
	}

}
