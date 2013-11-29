package controllers;

/**
 * The robot can only ever have one state at a time. Therefore, it maintains a
 * singleton design pattern. The robot can only execute one "task" at a time.
 * <p>
 * The SEARCH, RECOGNIZE, COLLECT, DROP_OFF, WALL_FOLLOWER states all correspond
 * to the controllers. The states LOCALIZING and JUST_TRAVEL correspond to
 * services that must be executed alone (Localization and Navigation only). The
 * states PAUSE state is only to stopping the re-execution. The robot
 * effectively does nothing besides background services.
 */
public enum State {
	SEARCH, RECOGNIZE, COLLECT, DROP_OFF, WALL_FOLLOWER, LOCALIZING, PAUSE, JUST_TRAVEL
}
