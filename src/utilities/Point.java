package utilities;

public class Point  {
	public double x;
	public double y;
	
	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public Point() {
		this.x = Double.NaN;
		this.y = Double.NaN;
	}
	
	public String toString() {
		return "{x:" + x + ", y:" + y + "}";
	}
}
