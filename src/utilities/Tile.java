package utilities;
/**
 * Currently not implemented
 * @author
 *
 */
public class Tile {
	private Point firstCorner;
	private Point secondCorner;
	private double dY;
	private double dX;
	
	public Tile(Point firstCorner, Point secondCorner) {
		super();
		this.firstCorner = firstCorner;
		this.secondCorner = secondCorner;
	}
	public Point getFirstCorner() {
		return firstCorner;
	}
	public void setFirstCorner(Point firstCorner) {
		this.firstCorner = firstCorner;
	}
	public Point getSecondCorner() {
		return secondCorner;
	}
	public void setSecondCorner(Point secondCorner) {
		this.secondCorner = secondCorner;
	}
	private void update_dXdY() {
		this.dX = firstCorner.x - secondCorner.x;
		this.dY = firstCorner.y - secondCorner.y;
	}
	public boolean withinProximityOfTile(Point xy, double distance) {
		return true;
	}
	

}
