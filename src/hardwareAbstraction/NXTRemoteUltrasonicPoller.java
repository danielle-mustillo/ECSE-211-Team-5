package hardwareAbstraction;

import java.io.IOException;

import lejos.nxt.comm.RConsole;

public class NXTRemoteUltrasonicPoller implements RemoteCommands, UltrasonicPoller {
	private int id;
	private NXTRemoteCommand sensorCommand;
	
	public NXTRemoteUltrasonicPoller(NXTRemoteCommand nxtCommand, int id) {
		this.id = id;
		this.sensorCommand = nxtCommand;
	}
	

	@Override
	public void start() {
		RConsole.println("start USP");
		sensorCommand.send(id, START_USP);
	}
	
	@Override
	public void stop() {
		RConsole.println("stop USP");
		sensorCommand.send(id, STOP_USP);
	}
	
	@Override
	public void resetUSP() {
		RConsole.println("reset USP");
		sensorCommand.send(id, RESET_USP);
	}


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
	public void pingLeft() {
		RConsole.println("ping left USP");
		sensorCommand.send(id, PING_LEFT);
	}


	@Override
	public void pingRight() {
		RConsole.println("ping right USP");
		sensorCommand.send(id, PING_RIGHT);
	}


	@Override
	public void pingCenter() {
		RConsole.println("ping center USP");
		sensorCommand.send(id, PING_CENTER);
	}


	@Override
	public void pingAll() {
		RConsole.println("ping all USP");
		sensorCommand.send(id, PING_ALL);
	}


	@Override
	public void pingSequential() {
		RConsole.println("ping sequential USP");
		sensorCommand.send(id, PING_SEQUENTIAL);
	}
	
	@Override
	public void pingSides() {
		RConsole.println("ping side USP's");
		sensorCommand.send(id, PING_SIDES);
	}
}
