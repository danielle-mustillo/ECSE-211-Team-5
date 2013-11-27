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
			//Point go = new Point(0,0);
			//double xInterest0 = Settings.greenZoneCoords[0].x;
			//double xInterest1 = Settings.greenZoneCoords[1].x;
			//double yInterest0 = Settings.greenZoneCoords[0].y;
			//double yInterest1 = Settings.greenZoneCoords[0].y;
			Point nextPoint = new Point();
			
			switch(Settings.startingCorner) {
			case BOTTOM_LEFT : //go = new Point(xInterest0 - 30, yInterest0 - 30);
					nextPoint = bottomLeftCorner();
			break;
			case BOTTOM_RIGHT : //go = new Point(xInterest0 - 30, yInterest1 + 30);
					nextPoint = bottomRightCorner();
			break;
			case TOP_LEFT : //go = new Point(xInterest1 + 30, yInterest1 + 30);
					nextPoint = topLeftCorner();
			break;
			//wont run on the crack
			case TOP_RIGHT : //go = new Point(xInterest1 + 30, yInterest0 - 30);
					nextPoint = topRightCorner();
			break;
			}
			this.defaultPath += 1;
			this.defaultPath %= 8;
			if(Settings.redZone.withinProximityOfTile(new Position(nextPoint.x, nextPoint.y, 0), 30)[0] == 'n') {
				manager.sm.nav.addToRoute(nextPoint);
			} else {
				//manager.sm.nav.addToRoute(nextPoint);
				manager.sm.nav.addToRoute(new Point(nextPoint.x, Settings.redZoneCoords[1].y + 15));
				manager.sm.nav.addToRoute(new Point(Settings.redZoneCoords[1].x + 15, Settings.redZoneCoords[1].y + 15));
				manager.sm.nav.addToRoute(new Point(Settings.redZoneCoords[1].x + 15, Settings.redZoneCoords[0].y - 15));
				
				
			}
		}
	}
	
	public Point bottomLeftCorner() {
		switch(this.defaultPath) {
		case 0 : //go = new Point(xInterest0 - 30, yInterest0 - 30);
				return new Point(45, 45);
		
		case 1 : //go = new Point(xInterest0 - 30, yInterest1 + 30);
				return new Point(105, 45);
		
		case 2 : //go = new Point(xInterest1 + 30, yInterest1 + 30);
				return new Point(105, 105);
		
		//wont run on the crack
		case 3 : //go = new Point(xInterest1 + 30, yInterest0 - 30);
				return new Point(45, 105);
		
		case 4 : //manager.sm.nav.addToRoute(new Point(15,135));
				return new Point(45, 165);
		
		case 5 : //manager.sm.nav.addToRoute(new Point(180,180));
				return new Point(105, 165);
		
		case 6 : //manager.sm.nav.addToRoute(new Point(00,180));
				return new Point(105, 225);
		
		case 7 : //manager.sm.nav.addToRoute(new Point(00,00));
				return new Point(165, 225);
		}
		
		return new Point();
	}
	
	public Point topRightCorner() {
		switch(this.defaultPath) {
		case 7 : //go = new Point(xInterest0 - 30, yInterest0 - 30);
				return new Point(285, 45);
		
		case 6 : //go = new Point(xInterest0 - 30, yInterest1 + 30);
				return new Point(225, 45);
		
		case 5 : //go = new Point(xInterest1 + 30, yInterest1 + 30);
				return new Point(225, 105);
		
		//wont run on the crack
		case 4 : //go = new Point(xInterest1 + 30, yInterest0 - 30);
				return new Point(285, 105);
		
		case 3 : //manager.sm.nav.addToRoute(new Point(15,135));
				return new Point(285, 165);
		
		case 2 : //manager.sm.nav.addToRoute(new Point(180,180));
				return new Point(225, 165);
		
		case 1 : //manager.sm.nav.addToRoute(new Point(00,180));
				return new Point(225, 225);
		
		case 0 : //manager.sm.nav.addToRoute(new Point(00,00));
				return new Point(165, 225);
		}
		
		return new Point();
	}
	
	public Point topLeftCorner() {
		switch(this.defaultPath) {
		case 7 : //go = new Point(xInterest0 - 30, yInterest0 - 30);
				return new Point(45, 45);
		
		case 6 : //go = new Point(xInterest0 - 30, yInterest1 + 30);
				return new Point(105, 45);
		
		case 5 : //go = new Point(xInterest1 + 30, yInterest1 + 30);
				return new Point(105, 105);
		
		//wont run on the crack
		case 4 : //go = new Point(xInterest1 + 30, yInterest0 - 30);
				return new Point(45, 105);
		
		case 3 : //manager.sm.nav.addToRoute(new Point(15,135));
				return new Point(45, 165);
		
		case 2 : //manager.sm.nav.addToRoute(new Point(180,180));
				return new Point(105, 165);
		
		case 1 : //manager.sm.nav.addToRoute(new Point(00,180));
				return new Point(105, 225);
		
		case 0 : //manager.sm.nav.addToRoute(new Point(00,00));
				return new Point(165, 225);
		}
		
		return new Point();
	}
	
	public Point bottomRightCorner() {
		switch(this.defaultPath) {
		case 0 : //go = new Point(xInterest0 - 30, yInterest0 - 30);
				return new Point(285, 45);
		
		case 1 : //go = new Point(xInterest0 - 30, yInterest1 + 30);
				return new Point(225, 45);
		
		case 2 : //go = new Point(xInterest1 + 30, yInterest1 + 30);
				return new Point(225, 105);
		
		//wont run on the crack
		case 3 : //go = new Point(xInterest1 + 30, yInterest0 - 30);
				return new Point(285, 105);
		
		case 4 : //manager.sm.nav.addToRoute(new Point(15,135));
				return new Point(285, 165);
		
		case 5 : //manager.sm.nav.addToRoute(new Point(180,180));
				return new Point(225, 165);
		
		case 6 : //manager.sm.nav.addToRoute(new Point(00,180));
				return new Point(225, 225);
		
		case 7 : //manager.sm.nav.addToRoute(new Point(00,00));
				return new Point(165, 225);
		}
		
		return new Point();
	}
}
