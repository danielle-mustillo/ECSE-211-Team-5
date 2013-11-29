/**
 * 
 */
package launcher;

import hardwareAbstraction.Claw;
import hardwareAbstraction.Forklift;
import hardwareAbstraction.Forklift.ForkliftState;
import utilities.BluetoothTransmission;
import controllers.State;
import lejos.nxt.Button;
import manager.Manager;

/**
 * 
 * Main Entry
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
		// For speed to localizes in time
		BluetoothTransmission.getBluetoothData();

		// When the robot has finished getting data, setup the forklift and claw
		// and localize.
		Claw.releaseObject();
		try {
			Thread.sleep(Forklift.setHeight(ForkliftState.LIFT_HEIGHT));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		manager.sm.localization.start();

		// hold state to localization and do nothing else until done.
		while (manager.cm.getState() == State.LOCALIZING) {
			manager.um.nap(150);
		}

		// Once localization is done, reset the forklift and start using the
		// controllers (in localization).
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
