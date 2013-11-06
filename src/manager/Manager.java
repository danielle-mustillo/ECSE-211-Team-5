package manager;

public class Manager {
	
	public UtilityManager um;
	public ControllerManager cm;
	public ServiceManager sm;
	public HardwareManager hm;	

	public Manager() {
		this.um = new UtilityManager(this);
		this.cm = new ControllerManager(this);
		this.sm = new ServiceManager(this);
		this.hm = new HardwareManager(this);
	}
}
