package hardwareAbstraction;

import java.io.IOException;

import lejos.nxt.comm.RConsole;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.RegulatedMotorListener;

/**
 * 
 * Class for handling the remote motor abstraction layer on the master brick.
 * {@link RemoteCommands} interface to send the appropriate command id via
 * {@link NXTRemoteCommand} over RS485.
 * 
 * The receiving end implements {@link NXTRegulatedMotor} to carry out the
 * actual commands, once they have been interpreted.
 * 
 * Code Sourced from Lejos Forums
 * (http://www.lejos.org/forum/viewtopic.php?f=7&t=2620)
 * 
 * @author cs07cc4
 * 
 */
public class NXTRemoteMotor implements RemoteCommands, RegulatedMotor {

	/**
	 * Motor id. (1,2,3) -> (a,b,c)
	 */
	private int id;

	private NXTRemoteCommand motorCommand;

	// private ListenerConnection listenerConnection=null;

	/**
	 * Initializes id and motorCommand
	 * 
	 * @param nxtCommand
	 *            {@link NXTRemoteCommand} object required for remote motor
	 *            functionality
	 * @param id
	 *            id of the motor (1,2,3) -> (a,b,c)
	 */
	public NXTRemoteMotor(NXTRemoteCommand nxtCommand, int id) {

		this.id = id;
		this.motorCommand = nxtCommand;
		// this.listenerConnection=listenersConnection;
	}

