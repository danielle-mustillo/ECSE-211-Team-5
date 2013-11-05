/* Danielle Mustillo, Nathaniel Anthonisen
 * 260533476, 260470621
 * ECSE 211 - Design Principles and Methods
 * September 24, 2013
 */
/*
 * Lab2.java
 */
import lejos.nxt.*;

public class Lab2 {
	private static final SensorPort lsPort = SensorPort.S1;
	private static final double leftRadius = 2.08, rightRadius = 2.08, wheelBase = 15.23;
	private static final double csOffset = 5.01;
	
	public static void main(String[] args) {
		int buttonChoice;
		ColorSensor colorSensor = new ColorSensor(lsPort);
		colorSensor.setFloodlight(true);
		
		// some objects that need to be instantiated
		OdometerOld odometer = new OdometerOld(leftRadius, rightRadius, wheelBase);
		OdometryCorrection odometryCorrection = new OdometryCorrection(odometer, colorSensor, csOffset);
		OdometryDisplay odometryDisplay = new OdometryDisplay(odometer, colorSensor);
		

		do {
			// clear the display
			LCD.clear();

			// ask the user whether the motors should drive in a square or float
			LCD.drawString("< Left | Right >", 0, 0);
			LCD.drawString("       |        ", 0, 1);
			LCD.drawString(" Float | Drive  ", 0, 2);
			LCD.drawString("motors | in a   ", 0, 3);
			LCD.drawString("       | square ", 0, 4);

			buttonChoice = Button.waitForAnyPress();
		} while (buttonChoice != Button.ID_LEFT
				&& buttonChoice != Button.ID_RIGHT);

		//choose only motor A and C
		if (buttonChoice == Button.ID_LEFT) {
			for (NXTRegulatedMotor motor : new NXTRegulatedMotor[] { Motor.A, Motor.B, Motor.C }) {
				motor.forward();
				motor.flt();
			}

			// start only the odometer and the odometry display
			odometer.start();
			odometryDisplay.start();
		} else {
			// start the odometer, the odometry display and (possibly) the
			// odometry correction
			odometer.start();
			odometryDisplay.start();
			odometryCorrection.start();

			// spawn a new Thread to avoid SquareDriver.drive() from blocking
			(new Thread() {
				public void run() {
					SquareDriver.drive(Motor.A, Motor.C, leftRadius, rightRadius, wheelBase);
				}
			}).start();
		}
		
		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		System.exit(0);
	}
}