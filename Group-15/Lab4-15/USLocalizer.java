import java.util.ArrayList;
import java.util.List;

import lejos.nxt.Motor;
import lejos.nxt.UltrasonicSensor;
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
 * Ultrasonic Localization
 * Accurate to within ~3degs
 */

public class USLocalizer {
	
	//Types of localization
	public enum LocalizationType { FALLING_EDGE, RISING_EDGE };
	
	//Speed in deg/sec of which to rotate during localization
	public static double ROTATION_SPEED = 30;
	
	//period to check ultrasonic sensor in ms
	public final int UPDATE_PERIOD = 30;
	
	//threshold distance in cm, to determine if we are at a critical angle
	public final int THRESHOLD = 30;
	
	//Objects for interaction with other classes
	private Odometer odo;
	private TwoWheeledRobot robot;
	private UltrasonicSensor us;
	private LocalizationType locType;
	private Navigation nav;
	
	// Store list of previous ultrasonic sensor values for filtering
	private List<Integer> usValues = new ArrayList<Integer>();
	
	/**
	 * Ultrasonic sensor class constructor
	 * @param odo
	 * @param us
	 * @param locType
	 */
	public USLocalizer(Odometer odo, UltrasonicSensor us, LocalizationType locType) {
		this.odo = odo;
		this.robot = odo.getTwoWheeledRobot();
		this.us = us;
		this.locType = locType;
		this.nav = odo.getNavigation();
		
		// switch off the ultrasonic sensor
		us.off();
	}
	
	/**
	 * Carrys out Ultrasonic localization
	 */
	public void doLocalization() {
		
		//to feed to odometer
		double [] pos = new double [3];
		
		// Critical angles (when wall is detected/not detected
		double angleA = -1, angleB = -1;
		
		//amount to adjust the heading by
		double deltaTheta = 0;
		
		//to ensure we run only once an UPDATE_PERIOD
		long correctionStart, correctionEnd;
		
		// to prevent errors when changing directions
		int BDelay = 0; 
		
		//Fill the array of usValues with the current reading of the ultrasonic sensor
		getFilteredData();
		getFilteredData();
		getFilteredData();
		getFilteredData();
		
		/*
		 * Falling edge detected (no wall -> wall)
		 * 
		 * 	rotate the robot until it sees no wall
		 *  keep rotating until the robot sees a wall, then latch the angle
		 *  switch direction and wait until it sees no wall
		 *  keep rotating until the robot sees a wall, then latch the angle
		 */
		if (locType == LocalizationType.FALLING_EDGE) {
			
			//to keep track of whether we were facing a wall or not
			boolean noWall = false;
			
			//Localization loop
			locloop: 
				while(true) {
				
				correctionStart = System.currentTimeMillis();
				
				//set the direction of rotation, once angleA is found it will be >0
				if(angleA < 0) {
					robot.setSpeeds(0, 40);
				} else {
					robot.setSpeeds(0, -40);
					
					//prevent detection of same wall/no wall barrier 
					BDelay++;
				}
				
				//retrieve the value of the filtered Ultrasonic data
				int distance = getFilteredData();
				
				//We were seeing a wall and the distance is greater than the threshold -> we are not seeing a wall
				if(!noWall && distance > THRESHOLD) {
					
					//update noWall
					noWall = true;
				} 
				// we are not seeing a wall, haven't detected angleA and distance less than threshold -> we have found angleA
				else if(noWall && angleA < 0 && distance < THRESHOLD) {
					
					//set angleA
					angleA = odo.getTheta();
					
					//reset noWall
					noWall = false;					
				} 
				//if we are not seeing a wall, and haven't detected angleB, the distance is less than the threshold and the delay since detecting angleA is greater than 50 iterations -> we have found angleB
				else if(noWall && angleB < 0 && distance < THRESHOLD && BDelay > 50) {
					
					//set angleB
					angleB = odo.getTheta();
					
					//Stop robot
					robot.setSpeeds(0, 0);
					
					//break the us localization loop
					break locloop;
				}
				
				// this ensure the odometry correction occurs only once every period
				correctionEnd = System.currentTimeMillis();
				
				if (correctionEnd - correctionStart < UPDATE_PERIOD) {
					try {
						Thread.sleep(UPDATE_PERIOD - (correctionEnd - correctionStart));
					} catch (InterruptedException e) {
						// there is nothing to be done here because it is not
						// expected that the odometry correction will be
						// interrupted by another thread
					}
				}
				
			}
			
			//first part of deltaTheta, the negative average of <A and <B
			deltaTheta = -(angleA + angleB) / 2;
			
			//Depending on what angle is bigger, offset deltaTheta to the correct amount
			if(angleA > angleB) {
				deltaTheta += 226;
			} else {
				deltaTheta += 46;
			}
		} 
		
		/*
		 * Rising Edge Detection
		 * 
		 * The robot should turn until it sees the wall, then look for the
		 * "rising edges:" the points where it no longer sees the wall.
		 * This is very similar to the FALLING_EDGE routine, but the robot
		 * will face toward the wall for most of it.
		 */
		
		else {
			
			// true if we are facing a wall
			boolean wall = false;
			
			//localization loop
			locloop: 
				while(true) {
				
				correctionStart = System.currentTimeMillis();
				
				//Set speed of robot depending on whether we have detected angleA 
				if(angleA < 0) {
					robot.setSpeeds(0, 40);
				} else {
					robot.setSpeeds(0, -40);
					//prevent incorrect detection of angleB (ie right after angleA has been detected)
					BDelay++;
				}
				
				//get filtered Ultrasonic data
				int distance = getFilteredData();
				
				//if no wall and distance less than threshold -> we are seeing a wall
				if(!wall && distance < THRESHOLD) {
					wall = true;
				} 
				//if we were seeing a wall, angleA is not set and distance > threshold -> we have angleA
				else if(wall && angleA < 0 && distance > THRESHOLD) {
					
					//set AngleA
					angleA = odo.getTheta();
					
					//reset wall
					wall = false;					
					
				} 
				//if we were seeing a wall, angleB is not set, distance > threshold, and it has been more than 50 iterations since detecting angleA - > we have angleB
				else if(wall && angleB < 0 && distance > THRESHOLD && BDelay > 50) {
					
					//set angleB
					angleB = odo.getTheta();
					
					//stop robot
					robot.setSpeeds(0, 0);
					
					//break the localization loop
					break locloop;
				}
				
				// this ensure the odometry correction occurs only once every period
				correctionEnd = System.currentTimeMillis();
				
				if (correctionEnd - correctionStart < UPDATE_PERIOD) {
					try {
						Thread.sleep(UPDATE_PERIOD - (correctionEnd - correctionStart));
					} catch (InterruptedException e) {
						// there is nothing to be done here because it is not
						// expected that the odometry correction will be
						// interrupted by another thread
					}
				}
				
			}
			
			//first part of deltaTheta, the negative average of <A and <B
			deltaTheta = -(angleA + angleB) / 2;
			
			//Depending on what angle is bigger, offset deltaTheta to the correct amount
			if(angleB > angleA) {
				deltaTheta += 228;
			} else {
				deltaTheta += 48;
			}
		}
	
		// initalize our position to x=-14, y=-14, theta += deltaTheta | we are roughly in the center of the square and deltaTheta was calculated by rising or falling edge detection
		odo.setPosition(new double [] {-14.0, -14.0, odo.getTheta() + deltaTheta}, new boolean [] {true, true, true});
		
	}
	
