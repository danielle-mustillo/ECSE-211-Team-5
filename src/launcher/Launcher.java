/**
 * 
 */
package launcher;

import controllers.State;
import lejos.nxt.Button;
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
		RConsole.openUSB(20000);
		Button.waitForPress();
		
		Manager manager = new Manager();
		
		manager.hm.drive.setSpeeds(300, 0);
		int count = 0;
		while(count < 80) {
			RConsole.println(manager.sm.odo.getPosition().toString());
			manager.um.nap(100);
			count++;
		}
		manager.hm.drive.stop();
		RConsole.println(manager.sm.odo.getPosition().toString());
		
		/*manager.sm.localization.start();
		
		while(manager.cm.getState() == State.LOCALIZING) {
			manager.um.nap(150);
		}*/
		
		Button.waitForPress();
	
	}

}
