package manager;

import lejos.nxt.comm.RConsole;
import hardwareAbstraction.NXTRemoteCommand;
import hardwareAbstraction.NXTRemoteMotor;
import utilities.*;

public class UtilityManager {
	
	public Manager manager;
	public Communicator comLink; 
	public Map map;
	public NXTRemoteCommand command;
	
	
	public UtilityManager(Manager manager) {
		this.manager = manager;
		
		//BluetoothTransmission.getBluetoothData();
		this.comLink = new Communicator(Settings.NXTSlaveName);
		this.command = new NXTRemoteCommand(comLink);
		
		Settings.clawMotor = new NXTRemoteMotor(command, Settings.CLAW_MOTOR_ID);
		Settings.liftMotor = new NXTRemoteMotor(command, Settings.LIFT_MOTOR_ID);
		Settings.ultrasonicMotor = new NXTRemoteMotor(command, Settings.ULTRASONIC_MOTOR_ID);
		
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
	 * @return
	 */
	public String arrayToString(double[] arr) {
		String output = "[";
		for (int i=0; i< arr.length; i++) {
			output += String.valueOf(arr[i]) + ", ";
		}
		
		return output + "]";
	}
}
