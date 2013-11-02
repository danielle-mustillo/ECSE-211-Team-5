import java.util.ArrayList;
import java.util.List;

import lejos.nxt.ColorSensor;
import lejos.nxt.comm.RConsole;
import lejos.util.Timer;
import lejos.util.TimerListener;


public class LightPoller implements TimerListener {

	private static final int CORRECTION_PERIOD = 20;
	
	private ColorSensor ls;
	
	//store light sensor values (for filtering purposes)
	private List<Integer> lsValues = new ArrayList<Integer>();
	
	private Timer lsTimer;
	
	private boolean started;
	
	private Object lock;
	
	private int floodLightColor = 0;

	
	public LightPoller(ColorSensor ls) {
		this.ls = ls;
		this.lsTimer = new Timer(CORRECTION_PERIOD, this);
		this.lock = new Object();
		ls.setFloodlight(floodLightColor);
	}
	
	public void timedOut() {
		
		int value = ls.getRawLightValue();
		RConsole.println(String.valueOf(value));
		synchronized (lock) {
			lsValues.add(value / 2);
			
			if(lsValues.size()>3) {	
				lsValues.remove(0);
			}
		}
	}
	
	public void start() {
		if(!started) {
			lsTimer.start();
			started = true;
		}		
	}
	
	public void stop() {
		if(started) {
			lsTimer.stop();
			started = false;
		}
	}
	
	public void setFloodLight(int color) {
		//this.stop();
		ls.setFloodlight(color);		
		floodLightColor = color;
		
		while (ls.getRawLightValue() == -1) {
			try {
				Thread.sleep(250);
			} catch (Exception e) {
			}
			RConsole.println(String.valueOf(ls.getFloodlight()));
			ls.setFloodlight(color);
		}
		
		synchronized (lock) {
			lsValues.clear();
		}
		
		//this.start();
		
	}
	
	public int differenceFilter() {
		
		int result;
		
		synchronized (lock) {
			if(lsValues.size()<3) {
				result =  lsValues.get(lsValues.size()-1);
			} else {
				// Smooth and difference
				result =  -lsValues.get(0) - lsValues.get(1) + lsValues.get(2) + lsValues.get(3);
			}
		}
		
		return result;
	}
	
	public int smoothingFilter() {
		int result;
		
		synchronized (lock) {
			if(lsValues.size() >= 4 ) {
				// Smooth
				result = lsValues.get(2) + lsValues.get(3);
			} else if(lsValues.size() > 1) {
				result =  lsValues.get(lsValues.size()-1) + lsValues.get(lsValues.size()-2);
			} else if (lsValues.size() == 1) {
				result = lsValues.get(0) * 2;
			} else {
				result = ls.getRawLightValue();
			}
		}
		
		return result;
	}
	
	public int calibrateSensor() {
		
		setFloodLight(0);
		
		return smoothingFilter();		
	}
	
	public int getLightValue() {
		return ls.getRawLightValue();
	}
	
	
}
