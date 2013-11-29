package services;

import controllers.State;
import utilities.*;
import lejos.nxt.Sound;
import lejos.nxt.comm.RConsole;
import lejos.util.Timer;
import lejos.util.TimerListener;
import manager.*;

/**
 * Localization handles all initial localization needs, both Ultrasonic and Line
 * localization. It will also account for the different starting corners set in
 * {@link Settings}
 */
public class Localization implements TimerListener {

	private Manager manager;
	private Timer timer;

	/**
	 * Speed in deg/sec of which to rotate during localization
	 */
	private double ROTATION_SPEED = 30;

	/**
	 * Period to check ultrasonic sensor in ms
	 */
	private final int UPDATE_PERIOD = 20;

	/**
	 * threshold distance in cm, to determine if we are at a critical angle
	 */
	private final int THRESHOLD = 30;

	/**
	 * The type of detection to use for {@link angleA}
	 */
	private boolean rising;
	/**
	 * The value of the first critical angle
	 */
	private double angleA;
	/**
	 * Value of the second critical angle
	 */
	private double angleB;

	/**
	 * True if lineLocalization has been setup
	 */
	private boolean lineLocalization;
	/**
	 * Number of lines detected by the right sensor
	 */
	private int rightLineCount;
	/**
	 * number of lines detected by the left sensor. Offset by 4
	 */
	private int leftLineCount;
	/**
	 * Array of odometer headings when the grid lines are detected. (0-3) ->
	 * right, (4-7) -> left
	 */
	private double[] lineDetectedHeadings = new double[8];

	// public boolean corrected = false;

	/**
	 * Localization constructor, initializes Timer for TimerListener
	 * 
	 * @param manager
	 */
	public Localization(Manager manager) {
		this.manager = manager;
		this.timer = new Timer(UPDATE_PERIOD, this);
	}

	/**
	 * Starts the localization process
	 * 
	 * It will take the current center ultrasonic reading to determine if the
	 * robot is facing a wall or the field. Based on this it will decide whether
	 * the robot is to do falling then rising localization or rising then rising
	 * localization
	 * 
	 */
	public void start() {

		// Ensure the Center ultrasonic is at a good height and wait till it is
		// done
		// manager.um.nap(Forklift.setHeight(Forklift.ForkliftState.SCAN_HEIGHT_LOW));

		// Retrieves center Ultrasonic reading
		int usReading = updateUltrasonic();

		// Ultrasonic poller no yet ready
		if (usReading < 4) {
			manager.um.nap(120);
			start();
		}
		// Facing a wall
		else if (usReading < THRESHOLD) {
			rising = true;

		}
		// currently not facing a wall, use falling edge, then rising edge
		else if (usReading > THRESHOLD) {
			rising = false;

		}
		// on the threshold, so start moving then try to start again
		else {

			manager.hm.drive.setSpeeds(0, ROTATION_SPEED);
			manager.um.nap(50);
			start();
			return;
		}

		angleA = Double.NaN;
		angleB = Double.NaN;
		lineDetectedHeadings[3] = Double.NaN;

		timer.start();

	}

	public void stop() {
		timer.stop();
		manager.hm.drive.stop();
	}

	/**
	 * Controls Localization. Calls relevant helper methods depending on stage
	 * of localization
	 * (ultrasonic->prepareLineLocalization->lineLocalization->updatePosition
	 * &adjustForStartingCorner)
	 */
	public void timedOut() {
		// ultrasonic localization not complete.
		if (Double.isNaN(angleB)) {
			ultrasonicLocalization();
		}
		// //not finished line localization
		else if (leftLineCount < 4 || rightLineCount < 4) {
			// move to correct starting orientation for line localization
			if (!lineLocalization) {
				prepareLineLocalization();
			}
			// Carry out line localization
			else {
				lineLocalization();
			}
		}
		// localization complete, update position
		else {
			stop();
			updatePosition();
			adjustForStartingCorner();
			manager.cm.setState(State.SEARCH);
		}
	}

