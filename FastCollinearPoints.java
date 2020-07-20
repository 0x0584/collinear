import java.util.Comparator;
import java.util.Iterator;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdRandom;

import edu.princeton.cs.algs4.WeightedQuickUnionUF;


public class FastCollinearPoints
{
	private static final int width = 800, height = 600;
	private static final int offset = 32768/20;
	private static final String name = StdRandom.uniform(0, 326988) + " ";

	private final WeightedQuickUnionUF uf;

	private static int img = 0;

	private final Merge merge = new Merge();

	private Point[] pnts;
	private int[] indices;
	private int[] aux;
	private int size;

	private LineSegment[] segmts;
	private int n_segmts;

	///////////////////////////////////////////////////////////////////////////////

	private Point at(int i) {
		return (i < 0 || i >= size || indices[i] < 0) ? null : pnts[indices[i]];
	}

	private void init(Point[] points) {
		uf = WeightedQuickUnionUF(size);

		n_segmts = 0;
		pnts = points;
		size = points.length;

		indices = new int[size];
		aux = new int[size];
		segmts = new LineSegment[4];

		for (int i = 0; i < size; ++i)
			indices[i] = i;
	}

	///////////////////////////////////////////////////////////////////////////////

	private void drawSegment(String s, Point p, Point q, java.awt.Color color,
							 double pensize) {
		LineSegment tmp = new LineSegment(p, q);
		StdDraw.setPenColor(color);
		StdDraw.setPenRadius(pensize);
		tmp.draw();
		StdDraw.save("grid" + "_" + img + ".png"); img++;
		StdDraw.show();
		StdOut.println(s + " |> " + tmp);
	}

	public int generate_segment(Point p, int j, double slp, java.awt.Color color) {
		int k = j + 1;

		Point s = p;
		Point t = at(j);

		StdDraw.setPenColor(StdDraw.BLUE);
		StdDraw.setPenRadius(0.03);
		p.draw(); at(j).draw();
		StdOut.println("Point p fix:  " + s + "slope: " + p.slopeTo(p));
		StdOut.println("Point at(j): " + t + "slope: " + p.slopeTo(t));

		StdDraw.setPenRadius(0.025);
		for (; k < pnts.length && p.slopeTo(at(k)) == slp; k++) {
			StdOut.println("Point: " + at(k) + "slope: " + p.slopeTo(at(k)));
			if (at(k).compareTo(s) < 0) s = at(k);
			if (at(k).compareTo(t) > 0 ) t = at(k);
			StdDraw.setPenColor(StdDraw.YELLOW);
			StdDraw.setPenRadius(0.03);
			at(k).draw();
			StdDraw.show();
			drawSegment(" generate " + img, s, t, color, 0.002);
		}
		StdOut.println("////\n");
			StdDraw.show();

		// k--;

		if (k - j > 2) {
			StdDraw.setPenColor(StdDraw.RED);
			StdDraw.setPenRadius(0.004);
			p.draw(); s.draw(); t.draw();
			drawSegment(" yellow >>> " + img, s, t, StdDraw.YELLOW, 0.005);

			resize();
			LineSegment sg = new LineSegment(s, t);

			segmts[n_segmts++] = sg;
			StdOut.println("SEGMENT: " + sg);
		}


		return k;
	}

    // finds all line segments containing 4 or more points
	public FastCollinearPoints(Point[] points) {
		init(points);
		java.awt.Color[] col = {StdDraw.RED, StdDraw.GREEN, StdDraw.BLUE,
								StdDraw.ORANGE, StdDraw.BLACK, StdDraw.PINK};

		for (Point p : points) {

			merge.sort(p.slopeOrder());

			StdOut.println(" fix point >> " + p);

			int k = 0;
			for (int j = 1; j < size && at(j) != null; ++j) {
				StdDraw.setPenColor(StdDraw.BLACK);
				StdDraw.setPenRadius(0.03);
				for (int i = 0, x, y; i < points.length; ++i)
					points[i].draw();
				StdDraw.setPenColor(StdDraw.ORANGE);
				StdDraw.setPenRadius(0.03);
				p.draw(); at(j).draw();
				drawSegment(" green >>> ", p, at(j), StdDraw.GREEN, 0.001);
				j = generate_segment(p, j, p.slopeTo(at(j)), col[k++ % col.length]);
				StdDraw.show();
			}

			for (int i = 0, x, y; i < points.length; ++i)
				points[i].draw();
			StdDraw.show();

			StdOut.println("\n\n >>------ ######## SEGMENTS ########### ------\n " );
			k = 0;
			for (LineSegment segment : segments()) {
				if (segment == null)
					break;
				StdOut.println(segment);
				StdDraw.setPenColor(col[k++ % col.length]);
				StdDraw.setPenRadius(0.008);
				segment.draw();
			}
			StdDraw.show();

			// eliminate(i);
			StdOut.println(" >>------------\n\n " );

		}
		fit();
	}

    // the number of line segments
	public int numberOfSegments()					{ return n_segmts; }

	// the line segments
	public LineSegment[] segments()					{ return segmts; }

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

	private class Merge
	{
		private boolean less(int[] arr, int i, int j, Comparator<Point> cmp) {
			if (arr[i] < 0) return false;
			else if (arr[j] < 0) return true;
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

	public static void main(String[] args) {
		// read the n points from a file
		In in = new In(args[0]);
		int n = in.readInt();
		Point[] points = new Point[n];
		int [][] xy = new int[n][2];

		// draw the points
		int xmax = -1;
		int ymax = -1;
		for (int i = 0; i < n; i++) {
			int x = xy[i][0] = in.readInt();
			int y = xy[i][1] = in.readInt();
			if (x > xmax) xmax = x;
			if (y > ymax) ymax = y;
			points[i] = new Point(x, y);
		}

		StdDraw.setCanvasSize(width, height);
		StdDraw.enableDoubleBuffering();

		StdDraw.setPenColor(StdDraw.BOOK_RED);
		StdDraw.setPenRadius(0.01);
		StdDraw.setXscale(-offset, xmax + offset);
		StdDraw.setYscale(-offset, ymax + offset);
		for (int i = 0, x, y; i < points.length; ++i) {
			points[i].draw();
			x = xy[i][0];
			y = xy[i][1];
 			StdDraw.text(x,y-offset/2, x + "," + y, 0);
		}
		StdDraw.show();


		StdOut.println(" >>> SEGMENTS!!! <<<<< ");
		FastCollinearPoints collinear = new FastCollinearPoints(points);
		for (LineSegment segment : collinear.segments()) {
			StdOut.println(segment);
			StdDraw.setPenColor(StdDraw.BOOK_BLUE);
			StdDraw.setPenRadius(0.005);
			segment.draw();
		}
		StdDraw.show();

		// for (int i = 0, x, y; i < points.length; ++i) {
		// 	points[i].draw();
		// 	x = xy[i][0];
		// 	y = xy[i][1];
		// 	StdDraw.text(x,y-offset/2, "" + x + ", " + y, 0);
		// }
		// StdDraw.show();
	}
}
