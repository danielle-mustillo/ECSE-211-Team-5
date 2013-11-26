package controllers;

import java.util.Stack;

import utilities.Point;
import utilities.Position;
import utilities.Settings;
import hardwareAbstraction.Claw;
import hardwareAbstraction.Forklift;
import hardwareAbstraction.Forklift.ForkliftState;
import lejos.nxt.Sound;
import lejos.nxt.comm.RConsole;
import manager.Manager;

public class Collect extends Controller {

private Manager manager;
private Stack<Point> oldRoute;
private static boolean setup = false;
	
	public Collect(Manager manager) {
		this.manager = manager;
	}
	
	/**
	 * This method grabs the object, lifts it and then tells the robot to Search again. 
	 * Maintains the singleton design of this system. 
	 */
	public void run() {
		/*
		 * Setup will stop the robot, add a new destination closer to the object to pick it up
		 */
//		if (!setup) {
//			setup = true;
//
//			// stop navigation for the moment.
//			oldRoute = manager.sm.nav.getRoute();
//			manager.sm.nav.setRoute(new Stack<Point>());
//			manager.hm.drive.stop();
//
//			// setup claw and navigate towards the block
//			Sound.twoBeeps();
//			Forklift.setHeight(ForkliftState.GROUND);
//			
//			
//			
//			//navigate towards block
////			int distance = manager.hm.ultrasonicPoller.getUSReading(1) - clawOffset < 0 ? 0 : manager.hm.ultrasonicPoller.getUSReading(1) - clawOffset;
//			
//		}
		/*
		 * Once the robot is in position to pickup the object, it will then grab the object and lift it. 
		 * It will then pass on control to DropOff.java or Search.java depending on if the block "hopper" is full. 
		 */
//		RConsole.println(""+manager.sm.nav.getRoute().empty());
//		if(!manager.sm.nav.getRoute().empty()) {
//			RConsole.println(""+manager.sm.nav.getRoute().peek());
//		}
//		if (manager.sm.nav.getRoute().empty()) {
			// grab and lift
		manager.cm.setState(State.PAUSE);
		
			sleep(Claw.grabObject());
			sleep(Forklift.setHeight(ForkliftState.LIFT_HEIGHT));

			// update storage count and go to the required next step (searching
			// or dropping off).
			manager.cm.setStored(manager.cm.getStored() + 1);
			// reset old route and add next destination to it
			manager.sm.nav.setRoute(oldRoute);
			if (manager.cm.getStored() >= Settings.maxBlockCapacity)
				manager.cm.setState(State.DROP_OFF);
			else
				manager.cm.setState(State.SEARCH);
			
			//clean up method.
			setup = false;
//		}
	}
	
	public static void sleep(int num) {
		try {
		Thread.sleep(num);
		} catch(InterruptedException e) {}
	}
	
}
