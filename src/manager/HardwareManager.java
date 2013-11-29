package manager;

import controllers.State;
import hardwareAbstraction.*;

/**
 * Initializes all sensor classes and appropriate motor classes.  Provides access to sensor pollers and motor classes.
 * @author Riley
 *
 */
public class HardwareManager {

	/**
	 * Access to the rest of the robots functions
	 */
	public Manager manager;
	/**
	 * Handles driving motors, used by {@link Navigation} and {@link odometer}
	 */
	public Drive drive;
	/**
	 * Handles the forklift motor, that raises and lowers the claw mechanism.  
	 */
	public Forklift forklift;
	/**
	 * Handles the claw motor. Opens and closes the claw
	 */
	public Claw claw;
	/**
	 * Handles the ultrasonic motor.  Rotates the Ultrasonic sensors to the forward or side facing positions
	 */
	public UltrasonicMotor ultrasonicMotor;
	/**
	 * Handles the front colour sensor.  Used for differentiating between objects
	 */
	public ColorPoller colorPoller;
	/**
	 * Detects grid lines on the floor with the two rear facing colour sensors
	 */
	public LinePoller linePoller;
	/**
	 * Handles interaction with the remote ultrasonic poller. Provides methods to set the mode of the poller and retrieve filtered data from each sensor
	 */
	public NXTRemoteUltrasonicPoller ultrasonicPoller;
	
	/**
	 * Initializes all class objects.
	 * @param manager
	 */
	public HardwareManager(Manager manager) {
		this.manager = manager;
		
		this.drive = new Drive();
	
		this.forklift = new Forklift();
	
		this.claw = new Claw();
	
		this.ultrasonicMotor = new UltrasonicMotor();

		this.colorPoller = new ColorPoller();

		this.linePoller = new LinePoller();

		this.ultrasonicPoller = new NXTRemoteUltrasonicPoller(manager.um.command, 4);

	}
	
	/**
	 * Resets the motors to their default starting position.  Also stops the stop before doing so.
	 */
	public void reset() {
		manager.cm.setState(State.PAUSE);
		drive.stop();		
		Claw.grabObject();
		Forklift.setHeight(Forklift.ForkliftState.SCAN_HEIGHT);
		UltrasonicMotor.setForwardPosition();
	}
}
