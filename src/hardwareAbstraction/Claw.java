package hardwareAbstraction;

import utilities.Settings;

/**
 * Controls the Claw Motor. Uses the claw motor defined in {@link Settings}.
 * This class keeps track of its current state so grabObject and releaseObject
 * will only move the claw to fixed positions and nowhere else. Prevents
 * over-gripping or opening the claw too wide.
 * <p>
 * Only open/close functions have been implemented.
 * <p>
 * This class will have to be modified to only return the time necessary to
 * actually complete the action. Now it just returns a default time despite
 * whatever its current position.
 */
public class Claw {
	static NXTRemoteMotor claw = Settings.clawMotor;

	/**
	 * This method grabs an object. Returns a default time to sleep (2s)
	 */
	public static int grabObject() {
		claw.setAcceleration(200);
		claw.setSpeed(150);
		claw.rotateTo(90, true);
		return 2000;
	}

	/**
	 * This method releases an object. Returns a default time to sleep (2s)
	 */
	public static int releaseObject() {
		claw.setAcceleration(200);
		claw.setSpeed(150);
		claw.rotateTo(-55, true);
		return 2000;
	}
}
