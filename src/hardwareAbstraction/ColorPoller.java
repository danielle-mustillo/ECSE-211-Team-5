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
	private ColorSensor cs;
	private Timer poller;
	private int poleRate = 30;
	private int[] readings;

	public ColorPoller() {
		this.cs = Settings.frontColorSensor;
		this.readings = new int[5];
		this.resetCP();
	}

	public void start() {
		this.poller = new Timer(poleRate, this);
		this.resetCP();
		this.poller.start();
	}

	public void stop() {
		this.poller.stop();
		this.poller = null;
	}

	@Override
	public void timedOut() {
		int red, blue;
		Color color = cs.getColor();
		red = color.getRed();
		blue = color.getBlue();
		double proportion = (double) red / blue;
		proportion *= 100;
		addReading((int) proportion);
	}

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
	
	public ObjectDetected getObjectReading() {
		double average = ( readings[4] + readings[3] + readings[2] + readings[1] + readings[0] ) / 5;
		return average <= 0.9  ? ObjectDetected.OBSTACLE : ObjectDetected.BLUE_BLOCK;
	}
	
	public enum ObjectDetected {
		OBSTACLE, BLUE_BLOCK
	}
}