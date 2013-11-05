/* Danielle Mustillo, Nathaniel Anthonisen
 * 260533476, 260470621
 * ECSE 211 - Design Principles and Methods
 * October 08, 2013
 */
/*
 *Lab4.java
 */
import lejos.util.Timer;
import lejos.nxt.*;

public class Lab5 {

	public static void main(String[] args) {
		//start display and choose program.
		int programChoice = mainMenu();
		
		SwitchBoard switches = new SwitchBoard();
		UltrasonicSensor us = new UltrasonicSensor(SensorPort.S2);
		UltrasonicPoller up = new UltrasonicPoller(us, switches);
		ColorSensor cs = new ColorSensor(SensorPort.S1);
		ColorPoller cp = new ColorPoller(cs, switches);
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} //let the changes propagate through the system.
		
		//exit program if escape is pressed.
		Button.ESCAPE.addButtonListener(new ExitProgramButton()); 
		
		//sense the blocks placed infront of it.
		if (programChoice == Button.ID_LEFT) {	
			ComplexObjectSampler objSample = new ComplexObjectSampler(switches);
			objSample.start();
		//rising edge
		} else {
			Odometer odo = new Odometer();
			LCDInfo display = new LCDInfo(odo,switches);
			
			//TODO WHILE TRUE, CHECK ACTIVITIES AND EXECUTE THEM. Or maybe not needed???
			
			// perform the ultrasonic localization
//			USLocalizer usl = new USLocalizer(odo, us, USLocalizer.LocalizationType.RISING_EDGE);
//			usl.doLocalization();
//			
//			while(true) {
//				if(switches.getActivity() == CurrentActivity.BACKING_AWAY_FROM_OBSTACLE) {
//					
//				}
//			}
		}
		

		Button.waitForAnyPress();
	}
	
	/* Prints the main menu for the user */
	public static int mainMenu() {
		int buttonChoice;
		do {
			// clear the display
			LCD.clear();

			// ask the user whether they want falling edge or rising edge.
			LCD.drawString("< Left | Right >", 0, 0);
			LCD.drawString("       |        ", 0, 1);
			LCD.drawString(" Sense | Search ", 0, 2);
			LCD.drawString("  Obj  |   Obj  ", 0, 3);
			LCD.drawString("       |        ", 0, 4);

			buttonChoice = Button.waitForAnyPress();
		} while (buttonChoice != Button.ID_LEFT && buttonChoice != Button.ID_RIGHT);
		return buttonChoice;
	}

	/* Exit the program no matter its current state */
	public static class ExitProgramButton implements ButtonListener {
		@Override
		public void buttonPressed(Button b) {
			System.exit(0);
		}
		@Override
		public void buttonReleased(Button b) {
		}
	}
	

}
