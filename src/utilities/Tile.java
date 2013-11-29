package utilities;

import java.util.List;

import lejos.nxt.Sound;

/**
 * This class provides the functionality of a tile, specific for different zones
 * of interest to the robot. In this case, the only functionality of tile is to
 * document the red and green zones of the robot. It contains the two beamed
 * over coordinates stored as {@link Point} objects (as specified by the
 * bluetooth connection) and an array of points that define the edges of the
 * tile.
 */
public class Tile {
	// The array of points along the tile edges.
	private Point[] leftFace;
	private Point[] topFace;
	private Point[] rightFace;
	private Point[] bottomFace;

	// The two coordinates.
	private Point firstCorner;
	private Point secondCorner;

	/**
	 * Initializes the edges based on the two coordinates.
	 * 
	 * @param first
	 *            The first point.
	 * @param second
	 *            The second point.
	 */
	public Tile(Point first, Point second) {
		this.firstCorner = first;
		this.secondCorner = second;

		this.leftFace = new Point[8];
		this.topFace = new Point[8];
		this.rightFace = new Point[8];
		this.bottomFace = new Point[8];

		for (int j = 0; j < 8; j++)
			bottomFace[j] = new Point(first.x + Math.abs(first.x - second.x)
					* j / 8, first.y);
		for (int j = 0; j < 8; j++)
			rightFace[j] = new Point(second.x, first.y
					+ Math.abs(first.y - second.y) * j / 8);
		for (int j = 0; j < 8; j++)
			topFace[j] = new Point(second.x - Math.abs(first.x - second.x) * j
					/ 8, second.y);
		for (int j = 0; j < 8; j++)
			leftFace[j] = new Point(first.x, second.y
					- Math.abs(first.y - second.y) * j / 8);
	}

	/**
	 * Calculates if the robot (or otherwise another {@link Position} object) is
	 * within a distance of the tile.
	 * <p>
	 * The return of this object is a character array with the data that will be
	 * processed elsewhere.
	 * 
	 * @param pos
	 *            The position object to compare against.
	 * @param distance
	 *            The distance to calculate against.
	 * @return A character array containing the results. The first element in
	 *         the character array contains whether the robot is within the
	 *         distance of the tile or not ('y' and 'n' respectively). The
	 *         second element in the character array contains which edge based
	 *         on the first letter of its name (Left = 'l', Right = 'r', Top =
	 *         'u', Bottom = 'b').
	 */
	public char[] withinProximityOfTile(Position pos, double distance) {
		int resolution = 6;
		for (int i = 0; i < resolution; ++i) {
			for (Point point : bottomFace) {
				Point position = new Point(pos.x, pos.y);
				double dis = point.distance(position);
				if (dis < distance) {
					char[] ch = new char[2];
					ch[0] = 'y';
					ch[1] = 'b';
					return ch;
				}
			}
			for (Point point : topFace) {
				Point position = new Point(pos.x, pos.y);
				double dis = point.distance(position);
				if (dis < distance) {
					char[] ch = new char[2];
					ch[0] = 'y';
					ch[1] = 't';
					return ch;
				}
			}
			for (Point point : leftFace) {
				Point position = new Point(pos.x, pos.y);
				double dis = point.distance(position);
				if (dis < distance) {
					char[] ch = new char[2];
					ch[0] = 'y';
					ch[1] = 'l';
					return ch;
				}
			}

			for (Point point : rightFace) {
				Point position = new Point(pos.x, pos.y);
				double dis = point.distance(position);
				if (dis < distance) {
					char[] ch = new char[2];
					ch[0] = 'y';
					ch[1] = 'r';
					return ch;
				}
			}
		}
		char[] ch = new char[2];
		ch[0] = 'n';
		ch[1] = 'n';
		return ch;
	}

	/**
	 * Computes the size of the array object. The first element is the x element
	 * and the second element returned is the y element.
	 * 
	 * @return The dimensions of the tile.
	 */
	private double[] computeSize() {
		double dX = Math.abs(firstCorner.x - secondCorner.x);
		double dY = Math.abs(firstCorner.y - secondCorner.y);
		double[] output = { dX, dY };
		return output;
	}

	/**
	 * This method computes the avoidance trajectory of the robot based on a
	 * position. It is assumed the {@link Position} object input is the current
	 * position of the robot. However, it could be any object.
	 * 
	 * @param pos
	 *            A Position object to compare against.
	 * @param ch
	 *            The side the tile detected. This greatly influences what path
	 *            is taken.
	 * @param distance
	 *            The distance the robot should remain from the tile (used for
	 *            safety purposes).
	 * @return An array of {@link Point}'s that must be taken by the robot to
	 *         avoid the tile.
	 */
	public Point[] computeAvoidanceTrajectory(Position pos, char[] ch,
			int distance) {
		double[] size = computeSize();

		if (ch[0] == 'n')
			return null;
		else {
			double length = 0;
			double angle = 0;
			Point first = new Point(0, 0);
			switch (ch[1]) {
			case 'l':
				length = size[1];
				angle = Math.PI / 2;
				first = pos.goToDistanceAndAngle(distance, Math.PI);
				break;
			case 'r':
				length = size[1];
				angle = Math.PI / 2;
				first = pos.goToDistanceAndAngle(distance, 0);
				break;
			case 't':
				length = size[0];
				angle = 0;
				first = pos.goToDistanceAndAngle(distance, Math.PI / 2);
				break;
			case 'b':
				length = size[0];
				angle = 0;
				first = pos.goToDistanceAndAngle(distance, 3 * Math.PI / 2);
				break;
			}

			Point second = pos.goToDistanceAndAngle(length + 3 * distance,
					angle);
			Point[] points = new Point[2];
			points[1] = first;
			points[0] = second;
			return points;
		}
	}
}
