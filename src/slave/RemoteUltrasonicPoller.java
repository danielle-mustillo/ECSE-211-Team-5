package slave;

import hardwareAbstraction.UltrasonicMotor;
import controllers.State;
import utilities.Settings;
import lejos.nxt.LCD;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.comm.RConsole;
import lejos.util.Timer;
import lejos.util.TimerListener;
import manager.Manager;

/**
 * This class serves as a higher level abstraction of a ultrasonic sensor. It is
 * wrapped in a poller class which will now handle the returned values and the
 * polling of that sensor
 * <p>
 * 
 * @author Danielle
 * @author Riley
 */
public class RemoteUltrasonicPoller implements TimerListener {
	private UltrasonicSensor[] us = new UltrasonicSensor[3];
	
	//properties of the USPoller
	private int pollRate;
	private Timer poller;
	private int readings[][];
	
	//internal designation of the sensors
	private int left = 0;
	private int center = 1;
	private int right = 2;
	
	// internal use of a counter for sequential pinging.
	private int counter;

	// the threads which run the US.
	private Thread leftUS;
	private Thread centerUS;
	private Thread rightUS;
	
	// the instruction to the USP.
	public USPState state;
	
	public enum USPState {
		PING_CENTER, PING_ALL, PING_LEFT, PING_RIGHT, PING_SEQUENTIAL;
	}

	public RemoteUltrasonicPoller() {
		us[left] = new UltrasonicSensor(SensorPort.S3);
		us[center] = new UltrasonicSensor(SensorPort.S1);
		us[right] = new UltrasonicSensor(SensorPort.S2);

		this.pollRate = 100;
		this.readings = new int[3][5];

		us[left].off();
		us[center].off();
		us[right].off();

		this.leftUS = new Thread(new LeftUS());
		this.centerUS = new Thread(new CenterUS());
		this.rightUS = new Thread(new RightUS());
		
		this.state = USPState.PING_ALL;
		
		this.start();
	}
	
	public void setUSPState(USPState state) {
		this.state = state;
	}

	/**
	 * Pings all three ultrasonic sensors and gets their values. Puts them into
	 * the readings array
	 */
	@Override
	public void timedOut() {
		
		/*
		 * Ensures minimal lag for ultrasonic localization 
		 */
		if(state == USPState.PING_CENTER) {
			centerUS.run();
		} else if(state == USPState.PING_LEFT) {
			leftUS.run();
		} else if(state == USPState.PING_RIGHT) {
			rightUS.run();
		} else if(state == USPState.PING_SEQUENTIAL) {
			switch(counter) {
			case 0 : leftUS.run();
			break;
			case 1 : centerUS.run();
			break;
			case 2 : rightUS.run();
			break;
			}
			counter += 1;
			counter = counter % 3;
		} else { //if state is PING_ALL
			centerUS.run();
			leftUS.run();
			rightUS.run();
		}
	}

	// For debugging purposes.
	private String toStringLastValues() {
		String out = "";
		out += " L: " + getUSReading(left);
		out += " C: " + getUSReading(center);
		out += " R: " + getUSReading(right);
		return out;
	}

	/**
	 * Starts this instance of the ultrasonic poller Stop must be called to stop
	 * the reading again.
	 */
	public void start() {
		counter = 0;
		this.poller = new Timer(pollRate, this);
		this.poller.start();
	}

	/**
	 * Resets the ultrasonic sensor values to the default -1 values. The
	 * ultrasonic sensor will never return negative values during normal
	 * operation. Stops the polling during this operation to avoid overwriting
	 * good data.
	 * @bug throws null pointer exception. Has potentially been fixed. Needs to be tested.
	 */
	public void resetUSP() {
		this.stop();
		for (int i = 0; i < readings.length; ++i) {
			for (int j = 0; j < readings[1].length; ++j) {
				readings[i][j] = -1;
			}
		}
		this.start();
	}

	/**
	 * Checks if the ultrasonic sensor has collected atleast 5 values. It does
	 * this by checking for any negative numbers in the readings.
	 */
	public boolean isSetup() {
		if (readings[2][4] == -1 || readings[0][4] == -1 || readings[1][4] == -1)
			return false;
		else
			return true;
	}

