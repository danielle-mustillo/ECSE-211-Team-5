package controllers;

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
		Claw.grabObject();
		Forklift.liftObject();
		//TODO determine if the robot needs to drop off or search again. By default now it searches. 
		manager.cm.setState(State.SEARCH);
	}
}
