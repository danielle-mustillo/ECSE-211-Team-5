package manager;

import lejos.util.Timer;
import lejos.util.TimerListener;
import controllers.*;


/**
 * 
 * Manages the controllers.  Throughout the life of the program, 
 * the controller corresponding to the current state will be run
 * It is up to each controller to change the state of a controller.
 * 
 * 
 * @author Riley
 *
 */
public class ControllerManager implements TimerListener {

	/**
	 * Access to robots other functions
	 */
	public Manager manager;
	/**
	 * Current controller that should be running
	 */
	private State state; 
	/**
	 * Number of blocks currently within the robot possession. 
	 */
	private int stored;
	/**
	 * Array of controllers, used to loop through them as the state changes 
	 */
	private Controller[] controllers; 
	
	/**
	 * Timer for timer listener
	 */
	private Timer timer;
	
	/**
	 * Update period, default is 50ms
	 */
	private final int UPDATE_PERIOD = 50;
	
	/**
	 * Initializes all controllers and starts the timer
	 * @param manager
	 */
	public ControllerManager(Manager manager) {
		this.manager = manager;
		this.stored = 0;
		this.timer = new Timer(UPDATE_PERIOD, this);
		this.controllers = new Controller[]{new Search(manager), new Recognize(manager), new Collect(manager), new DropOff(manager), new WallFollower(manager)};
		start();
	}
	
	/**
	 * Starts the timer, sets the state to Localizing
	 */
	public void start() {
		state = State.LOCALIZING;
		timer.start();
	}
	
	/**
	 * Runs the appropriate controller based on the current state
	 */
	public void timedOut() {
		
		if(state == State.SEARCH) controllers[0].run();
		else if(state == State.RECOGNIZE) controllers[1].run();
		else if(state == State.COLLECT) controllers[2].run();
		else if(state == State.DROP_OFF) controllers[3].run();
		else if(state == State.WALL_FOLLOWER) controllers[4].run();
		
	}
	
	/**
	 * Stops the controller timer.  No more controllers will be run until start is called.
	 */
	public void stop() {
		timer.stop();
	}

	/**
	 * Gets the current state
	 * @return returns the current controller state
	 */
	public State getState() {
		return state;
	}

	/**
	 * Sets then controller state
	 * @param state new state
	 */
	public void setState(State state) {
		this.state = state;
	}

	/**
	 * Returns the number of blocks currently stored by the robot
	 * @return the number of blocks currently stored by the robot
	 */
	public int getStored() {
		return stored;
	}

	/**
	 * Sets the number of blocks currently stored by the robot
	 * @param stored
	 */
	public void setStored(int stored) {
		this.stored = stored;
	}
	
	
	
}
