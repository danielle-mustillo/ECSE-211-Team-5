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
	private final int MAX_ROTATE_SPEED = 35;

	private Stack<Point> route;
	private Position currentPos;
	private double dX;
	private double dY;
	private double dH;

	private boolean scannedAhead;

	public Navigation(Manager manager) {
		RConsole.println("Navigation initialized");
		this.manager = manager;
		this.route = initializeRoute();
		this.currentPos = new Position();
		this.time = new Timer(UPDATE_PERIOD, this);
	}

	// TODO an initializer of default points should be constructed here.
	private Stack<Point> initializeRoute() {
		return new Stack<Point>();
	}

	@Override
	public void timedOut() {
		if (manager.cm.getState() == State.SEARCH
				|| manager.cm.getState() == State.DROP_OFF
				|| manager.cm.getState() == State.RECOGNIZE
				|| manager.cm.getState() == State.TESTING) {
			
			if (route.empty()) {
				// nothing is done
			} else {
				nextDestination = route.peek();
				// if navigation must be done

				// update the new headings to travel to
				setupDeltaPositonAndHeading();

				// see if we need to make a big turn
				if (Math.abs(dH) > 0.1) {
					// if we need to turn more than 0.2 rads or 0.1 for
					// completing a turn, call the turnTo method
					// otherwise we can adjust small angle errors by slowing
					// one wheel down slightly
					turnTo(dH);
				} else if (Math.abs(dX) > 1 || Math.abs(dY) > 1) {
					// RConsole.println(""+Math.abs(dX)+" "+Math.abs(dY));
					// scan ahead only once facing the correct orientation, then
					// travelTo that destination.
					// TODO comment back this code. Problematic code for the
					// moment.
					if (!scannedAhead) {
						manager.cm.setState(State.PAUSE);
						Sound.beep();
						RConsole.println("Scanning Ahead");
						scannedAhead = true;
						
						manager.hm.drive.stop();
						sleep(UltrasonicMotor.setForwardPosition());
						/*
						 * TODO this must be calibrated to the delay experienced
						 * on the ultrasonic sensor. I want to make sure that 5
						 * readings have been picked up by the ultrasonic
						 * sensor. Therefore 5 seconds should be long enough
						 * (until we can test otherwise).
						 */
						manager.hm.ultrasonicPoller.resetUSP();
						while(!manager.hm.ultrasonicPoller.isSetup()) {
							manager.um.nap(200);
						}
						int lowest = manager.hm.ultrasonicPoller.getLowestReading();
						manager.sm.lcdInfo.debugValue = lowest;
												
						if (lowest < 20) {
							RConsole.println("Read less than 20");
//							Sound.beepSequenceUp();
							//TODO comment this back in when recognize works. 
							manager.cm.setState(State.RECOGNIZE);
							Sound.twoBeeps();
							
						}
						else if (lowest < 50) {
							RConsole.println("Read less than 30");
							RConsole.println("Pushing the following to the stack" + manager.sm.odo.getPosition().addDistanceToPosition(lowest - 5));
							//route.push(manager.sm.odo.getPosition().addDistanceToPosition(lowest - Settings.clawToUSDistance));
							sleep(UltrasonicMotor.setDefaultPosition());
							manager.cm.setState(State.SEARCH);
							Sound.beepSequence();
						} else {
							sleep(UltrasonicMotor.setDefaultPosition());
							manager.hm.ultrasonicPoller.resetUSP();
							while(!manager.hm.ultrasonicPoller.isSetup()) {
								manager.um.nap(200);
							}
							manager.cm.setState(State.SEARCH);
							Sound.beep();
							// do no processing, just continue along doing nothing. 
						}
					} else {
						travelTo();
						RConsole.println("Not scanning ahead");
					}
				} else {
					
					// stop the motors, reset scanning state and get next
					// destination.
					manager.hm.drive.stop();
					scannedAhead = false;
					if(!route.empty())
						route.pop();
				}

			}
		}
	}

	public void pause() {
		this.time.stop();
		this.time = null;
	}

	public void start() {
		RConsole.println("Navigation started");
		this.time = new Timer(UPDATE_PERIOD, this);
		this.time.start();
	}

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

	public void turnTo(double dH) {

		// if angle error greater than 0.6 deg
		if (Math.abs(dH) >= 0.01) {
			manager.hm.drive.setSpeeds(0, calculateRotationSpeed(dH));
		} else {
			// we have finished turning
			manager.hm.drive.stop();
		}
	}

	public void turnToComplete(double angle) {

		angle = Angle.principleAngle(angle);
		// set dH to the difference of theta and currentTheta adjust to [-PI,
		// PI]
		double dH = Angle.minimumAngle(currentPos.theta, angle);

		while (Math.abs(dH) > 0.01) {
			dH = Angle.minimumAngle(currentPos.theta, angle);
			turnTo(dH);
			manager.um.nap(70);
		}

		manager.hm.drive.stop();

	}

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
	 * This method will export the route currently programmed and resets it to
	 * an empty route.
	 * 
	 * @return
	 */
	public Stack<Point> exportAndResetRoute() {
		Stack<Point> export = this.route;
		this.route = new Stack<Point>();
		return export;
	}

	public void addToRoute(Point xy) {
		this.scannedAhead = false;
		this.route.push(xy);
	}
	
	private void sleep(int time){
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
		}
	}
}
