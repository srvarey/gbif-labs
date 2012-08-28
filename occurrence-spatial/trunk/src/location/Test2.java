package location;

// C:\Documents and Settings\phillips\tmp\gbifdata>ogr2ogr -f "MapInfo file" -dsco FORMAT=MIF -spat 5 46 15 55 converted4 WDPApol2009_1/WDPApol2009_1

// [phillips@cheetah ...gbif/occurrence]$ zcat allData.txt.gz | awk -F "\t" '$11 ~ /[0-9]+/ {if ($11 > 46 && $11 < 55 && $12 > 5 && $12 < 15 && $12 ~ /[0-9]*/) print NR, $11, $12}' > germany3.txt


// to do:  
// read in all points, partition into .1deg cells
// count how many in each PA
// give to Javier / Tim
 

import java.util.*;
import miftools.*;
import java.io.*;

class Test2 {
    long lasttime;
    double minx=5, maxx=15, miny=46, maxy=55;

    public static void main(String[] args) {
	try {
	    new Test2().go(args);
	} catch (IOException e) {
	    System.out.println(e.toString());
	    System.exit(1);
	}
    }

    Point[] ps2points(PointSeq ps) {
	Point[] p = new Point[ps.numVertices()];
	for (int j=0; j<p.length; j++)
	    p[j] = new Point(ps.getX(j), ps.getY(j));
	return p;
    }

    Polygon ps2polygon(PointSeq ps) {
	if (ps.siblings != null) return ps2polygon(ps.siblings);
	return new Polygon(ps2points(ps));
    }

    Polygon ps2polygon(PointSeq[] ps) {
	Point[][] p = new Point[ps.length][];
	for (int i=0; i<ps.length; i++)
	    p[i] = ps2points(ps[i]);
	return new Polygon(p);
    }

    int xbin(double x) { return (int) ((x-minx)*10); }
    int ybin(double y) { return (int) ((y-miny)*10); }

    void go(String[] args) throws IOException {
	String polygonfile = "WDPApol2009.mif.gz";
	String pointfile = "pointsx5to15y46to55.csv";
	start();
	MILayer mpolygons = new MIFIO().readMIF(polygonfile);
	time("Loaded " + mpolygons.size() + " polygons");
	ArrayList<Polygon> polygons = new ArrayList();
	ArrayList<String> parknames = new ArrayList();
	int nmpolygons = mpolygons.size(); 
	final ArrayList<Point> points[][] = new ArrayList[xbin(maxx)+1][ybin(maxy)+1];
	for (int i=0; i<points.length; i++)
	    for (int j=0; j<points[i].length; j++)
		points[i][j] = new ArrayList<Point>();
	CsvOnePass csv = new CsvOnePass(pointfile);
	csv.apply(csv.new Applier() {
		public void process() {
		    double xx = getDouble(2), yy = getDouble(1);
		    int x = xbin(xx);
		    int y = ybin(yy);
		    if (x>=0 && y>=0 && x<points.length && y<points[0].length)
			points[x][y].add(new Point(xx, yy, get(0)));
		}});
	int npoints=0;
	for (int i=0; i<points.length; i++)
	    for (int j=0; j<points[i].length; j++)
		npoints += points[i][j].size();
	time("Loaded " + npoints + " test points");
	int cnt=0;
	inbox = 0;
	for (int i=0; i<nmpolygons; i++)
	    if (mpolygons.get(i) instanceof PointSeq) {
		PointSeq ps = (PointSeq) mpolygons.get(i);
		if (ps.getType() != PointSeq.Region) continue;
		if (ps.siblings!=null && ps.siblings[0] != ps) continue;
		Polygon poly = ps2polygon(ps);
		cnt += findInside(poly, points, ps.getData(1));
	    }
	time("Found containments (" + cnt + " points out of " + npoints + ", did " + inbox + " point-in-polygon checks)");
    }

    int inbox;
    int findInside(Polygon poly, ArrayList<Point>[][] points, String name) {
	int cnt=0;
	for (int x=xbin(poly.minx); x<=xbin(poly.maxx); x++) {
	    if (x<0||x>=points.length) continue;
	    for (int y=ybin(poly.miny); y<=ybin(poly.maxy); y++) {
		if (y<0||y>=points[x].length) continue;
		for (Point p: points[x][y]) {
		    inbox++;
		    if (poly.contains(p)) 
			cnt++;
		}
	    }
	}
	System.out.println(name + " " + cnt);
	return cnt;
    }

    void start() { lasttime = System.currentTimeMillis(); }
    void time(String s) { 
	long t = System.currentTimeMillis();
	System.out.println(s + " in " + (t-lasttime)/1000.0 + " seconds");
	lasttime = t;
    }

}
