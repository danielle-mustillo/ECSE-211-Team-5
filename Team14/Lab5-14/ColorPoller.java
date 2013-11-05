import lejos.nxt.ColorSensor;
import lejos.robotics.Color;
import lejos.util.Timer;
import lejos.util.TimerListener;


public class ColorPoller implements TimerListener {
	private ColorSensor cs;
	private SwitchBoard switches;
	private Timer poller;

	public ColorPoller(ColorSensor cs, SwitchBoard switches) {
		this.cs = cs;
		this.switches = switches;
		this.poller = new Timer(250, this);
		this.poller.start();
	}

	@Override
	public void timedOut() {
		int red, blue;
		cs.setFloodlight(Color.RED);
		nap();
		red = cs.getRawLightValue();
		
		cs.setFloodlight(Color.BLUE);
		nap();
		blue = cs.getRawLightValue();
		
		switches.addCSReading((double)red/blue);
	}
	
	public void nap() {
		try {
			Thread.sleep(1);
		} catch (InterruptedException e1) {
		}
	}

}
