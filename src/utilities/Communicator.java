package utilities;

import java.io.IOException;

import lejos.nxt.LCD;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.comm.RConsole;
import lejos.nxt.comm.RS485;
import lejos.nxt.remote.RemoteNXT;

/**
 * This class will communicate with the slave NXT and initialize the ports on
 * it. Only uses the motor and the ultrasonicSensors on the other NXT as it is
 * impossible to initialize a ColorSensor on the remote NXT.
 * <p>
 * @author danielle
 * 
 */
public class Communicator {

	/**
	 * The Communicator object that will just connect to the slaveNXT. Uses exclusively RS485 for a reliable connection. It initializes the motors from the remote brick.
	 * @param slaveNXT	A String with the name of the remoteNXT connected via RS485. 
	 */
	public Communicator(String slaveNXT) {
		RemoteNXT nxt = null;
		try {
			nxt = new RemoteNXT(slaveNXT, RS485.getConnector());
		} catch (IOException ioe) {
			catchBug("RS485 Connection has Failed. See error: "
					+ ioe.getMessage());
		}

		LCD.clear();
		
		Settings.forkliftMotor = nxt.A;
		Settings.clawMotor = nxt.C;
		Settings.ultrasonicMotor = nxt.B;
		RConsole.println("Connected");
		Settings.leftUltrasonic = new UltrasonicSensor(nxt.S3);
		Settings.centerUltrasonic = new UltrasonicSensor(nxt.S1);
		Settings.rightUltrasonic = new UltrasonicSensor(nxt.S2);
	}

	/**
	 * The static helper method here just exits the system when commanded. It
	 * displays a message on the NXT for the user.
	 * <p>
	 * @param bug
	 *            A String object with the message on screen.
	 */
	public static void catchBug(String bug) {
		LCD.clear();
		LCD.drawString(bug, 0, 0);
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// No bug expected here ever.
		}
		LCD.clear();
		System.exit(1);
	}
}