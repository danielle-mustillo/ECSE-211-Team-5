/* Danielle Mustillo, Nathaniel Anthonisen
 * 260533476, 260470621
 * ECSE 211 - Design Principles and Methods
 * September 24, 2013
 */

import lejos.nxt.Motor;

/*
 * Odometer.java
 */

public class OdometerOld extends Thread {
	// robot position
	private double x, y, theta;
	
	//constants of the robot
	private double wheelBase, leftRadius, rightRadius;

	// odometer update period, in ms
	private static final long ODOMETER_PERIOD = 25;

	// lock object for mutual exclusion
	private Object lock;

	// default constructor
	// added the radiuses since this is necessary to calibrate the odometer
	public OdometerOld (double leftRadius, double rightRadius, double wheelBase) {
		x = 0.0;
		y = 0.0;
		theta = 0.0; //in radians
		this.leftRadius = leftRadius;
		this.rightRadius = rightRadius;
		this.wheelBase = wheelBase;
		lock = new Object();
	}

	// run method (required for Thread)
	public void run() {
		long updateStart, updateEnd;
		
		double oldLeftDistance = 0;
		double oldRightDistance = 0;
		while (true) {
			updateStart = System.currentTimeMillis();
			// put (some of) your odometer code here
			
			//get distance traveled by both motors
			double leftDistance =  tachoCountToDistance(Motor.A.getTachoCount(), leftRadius);
			double rightDistance = tachoCountToDistance(Motor.C.getTachoCount(), rightRadius);
			
			//get the distance moved since last measurement of the motors and the centre of the bot
			double leftDeltaDistance =  leftDistance - oldLeftDistance;
			double rightDeltaDistance = rightDistance - oldRightDistance;
			double centreDeltaDistance = ( leftDeltaDistance + rightDeltaDistance ) / 2;
			
			//change in theta
			//in radians, use small angle assumption valid because we sample so fast
			double deltaTheta = (leftDeltaDistance - rightDeltaDistance) / wheelBase; 

			//compute the change in x and y and theta
			synchronized (lock) {
				theta += deltaTheta;
				double newAvgHeading = (theta + (theta + deltaTheta) ) / 2; //the overall theta of the robot since last measurement
				x +=     Math.sin(newAvgHeading) * centreDeltaDistance;
				y +=     Math.cos(newAvgHeading) * centreDeltaDistance;
				if(theta > Math.PI * 2)
					theta -= 2 * Math.PI;
				if(theta < 0)
					theta += 2 * Math.PI;
			}
			//keep this odometer values for next measurement
			oldLeftDistance = leftDistance;
			oldRightDistance = rightDistance;

			// this ensures that the odometer only runs once every period
			updateEnd = System.currentTimeMillis();
			if (updateEnd - updateStart < ODOMETER_PERIOD) {
				try {
					Thread.sleep(ODOMETER_PERIOD - (updateEnd - updateStart));
				} catch (InterruptedException e) {
					// there is nothing to be done here because it is not
					// expected that the odometer will be interrupted by
					// another thread
				}
			}
		}
	}

	/**
	 * Calculates circumference given a radius
	 * @param radius
	 * @return
	 */
	private double calculateCircumference(double radius) {
		return 2 * Math.PI * radius;
	}
	/**
	 * Converts tachoCount to distance traveled
	 * @param tachoCount
	 * @param radius
	 * @return
	 */
	private double tachoCountToDistance(int tachoCount, double radius) {
		return calculateCircumference(radius) * tachoCount / 360; //circumference multiplied by number of revolutions
	}
	
	// accessors
	public void getPosition(double[] position, boolean[] update) {
		// ensure that the values don't change while the odometer is running
		synchronized (lock) {
			if (update[0])
				position[0] = x;
			if (update[1])
				position[1] = y;
			if (update[2])
				position[2] = theta;
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

	// mutators
	public void setPosition(double[] position, boolean[] update) {
		// ensure that the values don't change while the odometer is running
		synchronized (lock) {
			if (update[0])
				x = position[0];
			if (update[1])
				y = position[1];
			if (update[2])
				theta = position[2];
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
}