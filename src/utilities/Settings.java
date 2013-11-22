package utilities;

import hardwareAbstraction.NXTRemoteCommand;
import hardwareAbstraction.NXTRemoteMotor;
import lejos.nxt.ColorSensor;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;

public class Settings {

	private static Communicator com = new Communicator("NXT");
	
	private static NXTRemoteCommand motorCommand = new NXTRemoteCommand(com);
	
	public static ColorSensor frontColorSensor = new ColorSensor(SensorPort.S1);
	public static ColorSensor rearLeftColorSensor = new ColorSensor(SensorPort.S2);
	public static ColorSensor rearRightColorSensor = new ColorSensor(SensorPort.S3);
	
//	public static UltrasonicSensor leftUltrasonic;
//	public static UltrasonicSensor centerUltrasonic;
//	public static UltrasonicSensor rightUltrasonic;
	
	public static NXTRegulatedMotor leftDriveMotor = Motor.B;
	public static NXTRegulatedMotor rightDriveMotor = Motor.A;
	
	public static NXTRemoteMotor clawMotor = new NXTRemoteMotor(motorCommand, 1);
	public static NXTRemoteMotor ultrasonicMotor = new NXTRemoteMotor(motorCommand, 2);
	public static NXTRemoteMotor liftMotor = new NXTRemoteMotor(motorCommand, 3);
	
	public static final String NXTSlaveName = "NXT";
	
	public static int role;
	public static Point[] redZoneCoords;
	public static Point[] greenZoneCoords = new Point[2]; //TODO remove once BT is working.
	public static StartingCorner startingCorner = StartingCorner.BOTTOM_LEFT;
	
	public static final double LS_OFFSET = 15.0;
	public static final double LS_WIDTH = 11.5;
	public static final double LS_LENGTH = 13.5;
	public static final double TILE_SIZE = 30.48;
	public static final int maxBlockCapacity = 1;
	
	public static final int clawToUSDistance = 7; //TODO measure this. 
	
}
