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
	// private final WeightedQuickUnionUF uf;


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

		// uf = WeightedQuickUnionUF(size);
	private void init(Point[] points) {

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
		// StdDraw.save("grid" + "_" + img + ".png"); img++;
		StdOut.println(s + " |> " + tmp);
	}

	public int generate_segment(Point p, int j, double slp, java.awt.Color color) {
		int k = j + 1;

		Point s;
		Point t;

		if (at(j).compareTo(p) > 0) { s = p;  t = at(j); }
		else { s = at(j); t = p; }

		// StdOut.println("Point p fix:  " + s + "slope: " + p.slopeTo(p));
		// StdOut.println("Point at(j): " + at(j) + "slope: " + p.slopeTo(at(j)));

		// StdDraw.setPenRadius(0.01);
		for (; k < size && p.slopeTo(at(k)) == slp; k++) {

			// StdOut.println("Point: " + at(k) + "slope: "
			// 			   + p.slopeTo(at(k)));

			if (at(k).compareTo(s) < 0) {
				// StdOut.println("a: " + s + " <-> " + at(k));
				s = at(k);
			} else {
				// StdOut.println("a: " + s);
			}

			if (at(k).compareTo(t) > 0) {
				// StdOut.println("b: " + t + " <-> " + at(k));
				t = at(k);
			} else {
				// StdOut.println("b: " + t);
			}

		}
		// StdOut.println("////\n");
		// StdDraw.show();

		// k--;

		if (k - j > 2) {

			resize();
			LineSegment sg = new LineSegment(s, t);

			for (int i = 0; i < n_segmts; ++i) {
				// StdOut.println(sg + " ||| " + segmts[i]);
				if (sg.toString().equals(segmts[i].toString()))
					return k;
			}


			drawSegment("  >>> " + img, s, t, color, 0.003);
			StdDraw.show();

			segmts[n_segmts++] = sg;
			// StdOut.println("SEGMENT: " + sg);
		}

		return k;
	}

    // finds all line segments containing 4 or more points
	public FastCollinearPoints(Point[] points) {
		java.awt.Color[] col = {StdDraw.GREEN, StdDraw.BLUE, StdDraw.ORANGE,
								StdDraw.BLACK, StdDraw.PINK};

		init(points);
		for (Point p : points) {

			merge.sort(p.slopeOrder());

 			// StdOut.println(" >>>>>>>>>>>>>>>>> SlopeOrder of " + p + "\n");
			// for (int v : indices)
			// 	StdOut.printf("%-25s | slope: %-25.3f |\n", pnts[v].toString(), p.slopeTo(pnts[v]));
			// StdOut.println(" >>>>>>>>>>>>>>>>> SlopeOrder of " + p + "\n");

			int k = 0;
			int prev = n_segmts;

			for (int j = 1; j < size && at(j) != null;) {
				j = generate_segment(p, j, p.slopeTo(at(j)), StdDraw.BOOK_LIGHT_BLUE);
			}
			StdDraw.show();

			k = 0;
			if (n_segmts != prev) {
				StdDraw.setPenColor(StdDraw.BOOK_BLUE);
				// StdOut.println("\n >>------ |||||||||||||||||||||||||| ------ \n" );
				for (int i = 0; i < n_segmts; ++i) {
					// StdOut.println(segmts[i]);
					segmts[i].draw();
				}
				StdDraw.show();
				// StdOut.println("\n >>------ |||||||||||||||||||||||||| ------\n\n " );
			}
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
		LineSegment[] fresh = new LineSegment[n_segmts];
		for (n_segmts = 0; n_segmts < fresh.length; ++n_segmts)
			fresh[n_segmts] = segmts[n_segmts];
		segmts = fresh;
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
			// x = xy[i][0];
			// y = xy[i][1];
 			// StdDraw.text(x,y-offset/2, x + "," + y, 0);
		}
		StdDraw.show();


		FastCollinearPoints collinear = new FastCollinearPoints(points);
		StdDraw.setPenColor(StdDraw.BOOK_RED);
		StdDraw.setPenRadius(0.007);
		StdOut.println(" >>> SEGMENTS!!! <<<<< ");
		for (LineSegment segment : collinear.segments()) {
			StdOut.println(segment);
			segment.draw();
		}
		StdDraw.show();

		// StdDraw.setPenColor(StdDraw.BOOK_RED);
		// StdDraw.setPenRadius(0.01);
		// for (int i = 0, x, y; i < points.length; ++i) {
		// 	points[i].draw();
		// 	// x = xy[i][0];
		// 	// y = xy[i][1];
		// 	// StdDraw.text(x,y-offset/2, "" + x + ", " + y, 0);
		// }
		// StdDraw.show();
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

}
