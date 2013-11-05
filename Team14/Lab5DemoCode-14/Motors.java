/* Danielle Mustillo, Nathaniel Anthonisen
 * 260533476, 260470621
 * ECSE 211 - Design Principles and Methods
 * October 22, 2013
 */
/*
 * Motors.java
 */
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;

public class Motors {
	//object lock.
	public static Object lock;
	
	//properties of motors
	private static final int FORWARD_SPEED = 150;
	private static int ROTATE_SPEED = 100;
	private static double leftRadius = 2.08, rightRadius = 2.08, width = 15.23;
	
	// motors
	private static NXTRegulatedMotor leftMotor=Motor.A;
	private static NXTRegulatedMotor rightMotor=Motor.C;
	
	/**Sets the acceleration of both wheels
	 * @param n is the acceleration value.
	 */
	public static void setAcceleration(int n) {
		leftMotor.setAcceleration(n);
		rightMotor.setAcceleration(n);
	}
	
	/**Sets the rotation speed of the robot
	 * @param n is the speed
	 */
	public static void setRotationSpeed(int n) {
		ROTATE_SPEED = n;
	}
	
	/**Stops all motors. Used to reset the motor.
	 */
	public static void stop() {
		leftMotor.stop();
		rightMotor.stop();
	}
	
	/**Tell the robot to go straight indefinitely. 
	 */
	public static void straight() {
		leftMotor.setSpeed(FORWARD_SPEED);
		rightMotor.setSpeed(FORWARD_SPEED);
		leftMotor.forward();
		rightMotor.forward();
	}

	/**Tell the robot to go backwards indefinitely
	 */
	public static void backward() {
		leftMotor.setSpeed(FORWARD_SPEED);
		rightMotor.setSpeed(FORWARD_SPEED);
		leftMotor.backward();
		rightMotor.backward();
	}
	
	/**Tell the robot to rotate Clockwise in place.
	 */
	public static void rotateClockwise() {
		rightMotor.setSpeed(ROTATE_SPEED);
		leftMotor.setSpeed(ROTATE_SPEED);
		rightMotor.backward();
		leftMotor.forward();
	}
	
	/**Tell the robot to rotate CounterClockwise in place.
	 */
	public static void rotateCounterClockwise() {
		rightMotor.setSpeed(ROTATE_SPEED);
		leftMotor.setSpeed(ROTATE_SPEED);
		rightMotor.forward();
		leftMotor.backward();
	}
	
	/**Taken from code given in lab 4, was in odometer put in here since it makes more sense.
	 * @return double array with 2 elements, displacement in position 0 and heading in position 1.
	 */
	public static double[] getDisplacementAndHeading() {
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
