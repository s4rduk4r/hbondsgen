/**
 * Line in 2D cartesian space
 */
package hbondsgen.math;

public class Line2d {
	/*
	 * Properties
	 */
	public Double k = null;
	public Double b = null;
	
	/*
	 * Interface
	 */
	//Check if lines are equal
	public boolean isEqual(Line2d right) {
		return k == right.k && b == right.b;
	}
	
	//Check if lines are collinear
	public boolean isCollinear(Line2d right) {
		return k == right.k;
	}
	
	//Check if point belong to this line
	public boolean isBelong(Point2d p) {
		return p.y == p.x * k + b;
	}
	
	//Get intersection point of two lines
	public Point2d intersection(Line2d right) {
		/*
		 * (x0, y0) - intersection point
		 * x0 = (b2 - b1) / (k1 - k2); y0 = (b2 - b1) / (k1 - k2) * k1 + b1;
		 */
		Double x0 = (right.b - b) / (k - right.k);
		return new Point2d(x0, x0 * k + b);
	}
	
	/*
	 * Constructor
	 */
	//Initialization constructor
	public Line2d(Double k, Double b) {
		this.k = k;
		this.b = b;
	}
	
	public Line2d(Point2d p0, Point2d p1) {
		k = (p1.y - p0.y) / (p1.x - p0.x);
		b = p0.y - p0.x * k; 
	}
	
	public Line2d(Double x0, Double y0, Double x1, Double y1) {
		k = (y1 - y0) / (x1 - x0);
		b = y0 - x0 * k;
	}
}
