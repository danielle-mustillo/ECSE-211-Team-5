/* Danielle Mustillo, Nathaniel Anthonisen
 * 260533476, 260470621
 * ECSE 211 - Design Principles and Methods
 * October 22, 2013
 */
/*
 *LCDinfo.java
 */

import lejos.nxt.LCD;
import lejos.nxt.comm.RConsole;
import lejos.util.Timer;
import lejos.util.TimerListener;
/**Displays on screen the user information. In this case, the screen is RConsole.*/
public class Info implements TimerListener{
	public static final int LCD_REFRESH = 500;
	private Odometer odo;
	private SwitchBoard switches;
	private Timer lcdTimer;
	
	public Info(Odometer odo, SwitchBoard switches) {
		this.odo = odo;
		this.switches = switches;
		RConsole.openUSB(10000);
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
		RConsole.println("");
		RConsole.println("X:    " + odo.getX());
		RConsole.println("Y:    " + odo.getY());
		RConsole.println("H:    " + odo.getTheta());
		RConsole.println("US:   " + switches.getUSReading());
		RConsole.println("LS:   " + switches.getCSReading());
		RConsole.println("Act:  " + switches.getActivity().name());

	}
}
