package services;

import utilities.Position;
import utilities.Settings;
import lejos.nxt.Sound;
import lejos.nxt.comm.RConsole;
import lejos.util.Timer;
import lejos.util.TimerListener;
import manager.*;
/**
 * OdometryCorrection makes use of the {@link LinePoller},
 * each time a new line is detected, it will record the position of the odometer and what sensor detected the line.
 * Then it will check to see if the previous detected line was with the other sensor.  If that is the case then, provided
 * the difference in x, y, theta of the two poistions is relatively small and the robot is roughly in the center of the tile, 
 * it will update x or y and theta of the {@link odometer}.
 * 
 * Note: The current methods used required the robot to travel in the center of the tiles, frequently enough.
 * @author Riley 
 *
 */
public class OdometryCorrection implements TimerListener  {
	/**
	 * Period to update, should be similar to update period of {@link LinePoller}
	 */
	public final int UPDATE_PERIOD = 15;
	/**
	 * Max distance to adjust x or y
	 */
	public final int MAX_CHANGE_DISTANCE = 5;
	/**
	 * Max angle to adjust theta;
	 */
	public final double MAX_CHANGE_ANGLE = 0.2;
	/**
	 * Max distance from the center of the tile to still correct
	 */
	public final int MAX_DISTANCE_FROM_TILE_CENTER = 2;
	/**
	 * Max distance between points to correct (i.e. max distance between left sensor detection position and right senosr detection position)
	 */
	public final int MAX_DISTANCE_BETWEEN_POINTS = 5;
	
	/**
	 * Timer for timer listener
	 */
	public Timer timer;
	/**
	 * {@link Manager} for access to robots other functions 
	 */
	public Manager manager;
	/**
	 * id of the left sensor
	 */
	private int left = 0;
	/**
	 * id of the right sensor
	 */
	private int right = 1;
	
	/**
	 * The odometer position at the detection of the last line
	 */
	private Position lastPos;
	/**
	 * the id of the last sensor to detect a line
	 */
	private int lastSensor;
	
	/**
	 * For offseting the grid lines (i.e. to move 0,0 away from the first grid intersection )
	 */
	private double xOffset = 0, yOffset = 0;
	
	/**
	 * Initializes timer
	 * @param manager
	 */
	public OdometryCorrection(Manager manager) {
		this.timer = new Timer(UPDATE_PERIOD, this);
		this.lastPos = null;
		lastSensor = -1;
		this.manager = manager;
	}
	
	/**
	 * Starts odometry Correction
	 */
	public void start() {
		timer.start();
	}
	
	/**
	 * Stops odometry Correction
	 */
	public void stop() {
		timer.stop();
	}
	
	/**
	 * Based on which sensor has entered a line and the state of the last detected one, 
	 * the method will call the relevant lineDetected and/or setup lastPos
	 */
	public void timedOut() {
		//Line detected by left sensor
		if(manager.hm.linePoller.enteringLine(left)) {
			if(this.lastPos == null) {
				this.lastPos = manager.sm.odo.getPosition();
				lastSensor = left;
			} else if(lastSensor == left) {
				lineDetected(lastPos, left);
				lastPos = manager.sm.odo.getPosition();
			} else {
				lineDetected(manager.sm.odo.getPosition(), lastPos, right);
				lastPos = manager.sm.odo.getPosition();
				lastSensor = left;
			}
		}
		//Line detected by right sensor
		if(manager.hm.linePoller.enteringLine(right)) {
			if(this.lastPos == null) {
				this.lastPos = manager.sm.odo.getPosition();
				lastSensor = right;
			} else if(lastSensor == right) {
				lineDetected(lastPos, right);
				lastPos = manager.sm.odo.getPosition();
			} else {
				lineDetected(lastPos, manager.sm.odo.getPosition(), left);
				lastPos = manager.sm.odo.getPosition();
				lastSensor = right;
			}
		}
		
		
	}
	
