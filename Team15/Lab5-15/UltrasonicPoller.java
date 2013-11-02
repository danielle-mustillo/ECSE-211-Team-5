import java.util.ArrayList;
import java.util.List;

import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.comm.RConsole;
import lejos.util.TimerListener;
import lejos.util.Timer;

/**
 * 
 * @project Lab 5 Object Recognition 
 * @names Riley van Ryswyk & Aditya Saha
 * @studentID 260447357 & 260453165
 * @group 15
 * @course ECSE 211 
 * @date 10/22/2013
 * 
 * Class for interaction with ultrasonic sensor
 * 
 */
public class UltrasonicPoller implements TimerListener {

	private UltrasonicSensor us;
	
	//period to Update Ultrasonic sensor in ms
	private final int PERIOD = 35;
	
	// Store list of previous ultrasonic sensor values for filtering
	private List<Integer> usValues = new ArrayList<Integer>();
	
	//Ultrasonic motor
	private NXTRegulatedMotor usMotor;
	
	private Odometer odo;
	
	private Timer usTimer;
	
	// True if we are polling the ultrasonic sensor once a period
	private boolean started;
	
	private Object lock;
	
	/**
	 * UltrasonicPoller constructor
	 * @param us
	 * @param usMotor
	 * @param odo
	 */
	public UltrasonicPoller(UltrasonicSensor us, NXTRegulatedMotor usMotor, Odometer odo) {
		this.us = us;
		this.usTimer = new Timer(PERIOD, this);
		this.started = false;
		this.lock = new Object();
		this.usMotor = usMotor;
		this.odo = odo;
		
		//limit usMotor acceleration
		usMotor.setAcceleration(1000);
		//stop continuous mode of Ultrasonic sensor
		us.off();
		
	}
	
	/**
	 * Called once a period when started
	 * calls pingUS()
	 */
	public void timedOut() {
		pingUS();		
	}
	
	/**
	 * Pings ultrasonic sensor and records the result
	 */
	public void pingUS() {
		int distance;
		
		// do a ping
		us.ping();
		
		// wait for the ping to complete
		try { Thread.sleep(30); } catch (InterruptedException e) {}
		
		// there will be a delay here
		distance = us.getDistance();

		synchronized (lock) {
		
			if(usValues.size() > 4) { 
				//remove the oldest one
				usValues.remove(0);
			}
			
			//add the current value to array of values
			usValues.add(distance);
		}
	}
	
	/**
	 * Starts the timer for the timeOut method
	 */
	public void start() {
		if(!started) {
			usTimer.start(); 
			this.started = true;
		}
	}
	
	/**
	 * stops the timer for the timeOut method
	 */
	public void stop() {
		if(started) {
			usTimer.stop();
			this.started = false;
		}
	}
	
	/**
	 * clears all stored values
	 */
	public void clear() {
		usValues.clear();
	}
	
	/**
	 * takes 5 readings from the ultrasonic sensor
	 * and returns the filtered distance
	 * @return
	 */
	public int scan() {
		stop();
		
		synchronized (lock) {
			usValues.clear();			
		}
		
		pingUS();
		pingUS();
		pingUS();
		pingUS();
		pingUS();
		
		return filterUS();
		
	}
	
	
	/**
	 * Rotates the ultrasonic sensor to face the desired heading 
	 * it will limit the rotation to what is physically possible by the robot design
	 * @param heading
	 */
	public void rotateUS(int heading) {
		
		int robotHeading = (int) odo.getTheta();
		
		//relative to the robots heading to ensure it doesn't go backwards or something like that
		int rotateTo =  robotHeading - heading;
		
		//limits the angle of rotation
		if(rotateTo < -120) {
			rotateTo  = -120;
		} else if(rotateTo > 40) {
			rotateTo = 40;
		}
		
		//rotate the ultrasonic sensor
		usMotor.rotateTo(rotateTo);
		
	}
	
	/**
	 * filters the data store in the usValues array using mode filtering
	 * if the length of usVaules is less than 3, it will return the last reading
	 * @return filtered distance
	 */
	public int filterUS() {
		//make sure we have sufficient number of samples
		if(usValues.size() >= 3) {
			
			//initialize vars
			int result;
			int size;
			List<Integer> usValuesSorted;
			
			//make sure nothing changes while copying array reading usValues
			synchronized (lock) {
				//array of sorted values
				usValuesSorted = new ArrayList<Integer>(usValues);
			}
			
			//length of values (should be 5)
			size = usValuesSorted.size();
			
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

			//return result
			return result;
		} 
		//not enough data for filtering yet, so return the current value
		else {
			int result; 
			synchronized (lock) {
				if (usValues.size() > 0) {
					result = usValues.get(usValues.size()-1);
				} else {
					result = 256;
				}
			}
			
			return result;
		}
	}
	
	
}
