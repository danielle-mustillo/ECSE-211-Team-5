import java.util.Arrays;


public class SwitchBoard {
//	private boolean isLocalizing;
//	private boolean isNavigating;
//	private boolean isSampling;
//	private boolean isAvoidingObstacle;
//	private boolean isNavigatingToHome;
//	private boolean isHome;
//	private boolean obstacleDetected;
	
	//TODO adjust locks to be more right, aka need 5 locks.
	private CurrentActivity activity;
	private boolean hasFoundBlueBlock;
	
	private int detectionThreshold;
	
	private int ultrasonicReadings[];
	private double colorSensorReadings[];

	public SwitchBoard() {
		activity = CurrentActivity.LOCALIZING;
		
//		this.isLocalizing=true; //first operation done always
//		this.isNavigating=false;
//		this.isSampling=false;
//		this.isAvoidingObstacle=false;
//		this.isHome=false;
		
		setHasFoundBlueBlock(false);

		this.setDetectionThreshold(30);
		this.setUltrasonicReadings(new int[5]);
		this.setColorSensorReadings(new double[3]);
	}
	
	//synchronized used for concurrency errors.
	
	public synchronized CurrentActivity getActivity() {
		return activity;
	}

	//by default, the robot can only be doing one of 5 things at any given moment
	public synchronized void setActivity(CurrentActivity activity) {
		//commented out because this is irrelevant now.
//		this.isLocalizing=false;
//		this.isNavigating=false;
//		this.isSampling=false;
//		this.isAvoidingObstacle=false;
//		if(activity==CurrentActivity.NAVIGATING) 
//			this.isNavigating=true;
//		else if(activity==CurrentActivity.LOCALIZING)
//			this.isLocalizing=true;
//		else if(activity==CurrentActivity.SAMPLING)
//			this.isSampling=true;
//		else if(activity==CurrentActivity.AVOIDING_OBSTACLE)
//			this.isAvoidingObstacle=true;
//		else if(activity==CurrentActivity.NAVIGATING_TO_HOME);
//			this.isNavigatingToHome=true;
		this.activity = activity;
	}
	
	public synchronized void addUSReading(int reading) {
		ultrasonicReadings[4] = ultrasonicReadings[3];
		ultrasonicReadings[3] = ultrasonicReadings[2];
		ultrasonicReadings[2] = ultrasonicReadings[1];
		ultrasonicReadings[1] = ultrasonicReadings[0];
		ultrasonicReadings[0] = reading;
	}
	
	public synchronized int getUSReading() {
		return ultrasonicReadings[0];
	}
	
	public synchronized double getCSReading() {
		return colorSensorReadings[0];
	}

	public synchronized int[] getUltrasonicReadings() {
		return ultrasonicReadings;
	}

	public synchronized void setUltrasonicReadings(int ultrasonicReadings[]) {
		this.ultrasonicReadings = ultrasonicReadings;
	}
	
	public synchronized void addCSReading(double reading) {
		colorSensorReadings[4] = colorSensorReadings[3];
		colorSensorReadings[3] = colorSensorReadings[2];
		colorSensorReadings[2] = colorSensorReadings[1];
		colorSensorReadings[1] = colorSensorReadings[0];
		colorSensorReadings[0] = reading;
	}
	
	//identify the non-Gaussian distribution data.
	public synchronized int computeUSMedian(int [] readings) {
		int[] newReadings = new int[5];
		for(int i = 0; i < 5; i++) 
			newReadings[i] = readings[i];
		Arrays.sort(newReadings);
		return newReadings[2];
	}
	
	//used to identify Gaussian distribution data.
	public synchronized int computeCSMean(int[] readings) {
		if(readings.length != 5) return -1; //dont compute if length is not right
		return (readings[4]+readings[3]+readings[2]+readings[1]+readings[0])/5;
	}
	

	public synchronized double[] getColorSensorReadings() {
		return colorSensorReadings;
	}

	public synchronized void setColorSensorReadings(double colorSensorReadings[]) {
		this.colorSensorReadings = colorSensorReadings;
	}

	public boolean isHasFoundBlueBlock() {
		return hasFoundBlueBlock;
	}

	public void setHasFoundBlueBlock(boolean hasFoundBlueBlock) {
		this.hasFoundBlueBlock = hasFoundBlueBlock;
	}

	public int getDetectionThreshold() {
		return detectionThreshold;
	}

	public void setDetectionThreshold(int detectionThreshold) {
		this.detectionThreshold = detectionThreshold;
	}

	//TODO to be updated with enums.
//	public synchronized boolean isObstacleDetected() {
//		return obstacleDetected;
//	}
//
//	public synchronized void setObstacleDetected(boolean obstacleDetected) {
//		this.obstacleDetected = obstacleDetected;
//	}
	
	//updater
//	public synchronized void updateObstacleDetected() {
//		if(getUSReading() < detectionThreshold) 
//			setObstacleDetected(true);
//		else
//			setObstacleDetected(false);
//	}

}
