package hardwareAbstraction;

import utilities.Settings;
import lejos.nxt.comm.RConsole;
import lejos.nxt.remote.RemoteMotor;

public class Claw {
	static RemoteMotor claw = Settings.clawMotor;
	static int value = 90; // 45 degrees. needs to be tested. 
	
	/**
	 * This method grabs an object. Returns nothing
	 * @bug the execution of external motors causes exceptions. Try-catch block was put for now. Must be fixed. 
	 */
	public static void grabObject() {
		RConsole.println("grabbing object");
		try {
			claw.setSpeed(150);
			claw.rotate(value);
		} catch (ArrayIndexOutOfBoundsException e){
			
		}
	}
	
	/**
	 * This method releases an object. Returns nothing
	 * @bug the execution of external motors causes exceptions. Try-catch block was put for now. Must be fixed. 
	 */
	public static void releaseObject() {
		RConsole.println("releasing object");
		try {
			claw.setSpeed(150);
			claw.rotate(-value);
		} catch (ArrayIndexOutOfBoundsException e){
			
		}
	}
}
