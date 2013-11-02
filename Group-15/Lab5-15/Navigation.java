import lejos.nxt.comm.RConsole;


/**
 * 
 * @project Lab 4 Localization 
 * @names Riley van Ryswyk & Aditya Saha
 * @studentID 260447357 & 260453165
 * @group 15
 * @course ECSE 211 
 * @date 06/22/13
 * 
 * Navigation 
 * 
 */
public class Navigation {

	private Odometer odometer;
	private TwoWheeledRobot robot;

	// Update every 50ms
	private static final long UPDATE_PERIOD = 50;

	// Max speeds in cm/sec, deg/sec
	private final int MAX_FORWARD_SPEED = 8;
	private final int MAX_ROTATE_SPEED = 40;

	// navigation variables
	private static boolean navigating = false;
	private static boolean turning = false;
	private boolean paused = false;

	public Navigation(Odometer odo) {
		this.odometer = odo;
		this.robot = odo.getTwoWheeledRobot();
	}


	/**
	 * Method to travel to a set of Cartesian coordinates
	 * @param x
	 * @param y
	 */
	public void travelTo(double x, double y) {

		// we're navigating
		navigating = true;

		long correctionStart, correctionEnd;

		// main loop
		travelLoop: while (navigating) {
			correctionStart = System.currentTimeMillis();

			if(!paused) {

				/*
				 * retrieve current position and calculate dX and dY
				 */
				double[] pos = new double[3];

				odometer.getPosition(pos);

				double currentX = pos[0];
				double currentY = pos[1];
				double currentTheta = pos[2];

				// Distance between where we are and where we need to travel to
				double dX = x - currentX;
				double dY = y - currentY;

				/*
				 * if within 0.5cm (x&y) of the destination x,y stop otherwise
				 * continuing traveling
				 */
				if (Math.abs(dX) < 1 && Math.abs(dY) < 1) {
					navigating = false;
					robot.stop();
					break travelLoop;
				}
				/*
				 * We are not at our destination and the path is clear
				 */
				else {

					// the required theta to travel to our destination
					double theta = Math.atan2(dY, dX) * 180.0 / Math.PI;
					// adjust to [0, 360]
					theta = (theta < 0) ? 360 + theta : theta;

					// error in theta
					double deltaTheta = theta - currentTheta;

					// Change deltaTheta to [0,360)
					if (deltaTheta > 180) {
						deltaTheta -= 360;
					} else if (deltaTheta < -180) {
						deltaTheta += 360;
					}

					// see if we need to make a big turn
					if (Math.abs(deltaTheta) > 5 ) {
						// if we need to turn more than 0.2 rads or 0.1 for
						// completing a turn, call the turnTo method
						// otherwise we can adjust small angle errors by slowing one
						// wheel down slightly
						turnTo(theta);

					}
					// We need to go straight (or relatively straight)
					else {

						/*
						 * For minor angle corrections
						 */


						// amount to change right wheel speed by
						int dL = 0;


						// If angle error is greater than 3 deg, make adjustment
						if (Math.abs(deltaTheta) > 0.5) {

							// if we are facing to the left of where we should be,
							// slight right
							if (deltaTheta > 0) {

								dL = -5;
							}

							// if we are facing to the right of where we should be,
							// slight left
							else if (deltaTheta < 0) {

								dL = 5;
							}
						}

						/*
						 * End minor angle corrections
						 */

						// Distance to destination
						double distanceToTravel = (dX * Math.cos(Math.toRadians(currentTheta)))	+ (dY * Math.sin(Math.toRadians(currentTheta)));
						// high speed
						if (distanceToTravel > 3) {
							robot.setSpeeds(MAX_FORWARD_SPEED, dL);
						}
						// start to slow down
						else if (distanceToTravel > 1) {
							robot.setSpeeds(MAX_FORWARD_SPEED / 2, dL);
						}
						// go really slow, so that we don't overshoot
						else {
							robot.setSpeeds(MAX_FORWARD_SPEED / 5, dL / 3);
						}

					}  
				}  
			} 
			//paused
			else {
				robot.setSpeeds(0, 0);
			}

			// this ensure the odometry correction occurs only once every
			// period


			correctionEnd = System.currentTimeMillis();

			if (correctionEnd - correctionStart < UPDATE_PERIOD) {
				try {
					Thread.sleep(UPDATE_PERIOD
							- (correctionEnd - correctionStart));
				} catch (InterruptedException e) {
					// there is nothing to be done here because it is not
					// expected that the odometry correction will be
					// interrupted by another thread
				}
			}
		}
	}