	/**
	 * When only one sensor detected a line. Currently not implemented. 
	 * @param pos
	 * @param sensor
	 */
	public void lineDetected(Position pos, int sensor) {
		
	}
	
	
	/**
	 * Both sensors detected a line, decides whether the points are consider value according to thresholds, and then how to correct the robots position
	 * @param leftPos
	 * @param rightPos
	 */
	public void lineDetected(Position leftPos, Position rightPos, int firstSensor) {
		Sound.beep();
		/*
		 * Ensures that the point are reasonably close to each other
		 */
		if(leftPos.distanceToPoint(rightPos) > MAX_DISTANCE_BETWEEN_POINTS && Math.abs(leftPos.theta-rightPos.theta) > 0.1 ) {
			return;
		}
		
		//Average the x and y of the line detected, as the robot should detect both at the same time if it is going perpendicular to the grid line
		double x = (leftPos.x + rightPos.x) / 2.0;
		double y = (leftPos.y + rightPos.y) / 2.0;
		//Average the heading 
		double theta = (leftPos.theta + rightPos.theta) / 2.0;
		
		double dx = 0;
		double dy = 0;
		
		
		/*
		 * Update the Y position as we are going in the Y direction
		 */
		if(Math.abs(Math.sin(theta)) > 0.98) {
			
			/*
			 * Check if we are on an x line
			 */
			double line = getClosestX(x, theta);
			
			//make sure the sensor is not close to a line parallel to direction of travel
			if(Math.abs(x-line) > Settings.LS_WIDTH / 2 + MAX_DISTANCE_FROM_TILE_CENTER) {
				
				/*
				 * getClosestY returns the closest line plus the offset for the light Sensor	
				 */
				line = getClosestY(y, theta);
				
				//if we are within MAX_CHANGE_DISTANCE of the line, set dy
				if(Math.abs(line - y) < MAX_CHANGE_DISTANCE) {
					RConsole.println("dY: " + String.valueOf(line-y));
					dy = line - y;
					
				}
			}
			
		}
		/*
		 * Update the X position, going in X direction
		 */
		else if(Math.abs(Math.cos(theta)) > 0.98) {
		
			/*
			 * Check if we are on an x line
			 */
			double line = getClosestY(y, theta);
			
			// make sure the sensor is not close to a line parallel with direction of travel
			if(Math.abs(y-line) > Settings.LS_WIDTH / 2 + MAX_DISTANCE_FROM_TILE_CENTER) {
			
				/*
				 * We have already detected a x line, so get the closest line
				 */
				line = getClosestX(x, theta);
				
				//if we are within MAX_CHANGE_DISTANCE of the line, update dx
				if(Math.abs(line - x) < MAX_CHANGE_DISTANCE) {
					RConsole.println("dX: " + String.valueOf(line-x));
					dx = line - x;
					
				}
			}
			
		}
		
		
		/* 
		 * Angle Correction
		 */
		//correction angle is arctan (displacement/width)
		double dTheta = Math.atan(leftPos.distanceToPoint(rightPos) / Settings.LS_WIDTH);
		//adjust sign
		if(firstSensor == left) {
			dTheta *= -1;
		}
		
		//makes sure that its only a good angle correction
		if(Math.abs(dTheta) < MAX_CHANGE_ANGLE && (Math.abs(dx) > 0.01 || Math.abs(dy) > 0.01)) {
			RConsole.println("dTheta: " + String.valueOf(dTheta));
			manager.sm.odo.adjustPosition(dx, dy, dTheta);
		}
		
		RConsole.println("Odo: " + manager.sm.odo.getPosition().toString());
		RConsole.println("right: " + rightPos.toString());
		RConsole.println("left: " + leftPos.toString() + "\n");
		
	}
	

			
		
	/**
	 * Returns the closest y line plus the offset for the light sensor
	 * @param y
	 * @param theta
	 * @return
	 */
	public double getClosestY(double y, double theta) {
		//adjust light sensor offset for direction of robot
		double R = Math.sin(theta)*Settings.LS_LENGTH;
		return (Math.round((y-R-yOffset)/Settings.TILE_SIZE)*Settings.TILE_SIZE + yOffset + R);
	}
	
	/**
	 * Returns the closest x line plus the offset for the light sensor
	 * @param x
	 * @param theta
	 * @return
	 */
	public double getClosestX(double x, double theta) {
		//adjust light sensor offset for direction of robot
		double R = Math.cos(theta)*Settings.LS_LENGTH;
		return (Math.round((x-R-xOffset)/Settings.TILE_SIZE)*Settings.TILE_SIZE + xOffset + R);
	}
	
}
