import java.util.Comparator;
import java.util.Iterator;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdRandom;

public class FastCollinearPoints
{
	private static final int width = 800, height = 600;
	private static final int offset = 32768/20;
	// private static int img = 0;

	private final Merge merge = new Merge();

	private Point[] pnts;
	private int[] indices;
	private int[] aux;
	private int size;

	private Segment[] segmts;
	private int n_segmts;

	private class Segment {
		private final Point s;
		private final Point t;
		private final int[] A;
		private final LineSegment seg;

		public Segment(int s, int t, int[] A, double slp) {
			this.A = A;
			this.s = pnts[s]; this.t = pnts[t];
			this.seg = new LineSegment(this.s, this.t);
		}

		public LineSegment segment()		{ return seg; }

		public void        draw()			{ seg.draw(); }

		public boolean	   belong(Point p)	{
			// StdOut.println("IN: " + p );
			// StdOut.println("SEG: " + s + " " + t);
			double sp = s.slopeTo(p);
			double tp = t.slopeTo(p);

			return sp == Double.NEGATIVE_INFINITY
				|| tp == Double.NEGATIVE_INFINITY
				|| sp == tp;
		}

		@Override
		public String      toString()		{ return seg.toString(); }
	}

	///////////////////////////////////////////////////////////////////////////////

	private int   indexOf(int i)	{ return indices[i]; }
	private Point at(int i)			{ return pnts[indices[i]];}

	///////////////////////////////////////////////////////////////////////////////

	private void init(Point[] points) {

		n_segmts = 0;
		pnts = points;
		size = points.length;

		indices = new int[size];
		aux = new int[size];
		segmts = new Segment[4];

		for (int i = 0; i < size; ++i)
			indices[i] = i;
	}

	private static java.awt.Color randomColor() {
		// return new java.awt.Color(StdRandom.uniform(StdRandom.uniform(1, 255 * 7)) / 7,
		// 						  StdRandom.uniform(StdRandom.uniform(1, 255 * 3)) / 3,
		// 						  StdRandom.uniform(StdRandom.uniform(1, 255 * 5)) / 5);
		return StdDraw.MAGENTA;
	}

	private void append_segment(int s, int t, int k, int j, double slp, java.awt.Color color) {
		int[] in_seg = new int[k - j];

		for (int u = j; u < k; ++u)
			in_seg[u - j] = indexOf(u);

		resize();
		segmts[n_segmts++] = new Segment(indexOf(s), indexOf(t), in_seg, slp);
		StdDraw.setPenColor(color);
		StdDraw.setPenRadius(0.001);
		segmts[n_segmts - 1].draw();
		StdDraw.show();
		StdOut.println(segmts[n_segmts - 1]);
	}

	public int generate_segment(int j, double newslp, java.awt.Color color) {
		int s = 0, t = j;

		if (at(s).compareTo(at(t)) > 0) {
			s = j; t = 0;
		}

		int k = j + 1;
		int n_pnts = 0;
		for (; k < size; k++) {
			if (at(k) == null || at(0).slopeTo(at(k)) != newslp)
				break;
			n_pnts++;
			if (at(s) == null || at(k).compareTo(at(s)) < 0)
				s = k;
			else if (at(t) == null || at(k).compareTo(at(t)) > 0)
				t = k;
		}
		if (at(s) == null || at(t) == null)
			return -1;
		if (n_pnts > 1) {
			if (checkAllSegments(at(s), at(t)))
				return k;
			append_segment(s, t, k, j, newslp, color);
		}
		return k < size && at(k) == null ? -1 : k;
	}


	private Point fixed() { return at(0); }

	private boolean checkAllSegments(Point p, Point q) {
		for (Segment s : segmts) {
			if (s == null)
				break;
			if (s.belong(p) && s.belong(q)) {
				StdOut.println(p + "and " + q + " belong to " + s);
				return true;
			}
		}
		return false;
	}

