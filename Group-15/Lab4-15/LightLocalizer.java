import java.util.ArrayList;
import java.util.List;

import lejos.nxt.ColorSensor;
import lejos.nxt.Sound;
import lejos.nxt.comm.RConsole;

/**
 * 
 * @project Lab 4 Localization 
 * @names Riley van Ryswyk & Aditya Saha
 * @studentID 260447357 & 260453165
 * @group 15
 * @course ECSE 211 
 * @date 06/10/2013
 * 
 * Light sensor localization
 */

public class LightLocalizer {
	
	private static final long CORRECTION_PERIOD = 10;
	private Odometer odo;
	private TwoWheeledRobot robot;
	private ColorSensor lightSensor;
	
	//store light sensor values (for filtering purposes)
	private List<Double> lsValues = new ArrayList<Double>();
	
	//the headings when a line is detected
	private List<Double> angles = new ArrayList<Double>();
	
	// Keep track of whether we are on a line on not
	private boolean onLine = false;
	private int lineCount = 0;
	
	// tile size, light sensor offset
	private final double TILE_SIZE = 30.48, LS_OFFSET = 12.8;
	
	/**
	 * Light sensor localization constructor
	 * @param odo
	 * @param ls
	 */
	public LightLocalizer(Odometer odo, ColorSensor ls) {
		this.odo = odo;
		this.robot = odo.getTwoWheeledRobot();
		this.lightSensor = ls;
		
		//for line detection
		Sound.setVolume(50);
		// turn on the light to red
		ls.setFloodlight(0);
		
	}
	
	/**
	 * do light sensor localization
	 * finds x, y, theta to within 0.2cm, 1 deg
	 */
	public void doLocalization() {
		// drive to location listed in tutorial
		// start rotating and clock all 4 gridlines
		// do trig to compute (0,0) and 0 degrees
		// when done travel to (0,0) and turn to 0 degrees
		
		// travel to roughly -4cm, -4cm
		odo.getNavigation().travelTo(-4.0,-4.0);
		
		//Initialize line count to 0
		lineCount = 0;
		
		//clear angles array
		angles.clear();
		
		//populate light sensor array with a sufficient amount of values
		lsValues.add((double) lightSensor.getRawLightValue() / 2.0);
		lsValues.add((double) lightSensor.getRawLightValue() / 2.0);
		lsValues.add((double) lightSensor.getRawLightValue() / 2.0);
		
		//ensures that we pass the lines in the same order every time | could be avoided with a more sophisticated calculation of thetaX and thetaY
		if(odo.getTheta() > 80) {
			odo.getNavigation().turnTo(45);
		}
				
		//to mantain the correct CORRECTION_PERIOD
		long correctionStart, correctionEnd;
		
		//line detection loop
		while (lineCount < 4) {
			correctionStart = System.currentTimeMillis();
			
			//sets robot to rotate at 35 deg/s
			robot.setSpeeds(0, 35);
			
			//adds light sensor value to array of values
			lsValues.add((double) lightSensor.getRawLightValue() / 2.0);
				
			//filters the light sensor information, and determines if a line was detected or not
			filterLightSensor();
			
			// this ensure the odometry correction occurs only once every period
			correctionEnd = System.currentTimeMillis();
			
			if (correctionEnd - correctionStart < CORRECTION_PERIOD) {
				try {
					Thread.sleep(CORRECTION_PERIOD
							- (correctionEnd - correctionStart));
				} catch (InterruptedException e) {
					// there is nothing to be done here because it is not
					// expected that the odometry correction will be
					// interrupted by another thread
				}
			}
		}
		
		//stop robot
		robot.setSpeeds(0, 0);
		
		//resets onLine
		onLine = false;
		
		// calculates the angle spanned between the headings of the robot when the lines on the X and Y axes were detected respectively
		double thetaX = angles.get(0) - angles.get(2);
		double thetaY = angles.get(1) - angles.get(3);
		
		// Based on the above angles, the current x, y coordinates are calculated (assuming 0,0 is the closest intersection)
		double x = -LS_OFFSET * Math.cos(Math.toRadians(thetaY/2.0));
		double y = -LS_OFFSET * Math.cos(Math.toRadians(thetaX/2.0));
		
		// if the first detection was at a heading of between [0,60], we are not in the negative x,y region
		// so invert y | x will be correctly inverted
		if(angles.get(0) < 60) {
			y = -y;
		} 
		
		// we are not in the right stop to determine theta -> update current position (x,y) and then redo localization
		if(x > 0 || y > 0) {
			odo.setPosition(new double [] {x, y, odo.getTheta() }, new boolean [] {true, true, true});
			doLocalization();
		}
		//localization was successful
		else {
			// calculate amount to adjust theta by, achieved through averaging the values for dTheta calculated on both the x and y axes
			double dTheta = (439-thetaX/2.0-thetaY/2.0-angles.get(3)-angles.get(2))/2.0;
			
			//update our current position 
			odo.setPosition(new double [] {x, y, odo.getTheta()+dTheta }, new boolean [] {true, true, true});
		}
		
	}
	
	/**
	 * Applys smoothing and differencing to the recorded values
	 * detects when black line is detected and when the regular floor is detected again 
	 */
	public void filterLightSensor() {
		
		double result;
		
		// Smooth and difference
		result = -lsValues.get(0) - lsValues.get(1) + lsValues.get(2) + lsValues.get(3);
		
		//if not currently on a line and filter result is less than -45 we just enter a line			
		if(!onLine && result < -45) {
			onLine = true;
			
			//go to line detection logic
			lineDetected();
			
			//alert us with a beep that a line was detected
			Sound.beep();
			
			lineCount++;
			
		} 
		//if we are on a line and filter result > 45 we have now left the line
		else if(onLine && result > 45) {
			onLine = false;
		}
		
		//shift values left by 1 for next iteration
		lsValues.remove(0);
	}
	
	/**
	 * when a line is detected, add it to the array of line detected angles
	 */
	public void lineDetected() {
		angles.add(odo.getTheta());
	}

}
