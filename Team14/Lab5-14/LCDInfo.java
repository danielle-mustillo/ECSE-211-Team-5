/* Danielle Mustillo, Nathaniel Anthonisen
 * 260533476, 260470621
 * ECSE 211 - Design Principles and Methods
 * October 08, 2013
 */
/*
 *LCDinfo.java
 */

import lejos.nxt.LCD;
import lejos.nxt.comm.RConsole;
import lejos.util.Timer;
import lejos.util.TimerListener;

public class LCDInfo implements TimerListener{
	public static final int LCD_REFRESH = 100;
	private Odometer odo;
	private SwitchBoard switches;
	private Timer lcdTimer;
	
	public LCDInfo(Odometer odo, SwitchBoard switches) {
		this.odo = odo;
		this.switches = switches;
		RConsole.openUSB(10000);
		// start the timer
		updateDisplay();
	}
	
	public void updateDisplay() {
		this.lcdTimer = new Timer(LCD_REFRESH, this);
		lcdTimer.start();
	}
	
	public void stopDisplay() {
		lcdTimer.stop();
		this.lcdTimer=null;
		LCD.clear();
	}
	
	public void timedOut() { 
		LCD.clear();
		LCD.drawString("X:           ", 0, 0);
		LCD.drawString("Y:           ", 0, 1);
		LCD.drawString("H:           ", 0, 2);
		LCD.drawString("US:          ", 0, 3);
		LCD.drawString("LS:          ", 0, 4);
		LCD.drawString("Act:         ", 0, 5);
		LCD.drawString("                           ", 0, 6);
		LCD.drawInt((int)odo.getX()*10, 5, 0);
		LCD.drawInt((int)odo.getY()*10, 5, 1);
		LCD.drawInt((int)odo.getTheta()*10, 5, 2);
		LCD.drawInt((int)switches.getUSReading(), 5, 3);
		LCD.drawInt((int)switches.getCSReading()*100, 5, 4);
		LCD.drawString(switches.getActivity().name(), 0, 6);
	}
}
