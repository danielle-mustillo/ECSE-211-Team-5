package services;

import hardwareAbstraction.UltrasonicMotor;
import java.util.Stack;
import utilities.*;
import controllers.State;
import lejos.nxt.Sound;
import lejos.nxt.comm.RConsole;
import lejos.util.Timer;
import lejos.util.TimerListener;
import manager.*;

/**
 * 
 * Navigates the robot along a route, which is contained in this class the route
 * is set by the search and drop off controllers.
 * <p>
 * The navigation routine works in the following fashion 1. The robot will check
 * if there are waypoints for the robot to head towards. These waypoints would
 * be defined by other classes, specifically the controllers. 2. The robot will
 * turn towards that waypoint until its approximately lined up. 3. The robot
 * will scan ahead, either just driving, adding points to the stack or calling
 * the {@link Recognize} controller. 4. Once the robot got to the destination,
 * the next waypoint will be popped from the stack.
 * <p>
 * This class makes heavy use of the {@link Odometer} class to decide what to do
 * (rotate, drive, etc) and the {@link hardwareAbstraction.Drive} hardware abstraction class to
 * control the motors. The helper classes {@link utilities.Angle} is used to do angle
 * processing and {@link utilities.Point} to store the different waypoints to go to.
 */
public class Navigation implements TimerListener {
	private Manager manager;

	// properties of the controller
	private Timer time;
	private final int UPDATE_PERIOD = 100;
	private final int MAX_FORWARD_SPEED = 8;
	private final int MAX_ROTATE_SPEED = 35;

	// the next points to go to.
	private Point nextDestination;
	private Stack<Point> route; //contains the waypoints.
	private Stack<Point> storedRoute;

	// odometry values.
	private Position currentPos;
	private double dX;
	private double dY;
	private double dH;

	// control of the side ultrasonic sensors.
	private boolean scannedAhead;

	public Navigation(Manager manager) {
		this.manager = manager;
		this.route = initializeRoute();
		this.currentPos = new Position();
		this.time = new Timer(UPDATE_PERIOD, this);
	}

	private Stack<Point> initializeRoute() {
		return new Stack<Point>();
	}

	/**
	 * This method conducts the actual navigation. The issue with navigation
	 * however is that a considerable amount of scanning and point setting must
	 * be done here. This is not ideal. Furthermore, the final product
	 * experienced a considerable amount of problems as a result of new changes.
	 * The robot attempted to "overnavigate" to points and spent much time
	 * rotating around a destination it intended to go to.
	 */
	@Override
	public void timedOut() {
		// the controller only activates when told to.
		if (manager.cm.getState() == State.SEARCH
				|| manager.cm.getState() == State.DROP_OFF
				|| manager.cm.getState() == State.RECOGNIZE
				|| manager.cm.getState() == State.JUST_TRAVEL) {

			// if there is nothing on the stack of destinations, do nothing.
			if (route.empty()) {
				// nothing is done
			} else {
				// pull up the next point to go to.
				nextDestination = route.peek();

				// update the new headings to travel to
				setupDeltaPositonAndHeading();

				// see if we need to make a big turn
				if (Math.abs(dH) > 0.1) {
					/*
					 * if we need to turn more than 0.2 rads or 0.1 for
					 * completing a turn, call the turnTo method otherwise we
					 * can adjust small angle errors by slowing one wheel down
					 * slightly
					 */
					turnTo(dH);
				} else if (Math.abs(dX) > 1 || Math.abs(dY) > 1) {
					
					/*
					 * This controller will for the moment not scan ahead if the
					 * robot currently is in drop_off controller. However, this
					 * is bugged and not the correct functionality for the
					 * moment. Future work would require the robot scan ahead
					 * with only the two side ultrasonic sensors when the robot
					 * is carrying at least one block.
					 */
					if (!scannedAhead
							&& manager.cm.getState() != State.DROP_OFF) {
						scannedAhead = true;
						manager.sm.obstacleAvoidance.scanAhead();
					} else {
						travelTo();
						RConsole.println("Not scanning ahead");
					}
				} else {

					// stop the motors, reset scanning state and get next
					// destination (if there is one).
					manager.hm.drive.stop();
					scannedAhead = false;
					if (!route.empty())
						route.pop();
				}

			}
		}
	}

	/**
	 * Pauses this service by stopping the timer.
	 */
	public void pause() {
		this.time.stop();
		this.time = null;
	}

	/**
	 * Restarts this service by re-initalizing the timer.
	 */
	public void start() {
		RConsole.println("Navigation started");
		this.time = new Timer(UPDATE_PERIOD, this);
		this.time.start();
	}

	/**
	 * This method will store the old route temporarily and shift to a new
	 * route. This is used when certain operations need a specific route, but we
	 * don't want the old points of interest destroyed.
	 * 
	 * @param alternate
	 *            If true, the alternate route is engaged. Else the main route
	 *            is engaged.
	 */
	public void alternateRoute(boolean alternate) {
		if (alternate) {
			storedRoute = route;
			route = new Stack<Point>();
		} else {
			route = storedRoute;
			storedRoute = new Stack<Point>();
		}

	}

