import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;

public class Motors {
	//properties of driver
	private static final int FORWARD_SPEED = 200;
	private static final int ROTATE_SPEED = 150;
	
	//properties of object avoidance
	private static final int MOTOR_HIGH = 300;
	private static final int MOTOR_LOW = 150;

	private static double leftRadius = 2.08, rightRadius = 2.08, width = 15.24;
	// motors
	private static NXTRegulatedMotor leftMotor=Motor.A;
	private static NXTRegulatedMotor rightMotor=Motor.C;
	
	private static Object lock = new Object();
	
	/**Stops all motors. Used to reset the motor.
	 */
	public static void stop() {
		leftMotor.stop();
		rightMotor.stop();
	}
	
	/**Tell the robot to go straight indefinitely
	 */
	public static void straight() {
		leftMotor.setSpeed(FORWARD_SPEED);
		rightMotor.setSpeed(FORWARD_SPEED);
		leftMotor.forward();
		rightMotor.forward();
	}

	/**Tell the robot to go straight indefinitely
	 */
	public static void backward() {
		leftMotor.setSpeed(FORWARD_SPEED);
		rightMotor.setSpeed(FORWARD_SPEED);
		leftMotor.backward();
		rightMotor.backward();
	}
	
	/**Tell the robot to turn right sharp
	 */
	public static void turnRight() {
		leftMotor.setSpeed(FORWARD_SPEED);
		rightMotor.setSpeed(0); // need a very sharp corner
		leftMotor.forward();
		rightMotor.forward();
	}

	/**Tell the robot to turn left not sharply
	 */
	public static void turnLeft() {
		rightMotor.setSpeed(MOTOR_HIGH);
		leftMotor.setSpeed(MOTOR_LOW);
		rightMotor.forward();
		leftMotor.forward();
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
	
	/**Taken from code given in lab 4
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
