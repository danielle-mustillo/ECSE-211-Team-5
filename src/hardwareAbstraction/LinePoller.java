package hardwareAbstraction;

import lejos.nxt.ColorSensor;
import lejos.nxt.Sound;
import lejos.nxt.comm.RConsole;
import lejos.util.Timer;
import lejos.util.TimerListener;
import utilities.Settings;

/**
 * Detects grid lines with two rear floor facing color sensors
 * other threads should call enteringLine to know when a new line has been detected
 * @author Riley
 *
 */

public class LinePoller implements TimerListener {

	private final int UPDATE_PERIOD = 15;
	private final int THRESHOLD = 50;
	private boolean[] sensorOnLine;
	private boolean[] sensorEnteringLine;
	private ColorSensor[] sensor = new ColorSensor[2];
	private Timer timer;
	public int[][] readings;
	private int left = 0;
	private int right = 1;
	
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
	
	public void timedOut() {
		addReading(left, sensor[left].getRawLightValue()/2);
		addReading(right, sensor[right].getRawLightValue()/2);
		
		detectLine(right);
		detectLine(left);
	}
	
	private void addReading(int sensor, int reading) {
		readings[sensor][3] = readings[sensor][2];
		readings[sensor][2] = readings[sensor][1];
		readings[sensor][1] = readings[sensor][0];
		readings[sensor][0] = reading;
	}
	
	/**
	 * Filters and differences the data
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
			Sound.beep();
		}
		//if we are on a line and filter result > 45 we have now left the line
		else if(sensorOnLine[sensor] && result > THRESHOLD) {
			sensorOnLine[sensor] = false;
			//in the event that no other thread was running at the time
			sensorEnteringLine[sensor] = false;
		}
	}
		
	/**
	 * Returns true if the sensor has just detected a black line
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
