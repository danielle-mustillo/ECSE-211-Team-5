package utilities;

import lejos.nxt.ColorSensor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.UltrasonicSensor;

public class Settings {

	public static ColorSensor frontColorSensor;
	public static ColorSensor rearLeftColorSensor;
	public static ColorSensor rearRightColorSensor;
	
	public static UltrasonicSensor leftUltrasonic;
	public static UltrasonicSensor centerUltrasonic;
	public static UltrasonicSensor rightUltrasonic;
	
	public static NXTRegulatedMotor leftDriveMotor;
	public static NXTRegulatedMotor rightDriveMotor;
	public static NXTRegulatedMotor forkliftMotor;
	public static NXTRegulatedMotor ultrasonicMotor;
	public static NXTRegulatedMotor clawMotor;
	
	public static final String NXTSlaveName = "NXT";
	
}
