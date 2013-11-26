package controllers;

import hardwareAbstraction.Claw;
import hardwareAbstraction.Forklift;
import hardwareAbstraction.Forklift.ForkliftState;

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
		if(!initialized) {
			manager.cm.setState(State.DROP_OFF);
			
			//store old route temporarily, make a new route for the moment. 
			manager.sm.nav.alternateRoute(true);
			
			//go to the green zone
			this.manager.sm.nav.addToRoute(new Point(Settings.greenZoneCoords[0]));
		} 
		else {
			/*
			 * now the robot should attempt to head to greenZone. 
			 * so nothing is done here, everything is done in background. 
			 */
			
			// when the robot gets to the greenZone, 
			if(manager.sm.nav.getRoute().empty()) {
				//drop off the block
				Forklift.setHeight(ForkliftState.GROUND);
				Claw.releaseObject();
				
				//go back to previous state
				this.initialized = false;
				this.manager.sm.nav.alternateRoute(false);
				this.manager.cm.setState(State.SEARCH);
			}
		}
	}
}