	/**
	 * If the robot started facing the fall the robot will do rising, rising
	 * edge detection (angleA, angleB) if the robot started facing the field the
	 * robot will do falling edge, rising edge (angleA, angleB)
	 */
	private void ultrasonicLocalization() {
		// retrieve current reading
		int distance = updateUltrasonic();

		// Angle A is not yet set
		if (Double.isNaN(angleA)) {
			// if doing rising edge detection for Angle A, rotate CCW
			if (rising) {
				manager.hm.drive.setSpeeds(0, -ROTATION_SPEED);

				// No longer seeing the wall -> found angle A
				if (distance > THRESHOLD) {
					Sound.beep();
					angleA = manager.sm.odo.getTheta();
					RConsole.println(String.valueOf(angleA));
					manager.hm.drive.setSpeeds(0, ROTATION_SPEED);
				}
			}
			// Doing falling edge detection for Angle A, rotate CW
			else {
				manager.hm.drive.setSpeeds(0, ROTATION_SPEED);
				// no longer seeing the field -> found angle A
				if (distance < THRESHOLD) {
					Sound.beep();
					manager.hm.drive.stop();
					angleA = manager.sm.odo.getTheta();
					RConsole.println(String.valueOf(angleA));
				}
			}
		}
		// Angle B is not yet set
		else {
			manager.hm.drive.setSpeeds(0, ROTATION_SPEED);
			// if no longer seeing a wall -> found angle B
			// update odometer theta and stop
			if (distance > THRESHOLD
					&& Math.abs(angleA - manager.sm.odo.getTheta()) > 1) {
				Sound.beep();
				angleB = manager.sm.odo.getTheta();
				RConsole.println(String.valueOf(angleB));
				updateTheta();
				manager.hm.drive.setSpeeds(0, 0);
			}
		}
	}

	/**
	 * Updates Odometer theta based on the results of ultrasonic sensor
	 * localization
	 */
	private void updateTheta() {
		double deltaTheta = -(angleA + angleB) / 2;

		// The exact values for rising edge have not been found!
		if (rising) {
			// Depending on what angle is bigger, offset deltaTheta to the
			// correct amount
			if (angleA > angleB) {
				deltaTheta += 5.0 * Math.PI / 4.0;
			} else {
				deltaTheta += Math.PI / 4.0;
			}
		} else {
			// Depending on what angle is bigger, offset deltaTheta to the
			// correct amount
			if (angleA > angleB) {
				deltaTheta += 4.45 * Math.PI / 4.0;
			} else {
				deltaTheta += 0.45 * Math.PI / 4.0;
			}
		}

		// update the odometer
		manager.sm.odo.adjustPosition(0, 0, deltaTheta);

	}

	/**
	 * Calls checkLineSensor for each lineSensor
	 */
	private void lineLocalization() {
		// right sensor
		checkLineSensor(true);
		// left sensor
		checkLineSensor(false);
	}

	/**
	 * Updates the odometer's position based on line localization results Uses
	 * method similar to lab 5
	 */
	private void updatePosition() {

		double thetaXminus = (lineDetectedHeadings[0] + lineDetectedHeadings[4])
				/ 2.0 + ((lineDetectedHeadings[4] < Math.PI) ? Math.PI : 0); // Correction
																				// term,
																				// in
																				// case
																				// the
																				// branch
																				// cut
																				// has
																				// been
																				// passed
		double thetaYminus = (lineDetectedHeadings[3] + lineDetectedHeadings[7]) / 2.0;
		double thetaYplus = (lineDetectedHeadings[1] + lineDetectedHeadings[5]) / 2.0;
		double thetaXplus = (lineDetectedHeadings[2] + lineDetectedHeadings[6]) / 2.0;

		double thetaX = thetaXminus - thetaXplus;
		double thetaY = thetaYplus - thetaYminus;

		double x = -Settings.LS_OFFSET * Math.cos(thetaY / 2.0);
		double y = -Settings.LS_OFFSET * Math.cos(thetaX / 2.0);

		double dThetaX = -Math.PI / 2.0 + thetaX / 2.0 - thetaXminus;
		double dThetaY = -Math.PI - thetaYminus - thetaY / 2.0;

		double dTheta = (dThetaX + dThetaY) / 2.0;

		manager.sm.odo.adjustPosition(x, y, dTheta);
	}

