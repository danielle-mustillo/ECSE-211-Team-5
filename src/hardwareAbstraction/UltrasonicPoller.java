package hardwareAbstraction;

import utilities.Settings;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.comm.RConsole;
import lejos.util.Timer;
import lejos.util.TimerListener;

/**This class serves as a higher level abstraction of a ultrasonic sensor. It is
 * wrapped in a poller class which will now handle the returned values and the
 * polling of that sensor
 * <p>
 * @author danielle, Riley
 */
public class UltrasonicPoller implements TimerListener {
	private UltrasonicSensor[] us =  new UltrasonicSensor[3];
	public int pollRate;
	private Timer poller;
	private int readings[][];
	private boolean running = false;
	private int left = 0;
	private int center = 1;
	private int right = 2;
	private int counter;

	// TODO figure out what exactly this constructor should be.
	public UltrasonicPoller() {
		us[left] = Settings.leftUltrasonic;
		us[center] = Settings.centerUltrasonic;
		us[right] = Settings.rightUltrasonic;

		this.pollRate = 25;
		this.readings = new int[3][5];
		
		us[left].off();
		us[center].off();
		us[right].off();
		
		start();
	}

	/**Pings all three ultrasonic sensors and gets their values. Puts them into the readings array
	 */
	@Override
	public void timedOut() {
		
		if(counter == left) {
			pingUS(left);
		} else if(counter == center) {
			pingUS(center);
		} else {
			pingUS(right);
		}
		
		counter++;
		//keep the counter between 0 - 2
		counter = counter % 3;
	}

	/**
	 * Starts this instance of the ultrasonic poller Stop must be called to stop
	 * the reading again.
	 */
	public void start() {
		counter = 0;
		//for filtering purposes
		readings[2][4] = -1;
		readings[2][3] = -1;
		readings[2][2] = -1;
		readings[2][1] = -1;
		readings[2][0] = -1;
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
	
	/**
	 * Returns the filtered data for the sensor (median filtering)
	 * @param sensor
	 * @return
	 */
	public int getUSReading(int sensor) {
		
		// makes sure readings array is full of values so we have enough to filter with
		if(readings[2][4] > -1) {
			
			//initialize vars
			int size = 5;
			int[] usReadingsSorted = new int[5];
			//Copy array
			System.arraycopy(readings[sensor], 0, usReadingsSorted, 0, 5);

			//sort the values: lowest to highest
			for(int i=0; i<size; i++) {
				for(int j=i+1; j<size;j++) {
					if(usReadingsSorted[i] > usReadingsSorted[j]) {
						int temp = usReadingsSorted[i];
						usReadingsSorted[i] = usReadingsSorted[j];
						usReadingsSorted[j] = temp;					
					}
				}
			}
			
			//return the median
			return usReadingsSorted[2];			
			
		} else {
			return readings[sensor][0];
		}
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
	
	/**
	 * Pings ultrasonic sensor and records the result in readings
	 */
	private void pingUS(int sensor) {
		int distance;

		// do a ping
		us[sensor].ping();
		
		// wait for the ping to complete
		try { Thread.sleep(20); } catch (InterruptedException e) {}
		
		// there will be a delay here
		distance = us[sensor].getDistance();

		addReading(sensor, distance);
	}

	//helper method. 
	private void addReading(int sensor, int reading) {
		readings[sensor][4] = readings[sensor][3];
		readings[sensor][3] = readings[sensor][2];
		readings[sensor][2] = readings[sensor][1];
		readings[sensor][1] = readings[sensor][0];
		readings[sensor][0] = reading;
	}
}
