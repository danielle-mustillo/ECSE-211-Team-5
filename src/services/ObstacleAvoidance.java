package services;

import utilities.Point;
import controllers.State;
import manager.*;

/**
 * 
 * implement scanAhead(int maxDistance)
 * scans forward with all 3 us sensors (5 pings each for filtering purposes)
 * taking the closest reading:
 * if nothing closer than distance do nothing -> return true
 * if closer than distance but > 20cm ->add an intermediate point in route (at closest reading - safety margin) return true
 * if distance less than 20cm return false, change state to recognize controller
 * 
 * implement scan()
 * uses center ultrasonic to make sure the path ahead is clear (>20cm)
 * 
 * NOTE: 20cm is estimated value, will need to tweak
 * 
 * 
 * @author 
 *
 */
public class ObstacleAvoidance {
	private hardwareAbstraction.UltrasonicPoller poller;
	private int threshold;
	private int safetyThreshold;
	private Manager manager;
	
	public ObstacleAvoidance(Manager manager) {
		this.manager = manager;
		poller = manager.hm.ultrasonicPoller;
		//TODO test these values, find appropriate values for our robot. 
		this.threshold=20;
		this.safetyThreshold=5;
	}

	public boolean scanAhead() {
		// TODO note this may be necessary to avoid having the robot navigate while scanning. 
//		manager.cm.setState(State.PAUSE);
		
		// TODO somehow move all the three ultrasonic sensors forward with slave brick. Waiting for ultrasonicMotor implementation. 
		
		this.resetUSP();
		
		int smallestReading = -1;
		/* Only go forward when the data has propagated through. Only here for fail safe. */ 
		do {
			// let the current heading readings propagate through the poller.
			nap(poller.pollRate * 6);
		
			smallestReading = poller.getLowestReading();
		} while(smallestReading < 0);
		
		if(smallestReading < threshold) {
			manager.cm.setState(State.RECOGNIZE);
			return false;
		}
		else {
			//TODO remove or add this depending if state is changed to pause at start of scanAhead(); 
//			manager.cm.setState(State.SEARCH);
			if(smallestReading > threshold + safetyThreshold)
				return true;
			else {
				//put this point in the route stack as the next place to go. 
				Point pos = manager.sm.odo.getPosition().getPoint();
				//TODO calculate the new position to navigate to... summation of two points. To figure out. 
				manager.sm.nav.addToRoute(pos /* + add a point here*/);
				return true;
			}
		}
	}
	
	/**
	 * Scans with the central ultrasonic sensor. Used to verify if there is anything within this.threshold of robot.
	 * @return	True if nothing is found, otherwise false
	 */
	
	//TODO complete method stub. 
	public boolean scan() {
		this.resetUSP();
		//scan center 
		return true;
	}
	
	/**
	 * Useful method to reset pollers. Ensures only 
	 */
	private void resetUSP() {
		poller.stop();
		poller.start();
	}
	
	
	/** Helper method to avoid large try/catch blocks. Sleeps the current thread. 
	 * @param time	int value which represents the sleep time
	 */
	private void nap(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
