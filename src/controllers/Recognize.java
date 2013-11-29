package controllers;

import utilities.Position;
import utilities.Settings;
import hardwareAbstraction.Claw;
import hardwareAbstraction.ColorPoller.ObjectDetected;
import hardwareAbstraction.Forklift;
import hardwareAbstraction.Forklift.ForkliftState;
import hardwareAbstraction.UltrasonicMotor;
import manager.Manager;

/**
 * The robot will in this state lower the forklift and begin identifying the
 * block infront of it. With the aid of the color sensor, it will determine if
 * its reading is a blue block, an obstacle or simply a misread (ie. reads the
 * floor). If it reads the floor, the controller will attempt to realign the
 * robot to sample the block.
 */
public class Recognize extends Controller {

	private Manager manager;

	public Recognize(Manager manager) {
		this.manager = manager;
	}

	public void run() {
		// do nothing else, don't call recognize again.
		manager.cm.setState(State.PAUSE);

		// stop any navigation occurring and go to the secondary route. 
		manager.hm.drive.stop();
		manager.sm.nav.alternateRoute(true);

		// Setup forklift to sample objects.
		UltrasonicMotor.setDefaultPosition();
		Forklift.setHeight(ForkliftState.GROUND);
		sleep(Claw.grabObject());

		// Go forward a few cm to get right up to the block.
		Position currentPos = manager.sm.odo.getPosition();
		int travelDistance = 5; //travel about 5 cm forward. 
		manager.sm.nav.addToRoute(currentPos
				.addDistanceToPosition(travelDistance));
		manager.cm.setState(State.JUST_TRAVEL);

		// While traveling, make this controller wait. 
		while (!manager.sm.nav.getRoute().empty()) {
			sleep(200);
		}
		manager.cm.setState(State.PAUSE);
		
		// Sample the object ahead of it, wait for the sample to finish
		manager.hm.colorPoller.start();
		sleep(5000);

		// Once the sample has been collected, identify it
		ObjectDetected object = manager.hm.colorPoller.getObjectReading();
		
		// Based on the identification, do different tasks. 
		if (object == ObjectDetected.BLUE_BLOCK) {
			// open the claw to receive the object.
			sleep(Claw.releaseObject());

			// go forward a certain distance to "grab" the block.
			currentPos = manager.sm.odo.getPosition();
			travelDistance = Settings.tipOfClawToUSDistance
					- Settings.backOfClawToUSDistance;
			manager.sm.nav.addToRoute(currentPos
					.addDistanceToPosition(travelDistance));
			manager.cm.setState(State.JUST_TRAVEL);

			// while traveling, make this controller wait. 
			while (!manager.sm.nav.getRoute().empty()) {
				sleep(200);
			}

			// Reset route, collect now.
			manager.sm.nav.alternateRoute(false);
			manager.cm.setState(State.COLLECT);
		} else {
			// reset old route
			manager.sm.nav.alternateRoute(false);
			manager.cm.setState(State.WALL_FOLLOWER); // TODO change to wall
														// follower.
		}
		manager.hm.colorPoller.stop();
	}

	public static void sleep(int num) {
		try {
			Thread.sleep(num);
		} catch (InterruptedException e) {
		}
	}
}
