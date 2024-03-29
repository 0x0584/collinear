/******************************************************************************
 *  Compilation:  javac Point.java
 *  Execution:    java Point
 *  Dependencies: none
 *
 *  An immutable data type for points in the plane.
 *  For use on Coursera, Algorithms Part I programming assignment.
 *
 ******************************************************************************/

import java.util.Comparator;
import edu.princeton.cs.algs4.StdDraw;

public class Point
	implements Comparable<Point> {

    private final int x;     // x-coordinate of this point
    private final int y;     // y-coordinate of this point

    /**
     * Initializes a new point.
     *
     * @param  x the <em>x</em>-coordinate of the point
     * @param  y the <em>y</em>-coordinate of the point
     */
    public Point(int x, int y) {
        /* DO NOT MODIFY */
        this.x = x;
        this.y = y;
    }

    /**
     * Draws this point to standard draw.
     */
    public void draw() {
        /* DO NOT MODIFY */
        StdDraw.point(x, y);
    }

    /**
     * Draws the line segment between this point and the specified point
     * to standard draw.
     *
     * @param that the other point
     */
    public void drawTo(Point that) {
        /* DO NOT MODIFY */
        StdDraw.line(this.x, this.y, that.x, that.y);
    }

    /**
     * Returns the slope between this point and the specified point.
     * Formally, if the two points are (x0, y0) and (x1, y1), then the slope
     * is (y1 - y0) / (x1 - x0). For completeness, the slope is defined to be
     * +0.0 if the line segment connecting the two points is horizontal;
     * Double.POSITIVE_INFINITY if the line segment is vertical;
     * and Double.NEGATIVE_INFINITY if (x0, y0) and (x1, y1) are equal.
     *
     * @param  that the other point
     * @return the slope between this point and the specified point
     */
    public double slopeTo(Point that) {
		double deltax = that.x - this.x;
		double deltay = that.y - this.y;

		if (deltax == 0 && deltay == 0) return Double.NEGATIVE_INFINITY;
		else if (deltay == 0)           return Double.POSITIVE_INFINITY;
		else if (deltax == 0)           return 0;
		else                            return deltay / deltax;

    }

    /**
     * Compares two points by y-coordinate, breaking ties by x-coordinate.
     * Formally, the invoking point (x0, y0) is less than the argument point
     * (x1, y1) if and only if either y0 < y1 or if y0 = y1 and x0 < x1.
     *
     * @param  that the other point
     * @return the value <tt>0</tt> if this point is equal to the argument
     *         point (x0 = x1 and y0 = y1);
     *         a negative integer if this point is less than the argument
     *         point; and a positive integer if this point is greater than the
     *         argument point
     */
    public int compareTo(Point that) {
		if (that.y < this.y)		return +1;
		else if (this.y < that.y )	return -1;
		else if (that.x < this.x)	return +1;
		else if (this.x < that.x)	return -1;
		else						return  0;
    }

	private class PointComparator
		implements Comparator<Point>
	{
		public int compare(Point p, Point q) {
			double qslp = slopeTo(q);
			double pslp = slopeTo(p);

			if (pslp == qslp)
				return q.compareTo(p);
			return Double.compare(slopeTo(q), slopeTo(p));
		}
	}

    /**
     * Compares two points by the slope they make with this point.
     * The slope is defined as in the slopeTo() method.
     *
     * @return the Comparator that defines this ordering on points
     */
    public Comparator<Point> slopeOrder() {
		return new PointComparator();
    }


    /**
     * Returns a string representation of this point.
     * This method is provide for debugging;
     * your program should not rely on the format of the string representation.
     *
     * @return a string representation of this point
     */
    public String toString() {
        /* DO NOT MODIFY */
        return "(" + x + ", " + y + ")";
    }

    /**
     * Unit tests the Point data type.
     */
    public static void main(String[] args) {
		final int width = 800, height = 600, scale = 30;

		Point origin = new Point(0, 0),
			p = new Point(10, 10), q = new Point(20, 20);

		StdDraw.setCanvasSize(width, height);
		StdDraw.enableDoubleBuffering();
		StdDraw.setScale(-scale, scale);
		StdDraw.setPenRadius(0.03);
		StdDraw.setPenColor(StdDraw.BLUE);
		origin.draw(); p.draw(); q.draw();
		StdDraw.setPenRadius(0.01);
		StdDraw.setPenColor(StdDraw.MAGENTA);
		origin.drawTo(p); origin.drawTo(q); q.drawTo(p);
		StdDraw.show();
    }
}
