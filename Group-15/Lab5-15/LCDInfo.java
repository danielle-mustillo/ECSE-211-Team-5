import lejos.nxt.LCD;
import lejos.util.Timer;
import lejos.util.TimerListener;

/**
 * 
 * @project Lab 4 Localization 
 * @names Riley van Ryswyk & Aditya Saha
 * @studentID 260447357 & 260453165
 * @group 15
 * @course ECSE 211 
 * @date 06/10/2013
 * 
 * LCD display (from lab 4)
 */

public class LCDInfo implements TimerListener{
	public static final int LCD_REFRESH = 100;
	private Odometer odo;
	private Timer lcdTimer;
	public enum BlockType { STYROFOAM, WOOD, NONE };
	private BlockType type;
	private boolean blockDetected = false;
	
	// arrays for displaying data
	private double [] pos;
	
	/**
	 * LCDInfo constructor
	 * @param odo
	 */
	public LCDInfo(Odometer odo) {
		this.odo = odo;
		this.lcdTimer = new Timer(LCD_REFRESH, this);
		
		// initialise the arrays for displaying data
		pos = new double [3];
		
		// start the timer
		lcdTimer.start();
	}
	
	/**
	 * main update loop
	 */
	public void timedOut() { 
		odo.getPosition(pos);
		LCD.clear();
		LCD.drawString("X: ", 0, 0);
		LCD.drawString("Y: ", 0, 1);
		LCD.drawString("H: ", 0, 2);
		LCD.drawInt((int)(pos[0] * 10), 3, 0);
		LCD.drawInt((int)(pos[1] * 10), 3, 1);
		LCD.drawInt((int)pos[2], 3, 2);
		
		if(blockDetected) {
			LCD.drawString(" Block Detected ", 0, 3);
		}
		
		if(type == BlockType.STYROFOAM) {
			LCD.drawString(" Styrofoam ", 0, 4);
		} else if(type == BlockType.WOOD) {
			LCD.drawString(" Wood ", 0, 4);
		}
	}
	
	public void type(BlockType type) {
		this.type = type;
	}
	
	public void blockDetected(boolean detected) {
		blockDetected = detected;
		if(detected = false) {
			this.type = BlockType.NONE;
		}
	}
}
