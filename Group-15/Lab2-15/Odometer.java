import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;

/*
 * Odometer.java
 */

public class Odometer extends Thread {
	// robot position
	private double x, y, theta;

	// odometer update period, in ms
	private static final long ODOMETER_PERIOD = 25;

	// lock object for mutual exclusion
	private Object lock;
	
	// robot wheel parameters
	private final double radius = 2.12, base = 14.74;
	
	// robot wheel tacho parameters
	private int lTacho, rTacho;
	
	// motor reassignment
	private final NXTRegulatedMotor leftMotor = Motor.A, rightMotor = Motor.B;
	
	private final double degToDist = (radius * Math.PI) / 180;
	
	// default constructor
	public Odometer() {
		//initalize to x=0, y=0, theta = 90deg
		x = 0.0;
		y = 0.0;
		theta = Math.PI/2;
		lock = new Object();
		//Reset motor tacho counts to zero
		leftMotor.resetTachoCount();
		rightMotor.resetTachoCount();		
	}

	// run method (required for Thread)
	public void run() {
		long updateStart, updateEnd;

		while (true) {
			updateStart = System.currentTimeMillis();

			//save last tacho counts
			int lastLTacho = lTacho, lastRTacho = rTacho;
			
			//update tacho counts
			lTacho = leftMotor.getTachoCount();
			rTacho = rightMotor.getTachoCount();
			
			// calc distance traveled for each wheel since last update
			double dL = (lTacho - lastLTacho) * degToDist;
			double dR = (rTacho - lastRTacho) * degToDist;
			
			//cal dTheta and displacement of the center of the robot
			double dTheta = (dL-dR) / base;
			double displacement = (dR + dL) / 2.0;
			
			
			synchronized (lock) {
				//update theta
				theta += dTheta;
				//if going past 2pi offset theta to angle close to zero
				if(theta > 6.27) {
					theta -= 2*Math.PI;
				}
				//update x and y
				x += displacement * Math.cos(theta);
				y += displacement * Math.sin(theta);
			}

			// this ensures that the odometer only runs once every period
			updateEnd = System.currentTimeMillis();
			if (updateEnd - updateStart < ODOMETER_PERIOD) {
				try {
					Thread.sleep(ODOMETER_PERIOD - (updateEnd - updateStart));
				} catch (InterruptedException e) {
					// there is nothing to be done here because it is not
					// expected that the odometer will be interrupted by
					// another thread
				}
			}
		}
	}

	// accessors
	public void getPosition(double[] position, boolean[] update) {
		// ensure that the values don't change while the odometer is running
		synchronized (lock) {
			if (update[0])
				position[0] = x;
			if (update[1])
				position[1] = y;
			if (update[2])
				position[2] = theta;
		}
	}

	public double getX() {
		double result;

		synchronized (lock) {
			result = x;
		}

		return result;
	}

	public double getY() {
		double result;

		synchronized (lock) {
			result = y;
		}

		return result;
	}

	public double getTheta() {
		double result;

		synchronized (lock) {
			result = theta;
		}

		return result;
	}

	// mutators
	public void setPosition(double[] position, boolean[] update) {
		// ensure that the values don't change while the odometer is running
		synchronized (lock) {
			if (update[0])
				x = position[0];
			if (update[1])
				y = position[1];
			if (update[2])
				theta = position[2];
		}
	}

	public void setX(double x) {
		synchronized (lock) {
			this.x = x;
		}
	}

	public void setY(double y) {
		synchronized (lock) {
			this.y = y;
		}
	}

	public void setTheta(double theta) {
		synchronized (lock) {
			this.theta = theta;
		}
	}
}