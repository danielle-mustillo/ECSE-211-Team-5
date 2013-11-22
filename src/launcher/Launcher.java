/**
 * 
 */
package launcher;

import hardwareAbstraction.Claw;
import hardwareAbstraction.Forklift;
import hardwareAbstraction.Forklift.ForkliftState;
import hardwareAbstraction.UltrasonicMotor;
import hardwareAbstraction.UltrasonicPoller;
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
//		RConsole.openBluetooth(20000);
		
		
		Manager manager = new Manager();
		Button.waitForPress();
		/*manager.sm.localization.start();
		
		while(manager.cm.getState() == State.LOCALIZING) {
			manager.um.nap(150);
		}*/
		
		//sleep(Forklift.setHeight(ForkliftState.SCAN_HEIGHT_LOW));
		
//		manager.sm.odoCorrection.start();
		manager.hm.ultrasonicPoller.start();
		manager.sm.nav.start();
//		manager.sm.nav.turnToComplete(Math.PI);
		
		
		manager.cm.setState(State.SEARCH);
		
//		manager.sm.nav.addToRoute(new Point(15,15));
//		manager.sm.nav.addToRoute(new Point(45,15));
//		manager.sm.nav.addToRoute(new Point(45,165));
//		manager.sm.nav.addToRoute(new Point(15,165));
//		manager.sm.nav.addToRoute(new Point(15,15));
		
		
		//manager.sm.nav.addToRoute(new Point(60,0));
		
		//manager.cm.setState(State.SEARCH);
		
		
//		manager.cm.setState(State.RECOGNIZE);
//		manager.sm.nav.addToRoute(new Point(60,0));
//		manager.sm.nav.turnToComplete(0);
		
//		manager.cm.setState(State.COLLECT);
		
		Button.waitForPress();
		manager.hm.reset();
	}
	
	private static void sleep(int time){
		try {
		Thread.sleep(time);
		} catch (InterruptedException e) {
		}
	}

}
