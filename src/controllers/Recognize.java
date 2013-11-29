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

		Position currentPos = manager.sm.odo.getPosition();
		manager.sm.nav.addToRoute(currentPos.addDisAndAngleToPosition(1,
				Math.PI / 6));
		manager.cm.setState(State.JUST_TRAVEL);

		// while traveling, wait around
		while (!manager.sm.nav.getRoute().empty()) {
			sleep(200);
		}

		// stop any navigation occurring and go to the secondary route.
		manager.hm.drive.stop();
		manager.sm.nav.alternateRoute(true);

		// Setup forklift to sample objects.
		UltrasonicMotor.setDefaultPosition();
		Forklift.setHeight(ForkliftState.GROUND);
		sleep(Claw.grabObject());

		// Go forward a few cm to get right up to the block.
		currentPos = manager.sm.odo.getPosition();
		int travelDistance = 5; // travel about 5 cm forward.
		manager.sm.nav.addToRoute(currentPos
				.addDistanceToPosition(travelDistance));
		manager.cm.setState(State.JUST_TRAVEL);

		// While traveling, make this controller wait.
		while (!manager.sm.nav.getRoute().empty()) {
			sleep(200);
		}

		/*
		 * Sample the object ahead of it, wait for the sample to finish. The
		 * sampling happens recursively so this may take some time.
		 */
		manager.cm.setState(State.PAUSE);
		manager.hm.colorPoller.start();
		ObjectDetected object = sample(0);

		// Do different things based on what was read.
		if (object == ObjectDetected.BLUE_BLOCK) {
			// Open the claw to receive the object.
			sleep(Claw.releaseObject());

			// Go forward a certain distance to "grab" the block.
			currentPos = manager.sm.odo.getPosition();
			travelDistance = Settings.tipOfClawToUSDistance
					- Settings.backOfClawToUSDistance;
			manager.sm.nav.addToRoute(currentPos
					.addDistanceToPosition(travelDistance));
			manager.cm.setState(State.JUST_TRAVEL);

			// While traveling, make this controller wait.
			while (!manager.sm.nav.getRoute().empty()) {
				sleep(200);
			}

			// Reset route, collect now.
			manager.sm.nav.alternateRoute(false);
			manager.cm.setState(State.COLLECT);
		} else if (object == ObjectDetected.OBSTACLE) {
			// reset old route, but make sure to go to avoid the block by
			// initiating Wall Following.
			manager.sm.nav.alternateRoute(false);
			manager.cm.setState(State.WALL_FOLLOWER);
		} else {
			manager.cm.setState(State.SEARCH);
		}
		// stop the controller at the end of it.
		manager.hm.colorPoller.stop();
	}

	/**
	 * Sleeps the current controller for a given amount of time.
	 * 
	 * @param num
	 *            The time in mini-seconds.
	 */
	public static void sleep(int num) {
		try {
			Thread.sleep(num);
		} catch (InterruptedException e) {
		}
	}

	/**
	 * This samples the object. This controller does more than one sampling if
	 * it doesn't find the block the first time, by using recursion.
	 * 
	 * @param checks
	 *            The recursive number, which controlls whether re-execution
	 *            should occur.
	 * @return The object detected in enum.
	 */
	private ObjectDetected sample(int checks) {
		// sample for 1 second to ensure the changes have propogated through.
		sleep(1000);
		ObjectDetected object = manager.hm.colorPoller.getObjectReading();

		/*
		 * If lots of checks have occurred (4 checks) and nothing was found,
		 * return the floor. Otherwise keep checking until the robot finds a
		 * blue block or an obstacle.
		 */
		if (checks > 4)
			return ObjectDetected.FLOOR;
		else if (object == ObjectDetected.BLUE_BLOCK)
			return ObjectDetected.BLUE_BLOCK;
		else if (object == ObjectDetected.OBSTACLE)
			return ObjectDetected.OBSTACLE;
		else {
			// Turn slightly to sample just slightly to the side.
			Position currentPos = manager.sm.odo.getPosition();
			manager.sm.nav.addToRoute(currentPos.addDisAndAngleToPosition(1,
					-Math.PI / 12));
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
