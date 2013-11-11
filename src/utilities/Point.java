package utilities;

public class Point  {
	public double x;
	public double y;
	
	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public Point(Point point) {
		this.x = point.x;
		this.y = point.y;
	}
	
	public Point() {
		this.x = Double.NaN;
		this.y = Double.NaN;
	}
	
	public String toString() {
		return "{x:" + x + ", y:" + y + "}";
	}
	
	//return a point object identical to the current point. Not passed by reference. 
	public Point getPoint() {
		return new Point(this.x, this.y);
	}
}
