package utilities;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lejos.nxt.LCD;
import lejos.nxt.comm.RS485;
import lejos.nxt.comm.NXTCommConnector;
import lejos.nxt.comm.NXTConnection;

/**
 * This class will communicate with the slave NXT and initialize the ports on
 * it. Only uses the motor and the ultrasonicSensors on the other NXT as it is
 * impossible to initialize a ColorSensor on the remote NXT.
 * <p>
 * @author Danielle Mustillo
 * @author Riley
 * 
 */
public class Communicator {
	
	private NXTConnection con;
	private NXTCommConnector connector;
	public DataInputStream dis;
	public DataOutputStream dos;
	
	public Communicator(String extendedNXT) {
		
	       connector = RS485.getConnector();
	       con = connector.connect(extendedNXT, NXTConnection.RAW);
	       if (con == null) 
	       catchBug("RS485 Failed");
	      
	       dis = con.openDataInputStream();
	       dos = con.openDataOutputStream();
	}
	
	
	
	
	/**
	 * The Communicator object that will just connect to the slaveNXT. Uses exclusively RS485 for a reliable connection. It initializes the motors from the remote brick.
	 * @param slaveNXT	A String with the name of the remoteNXT connected via RS485. 
	 */
	/*public Communicator(String slaveNXT) {
		RemoteNXT nxt = null;
		try {
			nxt = new RemoteNXT(slaveNXT, RS485.getConnector());
			LCD.clear();
            LCD.drawString("Connected",0,1);
            Thread.sleep(2000);
		} catch (IOException ioe) {
			catchBug("RS485 Connection has Failed. See error: "
					+ ioe.getMessage());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		LCD.clear();
		
		Settings.forkliftMotor = nxt.C;
		Settings.clawMotor = nxt.A;
		Settings.ultrasonicMotor = nxt.B;
		Settings.leftUltrasonic = new UltrasonicSensor(nxt.S3);
		Settings.centerUltrasonic = new UltrasonicSensor(nxt.S1);
		Settings.rightUltrasonic = new UltrasonicSensor(nxt.S2);
		
	}*/

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