/* Danielle Mustillo, Nathaniel Anthonisen
 * 260533476, 260470621
 * ECSE 211 - Design Principles and Methods
 * October 08, 2013
 */
/*
 *LightLocalizer.java
 */
import lejos.nxt.ColorSensor;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.Sound;

/**
 * The robot uses light localization to correct its position. 
 * @author dmusti
 *
 */
public class LightLocalizer {
	private Odometer odo;
	private TwoWheeledRobot robot;
	private ColorSensor cs;
	private Navigation nav;
	
	public static int ROTATION_SPEED = 60;
	public static int STRAIGHT_SPEED = 200;
	private NXTRegulatedMotor leftMotor, rightMotor;
	
	private double firstAngle, secondAngle, thirdAngle, fourthAngle;
	
	byte lineCount;
	
	
	public LightLocalizer(Odometer odo, ColorSensor cs)  {
		this.odo = odo;
		this.robot = odo.getTwoWheeledRobot();
		this.cs = cs;
		this.lineCount = 0;
		this.leftMotor = Motor.A;
		this.leftMotor = Motor.C; 
		
		nav = new Navigation(odo, 2.08, 2.08, 15.24,200,100);
		
		// turn on the light
		cs.setFloodlight(true);
	}
	
	public void doLocalization() {
		// drive to location listed in tutorial
		// done
		nav.turnTo(360);
		
		
		// start rotating and clock all 4 gridlines
		rotateClockwise();
		
		byte testCount = 0; // byte to use less memory.
		int[] sensorValues = new int[2]; // the current and last value read by
		// the sensor
		
		while(lineCount < 4) {
		// keeps the testCount value to a byte.
			if (testCount >= 2)
				testCount = 5;

			// get the reading from the colourSensor
			sensorValues[0] = cs.getRawLightValue();

			// initial derivative of the values read
			int derivative = 0;

			// compute the derivatives
			if (testCount < 2) {
				// do nothing if not enough tests are done
			} else {
				int newAvg = sensorValues[0];
				int oldAvg = sensorValues[1];
				derivative = (newAvg - oldAvg) / -1; // divide by -1 because i
																// want a
																// positive derivative
			}

						// if a spike in value, aka black line, correct the odometer.
			if (derivative > 25) {
				registerLine();
				Sound.twoBeeps(); // let us know the odometer was corrected.

				// avoid double reading the black line.
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					// there is nothing to be done here because it is not
					// expected that the odometry correction will be
					// interrupted by another thread
				}
			}
			else {} //do nothing.
			
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
		
		
					
		
		
		// do trig to compute (0,0) and 0 degrees
		// when done travel to (0,0) and turn to 0 degrees
		
		
		
	}
	
	private void registerLine() {
		lineCount++;
		Sound.twoBeeps();
		if(lineCount == 0)
			firstAngle = odo.getTheta();
		if(lineCount == 1)
			secondAngle = odo.getTheta();
		if(lineCount == 2)
			thirdAngle = odo.getTheta();
		if(lineCount == 3)
			fourthAngle = odo.getTheta();
	}

	public void goStraight(double x, double y, double distance) {
		while( (Math.pow(odo.getX()-x, 2) + Math.pow(odo.getY()-y, 2) - distance) < 0.05) {
			leftMotor.setSpeed((int) ROTATION_SPEED);
			rightMotor.setSpeed((int) ROTATION_SPEED);
			leftMotor.forward();
			rightMotor.forward();
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		leftMotor.stop();
		rightMotor.stop();
	}
	
	public void rotateClockwise() {
		leftMotor.setSpeed((int) ROTATION_SPEED);
		rightMotor.setSpeed((int) ROTATION_SPEED);
		leftMotor.forward();
		rightMotor.backward();
	}
	
	public void rotateCounterClockwise() {
		leftMotor.setSpeed(-(int) ROTATION_SPEED);
		rightMotor.setSpeed((int) ROTATION_SPEED);
		leftMotor.backward();
		rightMotor.forward();
	}
	
	public void motorsStop() {
		leftMotor.stop();
		rightMotor.stop();
	}
}

