package org.gbif.occurrence.spatial.polygon;

import java.util.*;

public class Polygon { 
    Point[] vertices;  
    public double miny, maxy, minx, maxx;
    double[] xsort;
    Integer[][] segment;
    int nv, ne, nsegs;
    boolean[] isEdge;

    public Polygon(Point[] vertices) {
	nv = vertices.length;
	ne = nv-1;
	this.vertices = vertices;
	isEdge = new boolean[ne];
	Arrays.fill(isEdge, true);
	slabify();
    }

    // Constructs a polygon from multiple closed paths, and computes 
    // the slab decomposition to allow fast point-in-polygon lookups.
    public Polygon(Point[][] vertices) {
	int cnt=0;
	for (int i=0; i<vertices.length; i++)
	    cnt += vertices[i].length;
	nv = cnt;
	ne = nv-1;
	this.vertices = new Point[cnt];
	isEdge = new boolean[nv];
	Arrays.fill(isEdge, true);
	cnt=0;
	for (int i=0; i<vertices.length; i++) {
	    for (int j=0; j<vertices[i].length; j++)
		this.vertices[cnt++] = vertices[i][j];
	    if (!vertices[i][0].equals(vertices[i][vertices[i].length-1]))
		throw (new IllegalArgumentException("First and last vertex of each region must be equal"));
	    isEdge[cnt-1] = false;
	}
	slabify();
    }

    double getx(int v) { return vertices[v].x; }
    double gety(int v) { return vertices[v].y; }

    double[] unique(double[] x) {
	Arrays.sort(x);
	ArrayList<Double> a = new ArrayList<Double>();
	for (int i=0; i<x.length; i++) 
	    if (i==0 || x[i]!=x[i-1]) a.add(x[i]);
	double[] res = new double[a.size()];
	for (int i=0; i<res.length; i++) 
	    res[i] = a.get(i);
        return res;
    }

    double gety(double x, int v) {
	return gety(v) + (gety(v+1)-gety(v)) * (x-getx(v))/(getx(v+1)-getx(v));
    }

    void slabify() {
        double[] x = new double[nv];
	for (int v=0; v<nv; v++)
	    x[v] = getx(v);
	xsort = unique(x);
	nsegs = xsort.length-1;
	ArrayList[] sega = new ArrayList[nsegs];
	for (int i=0; i<nsegs; i++) sega[i] = new ArrayList();
	for (int e=0; e<ne; e++) {
	    if (!isEdge[e]) continue;
	    int[] xx = new int[] { 
		Arrays.binarySearch(xsort,getx(e)), 
		Arrays.binarySearch(xsort,getx(e+1)) };
	    Arrays.sort(xx);
	    for (int seg=xx[0]; seg<xx[1]; seg++)
		sega[seg].add(e);
	}
	segment = new Integer[nsegs][];
	for (int seg=0; seg<nsegs; seg++) {
	    segment[seg] = (Integer[]) sega[seg].toArray(new Integer[0]);
	    final double segmid = (xsort[seg]+xsort[seg+1])/2;
	    Arrays.sort(segment[seg], new Comparator<Integer>() {
			    public int compare(Integer i0, Integer i1) {
				double y0 = gety(segmid, i0), y1 = gety(segmid, i1);
				return (y0<y1) ? -1 : (y0==y1) ? 0 : 1;
			    }});
	}
	miny = maxy = gety(0);
	for (int v=1; v<nv; v++) {
	    if (gety(v) < miny) miny = gety(v);
	    if (gety(v) > maxy) maxy = gety(v);
	}
	minx = xsort[0];
	maxx = xsort[nsegs];
    }

    public boolean contains(final Point p) {
	if (p.x <= minx || p.x >= maxx) return false;
	if (p.y <= miny || p.y >= maxy) return false;
	int seg = Arrays.binarySearch(xsort, p.x);
	if (seg<0) seg = -seg - 2;
	int i = Arrays.binarySearch(segment[seg], -1, new Comparator<Integer>() {
					public int compare(Integer i0, Integer i1) {
					    double y0 = (i0==-1) ? p.y : gety(p.x, i0);
					    double y1 = (i1==-1) ? p.y : gety(p.x, i1);
					    return (y0<y1) ? -1 : (y0==y1) ? 0 : 1;
					}});
	if (i>=0) return false; // point lies on a line segment
	i = -i - 1;
	return (i%2 == 1);
    }

}
