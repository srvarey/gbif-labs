package org.gbif.occurrence.spatial.polygon;

public class Point {
    public double x, y;
    String name;
    public Point(double xx, double yy) { x=xx; y=yy; name=null; }
    public Point(double xx, double yy, String nm) { x=xx; y=yy; name=nm; }
    public boolean equals(Point p) {
	//System.out.println(this + " " + x + " " + y + " " + p + " " + p.x + " " + p.y);
	return p.x==x && p.y==y;
    }
}