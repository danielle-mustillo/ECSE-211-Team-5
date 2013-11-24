package hardwareAbstraction;

import utilities.Settings;
import lejos.nxt.comm.RConsole;
import lejos.nxt.remote.RemoteMotor;

public class Claw {
	static NXTRemoteMotor claw = Settings.clawMotor;
	static int value = 70; // 45 degrees. needs to be tested. 
	
	/**
	 * This method grabs an object. Returns nothing
	 * @bug the execution of external motors causes exceptions. Try-catch block was put for now. Must be fixed. 
	 */
	public static int grabObject() {
		RConsole.println("grabbing object");
		claw.setAcceleration(200);
		claw.setSpeed(150);
		claw.rotateTo(0, true);
		return 2000;
	}
	
	/**
	 * This method releases an object. Returns nothing
	 * @bug the execution of external motors causes exceptions. Try-catch block was put for now. Must be fixed. 
	 */
	public static int releaseObject() {
		RConsole.println("releasing object");
		claw.setAcceleration(200);
		claw.setSpeed(150);
		claw.rotateTo(-value, true);
		return 2000;
	}
}
