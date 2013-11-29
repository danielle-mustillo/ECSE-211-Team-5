package utilities;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import lejos.nxt.LCD;
import lejos.nxt.comm.RS485;
import lejos.nxt.comm.NXTCommConnector;
import lejos.nxt.comm.NXTConnection;

/**
 * This class will communicate with the slave NXT and initialize the ports on
 * it. Only uses the motor and the ultrasonicSensors on the other NXT as it is
 * impossible to initialize a ColorSensor on the remote NXT.
 * <p>
 * The first version of this class (as seen in the git commit logs) show that
 * this class used the built in functions already provided in LEJOS. However,
 * the built in functionality turned out to be unreliable and we have rewrote
 * the functionality. This has greatly improved the reliability of the
 * communication between the two classes.
 */
public class Communicator {
	/**
	 * Connection object for the connection between the two NXT bricks
	 */
	private NXTConnection con;
	/**
	 * RS485 Connector object
	 */
	private NXTCommConnector connector;
	public DataInputStream dis;
	public DataOutputStream dos;

	/**
	 * Master communicator. Establishes and stores the RS485 connection to the
	 * slave NXT. It will also open the input & output data streams
	 * 
	 * @param extendedNXT
	 */
	public Communicator(String extendedNXT) {

		connector = RS485.getConnector();
		con = connector.connect(extendedNXT, NXTConnection.RAW);
		if (con == null)
			catchBug("RS485 Failed");

		dis = con.openDataInputStream();
		dos = con.openDataOutputStream();
	}

	/**
	 * The static helper method here just exits the system when commanded. It
	 * displays a message on the NXT for the user.
	 * <p>
	 * 
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