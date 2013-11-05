/* Danielle Mustillo, Nathaniel Anthonisen
 * 260533476, 260470621
 * ECSE 211 - Design Principles and Methods
 * October 22, 2013
 */
/*
 * Navigation.java
 */
import lejos.nxt.*;
import lejos.nxt.comm.RConsole;
import lejos.robotics.Color;

public class Navigation extends Thread {

	// the odometer, ultrasonic sensor and switchboard
	private Odometer odo;
	private SwitchBoard switches;
	private ColorSensor cs = new ColorSensor(SensorPort.S3);
	private int detectionRange = 20;
	private boolean avoided;

	public Navigation(Odometer odometer, SwitchBoard switches) {
		// instantiate the class variables.
		this.odo = odometer;
		this.switches = switches;
	}

	public void run() {
		//travel to the other side of the field.
		travelTo(-180, 0);
		//correct Y
		odo.setY(0);
		// completes the current action and then goes to the end point if blue block is found.
		// the location to travel to is slightly off due to odometry errors.
		if(!switches.isHasFoundBlueBlock())
			 travelTo(-170, 50);
		if(!switches.isHasFoundBlueBlock())
			 travelTo(-160, 50);
		if(!switches.isHasFoundBlueBlock())
			 travelTo(-160, 0);
		if(!switches.isHasFoundBlueBlock())
			 travelTo(-140, 0);
		if(!switches.isHasFoundBlueBlock())
			 travelTo(-140, 50);
		if(!switches.isHasFoundBlueBlock())
			 travelTo(-120, 50);
		if(!switches.isHasFoundBlueBlock())
			 travelTo(-120, 0);
		if(switches.isHasFoundBlueBlock())
			travelTo(-160,70);
		System.exit(0);
	}

	/**Navigate to will do the brunt work of navigation. It will go to different points on the map and attempt to find the blue block.
	 * @param x coordinate in cm
	 * @param y coordinate in cm
	 */
	public void travelTo(double x, double y) {
		// navigate until the robot is quite close to its destination
		// necessary because the engines are notoriously imprecise
		while (Math.abs(x - odo.getX()) >= 0.5 || Math.abs(y - odo.getY()) >= 0.5) {
				// if the robot is too close to wall, start sampling the object it sees.
				if (switches.getUSReading() < switches.getDetectionThreshold()) {
					Motors.stop();
					sampleObject();
					nap(500);
					
					// if the detection yields a blue block, then pick it up.
					if (switches.isHasFoundBlueBlock()) {
						RConsole.println("has found blue block");
						nap(500);
						
						// pick it up by driving into the block, funneling it into the containment chamber.
						Motors.straight();
						double xNew = odo.getX();
						double yNew = odo.getY();
						while (computeDistanceTraveled(xNew, yNew) < 10) {
							nap(500);
						}
						RConsole.println("blue block picked up, ready to go home husten!");
					} else {
						// avoid the block or the wall, whatever it sees.
						RConsole.println("Entering stop, reverse and follow the wall");

						// backup away from block.
						double newX = odo.getX();
						double newY = odo.getY();
						Motors.backward();
						while (computeDistanceTraveled(newX, newY) < 10) {
							nap(500);
							RConsole.println("Backing up.");
						}
						Motors.stop();
						
						// if it sees an object, avoid the block and navigate around it.
						if(!avoided) {
							avoidBlock(false);
							avoided = true;
						}
						// dont avoid objects anymore, just back away and turn away from them. Relatively the same thing in the end though.
						else {
							Motors.rotateCounterClockwise();
							// if robot started facing wall,
							double startingAngle = odo.getTheta();
							do {
								nap(50);
							} while (Math.abs(odo.getTheta() - startingAngle + 90) % 360 > 3);
						}
					}
					// if the robot sees nothing but an empty space, then perform navigation as per lab 3.
				} else {
					double theta = odo.getStandardTheta(); // degrees
					double angle = determineAngle(x, y); // the angle the robot
															// should be to get
															// to its
															// destination
					double offset = theta - angle;

					// if the difference is fairly large (5 degrees), correction
					// is needed
					boolean correctionNeeded = Math.abs(offset) > 5;

					// turn if needed, otherwise go straight
					if (correctionNeeded)
						turnTo(angle);
					else {
						Motors.straight();
					}
				}
//			}
			nap(250);
		}

		// stop both motors at destination and take a short nap
		Motors.stop();
		nap(200);

	}

