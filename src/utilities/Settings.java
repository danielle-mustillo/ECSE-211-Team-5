package utilities;

import hardwareAbstraction.NXTRemoteCommand;
import hardwareAbstraction.NXTRemoteMotor;
import lejos.nxt.ColorSensor;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
/**
 * Contains most of the Motor/Sensor port settings, Physical Robot Parameters and match settings
 * @author Riley
 * @author Danielle
 *
 */
public class Settings {
	/**
	 * Colour sensor on the claw
	 */
	public static ColorSensor frontColorSensor = new ColorSensor(SensorPort.S1);
	/**
	 * left line sensor
	 */
	public static ColorSensor rearLeftColorSensor = new ColorSensor(SensorPort.S2);
	/**
	 * Right line sensor
	 */
	public static ColorSensor rearRightColorSensor = new ColorSensor(SensorPort.S3);
	
//	public static UltrasonicSensor leftUltrasonic;
//	public static UltrasonicSensor centerUltrasonic;
//	public static UltrasonicSensor rightUltrasonic;
	
	/**
	 * Left Driving motor
	 */
	public static NXTRegulatedMotor leftDriveMotor = Motor.B;
	/**
	 * Right Driving Motor
	 */
	public static NXTRegulatedMotor rightDriveMotor = Motor.A;
	
	/**
	 * The motor id that corresponds to the Claw motor. Used by {@link NXTRemoteControl}
	 */
	public static final int CLAW_MOTOR_ID = 1;
	/**
	 * The motor id that corresponds to the Ultrasonic motor. Used by {@link NXTRemoteControl}
	 */
	public static final int ULTRASONIC_MOTOR_ID = 2;
	/**
	 * The motor id that corresponds to the Lift motor. Used by {@link NXTRemoteControl}
	 */
	public static final int LIFT_MOTOR_ID = 3;
	
	public static NXTRemoteMotor clawMotor;
	public static NXTRemoteMotor ultrasonicMotor;
	public static NXTRemoteMotor liftMotor;
	
	/**
	 * Name of the slave NXT brick
	 */
	public static final String NXTSlaveName = "NXT";
	
	/**
	 * Robots Role id, set by {@link BluetoothTransmission}
	 */
	public static int role = 1;
	
	
	
	public static final int roleDestroyer = 2;
	public static final int roleBuilder = 1;
	
	
	/**
	 * Array of points corresponding to the red zone, set by {@link BluetoothTransmission}
	 */
	public static Point[] redZoneCoords = {new Point(240, 240), new Point(90,90)};
	
	public static Tile redZone = new Tile(redZoneCoords[0], redZoneCoords[1]);
	/**
	 * Array of points corresponding to the green zone, set by {@link BluetoothTransmission}
	 */
	public static Point[] greenZoneCoords = {new Point(180,180), new Point(210,210)}; //TODO remove once BT is working.
	
	public static Tile greenZone = new Tile(greenZoneCoords[0], greenZoneCoords[1]);
	/**
	 * Starting corner of the robot, set by {@link BluetoothTransmission}
	 */
	public static StartingCorner startingCorner = StartingCorner.BOTTOM_LEFT;
	/**
	 * Playing field width (in X direction)
	 */
	public static final int FIELD_X = 12;
	/**
	 * Playing field length (in y direction)
	 */
	public static final int FIELD_Y = 12;
	
	/**
	 * Line sensor distance from odometry centre
	 */
	public static final double LS_OFFSET = 15.0;
	/**
	 * distance between the line sensors
	 */
	public static final double LS_WIDTH = 11.5;
	/**
	 * Distance from the odometry centre to the line that passes through both line sensors
	 */
	public static final double LS_LENGTH = 13.5;
	/**
	 * Size of a tile
	 */
	public static final double TILE_SIZE = 30.48;
	/**
	 * Maximum carrying capacity of the robot
	 */
	public static final int maxBlockCapacity = 1;
	/**
	 * the distance from the end of the claw to the front of the ultrasonic sensor
	 */
	public static final int tipOfClawToUSDistance = 18; //TODO measure this. 
	/**
	 * the distance from the middle of the claw to the front of the ultrasonic sensor
	 */
	public static final int backOfClawToUSDistance = 2;
}
