/* Danielle Mustillo, Nathaniel Anthonisen
 * 260533476, 260470621
 * ECSE 211 - Design Principles and Methods
 * October 22, 2013
 */
/*
 * UltrasonicPoller.java
 */
import lejos.nxt.UltrasonicSensor;
import lejos.util.Timer;
import lejos.util.TimerListener;

/**This class simply functions as a way to synchronize the ultrasonic readings across all classes that use this.
 * It is now called the USPoller since it is an autonomous Ultrasonic Sensor.
 */

public class UltrasonicPoller implements TimerListener{
	private UltrasonicSensor us;
	private SwitchBoard switches;
	private Timer poller;
	private int pollRate;
	private boolean read255 = false;

	public UltrasonicPoller(UltrasonicSensor us, SwitchBoard switches) {
		this.us = us;
		this.switches = switches;
		this.pollRate=50;
	}

	/**Collect the data from the US and put that in the switchboard)
	 */
	@Override
	public void timedOut() {
		us.ping();
		try {
			Thread.sleep(20);
		} catch (InterruptedException e) {}
		int dist = us.getDistance();
		
		//filters out misreads by ignoring spuritous 255 values.
		if(dist == 255 && !read255) read255 = true;
		else {
			if(dist < 200) read255 = false;
			switches.addUSReading(dist);
		}
	}
	
	public void start() {
		this.poller = new Timer(pollRate, this);
		this.poller.start();
	}
	
	public void stop() {
		this.poller = null;
	}
	
}
