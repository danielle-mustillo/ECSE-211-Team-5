package services;

import java.util.Stack;

import utilities.*;
import controllers.State;
import lejos.util.TimerListener;
import manager.*;

/**
 * 
 * needs to implement timerlistener stack<Point> route timedOut (calls turnTo or
 * travelTo + obstacleAvoidance scan() depending on what one is needed) && only
 * be called while state is State.SEARCH || State.DROP_OFF turnTo travelTo
 * 
 * Once turn to face the direction to a new point, confer with obstacle
 * avoidance to scanAhead()
 * 
 * 
 * 
 * @author
 * 
 */
public class Navigation implements TimerListener {
	private Stack<Point> route;
	private Manager manager;
	private Point nextDestination;
	private Odometer odo;

	private static boolean navigating = false;
	private static boolean turning = false;
	private boolean paused = false;

	public Navigation(Manager manager) {
		this.manager = manager;
		this.route = initializeRoute();
		this.odo = manager.sm.odo;
	}

	// TODO an initializer of default points should be constructed here.
	private Stack<Point> initializeRoute() {
		return new Stack<Point>();
	}

	@Override
	public void timedOut() {
		//for the moment, I am not sure why this is timed out. Why is this not in search algorithm and used as a utility. I obviously did not get the memo.
		//
//		if (!navigating) {
			if (manager.cm.getState() == State.SEARCH
					|| manager.cm.getState() == State.DROP_OFF) {
//				navigating = true;
				if (nextDestination == null)
					nextDestination = route.pop();
				turnTo(nextDestination.angleTo(odo.getPosition()));
				travelTo(nextDestination);
//			}
		}
	}

	public void travelTo(Point xy) {
		double x = xy.getX();
		double y = xy.getY();

		// we're navigating
		navigating = true;

		long correctionStart, correctionEnd;

		// main loop
		travelLoop: while (navigating) {
			correctionStart = System.currentTimeMillis();

			if (!paused) {

				/*
				 * retrieve current position and calculate dX and dY
				 */
				double[] pos = new double[3];

				odo.getPosition(pos);

				double currentX = pos[0];
				double currentY = pos[1];
				double currentTheta = pos[2];

				// Distance between where we are and where we need to travel to
				double dX = x - currentX;
				double dY = y - currentY;

				/*
				 * if within 0.5cm (x&y) of the destination x,y stop otherwise
				 * continuing traveling
				 */
				if (Math.abs(dX) < 1 && Math.abs(dY) < 1) {
					navigating = false;
					robot.stop();
					break travelLoop;
				}
				/*
				 * We are not at our destination and the path is clear
				 */
				else {

					// the required theta to travel to our destination
					double theta = Math.atan2(dY, dX) * 180.0 / Math.PI;
					// adjust to [0, 360]
					theta = (theta < 0) ? 360 + theta : theta;

					// error in theta
					double deltaTheta = theta - currentTheta;

					// Change deltaTheta to [0,360)
					if (deltaTheta > 180) {
						deltaTheta -= 360;
					} else if (deltaTheta < -180) {
						deltaTheta += 360;
					}

					// see if we need to make a big turn
					if (Math.abs(deltaTheta) > 5) {
						// if we need to turn more than 0.2 rads or 0.1 for
						// completing a turn, call the turnTo method
						// otherwise we can adjust small angle errors by slowing
						// one
						// wheel down slightly
						turnTo(theta);

					}
					// We need to go straight (or relatively straight)
					else {

						/*
						 * For minor angle corrections
						 */

						// amount to change right wheel speed by
						int dL = 0;

						// If angle error is greater than 3 deg, make adjustment
						if (Math.abs(deltaTheta) > 0.5) {

							// if we are facing to the left of where we should
							// be,
							// slight right
							if (deltaTheta > 0) {

								dL = -5;
							}

							// if we are facing to the right of where we should
							// be,
							// slight left
							else if (deltaTheta < 0) {

								dL = 5;
							}
						}

						/*
						 * End minor angle corrections
						 */

						// Distance to destination
						double distanceToTravel = (dX * Math.cos(Math
								.toRadians(currentTheta)))
								+ (dY * Math.sin(Math.toRadians(currentTheta)));
						// high speed
						if (distanceToTravel > 3) {
							robot.setSpeeds(MAX_FORWARD_SPEED, dL);
						}
						// start to slow down
						else if (distanceToTravel > 1) {
							robot.setSpeeds(MAX_FORWARD_SPEED / 2, dL);
						}
						// go really slow, so that we don't overshoot
						else {
							robot.setSpeeds(MAX_FORWARD_SPEED / 5, dL / 3);
						}

					}
				}
			}
			// paused
			else {
				robot.setSpeeds(0, 0);
			}

			// this ensure the odometry correction occurs only once every
			// period

			correctionEnd = System.currentTimeMillis();

			if (correctionEnd - correctionStart < UPDATE_PERIOD) {
				try {
					Thread.sleep(UPDATE_PERIOD
							- (correctionEnd - correctionStart));
				} catch (InterruptedException e) {
					// there is nothing to be done here because it is not
					// expected that the odometry correction will be
					// interrupted by another thread
				}
			}
		}
	}

	public void turnTo(double angle) {

		// we are turning
		if (!turning)
			turning = true;

		long correctionStart, correctionEnd;

		// main loop
		while (turning) {
			correctionStart = System.currentTimeMillis();

			if (!paused) {

				// error in angle
				double deltaTheta = angle - odometer.getTheta();

				// convert to [-180,180] for minimal angle
				if (deltaTheta > 180) {
					deltaTheta -= 360;
				} else if (deltaTheta < -180) {
					deltaTheta += 360;
				}

				// if angle error greater than 1 deg
				if (Math.abs(deltaTheta) >= 1) {
					// if error positive and greater than 5 deg -> max speed CCW
					if (deltaTheta > 8) {
						robot.setSpeeds(0, -MAX_ROTATE_SPEED);
					}
					// positive error, but close to 0, so turn slow CCW to
					// prevent
					// overshoot
					else if (deltaTheta > 0) {
						robot.setSpeeds(0, -MAX_ROTATE_SPEED / 4);
					}
					// error negative and less than 5deg -> max speed CW
					else if (deltaTheta < -8) {
						robot.setSpeeds(0, MAX_ROTATE_SPEED);
					}
					// negative error, but close to 0, so turn slow CW to
					// prevent
					// overshoot
					else if (deltaTheta < 0) {
						robot.setSpeeds(0, MAX_ROTATE_SPEED / 4);
					}
				} else {
					// we have finished turning
					turning = false;
					robot.stop();
				}
			}
			// paused
			else {
				robot.setSpeeds(0, 0);
			}
			// this ensure the odometry correction occurs only once every period
			correctionEnd = System.currentTimeMillis();

			if (correctionEnd - correctionStart < UPDATE_PERIOD) {
				try {
					Thread.sleep(UPDATE_PERIOD
							- (correctionEnd - correctionStart));
				} catch (InterruptedException e) {
					// there is nothing to be done here because it is not
					// expected that the odometry correction will be
					// interrupted by another thread
				}
			}
		}

	}

}
