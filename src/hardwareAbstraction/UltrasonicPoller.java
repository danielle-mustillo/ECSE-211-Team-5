package hardwareAbstraction;

/**
 * This class serves as a higher level abstraction of a ultrasonic sensor. It is
 * wrapped in a poller class which will now handle the returned values and the
 * polling of that sensor. The ultrasonic sensors are assumed on the slave brick. 
 * <p>
 * 
 * @author danielle, Riley
 */
public interface UltrasonicPoller {
	public int pollRate = 100;
	public boolean running = false;
	public int left = 0;
	public int center = 1;
	public int right = 2;

	/**
	 * Starts this instance of the ultrasonic poller Stop must be called to stop
	 * the reading again.
	 */
	public void start();

	/**
	 * Resets the ultrasonic sensor values to the default -1 values. The
	 * ultrasonic sensor will never return negative values during normal
	 * operation. Stops the polling during this operation to avoid overwriting
	 * good data.
	 * @bug throws null pointer exception. Has potentially been fixed. Needs to be tested.
	 */
	public void resetUSP();

	/**
	 * Checks if the ultrasonic sensor has collected atleast 5 values. It does
	 * this by checking for any negative numbers in the readings.
	 */
	public boolean isSetup();

	public void pingLeft();
	
	public void pingRight();
	
	public void pingCenter();
	
	public void pingAll();
	
	public void pingSequential();
	
	public void pingSides();
	
	/**
	 * Stops this instance of the ultrasonic poller Start must be called to
	 * start reading again.
	 */
	public void stop();

	/**
	 * Returns the filtered data for the sensor (median filtering)
	 * 
	 * @param sensor
	 * @return
	 */
	public int getUSReading(int sensor);

	/**
	 * gets the lowest reading in the ultrasonicPoller at that time. Readings
	 * are not taken temporarily as they are not needed;
	 * <p>
	 * 
	 * @return The smallest reading of the last 5 polls.
	 */
	public int getLowestReading();
	
	/**
	 * This method gets the number representation of the US with the lowest value out of all the ultrasonic sensor. 
	 * So if the center has the lowest reading, 
	 */
	public USPosition getLowestSensor();
	
	public enum USPosition {
		LEFT, CENTER, RIGHT
	}
}