	/**
	 * Stops this instance of the ultrasonic poller Start must be called to
	 * start reading again.
	 */
	public void stop() {
		this.poller.stop();
		this.poller = null;
	}

	/**
	 * Returns the filtered data for the sensor (median filtering)
	 * 
	 * @param sensor
	 * @return
	 */
	public int getUSReading(int sensor) {

		// makes sure readings array is full of values so we have enough to
		// filter with
		if (readings[sensor][4] > -1) {

			// initialize vars
			int size = 5;
			int[] usReadingsSorted = new int[5];
			// Copy array
			System.arraycopy(readings[sensor], 0, usReadingsSorted, 0, 5);

			// sort the values: lowest to highest
			for (int i = 0; i < size; i++) {
				for (int j = i + 1; j < size; j++) {
					if (usReadingsSorted[i] > usReadingsSorted[j]) {
						int temp = usReadingsSorted[i];
						usReadingsSorted[i] = usReadingsSorted[j];
						usReadingsSorted[j] = temp;
					}
				}
			}

			// return the median
			return usReadingsSorted[2];

		} else {
			return readings[sensor][0];
		}
	}

	/**
	 * gets the lowest reading in the ultrasonicPoller at that time. Readings
	 * are not taken temporarily as they are not needed;
	 * <p>
	 * 
	 * @return The smallest reading of the last 5 polls.
	 */
	public int getLowestReading() {
		// stop reading if the robot was taking readings.
		boolean takingReadings = false;
		if (this.poller != null) {
			stop();
			takingReadings = true;
		}

		// calculate median value by sorting the readings
		int minValue = readings[0][0]; // get a value to start
		for (int usReadings[] : readings) {
			for (int reading : usReadings) {
				if (minValue > reading && reading != -1)
					minValue = reading;
			}
		}

		// start the readings again if the robot was taking readings before.
		if (takingReadings)
			start();
		return minValue;
	}
	
	/**
	 * This method gets the number representation of the US with the lowest value out of all the ultrasonic sensor. 
	 * So if the center has the lowest reading, 
	 */
	public USPosition getLowestSensor() {
		boolean takingReadings = false;
		if (this.poller != null) {
			stop();
			takingReadings = true;
		}

		// calculate median value by sorting the readings
		int minValue = readings[0][0]; // get a value to start
		int sensor = 0;
		int smallestSensor = 0;
		for (int usReadings[] : readings) {
			for (int reading : usReadings) {
				if (minValue > reading && reading != -1) {
					minValue = reading;
					smallestSensor = sensor;
				}
			}
			sensor++;
		}

		// start the readings again if the robot was taking readings before.
		if (takingReadings)
			start();
		if(smallestSensor == left)
			return USPosition.LEFT;
		else if(smallestSensor == center)
			return USPosition.CENTER;
		else
			return USPosition.RIGHT;
	}

	/**
	 * Pings ultrasonic sensor and records the result in readings
	 */
	private void pingUS(int sensor) {
		int distance;

		// do a ping
		us[sensor].ping();

		// wait for the ping to complete
		try {
			Thread.sleep(20);
		} catch (InterruptedException e) {
		}

		// there will be a delay here
		distance = us[sensor].getDistance();

		addReading(sensor, distance);
	}

	// helper method.
	private void addReading(int sensor, int reading) {
		readings[sensor][4] = readings[sensor][3];
		readings[sensor][3] = readings[sensor][2];
		readings[sensor][2] = readings[sensor][1];
		readings[sensor][1] = readings[sensor][0];
		readings[sensor][0] = reading;
	}
	
	/**
	 * Computes the average values read by a sensor
	 * @param sensor	The integer value corresponding to the ultrasonic sensor
	 * @return The average integer value, in integer value.
	 */
	public int computeAverage(int sensor) {
		int sum = 0;
		for(int i = 0; i < readings[sensor].length; i++)
			sum += readings[sensor][i];
		return (sum / readings[sensor].length);
	}

	public class LeftUS implements Runnable {

		@Override
		public void run() {
			pingUS(left);
		}
	}

	public class RightUS implements Runnable {

		@Override
		public void run() {
			pingUS(right);
		}
	}

	public class CenterUS implements Runnable {

		@Override
		public void run() {
			pingUS(center);
		}
	}
	
	public enum USPosition {
		LEFT, CENTER, RIGHT
	}
}
