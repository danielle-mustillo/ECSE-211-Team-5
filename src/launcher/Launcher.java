/**
 * 
 */
package launcher;

import hardwareAbstraction.Claw;
import hardwareAbstraction.Forklift;
import hardwareAbstraction.Forklift.ForkliftState;
import hardwareAbstraction.NXTRemoteCommand;
import hardwareAbstraction.NXTRemoteMotor;
import hardwareAbstraction.UltrasonicMotor;
import hardwareAbstraction.UltrasonicPoller;
import hardwareAbstraction.NXTRemoteUltrasonicPoller;
import utilities.BluetoothTransmission;
import utilities.Communicator;
import utilities.Point;
import controllers.State;
import lejos.nxt.Button;
import lejos.nxt.Sound;
import lejos.nxt.comm.RConsole;
import manager.Manager;

/**
 * 
 * Main Entry
 * 
 * @author Riley
 * @version 1.00
 *
 */
public class Launcher {

	/**
	 * 
	 * Robot ignition
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
		Manager manager = new Manager();
		//For speed to localizes in time
		BluetoothTransmission.getBluetoothData();

		//When the robot has finished getting data, setup the forklift and claw and localize. 
		Claw.releaseObject();
		try {
			Thread.sleep(Forklift.setHeight(ForkliftState.LIFT_HEIGHT));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		manager.sm.localization.start();
		
		//hold state to localization and do nothing else until done.
		while(manager.cm.getState() == State.LOCALIZING) {
			manager.um.nap(150);
		}
		
		//Once localization is done, reset the forklift and start using the controllers (in localization).
		Forklift.setHeight(ForkliftState.SCAN_HEIGHT_LOW);
		Claw.grabObject();

		Button.waitForPress();
	}
	
	public static void sleep(int ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
