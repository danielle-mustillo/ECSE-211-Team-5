package utilities;

import java.util.List;

import lejos.nxt.Sound;
/**
 * Currently not implemented
 * @author
 *
 */
public class Tile {
	private Point[] leftFace;
	private Point[] topFace;
	private Point[] rightFace;
	private Point[] bottomFace;
	private Point firstCorner;
	private Point secondCorner;
	
	public Tile(Point first, Point second) {
		this.firstCorner = first;
		this.secondCorner = second;
		
		this.leftFace = new Point[8];
		this.topFace = new Point[8];
		this.rightFace = new Point[8];
		this.bottomFace = new Point[8];
		
		for(int j = 0; j < 8; j++)
			bottomFace[j] = new Point(first.x + Math.abs(first.x - second.x) * j / 8, first.y);
		for(int j = 0; j < 8; j++)
			rightFace[j] = new Point(second.x, first.y + Math.abs(first.y - second.y) * j / 8);
		for(int j = 0; j < 8; j++)
			topFace[j] = new Point(second.x - Math.abs(first.x - second.x) * j / 8, second.y );
		for(int j = 0; j < 8; j++)
			leftFace[j] = new Point(first.x, second.y - Math.abs(first.y - second.y) * j / 8);
	}



	public char[] withinProximityOfTile(Position pos, double distance) {
		int resolution = 6;
		for(int i = 0; i < resolution; ++i) {
			for(Point point : bottomFace) {
				Point position = new Point(pos.x, pos.y);
				double dis = point.distance(position);
				if(dis < distance){
					char[] ch = new char[2];
					ch[0] = 'y';
					ch[1] = 'b';
					return ch;
				}
			}
			for(Point point : topFace) {
				Point position = new Point(pos.x, pos.y);
				double dis = point.distance(position);
				if(dis < distance){
					char[] ch = new char[2];
					ch[0] = 'y';
					ch[1] = 't';
					return ch;
				}
			}
			for(Point point : leftFace) {
				Point position = new Point(pos.x, pos.y);
				double dis = point.distance(position);
				if(dis < distance){
					char[] ch = new char[2];
					ch[0] = 'y';
					ch[1] = 'l';
					return ch;
				}
			}
			
			for(Point point : rightFace) {
				Point position = new Point(pos.x, pos.y);
				double dis = point.distance(position);
				if(dis < distance){
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
	
	private double[] computeSize() {
		double dX = Math.abs( firstCorner.x - secondCorner.x );
		double dY = Math.abs( firstCorner.y - secondCorner.y );
		double[] output = {dX, dY};
		return output;
	}
	
	public Point[] computeAvoidanceTrajectory(Position pos, char[] ch, int distance) {
		double[] size = computeSize();
		
		if(ch[0] == 'n')
			return null;
		else {
			double length = 0;
			double angle = 0;
			Point first = new Point(0,0);
			switch(ch[1]) {
			case 'l' : 
				length = size[1];
				angle = Math.PI / 2;
				first = pos.goToDistanceAndAngle(distance, Math.PI);
				break;
			case 'r' : 
				length = size[1];
				angle = Math.PI / 2;
				first = pos.goToDistanceAndAngle(distance, 0);
				break;
			case 't' : 
				length = size[0];
				angle = 0;
				first = pos.goToDistanceAndAngle(distance, Math.PI / 2);
				break;
			case 'b' :
				length = size[0];
				angle = 0;
				first = pos.goToDistanceAndAngle(distance, 3 * Math.PI / 2);
				break;
			}
			
			Point second = pos.goToDistanceAndAngle(length + 3 * distance, angle);
			Point[] points = new Point[2];
			points[1] = first;
			points[0] = second;
			return points;
		}
	}
	
	private double absoluteMin(double num, double num2) {
		if(num < 0 && num2 < 0)
			return Math.max(num, num2);
		else 
			return Math.min(num, num2);
	}
	
	public enum Edge {
		TOP, BOTTOM, LEFT, RIGHT
	}

}
