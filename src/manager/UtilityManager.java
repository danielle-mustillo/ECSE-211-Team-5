package manager;

import utilities.*;

public class UtilityManager {
	
	public Manager manager;
	public BluetoothTransmission bt;
	public Communicator comLink; 
	public Map map;
	
	
	public UtilityManager(Manager manager) {
		this.manager = manager;
		this.bt = new BluetoothTransmission();
		this.comLink = new Communicator(Settings.NXTSlaveName);
		this.map = new Map();
	}
}
