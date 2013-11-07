package hardwareAbstraction;

import lejos.nxt.NXTRegulatedMotor;
import utilities.Settings;

public class Drive {

	private NXTRegulatedMotor leftMotor;
	private NXTRegulatedMotor rightMotor;
	
	public static final double LEFT_RADIUS = 2.11;
	public static final double RIGHT_RADIUS = 2.11;
	public static final double WIDTH = 14.92;
	
	public Drive() {
		leftMotor = Settings.leftDriveMotor;
		rightMotor = Settings.rightDriveMotor;
	}
	
	/**
	 * returns displacement and heading based on tacho counts -> passed through the data array pointer
	 * @param data
	 */
	public void getDisplacementAndHeading(double [] data) {
		int leftTacho, rightTacho;
		leftTacho = leftMotor.getTachoCount();
		rightTacho = rightMotor.getTachoCount();
		
		data[0] = (leftTacho * LEFT_RADIUS + rightTacho * RIGHT_RADIUS) *	Math.PI / 360.0;
		data[1] = (leftTacho * LEFT_RADIUS - rightTacho * RIGHT_RADIUS) / WIDTH * Math.PI / 180.0;
	}
}
