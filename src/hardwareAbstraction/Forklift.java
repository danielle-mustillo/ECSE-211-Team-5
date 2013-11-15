package hardwareAbstraction;

import lejos.nxt.comm.RConsole;
import lejos.nxt.remote.RemoteMotor;
import utilities.Settings;

public class Forklift {
	static RemoteMotor lift = Settings.forkliftMotor;
	static int liftHeight = -15; // 15 cm upwards. Should be ok
	static int scanHeight = -10; // 10 cm upwards. Needs to be tested. 
	private static double radius = 1; //radius of "spool". Must be tested. 
	public static ForkliftState state = ForkliftState.GROUND; //sensor starts on the ground.
	/**
	 * This method lifts an object. Returns nothing.
	 * @bug the execution of external motors causes exceptions. Try-catch block was put for now. Must be fixed. 
	 */
	public static void liftObject() {
		RConsole.println("lifting object");
		// reset the forklift to ground state.
		if (state == ForkliftState.SCAN_HEIGHT) {
			resetScanHeight();
		}
		if (state == ForkliftState.LIFT_HEIGHT) {
			lowerObject();
		}
		state = ForkliftState.LIFT_HEIGHT;
		try {
			lift.setSpeed(100);
			lift.rotate(convertDistanceToAngle(liftHeight));
		} catch (ArrayIndexOutOfBoundsException e) {

		}
	}
	
	/**
	 * This method lowers an object. Returns nothing.
	 * @bug the execution of external motors causes exceptions. Try-catch block was put for now. Must be fixed. 
	 */
	public static void lowerObject() {
		RConsole.println("lowering to default position");
		if (state == ForkliftState.SCAN_HEIGHT)
			resetScanHeight();
		if (state == ForkliftState.GROUND)
			return;
		state = ForkliftState.GROUND;
		try {
			lift.setSpeed(100);
			lift.rotate(-convertDistanceToAngle(liftHeight));
		} catch (ArrayIndexOutOfBoundsException e){ }
	}
	
	/**
	 * This method will raise the forklift to allow the color sensor to identify the block. 
	 * @bug the execution of external motors causes exceptions. Try-catch block was put for now. Must be fixed.
	 */
	public static void setScanHeight() {
		RConsole.println("lifting to scan height");
		if (state == ForkliftState.LIFT_HEIGHT)
			lowerObject();
		if (state == ForkliftState.SCAN_HEIGHT)
			return;
		state = ForkliftState.SCAN_HEIGHT;
		try {
			lift.setSpeed(100);
			lift.rotate(convertDistanceToAngle(scanHeight));
		} catch (ArrayIndexOutOfBoundsException e){
		
		}
	}
	
	/**
	 * This method will lower the forklift after identifying the block. 
	 * @bug the execution of external motors causes exceptions. Try-catch block was put for now. Must be fixed.
	 */
	public static void resetScanHeight() {
		RConsole.println("lowering to default height");
		if (state == ForkliftState.LIFT_HEIGHT)
			lowerObject();
		if (state == ForkliftState.GROUND)
			return;
		state = ForkliftState.GROUND;
		try {
			lift.setSpeed(100);
			lift.rotate(-convertDistanceToAngle(scanHeight));
		} catch (ArrayIndexOutOfBoundsException e){
		
		}
	}

	/**
	 * This method turns a distance into an angle for the robot to turn. Takes as parameter the distance you want to lift. 
	 * It is essential the radius of this class be calibrated. The radius is the radius of the "spool" the string winds onto.
	 * The formula used is: d = 2*pi*radius*(angle)/360 ==> angle = 360 * d / (2*pi*radius).
	 * @param distance
	 * @return
	 */
	private static int convertDistanceToAngle(int distance) {
		return (int)( (distance * 360) / (2 * Math.PI * radius) );
	}
	
	public enum ForkliftState {
		GROUND, SCAN_HEIGHT, LIFT_HEIGHT;
	}
}
