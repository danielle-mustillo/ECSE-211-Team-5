package manager;

import controllers.State;
import hardwareAbstraction.*;

public class HardwareManager {

	public Manager manager;
	public Drive drive;
	public Forklift forklift;
	public Claw claw;
	public UltrasonicMotor ultrasonicMotor;
	public ColorPoller colorPoller;
	public LinePoller linePoller;
	public UltrasonicPoller ultrasonicPoller;	
	
	public HardwareManager(Manager manager) {
		this.manager = manager;
		this.drive = new Drive();
		this.forklift = new Forklift();
		this.claw = new Claw();
		this.ultrasonicMotor = new UltrasonicMotor();
		this.colorPoller = new ColorPoller();
		this.linePoller = new LinePoller();
		this.ultrasonicPoller = new UltrasonicPoller(manager);
	}
	
	public void reset() {
		manager.cm.setState(State.PAUSE);
		drive.stop();		
		Claw.grabObject();
		Forklift.setHeight(Forklift.ForkliftState.SCAN_HEIGHT);
		UltrasonicMotor.setForwardPosition();
	}
}
