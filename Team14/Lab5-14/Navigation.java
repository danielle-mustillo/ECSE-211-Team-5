/* Danielle Mustillo, Nathaniel Anthonisen
 * 260533476, 260470621
 * ECSE 211 - Design Principles and Methods
 * October 1, 2013
 */
/*
 * ObstacleDriver.java
 */
import lejos.nxt.*;

public class Navigation extends Thread {

	// the odometer, ultrasonic sensor and switchboard
	private Odometer odometer;
	private SwitchBoard switches;

	public Navigation(Odometer odometer, SwitchBoard switches) {
		// instantiate the class variables.
		this.odometer = odometer;
		this.switches = switches;
	}

	public void run() {
		// reset the motors
		Motors.stop();
		// take a short nap before navigating.
		try {
			Thread.sleep(250);
		} catch (InterruptedException e) {
			// not expected for this to occur
		}
		// navigate to points sequentially
		// TODO this will have to be changed so that it reads values all the
		// time on the first part of the lab.
		travelTo(00, 60);
		travelTo(60, 00);

		// let the user know were done
		LCD.drawString("PATH COMPLETE  ", 0, 5);
	}

	public void travelTo(double x, double y) {

		// navigate until the robot is quite close to its destination
		// necessary because the engines are notoriously imprecise
		while (Math.abs(x - odometer.getX()) >= 0.3 || Math.abs(y - odometer.getY()) >= 0.3) {
			//TODO force the sleep of this thread.
			if (switches.getActivity() == CurrentActivity.NAVIGATING) {
				// if the robot is too close to wall, stop this thread from
				// doing anything.
				// allows the WallFollower to engage.
				// this is the only place that the wallFollower is allowed to
				// engage.
				if (switches.getUSReading() < switches.getDetectionThreshold())
					switches.setActivity(CurrentActivity.SAMPLING); //pass control to sampling thread.
				else {
					// compare robot's angle (theta) to the angle it needs to be
					// (angle).
					// TODO you need to correct this so the angles are robust to
					// the point of perfection. This may require editing the
					// odometer to not correct angles, or otherwise take the mod
					// 360 of them.
					double theta = odometer.getTheta(); // degrees
					double angle = determineAngle(x, y); // the angle the robot
															// should be to get
															// to its
															// destination
					double offset = Math.abs(theta) - Math.abs(angle);

					// if the difference is more than three degrees, correction
					// is needed
					boolean correctionNeeded = Math.abs(offset) > 3;

					//TODO SHOVE THIS INSIDE TURN TO!!!
					// turn if needed, otherwise go straight
					if (correctionNeeded)
						turnTo(angle);
					else {
						Motors.straight();
					}
				}
			}
			// take a short nap
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// stop both motors at destination and take a short nap
		Motors.stop();
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Determines what the heading (angle) of the robot should be in degrees.
	 * This is in reference to the internal odometer. The basis of this is the
	 * arctan function. This function is "adjusted" to
	 * 
	 * @param x
	 * @param y
	 * @return the angle of the robot between (-180,180) degrees.
	 */
	private double determineAngle(double x, double y) {

		// how far the robot is from its destination in (x,y)
		double xOffset = x - odometer.getX();
		double yOffset = y - odometer.getY();

		// if the x or y component is very close to its destination, it is
		// effectively at the destination
		if (Math.abs(yOffset) < 0.05)
			yOffset = 0;
		if (Math.abs(xOffset) < 0.05)
			xOffset = 0;

		/*
		 * Gives the angle of the robot from its central position. We desire the
		 * angle from the robot's default "forward" position vector. We are
		 * using x/y because tangent is defined as opposite over adjacent X is
		 * perpendicular to this "forward" position vector (opposite). Y is
		 * parallel to the above mentioned vector (adjacent).
		 */
		double angle;
		// default is arctan of opp/adj
		if (yOffset >= 0)
			angle = Math.atan(xOffset / yOffset);
		// if angles above +- 90 degrees, add or subtract PI accordingly.
		else if (xOffset > 0)
			angle = Math.atan(xOffset / yOffset) + Math.PI;
		else
			angle = Math.atan(xOffset / yOffset) - Math.PI;

		angle *= (180 / Math.PI); // convert to degrees
		return angle;
	}

	/**
	 * Turns the wheels to the input angle theta.
	 * 
	 * @param angle
	 */
	public void turnTo(double angle) {
		//determines how much the bot should turn based on its current heading
		double correctionTheta = angle-odometer.getTheta();
		
		/* Corrects to choose the angles between (-180,180)
		 * correction Theta can not be higher than +- 360 degrees since both angle and theta are standarized between (-180,180). so correction only needs to be applied once */ 
		if(correctionTheta > 180) correctionTheta -= 360;
		if(correctionTheta < -180) correctionTheta += 360;
		
		// We do not want the program to be interrupted while turning
		// therefore we use rotate.
		// The next commands convert the angle into proper format
		// and turns
		
		LCD.drawString("TURNING        ", 0, 5);
		if(correctionTheta < 0)
			Motors.rotateCounterClockwise();
		else
			Motors.rotateClockwise();
		
	}
}