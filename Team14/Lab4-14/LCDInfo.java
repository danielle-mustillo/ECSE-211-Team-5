/* Danielle Mustillo, Nathaniel Anthonisen
 * 260533476, 260470621
 * ECSE 211 - Design Principles and Methods
 * October 08, 2013
 */
/*
 *LCDinfo.java
 */

import lejos.nxt.LCD;
import lejos.util.Timer;
import lejos.util.TimerListener;

public class LCDInfo implements TimerListener{
	public static final int LCD_REFRESH = 100;
	private Odometer odo;
	private Timer lcdTimer;
	private USLocalizer usl;
	
	// arrays for displaying data
	private double [] pos;
	
	public LCDInfo(Odometer odo, USLocalizer usl) {
		this.odo = odo;
		this.lcdTimer = new Timer(LCD_REFRESH, this);
		this.usl = usl;
		
		// initialise the arrays for displaying data
		pos = new double [3];
		
		// start the timer
		lcdTimer.start();
	}
	
	public void timedOut() { 
		odo.getPosition(pos);
		LCD.clear();
		LCD.drawString("X: ", 0, 0);
		LCD.drawString("Y: ", 0, 1);
		LCD.drawString("H: ", 0, 2);
		LCD.drawString("US: ", 0, 3);
		LCD.drawInt((int)(pos[0] * 10), 4, 0);
		LCD.drawInt((int)(pos[1] * 10), 4, 1);
		LCD.drawInt((int)pos[2], 4, 2);
		LCD.drawInt(usl.thisDistance, 4, 3);
	}
}
