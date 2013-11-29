package slave;

import lejos.nxt.LCD;
import lejos.nxt.MotorPort;

/**
 * The responder class responds to the LCP requests sent by the
 * {@link Communicator} class. Uses RS-485.
 * <p>
 * The first version of this class (as seen in the git commit logs) show that
 * this class used the built in functions already provided in LEJOS. However,
 * the built in functionality turned out to be unreliable and we have rewrote
 * the functionality. This has greatly improved the reliability of the
 * communication between the two classes.
 */
public class Responder {

	/**
	 * This program has a main as it is to be loaded on the slaveNXT brick. This
	 * is the main program that occurs on the slave.
	 * <p>
	 * It will start the {@link RemoteUltrasonicPoller} and the
	 * {@link NXTRemoteControl}
	 * 
	 * @param args
	 *            This is the default constructor, not needed.
	 */

	public static void main(String[] args) throws Exception {
		LCD.drawString("Connecting", 1, 1);
		RemoteUltrasonicPoller remoteUSP = new RemoteUltrasonicPoller();
		NXTRemoteControl motorControl = new NXTRemoteControl(MotorPort.A,
				MotorPort.B, MotorPort.C, remoteUSP);

		motorControl.start();

	}

}