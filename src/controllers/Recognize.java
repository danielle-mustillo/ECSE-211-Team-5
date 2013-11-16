package controllers;

import java.util.Stack;

import utilities.Point;
import utilities.Position;
import hardwareAbstraction.Forklift;
import hardwareAbstraction.Forklift.ForkliftState;
import lejos.nxt.comm.RConsole;
import manager.Manager;

public class Recognize extends Controller {

private Manager manager;
private boolean isSetup;
private Stack<Point> prevRoute;
private int lowValue;
private int highValue;
private final int middle = 1;
private boolean isInPosition;
	
	public Recognize(Manager manager) {
		this.manager = manager;
		this.isSetup = false;
		this.lowValue = -1; // reading when the forklift is down
		this.highValue = -1; // reading when the forklift is up.
		this.isInPosition = false;
	}
	
	public void run() {
		//setup the recognize parameters at the start, only does this once. 
		if (!isSetup) {
			manager.cm.setState(State.PAUSE);
			
			RConsole.println("Setting up");
			this.isSetup = true;
			this.isInPosition = false;
			
			//set navigation to do nothing for the moment. 
			this.prevRoute = manager.sm.nav.getRoute();
			manager.sm.nav.setRoute(new Stack<Point>());
			
			// lower forklift if raised
			
			Forklift.setHeight(ForkliftState.GROUND);
			
			// reset ultrasonic sensor
			manager.hm.ultrasonicPoller.resetUSP();
			
			//now return to previous state.
			manager.cm.setState(State.RECOGNIZE);

			// if not at scan height, set it to that
			if(Forklift.state == Forklift.ForkliftState.LIFT_HEIGHT) {
				manager.cm.setState(State.PAUSE);
				Forklift.setHeight(ForkliftState.GROUND);
				manager.cm.setState(State.RECOGNIZE);
			}
			if (Forklift.state != Forklift.ForkliftState.SCAN_HEIGHT) {
				manager.cm.setState(State.PAUSE);
				Forklift.setHeight(ForkliftState.SCAN_HEIGHT);
				manager.cm.setState(State.RECOGNIZE);
			}
			//TODO figure out if we still need the colorPoller or not. 
			// start the color poller 
			manager.hm.colorPoller.start();
		}
		//if the color poller has finally collected enough values. 
		if(manager.hm.colorPoller.isSetup()) {
			// TODO we might not even need this code anymore!!!!!!!
		}
		
		if (!this.isInPosition) {
				RConsole.println("See if the robot needs to correct its navigation");
				int midReading = manager.hm.ultrasonicPoller
						.getUSReading(middle);

				// check if the robot must move to 20 cm from the object, but only when the first reading is taken.
				if (midReading != -1) {
					// if not 20 cm from the object, then correct the robot to
					// be 20 from object
					if (midReading < 19 || midReading > 21) {
						RConsole.println("The robot is trying to get to the object");
						Position currentPos = manager.sm.odo.getPosition();
						manager.sm.nav.addToRoute(currentPos
								.addDistanceToPosition(20 - midReading)); //TODO debug this method. 
					}
					this.isInPosition = true;
				}
				
			}
		
		//once in position, route is empty. 
		if(manager.sm.nav.getRoute().empty()) {	
			//once the us is setup, get the low and high values. 
			if(manager.hm.ultrasonicPoller.isSetup()) {
				RConsole.print("Ultrasonic is setup, ");
				if (lowValue == -1) {
					// set low value. Reset the USP and raise the forklift. 
					RConsole.println("Setting lowValue");
					this.lowValue = manager.hm.ultrasonicPoller.computeAverage(middle);
					manager.hm.ultrasonicPoller.resetUSP();
					Forklift.setHeight(ForkliftState.SCAN_HEIGHT);
				} else {
					RConsole.println("Setting highValue");
					this.highValue = manager.hm.ultrasonicPoller.computeAverage(middle);
					
					//if an obstacle, the difference of the low and high values will be low. Otherwise it will be huge. 
					if(this.highValue - this.lowValue > 50) { //if a styrofoam block
						RConsole.println("Collection executed now.");
						manager.cm.setState(State.COLLECT);
					}
					else {
						RConsole.println("Wall Follower now");
						manager.cm.setState(State.WALL_FOLLOWER);
					}
					//restore previous state before this execution 
					manager.sm.nav.setRoute(this.prevRoute);
					this.isSetup = false;
				}
			}
		}
	}
}
