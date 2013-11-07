package services;

import lejos.nxt.NXTRegulatedMotor;
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
	private Timer timer;
	private Drive drive;
	
	private final int UPDATE_PERIOD = 25;
	
	private Object lock;
	private double x, y, theta;
	private double [] oldDH, dDH;

	public Odometer(Manager manager) {		
		x = 0.0;
		y = 0.0;
		theta = Math.PI/2;
		oldDH = new double [2];
		dDH = new double [2];
		lock = new Object();
		drive = manager.hm.drive;
		timer = new Timer(UPDATE_PERIOD, this);
		timer.start();
	}
	
	/**
	 * Updates the odometer every UPDATE_PERIOD
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
	 * returns current position of the robot
	 * @param pos
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
	 * returns the current heading of the robot
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
	 * returns current y coordinate of the robot
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
	 * returns current x coordinate of the robot
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
	 * set the current position
	 * @param pos
	 * @param update
	 */
	public void setPosition(Position pos) {
		synchronized (lock) {
			x = pos.x;
			y = pos.y;
			theta = pos.theta;
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
			theta += dTheta;
		}
	}
	
	
	
	
}
