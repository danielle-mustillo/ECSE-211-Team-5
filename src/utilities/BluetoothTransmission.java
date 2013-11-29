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
 * This class will open a bluetooth connection to the PC server. It will also
 * read the match variables and set the appropriate variables in
 * {@link Settings}.
 * 
 * @author Danielle
 * 
 */
public class BluetoothTransmission {
	/**
	 * Input Data Stream
	 */
	static DataInputStream stream;

	/**
	 * Calls {@link getConnection() } and then {@link getInformation() }. It
	 * will then call {@link close}
	 */
	public static void getBluetoothData() {
		Settings.greenZoneCoords = new Point[2];
		Settings.redZoneCoords = new Point[2];
		stream = getConnection();
		getInformation();
		close();
	}

	/**
	 * Waits for a bluetooth connection to the server, and when one becomes
	 * available it will connect.
	 * 
	 * @return
	 */
	private static DataInputStream getConnection() {
		LCD.clear();
		LCD.drawString("Starting BT connection", 0, 0);
		NXTConnection conn = Bluetooth.waitForConnection();
		LCD.drawString("BT Connected", 0, 1);
		return conn.openDataInputStream();
	}

	/**
	 * Closes the connection to the server
	 */
	private static void close() {
		try {
			stream.close();
		} catch (IOException e) {
		}
	}

	/**
	 * Reads the input stream for information, it will then set
	 * {@link Settings.role}, {@link Settings.startingCorner},
	 * {@link Settings.greenZoneCoords}, and {@link Settings.redZoneCoords}
	 */
	private static void getInformation() {
		// TODO verify the input of the coordinates with the server. I am
		// assuming the coordinates are sent as a series of 8 integers. The
		// order is unknown.
		try {
			Settings.role = stream.readInt();

			char useless = stream.readChar();
			int startingCorner = stream.readInt();
			// TODO figure out which numbers correspond to which corners. And
			// their values.
			switch (startingCorner) {
			case 1:
				Settings.startingCorner = StartingCorner.BOTTOM_LEFT;
				break;
			case 2:
				Settings.startingCorner = StartingCorner.BOTTOM_RIGHT;
				break;
			case 4:
				Settings.startingCorner = StartingCorner.TOP_LEFT;
				break;
			case 3:
				Settings.startingCorner = StartingCorner.TOP_RIGHT;
				break;
			}

			for (int i = 0; i < Settings.greenZoneCoords.length; i++) {
				useless = stream.readChar();
				double x = stream.readInt() * Settings.TILE_SIZE;
				useless = stream.readChar();
				double y = stream.readInt() * Settings.TILE_SIZE;
				if (Settings.role == Settings.roleBuilder) {
					Settings.greenZoneCoords[i] = new Point(x, y);
				} else {
					Settings.redZoneCoords[i] = new Point(x, y);
				}
			}

			for (int i = 0; i < Settings.redZoneCoords.length; i++) {
				useless = stream.readChar();
				double x = stream.readInt() * Settings.TILE_SIZE;
				useless = stream.readChar();
				double y = stream.readInt() * Settings.TILE_SIZE;
				if (Settings.role == Settings.roleDestroyer) {
					Settings.greenZoneCoords[i] = new Point(x, y);
				} else {
					Settings.redZoneCoords[i] = new Point(x, y);
				}
			}

			Settings.redZone = new Tile(Settings.redZoneCoords[0],
					Settings.redZoneCoords[1]);
			Settings.greenZone = new Tile(Settings.greenZoneCoords[0],
					Settings.greenZoneCoords[1]);

			Settings.role = 1;

		} catch (IOException e) {
			catchBug("Bluetooth recieve failed: " + e.getMessage());
		}

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
