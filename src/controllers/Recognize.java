package controllers;

import java.util.Stack;

import services.Navigation;
import utilities.Point;
import utilities.Position;
import hardwareAbstraction.Claw;
import hardwareAbstraction.ColorPoller.ObjectDetected;
import hardwareAbstraction.Forklift;
import hardwareAbstraction.Forklift.ForkliftState;
import hardwareAbstraction.UltrasonicPoller.USPosition;
import lejos.nxt.Sound;
import lejos.nxt.comm.RConsole;
import manager.Manager;

public class Recognize extends Controller {

private Manager manager;
private boolean isSetup;
private Stack<Point> prevRoute;
private int lowValue;
private int highValue;
private final int middle = 1;
private boolean inPosition;

	
	public Recognize(Manager manager) {
		this.manager = manager;
		this.isSetup = false;
		this.lowValue = -1; // reading when the forklift is down
		this.highValue = -1; // reading when the forklift is up.
		this.inPosition = false;
	}
	
	public void run() {
		//do nothing else, don't call recognize again. 
		manager.cm.setState(State.PAUSE);
		
		//stop any navigation occuring. 
		manager.hm.drive.stop();
		Stack<Point> oldRoute = manager.sm.nav.getRoute();
		manager.sm.nav.setRoute(new Stack<Point>());
		

		/*
		 * Get readings necessary from the USP
		 */
		// USPosition lowestSensor =
		// manager.hm.ultrasonicPoller.getLowestSensor();
		// int lowestValue = manager.hm.ultrasonicPoller.getLowestReading();

		/*
		 * Turn the robot to face the lowest sensor reading. The goal is after
		 * this, the robot will face directly towards the robot.
		 */
		// 10 centimeters separate the side sensors and the center sensor.
		// final int sensorOffset = 10;
		// double angle = Math.tan( sensorOffset / lowestValue);

		// TODO this is bugged. TurnToComplete does not work.
		// if(lowestSensor == USPosition.LEFT)
		// manager.sm.nav.turnToComplete(manager.sm.odo.getTheta() - angle);
		// if(lowestSensor == USPosition.RIGHT)
		// manager.sm.nav.turnToComplete(manager.sm.odo.getTheta() + angle);
		//

		// Setup forklift to sample objects. 
		sleep(Forklift.setHeight(ForkliftState.GROUND));
		sleep(Claw.grabObject());

		// sample the object ahead of it
		manager.hm.colorPoller.start();
		sleep(5000);

		//

		ObjectDetected object = manager.hm.colorPoller.getObjectReading();
		if (object == ObjectDetected.BLUE_BLOCK) {
			manager.sm.nav.setRoute(oldRoute);
			manager.cm.setState(State.COLLECT);
		} else {
			//reset old route
			manager.sm.nav.setRoute(oldRoute);
			manager.cm.setState(State.SEARCH); // TODO change to wall follower.
		}

	}
	
	public static void sleep(int num) {
		try {
		Thread.sleep(num);
		} catch(InterruptedException e) {}
	}
}
