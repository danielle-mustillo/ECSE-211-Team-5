package services;

import hardwareAbstraction.UltrasonicMotor;
import utilities.Point;
import utilities.Position;
import utilities.Settings;
import controllers.State;
import lejos.nxt.Sound;
import lejos.nxt.comm.RConsole;
import manager.*;

/**
 * The following controller performs obstacle avoidance by scanning with the
 * ultrasonic sensors. This functionality is done primarily for
 * {@link Navigation} for the moment. 
 */
public class ObstacleAvoidance {
	private hardwareAbstraction.UltrasonicPoller poller;
	private int threshold;
	private int safetyThreshold;
	private Manager manager;

	public ObstacleAvoidance(Manager manager) {
		this.manager = manager;
		poller = manager.hm.ultrasonicPoller;
		// TODO test these values, find appropriate values for our robot.
		this.threshold = 20;
		this.safetyThreshold = 5;
	}

	/**
	 * The following method should avoid objects by detecting them before going
	 * ahead. It should do the following: Scan ahead only once facing the
	 * correct orientation approximately, then travelTo that destination. If the
	 * robot is very far from a block, then just travel to the desired
	 * destination. If there is a block somewhere nearby in the robot's field of
	 * view, then travel only towards that block as measured by the ultrasonic
	 * sensors. If the block is very close (again measured by all three
	 * ultrasonic sensors), then the robot will simply scan it.
	 */
	public void scanAhead() {
		// pause the re-execution, flag the robot as having
		// scanned.
		manager.cm.setState(State.PAUSE);

		// stop the robot and setup the scanner.
		manager.hm.drive.stop();
		UltrasonicMotor.setForwardPosition();

		// this code may cause bugs, it is not known.
		if (manager.cm.getStored() > 0)
			manager.hm.ultrasonicPoller.pingSides();
		else
			manager.hm.ultrasonicPoller.pingSequential();

		// reset the ultrasonic poller and wait until it is
		// setup.
		manager.hm.ultrasonicPoller.resetUSP();
		while (!manager.hm.ultrasonicPoller.isSetup()) {
			manager.um.nap(200);
		}

		// get the lowest reading of all the sensors.
		int lowest = manager.hm.ultrasonicPoller.getLowestReading();

		if (lowest < Settings.tipOfClawToUSDistance + 5) {
			lowest = manager.hm.ultrasonicPoller.getUSReading(1);

			// let the user know something was found.
			RConsole.println("Read less than 20");
			Sound.beepSequenceUp();
			manager.cm.setState(State.RECOGNIZE);
		} else if (lowest < 50) {
			/*
			 * add to the stack a point just slightly before the lowest reading.
			 */
			// add a point to the stack.
			manager.sm.nav.addToRoute(manager.sm.odo.getPosition()
					.addDistanceToPosition(
							lowest - Settings.tipOfClawToUSDistance));

			// reset the side ultrasonic sensors. This may not
			// be necessary.
			UltrasonicMotor.setDefaultPosition();
			manager.hm.ultrasonicPoller.resetUSP();

			while (!manager.hm.ultrasonicPoller.isSetup()) {
				manager.um.nap(200);
			}

			manager.cm.setState(State.SEARCH);
			Sound.beepSequence();
		} else {
			// just go to the destination.
			UltrasonicMotor.setDefaultPosition();
			manager.hm.ultrasonicPoller.pingAll();
			manager.hm.ultrasonicPoller.resetUSP();

			while (!manager.hm.ultrasonicPoller.isSetup()) {
				manager.um.nap(200);
			}

			manager.cm.setState(State.SEARCH);
			Sound.beep();
		}
	}
}
