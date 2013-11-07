package services;

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
	
	public ObstacleAvoidance(Manager manager) {
		poller = manager.hm.ultrasonicPoller;
		this.threshold=20;
		this.safetyThreshold=5;
	}
	
	//TODO complete this stub.
	public boolean scanAhead() {
		// TODO somehow move all the three ultrasonic sensors forward with slave brick. Waiting for feedback from RS485 to do this. 
		
		//reset pollers. 
		poller.stop();
		poller.start();
		
		// let the current heading readings propogate through the poller.
		nap(poller.pollRate * 6);
		
		int smallestReading = poller.getLowestReading();
		if(smallestReading < threshold) {
			// TODO set the state
			return false;
		}
		else if(smallestReading > threshold + safetyThreshold)
			return true;
		else {
			//TODO add point in navigation class. 
			return true;
		}
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
