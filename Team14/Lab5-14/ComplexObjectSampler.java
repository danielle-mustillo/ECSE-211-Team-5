import lejos.nxt.LCD;
import lejos.util.Timer;
import lejos.util.TimerListener;

public class ComplexObjectSampler implements TimerListener{
	private SwitchBoard switches;
	private int detectionRange;
	private Timer sampler;

	public ComplexObjectSampler(SwitchBoard switches) {
		this.switches = switches;
		this.detectionRange = 30;
		this.sampler = new Timer(500, this);
	}

	@Override
	public void timedOut() {
		int dist = switches.getUSReading();
		double colReading = switches.getCSReading();
		LCD.drawString("US:               ", 0, 1);
		LCD.drawInt(dist, 3, 1);
		LCD.drawString("RBp:              ", 0, 3);
		LCD.drawString(String.valueOf(colReading), 4, 3);
		
		if(dist > detectionRange) {
			LCD.drawString("Bring obj closer             ", 0, 0);
		} else
			if(colReading > 1.15) {
				LCD.drawString("OBJECT DETECTED       ", 0, 0);
				switches.setHasFoundBlueBlock(false);
				switches.setActivity(CurrentActivity.BACKING_AWAY_FROM_OBSTACLE); 
			}
			else {
				LCD.drawString("BUILDING BLOCK DETECTED    ", 0, 0);
				switches.setHasFoundBlueBlock(true);
				switches.setActivity(CurrentActivity.PICKUP_OBJECT); //TODO implement the necessary 
			}
	}
	
	public void start() {
		sampler.start();
	}
	
	public void stop() {
		sampler.stop();
	}

}
