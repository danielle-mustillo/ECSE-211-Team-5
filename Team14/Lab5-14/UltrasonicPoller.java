import lejos.nxt.UltrasonicSensor;
import lejos.util.Timer;
import lejos.util.TimerListener;

/* This class simply functions as a way to synchronize the ultrasonic readings across all classes that use this.
 * It is now called the USPoller since it is an autonomous Ultrasonic Sensor.
 */

public class UltrasonicPoller implements TimerListener{
	private UltrasonicSensor us;
	private SwitchBoard switches;
	private Timer poller;

	public UltrasonicPoller(UltrasonicSensor us, SwitchBoard switches) {
		this.us = us;
		this.switches = switches;
		this.poller = new Timer(100, this);
		this.poller.start();
	}

	@Override
	public void timedOut() {
		us.ping();
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {}
		int dist = us.getDistance();
		if(dist > 50) dist = 50;
		switches.addUSReading(dist);
	}
}
