package controllers;

import utilities.Settings;
import hardwareAbstraction.Claw;
import hardwareAbstraction.Forklift;
import lejos.nxt.comm.RConsole;
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
		
		//grab and lift
		RConsole.println("grabbing object");
		try {
			Claw.grabObject();
		} catch (ArrayIndexOutOfBoundsException e){
			
		}
		RConsole.println("lifting object");
		
		try {
			Forklift.liftObject();
		} catch (ArrayIndexOutOfBoundsException e){
			
		}
		
		//update storage count and go to the required next step (searching or dropping off).
		RConsole.println("storage");
		manager.cm.setStored(manager.cm.getStored() + 1);
		if(manager.cm.getStored() >= Settings.maxBlockCapacity)
			manager.cm.setState(State.DROP_OFF);
		else
			manager.cm.setState(State.SEARCH);
	}
}
