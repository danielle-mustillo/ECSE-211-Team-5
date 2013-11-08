package services;

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
	
	
	private boolean lightLocalization;
	private int rightLineCount;
	private int leftLineCount;
	private double[] lineDetectedHeadings = new double[8]; 
	
	public Localization(Manager manager) {
		this.manager = manager;
		this.timer = new Timer(UPDATE_PERIOD, this);
	}
	
	/**
	 * Starts the localization process
	 */
	public void start() {
		//Retrieves center Ultrasonic reading
		int usReading = updateUltrasonic() ;
		
		//Currently facing a wall, use rising edge detection for both angles
		if(usReading < THRESHOLD) {
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
		lineDetectedHeadings[7] = Double.NaN;
		timer.start();
		
	}
	
	public void stop() {
		timer.stop();
	}
	
	public void timedOut() {
		if(angleB == Double.NaN) {
			ultrasonicLocalization();
		} else if(lineDetectedHeadings[7] == Double.NaN) {
			
			if(!lightLocalization) {
				if(manager.sm.odo.getTheta() > 50) {
					manager.hm.drive.setSpeeds(0, ROTATION_SPEED);
				} else if(manager.sm.odo.getTheta() < 40) {
					manager.hm.drive.setSpeeds(0, -ROTATION_SPEED);
				} else {
					manager.hm.drive.setSpeeds(0, ROTATION_SPEED);
					lightLocalization = true;
					rightLineCount = 0;
					leftLineCount = 4;					
				}
			} else {
				lineLocalization();
			}
		} else {
			updatePosition();
			stop();
		}
	}
	
	/**
	 * If the robot starts facing the fall the robot will do rising, rising edge detection (angleA, angleB)
	 * if the robot starts facing the field the robot will do falling edge, rising edge (angleA, angleB) 
	 */
	public void ultrasonicLocalization() {
		int distance = updateUltrasonic();
		
		if(angleA == Double.NaN) {
			if(rising) {
				manager.hm.drive.setSpeeds(0, -ROTATION_SPEED);
				if(distance > THRESHOLD) {
					angleA = manager.sm.odo.getTheta();
					manager.hm.drive.setSpeeds(0, ROTATION_SPEED);
				}
			} else {
				manager.hm.drive.setSpeeds(0, ROTATION_SPEED);
				if(distance < THRESHOLD) {
					manager.hm.drive.stop();
					angleA = manager.sm.odo.getTheta();
				}
			}
		} else {
			manager.hm.drive.setSpeeds(0, ROTATION_SPEED);
			if(distance > THRESHOLD && Math.abs(angleA-manager.sm.odo.getTheta()) > 50) {
				angleB = manager.sm.odo.getTheta();
				updateTheta();
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
				deltaTheta += 225;
			} else {
				deltaTheta += 45;
			}
		} else {
			//Depending on what angle is bigger, offset deltaTheta to the correct amount
			if(angleA > angleB) {
				deltaTheta += 225;
			} else {
				deltaTheta += 45;
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
	
	public void updatePosition() {
		
	}

	
	public int updateUltrasonic() {
		return manager.hm.ultrasonicPoller.getUSReading(1);
	}
	
	/**
	 * Updates the lineDetectedHeadings[] based on whether a new line has been detected
	 * @param rightSensor -> true if the right sensor is to be checked, false if the left sensor is to be checked
	 */
	public void checkLineSensor(boolean rightSensor) {
		if(manager.hm.linePoller.enteringLine(rightSensor)) {
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
