/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package markingmenus;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Gesture {
    Bezier curve;
    int ID,location,position;
    boolean reflection;
    double angle; //in radians
    ArrayList<Point2D.Double> gesturePoints;
    JLabel itemLabel;
    //Locations
    // Left_UP=0, UP_Center=1, Right_UP=2, Right_Center=3, Right_Down=4,Down_Cente=5, Left_Down=6, Left_Center=7
    Gesture(Bezier B, String name, int id, int locationID,int pos)
    {
        position = pos;
        location = locationID;
        curve = B;
        ID = id;
        angle = Math.toRadians(45*(ID/7));
        itemLabel = new JLabel(name, JLabel.CENTER);
        if(ID%7 ==0 || ID%7 ==1 || ID%7 ==2)
            reflection = true;
        else
            reflection = false;
    }
    public void drawGesture(JPanel Obj,Graphics g,Point center,boolean feedback,int offset)
    {
         g.setColor(Color.BLACK); 
        ((Graphics2D)g).rotate(angle,center.x,center.y);
        g.drawLine(center.x,center.y,center.x,center.y-offset-12);
        g.drawPolyline(getXCoordinates(center.x), getYCoordinates(center.y-offset),getSize());
        ((Graphics2D)g).rotate(-1*angle,center.x,center.y);
        if(feedback==true)
            g.setColor(new Color(57,46,207));
         else
             g.setColor(Color.WHITE);
        Rectangle rect = itemLabel.getBounds();
        g.draw3DRect(rect.x,rect.y, rect.width, rect.height, true);
        g.fill3DRect(rect.x,rect.y, rect.width, rect.height, true);
        if(feedback==true)
            itemLabel.setForeground(Color.WHITE);
         else
            itemLabel.setForeground(Color.BLACK);    
        Obj.add(itemLabel);
    }
    public int getSize()
    {
        return curve.curvePoints.size();
    }
    public Point getRectPoint(Rectangle rect) 
    {
        //int[] Location = {2,2,4,5,6,0,0  ,2,2,4,5,6,0,0   ,4,4,4,7,2,2,2   ,6,6,6,1,2,4,4   , 4,4,2,1,0,6,6,   
       // 6,6,0,1,2,4,4  , 0,0,0,3,6,6,6 ,  2,2,4,5,6,6,0};
        //Locations
        // Left_UP=0, UP_Center=1, Right_UP=2, Right_Center=3, Right_Down=4,Down_Cente=5, Left_Down=6, Left_Center=7
        Point P = new Point();
        switch(location)
        {
            case 0:
                P.x = rect.x;
                P.y = rect.y;
                break;
            case 1:
                P.x = rect.x - rect.width/2;
                P.y = rect.y;
                break;
            case 2:
                P.x = rect.x - rect.width;
                P.y = rect.y;
                break;
            case 3:
                P.x = rect.x - rect.width;
                P.y = rect.y - rect.height/2;
                break;
            case 4:
                P.x = rect.x - rect.width;
                P.y = rect.y - rect.height;
                break;
            case 5:
                P.x = rect.x - rect.width/2;
                P.y = rect.y - rect.height;
                break;
            case 6:
                P.x = rect.x;
                P.y = rect.y - rect.height;
                break;
            case 7:
                P.x = rect.x;
                P.y = rect.y - rect.height/2;
                break;
        }
        return P;
    }
    public void setLabelBounds(Point center,int height, int width,int offset) 
    {
        Rectangle rect = new Rectangle();
        rect.width = width;
        rect.height = height;
        AffineTransform AF = new AffineTransform();
        AF.rotate(angle,center.x,center.y);
        Point2D.Double pt = new Point2D.Double();
        int[] X = this.getXCoordinates(center.x);
        int[] Y = this.getYCoordinates(center.y-offset);
        pt.x = X[X.length-1];
        pt.y = Y[Y.length-1];
        
        if(position!=-1 )
        {
            pt.x = X[X.length-position-1];
            pt.y = Y[Y.length-position-1];
        }
        AF.transform(pt, pt);
        rect.x = (int)Math.round(pt.x);
        rect.y = (int)Math.round(pt.y);
        Point pt1 = getRectPoint(rect);
        rect.x = pt1.x;
        rect.y = pt1.y;
        itemLabel.setBounds(rect);
        
    }
    
    public int[] getXCoordinates(int translation) 
    {
        int[] coordinates = new int[curve.curvePoints.size()];
        for(int i=0; i<curve.curvePoints.size();i++)
        {
            if(reflection==true)//reflect along y-axis
            {
                coordinates[i] = (int)Math.round(2*curve.curvePoints.get(0).x - curve.curvePoints.get(i).x);
            }
            else
            {
                coordinates[i] = (int)Math.round(curve.curvePoints.get(i).x);
            }
            coordinates[i] = coordinates[i] + translation;
            
        }
        return coordinates;
    }
    public int[] getYCoordinates(int translation) 
    {
        int[] coordinates = new int[curve.curvePoints.size()];
        for(int i=0; i<curve.curvePoints.size();i++)
            coordinates[i] = (int)Math.round(curve.curvePoints.get(i).y) + translation;
        return coordinates;
    }
 	
}
