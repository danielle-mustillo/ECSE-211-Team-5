package controllers;

import utilities.Point;
import utilities.Position;
import utilities.Settings;
import hardwareAbstraction.Claw;
import hardwareAbstraction.ColorPoller.ObjectDetected;
import hardwareAbstraction.Forklift;
import hardwareAbstraction.Forklift.ForkliftState;
import hardwareAbstraction.UltrasonicMotor;
import lejos.nxt.Sound;
import manager.Manager;

public class Recognize extends Controller {

private Manager manager;

	public Recognize(Manager manager) {
		this.manager = manager;
	}
	
	public void run() {
		//do nothing else, don't call recognize again. 
		manager.cm.setState(State.PAUSE);
		
		Position currentPos = manager.sm.odo.getPosition();
		manager.sm.nav.addToRoute(currentPos.addDisAndAngleToPosition(1, Math.PI/6));
		manager.cm.setState(State.JUST_TRAVEL);

		// while traveling, wait around
		while (!manager.sm.nav.getRoute().empty()) {
			sleep(200);
		}
		
		//stop any navigation occuring. Alternate route. 
		manager.hm.drive.stop();
		manager.sm.nav.alternateRoute(true);

		// Setup forklift to sample objects. 
		UltrasonicMotor.setDefaultPosition();
		Forklift.setHeight(ForkliftState.GROUND);
		sleep(Claw.grabObject());
		
		// Go forward a few cm to get right up to the block.
		currentPos = manager.sm.odo.getPosition();
		int travelDistance = 5;
		manager.sm.nav.addToRoute(currentPos.addDistanceToPosition(travelDistance));
		manager.cm.setState(State.JUST_TRAVEL);
		
		// while traveling, wait around
		while(!manager.sm.nav.getRoute().empty()) {
			sleep(200);
		}
		manager.cm.setState(State.PAUSE);
		// sample the object ahead of it, wait for the sample to finish
		
		manager.hm.colorPoller.start();
		ObjectDetected object = sample(0);
		if (object == ObjectDetected.BLUE_BLOCK) {
			// open the claw to recieve the object.
			sleep(Claw.releaseObject());

			// go forward a certain distance to "grab" the block.
			currentPos = manager.sm.odo.getPosition();
			travelDistance = Settings.tipOfClawToUSDistance
					- Settings.backOfClawToUSDistance;
			manager.sm.nav.addToRoute(currentPos
					.addDistanceToPosition(travelDistance));
			manager.cm.setState(State.JUST_TRAVEL);

			// while traveling, wait around
			while (!manager.sm.nav.getRoute().empty()) {
				sleep(200);
			}

			// reset route, collect now.
			manager.sm.nav.alternateRoute(false);
			manager.cm.setState(State.COLLECT);
		} else if (object == ObjectDetected.OBSTACLE) {
			// reset old route
			manager.sm.nav.alternateRoute(false);
			manager.cm.setState(State.WALL_FOLLOWER); // TODO change to wall
														// follower.
		} else {
			manager.cm.setState(State.SEARCH);
		}
		manager.hm.colorPoller.stop();
	}
	
	public static void sleep(int num) {
		try {
		Thread.sleep(num);
		} catch(InterruptedException e) {}
	}
	
	private ObjectDetected sample(int checks) {
		sleep(1000);
		ObjectDetected object = manager.hm.colorPoller.getObjectReading();
		
		if(checks > 4)
			return ObjectDetected.FLOOR;
		else if(object == ObjectDetected.BLUE_BLOCK)
			return ObjectDetected.BLUE_BLOCK;
		else if(object == ObjectDetected.OBSTACLE)
			return ObjectDetected.OBSTACLE;
		else {
			Position currentPos = manager.sm.odo.getPosition();
			manager.sm.nav.addToRoute(currentPos.addDisAndAngleToPosition(1, -Math.PI/12));
			manager.cm.setState(State.JUST_TRAVEL);

			// while traveling, wait around
			while (!manager.sm.nav.getRoute().empty()) {
				sleep(200);
			}
			manager.cm.setState(State.PAUSE);
			return sample(++checks);
		}
	}
}
