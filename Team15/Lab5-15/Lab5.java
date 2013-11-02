import lejos.nxt.*;
import lejos.nxt.comm.RConsole;

/**
 * 
 * @project Lab 5 Object Recognition 
 * @names Riley van Ryswyk & Aditya Saha
 * @studentID 260447357 & 260453165
 * @group 15
 * @course ECSE 211 
 * @date 10/22/2013
 * 
 */

public class Lab5 {

	public static void main(String[] args) {
		RConsole.openBluetooth(20000);
		// setup the odometer, and ultrasonic and light sensors
		TwoWheeledRobot patBot = new TwoWheeledRobot(Motor.B, Motor.A);
		Odometer odo = new Odometer(patBot, true);
		UltrasonicSensor us = new UltrasonicSensor(SensorPort.S2);
		ColorSensor ls = new ColorSensor(SensorPort.S3);
		UltrasonicPoller usPoller = new UltrasonicPoller(us, Motor.C, odo);
				
		//choose the lab part
		int buttonChoice;
		
		do {
			// clear the display
			LCD.clear();

			// ask the user whether rising or falling localization
			LCD.drawString("< Left | Right >", 0, 0);
			LCD.drawString("  Part | Part   ", 0, 1);
			LCD.drawString("  One  | Two    ", 0, 2);

			buttonChoice = Button.waitForPress();
		} while (buttonChoice != Button.ID_LEFT
				&& buttonChoice != Button.ID_RIGHT);
		
		//setup display
		LCDInfo lcd = new LCDInfo(odo);
		
		//Object recognition
		if (buttonChoice == Button.ID_LEFT) {
			ObjectRecognitionOne one = new ObjectRecognitionOne(ls, usPoller, patBot, lcd);
			one.start();
			
		} 
		//Full lab 
		else {
			//initialize required classes
			USLocalizer usl = new USLocalizer(odo, usPoller, USLocalizer.LocalizationType.FALLING_EDGE);
			ObjectRecognitionTwo ort = new ObjectRecognitionTwo(ls, usPoller, patBot, odo);
			ObjectFinding of = new ObjectFinding(usPoller, odo, ort);
			//localize
			usl.doLocalization();
			
			//Find the styrofoam
			of.findStyrofoam();
		}
 		
		
		Button.waitForPress();
	}
	
	
	

}
