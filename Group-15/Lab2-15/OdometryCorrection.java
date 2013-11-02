import java.util.ArrayList;
import java.util.List;
import lejos.nxt.ColorSensor;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;

/* 
 * OdometryCorrection.java
 * 
 * Detects black grid lines on wooden floor, then corrects the position of the odometer
 * based on the position reported when the line was detected
 * 
 */

public class OdometryCorrection extends Thread {
	private static final long CORRECTION_PERIOD = 10;
	private Odometer odometer;
	private static final SensorPort lsPort = SensorPort.S1;
	private ColorSensor lightSensor = new ColorSensor(lsPort);
	private List<Double> lsValues = new ArrayList<Double>();
	private int runFilter = -3;
	private boolean onLine = false;
	
	// distance to next most positive x line, distance to next most positive y line, tile size, light sensor offset
	private double tileSize = 30.48, r = 12.8;
	private double xOffset = -tileSize/2, yOffset = tileSize/2;
	
	
	// constructor
	public OdometryCorrection(Odometer odometer) {
		this.odometer = odometer;
		//reduce volume
		Sound.setVolume(35);
		//set flood light to Red, red gives most accurate results for us
		lightSensor.setFloodlight(0);
		
	}

	// run method (required for Thread)
	public void run() {
		long correctionStart, correctionEnd;

		while (true) {
			correctionStart = System.currentTimeMillis();
			
			//update light sensor return value
			try { 
				lsValues.set(3, (double) lightSensor.getRawLightValue() / 2.0);			
			} catch(IndexOutOfBoundsException e) {
				lsValues.add((double) lightSensor.getRawLightValue() / 2.0);	
			}
			
			
			if(runFilter == 0) {
				filterLightSensor();
				runFilter = -1;				
			} else {
				runFilter++;
			}
			

			// this ensure the odometry correction occurs only once every period
			correctionEnd = System.currentTimeMillis();
			
			if (correctionEnd - correctionStart < CORRECTION_PERIOD) {
				try {
					Thread.sleep(CORRECTION_PERIOD
							- (correctionEnd - correctionStart));
				} catch (InterruptedException e) {
					// there is nothing to be done here because it is not
					// expected that the odometry correction will be
					// interrupted by another thread
				}
			}
		}
	}
	
	public void filterLightSensor() {
		
		double result;
		
		result = -lsValues.get(0) - lsValues.get(1) + lsValues.get(2) + lsValues.get(3);
		
		lsValues.remove(0);
				
		if(!onLine && result < -45) {
			//go to line detection logic
			lineDetected();
			
			// We are on the line
			onLine = true;
			
			//alert us with a beep that a line was detected
			Sound.beep();
			
		} else if(onLine && result > 45) {
			onLine = false;
		}		
	}
	
	/**
	 * retrieves last light sensor reading
	 * @return lastLSValue
	 */
	public String lightSensorValue() {
		return Double.toString(lsValues.get(0));
	}
	
	public void lineDetected() {
		
		//for getting the current x, y, theta
		double[] pos = new double[3];
		boolean[] update = {true, true, true};
		
		odometer.getPosition(pos, update);
		
		double x = pos[0];
		double y= pos[1];
		double theta = pos[2];
		
		/**
		 * sets orientation
		 * ~= 1 if going in Y direction
		 * ~= 0 if going in X direction
		 */
		double orientation = Math.abs(Math.sin(theta));	
		
		/**
		 * Update the Y position as we are going in the Y direction
		 */
		if(orientation > 0.99) {
			
			/**
			 * getClosestY returns the closest line plus the offset for the light Sensor	
			 */
			double line = getClosestY(y, theta);
			
			//if we are within 10cm of the line, update our position
			if(Math.abs(line - y) < 10) {
				
				odometer.setY(line);
			}
			
		}
		/**
		 * Update the X position, going in X direction
		 */
		else if(Math.abs(Math.cos(theta)) > 0.99) {
			
				
			/**
			 * We have already detected a x line, so get the closest line
			 * getClosestX returns the closest line plus the offset for the light Sensor
			 */
			double line = getClosestX(x, theta);
			
			//if we are within 10cm of the line, update our position
			if(Math.abs(line - x) < 10) {
				
				odometer.setX(line);
				
			}
			
		}
		
		/**
		 * Update theta as we are turning
		 */
		else {
			
			/*// for the second last corner, if we have enough seperation between the y straight and y angled line
			if(x<-45 && y<15 && y>(yOffset-r+2) && x > (xOffset-3*tileSize+r)) {
				
				//the yLine detected during turning
				double yPrime =  (Math.round((y+r-yOffset)/tileSize)*tileSize + yOffset);
				
				// new Theta
				double newTheta = 3*Math.PI/2 + Math.acos((yPrime-y)/r);
				
				// if Theta is a real number, udpate
				if(newTheta != Double.NaN) { 
					odometer.setTheta(newTheta);
					
				}
			}*/
		}
	}
	
	/**
	 * Returns the closest y line plus the offset for the light sensor
	 * @param y
	 * @param theta
	 * @return
	 */
	public double getClosestY(double y, double theta) {
		//adjust light sensor offset for direction of robot
		double R = (Math.sin(theta) < 0) ? -1*r : r;
		return (Math.round((y-R-yOffset)/tileSize)*tileSize + yOffset + R);
	}
	
	/**
	 * Returns the closest x line plus the offset for the light sensor
	 * @param x
	 * @param theta
	 * @return
	 */
	public double getClosestX(double x, double theta) {
		//adjust light sensor offset for direction of robot
		double R = (Math.cos(theta) < 0) ? -1*r : r;
		return (Math.round((x-R-xOffset)/tileSize)*tileSize + xOffset + R);
	}
}