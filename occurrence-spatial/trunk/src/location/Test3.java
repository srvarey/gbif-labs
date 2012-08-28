package location;

class Test3 {
    static Polygon p;
    static long lasttime;

    public static void main(String[] args) {

	// Two concentric circles
	int nv = 10000, np=500;
	start();
	Point[][] circles = new Point[2][nv+1];
	for (int rdiv=1; rdiv<3; rdiv++) {
	    for (int i=0; i<=nv; i++)
		circles[rdiv-1][i] = new Point(Math.sin(2*Math.PI*i/(double)nv)/rdiv, Math.cos(2*Math.PI*i/(double)nv)/rdiv);
	    // ensure first vertex matches last, to avoid floating point error
	    circles[rdiv-1][nv] = circles[rdiv-1][0];  
	}
	p = new Polygon(circles);
	time("Made polygon");

	int[] count = new int[2];
	for (int i=-np; i<=np; i++)
	    for (int j=-np; j<=np; j++)
		count[p.contains(new Point(i/(double) np, j/(double) np)) ? 1 : 0]++;
	report(count);

	// A zigzag that makes the slab decomposition use quadratic space
	nv = 100;
	np = 1000000;
        Point[] pp = new Point[nv*2+2];
	for (int i=0; i<nv; i++) {
	    pp[i*2] = new Point(-i, i);
	    pp[i*2+1] = new Point(i+1, i+0.5);
	}
	pp[nv*2] = new Point(nv, 0);
	pp[nv*2+1] = pp[0];
	p = new Polygon(pp);
	time("Made polygon");
	count[0] = count[1] = 0;
	for (int i=0; i<np; i++) {
	    double x = 0.1, y = nv * i / (double) np;
	    count[p.contains(new Point(x,y)) ? 1 : 0]++;
	}
	report(count);
    }

    static void report(int[] count) {
	System.out.println(count[0] + " " + count[1] + " " + count[1] / (double) (count[0] + count[1]));
	time("Tested points");
    }

    static void start() { lasttime = System.currentTimeMillis(); }
    static void time(String s) { 
	long t = System.currentTimeMillis();
	System.out.println(s + " " + (t-lasttime)/1000.0);
	lasttime = t;
    }

}

/*
	   p = new Polygon(new Point[] {
		new Point(0,0), new Point(0.05,1.1), new Point(1.2,1.3), new Point(1.4,-0.2), new Point(0,0) });
	   test(.5, .5);
	   test(2,3);
	   test(.5, 1.5);
	   test(.5, -1.5);
	   test(1.5, .5);
	   test(-1.5, .5);
	}

	static void test(double x, double y) {
	   System.out.println(x + " " + y + " " + p.contains(new Point(x,y)));
	}
}
*/
