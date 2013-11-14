package controllers;

import hardwareAbstraction.Claw;
import hardwareAbstraction.Forklift;

import java.util.Stack;

import utilities.Point;
import utilities.Settings;
import lejos.nxt.comm.RConsole;
import manager.Manager;

/**
 * This class will dropoff the block at the destination required. 
 * It will navigate to the green zone location and drop off the block.
 * @author danielle
 */
public class DropOff extends Controller {

private Manager manager;
private boolean initialized;
private Stack<Point> route;
	
	public DropOff(Manager manager) {
		this.manager = manager;
		this.initialized = false;
	}
	
	/** 
	 * @bug this method throws exceptions, which must be found and eliminated. 
	 */
	public void run() {
		//upon initialization,
		RConsole.println("drop_off");
		if(!initialized) {
			manager.cm.setState(State.DROP_OFF);
			
			//store old route temporarily, make a new route for the moment. 
			this.route = this.manager.sm.nav.getRoute();
			this.manager.sm.nav.setRoute(new Stack<Point>());
			
			//go to the green zone
			this.manager.sm.nav.addToRoute(new Point(Settings.greenZoneCoords[0]));
		} 
		else {
			/*now the robot should attempt to head to greenZone. 
			 * so nothing is done here, everything is done in background. 
			 */
			
			// when the robot gets to the greenZone, 
			if(manager.sm.nav.getRoute().peek() == null) {
				//drop off the block
				Forklift.lowerObject();
				Claw.releaseObject();
				
				//go back to previous state
				this.manager.sm.nav.setRoute(route);
				this.manager.cm.setState(State.SEARCH);
			}
		}
	}
}
