/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package markingmenus;


import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class Bezier{
    char curveType;
    ArrayList<Point2D.Double> curvePoints = new  ArrayList<Point2D.Double>();
    Bezier(char type)
    {
        curveType = type;
        switch(type)
        {
            case 's':
            case 'S':
                Point[] straightPoints = {new Point(0,0), new Point(0,-45), new Point(0,-90), new Point(0,-135)};
                buildCurve(straightPoints);
                break;
            case 'p':
            case 'P':
                Point[] pigtailPoints = {new Point(0,0), new Point(0,-172), new Point(61,-64), new Point(4,-53)};
                buildCurve(pigtailPoints);
                break;
            case 'c':
            case 'C':
                Point[] cuspedPoints = {new Point(0,0),new Point(0,-86), new Point(0,-142), new Point(38,-82)};
                buildCurve(cuspedPoints);
                break;
            case 'b':
            case 'B':
                Point[] bentPoints = {new Point(0,0), new Point(0,-94), new Point(0,-117), new Point(28,-117)};
                buildCurve(bentPoints);
                break;
        }
        /*Bent[0] = new Point(161,182);
        Bent[1] = new Point(161,88);
        Bent[2] = new Point(161,57);
        Bent[3] = new Point(189,61);
        Cusped[0] = new Point(161,196);
        Cusped[1] = new Point(161,96);
        Cusped[2] = new Point(161,40);
        Cusped[3] = new Point(199,112);
        Pigtail[0] = new Point(161,196);
        Pigtail[1] = new Point(161,2);
        Pigtail[2] = new Point(222,118);
        Pigtail[3] = new Point(165,129);
      */  
    }
    public Point[] getPointInt(double angle,boolean reflection)
    {
        Point[] IntegerPoints = new Point[curvePoints.size()];
        AffineTransform AF = new AffineTransform();
        AF.setToRotation(angle);
        for(int i =0;i<curvePoints.size();i++)
        {
            IntegerPoints[i] = new Point((int)Math.round(curvePoints.get(i).x),(int)Math.round(curvePoints.get(i).y));
            if(reflection == true)
                IntegerPoints[i].x = -1*IntegerPoints[i].x;
            AF.transform(IntegerPoints[i], IntegerPoints[i]);
        }
        return IntegerPoints;
    }
    private void buildCurve(Point[] controlPoints) 
    {
        
        double k = .025;	//time step value for the curve
        //Calculate bezier curve
        double x,y;
        for(double t=k;t<=1+k;t+=k)
        {
        //use Berstein polynomials (copied from http://www.math.ucla.edu/~baker/java/hoefer/Beziersrc.htm)
            x = (controlPoints[0].x+t*(-controlPoints[0].x*3+t*(3*controlPoints[0].x-
            controlPoints[0].x*t)))+t*(3*controlPoints[1].x+t*(-6*controlPoints[1].x+
            controlPoints[1].x*3*t))+t*t*(controlPoints[2].x*3-controlPoints[2].x*3*t)+
            controlPoints[3].x*t*t*t;
            y =(controlPoints[0].y+t*(-controlPoints[0].y*3+t*(3*controlPoints[0].y-
            controlPoints[0].y*t)))+t*(3*controlPoints[1].y+t*(-6*controlPoints[1].y+
            controlPoints[1].y*3*t))+t*t*(controlPoints[2].y*3-controlPoints[2].y*3*t)+
            controlPoints[3].y*t*t*t;
            curvePoints.add(new Point2D.Double(x,y));
        }
    }
    public void shiftCurve(Point center) 
    {
        for(int i =0;i<curvePoints.size();i++)
        {
            curvePoints.get(i).x = curvePoints.get(i).x + center.x;
            curvePoints.get(i).y = curvePoints.get(i).y + center.y;
        }
    }
}

