/**
 * 
 */
package utilities;

import java.io.DataInputStream;
import java.io.IOException;

import lejos.nxt.LCD;
import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.NXTConnection;

/**
 * This class will open a new 
 * @author Riley
 *
 */
public class BluetoothTransmission {
	static DataInputStream stream; 
	static int role;
	static int startingCorner;
	static Point[] redZoneCoords;
	static Point[] greenZoneCoords;
	
	public static void getBluetoothData() {
		greenZoneCoords = new Point[4];
		redZoneCoords = new Point[4];
		stream = new DataInputStream(getConnection());
		getInformation();
	}
	
	private static DataInputStream getConnection() {
		BTConnection btc = Bluetooth.waitForConnection(0, NXTConnection.LCP);
		return btc.openDataInputStream();
	}
	
	private static void getInformation() {
		//TODO verify the input of the coordinates with the server. I am assuming the coordinates are sent as a series of 8 integers. The order is unknown. 
		try {
			role = stream.readInt();
			char useless = stream.readChar();
			startingCorner = stream.readInt();
			for(int i = 0; i < greenZoneCoords.length; i++) {
				useless = stream.readChar();
				int x = stream.readInt();
				useless = stream.readChar();
				int y = stream.readInt();
				greenZoneCoords[i] = new Point(x,y);
			}
			for(int i = 0; i < redZoneCoords.length; i++) {
				useless = stream.readChar();
				int x = stream.readInt();
				useless = stream.readChar();
				int y = stream.readInt();
				redZoneCoords[i] = new Point(x,y);
			}
		} catch (IOException e) {
			catchBug("Bluetooth recieve failed: " + e.getMessage());
		}

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
