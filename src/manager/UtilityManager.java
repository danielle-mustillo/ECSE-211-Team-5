package manager;

import lejos.nxt.comm.RConsole;
import hardwareAbstraction.NXTRemoteCommand;
import hardwareAbstraction.NXTRemoteMotor;
import utilities.*;

/**
 * Initiates the RS485 Connection and the Bluetooth Connection to the server.
 * It will set the appropriate match parameters in {@link Settings}
 * <p>
 * It will also initialize the appropriate {@link NXTRemoteMotor} motors and sets them in {@link Settings}
 * @author Riley
 *
 */
public class UtilityManager {
	
	/**
	 * Link to the other functions of the robot
	 */
	public Manager manager;
	/**
	 * RS 485 Connection
	 */
	public Communicator comLink;
	/**
	 * Not implemented
	 */
	public Map map;
	/**
	 * Object that handles the RS485 messaging on the master brick
	 */
	public NXTRemoteCommand command;
	
	
	public UtilityManager(Manager manager) {
		this.manager = manager;
		
		//BluetoothTransmission.getBluetoothData();
		this.comLink = new Communicator(Settings.NXTSlaveName);
		this.command = new NXTRemoteCommand(comLink);
		
		Settings.clawMotor = new NXTRemoteMotor(command, Settings.CLAW_MOTOR_ID);
		Settings.clawMotor.setAcceleration(200);
		Settings.clawMotor.setSpeed(200);
		Settings.liftMotor = new NXTRemoteMotor(command, Settings.LIFT_MOTOR_ID);
		Settings.liftMotor.setAcceleration(200);
		Settings.liftMotor.setSpeed(200);
		Settings.ultrasonicMotor = new NXTRemoteMotor(command, Settings.ULTRASONIC_MOTOR_ID);
		Settings.ultrasonicMotor.setAcceleration(200);
		Settings.ultrasonicMotor.setSpeed(200);
		
		this.map = new Map();
	}
	
	/** Helper method to avoid large try/catch blocks. Sleeps the current thread. 
	 * @param time int value which represents the sleep time
	 */
	public void nap(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Converts an array to string
	 * @param arr
	 * @return String of [arr[0], arr[1],...]
	 */
	public String arrayToString(double[] arr) {
		String output = "[";
		for (int i=0; i< arr.length; i++) {
			output += String.valueOf(arr[i]) + ", ";
		}
		
		return output + "]";
	}
}
