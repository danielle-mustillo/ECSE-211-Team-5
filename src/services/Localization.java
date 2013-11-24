package services;

import hardwareAbstraction.Forklift;
import controllers.State;
import utilities.*;
import lejos.nxt.LCD;
import lejos.nxt.Sound;
import lejos.nxt.comm.RConsole;
import lejos.util.Timer;
import lejos.util.TimerListener;
import manager.*;

public class Localization implements TimerListener {
	
	private Manager manager;
	private Timer timer;
	
	
	//Speed in deg/sec of which to rotate during localization
	private double ROTATION_SPEED = 30;
	
	//period to check ultrasonic sensor in ms
	private final int UPDATE_PERIOD = 20;
	
	//threshold distance in cm, to determine if we are at a critical angle
	private final int THRESHOLD = 30;
	
	private boolean rising;
	private double angleA;
	private double angleB;
	
	private boolean lineLocalization;
	private int rightLineCount;
	private int leftLineCount;
	private double[] lineDetectedHeadings = new double[8]; 
	
	public boolean corrected = false;
	
	public Localization(Manager manager) {
		this.manager = manager;
		this.timer = new Timer(UPDATE_PERIOD, this);
	}
	
	/**
	 * Starts the localization process
	 */
	public void start() {
		
		//Ensure the Center ultrasonic is at a good height and wait till it is done
		//manager.um.nap(Forklift.setHeight(Forklift.ForkliftState.SCAN_HEIGHT_LOW));
		
		//Retrieves center Ultrasonic reading
		int usReading = updateUltrasonic();
		
		//Ultrasonic poller no yet ready
		if(usReading < 4) {
			manager.um.nap(120);
			start();
		}
		
		else if(usReading < THRESHOLD) {
			rising = true;
			
		} 
		//currently not facing a wall, use falling edge, then rising edge
		else if (usReading > THRESHOLD) {
			rising = false;
			
		} 
		//on the threshold, so start moving then try to start again
		else {
			
			manager.hm.drive.setSpeeds(0, ROTATION_SPEED);
			manager.um.nap(50);
			start();
			return;
		}
		
		angleA = Double.NaN;
		angleB = Double.NaN;
		lineDetectedHeadings[3] = Double.NaN;
		
		timer.start();
		
	}
	
	public void stop() {	
		timer.stop();
		manager.hm.drive.stop();
	}
	
	/**
	 * Controls Localization
	 * Calls relevant methods depending on stage of localization 
	 */
	public void timedOut() {
		// ultrasonic localization complete. 
		if(Double.isNaN(angleB)) {
			ultrasonicLocalization();
		} 
//		//not finished line localization
		else if(leftLineCount < 4 || rightLineCount < 4) {
			//move to correct orientation for line localization
			if(!lineLocalization) {
				prepareLineLocalization();
			}else{
				lineLocalization();
				}
		} 
		//localization complete, update position
		else {
			stop();
			updatePosition();
			adjustForStartingCorner();
			manager.cm.setState(State.TESTING);
		}
	}
	
	/**
	 * If the robot starts facing the fall the robot will do rising, rising edge detection (angleA, angleB)
	 * if the robot starts facing the field the robot will do falling edge, rising edge (angleA, angleB) 
	 */
	public void ultrasonicLocalization() {
		int distance = updateUltrasonic();
		
		if(Double.isNaN(angleA)) {
			if(rising) {
				manager.hm.drive.setSpeeds(0, -ROTATION_SPEED);
				if(distance > THRESHOLD) {
					
					Sound.beep();
					angleA = manager.sm.odo.getTheta();
					RConsole.println(String.valueOf(angleA));
					manager.hm.drive.setSpeeds(0, ROTATION_SPEED);
				}
			} else {
				manager.hm.drive.setSpeeds(0, ROTATION_SPEED);
				if(distance < THRESHOLD) {
					Sound.beep();
					manager.hm.drive.stop();
					angleA = manager.sm.odo.getTheta();
					RConsole.println(String.valueOf(angleA));
				}
			}
		} else {
			manager.hm.drive.setSpeeds(0, ROTATION_SPEED);
			if(distance > THRESHOLD && Math.abs(angleA-manager.sm.odo.getTheta()) > 1) {
				Sound.beep();
				angleB = manager.sm.odo.getTheta();
				RConsole.println(String.valueOf(angleB));
				updateTheta();
				manager.hm.drive.setSpeeds(0, 0);
			}
		}
	}
	