	/**The method to avoid the block-aid. It navigates around it in a fixed fashion, while constantly monitoring key elements along the path (such as other blocks).
	 * We don't want the program getting stuck reading non existent objects (such as the wall) so more than one recursion was not allowed. 
	 * @param recursed tells the method whether it was called by recursion or not. 
	 */
	private void avoidBlock(boolean recursed) {
		// record starting position.
		double[] starting = { odo.getX(), odo.getY(), odo.getTheta() };

		// turns the robot parallel to the block it sees. Takes as input the theta of the robot.
		turnParallelToBlockFirst(starting[2]);

		// calls the avoid block if it read another block and was not called by recursion
		if (switches.getUSReading() < 40 && !recursed) {
			avoidBlock(true);
		} else {
			// otherwise just travel parallel to the current block.
			travelParallelToBlock();
		}

		// if it sees a block, call the recursion if it was not already called.
		if (switches.getUSReading() < this.detectionRange && !recursed) {
			avoidBlock(true);
		}
		// otherwise just travel past the block to clear it on the upward axis.
		else {
			travelPastBlock();
		}

		// if it sees a block, call the recursion if it was not already called.
		if (switches.getUSReading() < this.detectionRange && !recursed) {
			avoidBlock(true);
		}// otherwise turn parallel to the second face of the block.
		else {
			turnParallelToBlockSecond(odo.getTheta());
		}

		// if it sees a block, call the recursion if it was not already called.
		if (switches.getUSReading() < this.detectionRange && !recursed) {
			avoidBlock(true);
		} //otherwise just go straight, detecting blocks along the way. 
		else {
			travelAlongBlock();
			if (switches.getUSReading() < this.detectionRange && !recursed) {
				avoidBlock(true);
			}
			travelAlongBlock();
			if (switches.getUSReading() < this.detectionRange && !recursed) {
				avoidBlock(true);
			}
			travelAlongBlock();
			if (switches.getUSReading() < this.detectionRange && !recursed) {
				avoidBlock(true);
			}
		}
	}

	/**This method tells the robot to travel along the block in parallel. 
	 * It knows it by computing the distance it is from where it started
	 * The distance is fixed in this case as turning the US would be impractical in this case. 
	 */
	private void travelAlongBlock() {
		double xCurr = odo.getX();
		double yCurr = odo.getY();
		Motors.straight();
		while (computeDistanceTraveled(xCurr, yCurr) < 15) {
			nap(50);
		}
	}

	/**This method will begin the navigation around an obstacle. 
	 * It takes as parameter the starting angle to compute where it needs to face in heading.
	 * @param startingAngle the angle of the robot at the time of calling.
	 */
	private void turnParallelToBlockFirst(double startingAngle) {
		// face towards north.
		Motors.rotateClockwise();
		// if robot started facing wall,
		do {
			nap(50);
		} while (Math.abs(odo.getTheta() - startingAngle - 90) % 360 > 3);
	}

	/**This method will begin navigation around the second face of an obstacle. 
	 * It takes as parameter the starting angle to compute the final angle.
	 * @param startingAngle
	 */
	private void turnParallelToBlockSecond(double startingAngle) {
		Motors.rotateCounterClockwise();
		// if robot started facing wall,
		do {
			nap(50);
		} while (Math.abs(odo.getTheta() - startingAngle + 90) % 360 > 3);
	}

	/**Travels the robot along the large face of a wall. The US is turned in this case to sample more correctly.
	 */
	private void travelParallelToBlock() {
		Motor.B.rotate(-90); // rotate to face wall.
		// while you see the block go straight
		Motors.straight();
		while (switches.getUSReading() < 30) {

			RConsole.println("US: " + switches.getUSReading());
			nap(50);
		}
		Motor.B.rotate(90); // rotate to face foward
	}

	/**Travels a fixed distance past the last face of a wall detected. Used to "clear" the object so turning will not cause contact. 
	 */
	private void travelPastBlock() {
		double xCurr = odo.getX();
		double yCurr = odo.getY();
		while (computeDistanceTraveled(xCurr, yCurr) < 15) {
			nap(50);
		}
		Motors.stop();
	}

