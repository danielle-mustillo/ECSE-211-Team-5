package hardwareAbstraction;

/**
 * 
 * Contains a list of integer commands to communicate to the remote motors
 * 
 * Code Sourced from Lejos Forums (http://www.lejos.org/forum/viewtopic.php?f=7&t=2620)
 * 
 * @author cs07cc4
 *
 */

public interface RemoteCommands {
	  public static final int FORWARD = 0;
	  public static final int BACKWARD = 1;
	  public static final int ROTATE = 2;
	  public static final int STOP = 3;
	  public static final int ROTATE_TO = 4;
	  public static final int FLT = 5;
	  public static final int GET_TACHO_COUNT = 6;
	  public static final int IS_MOVING = 7;
	  public static final int SET_SPEED = 8;
	  public static final int SET_ACCELERATION = 9;
	  public static final int GET_ACCELERETION = 10;
	  public static final int GET_LIMIT_ANGLE = 11;
	  public static final int RESET_TACHO_COUNT = 12;
	  public static final int GET_SPEED = 13;
	  public static final int IS_STALLED = 14;
	  public static final int GET_ROTATION_SPEED= 15;
	  public static final int GET_MAX_SPEED= 16;
	  public static final int SUSPEND_REGULATION= 17;
	  public static final int ADD_LISTENER= 18;
	  public static final int ROTATION_STARTED= 19;
	  public static final int ROTATION_STOPPED= 20;
}