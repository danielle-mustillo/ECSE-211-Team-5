/* Danielle Mustillo, Nathaniel Anthonisen
 * 260533476, 260470621
 * ECSE 211 - Design Principles and Methods
 * October 08, 2013
 */
/*
 *Odometer.java
 */

import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.comm.RConsole;
import lejos.util.Timer;
import lejos.util.TimerListener;

public class Odometer implements TimerListener {
	public static final int DEFAULT_PERIOD = 25;
//	private TwoWheeledRobot robot;
	private Timer odometerTimer;
	private Object lock;
	private double x, y, theta;
	private double [] oldDH, dDH;
	private NXTRegulatedMotor leftMotor, rightMotor;
	private double leftRadius = 2.08, rightRadius = 2.08, width = 15.24;
	
	public Odometer() {
		// initialise variables
		odometerTimer = new Timer(DEFAULT_PERIOD, this);
		x = 0.0;
		y = 0.0;
		theta = 0.0;
		leftMotor = Motor.A;
		rightMotor = Motor.C;
		oldDH = new double [2];
		dDH = new double [2];
		lock = new Object();
		odometerTimer.start();
	}

// extra constructors not needed.
//	public Odometer(TwoWheeledRobot robot) {
//		this(robot, DEFAULT_PERIOD, false);
//	}
//	
//	public Odometer(TwoWheeledRobot robot, boolean start) {
//		this(robot, DEFAULT_PERIOD, start);
//	}
//	
//	public Odometer(TwoWheeledRobot robot, int period) {
//		this(robot, period, false);
//	}
	
	public void timedOut() {
		dDH = getDisplacementAndHeading();
		dDH[0] -= oldDH[0];
		dDH[1] -= oldDH[1];
		
		// update the position in a critical region
		synchronized (lock) {
			theta += dDH[1];
			theta = fixDegAngle(theta);
			
			x += dDH[0] * Math.sin(Math.toRadians(theta));
			y += dDH[0] * Math.cos(Math.toRadians(theta));
		}
		
		oldDH[0] += dDH[0];
		oldDH[1] += dDH[1];
	}
	
	// static 'helper' methods
	/** Computes the minimum angle the robot is heading, between 0 and 360.
	 * @param angle in degrees of any between -360 and 720.
	 * @return angle in degrees between 0 and 360
	 */
	public static double fixDegAngle(double angle) {		
		if (angle < 0.0)
			angle = 360.0 + (angle % 360.0);
		
		return angle % 360.0;
	}
	
	public static double minimumAngleFromTo(double a, double b) {
		double d = fixDegAngle(b - a);
		
		if (d < 180.0)
			return d;
		else
			return d - 360.0;
	}
	
	// accessors
	public void getPosition(double [] pos) {
		synchronized (lock) {
			pos[0] = x;
			pos[1] = y;
			pos[2] = theta;
		}
	}
	
	public double getX() {
		double result;

		synchronized (lock) {
			result = x;
		}

		return result;
	}

	public double getY() {
		double result;

		synchronized (lock) {
			result = y;
		}

		return result;
	}

	public double getTheta() {
		double result;

		synchronized (lock) {
			result = theta;
		}

		return result;
	}
	
	public double getStandardTheta() {
		double result = getTheta();
		if(result > 180) result -= 360;
		return result;
	}
	
	// mutators
	public void setPosition(double [] pos, boolean [] update) {
		synchronized (lock) {
			if (update[0]) x = pos[0];
			if (update[1]) y = pos[1];
			if (update[2]) theta = pos[2];
		}
	}
	
	public void setX(double x) {
		synchronized (lock) {
			this.x = x;
		}
	}

	public void setY(double y) {
		synchronized (lock) {
			this.y = y;
		}
	}

	public void setTheta(double theta) {
		synchronized (lock) {
			this.theta = theta;
		}
	}
	
	/**Taken from code given in lab 4
	 * @return double array with 2 elements, displacement in position 0 and heading in position 1.
	 */
	public double[] getDisplacementAndHeading() {
		//initialize
		double data[] = new double[2];
		int leftTacho, rightTacho;
		
		//get tacho
		leftTacho = leftMotor.getTachoCount();
		rightTacho = rightMotor.getTachoCount();
		
		//compute displacement and heading based on lab 2.
		data[0] = (leftTacho * leftRadius + rightTacho * rightRadius) *	Math.PI / 360.0;
		data[1] = (leftTacho * leftRadius - rightTacho * rightRadius) / width;
		return data;
	}
}
