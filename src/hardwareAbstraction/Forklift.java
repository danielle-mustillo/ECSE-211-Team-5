package hardwareAbstraction;

import lejos.nxt.comm.RConsole;
import lejos.nxt.remote.RemoteMotor;
import utilities.Settings;

public class Forklift {
	static RemoteMotor lift = Settings.forkliftMotor;
	static int distance = -45; // 45 cm upwards. needs to be tested. 
	private static double radius = 1; //radius of "spool". Must be tested. 
	
	/**
	 * This method lifts an object. Returns nothing.
	 * @bug the execution of external motors causes exceptions. Try-catch block was put for now. Must be fixed. 
	 */
	public static void liftObject() {
		RConsole.println("lifting object");
		try {
			lift.rotate(convertDistanceToAngle(distance));
		} catch (ArrayIndexOutOfBoundsException e){
		
		}
	}
	
	/**
	 * This method lowers an object. Returns nothing.
	 * @bug the execution of external motors causes exceptions. Try-catch block was put for now. Must be fixed. 
	 */
	public static void lowerObject() {
		RConsole.println("lowering object");
		try {
			lift.rotate(-convertDistanceToAngle(distance));
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
}
