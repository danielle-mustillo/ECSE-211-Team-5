package controllers;

import utilities.Settings;
import hardwareAbstraction.Claw;
import hardwareAbstraction.Forklift;
import hardwareAbstraction.Forklift.ForkliftState;
import manager.Manager;

/**
 * This controller will collect the object. It will simply grab the object and
 * transfer state onto another controller.
 */
public class Collect extends Controller {

	private Manager manager;

	public Collect(Manager manager) {
		this.manager = manager;
	}

	/**
	 * This method grabs the object, lifts it and then tells the robot to
	 * DropOff or Search again. The method will Pause the state to allow the
	 * execution to only happen once per call.
	 */
	public void run() {
		manager.cm.setState(State.PAUSE);

		sleep(Claw.grabObject());
		sleep(Forklift.setHeight(ForkliftState.LIFT_HEIGHT));

		/*
		 * Update storage count and go to the required next step (searching or
		 * dropping off). However, this functionality is not complete so for the
		 * moment, the robot always goes to DROP_OFF
		 */
		manager.cm.setStored(manager.cm.getStored() + 1);
		// reset old route and add next destination to it
		if (manager.cm.getStored() >= Settings.maxBlockCapacity)
			manager.cm.setState(State.DROP_OFF);
		else
			manager.cm.setState(State.DROP_OFF);
		// manager.cm.setState(State.SEARCH);
	}

	public static void sleep(int num) {
		try {
			Thread.sleep(num);
		} catch (InterruptedException e) {
		}
	}

}
