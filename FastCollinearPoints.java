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
	private static final String name = StdRandom.uniform(0, 326988) + " ";

	private static int img = 0;

	private final Merge merge = new Merge();

	private Point[] pnts;
	private int[] indices;
	private int[] aux;
	private int size;

	private Segment[] segmts;
	private int n_segmts;

	private class Segment {
		private int s, t;
		private int[] A;
		private double slp;
		private LineSegment seg;

		public Segment(int s, int t, int[] A, double slp) {
			this.s = s;
			this.t = t;
			this.A = A;
			this.slp = slp;
			this.seg = new LineSegment(pnts[s], pnts[t]);
		}

		public LineSegment segment()		{ return seg; }

		public void        draw()			{ seg.draw(); }

		public double      slope()			{ return slp; }

		@Override
		public String      toString()		{ return seg.toString(); }
	}

	///////////////////////////////////////////////////////////////////////////////

	private int   indexOf(int i)	{ return indices[i]; }
	private Point at(int i)			{ return pnts[indices[i]];}

	private int   root()			{ return indices[0]; }
	private Point at_root()			{ return at(root());}

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


	private void drawSegment(String s, Point p, Point q, java.awt.Color color,
							 double pensize) {
		LineSegment tmp = new LineSegment(p, q);
		StdDraw.setPenColor(color);
		StdDraw.setPenRadius(pensize);
		tmp.draw();
		// StdDraw.save("grid" + "_" + img + ".png"); img++;
		StdOut.println(s + " |> " + tmp);
	}

	///////////////////////////////////////////////////////////////////////////////

	private void append_segment(int s, int t, int k, int j, double slp) {
		int[] in_seg = new int[k - j];
		for (int u = j; u < k; ++u)
			in_seg[u - j] = indexOf(u);

		resize();
		segmts[n_segmts++] = new Segment(indexOf(s), indexOf(t), in_seg, slp);
		// drawSegment("SEGMENT: ", at(s), at(t), StdDraw.MAGENTA, 0.001);
		// for (int i = 0; i < n_segmts; ++i)
		// 	segmts[i].draw();
		// StdDraw.save("grid_" + img++ + ".png");
		// StdDraw.show();
	}

	public int generate_segment(int j, double newslp, double slp) {
		int k = j + 1;

		int s;
		int t;

		// StdOut.println("\n ===> " + newslp + "  SLOPES " + slp);
		// StdOut.println(" ===> " + at(j) + " 88888888 " + at(0) + "\n");
		if (at(j).compareTo(at(0)) > 0) { s = 0; t = j; }
		else								{ s = j; t = 0; }

		// StdOut.println("Point p fix:  " + at(s) + "slope: " + p.slopeTo(p));
		// StdOut.println("Point at(j): " + at(j) + "slope: " + p.slopeTo(at(j)));

		// StdDraw.setPenRadius(0.003);
		// StdDraw.setPenColor(StdDraw.BLACK);

		for (; k < size; k++) {

			// StdOut.println("Point: " + at(k) + "slope: "
			// 			   + p.slopeTo(at(k)));

			// if (at(k) == null)				StdOut.println("NUL!!!");
			if (at(k) == null)				break;

			else if (at(0).slopeTo(at(k)) != newslp
					 || at(0).slopeTo(at(k)) == slp)
				break;

			// at(k).draw();
			// StdDraw.show();


			if (at(s) == null || at(k).compareTo(at(s)) < 0)			s = k;
			else if (at(t) == null || at(k).compareTo(at(t)) > 0)		t = k;

		}

		if (at(s) == null || at(t) == null)
			return -1;

		// k--;

		if (k - j > 2) {

			LineSegment sg = new LineSegment(at(s), at(t));
			for (int i = 0; i < n_segmts; ++i)
				if (sg.toString().equals(segmts[i].toString()))
					return k;

			append_segment(s, t, k, j, slp);
		}

		return k < size && at(k) == null ? -1 : k;
	}

	private void recurse_segments(int ipnt, double slp) {

		java.awt.Color[] col = {StdDraw.GREEN,	StdDraw.BLUE,
								StdDraw.ORANGE, StdDraw.BLACK,
								StdDraw.PINK,	StdDraw.MAGENTA};

		if (pnts[ipnt] == null)
			return;

		Point p = pnts[ipnt];

		// StdDraw.setPenRadius(0.005);
		// StdDraw.setPenColor(StdDraw.RED);
		// p.draw();

		// StdOut.println(" Point p is fixed: " + p);

		merge.sort(p.slopeOrder());

		// StdOut.println(" >>>>>>>>>>>>>>>>> SlopeOrder of " + p);
		// for (int v : indices) {
		// 	if (pnts[v] == null) { StdOut.println( v + " is NULL "); continue; }
		// 	StdOut.printf("%-25s | slope: %-25.3f |\n", pnts[v].toString(), p.slopeTo(pnts[v]));
		// }
		// StdOut.println(" >>>>>>>>>>>>>>>>> SlopeOrder of " + p + "\n");

		int prev = n_segmts;

		for (int j = 1; j < size;) {
			if (at(j) == null) {j++; continue;}
			double newslp = p.slopeTo(at(j));
			j = generate_segment(j, newslp, slp);
			if (j < 0) break;
		}

		// StdOut.println(" >>>> >>>>>>>>>>>>> SlopeOrder of " + p);
		// for (int v : indices) {
		// 	if (pnts[v] == null) { StdOut.println( v + " is NULL "); continue; }
		// 	StdOut.printf("%-25s | slope: %-25.3f |\n", pnts[v].toString(), p.slopeTo(pnts[v]));
		// }
		// StdOut.println(" >>>> >>>>>>>>>>>>> SlopeOrder of " + p + "\n");

		// StdDraw.setPenColor(StdDraw.ORANGE);
		// StdDraw.setPenRadius(0.003);
		// pnts[ipnt].draw();
		// StdDraw.show();

		// StdOut.println("\n ||||||||||||||||||");
		// for (Segment s : segmts)
		// 	if (s != null)
		// 		StdOut.println(" || " + s.segment());
		// StdOut.println(" ||||||||||||||||||\n");

		for (int l = n_segmts; prev < l; ++prev) {
			// StdDraw.setPenRadius(0.01);
			// StdDraw.setPenColor(StdDraw.BOOK_BLUE);
			// segmts[prev].draw();
			// StdDraw.show();

			// StdDraw.setPenRadius(0.005);
			// StdDraw.setPenColor(StdDraw.MAGENTA);
			// // StdOut.println(" all found points \n");
			// for (int q : segmts[prev].A) {
			// 	if (pnts[q] == null) continue;
				// StdOut.printf(pnts[q] + " ");
			// 	pnts[q].draw();
			// 	StdDraw.show();
			// }
			// StdOut.println(" all found points relative to " + p + "\n");
			// StdOut.printf("\n");

			for (int q : segmts[prev].A) {
				if (pnts[q] == null) continue;
				// StdOut.println("\n1 !!!!! recurse using : " + pnts[q] + " p: " + p + "\n");
				recurse_segments(q, segmts[prev].slope());
				// StdOut.println("\n2 !!!!! recurse using : " + pnts[q] + " p: " + p + "\n\n");
			}

			// StdDraw.setPenRadius(0.004);
			// StdDraw.setPenColor(StdDraw.CYAN);
			for (int q : segmts[prev].A) {
				// if (pnts[q] != null)
				// 	pnts[q].draw();
				pnts[q] = null;
			}
		}

		// StdOut.println(" >>>> >>>>> >>>>>>>> SlopeOrder of " + p);
		// for (int v : indices) {
		// 	if (pnts[v] == null) { StdOut.println( v + " is NULL "); continue; }
		// 	StdOut.printf("%-25s | slope: %-25.3f |\n", pnts[v].toString(), p.slopeTo(pnts[v]));
		// }
		// StdOut.println(" >>>> >>>>> >>>>>>>> SlopeOrder of " + p + "\n");

	}

	// finds all line segments containing 4 or more points
	public FastCollinearPoints(Point[] points) {
		init(points);
		for (int i = 0; i < size; ++i) {
			// StdOut.println(" |||||||||| ################# ||||||||\n");
			if (pnts[i] == null)
				continue;
			recurse_segments(i, Double.NEGATIVE_INFINITY);
			// StdDraw.setPenRadius(0.0075);
			// StdDraw.setPenColor(StdDraw.YELLOW);
			// pnts[i].draw();
			pnts[i] = null;
		}
		StdOut.println(" >>> !!! <<<<< ");
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

		StdDraw.setPenColor(StdDraw.DARK_GRAY);
		StdDraw.setPenRadius(0.003);
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
		StdDraw.setPenColor(StdDraw.GREEN);
		StdDraw.setPenRadius(0.002);
		StdOut.println(" >>> SEGMENTS!!! <<<<< ");
		for (LineSegment segment : collinear.segments()) {
			StdOut.println(segment);
			segment.draw();
		}
		StdDraw.show();
		StdOut.println(" >>> SEGMENTS!!! <<<<< ");

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
