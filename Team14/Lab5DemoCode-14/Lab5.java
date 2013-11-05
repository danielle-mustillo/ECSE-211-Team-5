/* Danielle Mustillo, Nathaniel Anthonisen
 * 260533476, 260470621
 * ECSE 211 - Design Principles and Methods
 * October 22, 2013
 */
/*
 *Lab5.java
 */
import lejos.nxt.*;
import lejos.nxt.comm.RConsole;

public class Lab5 {

	public static void main(String[] args) {
		//start display and choose program.
		int programChoice = mainMenu();
		
		//setup the motor acceleration (reduce error on the odometer)
		Motors.setAcceleration(3000);
		
		//instantiate the pollers and the switchboard.
		SwitchBoard switches = new SwitchBoard();
		
		
		//exit program if escape is pressed.
		Button.ESCAPE.addButtonListener(new ExitProgramButton()); 
		
		//lets all the changes with teh UP propogate through the system.
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		//sense the blocks placed infront of it.
		if (programChoice == Button.ID_LEFT) {	
			Lab5Part1 part1 = new Lab5Part1();
			//part1.start();
		} else {
			//ultrasonic reader and storer.
			UltrasonicSensor us = new UltrasonicSensor(SensorPort.S2);
			UltrasonicPoller up = new UltrasonicPoller(us, switches);
			
			//used as objects
			Odometer odo = new Odometer();
			Info display = new Info(odo,switches);
			USLocalizer usl = new USLocalizer(odo, switches);
			Navigation nav = new Navigation(odo, switches);
//			WallFollowController wallFollow = new WallFollowController(2, 20, odo, switches);
			
			up.start();
//			cp.start();
			
			nap(500); //propogate changes!!
			
			//switches.setActivity(CurrentActivity.LOCALIZING);
			//RConsole.println("Act:  " + switches.getActivity().name());
			usl.doLocalization();
			
			//orient
			switches.setActivity(CurrentActivity.NAVIGATING);
			RConsole.println("Act:  " + switches.getActivity().name());
			
			//now navigate for real.
//			wallFollow.start();
			nav.start();
		}
		try {
			Thread.sleep(20000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} //let the changes propagate through the system.
		
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
		LCD.clear();
		return buttonChoice;
	}
	//useful helper method
	public static void nap(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}
