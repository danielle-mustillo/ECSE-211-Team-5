/* Danielle Mustillo, Nathaniel Anthonisen
 * 260533476, 260470621
 * ECSE 211 - Design Principles and Methods
 * October 22, 2013
 */
/*
 * SwitchBoard.java
 */
public class SwitchBoard {
	//readings and storage values.
	private CurrentActivity activity;
	private Object activityLock = new Object();
	
	private boolean hasFoundBlueBlock;
	
	private int detectionThreshold;
	
	private int ultrasonicReadings[];
	private Object ultraSonicLock = new Object();
	
	private double colorSensorReadings[];
	private Object colorLock = new Object();
	
	private double[] odoPositions; 

	public SwitchBoard() {
		activity = CurrentActivity.LOCALIZING;
		
		//set initial state
		this.setHasFoundBlueBlock(false);
		this.setDetectionThreshold(10);
		this.setUltrasonicReadings(new int[5]);
		this.setColorSensorReadings(new double[5]);
		this.setOdoPositions(new double[3]);
		this.setDetectionThreshold(8);
	}
	
	/* All the methods here are syncrhonized in some fashion or another. This is to avoid concurrency errors */
	
	public synchronized CurrentActivity getActivity() {
		synchronized(activityLock) {
			return activity;
		}
	}

	//by default, the robot can only be doing one of 5 things at any given moment
	public synchronized void setActivity(CurrentActivity activity) {
		synchronized(activityLock) {
			this.activity = activity;
		}
	}
	
	//adds one reading at a time.
	public void addUSReading(int reading) {
		synchronized(ultraSonicLock) {
			ultrasonicReadings[4] = ultrasonicReadings[3];
			ultrasonicReadings[3] = ultrasonicReadings[2];
			ultrasonicReadings[2] = ultrasonicReadings[1];
			ultrasonicReadings[1] = ultrasonicReadings[0];
			ultrasonicReadings[0] = reading;
		}
	}
	
	//get the previous meeting values
	public int getUSReading() {
		synchronized(ultraSonicLock) {
			return ultrasonicReadings[0];
		}
	}

	//get the last CS reading.
	public double getCSReading() {
		synchronized(colorLock) {
			return colorSensorReadings[0];
		}
	}

	public synchronized int[] getUltrasonicReadings() {
		synchronized(ultraSonicLock) {
			return ultrasonicReadings;
		}
	}

	public synchronized void setUltrasonicReadings(int ultrasonicReadings[]) {
		synchronized(ultraSonicLock) {
			this.ultrasonicReadings = ultrasonicReadings;
		}
	}
	
	public synchronized void addCSReading(double reading) {
		synchronized(colorLock) {
			colorSensorReadings[4] = colorSensorReadings[3];
			colorSensorReadings[3] = colorSensorReadings[2];
			colorSensorReadings[2] = colorSensorReadings[1];
			colorSensorReadings[1] = colorSensorReadings[0];
			colorSensorReadings[0] = reading;
		}
	}
	
	//used to identify Gaussian distribution data.
	public double computeCSMean() {
		synchronized(colorLock) {
			if(colorSensorReadings.length != 5) return -1; //dont compute if length is not right
			return (colorSensorReadings[4]+colorSensorReadings[3]+colorSensorReadings[2]+colorSensorReadings[1]+colorSensorReadings[0]) / 5;
		}
	}
	

	public synchronized double[] getColorSensorReadings() {
		return colorSensorReadings;
	}

	public synchronized void setColorSensorReadings(double colorSensorReadings[]) {
		this.colorSensorReadings = colorSensorReadings;
	}

	public synchronized boolean isHasFoundBlueBlock() {
		return hasFoundBlueBlock;
	}

	public synchronized void setHasFoundBlueBlock(boolean hasFoundBlueBlock) {
		this.hasFoundBlueBlock = hasFoundBlueBlock;
	}

	public synchronized int getDetectionThreshold() {
		return detectionThreshold;
	}

	public synchronized void setDetectionThreshold(int detectionThreshold) {
		this.detectionThreshold = detectionThreshold;
	}

	public synchronized double[] getOdoPositions() {
		return odoPositions;
	}

	public synchronized void setOdoPositions(double[] odoPositions) {
		this.odoPositions = odoPositions;
	}
}
