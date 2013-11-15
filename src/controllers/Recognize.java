package controllers;

import hardwareAbstraction.Forklift;
import manager.Manager;

public class Recognize extends Controller {

private Manager manager;
private boolean isSetup;
	
	public Recognize(Manager manager) {
		this.manager = manager;
		this.isSetup = false;
	}
	
	public void run() {
		if (!isSetup) {
			// if not at scan height, set it to that
			if(Forklift.atLiftHeight) {
				manager.cm.setState(State.PAUSE);
				Forklift.lowerObject();
				manager.cm.setState(State.RECOGNIZE);
			}
			if (!Forklift.atScanHeight) {
				manager.cm.setState(State.PAUSE);
				Forklift.setScanHeight();
				manager.cm.setState(State.RECOGNIZE);
			}
			//TODO figure out if we still need the colorPoller or not. 
			// start the color poller 
			manager.hm.colorPoller.start();
		}
		//if the color poller has finally collected enough values. 
		if(manager.hm.colorPoller.isSetup()) {
			// TODO we might not even need this code anymore!!!!!!!
		}
		
		
	}
}
