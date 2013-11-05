/* Danielle Mustillo, Nathaniel Anthonisen
 * 260533476, 260470621
 * ECSE 211 - Design Principles and Methods
 * September 24, 2013
 */
import lejos.nxt.ColorSensor;
import lejos.nxt.Sound;

/* 
 * OdometryCorrection.java
 */

public class OdometryCorrection extends Thread {
	private static final long CORRECTION_PERIOD = 10;
	private OdometerOld odometer; // compare the values to correct to against that
								// of the odometer
	private ColorSensor colorSensor; // needed to do correction
	private double csOffset; // need to know how far the sensor is from the
								// centre of the bot, for correction purposes
	private Sound sound; // for debugging and interest

	/**
	 * Constructor Requires the colour sensor input to correct, requires the
	 * csOffset to know how to calibrate the corrections.
	 * 
	 * @param odometer
	 * @param colorSensor
	 * @param csOffset
	 */
	public OdometryCorrection(OdometerOld odometer, ColorSensor colorSensor,
			double csOffset) {
		this.odometer = odometer;
		this.colorSensor = colorSensor;
		this.csOffset = csOffset;
	}

	// run method (required for Thread)
	public void run() {
		long correctionStart, correctionEnd;
		byte testCount = 0; // byte to use less memory.
		int[] sensorValues = new int[2]; // the current and last value read by
											// the sensor

		while (true) {
			correctionStart = System.currentTimeMillis();

			// keeps the testCount value to a byte.
			if (testCount >= 2)
				testCount = 5;

			// get the reading from the colourSensor
			sensorValues[0] = colorSensor.getRawLightValue();

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

			if (SquareDriver.getTurning()) {
				// do nothing, robot is turning. Read no black lines.
			} else {
				// if a spike in value, aka black line, correct the odometer.
				if (derivative > 25) {
					correctOdometer();
					sound.twoBeeps(); // let us know the odometer was corrected.

					// avoid double reading the black line.
					try {
						Thread.sleep(CORRECTION_PERIOD * 5);
					} catch (InterruptedException e) {
						// there is nothing to be done here because it is not
						// expected that the odometry correction will be
						// interrupted by another thread
					}
				}
			}

			// put your correction code here

			// this ensure the odometry correction occurs only once every
			// period
			correctionEnd = System.currentTimeMillis();
			if (correctionEnd - correctionStart < CORRECTION_PERIOD) {
				try {
					Thread.sleep(CORRECTION_PERIOD
							- (correctionEnd - correctionStart));
				} catch (InterruptedException e) {
					// there is nothing to be done here because it is not
					// expected that the odometry correction will be
					// interrupted by another thread
				} finally {
					testCount++; // test complete
					sensorValues[1] = sensorValues[0]; // put current value as
														// the old value.
				}
			}
		}
	}

	public void correctOdometer() {
		// get odometer readings
		double angle = odometer.getTheta();

		/*
		 * This if statement is present because, should we want to program a
		 * different driver The code would be easy to re-calibrate. The only
		 * thing that would change is this if statement or put an else
		 * statement.
		 */
		/*
		 * Should the square driver go more than three bricks (or more than 5,
		 * 15.4 cm distances) This value would need to be changed
		 */
		// for the moment, we will never reach the below stated values.
		// if we ever will, these can easily be adjusted.
		double currentDifference = Math.pow(10, 10); 
		double prevDifference = Math.pow(10, 11); 
		double correctedValue = 0;
		boolean found = false;

		for (int n = 0; !found; ++n) {
			int m = 2 * n + 1; // only odd values really possible (no line half way through blocks)
			double possibleLineLocation = 15.4 * m - introduceOffset(angle);
			currentDifference = calculateDifference(possibleLineLocation, angle);
			
			if (Math.abs(currentDifference) > Math.abs(prevDifference)) {
				found = true;
				setOdometer(angle, correctedValue);
			}
			correctedValue = 15.4 * m - csOffset;
			prevDifference = currentDifference;
		}
	}
	
	private double calculateDifference(double possibleLineLocation, double angle) {
		double y = odometer.getY();
		double x = odometer.getX();
		
		if (angle < 0.785) { // between 0 and 45 degrees
			return y - possibleLineLocation;
		} else if (angle < 2.356) { // less than 135 degrees
			return x - possibleLineLocation;
		} else if (angle < 3.927) { // less than 225 degrees
			return y - possibleLineLocation;
		} else if (angle < 5.498) { // less than 315 degrees
			return x - possibleLineLocation;
		} else { // between 360 and 315 degrees
			return y - possibleLineLocation;
		}
	}
	
	//adds positive value if going forward. 
	private double introduceOffset(double angle) {
		if (angle < 0.785) { // between 0 and 45 degrees
			return csOffset;
		} else if (angle < 2.356) { // less than 135 degrees
			return csOffset;
		} else if (angle < 3.927) { // less than 225 degrees
			return -csOffset;
		} else if (angle < 5.498) { // less than 315 degrees
			return -csOffset;
		} else { // between 360 and 315 degrees
			return csOffset;
		}
	}

	/*
	 * private double findClosestLine(double angle, double position) { if (angle
	 * < 0.785) { // between 0 and 45 degrees int m = 2 * n + 1; // only odd
	 * values really possible (no line // half way through blocks)
	 * currentDifference = y - (15.4 * m - csOffset);
	 * 
	 * // will never be true until the second run, so will never set //
	 * correctedValue to 0. if (Math.abs(currentDifference) >
	 * Math.abs(prevDifference)) { found = true; setOdometer(angle,
	 * correctedValue); } else prevDifference = currentDifference;
	 * 
	 * correctedValue = 15.4 * m - csOffset; } else if (angle < 2.356) { // less
	 * than 135 degrees odometer.setX(value); } else if (angle < 3.927) { //
	 * less than 225 degrees odometer.setY(value); } else if (angle < 5.498) {
	 * // less than 315 degrees odometer.setX(value); } else if (angle < 6.30) {
	 * // between 360 and 315 degrees odometer.setY(value); } return 0; }
	 */
	private void setOdometer(double angle, double value) {
		if (angle < 0.785) { // between 0 and 45 degrees
			odometer.setY(value);
		} else if (angle < 2.356) { // less than 135 degrees
			odometer.setX(value);
		} else if (angle < 3.927) { // less than 225 degrees
			odometer.setY(value);
		} else if (angle < 5.498) { // less than 315 degrees
			odometer.setX(value);
		} else if (angle < 6.30) { // between 360 and 315 degrees
			odometer.setY(value);
		}
	}
}