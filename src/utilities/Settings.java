package utilities;

import lejos.nxt.ColorSensor;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.remote.RemoteMotor;

public class Settings {

	
	
	public static ColorSensor frontColorSensor = new ColorSensor(SensorPort.S1);
	public static ColorSensor rearLeftColorSensor = new ColorSensor(SensorPort.S2);
	public static ColorSensor rearRightColorSensor = new ColorSensor(SensorPort.S3);
	
	public static UltrasonicSensor leftUltrasonic;
	public static UltrasonicSensor centerUltrasonic;
	public static UltrasonicSensor rightUltrasonic;
	
	public static NXTRegulatedMotor leftDriveMotor = Motor.B;
	public static NXTRegulatedMotor rightDriveMotor = Motor.A;
	
	public static RemoteMotor forkliftMotor;
	public static RemoteMotor ultrasonicMotor;
	public static RemoteMotor clawMotor;
	
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
