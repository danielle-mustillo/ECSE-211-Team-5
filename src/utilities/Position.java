package utilities;

public class Position extends Point {

	public double theta;

	public Position(double x, double y, double theta) {
		this.x = x;
		this.y = y;
		this.theta = theta;
	}

	public Position() {
		this.x = Double.NaN;
		this.y = Double.NaN;
		this.theta = Double.NaN;
	}

	public String toString() {
		return "{x:" + x + ", y:" + y + ", theta:" + theta + "}";
	}

	/**
	 * This method assumes the robot is at this position presently. It adds a distance to
	 * the position, generating a new {@link Point} object. This is useful when the robot needs to
	 * travel somewhere in a straight line ahead of it. Formula used for newX
	 * position is x + deltaX where deltaX is distance * cos(theta). Theta is
	 * discarded due to logical reasons (the angle the robot ends up at its
	 * destination is not necessarily the same as the angle it started with.
	 * <p>
	 * @param distance
	 *            A distance the robot should travel forward
	 * @return A point with the destination coordinates.
	 */
	public Point addDistanceToPosition(double distance) {
		double posX = this.x + distance * Math.cos(this.theta);
		double posY = this.y + distance * Math.sin(this.theta);
		return new Point(posX, posY);
	}
}
