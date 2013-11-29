package controllers;

import hardwareAbstraction.Claw;
import hardwareAbstraction.Forklift;
import hardwareAbstraction.Forklift.ForkliftState;

import utilities.Point;
import utilities.Settings;
import manager.Manager;

/**
 * This class will dropoff the block at the destination required. The robot will
 * navigate to the green zone location and drop off the block there. It is
 * assumed that the robot has a block (or multiple) at this point.
 */
public class DropOff extends Controller {

	private Manager manager;
	private boolean initialized;

	public DropOff(Manager manager) {
		this.manager = manager;
		this.initialized = false;
	}

	/**
	 * The
	 */
	public void run() {
		/*
		 * When the controller is first called, setup the dropoff controller.
		 * This is to be reset when control is passed away from this controller.
		 */
		if (!initialized) {
			initialized = true;

			// store old route temporarily, make a new route for the moment.
			manager.sm.nav.alternateRoute(true);

			// go to the green zone by allowing this controller to be called
			// many times.
			this.manager.sm.nav.addToRoute(new Point(
					Settings.greenZoneCoords[0]));
			manager.cm.setState(State.DROP_OFF);
		} else {
			/*
			 * Now the robot should attempt to head to greenZone. so nothing is
			 * done for the moment. Everything is done in background.When the
			 * robot gets to the greenZone, the following code is executed here.
			 */
			if (manager.sm.nav.getRoute().empty()) {
				// only execute this code once.
				manager.cm.setState(State.PAUSE);

				// drop off the block by dropping the forklift and releasing the
				// object
				sleep(Forklift.setHeight(ForkliftState.GROUND));
				sleep(Claw.releaseObject());

				// back away from the block.
				manager.hm.drive.setSpeeds(-100, 0);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
				manager.hm.drive.stop();

				/*
				 * Go back to previous state. The forklift and claw will return
				 * to the previous state. Store the previous route and go back
				 * to Searching.
				 */
				Claw.grabObject();
				sleep(Forklift.setHeight(ForkliftState.SCAN_HEIGHT_LOW));
				this.initialized = false;
				this.manager.sm.nav.alternateRoute(false);
				this.manager.cm.setState(State.SEARCH);
			}
		}
	}

	private void sleep(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
		}
	}
}
