package services;

import lejos.util.Timer;
import lejos.util.TimerListener;
import manager.*;
import utilities.*;
import hardwareAbstraction.Drive;

/**
 * Odometer -> keeps track of the robot's current position
 * modified from the odometer code given in Lab 4
 * 
 * @author Riley
 *
 */

public class Odometer implements TimerListener {
	/**
	 * Timer for TimerListener
	 */
	private Timer timer;
	/**
	 * Class variable for easy access to drive motors
	 */
	private Drive drive;
	/**
	 * Period to update odometer, in ms
	 */
	private final int UPDATE_PERIOD = 25;
	
	/**
	 * For synchronization
	 */
	private Object lock;
	/**
	 * Odometer variables
	 */
	private double x, y, theta;
	/**
	 * Used to find dx, dy and dtheta
	 */
	private double [] oldDH, dDH;

	/**
	 * Initializes x, y, theta.
	 * Initializes timer, and starts the timer
	 * @param manager
	 */
	public Odometer(Manager manager) {		
		x = 0.0;
		y = 0.0;
		theta = 0.0;
		oldDH = new double [2];
		dDH = new double [2];
		lock = new Object();
		drive = manager.hm.drive;
		timer = new Timer(UPDATE_PERIOD, this);
		timer.start();
	}
	
	/**
	 * Updates the odometer every {@link UPDATE_PERIOD }, based on the change in displacement and heading from {@link Drive}
	 * 
	 * Ensures theta is normalized between [0, 2PI)
	 */
	public void timedOut() {
		drive.getDisplacementAndHeading(dDH);
		//change in displacement and heading
		dDH[0] -= oldDH[0];
		dDH[1] -= oldDH[1];
		
		// update the position in a critical region
		synchronized (lock) {
			theta -= dDH[1];
			theta = Angle.principleAngle(theta);
			
			x += dDH[0] * Math.cos(theta);
			y += dDH[0] * Math.sin(theta);
		}
		
		//update old displacement and heading
		oldDH[0] += dDH[0];
		oldDH[1] += dDH[1];
		
	}
	
	/**
	 * Returns the current position of the robot, as according to the odometer
	 * @return 
	 */
	public Position getPosition() {
		Position pos = new Position();
		synchronized (lock) {
			pos.x = x;
			pos.y = y;
			pos.theta = theta;
		}
		
		return pos;
	}
	
	/**
	 * Returns the current heading of the robot (theta)
	 * @return
	 */
	public double getTheta() {
		double result;

		synchronized (lock) {
			result = theta;
		}

		return result;
	}
	
	/**
	 * Returns current y coordinate of the robot
	 * @return
	 */
	public double getY() {
		double result;

		synchronized (lock) {
			result = y;
		}

		return result;
	}
	
	/**
	 * Returns current x coordinate of the robot
	 * @return
	 */
	public double getX() {
		double result;

		synchronized (lock) {
			result = x;
		}

		return result;
	}
	
	/**
	 * Sets the current position of the odometer
	 * @param pos
	 */
	public void setPosition(Position pos) {
		synchronized (lock) {
			x = pos.x;
			y = pos.y;
			theta = Angle.principleAngle(pos.theta);
		}
	}
	
	/**
	 * Adjust the current position by:
	 * @param dx
	 * @param dy
	 * @param dTheta
	 */
	public void adjustPosition(double dx, double dy, double dTheta) {
		synchronized (lock) {
			x += dx;
			y += dy;
			theta = Angle.principleAngle(theta+dTheta);
		}
	}
}
