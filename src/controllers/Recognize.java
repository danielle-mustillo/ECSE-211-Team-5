package controllers;

import java.util.Stack;

import utilities.Point;
import utilities.Position;
import hardwareAbstraction.Forklift;
import lejos.nxt.comm.RConsole;
import manager.Manager;

public class Recognize extends Controller {

private Manager manager;
private boolean isSetup;
private Stack<Point> prevRoute;
private int lowValue;
private int highValue;
private final int middle = 1;
private boolean navigating;;
	
	public Recognize(Manager manager) {
		this.manager = manager;
		this.isSetup = false;
		this.lowValue = -1;
		this.highValue = -1;
		this.navigating = false;
	}
	
	public void run() {
		if (!isSetup) {
			RConsole.println("Setting up");
			this.isSetup = true;
			//set navigation to do nothing for the moment. 
			this.prevRoute = manager.sm.nav.getRoute();
			manager.sm.nav.setRoute(new Stack<Point>());
			
			// reset forklift
			manager.cm.setState(State.PAUSE);
			RConsole.println("Lowering Forklift");
			Forklift.lowerObject();
			
			// reset ultrasonic sensor
			manager.hm.ultrasonicPoller.resetUSP();
			
			//now return to previous state.
			manager.cm.setState(State.RECOGNIZE);

		}
		//if not navigating
		if(manager.sm.nav.getRoute().empty()) {
			this.navigating = false;
			RConsole.println("Route is empty, see if the robot needs to correct its navigation");
			int midReading = manager.hm.ultrasonicPoller.getUSReading(middle);
			
			//check if the robot must be corrected (if the midreading is not initialized and the low value has not been taken), do that if so.
			if(midReading != -1 && lowValue == -1) {
				//correct to about 20 cm.
				if(midReading < 19 || midReading > 21 && !this.navigating) {
					RConsole.println("The robot is trying to get to the object");
					Position currentPos = manager.sm.odo.getPosition();
					manager.sm.nav.addToRoute(currentPos.addDistanceToPosition(20 - midReading));
					//end this if statement now, don't execute next if statement.
				}
			}
		}
		
		if(manager.sm.nav.getRoute().empty()) {	
			//once the us is setup, get the low and high values. 
			if(manager.hm.ultrasonicPoller.isSetup()) {
				RConsole.print("Ultrasonic is setup, ");
				if (lowValue == -1) {
					RConsole.println("Setting lowValue");
					this.lowValue = manager.hm.ultrasonicPoller.computeAverage(middle);
					Forklift.setScanHeight();
				} else {
					RConsole.println("Setting highValue");
					this.highValue = manager.hm.ultrasonicPoller.computeAverage(middle);
					
					//if a block, the difference of the low and high values will be low. Otherwise it will be huge. 
					if(this.highValue - this.lowValue > 50) { //if a styrofoam block
						RConsole.println("Collection executed now.");
						manager.cm.setState(State.COLLECT);
					}
					else {
						RConsole.println("Wall Follower now");
						manager.cm.setState(State.WALL_FOLLOWER);
					}
				}
			}
		}
		
		//TODO figure out if we still need the colorPoller or not. 
		// start the color poller 
//		manager.hm.colorPoller.start();
		//if the color poller has finally collected enough values. 
//		if(manager.hm.colorPoller.isSetup()) {
//			// TODO we might not even need this code anymore!!!!!!!
//		}
		
		
		
	}
}
