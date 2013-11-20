package hardwareAbstraction;

import utilities.Settings;

public class UltrasonicMotor {
	public static boolean isForward = false;

	/**
	 * The default position is set to be with the two side ultrasonic sensors
	 * pointing away from the center of the robot. Therefore, it will be at an
	 * angle when viewed from the front of the robot.
	 */
	public static int setDefaultPosition() {
		isForward = false;
		try {
			Settings.ultrasonicMotor.setAcceleration(200);
			Settings.ultrasonicMotor.setSpeed(200);
			Settings.ultrasonicMotor.rotateTo(45, true);
		} catch (ArrayIndexOutOfBoundsException e) {
		} 
		return 2000;
	}
	
	/**
	 * The forward position is set to be with the two side ultrasonic sensors
	 * pointing directly ahead, in line with the centre ultrasonic sensor. Therefore, it will be at an
	 * angle when viewed from the front of the robot.
	 */
	public static int setForwardPosition() {
		isForward = true;
		try {
			Settings.ultrasonicMotor.setAcceleration(200);
			Settings.ultrasonicMotor.setSpeed(200);
			Settings.ultrasonicMotor.rotate(-45);
		} catch (ArrayIndexOutOfBoundsException e) {
		} 
		return 2000;
	}

}
