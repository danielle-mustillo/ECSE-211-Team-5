package controllers;

import hardwareAbstraction.Claw;
import hardwareAbstraction.Forklift;
import hardwareAbstraction.Forklift.ForkliftState;
import utilities.Point;
import utilities.Position;
import manager.Manager;

public class WallFollower extends Controller {

private Manager manager;
private boolean left;
	
	public WallFollower(Manager manager) {
		this.manager = manager;
	}
	
	public void run() {
		left = false;
		//do only one thing for now
		manager.cm.setState(State.PAUSE);
		manager.sm.nav.alternateRoute(true);
		Claw.releaseObject();
		Forklift.setHeight(ForkliftState.SCAN_HEIGHT_LOW);
		
		//drive backwards about 10 cm.
		manager.hm.drive.setSpeeds(-60, 0);
		sleep(1000);
		manager.hm.drive.stop();
		
		obstacleAvoid(40,Math.PI/2);
		if(!left)
			obstacleAvoid(80,-Math.PI/2);
		
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
	
	
	private void obstacleAvoid(int distance, double angle) {
		Position currPos;
		Point destination;
		
		manager.cm.setState(State.JUST_TRAVEL);
		currPos = manager.sm.odo.getPosition();
		destination = currPos.addDisAndAngleToPosition(distance, angle);
		manager.sm.nav.addToRoute(destination);
		
		//TODO if the route is found not to be the above stated point, then it MIGHT have to exit. To check this.
		while(!manager.sm.nav.getRoute().empty() && !left) {
			if(!manager.sm.nav.getRoute().peek().equals(destination))
				left = true;
			sleep(200);
		}
	}

	@Override
	public void alert() {
		//alert does nothing.
	}
}
