/* Danielle Mustillo, Nathaniel Anthonisen
 * 260533476, 260470621
 * ECSE 211 - Design Principles and Methods
 * September 24, 2013
 */
/*
 * OdometryDisplay.java
 */
import lejos.nxt.LCD;
import lejos.nxt.ColorSensor;

public class OdometryDisplay extends Thread {
	private static final long DISPLAY_PERIOD = 250;
	private OdometerOld odometer;
	private ColorSensor colorSensor; //shows what the colour sensor sees. Useful for interest/future debugging.

	// constructor
	public OdometryDisplay(OdometerOld odometer, ColorSensor colorSensor) {
		this.odometer = odometer;
		this.colorSensor = colorSensor;
	}

	// run method (required for Thread)
	public void run() {
		long displayStart, displayEnd;
		double[] position = new double[3];

		// clear the display once
		LCD.clearDisplay();

		while (true) {
			displayStart = System.currentTimeMillis();

			// clear the lines for displaying odometry information
			LCD.drawString("X:              ", 0, 0);
			LCD.drawString("Y:              ", 0, 1);
			LCD.drawString("T:              ", 0, 2);
			LCD.drawString("C:              ", 0, 3);

			// get the odometry information
			odometer.getPosition(position, new boolean[] { true, true, true });

			// display odometry information
			for (int i = 0; i < 3; i++) {
				LCD.drawString(formattedDoubleToString(position[i], 2), 3, i);
			}
			
			//display the colour sensor readings
			int value = colorSensor.getRawLightValue();
			LCD.drawString(""+value, 3, 3);

			// throttle the OdometryDisplay
			displayEnd = System.currentTimeMillis();
			
			//if the difference is less than the display period, sleep the thread for that amount of time. 
			if (displayEnd - displayStart < DISPLAY_PERIOD) {
				try {
					Thread.sleep(DISPLAY_PERIOD - (displayEnd - displayStart));
				} catch (InterruptedException e) {
					// there is nothing to be done here because it is not
					// expected that OdometryDisplay will be interrupted
					// by another thread
				}
			}
		}
	}
	
	private static String formattedDoubleToString(double x, int places) {
		String result = "";
		String stack = "";
		long t;
		
		// put in a minus sign as needed
		if (x < 0.0)
			result += "-";
		
		// put in a leading 0
		if (-1.0 < x && x < 1.0)
			result += "0";
		else {
			//negative already accounted for
			t = (long)x;
			if (t < 0)
				t = -t;
			
			while (t > 0) {
				stack = Long.toString(t % 10) + stack;
				t /= 10;
			}
			
			result += stack;
		}
		
		// put the decimal, if needed
		if (places > 0) {
			result += ".";
		
			// put the appropriate number of decimals
			for (int i = 0; i < places; i++) {
				x = Math.abs(x);
				x = x - Math.floor(x);
				x *= 10.0;
				result += Long.toString((long)x);
			}
		}
		
		return result;
	}

}
