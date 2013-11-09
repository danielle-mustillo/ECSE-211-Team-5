package manager;

import lejos.nxt.comm.RConsole;

public class Manager {
	
	public UtilityManager um;
	public ControllerManager cm;
	public ServiceManager sm;
	public HardwareManager hm;	

	public Manager() {
		RConsole.println("initializing");
		this.um = new UtilityManager(this);
		RConsole.println("um");
		this.hm = new HardwareManager(this);
		RConsole.println("hm");
		this.sm = new ServiceManager(this);
		RConsole.println("sm");
		this.cm = new ControllerManager(this);
		RConsole.println("ControllerManager");
		
		
	}
}
