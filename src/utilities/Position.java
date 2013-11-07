package utilities;

public class Position extends Point {

	public double theta;
	
	public Position (double x, double y, double theta) {
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
}
