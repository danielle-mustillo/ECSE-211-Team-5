/* Danielle Mustillo, Nathaniel Anthonisen
 * 260533476, 260470621
 * ECSE 211 - Design Principles and Methods
 * October 08, 2013
 */
/*
 *USLocalizer.java
 */
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.Sound;
import lejos.nxt.UltrasonicSensor;
import lejos.util.Timer;
import lejos.util.TimerListener;

public class USLocalizer implements TimerListener {
	public enum LocalizationType {
		FALLING_EDGE, RISING_EDGE
	};

	public enum StartingPosition {
		FACING_WALL, FACING_OPEN
	};

	//constants
	public static int ROTATION_SPEED = 100;
	public static int STRAIGHT_SPEED = 200;
	private int bandwidth;
	private int threshold;
	
	//initalize the sensors and odometer. 
	private Odometer odo;
	private UltrasonicSensor us;
	
	//enum constants
	private LocalizationType locType;
	private StartingPosition startPos;
	
	//data storage locations
	public int lastDistance;
	public int thisDistance;
	public int[] distances;
	
	
	//initialize the timer to gather US data. 
	private Timer time;
	private NXTRegulatedMotor leftMotor, rightMotor;

	public USLocalizer(Odometer odo, UltrasonicSensor us,
			LocalizationType locType) {
		//initalize the sensors and odometer. 
		this.odo = odo;
		this.us = us;
		
		//initialize the timer to gather US data. 
		this.time = new Timer(60, this);
		
		//constants
		this.locType = locType;
		this.leftMotor = Motor.A;
		this.rightMotor = Motor.C;
		
		this.bandwidth = 2; // allowed bandwidth to overcome to engage the allotment of angle A,B
		this.threshold = 50; // wall reading that is ideal.
		
		// measurements location
		distances = new int[5];

		
		// set up refreshing on us

		// switch off the ultrasonic sensor
		us.off();
	}

	/**Method does localization. Uses the interrupted exception to gather data for the US. 
	 * @throws InterruptedException
	 */
	public void doLocalization() throws InterruptedException {
		//initalize data
		double angleA, angleB;
		
		//initialize the data array and start collecting data. 
		getData(); 
		time.start();
		getFilteredData(); // get the first measurement

		// determine initial orientation.
		if (thisDistance < threshold) {
			startPos = StartingPosition.FACING_WALL;
			Sound.beep();
		} else {
			startPos = StartingPosition.FACING_OPEN;
		}

		if (locType == LocalizationType.FALLING_EDGE) {
			// robot starts, make it face angle B (when distance is around 50)
			if (startPos == StartingPosition.FACING_WALL) { // facing the wall
				nap(5000);
				// get out when the robot no longer sees a wall
				while (thisDistance < threshold + bandwidth
						&& lastDistance <= threshold) {
					rotateClockwise();
				}
				motorsStop();
			} else {
				nap(5000);
				// get out when the robot no longer sees an open area. 
				while (thisDistance > threshold - bandwidth
						&& lastDistance >= threshold) {
					rotateCounterClockwise();
				}
				motorsStop();
			}
			// short nap, then turn towards A.
			Sound.twoBeeps();
			rotateClockwise();
			nap(3000);
			// latch angle when sees the first wall
			while (thisDistance >= threshold - bandwidth
					&& lastDistance >= threshold) {
				rotateClockwise();
			}
			//found angle A, latch the angle. 
			Sound.twoBeeps();
			motorsStop();
			angleA = odo.getTheta();

			//turn towards B.
			rotateCounterClockwise();
			nap(3000);
			
			//latch B when the robot sees the wall again.
			while (thisDistance >= threshold - bandwidth
					&& lastDistance >= threshold) {
				rotateCounterClockwise();
			}
			Sound.twoBeeps();

			//latch the angle B.
			motorsStop();
			angleB = odo.getTheta();

			nap(3000);

			// find the angle to find black line intersection.
			double heading45;
			double newAngleA = angleA, newAngleB = angleB;

			if (angleA > 180)
				newAngleA = angleA - 360;
			if (angleB > 180)
				newAngleB = angleB - 360;

			//set that computed angle into heading45
			heading45 = (newAngleA + newAngleB) / 2;

			//rotate to face the intersection
			while (Math.abs(odo.getTheta() - heading45) > 3 ) {
				rotateClockwise();
			}
			motorsStop();

			time.stop();
			
			//move towards intersection by 10 cm, set angle and rotate to 0.
			odo.setTheta(45);
			motorsStop();

			time.stop();

			goStraight(odo.getX(), odo.getY(), 100);
			
			//rotate to zero.
//			while (Math.abs(odo.getTheta()) > 3) {
//				rotateCounterClockwise();
//			}
		} else {
			/*The following code is virtually identical to taht which is above. The key differences will be commented. All other code remains the same*/
			
			if (startPos == StartingPosition.FACING_WALL) {
				nap(5000);
				// rotate to face B.
				while (thisDistance < threshold + bandwidth
						&& lastDistance <= threshold) {
					rotateClockwise();
				}
				motorsStop();
			} else {
				nap(5000);
				while (thisDistance > threshold - bandwidth
						&& lastDistance >= threshold) {
					rotateCounterClockwise();
				}
				motorsStop();
			}
			// short nap, then turn. Here were turning towards the wall until we dont see the wall again.
			Sound.twoBeeps();
			rotateCounterClockwise();
			nap(3000);

			// latch angle when the robot no longer sees the wall.
			while (thisDistance <= threshold + bandwidth
					&& lastDistance <= threshold) {
				rotateCounterClockwise();
			}
			Sound.twoBeeps();
			motorsStop();
			angleA = odo.getTheta();

			//rotate to face A, facing the wall.
			rotateClockwise();
			nap(3000);

			while (thisDistance <= threshold + bandwidth
					&& lastDistance <= threshold) {
				rotateClockwise();
			}
			Sound.twoBeeps();

			motorsStop();
			angleB = odo.getTheta();

			nap(3000);

			
			double heading45;
			double newAngleA = angleA, newAngleB = angleB;

			if (angleA > 180)
				newAngleA = angleA - 360;
			if (angleB > 180)
				newAngleB = angleB - 360;

			//here the heading computed needs to be added to 180 since the angle computed will be that towards the corner of the wall
			//the 180 turns the robot around.
			heading45 = (newAngleA + newAngleB) / 2 + 180;

			while (Math.abs(odo.getTheta() - heading45) > 2) {
				rotateClockwise();
			}
			odo.setTheta(45);
			motorsStop();

			time.stop();

			goStraight(odo.getX(), odo.getY(), 100);
			
			//rotate to zero.
			while (Math.abs(odo.getTheta()) > 2) {
				rotateCounterClockwise();
			}
		}
	}

