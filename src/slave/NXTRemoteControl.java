package slave;

import hardwareAbstraction.RemoteCommands;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import slave.RemoteUltrasonicPoller.USPState;
import lejos.nxt.*;
import lejos.nxt.comm.NXTCommConnector;
import lejos.nxt.comm.NXTConnection;
import lejos.nxt.comm.RConsole;
import lejos.nxt.comm.RS485;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.RegulatedMotorListener;

/**
 * Enables remote control of a motor object using RS485.
 * 
 * <p>
 * Listens for commands sent on RS485 connection. When a new command is received
 * it will be interpreted, and carried out. If data is to be sent back, it will
 * send data back through the RS485 connection.
 * <p>
 * It will wait and automatically connect to the master brick over RS485.
 * <p>
 * A modified version from http://www.lejos.org/forum/viewtopic.php?f=7&t=2620
 * 
 * @author cs07cc4 (LeJOS forum username)
 **/
public class NXTRemoteControl extends Thread implements RemoteCommands,
		RegulatedMotorListener {

	private DataInputStream dis = null;
	private DataOutputStream dos = null;

	/**
	 * Communication connector object
	 */
	private NXTCommConnector connector;
	/**
	 * Connection between the NXTs
	 */
	private NXTConnection con;

	/**
	 * Id of the motor/poller to be acted on
	 */
	private int id;
	/**
	 * The command to be carried out as according to {@link RemoteCommands}
	 * interface
	 */
	private int command;

	private int tachoCount;
	private boolean isMoving;
	private int speed;
	private float maxSpeed;
	private int acceleration;
	private int angle;
	private boolean immediateReturn, isStalled;
	private boolean isSetup = false;

	/**
	 * Motor A
	 */
	private static NXTRegulatedMotor A = null;
	/**
	 * Motor B
	 */
	private static NXTRegulatedMotor B = null;
	/**
	 * Motor C
	 */
	private static NXTRegulatedMotor C = null;
	/**
	 * Ultrasonic Poller
	 */
	private static RemoteUltrasonicPoller usp = null;

	/**
	 * Constructor for only 1 motor. Not used
	 * 
	 * @param portA
	 */
	public NXTRemoteControl(MotorPort portA) {

		A = new NXTRegulatedMotor(portA);

	}

	/**
	 * Constructor for two motors. not used
	 * 
	 * @param portA
	 * @param portB
	 */
	public NXTRemoteControl(MotorPort portA, MotorPort portB) {

		A = new NXTRegulatedMotor(portA);
		B = new NXTRegulatedMotor(portB);

	}

	/**
	 * Constructor for three motors. Not used.
	 * 
	 * @param portA
	 * @param portB
	 * @param portC
	 */
	public NXTRemoteControl(MotorPort portA, MotorPort portB, MotorPort portC) {

		A = new NXTRegulatedMotor(portA);
		B = new NXTRegulatedMotor(portB);
		C = new NXTRegulatedMotor(portC);

	}

	/**
	 * Constructor for three motors and the {@link RemoteUltrasonicPoller}
	 * 
	 * @param portA
	 * @param portB
	 * @param portC
	 * @param usPoller
	 */
	public NXTRemoteControl(MotorPort portA, MotorPort portB, MotorPort portC,
			RemoteUltrasonicPoller usPoller) {
		A = new NXTRegulatedMotor(portA);
		B = new NXTRegulatedMotor(portB);
		C = new NXTRegulatedMotor(portC);
		usp = usPoller;
	}

	/**
	 * Executes motor commands. It will read the {@link dis} for a param if
	 * necessary. If a value is to be returned, it will return it via the
	 * {@link dos}
	 * 
	 * @param id
	 *            - id of the motor (1 -> MotorA, 2 -> MotorB, 3-> MotorC)
	 * @param command
	 *            - command to be carried out as per {@link RemoteCommands}
	 *            interface
	 * @throws IOException
	 */
	protected void executeMotorCommand(int id, int command) throws IOException {

		NXTRegulatedMotor motor = getMotor(id);

		switch (command) {

		case FORWARD: {
			RConsole.println("FORWARD");
			motor.forward();
			break;
		}

		case BACKWARD: {
			RConsole.println("BACKWARD");
			motor.backward();
			break;
		}

		case ROTATE: {
			RConsole.println("ROTATE");
			angle = dis.readInt();
			RConsole.println("angle" + angle);
			immediateReturn = dis.readBoolean();
			RConsole.println("immediateReturn" + immediateReturn);
			motor.rotate(angle, immediateReturn);
			break;
		}

		case STOP: {
			RConsole.println("STOP");
			motor.stop();
			break;
		}

		case ROTATE_TO: {
			RConsole.println("ROTATE_TO");
			angle = dis.readInt();
			immediateReturn = dis.readBoolean();
			RConsole.println("angle" + angle);
			RConsole.println("immediateReturn" + immediateReturn);
			motor.rotateTo(angle, immediateReturn);
			break;
		}

		case FLT: {
			RConsole.println("FLT");
			motor.flt();
			break;
		}

		case GET_TACHO_COUNT: {
			RConsole.println("GET_TACHO_COUNT");
			tachoCount = motor.getTachoCount();
			RConsole.println("tachoCount" + tachoCount);
			dos.writeInt(tachoCount);
			dos.flush();
			break;
		}

		case IS_MOVING: {
			RConsole.println("IS_MOVING");
			isMoving = motor.isMoving();
			RConsole.println("isMoving" + isMoving);
			dos.writeBoolean(isMoving);
			dos.flush();
			break;
		}

		case SET_SPEED: {
			int speed;
			RConsole.println("SET_SPEED");
			speed = dis.readInt();
			RConsole.println("SET_SPEED=" + speed);
			motor.setSpeed(speed);
			break;
		}

		case SET_ACCELERATION: {
			RConsole.println("SET_ACCELERATION");
			acceleration = dis.readInt();
			RConsole.println("acceleration=" + acceleration);
			motor.setAcceleration(acceleration);

			break;
		}

		case GET_LIMIT_ANGLE: {
			RConsole.println("GET_LIMIT_ANGLE");
			angle = motor.getLimitAngle();
			RConsole.println("angle=" + angle);
			dos.writeInt(angle);
			dos.flush();
			break;
		}

		case RESET_TACHO_COUNT: {
			RConsole.println("RESET_TACHO_COUNT");
			motor.resetTachoCount();
			break;
		}

		case GET_SPEED: {
			RConsole.println("GET_SPEED");
			speed = motor.getSpeed();
			RConsole.println("speed=" + speed);
			dos.writeInt(speed);
			dos.flush();
			break;
		}

		case IS_STALLED: {
			RConsole.println("IS_STALLED");
			isStalled = motor.isMoving();
			RConsole.println("isStalled=" + isStalled);
			dos.writeBoolean(isStalled);
			dos.flush();
			break;

		}

		case GET_ROTATION_SPEED: {
			RConsole.println("GET_ROTATION_SPEED");
			speed = motor.getRotationSpeed();
			RConsole.println("speed=" + speed);
			dos.writeInt(speed);
			dos.flush();
			break;
		}

		case GET_MAX_SPEED: {
			RConsole.println("GET_MAX_SPEED");
			maxSpeed = motor.getMaxSpeed();
			RConsole.println("maxSpeed=" + maxSpeed);
			dos.writeFloat(maxSpeed);
			dos.flush();
			break;

		}
		case ADD_LISTENER: {
			motor.addListener(this);
			// if (listenersCon==null){
			// connectionThread connect=new connectionThread();
			// connect.start();
			// }

			break;

		}

		case SUSPEND_REGULATION: {
			boolean suspended;
			RConsole.println("SUSPEND_REGULATION");
			suspended = motor.suspendRegulation();
			dos.writeBoolean(suspended);
			dos.flush();
			break;

		}
		}

	}

	/**
	 * Executes Ultrasonic Poller sensor commands. It will read a param for the
	 * {@link dis} if necessary. If a return value is required, it will be sent
	 * via the {@link dos}.
	 * 
	 * @param id
	 *            - will always be four, used to differentiate from motors
	 * @param command
	 *            - command to execute as per {@link RemoteCommands} interface
	 * @throws IOException
	 */
	protected void executeSensorCommand(int id, int command) throws IOException {

		switch (command) {
		case START_USP: {
			RConsole.println("START_USP");
			usp.start();
			break;
		}
		case STOP_USP: {
			RConsole.println("STOP_USP");
			usp.stop();
			break;
		}
		case RESET_USP: {
			RConsole.println("RESET_USP");
			usp.resetUSP();
			break;
		}
		case IS_SETUP: {
			RConsole.println("IS_SETUP");
			isSetup = usp.isSetup();
			dos.writeBoolean(isSetup);
			dos.flush();
			break;
		}
		case GET_US_READING: {
			RConsole.println("GET_US_READING");
			int sensor = dis.readInt();
			RConsole.println("sensor= " + sensor);
			int reading = usp.getUSReading(sensor);
			RConsole.println("value= " + String.valueOf(reading));
			dos.writeInt(reading);
			dos.flush();
			break;
		}
		case GET_LOWEST_READING: {
			RConsole.println("GET_LOWEST_READING");
			usp.getLowestReading();
			break;
		}
		case PING_CENTER: {
			RConsole.println("PING_CENTER");
			usp.setUSPState(USPState.PING_CENTER);
			break;
		}
		case PING_LEFT: {
			RConsole.println("PING_LEFT");
			usp.setUSPState(USPState.PING_LEFT);
			break;
		}
		case PING_RIGHT: {
			RConsole.println("PING_RIGHT");
			usp.setUSPState(USPState.PING_RIGHT);
			break;
		}
		case PING_ALL: {
			RConsole.println("PING_ALL");
			usp.setUSPState(USPState.PING_ALL);
			break;
		}
		case PING_SEQUENTIAL: {
			RConsole.println("PING_SEQUENTIAL");
			usp.setUSPState(USPState.PING_SEQUENTIAL);
			break;
		}
		case PING_SIDES: {
			RConsole.println("PING_SIDES");
			usp.setUSPState(USPState.PING_SIDES);
			break;
		}

		}
	}

	/**
	 * Returns the RemoteUltrasonicPoller if id == 4
	 * 
	 * @param id
	 * @return
	 */
	protected RemoteUltrasonicPoller getSensor(int id) {

		switch (id) {
		case 4:
			return usp;
		}
		return null;
	}

	/**
	 * Returns the Motor designation (A ||B || C) based on id (1 || 2 || 3)
	 * 
	 * @param id
	 * @return
	 */
	protected NXTRegulatedMotor getMotor(int id) {

		switch (id) {
		case 1:
			return A;

		case 2:
			return B;

		case 3:
			return C;
		}

		return null;
	}

	/**
	 * Returns numerical motor id based on input motor
	 * 
	 * @param motor
	 * @return
	 */
	protected int getMotor(RegulatedMotor motor) {

		if (motor.equals(Motor.A))
			return 1;
		else if (motor.equals(Motor.B))
			return 2;
		else if (motor.equals(Motor.C))
			return 3;

		return -1;
	}

	/**
	 * Connects to the other NXT brick. It will then continually check the
	 * {@link dis} for any new commands, and call the necessary execute method
	 */
	public void run() {

		while (true) {

			// Wait for connection if not connected
			if (con == null) {
				LCD.drawString("waiting", 0, 2);

				connector = RS485.getConnector();
				con = connector.waitForConnection(0, NXTConnection.RAW);

				if (con != null) {
					dis = con.openDataInputStream();
					dos = con.openDataOutputStream();
				}
				LCD.drawString("Connected", 0, 2);
				LCD.refresh();
			}

			try {
				id = dis.readInt();
				command = dis.readInt();
				RConsole.println("id=" + id + "command =" + command);
				if (id > 3) {
					executeSensorCommand(id, command);
				} else {
					executeMotorCommand(id, command);
				}

			} catch (IOException e) {
				// A.stop();
				// B.stop();
				// con.close();
				// con = null;
				// e.printStackTrace();
			}

		}
	}

	@Override
	/**
	 * Not used. Left for future interest. 
	 */
	public void rotationStarted(RegulatedMotor motor, int tachoCount,
			boolean stalled, long timeStamp) {

		// try {
		//
		// int id=getMotor( motor);
		// lisdos.writeInt(id);
		// lisdos.writeInt(ROTATION_STARTED);
		// lisdos.writeInt(tachoCount);
		// lisdos.writeBoolean(stalled);
		// lisdos.writeLong(timeStamp);
		// lisdos.flush();
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
	}

	@Override
	/**
	 * Not used. Left for future interest. 
	 */
	public void rotationStopped(RegulatedMotor motor, int tachoCount,
			boolean stalled, long timeStamp) {

		// try {
		//
		// int id=getMotor(motor);
		// lisdos.writeInt(id);
		// lisdos.writeInt(ROTATION_STOPPED);
		// lisdos.writeInt(tachoCount);
		// lisdos.writeBoolean(stalled);
		// lisdos.writeLong(timeStamp);
		// lisdos.flush();
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

	}

	// public class connectionThread extends Thread {
	//
	// public void run(){
	//
	// // Wait for connection if not connected
	// if (listenersCon == null) {
	// LCD.drawString("waiting", 0, 2);
	//
	// connector = RS485.getConnector();
	// listenersCon = connector.waitForConnection(0, NXTConnection.RAW);
	//
	// if (con != null) {
	// lisdis = con.openDataInputStream();
	// lisdos = con.openDataOutputStream();
	// }
	// LCD.drawString("Connected", 0, 2);
	// LCD.refresh();
	// }
	// }
	// }

}