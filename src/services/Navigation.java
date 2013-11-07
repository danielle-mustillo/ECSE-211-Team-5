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
	private Odometer odo;
	private Point nextDestination;

	private double[] pos;
	private double dX;
	private double dY;
	private double dH;

	private boolean scannedAhead;

	public Navigation(Manager manager) {
		this.manager = manager;
		this.route = initializeRoute();
		this.odo = manager.sm.odo;
		this.pos = new double[3];
	}

	// TODO an initializer of default points should be constructed here.
	private Stack<Point> initializeRoute() {
		return new Stack<Point>();
	}

	@Override
	public void timedOut() {
		nextDestination = route.peek();
		if (nextDestination == null) {
			// nothing is done
		} else {
			// if navigation must be done
			if (manager.cm.getState() == State.SEARCH
					|| manager.cm.getState() == State.DROP_OFF) {
				// update the changes in heading
				setupDeltaPositonAndHeading();
				// see if we need to make a big turn
				if (Math.abs(dH) > 5) {
					// if we need to turn more than 0.2 rads or 0.1 for
					// completing a turn, call the turnTo method
					// otherwise we can adjust small angle errors by slowing
					// one
					// wheel down slightly
					turnTo();
				} else if (Math.abs(dX) > 1 || Math.abs(dY) > 1) {
					//scan ahead only once facing the correct orientation, then travelTo that destination.
					if (!scannedAhead)
						manager.sm.obstacleAvoidance.scanAhead();
					travelTo();
				} else {
					//stop the motors, reset scanning state and get next destination. 
					robot.stop();
					scannedAhead = false;
					route.pop();
				}
			}
		}
	}
	
	private void setupDeltaPositonAndHeading() {
		odo.getPosition(pos);

		double currentX = pos[0];
		double currentY = pos[1];
		double currentTheta = pos[2];

		// Distance between where we are and where we need to travel to
		dX = nextDestination.getX() - currentX;
		dY = nextDestination.getY() - currentY;

		// the required theta to travel to our destination
		double theta = Math.atan2(dY, dX) * 180.0 / Math.PI;
		// adjust to [0, 360]
		theta = (theta < 0) ? 360 + theta : theta;

		// error in theta
		dH = theta - currentTheta;

		// Change deltaTheta to [0,360)
		if (dH > 180) {
			dH -= 360;
		} else if (dH < -180) {
			dH += 360;
		}
	}

	public void travelTo() {
		/*
		 * For minor angle corrections
		 */

		// amount to change right wheel speed by
		int dL = 0;

		// If angle error is greater than 3 deg, make adjustment
		if (Math.abs(dH) > 0.5) {

			// if we are facing to the left of where we should
			// be,
			// slight right
			if (dH > 0) {

				dL = -5;
			}

			// if we are facing to the right of where we should
			// be,
			// slight left
			else if (dH < 0) {

				dL = 5;
			}
		}

		/*
		 * End minor angle corrections
		 */

		// Distance to destination
		double currentTheta = pos[2];
		double distanceToTravel = (dX * Math.cos(Math.toRadians(currentTheta)))
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

	public void turnTo() {

		// if angle error greater than 1 deg
		if (Math.abs(dH) >= 1) {
			// if error positive and greater than 5 deg -> max speed CCW
			if (dH > 8) {
				robot.setSpeeds(0, -MAX_ROTATE_SPEED);
			}
			// positive error, but close to 0, so turn slow CCW to
			// prevent
			// overshoot
			else if (dH > 0) {
				robot.setSpeeds(0, -MAX_ROTATE_SPEED / 4);
			}
			// error negative and less than 5deg -> max speed CW
			else if (dH < -8) {
				robot.setSpeeds(0, MAX_ROTATE_SPEED);
			}
			// negative error, but close to 0, so turn slow CW to
			// prevent
			// overshoot
			else if (dH < 0) {
				robot.setSpeeds(0, MAX_ROTATE_SPEED / 4);
			}
		} else {
			// we have finished turning
			robot.stop();
		}
	}

}
