package hardwareAbstraction;

import lejos.nxt.ColorSensor;
import lejos.nxt.Sound;
import lejos.nxt.comm.RConsole;
import lejos.util.Timer;
import lejos.util.TimerListener;
import utilities.Settings;

/**
 * Detects grid lines with two rear floor facing color sensors
 * other threads should call {@link enteringLine} to know when a new line has been detected
 * <p>
 * Uses a mean and difference filter to detect the lines.  Once {@link enteringLine} has been called, 
 * its control variable will be reset and it will no longer return true until a new line has been crossed
 * @author Riley
 *
 */

public class LinePoller implements TimerListener {

	/**
	 * Period to update the sensors and run the detection algorithm.  default 15 ms
	 */
	private final int UPDATE_PERIOD = 15;
	/**
	 * Threshold for the detection algorithm.  Most lines are well above 70, and maximum noise is less than 40
	 */
	private final int THRESHOLD = 50;
	/**
	 * boolean variable for storing whether a sensor is on a line or not
	 */
	private boolean[] sensorOnLine;
	/**
	 * boolean variable for keeping track of whether a sensor has entered a new line.  This is used by {@link enteringLine}
	 */
	private boolean[] sensorEnteringLine;
	/**
	 * Array of line Color sensors
	 */
	private ColorSensor[] sensor = new ColorSensor[2];
	/**
	 * Timer for timer listener
	 */
	private Timer timer;
	/**
	 * Array of readings. stores 4 readings for each sensor
	 */
	public int[][] readings;
	/**
	 * identifier for the left sensor
	 */
	private int left = 0;
	/**
	 * identifier for the right sensor
	 */
	private int right = 1;
	
	/**
	 * Initializes the two color sensors as specified in {@link Settings}
	 * as well it initializes the arrays required.
	 * <p>
	 * it sets the floodlight to Red for each sensor, and starts the timer
	 */
	public LinePoller() {
		sensor[right] = Settings.rearRightColorSensor;
		sensor[left] = Settings.rearLeftColorSensor;
		readings = new int[2][4];
		sensorOnLine = new boolean[]{false, false};
		sensorEnteringLine = new boolean[]{false, false};
		timer = new Timer(UPDATE_PERIOD, this);
		setFloodlight(left, 0);
		setFloodlight(right, 0);
		
		start();
	}
	
	/**
	 * Starts line polling
	 * And populates array with readings
	 */
	public void start() {		
		//Populate Readings Array
		addReading(left, sensor[left].getRawLightValue()/2);
		addReading(right, sensor[right].getRawLightValue()/2);
		addReading(left, sensor[left].getRawLightValue()/2);
		addReading(right, sensor[right].getRawLightValue()/2);
		addReading(left, sensor[left].getRawLightValue()/2);
		addReading(right, sensor[right].getRawLightValue()/2);
		addReading(left, sensor[left].getRawLightValue()/2);
		addReading(right, sensor[right].getRawLightValue()/2);
		
		timer.start();
	}
	
	/**
	 * Takes new {@link getRawLightValue() } reading and stores it in the readings array by calling {@link addReading()}
	 * <p>
	 * then calls {@link detectLine() } for each sensor
	 */
	public void timedOut() {
		addReading(left, sensor[left].getRawLightValue()/2);
		addReading(right, sensor[right].getRawLightValue()/2);
		
		detectLine(right);
		detectLine(left);
	}
	
	/**
	 * Adds a new reading to the readings array for the passed sensor.  it will be put in the 0th index.
	 * @param sensor id of the sensor the reading was from
	 * @param reading value of reading
	 */
	private void addReading(int sensor, int reading) {
		readings[sensor][3] = readings[sensor][2];
		readings[sensor][2] = readings[sensor][1];
		readings[sensor][1] = readings[sensor][0];
		readings[sensor][0] = reading;
	}
	
	/**
	 * Uses mean and difference filtering to detect a line.
	 * <p>
	 * if a new line is detected (using {@link THRESHOLD}), it will set {@link sensorOnLine[sensor] } to true and {@link sensorEnteringLine[sensor]} to true 
	 * <p>
	 * if the end of a line was detected it will reset {@link sensorOnLine[sensor] } and {@link sensorEnteringLine[sensor]} to false.
	 * @param sensor
	 */
	private void detectLine(int sensor) {
		int result;
		
		// Smooth and difference
		result = -readings[sensor][3] - readings[sensor][2] + readings[sensor][1] + readings[sensor][0];
		
		//if not currently on a line and filter result is less than -45 we just enter a line			
		if(!sensorOnLine[sensor] && result < -THRESHOLD) {
			sensorOnLine[sensor] = true;
			sensorEnteringLine[sensor] = true;
			//alert us with a beep that a line was detected
			
		}
		//if we are on a line and filter result > 45 we have now left the line
		else if(sensorOnLine[sensor] && result > THRESHOLD) {
			sensorOnLine[sensor] = false;
			//in the event that no other thread was running at the time
			sensorEnteringLine[sensor] = false;
		}
	}
		
	/**
	 * Returns true if the sensor has detected a new black line, 
	 * is still on the line and the method has not yet been called since the line's detction.
	 * Once called and if {@link sensorEnteringLine[sensor]} is true, 
	 * it will reset {@link sensorEnteringLine[sensor]}.
	 * 
	 * @param sensor | left = 0 & right = 1
	 * @return
	 */
	public boolean enteringLine(int sensor) {
		if(sensorEnteringLine[sensor]) {
			//reset so the line is only counted once;
			sensorEnteringLine[sensor] = false;
			return true;
		}
		return false;
	}
	/**
	 * Sets the floodlight color of the color sensor.  
	 * It will make sure the floodlight is set by ensuring {@link getRawLightValue} does not return -1.  
	 * The method will not return until the sensor outputs valid data (i.e. not -1)
	 * @param sensor
	 * @param color
	 */
	public void setFloodlight(int sensor, int color) {
		this.sensor[sensor].setFloodlight(color);
		do {
			nap(75);
		} while(this.sensor[sensor].getRawLightValue() == -1);
	}
	
	/** Helper method to avoid large try/catch blocks. Sleeps the current thread. 
	 * @param time int value which represents the sleep time
	 */
	public void nap(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
