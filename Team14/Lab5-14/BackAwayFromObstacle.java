
public class BackAwayFromObstacle {
	private Odometer odo;
	private SwitchBoard switches;
	
	public BackAwayFromObstacle(Odometer odo, SwitchBoard switches) { 
		this.odo = odo;
		this.switches = switches;
	}
	
	public void run() {
		double xInitial = odo.getX();
		double yInitial = odo.getY();
		Motors.backward();
		double distance;
		do {
			distance = Math.sqrt(Math.pow(odo.getX() - xInitial, 2) + Math.pow(odo.getY() - yInitial, 2));
			nap(500);
		} while(distance < 20);
		switches.setActivity(CurrentActivity.NAVIGATING);
	}
	
	public void nap(int time) {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
