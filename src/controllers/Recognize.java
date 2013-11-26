package controllers;

import utilities.Position;
import utilities.Settings;
import hardwareAbstraction.Claw;
import hardwareAbstraction.ColorPoller.ObjectDetected;
import hardwareAbstraction.Forklift;
import hardwareAbstraction.Forklift.ForkliftState;
import manager.Manager;

public class Recognize extends Controller {

private Manager manager;

	public Recognize(Manager manager) {
		this.manager = manager;
	}
	
	public void run() {
		//do nothing else, don't call recognize again. 
		manager.cm.setState(State.PAUSE);
		
		//stop any navigation occuring. Alternate route. 
		manager.hm.drive.stop();
		manager.sm.nav.alternateRoute(true);

		// Setup forklift to sample objects. 
		sleep(Forklift.setHeight(ForkliftState.GROUND));
		sleep(Claw.grabObject());

		// sample the object ahead of it, wait for the sample to finish
		manager.hm.colorPoller.start();
		sleep(5000);

		ObjectDetected object = manager.hm.colorPoller.getObjectReading();
		if (object == ObjectDetected.BLUE_BLOCK) {
			//open the claw to recieve the object.
			sleep(Claw.releaseObject());
			
			//go forward a certain distance to "grab" the block.
			Position currentPos = manager.sm.odo.getPosition();
			final int travelDistance = Settings.tipOfClawToUSDistance - Settings.centerOfClawToUSDistance;
			manager.sm.nav.addToRoute(currentPos.addDistanceToPosition(travelDistance));
			manager.cm.setState(State.JUST_TRAVEL);
			
			// while traveling, wait around
			while(!manager.sm.nav.getRoute().empty()) {
				sleep(200);
			}
			
			//reset route, collect now.
			manager.sm.nav.alternateRoute(false);
			manager.cm.setState(State.COLLECT);	
		} else {
			//reset old route
			manager.sm.nav.alternateRoute(false);
			manager.cm.setState(State.SEARCH); // TODO change to wall follower.
		}
		manager.hm.colorPoller.stop();
	}
	
	public static void sleep(int num) {
		try {
		Thread.sleep(num);
		} catch(InterruptedException e) {}
	}
}
