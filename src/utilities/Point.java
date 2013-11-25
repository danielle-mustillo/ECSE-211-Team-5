package utilities;
/**
 * Helper class that uses a Point object to store a points x and y coordinates. As well it provides convenient helper methods  
 * @author Riley
 *
 */
public class Point  {
	public double x;
	public double y;
	
	/**
	 * Initializes a point to the passed x and y
	 * @param x
	 * @param y
	 */
	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}
	/**
	 * Initializes a point with the same x and y as the point passed
	 * @param point
	 */
	public Point(Point point) {
		this.x = point.x;
		this.y = point.y;
	}
	/**
	 * Initializes a point, with x and y == Double.NaN
	 */
	public Point() {
		this.x = Double.NaN;
		this.y = Double.NaN;
	}
	/**
	 * Outputs the x and y of the point to a string
	 */
	public String toString() {
		return "{x:" + x + ", y:" + y + "}";
	}
	/**
	 * Returns the distance between this point and the point passed in
	 * @param point
	 * @return
	 */
	public double distanceToPoint(Point point) {
		return Math.sqrt(Math.pow(x - point.x, 2) + Math.pow(y - point.y, 2));
	}
	
	/**
	 * Returns a new Point with the same x and y as the point. Not pass by reference
	 * @return
	 */
	public Point getPoint() {
		return new Point(this.x, this.y);
	}
}
