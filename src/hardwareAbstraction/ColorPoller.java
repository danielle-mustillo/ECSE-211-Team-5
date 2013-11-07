package hardwareAbstraction;

import utilities.Settings;
import lejos.nxt.ColorSensor;
import lejos.robotics.Color;
import lejos.util.Timer;
import lejos.util.TimerListener;

public class ColorPoller implements TimerListener {
	private ColorSensor cs;
	private Timer poller;
	private int poleRate = 30;
	private int[] readings;

	public ColorPoller() {
		this.cs = Settings.frontColorSensor;
		this.readings = new int[5];
		this.poller = new Timer(poleRate, this);
	}
	
	public void start() {
		this.poller = new Timer(poleRate, this);
		this.poller.start();
	}
	
	public void stop() {
		this.poller = null;
	}

	@Override
	public void timedOut() {
		int red, blue;
		Color color = cs.getColor();
		red = color.getRed();
		blue = color.getBlue();
		double proportion = (double)red/blue;
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
}