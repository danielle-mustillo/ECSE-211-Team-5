package controllers;

import hardwareAbstraction.UltrasonicMotor;
import utilities.Point;
import utilities.Position;
import utilities.Settings;
import manager.Manager;

public class Search extends Controller  {

	private Manager manager;
	private int defaultPath;
	private final int middle = 1;
	public boolean skip;
	
	public Search(Manager manager) {
		this.defaultPath = 0;
		this.manager = manager;
		this.skip = false;
	}
	
	public void run() {
		defaultRouter();
/*		if(UltrasonicMotor.isForward) {
			UltrasonicMotor.setDefaultPosition();
			manager.hm.ultrasonicPoller.pingAll();
			manager.hm.ultrasonicPoller.resetUSP();
			while(!manager.hm.ultrasonicPoller.isSetup()) {
				manager.um.nap(200);
			}
		}
*/		
//		Point pointOfInterest = new Point();
		
//		if(manager.sm.mapper.update(pointOfInterest)) {
//			manager.sm.nav.addToRoute(pointOfInterest);
//		}
		
	}
	/**
	 * This method will check the route in navigation and add a coordinate in the route. Allows the Navigation to always have "something to do" when nothing interesting is found
	 */
	private void defaultRouter() {
		if(manager.sm.nav.getRoute().empty()) {
			Point go = new Point(0,0);
			double xInterest0 = Settings.greenZoneCoords[0].x;
			double xInterest1 = Settings.greenZoneCoords[1].x;
			double yInterest0 = Settings.greenZoneCoords[0].y;
			double yInterest1 = Settings.greenZoneCoords[0].y;
			switch(this.defaultPath) {
			case 0 : go = new Point(xInterest0 - 30, yInterest0 - 30);
			break;
			case 1 : go = new Point(xInterest0 - 30, yInterest1 + 30);
			break;
			case 2 : go = new Point(xInterest1 + 30, yInterest1 + 30);
			break;
			//wont run on the crack
			case 3 : go = new Point(xInterest1 + 30, yInterest0 - 30); 
			break;
			case 4 : manager.sm.nav.addToRoute(new Point(15,135));
			break;
			case 5 : manager.sm.nav.addToRoute(new Point(180,180));
			break;
			case 6 : manager.sm.nav.addToRoute(new Point(00,180));
			break;
			case 7 : manager.sm.nav.addToRoute(new Point(00,00));
			break;
			}
			this.defaultPath += 1;
			this.defaultPath %= 4;
			if(Settings.redZone.withinProximityOfTile(new Position(go.x, go.y, 0), 30) != null)
				manager.sm.nav.addToRoute(go);
		}
		
	}
}
