package manager;

import services.*;

public class ServiceManager {

	public Manager manager;
	public Odometer odo;
	public Navigation nav;
	public OdometryCorrection odoCorrection;
	public ObstacleAvoidance obstacleAvoidance;
	public Mapper mapper;
	public Localization localization;
	public HardwareViewer hwView;
	
	public ServiceManager(Manager manager) {
		this.manager = manager;
		this.odo = new Odometer(manager);
		this.nav = new Navigation(manager);
		this.odoCorrection = new OdometryCorrection(manager);
		this.obstacleAvoidance = new ObstacleAvoidance(manager);
		this.mapper = new Mapper(manager);
		this.localization = new Localization(manager);
		this.hwView = new HardwareViewer(manager);
	}
}