	/**Computes the distance traveled since the two parameters x and y.
	 * @param x the value we want to measure to from our current position
	 * @param y the value we want to measure to from our current position
	 * @return
	 */
	private double computeDistanceTraveled(double x, double y) {
		double currentX = odo.getX();
		double currentY = odo.getY();
		return Math.sqrt(Math.pow(currentX - x, 2) + Math.pow(currentY - y, 2));
	}

	/**
	 * Determines what the heading (angle) of the robot should be in degrees.
	 * This is in reference to the internal odometer. The basis of this is the
	 * arctan function. This function is "adjusted" to
	 * 
	 * @param x the value we need
	 * @param y the value we need
	 * @return the angle of the robot between (-180,180) degrees.
	 */
	// Tested
	private double determineAngle(double x, double y) {

		// how far the robot is from its destination in (x,y)
		double xOffset = x - odo.getX();
		double yOffset = y - odo.getY();

		// if the x or y component is very close to its destination, it is
		// effectively at the destination
		if (Math.abs(yOffset) < 0.05)
			yOffset = 0;
		if (Math.abs(xOffset) < 0.05)
			xOffset = 0;

		/*
		 * Gives the angle of the robot from its central position. We desire the
		 * angle from the robot's default "forward" position vector. We are
		 * using x/y because tangent is defined as opposite over adjacent X is
		 * perpendicular to this "forward" position vector (opposite). Y is
		 * parallel to the above mentioned vector (adjacent).
		 */
		double angle;
		// default is arctan of opp/adj
		if (yOffset >= 0)
			angle = Math.atan(xOffset / yOffset);
		// if angles above +- 90 degrees, add or subtract PI accordingly.
		else if (xOffset > 0)
			angle = Math.atan(xOffset / yOffset) + Math.PI;
		else
			angle = Math.atan(xOffset / yOffset) - Math.PI;

		angle *= (180 / Math.PI); // convert to degrees
		return angle;
	}

	/**
	 * Turns the wheels to the input angle theta.
	 * 
	 * @param angle
	 */
	public void turnTo(double angle) {
		// determines how much the bot should turn based on its current heading
		double correctionTheta = angle - odo.getTheta();

		/*
		 * Corrects to choose the angles between (-180,180) correction Theta can
		 * not be higher than +- 360 degrees since both angle and theta are
		 * standarized between (-180,180). so correction only needs to be
		 * applied once
		 */
		if (correctionTheta > 180)
			correctionTheta -= 360;
		if (correctionTheta < -180)
			correctionTheta += 360;

		// We do not want the program to be interrupted while turning
		// therefore we use rotate.
		// The next commands convert the angle into proper format
		// and turns

		LCD.drawString("TURNING        ", 0, 5);
		if (correctionTheta < 0)
			Motors.rotateCounterClockwise();
		else
			Motors.rotateClockwise();
	}

	/**The sampleObject method will simply call the sampler method and update the status of the blue block.
	 * Essentially controls the filtering of colour sensor data.
	 * A median filter is used for the Gaussian Error.
	 */
	public void sampleObject() {
		sampler();
		sampler();
		sampler();
		sampler();
		sampler();
		RConsole.println(""+switches.computeCSMean());
		// compute whether the sample is positive or negative for blue block.
		if (switches.computeCSMean() > 1.65)
			switches.setHasFoundBlueBlock(false);
		else
			switches.setHasFoundBlueBlock(true);
	}

	/**Samples the object in quesiton by collecting its relative values using the colour sensor and return the red blue proportion. 
	 * This is what is populates the array of CS readings.
	 */
	private void sampler() {
		double red, blue;
		try {
			Thread.sleep(1);
		} catch (InterruptedException e1) {
		}
		//get the colour reading from the color sensor.
		Color col = cs.getColor();
		red = col.getRed();
		blue = col.getBlue();

		//calculate the proportion of red to blue and put that into the array of readings
		switches.addCSReading((double) red / blue);
	}

	/**Helpful helper method to sleep threads
	 * @param time to sleep
	 */
	public static void nap(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}