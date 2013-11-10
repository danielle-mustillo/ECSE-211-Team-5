package controllers;

import utilities.Settings;
import hardwareAbstraction.Claw;
import hardwareAbstraction.Forklift;
import manager.Manager;

public class Collect extends Controller {

private Manager manager;
	
	public Collect(Manager manager) {
		this.manager = manager;
	}
	
	/**
	 * This method grabs the object, lifts it and then tells the robot to Search again. 
	 * Maintains the singleton design of this system. 
	 */
	public void run() {
		//pause the re-execution
		manager.cm.setState(State.PAUSE);
		Claw.grabObject();
		Forklift.liftObject();
		manager.cm.setStored(manager.cm.getStored());
		if(manager.cm.getStored() == Settings.maxBlockCapacity)
			manager.cm.setState(State.DROP_OFF);
		else
			manager.cm.setState(State.SEARCH);
	}
}
