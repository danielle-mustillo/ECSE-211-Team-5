/* Danielle Mustillo, Nathaniel Anthonisen
 * 260533476, 260470621
 * ECSE 211 - Design Principles and Methods
 * October 08, 2013
 */
/*
 *Lab4.java
 */
import java.util.Timer;

import lejos.nxt.*;

public class Lab4 {

	public static void main(String[] args) throws InterruptedException {
		// setup the odometer, display, and ultrasonic and light sensors
		int buttonChoice;
		do {
			// clear the display
			LCD.clear();

			// ask the user whether they want falling edge or rising edge.
			LCD.drawString("< Left | Right >", 0, 0);
			LCD.drawString("       |        ", 0, 1);
			LCD.drawString("Falling| Rising ", 0, 2);
			LCD.drawString(" Edge  |  Edge  ", 0, 3);
			LCD.drawString("       |        ", 0, 4);

			buttonChoice = Button.waitForAnyPress();
		} while (buttonChoice != Button.ID_LEFT
				&& buttonChoice != Button.ID_RIGHT);
		
		Button.ESCAPE.addButtonListener(new ExitProgramButton()); //exit program if escape is pressed.
		
		//falling edge
		if (buttonChoice == Button.ID_LEFT) {	
			TwoWheeledRobot patBot = new TwoWheeledRobot(Motor.A, Motor.C);
			Odometer odo = new Odometer(patBot, true);
			UltrasonicSensor us = new UltrasonicSensor(SensorPort.S2);
			ColorSensor cs = new ColorSensor(SensorPort.S1);

			
			// perform the ultrasonic localization
			USLocalizer usl = new USLocalizer(odo, us, USLocalizer.LocalizationType.FALLING_EDGE);
			LCDInfo lcd = new LCDInfo(odo,usl);
			usl.doLocalization();
			
			// perform the light sensor localization
//			LightLocalizer lsl = new LightLocalizer(odo, cs);
//			lsl.doLocalization();
		//rising edge
		} else {
			TwoWheeledRobot patBot = new TwoWheeledRobot(Motor.A, Motor.C);
			Odometer odo = new Odometer(patBot, true);
			UltrasonicSensor us = new UltrasonicSensor(SensorPort.S2);
			ColorSensor cs = new ColorSensor(SensorPort.S1);
			
			// perform the ultrasonic localization
			USLocalizer usl = new USLocalizer(odo, us, USLocalizer.LocalizationType.RISING_EDGE);
			LCDInfo lcd = new LCDInfo(odo,usl);
			usl.doLocalization();
			
			// perform the light sensor localization
//			LightLocalizer lsl = new LightLocalizer(odo, cs);
//			lsl.doLocalization();
		}
		

		Button.waitForAnyPress();
	}

}
