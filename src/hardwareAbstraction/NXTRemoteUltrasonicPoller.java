package hardwareAbstraction;

import java.io.IOException;

import lejos.nxt.comm.RConsole;

/**
 * Abstraction layer for the ultrasonic poller over RS485.  
 * Uses methods from {@link NXTRemoteCommand} to send and receive appropriate data.
 * Also implements {@link RemoteCommands} to assist with keeping track of command ids.  
 * Implements {@link UltrasonicPoller} to ensure all required methods are implemented as they were before.  
 * @author Danielle
 *
 */
public class NXTRemoteUltrasonicPoller implements RemoteCommands, UltrasonicPoller {
	/**
	 * Id of the {@link RemoteUltrasonicPoller}.  Should be four
	 */
	private int id;
	/**
	 * Class reference to the communication protocol abstraction layer class {@link NXTRemoteCommand}
	 */
	private NXTRemoteCommand sensorCommand;
	
	/**
	 * Initializes id to the passed id. Sets reference to {@link sensorCommand}
	 * @param nxtCommand
	 * @param id
	 */
	public NXTRemoteUltrasonicPoller(NXTRemoteCommand nxtCommand, int id) {
		this.id = id;
		this.sensorCommand = nxtCommand;
	}
	

	/**
	 * Sends command to start {@link RemoteUltrasonicPoller}
	 */
	@Override
	public void start() {
		RConsole.println("start USP");
		sensorCommand.send(id, START_USP);
	}
	
	/**
	 * Sends command to stop {@link RemoteUltrasonicPoller}
	 */
	@Override
	public void stop() {
		RConsole.println("stop USP");
		sensorCommand.send(id, STOP_USP);
	}
	
	/**
	 * Sends command to reset the {@link RemoteUltrasonicPoller}
	 */
	@Override
	public void resetUSP() {
		RConsole.println("reset USP");
		sensorCommand.send(id, RESET_USP);
	}


	/**
	 * Checks to see if the {@link RemoteUltrasonicPoller} is setup (readings array is full of values).
	 * @return the boolean response from the slave NXT brick
	 */
	@Override
	public boolean isSetup() {
		boolean setup = false;
		RConsole.println("is Setup");
		sensorCommand.send(id, IS_SETUP);
		try {
			setup = sensorCommand.getBool();
		} catch (IOException e) {
			System.out.println("Recieve Error");
			//no exception expected here. 
		}
		return setup;
	}

	/**
	 * Sends command to calculate the filtered reading for the passed sensor (0,1,2) -> (left,center,right)
	 * <p>
	 * @return the filtered sensor reading (median filtering)
	 */
	@Override
	public int getUSReading(int sensor) {
		int reading = -1;
		
		sensorCommand.send(id, GET_US_READING, sensor);
		try {
			reading = sensorCommand.getInt();
		} catch (IOException e) {
			//no exception expected here. 
		}
		RConsole.println("Reading" + String.valueOf(reading));
		return reading;	
		
	}


	@Override
/*	public int getLowestReading() {
		int reading = -1;
		RConsole.println("getLowestReading from USP");
		sensorCommand.send(id, GET_LOWEST_READING);
		try {
			reading = sensorCommand.getInt();
		} catch (IOException e) {
			//no exception expected here. 
		}
		return reading;	
	}
*/
	/**
	 * Uses {@link getUSReading()} to get the filtered reading for the left, center and right ultrasonic sensor
	 * @return the smallest of the readings from the 3 ultrasonic sensors
	 */
	public int getLowestReading() {
		int left = getUSReading(0);
		int center = getUSReading(1);
		int right = getUSReading(2);
		
		if(left <= center && left <= right) {
			return left;
		} else if (center <= right) {
			return center;
		} else {
			return right;
		}
		
	}

	@Override
	/**
	 * Sends command to get the lowest reading, and compares the readings
	 * @return {@link USPosition} that corresponds to the sensor with the lowest reading
	 */
	public USPosition getLowestSensor() {
		int reading = -1;
		RConsole.println("getLowestReading from USP");
		sensorCommand.send(id, GET_LOWEST_READING);
		try {
			reading = sensorCommand.getInt();
		} catch (IOException e) {
			//no exception expected here. 
		}
		USPosition usPos = null;
		switch(reading) {
		case left : usPos = USPosition.LEFT;
		break;
		case center : usPos =  USPosition.CENTER;
		break;
		case right : usPos = USPosition.RIGHT;
		break;
		}
		return usPos;
	}


	@Override
	/**
	 * Sends command to set the {@link RemoteUltrasonicPoller} to ping only the left Ultrasonic sensor
	 */
	public void pingLeft() {
		RConsole.println("ping left USP");
		sensorCommand.send(id, PING_LEFT);
	}


	/**
	 * Sends command to set the {@link RemoteUltrasonicPoller} to ping only the right Ultrasonic sensor
	 */
	@Override
	public void pingRight() {
		RConsole.println("ping right USP");
		sensorCommand.send(id, PING_RIGHT);
	}


	/**
	 * Sends command to set the {@link RemoteUltrasonicPoller} to ping only the center Ultrasonic Sensor
	 */
	@Override
	public void pingCenter() {
		RConsole.println("ping center USP");
		sensorCommand.send(id, PING_CENTER);
	}


	/**
	 * Sends command to {@link RemoteUltrasonicPoller} to ping all three ultrasonic Sensors at the same time
	 */
	@Override
	public void pingAll() {
		RConsole.println("ping all USP");
		sensorCommand.send(id, PING_ALL);
	}


	/**
	 * Sends command to {@link RemoteUltrasonicPoller} to ping all three ultrasonic sensors, but only one at a time
	 */
	@Override
	public void pingSequential() {
		RConsole.println("ping sequential USP");
		sensorCommand.send(id, PING_SEQUENTIAL);
	}
}
