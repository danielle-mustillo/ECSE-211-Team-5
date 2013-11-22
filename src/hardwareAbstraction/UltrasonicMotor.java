package hardwareAbstraction;

import utilities.Settings;

public class UltrasonicMotor {
	public static boolean isForward = true;

	/**
	 * The default position is set to be with the two side ultrasonic sensors
	 * pointing away from the center of the robot. Therefore, it will be at an
	 * angle when viewed from the front of the robot.
	 */
	public static synchronized int setDefaultPosition() {
		try {
			Settings.ultrasonicMotor.setAcceleration(200);
			Settings.ultrasonicMotor.setSpeed(200);
			Settings.ultrasonicMotor.rotateTo(-90, true);
			isForward = false;
		} catch (ArrayIndexOutOfBoundsException e) {
		} 
		return 2000;
	}
	
	/**
	 * The forward position is set to be with the two side ultrasonic sensors
	 * pointing directly ahead, in line with the centre ultrasonic sensor. Therefore, it will be at an
	 * angle when viewed from the front of the robot.
	 */
	public static synchronized int setForwardPosition() {
		
		try {
			Settings.ultrasonicMotor.setAcceleration(200);
			Settings.ultrasonicMotor.setSpeed(200);
			Settings.ultrasonicMotor.rotateTo(0, true);
			isForward = true;
		} catch (ArrayIndexOutOfBoundsException e) {
		} 
		return 2000;
	}

}
