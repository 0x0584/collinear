import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.In;

import java.util.Comparator;

public class BruteCollinearPoints {
	private static final int step = 3;

	private LineSegment[] segmts = new LineSegment[step];
	private int n_segmts = 0;

	private void resize() {
		if (n_segmts == segmts.length) {
			LineSegment[] fresh = new LineSegment[n_segmts + segmts.length];
			for (n_segmts = 0; n_segmts < segmts.length; ++n_segmts)
				fresh[n_segmts] = segmts[n_segmts];
			segmts = fresh;
		}
	}

	private void fit() {
		LineSegment[] fresh = new LineSegment[n_segmts];
		for (n_segmts = 0; n_segmts < fresh.length; ++n_segmts)
			fresh[n_segmts] = segmts[n_segmts];
		segmts = fresh;
	}


	private void generate_segment(Point p, Point q) {
		LineSegment seg = new LineSegment(p, q);

		for (int walk = 0; walk < n_segmts; ++walk)
			if (seg.toString().equals(segmts[walk].toString()))
				return ;

		resize();
		seg.draw();
		StdDraw.show();
		segmts[n_segmts++] = seg;
	}

	// finds all line segments containing 4 points
	public BruteCollinearPoints(Point[] points) {
		for (int i = 0; i < points.length; ++i) {
			for (int j = 0; j < points.length; ++j) {
				if (i == j)
					continue;

				double slp = points[i].slopeTo(points[j]);
				int found = 0;

				int s, t;

				if (points[i].compareTo(points[j]) < 0) { s = i; t = j; }
				else { s = j; t = i; }

				for (int k = 0; k < points.length; ++k) {
					if (k == i || k == j)
						continue;
					if (slp == points[i].slopeTo(points[k])) {
						if (points[k].compareTo(points[s]) < 0) s = k;
						if (points[k].compareTo(points[t]) > 0) t = k;
						found++;
					}
				}

				if (found > 1)
					generate_segment(points[s], points[t]);
			}
		}
		fit();
	}

	// the number of line segments
	public int numberOfSegments()          {   return n_segmts;   }

	// the line segments
	public LineSegment[] segments()        {   return segmts;   }


	public static void main(String[] args) {
		final int width = 800, height = 600;
		final int offset = 32768/20;

		StdDraw.setCanvasSize(width, height);
		StdDraw.enableDoubleBuffering();

		// read the n points from a file
		In in = new In(args[0]);
		int n = in.readInt();
		Point[] points = new Point[n];
		int [][] xy = new int[n][2];

		// draw the points
		StdDraw.setPenColor(StdDraw.BOOK_RED);
		StdDraw.setPenRadius(0.02);
		int xmax = -1;
		int ymax = -1;
		for (int i = 0; i < n; i++) {
			int x = xy[i][0] = in.readInt();
			int y = xy[i][1] = in.readInt();
			if (x > xmax) xmax = x;
			if (y > ymax) ymax = y;
			points[i] = new Point(x, y);
		}
		StdDraw.setXscale(-offset, xmax + offset);
		StdDraw.setYscale(-offset, ymax + offset);
		for (int i = 0, x, y; i < points.length; ++i) {
			points[i].draw();
			x = xy[i][0];
			y = xy[i][1];
			StdDraw.text(x,y-offset/2, "" + x + ", " + y, 0);
		}
		StdDraw.show();

		java.awt.Color[] colors = {StdDraw.RED, StdDraw.GREEN, StdDraw.BLUE,
								   StdDraw.CYAN, StdDraw.MAGENTA, StdDraw.BLACK};
		int col = 0;
		// print and draw the line segments
		StdDraw.setPenRadius(0.005);
		BruteCollinearPoints collinear = new BruteCollinearPoints(points);
		for (LineSegment segment : collinear.segments()) {
			StdOut.println(segment);
			StdDraw.setPenColor(colors[col++%colors.length]);
			segment.draw();
		}
		StdDraw.show();

		StdDraw.setPenColor(StdDraw.BOOK_RED);
		StdDraw.setPenRadius(0.02);

		for (int i = 0, x, y; i < points.length; ++i) {
			points[i].draw();
			x = xy[i][0];
			y = xy[i][1];
			StdDraw.text(x,y-offset/2, "" + x + ", " + y, 0);
		}
		StdDraw.show();
	}
}
