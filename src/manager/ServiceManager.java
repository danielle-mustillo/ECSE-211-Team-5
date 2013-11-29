package manager;

import services.*;

/**
 * Stores access to all the services of the robot. Allows for convenient
 * referencing in other classes
 */
public class ServiceManager {

	/**
	 * Link to other robot functions
	 */
	public Manager manager;
	/**
	 * Odometer service
	 */
	public Odometer odo;
	/**
	 * Navigation service
	 */
	public Navigation nav;
	/**
	 * Odometry Correction service
	 */
	public OdometryCorrection odoCorrection;
	/**
	 * Obstacle Avoidance Service
	 */
	public ObstacleAvoidance obstacleAvoidance;
	/**
	 * Mapper Service
	 */
	public Mapper mapper;
	/**
	 * Localization service
	 */
	public Localization localization;
	/**
	 * Control the output on the LCD screen, useful for debugging purposes.
	 */
	public LCDInfo lcdInfo;

	/**
	 * Initializes the services of the robot (All class objects). This will
	 * start odometer and lcdInfo by default.
	 * 
	 * @param manager
	 */
	public ServiceManager(Manager manager) {
		this.manager = manager;
		this.odo = new Odometer(manager);
		this.nav = new Navigation(manager);
		this.odoCorrection = new OdometryCorrection(manager);
		this.obstacleAvoidance = new ObstacleAvoidance(manager);
		this.mapper = new Mapper(manager);
		this.localization = new Localization(manager);
		this.lcdInfo = new LCDInfo(manager);
		// this.hwView = new HardwareViewer(manager);
	}

	/**
	 * Starts navigation service
	 */
	public void start() {
		nav.start();
		// hwView.start(75);
	}
}
