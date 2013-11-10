package controllers;

/**
 * The robot can only ever have one state at a time. Therefore, it maintains a singleton design pattern. The robot can only execute one "task" at a time. 
 * @author Riley
 *
 */
public enum State {
	SEARCH, RECOGNIZE, COLLECT, DROP_OFF, WALL_FOLLOWER, LOCALIZING, PAUSE
}
