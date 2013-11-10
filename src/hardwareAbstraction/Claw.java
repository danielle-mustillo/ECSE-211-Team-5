package hardwareAbstraction;

import utilities.Settings;
import lejos.nxt.remote.RemoteMotor;

public class Claw {
	static RemoteMotor claw = Settings.clawMotor;
	static int value = 45; // 45 degrees. needs to be tested. 
	
	/**
	 * This method grabs an object. Returns nothing
	 */
	public static void grabObject() {
		claw.rotate(value);
	}

}
