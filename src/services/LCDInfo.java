package services;

import utilities.Angle;
import utilities.Position;
import lejos.nxt.LCD;
import lejos.util.Timer;
import lejos.util.TimerListener;
import manager.Manager;

public class LCDInfo implements TimerListener{
	public static final int LCD_REFRESH = 100;
	private Manager manager;
	private Timer lcdTimer;
	
	/**
	 * LCDInfo constructor
	 * @param odo
	 */
	public LCDInfo(Manager manager) {
		this.manager = manager;
		this.lcdTimer = new Timer(LCD_REFRESH, this);
		// start the timer
		lcdTimer.start();
	}
	
	/**
	 * main update loop
	 */
	public void timedOut() { 
		Position pos = manager.sm.odo.getPosition();
		LCD.clear();
		LCD.drawString("X: ", 0, 0);
		LCD.drawString("Y: ", 0, 1);
		LCD.drawString("H: ", 0, 2);
		LCD.drawInt((int)(pos.x * 10), 3, 0);
		LCD.drawInt((int)(pos.y * 10), 3, 1);
		LCD.drawInt(Angle.radToDeg(pos.theta), 3, 2);
	}

}
