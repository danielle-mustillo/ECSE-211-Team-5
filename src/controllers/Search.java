package controllers;

import utilities.Point;
import utilities.Position;
import utilities.Settings;
import manager.Manager;

/**
 * This controller will proactively give the robot different coordinates of
 * interest to search for. It will add points into the navigation service when
 * empty for the moment.
 * <p>
 * A more intelligent design would be to choose points around the green zone.
 * Further improvement would be to choose points that avoid the red-zone when
 * the data has been uploaded via bluetooth.
 * <p>
 * Early versions of this code showed that a very simple search algorithm can be
 * designed by choosing default points around the map.
 */
public class Search extends Controller {

	private Manager manager;
	private int defaultPath;
	public boolean skip;

	public Search(Manager manager) {
		this.defaultPath = 0;
		this.manager = manager;
		this.skip = false;
	}

	public void run() {
		defaultRouter();
		// The following code was left here as it was an attempt to get the
		// mapping service to work. However, we did not have time to complete
		// that part.
		/*
		 * if(UltrasonicMotor.isForward) { UltrasonicMotor.setDefaultPosition();
		 * manager.hm.ultrasonicPoller.pingAll();
		 * manager.hm.ultrasonicPoller.resetUSP();
		 * while(!manager.hm.ultrasonicPoller.isSetup()) { manager.um.nap(200);
		 * } }
		 */
		// Point pointOfInterest = new Point();

		// if(manager.sm.mapper.update(pointOfInterest)) {
		// manager.sm.nav.addToRoute(pointOfInterest);
		// }

	}

	/**
	 * This method will check the route in navigation and add a coordinate in
	 * the route. Allows the Navigation to always have "something to do" when
	 * nothing interesting is found
	 */
	private void defaultRouter() {
		if (manager.sm.nav.getRoute().empty()) {
			/*
			 * Here would have some intelligent design. Previous revisions of
			 * the code part showed that some intelligent design to go around
			 * the green-zone only. However this did not prove successful.
			 * Therefore it was commented away and removed heavily.
			 */
			Point nextPoint = new Point();

			// Choose the best set of coordinates based on the starting corner.
			switch (Settings.startingCorner) {
			case BOTTOM_LEFT:
				nextPoint = bottomLeftCorner();
				break;
			case BOTTOM_RIGHT:
				nextPoint = bottomRightCorner();
				break;
			case TOP_LEFT:
				nextPoint = topLeftCorner();
				break;
			case TOP_RIGHT:
				nextPoint = topRightCorner();
				break;
			}
			this.defaultPath += 1;
			this.defaultPath %= 8;

			/*
			 * If the next point is in the red zone, then dont go there.
			 */
			if (Settings.redZone.withinProximityOfTile(new Position(
					nextPoint.x, nextPoint.y, 0), 30)[0] == 'n') {
				manager.sm.nav.addToRoute(nextPoint);
			} else {
				manager.sm.nav.addToRoute(new Point(nextPoint.x,
						Settings.redZoneCoords[1].y + 15));
				manager.sm.nav.addToRoute(new Point(
						Settings.redZoneCoords[1].x + 15,
						Settings.redZoneCoords[1].y + 15));
				manager.sm.nav.addToRoute(new Point(
						Settings.redZoneCoords[1].x + 15,
						Settings.redZoneCoords[0].y - 15));

			}
		}
	}

	public Point bottomLeftCorner() {
		switch (this.defaultPath) {
		case 0: 
			return new Point(45, 45);

		case 1: 
			return new Point(105, 45);

		case 2: 
			return new Point(105, 105);

		case 3: 
			return new Point(45, 105);

		case 4: 
			return new Point(45, 165);

		case 5: 
			return new Point(105, 165);

		case 6:
			return new Point(105, 225);

		case 7: 
			return new Point(165, 225);
		}

		return new Point();
	}

	public Point topRightCorner() {
		switch (this.defaultPath) {
		case 7:
			return new Point(285, 45);

		case 6:
			return new Point(225, 45);

		case 5:
			return new Point(225, 105);
		case 4:
			return new Point(285, 105);

		case 3:
			return new Point(285, 165);

		case 2:
			return new Point(225, 165);

		case 1: 
			return new Point(225, 225);

		case 0:
			return new Point(165, 225);
		}

		return new Point();
	}

	public Point topLeftCorner() {
		switch (this.defaultPath) {
		case 7:
			return new Point(45, 45);

		case 6:
			return new Point(105, 45);

		case 5:
			return new Point(105, 105);

		case 4:
			return new Point(45, 105);

		case 3:
			return new Point(45, 165);

		case 2:
			return new Point(105, 165);

		case 1:
			return new Point(105, 225);

		case 0:
			return new Point(165, 225);
		}

		return new Point();
	}

	public Point bottomRightCorner() {
		switch (this.defaultPath) {
		case 0:
			return new Point(285, 45);

		case 1:
			return new Point(225, 45);

		case 2:
			return new Point(225, 105);
		case 3:
			return new Point(285, 105);

		case 4:
			return new Point(285, 165);

		case 5:
			return new Point(225, 165);

		case 6:
			return new Point(225, 225);

		case 7:
			return new Point(165, 225);
		}

		return new Point();
	}
}
