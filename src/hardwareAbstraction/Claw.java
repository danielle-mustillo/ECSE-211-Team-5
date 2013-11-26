package hardwareAbstraction;

import utilities.Settings;
import lejos.nxt.comm.RConsole;
import lejos.nxt.remote.RemoteMotor;
/**
 * Controls the Claw Motor.  Uses the claw motor defined in {@link Settings}
 * <p>
 * Only open/close functions have been implemented
 * 
 * @author Danielle
 *
 */
public class Claw {
	static NXTRemoteMotor claw = Settings.clawMotor;
	static int value = 55; // 45 degrees. needs to be tested.
//	static int start = 5; // this is the starting angle. Slightly more "closed" than start. 
	
	/**
	 * This method grabs an object. Returns a default time to sleep (2s)
	 *  
	 */
	public static int grabObject() {
		RConsole.println("grabbing object");
		claw.setAcceleration(200);
		claw.setSpeed(150);
		claw.rotateTo(90, true);
		return 2000;
	}
	
	/**
	 * This method releases an object. Returns a default time to sleep (2s)
	 */
	public static int releaseObject() {
		RConsole.println("releasing object");
		claw.setAcceleration(200);
		claw.setSpeed(150);
		claw.rotateTo(-value, true);
		return 2000;
	}
}