	private void recurse_segments(int ipnt, java.awt.Color color) {
		if (pnts[ipnt] == null)
			return ;

		merge.sort(pnts[ipnt].slopeOrder());

		StdDraw.setPenColor(StdDraw.GREEN);
		StdDraw.setPenRadius(0.02);
		fixed().draw();
		StdDraw.show();

		Point p = pnts[ipnt];
		int prev = n_segmts;

		StdDraw.setPenColor(color);
		for (int j = 1; j > 0 && j < size && at(j) != null;) {
			j = generate_segment(j, fixed().slopeTo(at(j)), color);
		}


		StdDraw.setPenColor(StdDraw.RED);
		StdDraw.setPenRadius(0.01);
		p.draw();
		StdDraw.show();

		StdDraw.save("foo.png");

		pnts[ipnt] = null;

		for (int l = n_segmts; prev < l; ++prev) {
			for (int q : segmts[prev].A)
				recurse_segments(q, randomColor());
			// StdDraw.save("foo.png");
		}
	}

	// finds all line segments containing 4 or more points
	public FastCollinearPoints(Point[] points) {
		init(points);
		for (int i = 0; i < size; ++i) {
			recurse_segments(i, randomColor());
		}
	}

	// the number of line segments
	public int numberOfSegments() {
		return n_segmts;
	}

	// the line segments
	public LineSegment[] segments() {
		LineSegment[] fresh = new LineSegment[n_segmts];
		for (n_segmts = 0; n_segmts < fresh.length; ++n_segmts)
			fresh[n_segmts] = segmts[n_segmts].segment();
		return fresh;
	}

	private void resize() {
		if (n_segmts == segmts.length) {
			Segment[] fresh = new Segment[n_segmts + segmts.length - 1];
			for (n_segmts = 0; n_segmts < segmts.length; ++n_segmts)
				fresh[n_segmts] = segmts[n_segmts];
			segmts = fresh;
		}
	}

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
		StdDraw.setPenRadius(0.01);
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

		java.awt.Color[] colors = {StdDraw.RED, StdDraw.GREEN, StdDraw.BLUE,
								   StdDraw.CYAN, StdDraw.MAGENTA, StdDraw.BLACK};
		int col = 0;

		// print and draw the line segments
		FastCollinearPoints collinear = new FastCollinearPoints(points);
		StdDraw.setPenRadius(0.003);
		for (LineSegment segment : collinear.segments()) {
			StdDraw.setPenColor(randomColor());
			segment.draw();
			StdDraw.show();
			StdOut.println(segment);
		}
	}


	private class Merge
	{
		private boolean less(int[] arr, int i, int j, Comparator<Point> cmp) {
			if (pnts[arr[j]] == null) return false;
			else if (pnts[arr[i]] == null) return true;
			else return cmp.compare(pnts[arr[i]], pnts[arr[j]]) < 0;
		}

		private boolean isSorted(int lo, int hi, Comparator<Point> cmp) {
			for (int i = lo + 1; i < hi; ++i)
				if (less(indices, i, i - 1, cmp))
					return false;
			return true;
		}

		private void kagami(int lo, int hi) {
			for (int i = lo; i < hi; ++i)
				aux[i] = indices[i];
		}

		private void merge(int lo, int mid, int hi, Comparator<Point> cmp) {
			assert (isSorted(lo, mid, cmp));
			assert (isSorted(mid, hi, cmp));
			kagami(lo, hi);
			for (int k = lo, i = lo, j = mid; k < hi; ++k) {
				if (i == mid)					indices[k] = aux[j++];
				else if (j == hi)				indices[k] = aux[i++];
				else if (less(aux, i, j, cmp))	indices[k] = aux[j++];
				else							indices[k] = aux[i++];
			}
			assert (isSorted(lo, hi, cmp));
		}

		public void sort(int lo, int hi, Comparator<Point> cmp) {
			if (hi - lo < 2)
				return;
			int mid = lo + (hi - lo) / 2;
			sort(lo, mid, cmp);
			sort(mid, hi, cmp);
			merge(lo, mid, hi, cmp);
		}

		public void sort(Comparator<Point> compare) {
			sort(0, size, compare);
		}
	}
}
