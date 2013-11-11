/**
 * 
 */
package launcher;

import utilities.Point;
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
		
		manager.sm.localization.start();
		
		while(manager.cm.getState() == State.LOCALIZING) {
			manager.um.nap(150);
		}
		
		manager.sm.nav.addToRoute(new Point(0,0));
		manager.sm.nav.turnToComplete(0);
		
		Button.waitForPress();
	
	}

}
