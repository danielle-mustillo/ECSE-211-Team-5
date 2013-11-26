package controllers;

import utilities.Position;
import manager.Manager;

public class WallFollower extends Controller {

private Manager manager;
	
	public WallFollower(Manager manager) {
		this.manager = manager;
	}
	
	public void run() {
		//do only one thing for now
		manager.cm.setState(State.PAUSE);
		manager.sm.nav.alternateRoute(true);
		
		//drive backwards about 10 cm.
		manager.hm.drive.setSpeeds(-60, 0);
		sleep(1000);
		manager.hm.drive.stop();
		
		Position currPos;
		
		manager.cm.setState(State.JUST_TRAVEL);
		currPos = manager.sm.odo.getPosition();
		manager.sm.nav.addToRoute(currPos.addDisAndAngleToPosition(20, Math.PI/2));
		
		while(!manager.sm.nav.getRoute().empty()) {
			sleep(200);
		}
		
		manager.cm.setState(State.JUST_TRAVEL);
		currPos = manager.sm.odo.getPosition();
		manager.sm.nav.addToRoute(currPos.addDisAndAngleToPosition(20, Math.PI/2));
		
		while(!manager.sm.nav.getRoute().empty()) {
			sleep(200);
		}
		
		//exit conditions
		manager.sm.nav.alternateRoute(false);
		manager.cm.setState(State.SEARCH);
	}
	
	public static void sleep(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			//no error expected here.
		}
	}
}
