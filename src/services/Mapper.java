package services;

import hardwareAbstraction.UltrasonicMotor;
import utilities.Point;
import utilities.Position;
import manager.*;

public class Mapper {

	private final int THRESHOLD = 15;
	private final double ANGLE_THRESHOLD = 0.15;
	private final int WIDTH_THRESHOLD = 10;
	
	private int[] currentValue;
	private int[] previousValue;
	private Position[] start;
	private int[] startValue;
	private boolean[] startSet;
	
	//To match ultrasonic Poller
	private final int left = 0;
	private final int right = 2;
	
	
	
	Manager manager;
	
	public Mapper(Manager manager) {
		this.manager = manager;
		previousValue = new int[3];
		start = new Position[3];
		currentValue = new int[3];
		startSet = new boolean[]{false, false, false};
	}
	
	public boolean update(Point returnPoint) {
		if(!UltrasonicMotor.isForward) {
			currentValue[left] = manager.hm.ultrasonicPoller.getUSReading(left);
			currentValue[right] = manager.hm.ultrasonicPoller.getUSReading(right);
			
			if(!start(left)) {
				if(end(left)) {
					if(calculatePoint(left,returnPoint)) {
						return true;
					}					
				}
			}
			
			if(!start(right)) {
				if(end(right)) {
					if(calculatePoint(right,returnPoint)) {
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	
	private boolean calculatePoint(int sensor, Point returnPoint) {
		Position end = manager.sm.odo.getPosition();
		
		if(Math.abs(end.theta-start[sensor].theta) < ANGLE_THRESHOLD) {
			double dx = end.x - start[sensor].x;
			double dy = end.y - start[sensor].y;
			
			if(Math.abs(dx) > WIDTH_THRESHOLD) {
				returnPoint.x = end.x - dx/2;
				returnPoint.y = end.y;
				
				
				if(Math.cos(end.x) > 0) {
					if(sensor == left) {
						returnPoint.y += calculateDistanceToObject(startValue[sensor], currentValue[sensor]);
					} else {
						returnPoint.y -= calculateDistanceToObject(startValue[sensor], currentValue[sensor]);
					}
				}
				else {
					if(sensor == right) {
						returnPoint.y += calculateDistanceToObject(startValue[sensor], currentValue[sensor]);
					} else {
						returnPoint.y -= calculateDistanceToObject(startValue[sensor], currentValue[sensor]);
					}
				}
				
			} else if(Math.abs(dy) > WIDTH_THRESHOLD) {
				returnPoint.y = end.y - dy/2;
				returnPoint.x = end.x;
				
				if(Math.sin(end.x) > 0) {
					if(sensor == left) {
						returnPoint.x -= calculateDistanceToObject(startValue[sensor], currentValue[sensor]);
					} else {
						returnPoint.x += calculateDistanceToObject(startValue[sensor], currentValue[sensor]);
					}
				}
				else {
					if(sensor == right) {
						returnPoint.x -= calculateDistanceToObject(startValue[sensor], currentValue[sensor]);
					} else {
						returnPoint.x += calculateDistanceToObject(startValue[sensor], currentValue[sensor]);
					}
				}
				
			}
			
			return true;
		} else {
			return false;
		}
		
		
		
	}
	
	private int calculateDistanceToObject(int start, int end) {
		if(start == end) {
			return start;
		} else if( start > end ) {
			return end;
		} else {
			return start;
		}
	}
	
	private boolean start(int sensor) {
		if((currentValue[sensor] - previousValue[sensor] < -THRESHOLD) && !startSet[sensor]) {
			//store the starting point
			start[sensor] = manager.sm.odo.getPosition();
			startSet[sensor] = true;
			startValue[sensor] = currentValue[sensor];
			return true;
		} else {
			return false;
		}
		
	}
	
	private boolean end(int sensor) {
		if((previousValue[sensor] - currentValue[sensor] < -15 || (previousValue[sensor] == 255 && currentValue[sensor] != 255)) && startSet[sensor]) {
			startSet[sensor] = false;
			return true;
		} else {
			return false;
		}
	}
	
	
}
