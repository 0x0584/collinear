import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class BruteCollinearPoints {
	private static final int step = 3;
	private final WeightedQuickUnionUF uf;

	private LineSegment[] segmts;
	private final Point[] pnts;
	private int n_segmts;

	private void resize() {
		if (n_segmts == segmts.length) {
			LineSegment[] fresh = new LineSegment[n_segmts + segmts.length - 1];
			for (n_segmts = 0; n_segmts < segmts.length; ++n_segmts)
				fresh[n_segmts] = segmts[n_segmts];
			segmts = fresh;
		}
	}

	private void fit() {
		LineSegment[] fresh = new LineSegment[n_segmts - 1];
		for (n_segmts = 0; n_segmts < fresh.length; ++n_segmts)
			fresh[n_segmts] = segmts[n_segmts];
		segmts = fresh;
	}

	private void generate_segment(int p, int q) {
		resize();
		uf.union(p, q);
		LineSegment segmt = new LineSegment(pnts[p], pnts[q]);
		StdOut.println(segmt);
		segmt.draw();
		segmts[n_segmts++] = segmt;
	}

	private void generate_segments(int p, int q, int r, int s) {
		int[] ids = {uf.find(p), uf.find(q),
					 uf.find(r), uf.find(s)};

		StdDraw.setPenColor(StdDraw.BLACK);
		StdDraw.setPenRadius(0.004);

//		if (ids[0] != ids[1])
			generate_segment(p, q);
//		if (ids[1] != ids[2])
			generate_segment(q, r);
//		if (ids[2] != ids[3])
			generate_segment(r, s);
			StdDraw.show();
	}

	private boolean collinear(int p, int q, int r, int s) {
		double sq = pnts[p].slopeTo(pnts[q]);
		double sr = pnts[p].slopeTo(pnts[r]);
		double ss = pnts[p].slopeTo(pnts[s]);

		return (sq == sr && sr == ss && ss == sq);
	}

	private void show_line(Point p, Point q, java.awt.Color color, double rad) {
		StdDraw.setPenColor(color);
		StdDraw.setPenRadius(rad);
		new LineSegment(p, q).draw();
	}

	// finds all line segments containing 4 points
	public BruteCollinearPoints(Point[] points) {
		uf = new WeightedQuickUnionUF(points.length);
		segmts = new LineSegment[step];
		n_segmts = 0;
		pnts = points;

		// int[] ids = new int[points.length];
		// for (int i = 0; i < ids.length; ++i)
		// 	ids[i] = i;
		// StdRandom.shuffle(ids);
		// for (int i = 0; i < points.length; ++i) {
		// 	Point p = points[ids[i]];
		// 	points[ids[i]] = points[i];
		// 	points[i] = p;
		// }

		for (int i = 0; i < points.length; ++i) {
			for (int j = 0; j < points.length; ++j) {
				if (i == j) continue;
				show_line(points[i], points[j], StdDraw.MAGENTA, 0.006);
				for (int k = 0; k < points.length; ++k) {
					if (k == j || k == i) continue;
					show_line(points[i], points[k], StdDraw.GREEN, 0.004);
					for (int s = 0; s < points.length; ++s) {
						// show_line(points[i], points[j], StdDraw.GREEN, 0.02);
						// show_line(points[i], points[k], StdDraw.CYAN, 0.007);
						show_line(points[k], points[s], StdDraw.CYAN, 0.001);
						StdDraw.show();
						if (s != k && s != i && s != j
							&& collinear(i, j, k, s))
							generate_segments(i, j, k, s);

					}
				}
				StdDraw.save("grid_" + i + ".png");
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
			// StdDraw.text(x,y-offset/2, "" + x + ", " + y, 0);
		}
		StdDraw.show();

		// print and draw the line segments
		StdDraw.setPenColor(StdDraw.DARK_GRAY);
		StdDraw.setPenRadius(0.005);
		BruteCollinearPoints collinear = new BruteCollinearPoints(points);
		for (LineSegment segment : collinear.segments()) {
			StdOut.println(segment);
			segment.draw();
		}
		StdDraw.show();

		for (int i = 0, x, y; i < points.length; ++i) {
			points[i].draw();
			x = xy[i][0];
			y = xy[i][1];
			// StdDraw.text(x,y-offset/2, "" + x + ", " + y, 0);
		}
		StdDraw.show();
	}
}
