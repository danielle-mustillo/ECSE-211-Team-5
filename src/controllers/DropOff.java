package controllers;

import java.util.Stack;

import utilities.Point;
import utilities.Settings;
import manager.Manager;

/**
 * This class will dropoff the block at the destination required. 
 * It will navigate to the green zone location and drop off the block.
 * @author danielle
 */
public class DropOff extends Controller {

private Manager manager;
	
	public DropOff(Manager manager) {
		this.manager = manager;
	}
	
	public void run() {
		//pause the re-execution
		manager.cm.setState(State.PAUSE);
		
		//store the route. 
		Stack<Point> route = this.manager.sm.nav.exportAndResetRoute();
		
		//go to the green zone
		this.manager.sm.nav.addToRoute(new Point(Settings.greenZoneCoords[0]));
		
		//go back to previous state
		this.manager.sm.nav.setRoute(route);
		this.manager.cm.setState(State.SEARCH);
	}
}
