package hardwareAbstraction;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import utilities.Communicator;

/**
 * Handles RS485 communication on the master brick. It will send commands over
 * the output stream and read int, bool, float over the input stream.
 * 
 * Code Sourced from Lejos Forums
 * (http://www.lejos.org/forum/viewtopic.php?f=7&t=2620)
 * 
 * @author cs07cc4
 * 
 */
public class NXTRemoteCommand {

	DataInputStream dis;
	DataOutputStream dos;

	/**
	 * Sets the class {@link DataInputStream} and {@link DataOutputStream} to
	 * the one in the passed {@link Communicator}
	 * 
	 * @param communicator
	 */
	public NXTRemoteCommand(Communicator communicator) {
		this.dis = communicator.dis;
		this.dos = communicator.dos;
	}

	/**
	 * Sends a simple command over the RS485 connection
	 * 
	 * @param id
	 *            id of the object to be acted on (1,2,3,4) -> (Motor.A,
	 *            Motor.B, Motor.C, RemoteultrasonicPoller)
	 * @param command
	 *            command id as according to {@link RemoteCommands} interface
	 */
	public void send(int id, int command) {
		try {
			dos.writeInt(id);
			dos.writeInt(command);

			dos.flush();

		} catch (IOException e) {

			System.out.println("send problem " + e);

		}
	}

	/**
	 * Sends a command with 1 parameter and requests immediateReturn (if true)
	 * over RS485.
	 * 
	 * @param id
	 *            id of the object to be acted on (1,2,3,4) -> (Motor.A,
	 *            Motor.B, Motor.C, RemoteultrasonicPoller)
	 * @param command
	 *            command id as according to {@link RemoteCommands} interface
	 * @param param1
	 *            parameter to be sent over RS485 (e.g. 200 for
	 *            motor.setSpeed(200))
	 * @param immediateReturn
	 *            whether to return immediately or not
	 */
	public void send(int id, int command, int param1, boolean immediateReturn) {
		try {
			dos.writeInt(id);
			dos.writeInt(command);
			dos.writeInt(param1);
			dos.writeBoolean(immediateReturn);
			dos.flush();

		} catch (IOException e) {

			System.out.println("send problem " + e);

		}
	}

	/**
	 * Sends a command with 1 parameter over RS485.
	 * 
	 * @param id
	 *            id of the object to be acted on (1,2,3,4) -> (Motor.A,
	 *            Motor.B, Motor.C, RemoteultrasonicPoller)
	 * @param command
	 *            command id as according to {@link RemoteCommands} interface
	 * @param param1
	 *            parameter to be sent over RS485 (e.g. 200 for
	 *            motor.setSpeed(200))
	 */
	public void send(int id, int command, int param1) {
		try {
			dos.writeInt(id);
			dos.writeInt(command);
			dos.writeInt(param1);

			dos.flush();

		} catch (IOException e) {

			System.out.println("send problem " + e);

		}
	}

	/**
	 * Reads an int from the data input stream
	 * 
	 * @return int read from data input stream
	 * @throws IOException
	 */
	public int getInt() throws IOException {

		return dis.readInt();

	}

	/**
	 * Reads a float from the data input stream
	 * 
	 * @return float read from data input stream
	 * @throws IOException
	 */
	public float getFloat() throws IOException {

		return dis.readFloat();

	}

	/**
	 * Reads a boolean from the data input stream
	 * 
	 * @return boolean read from data input stream
	 * @throws IOException
	 */
	public boolean getBool() throws IOException {

		return dis.readBoolean();

	}
}