	/**
	 * Set's up the odometry class variables as necessary. This is what
	 * navigation compares against. Relies heavily on the odometer.
	 */
	private void setupDeltaPositonAndHeading() {
		currentPos = manager.sm.odo.getPosition();

		// Distance between where we are and where we need to travel to
		dX = nextDestination.x - currentPos.x;
		dY = nextDestination.y - currentPos.y;

		// the required theta to travel to our destination
		double theta = Math.atan2(dY, dX);

		// adjust to [0, 2PI]
		theta = Angle.principleAngle(theta);

		// set dH to the difference of theta and currentTheta adjust to [-PI,
		// PI]
		dH = Angle.minimumAngle(currentPos.theta, theta);
	}

	/**
	 * Travels to a destination by driving forward. Also does small amounts of
	 * angle correction as well.
	 */
	public void travelTo() {
		/*
		 * For minor angle corrections
		 */
		int dL = calculateRotationSpeed(dH);

		// Distance to destination
		double distanceToTravel = (dX * Math.cos(currentPos.theta))
				+ (dY * Math.sin(currentPos.theta));
		// high speed
		if (distanceToTravel > 3) {
			manager.hm.drive.setSpeeds(MAX_FORWARD_SPEED, dL);
		}
		// close to target so go really slow, so that we don't overshoot
		else {
			manager.hm.drive.setSpeeds(MAX_FORWARD_SPEED / 5, dL / 3);
		}

	}

	/**
	 * Does the majority of the angle correction. Stops the forward motion of
	 * the robot and simply rotates.
	 * 
	 * @param dH
	 *            The angle to turn to. This sets the drive controller.
	 */
	public void turnTo(double dH) {

		// if angle error greater than 0.6 deg
		if (Math.abs(dH) >= 0.01) {
			manager.hm.drive.setSpeeds(0, calculateRotationSpeed(dH));
		} else {
			// we have finished turning
			manager.hm.drive.stop();
		}
	}

	/**
	 * This method rotates the robot forcefully. It ignores whatever waypoints
	 * the robots have.
	 * 
	 * @param angle
	 *            The specific angle to turn to.
	 */
	public void turnToComplete(double angle) {

		angle = Angle.principleAngle(angle);
		// set dH to the difference of theta and currentTheta adjust to [-PI,
		// PI]
		double dH = Angle.minimumAngle(manager.sm.odo.getTheta(), angle);

		while (Math.abs(dH) > 0.01) {
			dH = Angle.minimumAngle(manager.sm.odo.getTheta(), angle);
			turnTo(dH);
			manager.um.nap(70);
		}

		manager.hm.drive.stop();

	}

	/**
	 * This method allows the robot to slightly adjust its forward speeds to
	 * adjust for small angle discrepancies without calling the turnTo method.
	 * Much more graceful movement.
	 * 
	 * @param dH	The angle the robot is off from its desired angle.
	 * @return	The speed to rotate. This is passed directly to {@link hardwareAbstraction.Drive}
	 */
	public int calculateRotationSpeed(double dH) {
		if (Math.abs(dH) > 0.01) {
			// if error positive and greater than 0.2 rad -> max speed CCW
			if (dH > 0.3) {
				return -MAX_ROTATE_SPEED;
			}
			// positive error, between .05 and .15 , so turn slow CCW to prevent
			// overshoot
			else if (dH > .15) {
				return -MAX_ROTATE_SPEED / 2;
			}
			// positive error, between .05 and .15 , so turn slow CCW to prevent
			// overshoot
			else if (dH > .05) {
				return -MAX_ROTATE_SPEED / 6;
			}
			// positive error, but close to 0, so turn slow CCW to prevent
			// overshoot
			else if (dH > 0.01) {
				return -MAX_ROTATE_SPEED / 16;
			}

			// if error negative and greater than 0.2 rad -> max speed CW
			if (dH < -0.3) {
				return MAX_ROTATE_SPEED;
			}
			// negative error, between .05 and .15 , so turn slow CW to prevent
			// overshoot
			else if (dH < -.15) {
				return MAX_ROTATE_SPEED / 2;
			}
			// negative error, between .05 and .15 , so turn slow CW to prevent
			// overshoot
			else if (dH < -.05) {
				return MAX_ROTATE_SPEED / 6;
			}
			// negative error, but close to 0, so turn slow CW to prevent
			// overshoot
			else if (dH < -0.01) {
				return MAX_ROTATE_SPEED / 16;
			}
		}
		// if this point is reached, dH is basically zero (<0.6deg)
		return 0;
	}

	// accessors and mutators
	public Stack<Point> getRoute() {
		return route;
	}

	public void setRoute(Stack<Point> route) {
		this.route = route;
	}

	/**
	 * Adds a {@link utilities.Point} to the route. Resets scan ahead because this will be the next point to travel to. It is executed right away.
	 * @param xy	The point to travel to.
	 */
	public void addToRoute(Point xy) {
		this.scannedAhead = false;
		this.route.push(xy);
	}
}
