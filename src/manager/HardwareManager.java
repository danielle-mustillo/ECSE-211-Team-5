package manager;

import lejos.nxt.comm.RConsole;
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
	public NXTRemoteUltrasonicPoller ultrasonicPoller;
	
	public HardwareManager(Manager manager) {
		this.manager = manager;
		
		this.drive = new Drive();
	
		this.forklift = new Forklift();
	
		this.claw = new Claw();
	
		this.ultrasonicMotor = new UltrasonicMotor();

		this.colorPoller = new ColorPoller();

		this.linePoller = new LinePoller();

		this.ultrasonicPoller = new NXTRemoteUltrasonicPoller(manager.um.command, 4);

	}
	
	public void reset() {
		manager.cm.setState(State.PAUSE);
		drive.stop();		
		Claw.grabObject();
		Forklift.setHeight(Forklift.ForkliftState.SCAN_HEIGHT);
		UltrasonicMotor.setForwardPosition();
	}
}
