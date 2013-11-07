package services;

import java.util.Stack;

import utilities.*;
import controllers.State;
import lejos.util.Timer;
import lejos.util.TimerListener;
import manager.*;

/**
 * 
 * Navigates the robot along a route, which is contained in this class
 * the route is set by the search and drop off controllers.
 * 
 * Further work required with scanAhead()
 * 
 * @author Danielle, Riley
 * 
 */
public class Navigation implements TimerListener {
	private Manager manager;
	private Point nextDestination;
	private Timer time;
	private final int UPDATE_PERIOD = 100;
	private final int MAX_FORWARD_SPEED = 8;
	private final int MAX_ROTATE_SPEED = 45;

	private Stack<Point> route;
	private Position currentPos;
	private double dX;
	private double dY;
	private double dH;

	private boolean scannedAhead;

	public Navigation(Manager manager) {
		this.manager = manager;
		this.route = initializeRoute();
		this.currentPos = new Position();
		this.time = new Timer(UPDATE_PERIOD, this);
		//start navigation right away
		this.time.start();
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
				// update the new headings to travel to
				setupDeltaPositonAndHeading();
				// see if we need to make a big turn
				if (Math.abs(dH) > 0.1) {
					// if we need to turn more than 0.2 rads or 0.1 for
					// completing a turn, call the turnTo method
					// otherwise we can adjust small angle errors by slowing
					// one wheel down slightly
					turnTo();
				} else if (Math.abs(dX) > 1 || Math.abs(dY) > 1) {
					//scan ahead only once facing the correct orientation, then travelTo that destination.
					if (!scannedAhead) {
						manager.hm.drive.stop();
						pause();
						manager.sm.obstacleAvoidance.scanAhead();
						start();
					}
					travelTo();
				} else {
					//stop the motors, reset scanning state and get next destination. 
					manager.hm.drive.stop();
					scannedAhead = false;
					route.pop();
				}
			}
		}
	}
	
	private void pause() {
		this.time.stop();
	}
	
	private void start() {
		this.time.start();
	}
	
	private void setupDeltaPositonAndHeading() {
		currentPos = manager.sm.odo.getPosition();

		// Distance between where we are and where we need to travel to
		dX = nextDestination.x - currentPos.x;
		dY = nextDestination.y - currentPos.y;

		// the required theta to travel to our destination
		double theta = Math.atan2(dY, dX);
		
		// adjust to [0, 360]
		theta = Angle.principleAngle(theta);

		// error in theta
		dH = theta - currentPos.theta;
		
		// adjust to [0, 360]
		dH = Angle.principleAngle(dH);
	}

	public void travelTo() {
		/*
		 * For minor angle corrections
		 */
		int dL = calculateRotationSpeed(dH);

		// Distance to destination
		double distanceToTravel = (dX * Math.cos(currentPos.theta))
								+ (dY * Math.sin(currentPos.theta));
		// high speed
		if (distanceToTravel > 1) {
			manager.hm.drive.setSpeeds(MAX_FORWARD_SPEED, dL);
		}
		// close to target so go really slow, so that we don't overshoot
		else {
			manager.hm.drive.setSpeeds(MAX_FORWARD_SPEED / 5, dL / 3);
		}

	}

	public void turnTo() {

		// if angle error greater than 0.6 deg
		if (Math.abs(dH) >= 0.01) {
			manager.hm.drive.setSpeeds(0, calculateRotationSpeed(dH));
		} else {
			// we have finished turning
			manager.hm.drive.stop();
		}
	}
	
	public int calculateRotationSpeed(double dH) {
		if (Math.abs(dH) > 0.01) {
			// if error positive and greater than 0.2 rad -> max speed CCW
			if (dH > 0.15) {
				return -MAX_ROTATE_SPEED;
			}
			// positive error, between .05 and .15 , so turn slow CCW to prevent overshoot
			else if (dH > .05) {
				return -MAX_ROTATE_SPEED / 4;
			}
			// positive error, but close to 0, so turn slow CCW to prevent overshoot
			else if (dH > 0) {
				return -MAX_ROTATE_SPEED / 8;
			}
			
			// if error negative and greater than 0.2 rad -> max speed CW
			if (dH < 0.15) {
				return MAX_ROTATE_SPEED;
			}
			// negative error, between .05 and .15 , so turn slow CW to prevent overshoot
			else if (dH < .05) {
				return MAX_ROTATE_SPEED / 4;
			}
			// negative error, but close to 0, so turn slow CW to prevent overshoot
			else if (dH < 0) {
				return MAX_ROTATE_SPEED / 8;
			}
		}
		//if this point is reached, dH is basically zero (<0.6deg)
		return 0;
	}

}