	/**
	 * Updates theta based on the results of ultrasonic sensor localization
	 */
	public void updateTheta() {
		double deltaTheta = -(angleA + angleB) / 2;
		if(rising) {
			//Depending on what angle is bigger, offset deltaTheta to the correct amount
			if(angleA > angleB) {
				deltaTheta +=  5.0 * Math.PI / 4.0;
			} else {
				deltaTheta +=  Math.PI / 4.0;
			}
		} else {
			//Depending on what angle is bigger, offset deltaTheta to the correct amount
			if(angleA > angleB) {
				deltaTheta +=  4.45 * Math.PI / 4.0;
			} else {
				deltaTheta +=  0.45 * Math.PI / 4.0;
			}
		}
		
		//update the odometer
		manager.sm.odo.adjustPosition(0, 0, deltaTheta);
		
	}
	
	/**
	 * calls checkLineSensor for each lineSensor
	 */
	public void lineLocalization() {
		checkLineSensor(true);
		checkLineSensor(false);
	}
	
	/**
	 * Updates the odometers position based on line localization results
	 */
	public void updatePosition() {
		
		double thetaXminus = (lineDetectedHeadings[0] + lineDetectedHeadings[4]) / 2.0
							 + ( (lineDetectedHeadings[4] < Math.PI) ? Math.PI : 0 );  //Correction term
		double thetaYminus = (lineDetectedHeadings[3] + lineDetectedHeadings[7]) / 2.0;
		double thetaYplus = (lineDetectedHeadings[1] + lineDetectedHeadings[5]) / 2.0;
		double thetaXplus = (lineDetectedHeadings[2] + lineDetectedHeadings[6]) / 2.0;
		
		double thetaX = thetaXminus - thetaXplus;
		double thetaY = thetaYplus - thetaYminus;
		
		double x = -Settings.LS_OFFSET * Math.cos(thetaY/2.0);
		double y = -Settings.LS_OFFSET * Math.cos(thetaX/2.0);
		
		double dThetaX = -Math.PI/2.0 + thetaX / 2.0 - thetaXminus;
		double dThetaY = -Math.PI - thetaYminus - thetaY/2.0;
		
		double dTheta = (dThetaX + dThetaY) / 2.0;
		
		manager.sm.odo.adjustPosition(x, y, dTheta);
		
		
	}
	
	public void prepareLineLocalization() {
		if(manager.sm.odo.getTheta() > (Math.PI/4 + 0.2)) {
			manager.hm.drive.setSpeeds(0, ROTATION_SPEED);
		} else if(manager.sm.odo.getTheta() < (Math.PI/4  - 0.2)) {
			manager.hm.drive.setSpeeds(0, -ROTATION_SPEED);
		} else {
			Sound.buzz();
			manager.hm.drive.setSpeeds(0, ROTATION_SPEED);
			lineLocalization = true;
			rightLineCount = 0;
			leftLineCount = 4;					
		}
	
	}
	
	
	
	/**
	 * Adjusts the localization position for the starting corner
	 */
	public void adjustForStartingCorner() {
		double x1 = manager.sm.odo.getX();
		double y1 = manager.sm.odo.getY();
		double deltaTheta = manager.sm.odo.getTheta();
		
		if(Settings.startingCorner == StartingCorner.BOTTOM_RIGHT) {
			deltaTheta += Math.PI/2;
			double x2 = -y1;
			y1 = x1;
			x1 *= x2;
			x1 += (Settings.FIELD_X - 2)*Settings.TILE_SIZE; 
			//return 3.0 * Math.PI / 4.0;
			
		} else if (Settings.startingCorner == StartingCorner.TOP_RIGHT) {
			deltaTheta += Math.PI;
			x1 *= -1;
			y1 *= -1;
			x1 += (Settings.FIELD_X - 2)*Settings.TILE_SIZE; 
			y1 += (Settings.FIELD_Y - 2)*Settings.TILE_SIZE; 
			//return 5.0 * Math.PI / 4.0;
		} else if (Settings.startingCorner == StartingCorner.TOP_LEFT) {
			deltaTheta -= Math.PI/2;
			double y2 = -x1;
			x1 *= y1;
			y1 += y2 + (Settings.FIELD_Y - 2)*Settings.TILE_SIZE; 
			//return 7.0 * Math.PI / 4.0;
		}
		
		manager.sm.odo.setPosition(new Position(x1, y1, deltaTheta));
		
	}

	
	public int updateUltrasonic() {
		return manager.hm.ultrasonicPoller.getUSReading(1);
	}
	
	/**
	 * Updates the lineDetectedHeadings[] based on whether a new line has been detected
	 * @param rightSensor -> true if the right sensor is to be checked, false if the left sensor is to be checked
	 */
	public void checkLineSensor(boolean rightSensor) {
		if(manager.hm.linePoller.enteringLine((rightSensor) ? 1 : 0)) {
			if(rightSensor && rightLineCount < 4) {
				Sound.beep();
				lineDetectedHeadings[rightLineCount] = manager.sm.odo.getTheta();
				rightLineCount++;
			} else if (leftLineCount < 8) {
				lineDetectedHeadings[leftLineCount] = manager.sm.odo.getTheta();
				leftLineCount++;
				Sound.beep();
			}
		}
	}	
}
