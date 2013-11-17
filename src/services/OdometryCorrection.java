package services;

import utilities.Point;
import utilities.Position;
import utilities.Settings;
import lejos.nxt.Sound;
import lejos.nxt.comm.RConsole;
import lejos.util.Timer;
import lejos.util.TimerListener;
import manager.*;

public class OdometryCorrection implements TimerListener  {
	
	public final int UPDATE_PERIOD = 15;
	public final int MAX_CHANGE_DISTANCE = 8;
	public final int MAX_CHANGE_ANGLE = 30;
	
	public Timer timer;
	public Manager manager;
	
	private int left = 0;
	private int right = 1;
	
	private Position lastPos;
	private int lastSensor;
	
	//Sets (0,0) to center of the of the first grid intersection
	private double xOffset = 0, yOffset = 0;
	
	public OdometryCorrection(Manager manager) {
		this.timer = new Timer(UPDATE_PERIOD, this);
		this.lastPos = null;
		lastSensor = -1;
		this.manager = manager;
	}
	
	public void start() {
		timer.start();
	}
	
	public void stop() {
		timer.stop();
	}
	
	
	/**
	 * Based on the which sensor is entered a line and the state of the last detected one, 
	 * the method will call the relevant lineDetected or setup lastPos
	 */
	public void timedOut() {
		if(manager.hm.linePoller.enteringLine(left)) {
			if(this.lastPos == null) {
				this.lastPos = manager.sm.odo.getPosition();
				lastSensor = left;
			} else if(lastSensor == left) {
				lineDetected(lastPos, left);
				lastPos = manager.sm.odo.getPosition();
			} else {
				lineDetected(manager.sm.odo.getPosition(), lastPos, right);
				lastPos = null;
				lastSensor = -1;
			}
		}
		
		if(manager.hm.linePoller.enteringLine(right)) {
			if(this.lastPos == null) {
				this.lastPos = manager.sm.odo.getPosition();
				lastSensor = right;
			} else if(lastSensor == right) {
				lineDetected(lastPos, right);
				lastPos = manager.sm.odo.getPosition();
			} else {
				lineDetected(lastPos, manager.sm.odo.getPosition(), left);
				lastPos = null;
				lastSensor = -1;
			}
		}
		
		
	}
	
	/**
	 * When only one sensor detected a line
	 * @param pos
	 * @param sensor
	 */
	public void lineDetected(Position pos, int sensor) {
		
	}
	
	
	/**
	 * Two sensors detected a line
	 * @param leftPos
	 * @param rightPos
	 */
	public void lineDetected(Position leftPos, Position rightPos, int firstSensor) {
		Sound.beep();
		/*
		 * Ensures that the point are reasonably close to each other
		 */
		if(leftPos.distanceToPoint(rightPos) > 5 && Math.abs(leftPos.theta-rightPos.theta) > 0.1 ) {
			return;
		}
		
		double x = (leftPos.x + rightPos.x) / 2.0;
		double y = (leftPos.y + rightPos.y) / 2.0;
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
			if(Math.abs(x-line) > Settings.LS_WIDTH / 2 + 3) {
				
				/*
				 * getClosestY returns the closest line plus the offset for the light Sensor	
				 */
				line = getClosestY(y, theta);
				
				//if we are within 10cm of the line, update our position
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
			if(Math.abs(y-line) > Settings.LS_WIDTH / 2 + 3) {
			
				/*
				 * We have already detected a x line, so get the closest line
				 */
				line = getClosestX(x, theta);
				
				//if we are within 10cm of the line, update our position
				if(Math.abs(line - x) < MAX_CHANGE_DISTANCE) {
					RConsole.println("dX: " + String.valueOf(line-x));
					dx = line - x;
					
				}
			}
			
		}
		
		
		/* 
		 * Angle Correction
		 */
		
		double dTheta = Math.atan(leftPos.distanceToPoint(rightPos) / Settings.LS_WIDTH);
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
