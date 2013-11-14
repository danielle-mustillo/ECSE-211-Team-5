package controllers;

import utilities.Point;
import manager.Manager;

public class Search extends Controller  {

	private Manager manager;
	private int defaultPath;
	
	public Search(Manager manager) {
		this.defaultPath = 0;
		this.manager = manager;
	}
	
	public void run() {
		defaultRouter();
	}
	/**
	 * This method will check the route in navigation and add a coordinate in the route. Allows the Navigation to always have "something to do" when nothing interesting is found
	 */
	private void defaultRouter() {
		if(manager.sm.nav.getRoute().empty()) {
			switch(this.defaultPath) {
			case 0 : manager.sm.nav.addToRoute(new Point(180,0));
			break;
			case 1 : manager.sm.nav.addToRoute(new Point(180,60));
			break;
			case 2 : manager.sm.nav.addToRoute(new Point(00,60));
			break;
			//wont run on the crack
			case 3 : manager.sm.nav.addToRoute(new Point(00,120)); 
			break;
			case 4 : manager.sm.nav.addToRoute(new Point(180,120));
			break;
			case 5 : manager.sm.nav.addToRoute(new Point(180,180));
			break;
			case 6 : manager.sm.nav.addToRoute(new Point(00,180));
			break;
			case 7 : manager.sm.nav.addToRoute(new Point(00,00));
			break;
			}
			this.defaultPath += 1;
			this.defaultPath %= 8;
		}
	}
	
}
