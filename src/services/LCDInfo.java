package services;

import utilities.Angle;
import utilities.Point;
import utilities.Position;
import lejos.nxt.LCD;
import lejos.util.Timer;
import lejos.util.TimerListener;
import manager.Manager;
/**
 * Prints Information to the LCD display of the NXT. This includes {@link Odometer} x, y, theta; a debug value, and the next point in the Route of {@link Navigation}.
 * 
 * It is a modified version of the code supplied in Lab 4
 * @author Riley
 *
 */
public class LCDInfo implements TimerListener{
	public static final int LCD_REFRESH = 300;
	private Manager manager;
	private Timer lcdTimer;
	public double debugValue = Double.NaN;
	
	/**
	 * LCDInfo constructor
	 * @param manager 
	 */
	public LCDInfo(Manager manager) {
		this.manager = manager;
		this.lcdTimer = new Timer(LCD_REFRESH, this);
		// start the timer
		lcdTimer.start();
	}
	
	/**
	 * Main update loop. 
	 * Updates the Odometer values, debug value, and next Point in the route in {@link Navigation} value on the LCD screen
	 * <p>
	 *  x, y are in mm.  Theta is in degrees
	 */
	public void timedOut() { 
//		Position pos = manager.sm.odo.getPosition();
//		LCD.clear();
//		LCD.drawString("X: ", 0, 0);
//		LCD.drawString("Y: ", 0, 1);
//		LCD.drawString("H: ", 0, 2);
//		LCD.drawInt((int)(pos.x * 10), 3, 0);
//		LCD.drawInt((int)(pos.y * 10), 3, 1);
//		LCD.drawInt(Angle.radToDeg(pos.theta), 3, 2);
//		
//		/*LCD.drawString("L: ", 0, 4);
//		LCD.drawString("C: ", 0, 5);
//		LCD.drawString("R: ", 0, 6);
//		LCD.drawInt(manager.hm.ultrasonicPoller.getUSReading(0), 3, 4);
//		LCD.drawInt(manager.hm.ultrasonicPoller.getUSReading(1), 3, 5);
//		LCD.drawInt(manager.hm.ultrasonicPoller.getUSReading(2), 3, 6);
//		*/
//		LCD.drawString("D: ", 0, 7);
//		LCD.drawString(String.valueOf(debugValue), 3, 7);
//		
//		if(!manager.sm.nav.getRoute().empty()) {
//			Point next = manager.sm.nav.getRoute().peek();
//			
//			LCD.drawString("PX: ", 8, 0);
//			LCD.drawString("PY: ", 8, 1);
//			LCD.drawInt((int)(next.x * 10), 11, 0);
//			LCD.drawInt((int)(next.y * 10), 11, 1);
//		}
		
	}

}
