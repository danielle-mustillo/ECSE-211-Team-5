/* Danielle Mustillo, Nathaniel Anthonisen
 * 260533476, 260470621
 * ECSE 211 - Design Principles and Methods
 * October 1, 2013
 */
/*
 * WallFollowController
 */

import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.comm.RConsole;

/*
 * BangBang Controller was used because the results were significantly better than PController for keeping the odometer on track.
 */
public class WallFollowController extends Thread {
	// properties of the controller
	private final int bandCenter; // the desired distance from the wall to follow
	private final int bandwidth; // allowance on either side of desired distance
	
	// properties of controller. Function of bandCenter and bandwidth.
	private final int TOO_FAR; // Too close to wall, adjustment threshold
	private final int TOO_CLOSE; // too far from wall, adjustment threshold.
	
	//the ultrasonic sensor and obstacle driver are communicated with.
	private UltrasonicSensor us;
//	private ObstacleDriver obstacleDriver;

	// readings of the ultrasonic sensor
	private int previousDistance; // the previous measured value of the sensor
	private int currentDistance; // the current measured value of the sensor
	
	// primary control variables
	private int gapValue; // an advanced counter.
	// gapValue < 0 --> wall nearby. Avoid it!
	// gapValue = 0 --> straight away, go straight
	// 0 < gapValue < TOLERANCE --> continue straight, counts up the counter.
	// gapValue > TOLERANCE --> pass back control to obstacleDriver. Begin navigating.
	private final int TOLERANCE; // the count of gapValue before control is given back to obstacleDriver

	private SwitchBoard switches;
	private Odometer odo;
	
	//useful class variables for passing back to nav.
	private double thisDistance = 0;
	private double lastDistance;

	public WallFollowController( int bandwidth, int tolerance, Odometer odo, SwitchBoard switches) {
		
		this.bandCenter = switches.getDetectionThreshold() * 3;
		this.bandwidth = bandwidth;


		this.TOO_FAR = bandCenter + bandwidth;
		this.TOO_CLOSE = bandCenter - bandwidth;
		
		this.gapValue = 0;
		this.TOLERANCE = tolerance;

		this.switches = switches;
		this.odo = odo;
		
		//this.obstacleDriver = obstacleDriver;
	}

	public void run() {
		//thread will always run when started.
		while (true) {
			//if not navigating, start wallFollowing
			if (switches.getActivity() == CurrentActivity.AVOIDING_OBSTACLE) {
				RConsole.println("Doing the obstacle avoidance");
				processUSData(switches.getUltrasonicReadings());
			}
			// take a short nap
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Main actions performed by thread.
	 * Follows walls.
	 * @param distance
	 */
	public void processUSData(int[] distance) {
		//keep the user up to date
		LCD.drawString("WALL_FOLLOWING    ", 0, 5);
		
		//keeps track of the last two distances measured.
		previousDistance = ( distance[3] + distance[4] ) / 2;
		currentDistance = ( distance[1] + distance[0] ) / 2;
		
		//determines if there are misread
		boolean farMisread = this.currentDistance == 255
				&& this.previousDistance < 255 - this.bandCenter;
		if (farMisread) {
			// if a misread if found, do nothing.
		} else {
			//computes the distance measured
			int avgDistance = getAverageDistance(); 
			
			//updates the control variable gapValue and act on it.
//			updateGapValue(avgDistance); 
//			actOnGapValue();
			
			if(avgDistance < bandCenter - bandwidth) {}
//				Motors.turnLeft();
			else if (avgDistance > bandCenter + bandwidth) {}
//				Motors.turnRight();
			else
				Motors.straight();
			
			checkPassBackToNav();
		}
	}

	private void checkPassBackToNav() {
		double dX = odo.getX() - switches.getOdoPositions()[0];
		double dY = odo.getY() - switches.getOdoPositions()[1];
		
		this.lastDistance = this.thisDistance;
		this.thisDistance  = Math.sqrt(Math.pow(dX, 2) + Math.pow(dY, 2));
		
		if(computeDerivative(thisDistance, lastDistance) < 0 )
			switches.setActivity(CurrentActivity.NAVIGATING);
	}
	
	private double computeDerivative(double a, double b) {
		return a - b;
	}

//	/**
//	 * Gives the motors the command based on the gap value. Uses tolerance as the second control variable
//	 * gapValue < 0 --> wall nearby. Avoid it!
//	 * gapValue = 0 --> straight away, go straight
//	 * 0 < gapValue < TOLERANCE --> continue straight, counts up the counter.
//	 * gapValue > TOLERANCE --> pass back control to obstacleDriver. Begin navigating.
//	 */
//	private void actOnGapValue() {
//		// if too close to wall, navigate around it
//		if (this.gapValue < 0) 
//			Motors.turnLeft();
//		// otherwise go straight
//		else if (this.gapValue < TOLERANCE)
//			Motors.straight();
//		// unless it has cleared the obstacle, then give back controller to obstacle driver
//		else if (this.gapValue >= TOLERANCE) 
//			Motors.turnRight();
//	}
//
//	/**
//	 * Checks if there is gap. If the robot is TOO_CLOSE
//	 * to wall, send a correct command (gapValue = -1). If the robot is within
//	 * desired range (bandwidth), send straight command (gapValue = 0) If the robot
//	 * is TOO_FAR from the wall, increment gapValue.
//	 * 
//	 * @param distance
//	 * 
//	 */
//	private void updateGapValue(int distance) {
//		// if too far from wall, start incrementing gapValue. 
//		// if this incrememnts too much, the robot will go back to navigating
//		if (distance > this.TOO_FAR)
//			++gapValue;
//		else {
//			// if robot is too close to a wall, it certainly is not a gap!
//			// Correct immediately.
//			if (distance < this.TOO_CLOSE) {
//				gapValue = -1;
//			}
//			// if not too close or too far, send straight command.
//			else {
//				gapValue = 0;
//			}
//		}
//	}

	/**
	 * Gets the average of the two distances
	 * 
	 * @return
	 */
	public int getAverageDistance() {
		return (currentDistance + previousDistance) / 2;
	}

}
