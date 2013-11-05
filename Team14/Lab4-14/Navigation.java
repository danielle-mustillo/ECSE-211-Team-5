/* Danielle Mustillo, Nathaniel Anthonisen
 * 260533476, 260470621
 * ECSE 211 - Design Principles and Methods
 * October 08, 2013
 */
/*
 *Navigation.java
 */
import lejos.nxt.*;

public class Navigation extends Thread {
	// speed of the controller
	private final int FORWARD_SPEED;
	private final int ROTATE_SPEED;

	// motors
	private NXTRegulatedMotor leftMotor;
	private NXTRegulatedMotor rightMotor;

	// properties of the robot
	private final double leftRadius;
	private final double rightRadius;
	private final double width;

	// the odometer
	private Odometer odometer;

	// the boolean which passes control to and from the wallFollower and the
	// driver
	private boolean navigationStatus;

	public Navigation( Odometer odometer, double leftRadius,
			double rightRadius, double width, int forwardSpeed, int rotateSpeed) {
		this.leftMotor = Motor.A;
		this.rightMotor = Motor.C;
		this.leftRadius = leftRadius;
		this.rightRadius = rightRadius;
		this.width = width;
		this.odometer = odometer;
		this.FORWARD_SPEED = forwardSpeed;
		this.ROTATE_SPEED = rotateSpeed;
		this.navigationStatus = true;
	}

	public void run() {
		// reset the motors
		for (NXTRegulatedMotor motor : new NXTRegulatedMotor[] { leftMotor,
				rightMotor }) {
			motor.stop();
		}
		// take a short nap
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// travel to coordinates

		// let the user know were done.
		LCD.drawString("PATH COMPLETE  ", 0, 5);
	}

	public void travelTo(double x, double y) {

		// default speed
		leftMotor.setSpeed(FORWARD_SPEED);
		rightMotor.setSpeed(FORWARD_SPEED);

		// navigate until the robot is quite close to its destination
		// necessary because the engines are notoriously imprecise
		while (Math.abs(x - odometer.getX()) >= 0.3
				|| Math.abs(y - odometer.getY()) >= 0.3) {
			// compare robot's angle (theta) to the angle it needs to be
			// (angle).
			double theta = odometer.getTheta() * 180 / Math.PI; // convert to
																// degrees
			double angle = determineAngle(x, y); // the angle the robot should
													// be to get to its
													// destination
			double offset = Math.abs(theta) - Math.abs(angle);

			// if the difference is more than three degrees, correction is
			// needed
			boolean correctionNeeded = Math.abs(offset) > 3;

			// turn if needed, otherwise go straight
			if (correctionNeeded)
				turnTo(angle);
			else {
				goStraight();
			}
			// take a short nap
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// stop both motors at destination and take a short nap
		leftMotor.stop();
		rightMotor.stop();
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}


	/**
	 * Robot tells the wheels to go straight. Informs the user of this decision.
	 */
	private void goStraight() {
		leftMotor.setSpeed(FORWARD_SPEED);
		rightMotor.setSpeed(FORWARD_SPEED);
		LCD.drawString("STRAIGHT        ", 0, 5);
		leftMotor.forward();
		rightMotor.forward();
	}

	/**
	 * Determines what the heading (angle) of the robot should be in degrees.
	 * This is in reference to the internal odometer.
	 * The basis of this is the arctan function.
	 * This function is "adjusted" to 
	 * @param x
	 * @param y
	 * @return the angle of the robot between (-180,180) degrees.
	 */
	private double determineAngle(double x, double y) {
		
		//how far the robot is from its destination in (x,y)
		double xOffset = x - odometer.getX();
		double yOffset = y - odometer.getY();

		//if the x or y component is very close to its destination, it is effectively at the destination
		if (Math.abs(yOffset) < 0.05)
			yOffset = 0;
		if (Math.abs(xOffset) < 0.05)
			xOffset = 0;

		/*
		 * Gives the angle of the robot from its central position.
		 * We desire the angle from the robot's default "forward" position vector.
		 * We are using x/y because tangent is defined as opposite over adjacent
		 * X is perpendicular to this "forward" position vector (opposite).
		 * Y is parallel to the above mentioned vector (adjacent).
		 */
		double angle;
		//default is arctan of opp/adj
		if (yOffset >= 0)
			angle = Math.atan(xOffset / yOffset);
		//if angles above +- 90 degrees, add or subtract PI accordingly.
		else if (xOffset > 0)
			angle = Math.atan(xOffset / yOffset) + Math.PI; 
		else
			angle = Math.atan(xOffset / yOffset) - Math.PI;

		angle *= (180 / Math.PI); // convert to degrees
		return angle;
	}

	/**
	 * Turns the wheels to the input angle theta.
	 * @param angle
	 */
	public void turnTo(double angle) {
		// default speeds during turn
		leftMotor.setSpeed(ROTATE_SPEED);
		rightMotor.setSpeed(ROTATE_SPEED);
		
		//determines how much the bot should turn based on its current heading
		double theta = odometer.getTheta();
		double correctionTheta = angle - theta * 180/Math.PI;
		
		/* Corrects to choose the angles between (-180,180)
		 * correction Theta can not be higher than +- 360 degrees since both angle and theta are standarized between (-180,180). so correction only needs to be applied once */ 
		if(correctionTheta > 180) correctionTheta -= 360;
		if(correctionTheta < -180) correctionTheta += 360;
		
		// We do not want the program to be interrupted while turning
		// therefore we use rotate.
		// The next commands convert the angle into proper format
		// and turns
	
		if(correctionTheta - odometer.getTheta() < 0) {
			while(Math.abs(correctionTheta - odometer.getTheta()) < 0.5) {
				leftMotor.setSpeed(ROTATE_SPEED);
				rightMotor.setSpeed(ROTATE_SPEED);
				leftMotor.forward();
				rightMotor.backward();
			}
		}
		else {
			while(Math.abs(correctionTheta - odometer.getTheta()) < 0.5) {
				leftMotor.setSpeed(ROTATE_SPEED);
				rightMotor.setSpeed(ROTATE_SPEED);
				rightMotor.forward();
				leftMotor.backward();
			}
		}
		LCD.drawString("TURNING        ", 0, 5);
		LCD.drawString("" + correctionTheta, 0, 6);
		
		
	}

	/**Allows the wall follower object to see if it should run or not
	 * @return boolean value true (navigate) or false (run wallFollower)
	 */
	public boolean isNavigating() {
		return navigationStatus;
	}

	// the following are converters for the turn to method.
	// they simply convert angles into useful values for turnTo
	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}

	private static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}
}