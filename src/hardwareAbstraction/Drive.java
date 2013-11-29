package hardwareAbstraction;

import lejos.nxt.NXTRegulatedMotor;
import utilities.Settings;

/**
 * This class handles the driving motors, it is a modified version of the TwoWheeledRobot class from Lab4. 
 *
 */
public class Drive {

	private NXTRegulatedMotor leftMotor;
	private NXTRegulatedMotor rightMotor;
	
	/**
	 * Left wheel radius
	 */
	public static final double LEFT_RADIUS = 2.03;
	/**
	 * Right wheel radius
	 */
	public static final double RIGHT_RADIUS = 2.03;
	/**
	 * Wheelbase of the robot
	 */
	public static final double WIDTH = 15.90;
	
	/**
	 * Initializes the {@link leftMotor} and {@link rightMotor} from the values in {@link Settings}
	 * <p>
	 * Sets motor acceleration to 500, allowing for relatively smooth acceleration 
	 */
	public Drive() {
		leftMotor = Settings.leftDriveMotor;
		rightMotor = Settings.rightDriveMotor;
		leftMotor.setAcceleration(500);
		rightMotor.setAcceleration(500);
	}
	
	/**
	 * returns displacement and heading based on tacho counts -> passed through the data array pointer
	 * <p>
	 * @param data array of length 2.  data[0] = displacement, data[1] = heading
	 */
	public void getDisplacementAndHeading(double [] data) {
		int leftTacho, rightTacho;
		leftTacho = leftMotor.getTachoCount();
		rightTacho = rightMotor.getTachoCount();
		
		data[0] = (leftTacho * LEFT_RADIUS + rightTacho * RIGHT_RADIUS) * Math.PI / 360.0;
		data[1] = (leftTacho * LEFT_RADIUS - rightTacho * RIGHT_RADIUS) / WIDTH * Math.PI / 180.0;
	}
	
	/**
	 * Sets both forward and rotational speed of the robot
	 * @param forwardSpeed speed to travel forward at  [cm/s]
	 * @param rotationalSpeed speed to rotate at [deg/s], positive is CW
	 */
	public void setSpeeds(double forwardSpeed, double rotationalSpeed) {
		double leftSpeed, rightSpeed;

		leftSpeed = (forwardSpeed + rotationalSpeed * WIDTH * Math.PI / 360.0) *
				180.0 / (LEFT_RADIUS * Math.PI);
		rightSpeed = (forwardSpeed - rotationalSpeed * WIDTH * Math.PI / 360.0) *
				180.0 / (RIGHT_RADIUS * Math.PI);

		
		// set motor directions
		if (leftSpeed > 0.0)
			leftMotor.forward();
		else {
			leftMotor.backward();
			leftSpeed = -leftSpeed;
		}
		
		if (rightSpeed > 0.0)
			rightMotor.forward();
		else {
			rightMotor.backward();
			rightSpeed = -rightSpeed;
		}
		
		// set motor speeds
		if (leftSpeed > 900.0)
			leftMotor.setSpeed(900);
		else
			leftMotor.setSpeed((int)leftSpeed);
		
		if (rightSpeed > 900.0)
			rightMotor.setSpeed(900);
		else
			rightMotor.setSpeed((int)rightSpeed);
	}
	
	/**
	 * Stops robot from moving
	 */
	public void stop() {
		rightMotor.stop(true);
		leftMotor.stop();
	}
	
}
