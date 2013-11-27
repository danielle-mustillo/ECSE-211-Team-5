package manager;

import lejos.nxt.comm.RConsole;

/**
 * Initializes all other manager classes.  
 * And provides an instance of itself to each sub manager. 
 * This way it is easy to access most robot functions from any class that has the manager passed to it.
 * @author Riley
 *
 */
public class Manager {
	
	public UtilityManager um;
	public ControllerManager cm;
	public ServiceManager sm;
	public HardwareManager hm;	

	/**
	 * Initializes the managers.  Calls {@link ServiceManager.start();} amd {@link HardwareManager.ultrasonicPoller.start()};
	 */
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
