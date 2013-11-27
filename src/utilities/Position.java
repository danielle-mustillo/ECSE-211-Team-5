package utilities;

/**
 * Using the {@link Point} class as a base, Position adds an angle theta.
 * 
 * @author Riley
 * @author Danielle
 * 
 */
public class Position extends Point {

	public double theta;

	/**
	 * Initializes the Position as per the passed x, y, theta
	 * 
	 * @param x
	 * @param y
	 * @param theta
	 */
	public Position(double x, double y, double theta) {
		this.x = x;
		this.y = y;
		this.theta = theta;
	}

	/**
	 * Initializes an empty Position object
	 */
	public Position() {
		this.x = Double.NaN;
		this.y = Double.NaN;
		this.theta = Double.NaN;
	}

	/**
	 * Outputs the position variables to a String
	 */
	public String toString() {
		return "{x:" + x + ", y:" + y + ", theta:" + theta + "}";
	}

	/**
	 * This method assumes the robot is at this position presently. It adds a
	 * distance to this position, generating a new {@link Point} object. This is
	 * useful when the robot needs to travel somewhere in a straight line ahead
	 * of it. Formula used for newX position is x + deltaX where deltaX is
	 * distance * cos(theta). A Point object is returned due to logical reasons
	 * (the angle the robot ends up at its destination is not necessarily the
	 * same as the angle it started with).
	 * <p>
	 * 
	 * @param distance
	 *            A distance the robot should travel forward
	 * @return A point with the destination coordinates.
	 */
	public Point addDistanceToPosition(double distance) {
		double posX = this.x + distance * Math.cos(this.theta);
		double posY = this.y + distance * Math.sin(this.theta);
		return new Point(posX, posY);
	}

	/**
	 * This method assumes the robot is at this position presently. It adds a
	 * distance to this position, generating a new {@link Point} object. It also
	 * adds the input angle to that position. This method is useful when the
	 * robot needs to travel somewhere relative to its current position. Formula
	 * used for newX position is x + deltaX where deltaX is distance *
	 * cos(theta). Theta in this case is the sum of its current angle and the
	 * input angle A Point object is returned due to logical reasons (the angle
	 * the robot ends up at its destination is not necessarily the same as the
	 * angle it started with).
	 * <p>
	 * 
	 * @param distance
	 *            A distance the robot should travel
	 * @param angle An angle the robot wants to turn by (relative to its current angle). 
	 * @return A point with the destination coordinates.
	 */
	public Point addDisAndAngleToPosition(double distance, double angle) {
		double posX = this.x + distance
				* Math.cos(Angle.principleAngle(angle + this.theta));
		double posY = this.y + distance
				* Math.sin(Angle.principleAngle(angle + this.theta));
		return new Point(posX, posY);
	}
	
	
	/**
	 * This method assumes the robot is at this position presently. It adds a
	 * distance to this position, generating a new {@link Point} object. It also
	 * adds the input angle to that position. This method is useful when the
	 * robot needs to travel somewhere relative to its current position. Formula
	 * used for newX position is x + deltaX where deltaX is distance *
	 * cos(theta). Theta in this case is the 
	 * input angle. A Point object is returned due to logical reasons (the angle
	 * the robot ends up at its destination is not necessarily the same as the
	 * angle it started with).
	 * <p>
	 * 
	 * @param distance
	 *            A distance the robot should travel
	 * @param angle An angle the robot wants to turn to (exactly). 
	 * @return A point with the destination coordinates.
	 */
	public Point goToDistanceAndAngle(double distance, double angle) {
		double posX = this.x + distance
				* Math.cos(Angle.principleAngle(angle));
		double posY = this.y + distance
				* Math.sin(Angle.principleAngle(angle));
		return new Point(posX, posY);
	}
}
