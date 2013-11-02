import lejos.util.Timer;
import lejos.util.TimerListener;
/**
 * 
 * @project Lab 4 Localization 
 * @names Riley van Ryswyk & Aditya Saha
 * @studentID 260447357 & 260453165
 * @group 15
 * @course ECSE 211 
 * @date 06/10/2013
 * 
 * Odometer (provided)
 * changed the way theta is defined (now 0 along +x axis and increases CCW)
 */
public class Odometer implements TimerListener {
	public static final int DEFAULT_PERIOD = 25;
	private TwoWheeledRobot robot;
	private Timer odometerTimer;
	private Navigation nav;
	// position data
	private Object lock;
	private double x, y, theta;
	private double [] oldDH, dDH;
	
	/**
	 * Odometer constructor
	 * @param robot
	 * @param period
	 * @param start
	 */
	public Odometer(TwoWheeledRobot robot, int period, boolean start) {
		// initialise variables
		this.robot = robot;
		this.nav = new Navigation(this);
		odometerTimer = new Timer(period, this);
		x = 0.0;
		y = 0.0;
		theta = 90.0;
		oldDH = new double [2];
		dDH = new double [2];
		lock = new Object();
		
		// start the odometer immediately, if necessary
		if (start)
			odometerTimer.start();
	}
	/**
	 * Odometer constructor
	 * @param robot
	 */
	public Odometer(TwoWheeledRobot robot) {
		this(robot, DEFAULT_PERIOD, false);
	}
	/**
	 * Odometer constructor
	 * @param robot
	 * @param start
	 */
	public Odometer(TwoWheeledRobot robot, boolean start) {
		this(robot, DEFAULT_PERIOD, start);
	}
	/**
	 * Odometer Constructor
	 * @param robot
	 * @param period
	 */
	public Odometer(TwoWheeledRobot robot, int period) {
		this(robot, period, false);
	}
	
	/**
	 * Update loop
	 * 
	 * Finds change in motors tacho count and then updates x,y,theta correspondingly 
	 */
	public void timedOut() {
		robot.getDisplacementAndHeading(dDH);
		//change in displacement and heading
		dDH[0] -= oldDH[0];
		dDH[1] -= oldDH[1];
		
		// update the position in a critical region
		synchronized (lock) {
			theta -= dDH[1];
			theta = fixDegAngle(theta);
			
			x += dDH[0] * Math.cos(Math.toRadians(theta));
			y += dDH[0] * Math.sin(Math.toRadians(theta));
		}
		
		//update old displacement and heading
		oldDH[0] += dDH[0];
		oldDH[1] += dDH[1];
	}
	
	/**
	 * returns position
	 * @param pos
	 */
	public void getPosition(double [] pos) {
		synchronized (lock) {
			pos[0] = x;
			pos[1] = y;
			pos[2] = theta;
		}
	}
	
	/**
	 * returns theta
	 * @return
	 */
	public double getTheta() {
		double result;

		synchronized (lock) {
			result = theta;
		}

		return result;
	}
	
	/**
	 * returns TwoWheeledRobot
	 * @return
	 */
	public TwoWheeledRobot getTwoWheeledRobot() {
		return robot;
	}
	
	/**
	 * returns navigation object
	 * @return
	 */
	public Navigation getNavigation() {
		return this.nav;
	}
	
	/**
	 * set the current position
	 * @param pos
	 * @param update
	 */
	public void setPosition(double [] pos, boolean [] update) {
		synchronized (lock) {
			if (update[0]) x = pos[0];
			if (update[1]) y = pos[1];
			if (update[2]) theta = pos[2];
		}
	}
	
	/**
	 * Ensures angle is always in [0,360)
	 * @param angle
	 * @return
	 */
	public static double fixDegAngle(double angle) {		
		if (angle < 0.0)
			angle = 360.0 + (angle % 360.0);
		
		return angle % 360.0;
	}
	/**
	 * returns the minimum angle
	 * @param a
	 * @param b
	 * @return
	 */
	public static double minimumAngleFromTo(double a, double b) {
		double d = fixDegAngle(b - a);
		
		if (d < 180.0)
			return d;
		else
			return d - 360.0;
	}
}
