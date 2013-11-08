package hardwareAbstraction;

import java.util.Arrays;

import lejos.nxt.UltrasonicSensor;
import lejos.util.Timer;
import lejos.util.TimerListener;

/**This class serves as a higher level abstraction of a ultrasonic sensor. It is
 * wrapped in a poller class which will now handle the returned values and the
 * polling of that sensor
 * <p>
 * @author danielle
 */
public class UltrasonicPoller implements TimerListener {
	private UltrasonicSensor[] us = {utilities.Settings.leftUltrasonic, utilities.Settings.centerUltrasonic, utilities.Settings.rightUltrasonic};
	public int pollRate;
	private Timer poller;
	private int readings[][];
	private boolean running = false;

	// TODO figure out what exactly this constructor should be.
	public UltrasonicPoller() {
		this.pollRate = 250;
		this.readings = new int[3][5];
		start();
	}

	/**Pings all three ultrasonic sensors and gets their values. Puts them into the readings array
	 */
	@Override
	public void timedOut() {
		//ping left sensor
		us[0].ping();
		addReading(us[0].getDistance(), 0);
		// ping center sensor
		us[1].ping();
		addReading(us[1].getDistance(), 1);
		//ping right sensor
		us[2].ping();
		addReading(us[2].getDistance(), 2);
	}

	/**
	 * Starts this instance of the ultrasonic poller Stop must be called to stop
	 * the reading again.
	 */
	public void start() {
		this.poller = new Timer(pollRate, this);
		this.poller.start();
		running = true;
	}

	/**
	 * Stops this instance of the ultrasonic poller Start must be called to
	 * start reading again.
	 */
	public void stop() {
		this.poller = null;
		running = false;
	}
	
	public int getUSReading(int sensor) {
		return 20;
	}
	

	/**
	 * gets the lowest reading in the ultrasonicPoller at that time. Readings
	 * are not taken temporarily as they are not needed;
	 * <p>
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
		int minValue = readings[0][0]; //get a value to start
		for (int usReadings[] : readings) {
			int i = 0;
			for(int reading : usReadings) {
				if(minValue > reading)
					minValue = reading;
				++i;
				}
		}

		// start the readings again if the robot was taking readings before.
		if (takingReadings)
			start();
		return minValue;

	}

	//helper method. 
	private void addReading(int reading, int sensor) {
		readings[sensor][4] = readings[sensor][3];
		readings[sensor][3] = readings[sensor][2];
		readings[sensor][2] = readings[sensor][1];
		readings[sensor][1] = readings[sensor][0];
		readings[sensor][0] = reading;
	}
}