	/**
	 * Orientents the robot to the specified angle
	 * @param angle
	 */
	public void turnTo(double angle) {
	
		// we are turning
		if (!turning)
			turning = true;
	
		long correctionStart, correctionEnd;
	
		// main loop
		while (turning) {
			correctionStart = System.currentTimeMillis();
	
			if(!paused) {
	
				// error in angle
				double deltaTheta = angle - odometer.getTheta();
	
				// convert to [-180,180] for minimal angle
				if (deltaTheta > 180) {
					deltaTheta -= 360;
				} else if (deltaTheta < -180) {
					deltaTheta += 360;
				}
	
				// if angle error greater than 1 deg
				if (Math.abs(deltaTheta) >= 1) {
					// if error positive and greater than 5 deg -> max speed CCW
					if (deltaTheta > 8) {
						robot.setSpeeds(0, -MAX_ROTATE_SPEED);
					}
					// positive error, but close to 0, so turn slow CCW to prevent
					// overshoot
					else if (deltaTheta > 0) {
						robot.setSpeeds(0, -MAX_ROTATE_SPEED / 4);
					}
					// error negative and less than 5deg -> max speed CW
					else if (deltaTheta < -8) {
						robot.setSpeeds(0, MAX_ROTATE_SPEED);
					}
					// negative error, but close to 0, so turn slow CW to prevent
					// overshoot
					else if (deltaTheta < 0) {
						robot.setSpeeds(0, MAX_ROTATE_SPEED / 4);
					}
				} else {
					// we have finished turning
					turning = false;
					robot.stop();
				}
			} 
			//paused
			else {
				robot.setSpeeds(0, 0);
			}
			// this ensure the odometry correction occurs only once every period
			correctionEnd = System.currentTimeMillis();
	
			if (correctionEnd - correctionStart < UPDATE_PERIOD) {
				try {
					Thread.sleep(UPDATE_PERIOD
							- (correctionEnd - correctionStart));
				} catch (InterruptedException e) {
					// there is nothing to be done here because it is not
					// expected that the odometry correction will be
					// interrupted by another thread
				}
			}
		}
	
	}

	/**
	 * backups the robot up along the current heading by a specific distance
	 * @param distance
	 */
	public void backup(int distance) {
		// we're navigating
		navigating = true;
	
		long correctionStart, correctionEnd;
	
	
		double[] pos = new double[3];
	
		odometer.getPosition(pos);
	
		double startX = pos[0];
		double startY = pos[1];
	
		// main loop
		travelLoop: while (navigating) {
			correctionStart = System.currentTimeMillis();
	
			if(!paused) {
				
				/*
				 * retrieve current position and calculate dX and dY
				 */
				
				odometer.getPosition(pos);
	
				double currentX = pos[0];
				double currentY = pos[1];
	
	
				// Distance between where we are and where we need to travel to
				double dX = currentX - startX;
				double dY = currentY - startY;
	
				/*
				 * if greater than the desired distance stop
				 */
				if (Math.pow(dX, 2) + Math.pow(dY, 2) > Math.pow(distance, 2)) {
					navigating = false;
					robot.stop();
					break travelLoop;
				}
				/*
				 * We are not at our destination so continue backing up
				 */
				else {
					robot.setSpeeds(-MAX_FORWARD_SPEED/2, 0);
				}
			} 
			//pause traveling
			else {
				robot.setSpeeds(0, 0);
			}
			// this ensure the odometry correction occurs only once every
			// period
			correctionEnd = System.currentTimeMillis();
	
			if (correctionEnd - correctionStart < UPDATE_PERIOD) {
				try {
					Thread.sleep(UPDATE_PERIOD
							- (correctionEnd - correctionStart));
				} catch (InterruptedException e) {
					// there is nothing to be done here because it is not
					// expected that the odometry correction will be
					// interrupted by another thread
				}
			}
		}
	}
	
	
	/**
	 * Returns true if the robot is navigating or turning
	 * @return
	 */
	public static boolean isNavigating() {
		return navigating;
	}
	
	/**
	 * stop navigating
	 * causes the travelloop to stop in each of the traveling methods
	 */
	public void stop() {
		navigating = false;
		turning = false;
	}
	
	/*
	 * pauses traveling
	 */
	public void pause() {
		paused = true;
	}
	
	//resumes traveling
	public void resume() {
		paused = false;
	}

}