	/**
	 * Ensures that the robot is facing towards the center of the field before
	 * starting line localization This way the light sensors will consistently
	 * cross the lines in the same order every time
	 */
	private void prepareLineLocalization() {
		// Heading to big, rotate CW
		if (manager.sm.odo.getTheta() > (Math.PI / 4 + 0.2)) {
			manager.hm.drive.setSpeeds(0, ROTATION_SPEED);
		}
		// Heading to small, rotate CCW
		else if (manager.sm.odo.getTheta() < (Math.PI / 4 - 0.2)) {
			manager.hm.drive.setSpeeds(0, -ROTATION_SPEED);
		}
		// Heading is in acceptable starting region, start localization
		else {
			Sound.buzz();
			manager.hm.drive.setSpeeds(0, ROTATION_SPEED);
			lineLocalization = true;
			rightLineCount = 0;
			leftLineCount = 4;
		}

	}

	/**
	 * Adjusts the localization position for the starting corner
	 */
	private void adjustForStartingCorner() {
		double x1 = manager.sm.odo.getX();
		double y1 = manager.sm.odo.getY();
		double deltaTheta = manager.sm.odo.getTheta();

		if (Settings.startingCorner == StartingCorner.BOTTOM_RIGHT) {
			deltaTheta += Math.PI / 2;
			double x2 = -y1;
			y1 = x1;
			x1 = x2;
			x1 += (Settings.FIELD_X - 2) * Settings.TILE_SIZE;
			// return 3.0 * Math.PI / 4.0;

		} else if (Settings.startingCorner == StartingCorner.TOP_RIGHT) {
			deltaTheta += Math.PI;
			x1 *= -1;
			y1 *= -1;
			x1 += (Settings.FIELD_X - 2) * Settings.TILE_SIZE;
			y1 += (Settings.FIELD_Y - 2) * Settings.TILE_SIZE;
			// return 5.0 * Math.PI / 4.0;
		} else if (Settings.startingCorner == StartingCorner.TOP_LEFT) {
			deltaTheta -= Math.PI / 2;
			double y2 = -x1;
			x1 = y1;
			y1 += y2 + (Settings.FIELD_Y - 2) * Settings.TILE_SIZE;
			// return 7.0 * Math.PI / 4.0;
		}

		manager.sm.odo.setPosition(new Position(x1, y1, deltaTheta));

	}

	/**
	 * Calls getUSReading from {@link UltrasonicPoller} for the center
	 * ultrasonic sensor
	 * 
	 * @return current center ultrasonic reading
	 */
	private int updateUltrasonic() {
		return manager.hm.ultrasonicPoller.getUSReading(1);
	}

	/**
	 * Updates the lineDetectedHeadings[] based on whether a new line has been
	 * detected by {@link LinePoller}
	 * 
	 * @param rightSensor
	 *            -> true if the right sensor is to be checked, false if the
	 *            left sensor is to be checked
	 */
	private void checkLineSensor(boolean rightSensor) {
		// True if a new line has been detected
		if (manager.hm.linePoller.enteringLine((rightSensor) ? 1 : 0)) {
			// if it was the right sensor that detected the line
			// add the current heading and increase rightLineCount
			if (rightSensor && rightLineCount < 4) {
				Sound.beep();
				lineDetectedHeadings[rightLineCount] = manager.sm.odo
						.getTheta();
				rightLineCount++;
			}
			// Otherwise if it was the left sensor that detected the line
			// add current heading and increase leftLineCount
			else if (leftLineCount < 8) {
				lineDetectedHeadings[leftLineCount] = manager.sm.odo.getTheta();
				leftLineCount++;
				Sound.beep();
			}
		}
	}
}