	/**
	 * Stops regulation
	 * 
	 * @return returns success of action
	 */
	public boolean suspendRegulation() {
		boolean suspended = false;
		motorCommand.send(id, SUSPEND_REGULATION);
		try {
			suspended = motorCommand.getBool();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return suspended;
	}

	/**
	 * Floats the motor. No electromagnetic resistance.
	 */
	@Override
	public void flt() {
		RConsole.println("flt");
		motorCommand.send(id, FLT);

	}

	/**
	 * Gets the current roatation speed of the motor
	 * 
	 * @return returns the current rotation speed of the motor
	 */
	@Override
	public int getRotationSpeed() {
		int speed = 0;
		RConsole.println("getRotationSpeed");
		motorCommand.send(id, GET_ROTATION_SPEED);
		try {
			speed = motorCommand.getInt();
			RConsole.println("speed=" + speed);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return speed;
	}

	/**
	 * Gets the tacho count of the motor
	 * 
	 * @return returns current tacho count
	 */
	@Override
	public int getTachoCount() {
		RConsole.println("getTachoCount");
		int tachoCount = 0;
		motorCommand.send(id, GET_TACHO_COUNT);
		try {
			tachoCount = motorCommand.getInt();
			RConsole.println("tachoCount=" + tachoCount);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tachoCount;
	}

	/**
	 * Resets the motor's tacho count
	 */
	@Override
	public void resetTachoCount() {
		RConsole.println("resetTachoCount");
		motorCommand.send(id, RESET_TACHO_COUNT);

	}

	/**
	 * Gets the limit angle of the motor (angle relative to starting point)
	 * 
	 * @return returns the limit angle
	 */
	@Override
	public int getLimitAngle() {
		RConsole.println("getLimitAngle");

		int angle = 0;
		motorCommand.send(id, GET_LIMIT_ANGLE);
		try {
			angle = motorCommand.getInt();
			RConsole.println("angle=" + angle);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return angle;
	}

	/**
	 * Gets the maximum speed supported by the motor
	 * 
	 * @return maximum speed supported by the motor
	 */
	@Override
	public float getMaxSpeed() {
		RConsole.println("getMaxSpeed=");
		float speed = 0;
		motorCommand.send(id, GET_MAX_SPEED);
		try {
			speed = motorCommand.getFloat();
			RConsole.println("speed=" + speed);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return speed;
	}

	/**
	 * Gets the current speed of the motor
	 * 
	 * @return returns the current speed of the motor
	 */
	@Override
	public int getSpeed() {
		RConsole.println("getSpeed=");
		int speed = 0;
		motorCommand.send(id, GET_SPEED);
		try {
			speed = motorCommand.getInt();
			RConsole.println("speed=" + speed);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return speed;
	}

	/**
	 * checks if the motor is stalled
	 * 
	 * @return returns true if the motor is stalled, otherwise false.
	 */
	@Override
	public boolean isStalled() {
		RConsole.println("isStalled");
		boolean isStalled = false;
		motorCommand.send(id, IS_STALLED);
		try {
			isStalled = motorCommand.getBool();
			RConsole.println("isStalled=" + isStalled);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return isStalled;

	}

	/**
	 * Tells the motor to rotate a certain angle
	 * 
	 * @param angle
	 *            the angle to rotate
	 */
	@Override
	public void rotate(int angle) {
		RConsole.println("rotate");
		motorCommand.send(id, ROTATE, angle, false);

	}

	/**
	 * Tells the motor to rotate to a certain angle. immediate return not yet
	 * implemented. Returns once the command is sent
	 * 
	 * @param angle
	 *            the angle to rotate
	 * @param immediateReturn
	 *            whether to return immediately or wait until the rotation is
	 *            finished
	 */
	@Override
	public void rotate(int angle, boolean immediateReturn) {
		RConsole.println("rotate");
		motorCommand.send(id, ROTATE, angle, immediateReturn);

	}

	/**
	 * Tells the motor to rotate to an angle with respect to its initial
	 * starting angle.
	 * 
	 * @param angle
	 *            the angle to rotateTo
	 */
	@Override
	public void rotateTo(int angle) {
		RConsole.println("rotateTo");
		motorCommand.send(id, ROTATE_TO, angle, false);
	}

	/**
	 * Tells the motor to rotate to an angle with respect to its initial
	 * starting angle. ImmediateReturn not implemented yet. methods returns once
	 * the command is sent
	 * 
	 * @param angle
	 *            the angle to rotateTo
	 * @param immediateReturn
	 *            if false the method will not return until the rotation is
	 *            complete. otherwise it will return right away
	 */
	@Override
	public void rotateTo(int angle, boolean immediateReturn) {
		motorCommand.send(id, ROTATE_TO, angle, immediateReturn);
	}

	/**
	 * Sets the acceleration of the motor
	 * 
	 * @param accel
	 *            integer value for acceleration
	 */
	@Override
	public void setAcceleration(int accel) {

		motorCommand.send(id, SET_ACCELERATION, accel);

	}

	/**
	 * Sets the speed of the motor
	 * 
	 * @param speed
	 *            speed to set the motor speed to
	 */
	@Override
	public void setSpeed(int speed) {
		RConsole.println("setSpeed " + speed);
		motorCommand.send(id, SET_SPEED, speed);

	}

	/**
	 * Tells the motor to rotate in the backwards direction
	 */
	@Override
	public void backward() {
		motorCommand.send(id, BACKWARD);

	}

	/**
	 * Tells the motor to rotate in the forwards direction
	 */
	@Override
	public void forward() {

		motorCommand.send(id, FORWARD);

	}

	/**
	 * Checks to see if the motor is moving
	 * 
	 * @return returns true if the motor is moving, false otherwise.
	 */
	@Override
	public boolean isMoving() {
		RConsole.println("isMoving=");

		boolean isMoving = false;
		motorCommand.send(id, IS_MOVING);
		try {
			isMoving = motorCommand.getBool();
			RConsole.println("isMoving=" + String.valueOf(isMoving));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return isMoving;
	}

	/**
	 * Stops the motor from moving
	 */
	@Override
	public void stop() {
		motorCommand.send(id, STOP);

	}

	/**
	 * Does nothing. Just overrides super method
	 */
	@Override
	public void addListener(RegulatedMotorListener arg0) {
		// TODO Auto-generated method stub

	}

	/**
	 * Does nothing. Just overrides super method
	 */
	@Override
	public void flt(boolean arg0) {
		// TODO Auto-generated method stub

	}

	/**
	 * Does nothing. Just overrides super method
	 */
	@Override
	public void stop(boolean arg0) {
		// TODO Auto-generated method stub

	}

	/**
	 * Does nothing. Just overrides super method
	 */
	@Override
	public void waitComplete() {
		// TODO Auto-generated method stub

	}

}
