package manager;

import lejos.nxt.comm.RConsole;

public class Manager {
	
	public UtilityManager um;
	public ControllerManager cm;
	public ServiceManager sm;
	public HardwareManager hm;	

	public Manager() {
		
		this.um = new UtilityManager(this);
		
		this.hm = new HardwareManager(this);
		
		this.cm = new ControllerManager(this);
		
		this.sm = new ServiceManager(this);
		
		
		//Start Services
		this.sm.start();
		this.hm.ultrasonicPoller.start();
		
	}
}
