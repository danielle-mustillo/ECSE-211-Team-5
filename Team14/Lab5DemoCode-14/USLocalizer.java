/* Danielle Mustillo, Nathaniel Anthonisen
 * 260533476, 260470621
 * ECSE 211 - Design Principles and Methods
 * October 08, 2013
 */
/*
 *USLocalizer.java
 */
import lejos.nxt.Sound;

public class USLocalizer {
	public enum LocalizationType {
		FALLING_EDGE, RISING_EDGE
	};

	public enum StartingPosition {
		FACING_WALL, FACING_OPEN
	};

	// constants
	private int threshold;

	// initalize the odometer and data.
	private Odometer odo;
	private SwitchBoard switches;

	private int bandwidth;
	// enum constants
	private StartingPosition startPos;

	/**
	 * The USLocalizer localizes the robot using exclusively the
	 * {@link UltrasonicSensor} instance passed in.
	 * <p>
	 * @param odo
	 *            the odometer instance currently in use within the robot.
	 * @param switches
	 *            the location where all data is stored.
	 * @param us	the instance of the ultrasonic sensor used to perform localization. 
	 */
	public USLocalizer(Odometer odo, SwitchBoard switches) {
		// Initialize the data sources
		this.odo = odo;
		this.switches = switches;

		// initialize useful constants
		this.bandwidth = 2; // allowed bandwidth to overcome to engage the
							// allotment of angle A,B
		this.threshold = 50; // wall reading that is ideal.
	}

	/**
	 * This method initializes the localization routine.It works based on the
	 * assumption the robot is along a diagonal of the map. It will update the
	 * {@link Odometer} with the new orientation once complete.
	 * <p>
	 * The thread is type safe. Sleeping threads are the only exceptions thrown,
	 * and this is handled within this method.
	 */
	public void doLocalization() {

		// initialize angles to be measured by ultrasonic sensor
		double angleA = 0.0, angleB = 0.0; // output angles for calculations.
		double deltaTheta = 0, correctTheta = 0.0; // intermediate values used
													// for localization
		double distance = switches.getUSReading(); // Distance obtained using a
													// fliter method
		// decide if facing wall or open area.
		if (switches.getUSReading() < threshold) {
			startPos = StartingPosition.FACING_WALL;
		} else {
			startPos = StartingPosition.FACING_OPEN;
		}

		// just assume were facing the open.
		Motors.rotateCounterClockwise();
		while (distance < 50) { // rotate robot until it does not sees a wall.
			distance = switches.getUSReading(); // get data along the way.
			nap(50);
		}
		// sleep for 1 second to avoid error
		nap(1000);

		// rotate robot until definitely sees the wall.
		Motors.rotateCounterClockwise();
		while (distance > 40) {
			distance = switches.getUSReading();
			nap(50);
		}

		// stop robot, it has seen a wall, latch angle
		Motors.stop();
		angleB = odo.getTheta(); // capture theta

		// avoid errror by sleeping.
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}

		// rotate robot in the other direction until it doesn't see the wall
		Motors.rotateClockwise();
		while (distance < 40) {
			distance = switches.getUSReading();
			nap(50);
		}
		// sleep for 1 second to avoid error
		nap(1000);

		// keep rotating robot until it sees a wall
		while (distance > 40) {
			distance = switches.getUSReading();
			nap(100);
		}

		// stop the robot
		Motors.stop();
		angleA = odo.getTheta(); // get angleA

		// calculating angle to be added to current Theta
		if (angleA > angleB) {
			deltaTheta = 225 - ((angleA + angleB) / 2);
		} else {
			deltaTheta = 45 - ((angleA + angleB) / 2);
		}
		// the new correction Theta is here
		correctTheta = odo.getTheta() + deltaTheta;

		Sound.beep();

		// orient the robot and set the odometer so that is 0,0,0
		odo.setPosition(new double[] { 0.0, 0.0, correctTheta }, new boolean[] {
				false, false, true });

		// choose direction to go
		double val = Odometer.minimumAngleFromTo(odo.getTheta(), 270);
		// choose smallest angle
		if (val < 0)
			Motors.rotateCounterClockwise();
		else
			Motors.rotateClockwise();
		// go to that angle.
		while (Math.abs(odo.getTheta() - 260) > 1.5) {
			nap(50);
		}
		Motors.stop();
		odo.setPosition(new double[] { 0.0, 0.0, 0.0 }, new boolean[] { true,
				true, true });
	}

	// sleep helper method
	private void nap(int time) {
		// short nap
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
