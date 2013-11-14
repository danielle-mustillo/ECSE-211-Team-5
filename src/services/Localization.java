package services;

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
		
		//Retrieves center Ultrasonic reading
		int usReading = updateUltrasonic();
		
		//Currently facing a wall, use rising edge detection for both angles
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
		
		LCD.drawString("Initial: ", 0, 4);
		LCD.drawInt(usReading, 10, 4);
		
		angleA = Double.NaN;
		angleB = Double.NaN;
		lineDetectedHeadings[3] = Double.NaN;
		
		timer.start();
		
	}
	
	public void stop() {	
		timer.stop();
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
//		else if(Double.isNaN(lineDetectedHeadings[3])) {
//			//move to correct orientation for line localization
//			if(!lineLocalization) {
//				prepareLineLocalization();
//			} else {
//				lineLocalization();
//			}
//		} 
//		//localization complete, update position
//		else {
//			updatePosition();
//			stop();
//		}
		//test stub :)
		else {
			if(Math.abs(manager.sm.odo.getTheta()) > 0.1 )
				manager.hm.drive.setSpeeds(0, ROTATION_SPEED);
			else {
				manager.hm.drive.setSpeeds(0,0);
				stop();
			}
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
					manager.hm.drive.setSpeeds(0, ROTATION_SPEED);
				}
			} else {
				manager.hm.drive.setSpeeds(0, ROTATION_SPEED);
				if(distance < THRESHOLD) {
					Sound.beep();
					manager.hm.drive.stop();
					angleA = manager.sm.odo.getTheta();
				}
			}
		} else {
			manager.hm.drive.setSpeeds(0, ROTATION_SPEED);
			if(distance > THRESHOLD && Math.abs(angleA-manager.sm.odo.getTheta()) > 1) {
				Sound.beep();
				angleB = manager.sm.odo.getTheta();
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
				deltaTheta +=  5.0 * Math.PI / 4.0;
			} else {
				deltaTheta +=  Math.PI / 4.0;
			}
		}
		
		/*
		 * Adjust for the starting corner
		 */
		if(Settings.startingCorner == StartingCorner.BOTTOM_RIGHT) {
			deltaTheta -= Math.PI/2;
		} else if (Settings.startingCorner == StartingCorner.TOP_RIGHT) {
			deltaTheta -= Math.PI;
		} else if (Settings.startingCorner == StartingCorner.TOP_LEFT) {
			deltaTheta += Math.PI/2;
		}
		
		RConsole.println(""+manager.sm.odo.getTheta());
		
		//update the odometer
		manager.sm.odo.adjustPosition(0, 0, deltaTheta);
		
		LCD.drawString("X: ", 0, 0);
		LCD.drawString("Y: ", 0, 1);
		LCD.drawString("H: ", 0, 2);
		LCD.drawInt(0, 3, 0);
		LCD.drawInt(0, 3, 1);
		LCD.drawInt((int) manager.sm.odo.getTheta(), 3, 2);
		
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
		double thetaXminus = (lineDetectedHeadings[0] + lineDetectedHeadings[4]) / 2.0;
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

		Position pos = manager.sm.odo.getPosition();
		
		LCD.drawString("X: ", 0, 0);
		LCD.drawString("Y: ", 0, 1);
		LCD.drawString("H: ", 0, 2);
		LCD.drawInt((int) pos.x, 3, 0);
		LCD.drawInt((int) pos.y, 3, 1);
		LCD.drawInt((int) pos.theta, 3, 2);
		
		
		manager.cm.setState(State.SEARCH);
	}
	
	public void prepareLineLocalization() {
		if(manager.sm.odo.getTheta() > lineLocalizationStartingOrientation() + 0.2 ) {
			manager.hm.drive.setSpeeds(0, ROTATION_SPEED);
		} else if(manager.sm.odo.getTheta() < lineLocalizationStartingOrientation() - 0.2 ) {
			manager.hm.drive.setSpeeds(0, -ROTATION_SPEED);
		} else {
			manager.hm.drive.setSpeeds(0, ROTATION_SPEED);
			lineLocalization = true;
			rightLineCount = 0;
			leftLineCount = 4;					
		}
	}
	
	/**
	 * returns the desired starting angle for line localization
	 */
	public double lineLocalizationStartingOrientation() {
		if(Settings.startingCorner == StartingCorner.BOTTOM_RIGHT) {
			return 3.0 * Math.PI / 4.0;
		} else if (Settings.startingCorner == StartingCorner.TOP_RIGHT) {
			return 5.0 * Math.PI / 4.0;
		} else if (Settings.startingCorner == StartingCorner.TOP_LEFT) {
			return 7.0 * Math.PI / 4.0;
		} else {
			return Math.PI / 4.0;
		}
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
				lineDetectedHeadings[rightLineCount] = manager.sm.odo.getTheta();
				rightLineCount++;
			} else if (leftLineCount < 8) {
				lineDetectedHeadings[leftLineCount] = manager.sm.odo.getTheta();
				leftLineCount++;
			}
		}
	}
	
}
