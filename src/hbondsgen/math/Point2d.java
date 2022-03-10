package hbondsgen.math;

public class Point2d {
	/*
	 *	Properties
	 */
	//Coordinates
	public Double x = null;
	public Double y = null;
	
	/*
	 *	Interface
	 */
	//Equal
	public boolean isEqual(Point3d right) {
		if(this.x != right.x)
			return false;
		if(this.y != right.y)
			return false;
		return true;
	}
	
	//Assign
	//{x,y,z}
	public void assign(Double x, Double y) {
		if( (null == x) || (null == y) )
			return;
		this.x = x;
		this.y = y;
	}
	//Point3d
	public void assign(Point2d right) {
		this.x = right.x;
		this.y = right.y;
	}
	
	/*
	 *	Constructor
	 */
	//Initialization constructor
	public Point2d(Double x, Double y) {
		if(null == x)
			x = 0.0d;
		if(null == y)
			y = 0.0d;
		assign(x, y);
	}
	
	//Initialization constructor
	public Point2d(Point2d right) {
		assign(right);
	}
}
