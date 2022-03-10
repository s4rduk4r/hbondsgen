package hbondsgen.math;

public class Segment2d {
	/*
	 * Properties
	 */
	public Point2d p0 = null;
	public Point2d p1 = null;
	
	/*
	 * In-class constants
	 */
	//Error correction
	public static final Double ERR_EPSILON = 1E-14; 
	
	/*
	 * Interface
	 */
	//Get line through this segment
	public Line2d getLine() {
		return new Line2d(p0, p1);
	}
	
	//Check if point belong to this segment
	public boolean isBelong(Point2d p) {
		Double minX, maxX, minY, maxY;
		if(p0.x > p1.x) {
			maxX = p0.x;
			minX = p1.x;
		} else {
			maxX = p1.x;
			minX = p0.x;
		}
		if(p0.y > p1.y) {
			maxY = p0.y;
			minY = p1.y;
		} else {
			maxY = p1.y;
			minY = p0.y;
		}
		//Check minX, maxX, minY, maxY
		if(maxX - minX <= ERR_EPSILON) {
			maxX = minX;
		}
		if(maxY - minY <= ERR_EPSILON) {
			maxY = minY;
		}
		p = errorCorrection(p, minX, maxX, minY, maxY);
		//Special cases check
		if(minX == maxX && minY == maxY) {
			return p.x == minX && p.y == minY;
		}
		if(minX == maxX) {
			return p.x == minX && p.y >= minY && p.y <= maxY;
		}
		if(minY == maxY) {
			return p.x >= minX && p.x <= maxX && p.y == minY;
		}
		return p.x >= minX && p.x <= maxX && p.y >= minY && p.y <= maxY;
	}
	
	//Check if point belong to this segment excluding points of segment
	public boolean isBelongExclusive(Point2d p) {
		Double minX, maxX, minY, maxY;
		if(p0.x > p1.x) {
			maxX = p0.x;
			minX = p1.x;
		} else {
			maxX = p1.x;
			minX = p0.x;
		}
		if(p0.y > p1.y) {
			maxY = p0.y;
			minY = p1.y;
		} else {
			maxY = p1.y;
			minY = p0.y;
		}
		//Check minX, maxX, minY, maxY
		if(maxX - minX <= ERR_EPSILON) {
			maxX = minX;
		}
		if(maxY - minY <= ERR_EPSILON) {
			maxY = minY;
		}
		p = errorCorrection(p, minX, maxX, minY, maxY);
		//Special cases check
		if(minX == maxX && minY == maxY) {
			return p.x == minX && p.y == minY;
		}
		if(minX == maxX) {
			return p.x == minX && p.y > minY && p.y < maxY;
		}
		if(minY == maxY) {
			return p.x > minX && p.x < maxX && p.y == minY;
		}
		return p.x > minX && p.x < maxX && p.y > minY && p.y < maxY;
	}
	
	/*
	 * Auxiliary methods
	 */
	//Error correction
	protected Point2d errorCorrection(Point2d p, Double minX, Double maxX, Double minY, Double maxY) {
		//Check p.x and p.y for being withing error correction range
		if(p.x >= minX - ERR_EPSILON && p.x <= minX + ERR_EPSILON)
			p.x = minX;
		if(p.x >= maxX - ERR_EPSILON && p.x <= maxX + ERR_EPSILON)
			p.x = maxX;
		if(p.y >= minY - ERR_EPSILON && p.y <= minY + ERR_EPSILON)
			p.y = minY;
		if(p.y >= maxY - ERR_EPSILON && p.y <= maxY + ERR_EPSILON)
			p.y = maxY;
		return p;
	}
	
	/*
	 * Constructor
	 */
	//Initialization constructor
	public Segment2d(Point2d p0, Point2d p1) {
		this.p0 = p0;
		this.p1 = p1;
	}
	
	public Segment2d(Double x0, Double y0, Double x1, Double y1) {
		p0 = new Point2d(x0, y0);
		p1 = new Point2d(x1, y1);
	}
}