	/**
	 * Pings Ultrasonic sensor, waits for return and applys median filtering to the Ultrasonic data
	 * @return
	 */
	private int getFilteredData() {
		int distance;
		
		// do a ping
		us.ping();
		
		// wait for the ping to complete
		try { Thread.sleep(25); } catch (InterruptedException e) {}
		
		// there will be a delay here
		distance = us.getDistance();
		
		//add the current value to array of values
		usValues.add(distance);	
		
		//make sure we have sufficient number of samples
		if(usValues.size() >= 5) {
									
			int result;
			
			//array of sorted values
			List<Integer> usValuesSorted = new ArrayList<Integer>(usValues);
			
			//length of values (should be 5)
			int size = usValuesSorted.size();
			
			//sort the values: lowest to highest
			for(int i=0; i<size; i++) {
				for(int j=i+1; j<size;j++) {
					if(usValuesSorted.get(i) > usValuesSorted.get(j)) {
						int temp = usValuesSorted.get(i);
						usValuesSorted.set(i, usValuesSorted.get(j));
						usValuesSorted.set(j, temp);					
					}
				}
			}
			
			// if odd pick the middle value, else average the two middle values
			if(size % 2 == 1 ) {
				result = usValuesSorted.get(size/2);
			} else {
				result = ( usValuesSorted.get(size/2-1) + usValuesSorted.get(size/2) ) / 2;
			}
			
			//shift values left by 1 for next iteration
			usValues.remove(0);

			//return result
			return result;
		} 
		//not enough data for filtering yet, so return the current value
		else {
			return distance;
		}		
				
	}

}