	// sleep helper method
	private void nap(int time) {
		// short nap
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	// populate all of the array. Used only once.
	private void getData() {

		// do a ping
		us.ping();

		// wait for the ping to complete
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
		}
		int temp = us.getDistance();
		if (temp > 70)
			temp = 70;

		// populate the array.
		distances[0] = temp;
		distances[1] = distances[0];
		distances[2] = distances[1];
		distances[3] = distances[2];
		distances[4] = distances[3];
		thisDistance = distances[0];
		lastDistance = distances[1];
	}

	/**
	 * Gets the data from the US, with using averages to smooth out the data and
	 * avoid having misreads.
	 * 
	 */
	private void getFilteredData() {
		// do a ping
		us.ping();

		// wait for the ping to complete
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
		}
		// there will be a delay here

		// cap the values read.
		int temp = us.getDistance();
		if (temp > 70)
			temp = 70;

		// store many values
		distances[4] = distances[3];
		distances[3] = distances[2];
		distances[2] = distances[1];
		distances[1] = distances[0];
		distances[0] = temp;

		// use average values
		thisDistance = (distances[4] + distances[3] + distances[2]
				+ distances[1] + distances[0]) / 5;
		lastDistance = (distances[4] + distances[3] + distances[2] + distances[1]) / 4;
	}

	public void goStraight(double x, double y, double distance) {
		while ((Math.pow(odo.getX() - x, 2) + Math.pow(odo.getY() - y, 2) - distance) < 0.05) {
			leftMotor.setSpeed((int) ROTATION_SPEED);
			rightMotor.setSpeed((int) ROTATION_SPEED);
			leftMotor.forward();
			rightMotor.forward();
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		leftMotor.stop();
		rightMotor.stop();
	}

	// move motor methods.
	public void rotateClockwise() {
		leftMotor.setSpeed((int) ROTATION_SPEED);
		rightMotor.setSpeed((int) ROTATION_SPEED);
		leftMotor.forward();
		rightMotor.backward();
	}

	public void rotateCounterClockwise() {
		leftMotor.setSpeed(-(int) ROTATION_SPEED);
		rightMotor.setSpeed((int) ROTATION_SPEED);
		leftMotor.backward();
		rightMotor.forward();
	}

	public void motorsStop() {
		leftMotor.stop();
		rightMotor.stop();
	}

	// collect data every timeout.
	@Override
	public void timedOut() {
		getFilteredData();
	}
}
