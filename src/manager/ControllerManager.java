package manager;

import lejos.util.Timer;
import lejos.util.TimerListener;
import controllers.*;


/**
 * 
 * Manages the controllers.  Throughout the life of the program, the controller corresponding to the current state will be run
 * It is up to each controller to change the state of a controller.
 * Additionally, the obstacle avoidance service may change the state
 * 
 * @author Riley
 *
 */
public class ControllerManager implements TimerListener {

	public Manager manager;
	private State state; 
	private Controller[] controllers; 
	private Timer timer;
	
	private final int UPDATE_PERIOD = 50;
	
	public ControllerManager(Manager manager) {
		this.manager = manager;
		this.timer = new Timer(UPDATE_PERIOD, this);
		this.controllers = new Controller[]{new Search(manager), new Recognize(manager), new Collect(manager), new DropOff(manager), new WallFollower(manager)};
	}
	
	public void start() {
		state = State.SEARCH;
		timer.start();
	}
	
	public void timedOut() {
		
		if(state == State.SEARCH) controllers[0].run();
		else if(state == State.RECOGNIZE) controllers[1].run();
		else if(state == State.COLLECT) controllers[2].run();
		else if(state == State.DROP_OFF) controllers[3].run();
		else if(state == State.WALL_FOLLOWER) controllers[4].run();
		
	}
	
	public void stop() {
		timer.stop();
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}
	
}
