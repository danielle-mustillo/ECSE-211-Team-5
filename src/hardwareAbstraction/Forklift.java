package hardwareAbstraction;

import lejos.nxt.remote.RemoteMotor;
import utilities.Settings;

public class Forklift {
	static RemoteMotor lift = Settings.forkliftMotor;
	static int value = 45; // 45 degrees. needs to be tested. 
	
	/**
	 * This method lifts an object. Returns nothing
	 */
	public static void liftObject() {
		lift.rotate(value);
	}
}
