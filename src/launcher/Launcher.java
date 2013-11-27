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
//		RConsole.openUSB(20000);
		
		Manager manager = new Manager();
		//For speed to localizes in time
		BluetoothTransmission.getBluetoothData();
		//Button.waitForPress();
		Claw.releaseObject();
		try {
			Thread.sleep(Forklift.setHeight(ForkliftState.LIFT_HEIGHT));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		
//		manager.sm.nav.start();
		
		
		
		manager.sm.localization.start();
		
		while(manager.cm.getState() == State.LOCALIZING) {
			manager.um.nap(150);
		}
		Forklift.setHeight(ForkliftState.SCAN_HEIGHT_LOW);
		manager.sm.odoCorrection.start();
		Claw.grabObject();
//		manager.cm.setState(State.SEARCH);
		
		
//		manager.sm.odo.adjustPosition(180, 0, Math.PI);
		
//		
//		manager.cm.setState(State.SEARCH);
		
		//Communicator com = new Communicator("NXT");
		
		//NXTRemoteCommand command = new NXTRemoteCommand(com);
		
//		NXTRemoteMotor clawMotor = new NXTRemoteMotor(command, 1);
//		NXTRemoteMotor ultrasonicMotor = new NXTRemoteMotor(command, 2);
//		NXTRemoteMotor liftMotor = new NXTRemoteMotor(command, 3);
		
		//NXTRemoteUltrasonicPoller usp = new NXTRemoteUltrasonicPoller(command, 4);
		/*NXTRemoteUltrasonicPoller usp = manager.hm.ultrasonicPoller;
		usp.start();
		
		int l;
		int r;
		int c;
		
		sleep(1000);
		c = usp.getUSReading(1);
		r = usp.getUSReading(2);
		l = usp.getUSReading(0);
		
		RConsole.print("C:" + c);
		RConsole.print("L:" + l);
		RConsole.print("R:" + r);
		
		usp.pingSequential();
		sleep(1000);
		c = usp.getUSReading(1);
		r = usp.getUSReading(2);
		l = usp.getUSReading(0);
		
		RConsole.print("C:" + c);
		RConsole.print("L:" + l);
		RConsole.print("R:" + r);
		
		usp.pingAll();
		sleep(1000);
		c = usp.getUSReading(1);
		r = usp.getUSReading(2);
		l = usp.getUSReading(0);
		
		RConsole.print("C:" + c);
		RConsole.print("L:" + l);
		RConsole.print("R:" + r);
		
		usp.stop();*/
		
//		ultrasonicMotor.setAcceleration(100);
//		ultrasonicMotor.setSpeed(100);
//		clawMotor.setAcceleration(100);
//		liftMotor.setAcceleration(100);
//		clawMotor.setSpeed(100);
//		liftMotor.setSpeed(100);
//		
//		
//		for(int i=0; i<10; i++) {
//			
//			if(i % 2 == 0) {
//				ultrasonicMotor.rotateTo(0);
//			} else {
//				ultrasonicMotor.rotateTo(-40);
//			}
//			
//			while(ultrasonicMotor.isMoving()) {
//				sleep(40);
//			}
//		}
		
		
		
		
		//sleep(Forklift.setHeight(ForkliftState.SCAN_HEIGHT_LOW));
		
//		manager.sm.odoCorrection.start();
		//manager.hm.ultrasonicPoller.start();
		//manager.sm.nav.start();
		//manager.sm.nav.turnToComplete(0);
		
		
		//manager.cm.setState(State.SEARCH);
		
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
		//manager.hm.reset();
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
