package controllers;

import hardwareAbstraction.Claw;
import hardwareAbstraction.Forklift;
import hardwareAbstraction.Forklift.ForkliftState;
import utilities.Point;
import utilities.Position;
import manager.Manager;

/**
 * The wall follower class will simply go around a block in a simple fashion. It
 * does not simply follow the wall but instead drives a certain distance 90
 * degrees offset from the obstacle until it clears the obstacle. This
 * controller is not entirely bug free and does encounter problems when there is
 * more than one obstacle in succession.
 * <p>
 * Future revisions would include more intelligence here. It would fix the
 * problem firstly. Then secondly, it would attempt to clear the wall with a
 * more intelligent method than driving a fixed distance away. Perhaps a method
 * to search for a fixed amount of time, and if that time elapsed then it would
 * be known the obstacle is a wall.
 */
public class WallFollower extends Controller {

	private Manager manager;
	private boolean left;

	public WallFollower(Manager manager) {
		this.manager = manager;
	}

	/**
	 * This method will reset the forklift to a useful state (scan height). Then
	 * the robot would back away from the object, turn around and drive to avoid
	 * the obstacle.
	 */
	public void run() {
		left = false;
		/*
		 * Stop re-execution and alternate the route for the moment. Reset the
		 * forklift and the claw.
		 */
		manager.cm.setState(State.PAUSE);
		manager.sm.nav.alternateRoute(true);
		Claw.releaseObject();
		Forklift.setHeight(ForkliftState.SCAN_HEIGHT_LOW);

		// drive backwards about 10 cm.
		manager.hm.drive.setSpeeds(-60, 0);
		sleep(1000);
		manager.hm.drive.stop();

		/*
		 * Turn right, drive a distance. Then turn right and drive further. The
		 * robot will have successfully cleared the block now.
		 */
		obstacleAvoid(40, Math.PI / 2);
		if (!left)
			obstacleAvoid(80, -Math.PI / 2);

		// exit conditions
		manager.sm.nav.alternateRoute(false);
		manager.cm.setState(State.SEARCH);
	}

	/**
	 * Sleeps the execution of this thread. Only useful for when the robot is
	 * paused or not re-executing the controllers.
	 * 
	 * @param time
	 *            The time in ms to do that.
	 */
	public static void sleep(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			// no error expected here.
		}
	}

	/**
	 * This is the obstacle avoidance algorithm. This method would need to be
	 * improved in future revisions based on the class comments.
	 * 
	 * @param distance
	 *            The distance to robot should travel forward.
	 * @param angle
	 *            The angle the robot should turn.
	 */
	private void obstacleAvoid(int distance, double angle) {
		Position currPos;
		Point destination;

		// Travels only for the moment. Travel to a certain point on the field.
		manager.cm.setState(State.JUST_TRAVEL);
		currPos = manager.sm.odo.getPosition();
		destination = currPos.addDisAndAngleToPosition(distance, angle);
		manager.sm.nav.addToRoute(destination);

		/*
		 * TODO This may be bugged portion. Does not let the robot assign more
		 * values to the route. This is why more than one block confuses this
		 * controller.
		 */
		while (!manager.sm.nav.getRoute().empty() && !left) {
			if (!manager.sm.nav.getRoute().peek().equals(destination))
				left = true;
			sleep(200);
		}
	}
}
