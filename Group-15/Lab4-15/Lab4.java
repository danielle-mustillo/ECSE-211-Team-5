import lejos.nxt.*;
import lejos.nxt.comm.RConsole;

/**
 * 
 * @project Lab 4 Localization 
 * @names Riley van Ryswyk & Aditya Saha
 * @studentID 260447357 & 260453165
 * @group 15
 * @course ECSE 211 
 * @date 06/10/2013
 * 
 */

public class Lab4 {

	public static void main(String[] args) {
		
		// setup the odometer, and ultrasonic and light sensors
		TwoWheeledRobot patBot = new TwoWheeledRobot(Motor.B, Motor.A);
		Odometer odo = new Odometer(patBot, true);
		UltrasonicSensor us = new UltrasonicSensor(SensorPort.S2);
		ColorSensor ls = new ColorSensor(SensorPort.S1);
		
		//choose method of Ultrasonic localization
		int buttonChoice;
		
		do {
			// clear the display
			LCD.clear();

			// ask the user whether rising or falling localization
			LCD.drawString("< Left | Right >", 0, 0);
			LCD.drawString("       |        ", 0, 1);
			LCD.drawString("  Rise | Fall   ", 0, 2);

			buttonChoice = Button.waitForPress();
		} while (buttonChoice != Button.ID_LEFT
				&& buttonChoice != Button.ID_RIGHT);
		
		//setup display
		LCDInfo lcd = new LCDInfo(odo);
		
		//Rising Edge Ultrasonic Localization
		if (buttonChoice == Button.ID_LEFT) {
			USLocalizer usl = new USLocalizer(odo, us, USLocalizer.LocalizationType.RISING_EDGE);
			usl.doLocalization();
		} 
		//Falling edge Ultrasonic Localization
		else {
			USLocalizer usl = new USLocalizer(odo, us, USLocalizer.LocalizationType.FALLING_EDGE);
			usl.doLocalization();
		}
		
		// Turn to 0 degrees to display the accuracy of the ultrasonic localization
		odo.getNavigation().turnTo(0);
		
		// perform the light sensor localization
		LightLocalizer lsl = new LightLocalizer(odo, ls);
		lsl.doLocalization();
		
		odo.getNavigation().travelTo(0, 0);
		odo.getNavigation().turnTo(0);
		
		Button.waitForPress();
	}

}
