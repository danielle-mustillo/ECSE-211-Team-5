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

	public ObstacleAvoidance(Manager manager) {
		
	}
}
