/* Danielle Mustillo, Nathaniel Anthonisen
 * 260533476, 260470621
 * ECSE 211 - Design Principles and Methods
 * October 22, 2013
 */
/*
 * LAb5Part1.java
 */

import lejos.nxt.Button;
import lejos.nxt.ColorSensor;
import lejos.nxt.LCD;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.comm.RConsole;
import lejos.robotics.Color;

public class Lab5Part1 {
	private static UltrasonicSensor us = new UltrasonicSensor(SensorPort.S1);
	private static ColorSensor cs = new ColorSensor(SensorPort.S4);
	
	public static void main(String[] args) {
		us = new UltrasonicSensor(SensorPort.S2);
		cs = new ColorSensor(SensorPort.S3);
		int distance = 0;
		RConsole.openUSB(10000);
		Button.ESCAPE.addButtonListener(new ExitProgramButton()); //exit program if escape is pressed.
		int detectionRange = 7;
		
		while(true) {
			int red, blue;
			try {
				Thread.sleep(1);
			} catch (InterruptedException e1) {
			}
			
			Color col = cs.getColor();
			red = col.getRed();
			blue = col.getBlue();
			
			double redBlueProportion = (double)red/blue;
			
			distance = getUSReading();
			
			
			LCD.drawString("US:               ", 0, 1);
			LCD.drawInt(distance, 3, 1);
			LCD.drawString("RBp:              ", 0, 3);
			LCD.drawString(String.valueOf(redBlueProportion), 4, 3);
			
			RConsole.println(""+ redBlueProportion);
			
			if(distance > detectionRange) {
				LCD.drawString("Bring obj closer             ", 0, 0);
				
			} else
				if(redBlueProportion > 1.65) {
					LCD.drawString("OBJECT DETECTED       ", 0, 0);
				}
				else {
					LCD.drawString("BUILDING BLOCK DETECTED    ", 0, 0);
				}
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static int getUSReading() {
		us.ping();
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return us.getDistance();
	}
}
