package hardwareAbstraction;

import utilities.Settings;
import lejos.nxt.ColorSensor;
import lejos.robotics.Color;
import lejos.util.Timer;
import lejos.util.TimerListener;

/**
 * This class is the hardware abstraction for the ColorSensor with additonal
 * functionality built in. The colorPoller will never have values below 0 during
 * normal operation. Therefore, negative numbers mean the colorPoller has not
 * been setup.
 * 
 * @author danielle
 * 
 */
public class ColorPoller implements TimerListener {
	/**
	 * The front colour sensor (on the claw)
	 */
	private ColorSensor cs;
	/**
	 * Timer for timer listener
	 */
	private Timer poller;
	/**
	 * Default poleRate, set to 30ms
	 */
	private int poleRate = 30;
	/**
	 * Array of past readings
	 */
	private int[] readings;

	/**
	 * Initializes colour sensor to the one defined in {@link Settings}
	 * Also, initializes readings array
	 */
	public ColorPoller() {
		this.cs = Settings.frontColorSensor;
		this.readings = new int[5];
		this.resetCP();
	}
	/**
	 * Starts polling the colour sensor
	 */
	public void start() {
		this.poller = new Timer(poleRate, this);
		this.resetCP();
		this.poller.start();
	}
	/**
	 * Stops polling the colour sensor
	 */
	public void stop() {
		this.poller.stop();
		this.poller = null;
	}
	/**
	 * Polls the colour sensor using {@link ColorSensor.getColor()}, and adds the ratio of the red to blue value to the readings array
	 */
	@Override
	public void timedOut() {
		int red, blue;
		Color color = cs.getColor();
		red = color.getRed();
		blue = color.getBlue();
		double proportion = ((double) red ) / blue;
		proportion *= 100;
		addReading((int) proportion);
	}

	/**
	 * Adds a new reading to the readings array by shifting the older readings to a higher index and storing the new reading in the 0th index.  The oldest reading is removed.
	 * @param shiftedProportion - new reading
	 */
	private void addReading(int shiftedProportion) {
		readings[4] = readings[3];
		readings[3] = readings[2];
		readings[2] = readings[1];
		readings[1] = readings[0];
		readings[0] = shiftedProportion;
	}

	/**
	 * This method resets the color poller to a default value which is -1
	 * everywhere. The colorPoller will never have values below 0 during normal
	 * operation. Therefore, negative numbers mean the colorPoller has not been
	 * setup.
	 */
	private void resetCP() {
		for (int i = 0; i < readings.length; ++i)
			readings[i] = -1;
	}

	/**
	 * This method will identify whether the colorPoller has been setup or not. 
	 * @return	True if the colorPoller is setup, else false.
	 */
	public boolean isSetup() {
		if (readings[4] < 0)
			return false;
		else
			return true;
	}
	
	/**
	 * This method will average the past five readings.  
	 * Then if the average is <= 0.9 it will return that an {@link ObjectDetected.OBSTACLE} was detected.  
	 * Otherwise it will return with {@link ObjectDetected.BLUE_BLOCK}
	 * @return type of block detected
	 */
	public ObjectDetected getObjectReading() {
		double average = ( readings[4] + readings[3] + readings[2] + readings[1] + readings[0] ) / 5;
		return average <= 0.8  ? ObjectDetected.OBSTACLE : ObjectDetected.BLUE_BLOCK;
	}
	
	/**
	 * Enum of the two types of blocks.  Either Obstacle or blue block
	 * @author Danielle
	 *
	 */
	public enum ObjectDetected {
		OBSTACLE, BLUE_BLOCK
	}
}