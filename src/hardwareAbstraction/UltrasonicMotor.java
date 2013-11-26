package hardwareAbstraction;

import utilities.Settings;

/**
 * Controls the Ultrasonic motor. It keeps track of whether or not the ultrasonic 
 * sensors are in the forward position or not.  Uses the motor define in the {@link Settings} file,
 * Which is a {@link NXTRemoteMotor}
 * @author Riley
 * @author Danielle
 *
 */
public class UltrasonicMotor {
	/**
	 * Whether the Ultrasonic sensor face forward or not.  default is true.
	 */
	public static boolean isForward = true;

	/**
	 * The default position is set to be with the two side ultrasonic sensors
	 * pointing away from the center of the robot. Therefore, it will be at an
	 * angle when viewed from the front of the robot.  They rotate 90 degrees
	 */
	public static synchronized void setDefaultPosition() {
		isForward = false;
		Settings.ultrasonicMotor.rotateTo(90);
		while(Settings.ultrasonicMotor.isMoving()) {
			sleep(100);
		}
	}
	
	
	/*public static synchronized int setDefaultPosition() {
		try {
			Settings.ultrasonicMotor.setAcceleration(200);
			Settings.ultrasonicMotor.setSpeed(200);
			Settings.ultrasonicMotor.rotateTo(-90, true);
			isForward = false;
		} catch (ArrayIndexOutOfBoundsException e) {
		} 
		return 2000;
	}*/
	
	/**
	 * The forward position is set to be with the two side ultrasonic sensors
	 * pointing directly ahead, in line with the centre ultrasonic sensor. Therefore, it will be at an
	 * angle when viewed from the front of the robot.
	 */
	public static synchronized void setForwardPosition() {
		isForward = true;
		Settings.ultrasonicMotor.rotateTo(0);
		while(Settings.ultrasonicMotor.isMoving()) {
			sleep(100);
		}
	}
	
	/*public static synchronized int setForwardPosition() {
		
		try {
			Settings.ultrasonicMotor.setAcceleration(200);
			Settings.ultrasonicMotor.setSpeed(200);
			Settings.ultrasonicMotor.rotateTo(0, true);
			isForward = true;
		} catch (ArrayIndexOutOfBoundsException e) {
		} 
		return 2000;
	}*/
	
	/**
	 * Sleeps the thread for a specified time
	 * @param time time to sleep the thread
	 */
	private static void sleep(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
