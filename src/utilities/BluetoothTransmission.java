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
import lejos.nxt.comm.RConsole;

/**
 * This class will open a new 
 * @author Danielle
 *
 */
public class BluetoothTransmission {
	static DataInputStream stream; 
	
	public static void getBluetoothData() {
		Settings.greenZoneCoords = new Point[2];
		Settings.redZoneCoords = new Point[2];
		stream = getConnection();
		getInformation();
		close();
	}
	
	private static DataInputStream getConnection() {
		LCD.clear();
		LCD.drawString("Starting BT connection", 0, 0);
		NXTConnection conn = Bluetooth.waitForConnection();
		LCD.drawString("BT Connected", 0, 1);
		return conn.openDataInputStream();
	}
	
	private static void close() {
		try {
			stream.close();
		} catch (IOException e) {
		}
	}
	
	private static void getInformation() {
		//TODO verify the input of the coordinates with the server. I am assuming the coordinates are sent as a series of 8 integers. The order is unknown. 
		try {
			Settings.role = stream.readInt();
			
			char useless = stream.readChar();
			int startingCorner = stream.readInt();
			//TODO figure out which numbers correspond to which corners. And their values. 
			switch(startingCorner) {
			case 1 : Settings.startingCorner = StartingCorner.BOTTOM_LEFT; 
			break;
			case 2 : Settings.startingCorner = StartingCorner.BOTTOM_RIGHT; 
			break;
			case 3 : Settings.startingCorner = StartingCorner.TOP_LEFT; 
			break;
			case 4 : Settings.startingCorner = StartingCorner.TOP_RIGHT; 
			break;
			}
			
			for(int i = 0; i < Settings.greenZoneCoords.length; i++) {
				useless = stream.readChar();
				int x = stream.readInt();
				useless = stream.readChar();
				int y = stream.readInt();
				Settings.greenZoneCoords[i] = new Point(x,y);
			}
			
			for(int i = 0; i < Settings.redZoneCoords.length; i++) {
				useless = stream.readChar();
				int x = stream.readInt();
				useless = stream.readChar();
				int y = stream.readInt();
				Settings.redZoneCoords[i] = new Point(x,y);
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
