/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package markingmenus;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

public abstract class MenusPanel extends JPanel implements MouseListener,MouseMotionListener 
{
     /**
     * @param args the command line arguments
     * 
     */
    private Gesture[] menuItems;
    private  Point  center, currPos;
    private int mode;// 0-no mode, 1- novice mode, 2-expert mode, 3-feedback mode
    private ArrayList<Point> inputGesture;
    private Bezier bentCurve,cuspedCurve,pigtailCurve, straight;
    private  int radius,noOfItems, selecteddirection,selectedgesture;
    private int[] idMap,offset;
    private  Timer feedbackTimer, mouseTimer;
    private JLabel[] groupLabels;
    private  int[] Location = {2,4,3,5,7,6,0  ,4,3,4,6,6,6,0   ,4,4,6,7,0,2,2   ,6,0,0,0,2,3,4   , 6,0,7,1,3,2,4, //4,4,2,1,0,6,6,   
    6,7,0,2,2,2,4  , 0,0,2,3,4,6,6 ,  2,4,4,4,6,7,0};
    private int[] Position = {9,-1,-1,-1,-1,-1,9  ,2,-1,-1,-1,-1,-1,4, 2,-1,-1,-1,-1,-1,2,  4,-1,-1,-1,-1,-1,8, 
    9,-1,-1,-1,-1,-1,9,  8,-1,-1,-1,-1,-1,4,  2,-1,-1,-1,-1,-1,2,  4,-1,-1,-1,-1,-1,8 };
    // Left_UP=0, UP_Center=1, Right_UP=2, Right_Center=3, Right_Down=4,Down_Cente=5, Left_Down=6, Left_Center=7
    protected MenusPanel(ArrayList<String> itemNames,ArrayList<Integer> itemIDs,String[] groups )
    {
        setLayout(null);
        idMap = new int[56]; //number of menu items 
        offset = new int[8]; //number of directions 
        for (int i =0;i<56;i++)
            idMap[i] = -1;
        inputGesture = new ArrayList<Point>();
        noOfItems = itemNames.size();
        menuItems = new Gesture[noOfItems];
        straight = new Bezier('s');
        pigtailCurve = new Bezier('p');
        cuspedCurve = new Bezier('c');
        bentCurve = new Bezier('b');
        for (int i =0;i<noOfItems;i++)
        {
            switch(itemIDs.get(i)%7)
            {
                case 0:
                case 6:
                    menuItems[i] = new Gesture(pigtailCurve, itemNames.get(i),itemIDs.get(i),Location[itemIDs.get(i)],Position[itemIDs.get(i)]);
                    break;
                case 1:
                case 5:
                    menuItems[i] = new Gesture(cuspedCurve, itemNames.get(i),itemIDs.get(i),Location[itemIDs.get(i)],Position[itemIDs.get(i)]);
                    break;
                case 2:
                case 4:
                    menuItems[i] = new Gesture(bentCurve, itemNames.get(i),itemIDs.get(i),Location[itemIDs.get(i)],Position[itemIDs.get(i)]);
                    break;
                case 3:
                    menuItems[i] = new Gesture(straight, itemNames.get(i),itemIDs.get(i),Location[itemIDs.get(i)],Position[itemIDs.get(i)]);
                    break;
            }
            idMap[itemIDs.get(i)] = i;
        }
        groupLabels = new JLabel[groups.length];
        for(int i=0;i<8;i++)
            groupLabels[i] = new JLabel(groups[i],JLabel.CENTER);
        selecteddirection = -1;
        selectedgesture = -1;
        radius = 10;
        mode = 2; //set to expert mode
        center = new Point(-1,-1);
        currPos = new Point();
        feedbackTimer = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mode = 0;
                repaint();
                feedbackTimer.stop();
            }
        });
        feedbackTimer.setInitialDelay(400);
        mouseTimer = new Timer(600, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                 if(((currPos.x - center.x)*(currPos.x - center.x) + (currPos.y - center.y)*(currPos.y - center.y))<radius*radius)
                     mode = 1;//novice mode set
                  repaint();
                mouseTimer.stop();
            }
        });
        mouseTimer.setInitialDelay(400);
        setLayout(null);
        addMouseListener(this);
        addMouseMotionListener(this); 
    }
    
   public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        if(mode==0)
        {
            this.removeAll();
            mode = 2;
        }
        if(mode==1)//Novice
        {
            g.setColor(Color.black);
            ((Graphics2D)g).setStroke(new BasicStroke(1));
            for(int i = 0;i<8;i++)
            {
                boolean flag = false;
                for(int j = 0;j<7;j++)
                {   
                    if(idMap[i*7+j]!=-1)
                    {
                        flag = true;
                        break;
                    }
                }
                if(flag==true)
                {
                //    ((Graphics2D)g).rotate(Math.toRadians(i*45),center.x,center.y);
                //    g.drawLine(center.x,center.y,center.x,(int)straight.curvePoints.get(straight.curvePoints.size()-1).y-offset[i]+ center.y);
              //      ((Graphics2D)g).rotate(-1*Math.toRadians(i*45),center.x,center.y);
                    this.add(groupLabels[i]);
                }
            }
            if(selecteddirection!=-1)
            {
                for(int j = 0;j<7;j++)
                {   
                    if(idMap[selecteddirection*7+j]!=-1)
                    {
                        if(menuItems[idMap[selecteddirection*7+j]].ID==(selecteddirection*7+selectedgesture))
                                menuItems[idMap[selecteddirection*7+j]].drawGesture(this, g, center,true,offset[selecteddirection]);
                         else
                                menuItems[idMap[selecteddirection*7+j]].drawGesture(this, g, center,false,offset[selecteddirection]);    
                    }
                }
            }
            validate();
            g.setColor(Color.RED);
            ((Graphics2D)g).setStroke(new BasicStroke(3));
            for (int k = 0;k<inputGesture.size()-1;k++)
                g.drawLine(inputGesture.get(k).x,inputGesture.get(k).y,inputGesture.get(k+1).x,inputGesture.get(k+1).y);
            g.setColor(Color.WHITE);
            g.fillOval(center.x-radius,center.y-radius, 2*radius,2*radius);
            g.setColor(Color.black);
            ((Graphics2D)g).setStroke(new BasicStroke(2));
            g.drawOval(center.x-radius,center.y-radius, 2*radius,2*radius);
        }
        if(mode==2)//Expert
        {
            g.setColor(Color.RED);
            ((Graphics2D)g).setStroke(new BasicStroke(3));
            for (int k = 0;k<inputGesture.size()-1;k++)
                g.drawLine(inputGesture.get(k).x,inputGesture.get(k).y,inputGesture.get(k+1).x,inputGesture.get(k+1).y);
             if(!inputGesture.isEmpty())
            {
                g.setColor(Color.WHITE);
                g.fillOval(center.x-radius,center.y-radius, 2*radius,2*radius);
                g.setColor(Color.black);
                ((Graphics2D)g).setStroke(new BasicStroke(2));
                g.drawOval(center.x-radius,center.y-radius, 2*radius,2*radius);
            }
        }
        if(mode==3)//Feedback
        {
            if(selecteddirection!=-1 && selectedgesture!=-1)
            {
                for(int i=0;i<menuItems.length;i++)
                {
                    if(menuItems[i].ID==(selecteddirection*7+selectedgesture))
                    {
                        menuItems[i].drawGesture(this, g, center,true,offset[selecteddirection]);
                        g.setColor(Color.WHITE);
                        g.fillOval(center.x-radius,center.y-radius, 2*radius,2*radius);
                        g.setColor(Color.black);
                        ((Graphics2D)g).setStroke(new BasicStroke(2));
                        g.drawOval(center.x-radius,center.y-radius, 2*radius,2*radius);
                        break;
                    }
                }
                validate();
                feedbackTimer.start();
                decideCallback();
                selecteddirection=-1;
                selectedgesture=-1;
            }
            else
            {
               mode = 2;
               this.removeAll();
            }
        }
    }
 
   public void mousePressed(MouseEvent e) 
    {
        //Invoked when a mouse button has been pressed on a component.
        center = e.getPoint();
        inputGesture.add(center);
        currPos = center;
        int Max = Integer.MIN_VALUE,index=-1;
        int temp;
        //Finding the max width of button
        for(int i = 0;i<noOfItems;i++)
        {
            temp = this.getGraphics().getFontMetrics().stringWidth(menuItems[i].itemLabel.getText());
            if(Max<temp)
            {
             Max = temp;
             index = i;
            }
        }
        
        //Setting the button's bound
        int height = this.getGraphics().getFontMetrics().getHeight();
        for(int i = 0;i<noOfItems;i++)
            menuItems[i].setLabelBounds(center, height,Max+10,0);
        //Calculating the offset by which to expand the menu so that there is no overlap between buttons
        Rectangle[] boundaries = new Rectangle[8];
        int minX=0,minY,maxX,maxY;
        for(int i = 0;i<8;i++)
        {
            minX = Integer.MAX_VALUE; 
            minY = Integer.MAX_VALUE;
            maxX = Integer.MIN_VALUE;
            maxY = Integer.MIN_VALUE;
            for(int j = 0;j<7;j++)
            {
                if(idMap[7*i+j]!=-1)
                {
                    if(minX>menuItems[idMap[7*i+j]].itemLabel.getX())
                        minX = menuItems[idMap[7*i+j]].itemLabel.getX();
                    if(maxX<menuItems[idMap[7*i+j]].itemLabel.getX())
                        maxX = menuItems[idMap[7*i+j]].itemLabel.getX();
                    if(minY>menuItems[idMap[7*i+j]].itemLabel.getY())
                        minY = menuItems[idMap[7*i+j]].itemLabel.getY();
                    if(maxY<menuItems[idMap[7*i+j]].itemLabel.getY())
                        maxY = menuItems[idMap[7*i+j]].itemLabel.getY();
                }
            }
            boundaries[i] = new Rectangle(minX,minY,(maxX-minX+Max+10),(maxY-minY+height));
            offset[i] = 0;
        }
        int MinSep = 20;
        if(boundaries[7].x!=Integer.MAX_VALUE) //NW
        {
            if((center.x - (boundaries[7].x + boundaries[7].width))<MinSep)
                offset[7] = MinSep - (center.x - (boundaries[7].x + boundaries[7].width));
            if((center.y - (boundaries[7].y + boundaries[7].height))<MinSep)
                offset[7] = Math.max(offset[7],MinSep - (center.y -(boundaries[7].y + boundaries[7].height)));
            boundaries[7].x = boundaries[7].x - offset[7];
            boundaries[7].y = boundaries[7].y - offset[7];
            offset[7] = (int)(offset[7]* Math.sqrt(2));
        }
        //NE
        if(boundaries[1].x!=Integer.MAX_VALUE)    
        {
            if((boundaries[1].x - center.x)<MinSep)
                offset[1] = MinSep - (boundaries[1].x - center.x);
            if((center.y - (boundaries[1].y + boundaries[1].height))<MinSep)
                offset[1] = Math.max(offset[1],MinSep - (center.y - (boundaries[1].y + boundaries[1].height)));
            boundaries[1].x = boundaries[1].x + offset[1];
            boundaries[1].y = boundaries[1].y - offset[1];
            offset[1] = (int)(offset[1]* Math.sqrt(2));
        }
        //SE
        if(boundaries[3].x!=Integer.MAX_VALUE)    
        {
            if((boundaries[3].x - center.x)<MinSep)
                offset[3] = MinSep - (boundaries[3].x - center.x);
            if((boundaries[3].y - center.y)<MinSep)
                offset[3] = Math.max(offset[3],MinSep - (boundaries[3].y - center.y));
            boundaries[3].x = boundaries[3].x + offset[3];
            boundaries[3].y = boundaries[3].y + offset[3];
            offset[3] = (int)(offset[3]* Math.sqrt(2));
        }
        //SW
        if(boundaries[5].x!=Integer.MAX_VALUE)    
        {
            if((center.x - (boundaries[5].x + boundaries[5].width))<MinSep)
                offset[5] = MinSep - (center.x - (boundaries[5].x + boundaries[5].width));
            if((boundaries[5].y - center.y)<MinSep)
                offset[5] = Math.max(offset[5],MinSep - (boundaries[5].y - center.y));
            boundaries[5].x = boundaries[5].x - offset[5];
            boundaries[5].y = boundaries[5].y + offset[5];
            offset[5] = (int)(offset[5]* Math.sqrt(2));
        }
        //E
         if(boundaries[2].x!=Integer.MAX_VALUE)    
        {
            if((boundaries[2].x - center.x) < MinSep)
                offset[2] = 2*MinSep - (boundaries[2].x - center.x);
            if(boundaries[3].x!=Integer.MAX_VALUE && boundaries[2].intersects(boundaries[3]))
                offset[2] = Math.max(offset[2],(boundaries[3].x +  boundaries[3].width + MinSep) - boundaries[2].x);
            if(boundaries[1].x!=Integer.MAX_VALUE && boundaries[2].intersects(boundaries[1]))
                offset[2] = Math.max(offset[2],(boundaries[1].x +  boundaries[1].width + MinSep) - boundaries[2].x);
            boundaries[2].x = boundaries[2].x + offset[2];
        }
         //W
        if(boundaries[6].x!=Integer.MAX_VALUE)    
        {
            if((center.x - (boundaries[6].x + boundaries[6].width)) < MinSep)
                offset[6] = 2*MinSep - (center.x - (boundaries[6].x + boundaries[6].width));
            if(boundaries[7].x!=Integer.MAX_VALUE && boundaries[6].intersects(boundaries[7]))
                offset[6] = Math.max(offset[6],boundaries[6].x - (boundaries[7].x -  boundaries[6].width - MinSep));
            if(boundaries[5].x!=Integer.MAX_VALUE && boundaries[6].intersects(boundaries[5]))
                offset[6] = Math.max(offset[6],boundaries[6].x - (boundaries[5].x -  boundaries[6].width - MinSep));
            boundaries[6].x = boundaries[6].x - offset[6];
        }
        offset[0] = Math.min(Math.min(Math.min(Math.min(Math.min(offset[1],offset[2]),offset[3]),offset[5]),offset[6]),offset[7]);
        offset[4] = offset[0];
        
         for(int i = 0;i<noOfItems;i++)
            menuItems[i].setLabelBounds(center, height,Max+10,offset[menuItems[i].ID/7]);
         Point pp = new Point();
         int w;
         for(int i = 0;i<8;i++)
         {
             pp.x = (int)straight.curvePoints.get(straight.curvePoints.size()-1).x+center.x;
             pp.y = (int)straight.curvePoints.get(straight.curvePoints.size()-1).y-offset[i]+ center.y;
             AffineTransform AF = new AffineTransform();
             AF.rotate(Math.toRadians(45*i),center.x,center.y);
             w = getGraphics().getFontMetrics().stringWidth(groupLabels[i].getText()) + 10;
              AF.transform(pp, pp);
             switch(i)
             {
                 case 0:
                     pp.y = pp.y - 2*height-1;
                     pp.x = pp.x - w/2;
                     break;
                 case 7:
                     pp.x = pp.x - w;
                 case 1:
                     pp.y = pp.y - 2*height-1;
                     break;
                 case 4:
                     pp.x = pp.x - w/2;
                     pp.y = pp.y + height;
                     break;
                 case 5:
                     pp.x = pp.x - w;
                 case 3:
                     pp.y = pp.y + height;
                     break;
                 case 2:
                     if(idMap[i*7+3]!=-1)
                         pp.x = pp.x + menuItems[idMap[i*7+3]].itemLabel.getWidth(); 
                     pp.y = pp.y - height/2;
                     break;
                 case 6:
                     pp.x = pp.x - w;
                      if(idMap[i*7+3]!=-1)
                         pp.x = pp.x - menuItems[idMap[i*7+3]].itemLabel.getWidth();     
                      pp.y = pp.y - height/2;
                     break;
                
             }
             groupLabels[i].setBounds(pp.x,pp.y, w , height);
         }
            
        mouseTimer.start();
    }
    public void mouseReleased(MouseEvent e) {
        if(mouseTimer.isRunning())
            mouseTimer.stop();
        Point[] nGest = inputGesture.toArray(new Point[inputGesture.size()]);
        if(nGest.length>2 && selecteddirection==-1 && selectedgesture==-1)
        {
            double[] distances = new double[7];
            selecteddirection = whichDirection();
            distances[0] = DTWDistance(normalize(reSampling(nGest,pigtailCurve.curvePoints.size()), pigtailCurve.getPointInt(Math.toRadians(45*selecteddirection),true)),normalize(pigtailCurve.getPointInt(Math.toRadians(45*selecteddirection),true),pigtailCurve.getPointInt(Math.toRadians(45*selecteddirection),true)));
        //   distances[0] = LinearDistance(reSampling(normalize(nGest, pigtailCurve.getPointInt(Math.toRadians(45*selecteddirection),true)),pigtailCurve.curvePoints.size()),normalize(pigtailCurve.getPointInt(Math.toRadians(45*selecteddirection),true),pigtailCurve.getPointInt(Math.toRadians(45*selecteddirection),true)));;
            distances[1] = DTWDistance(normalize(reSampling(nGest,cuspedCurve.curvePoints.size()), cuspedCurve.getPointInt(Math.toRadians(45*selecteddirection),true)),normalize(cuspedCurve.getPointInt(Math.toRadians(45*selecteddirection),true),cuspedCurve.getPointInt(Math.toRadians(45*selecteddirection),true)));
       //    distances[1] = LinearDistance(reSampling(normalize(nGest, cuspedCurve.getPointInt(Math.toRadians(45*selecteddirection),true)),cuspedCurve.curvePoints.size()),normalize(cuspedCurve.getPointInt(Math.toRadians(45*selecteddirection),true),cuspedCurve.getPointInt(Math.toRadians(45*selecteddirection),true)));
            distances[2] = DTWDistance(normalize(reSampling(nGest,bentCurve.curvePoints.size()), bentCurve.getPointInt(Math.toRadians(45*selecteddirection),true)), normalize(bentCurve.getPointInt(Math.toRadians(45*selecteddirection),true),bentCurve.getPointInt(Math.toRadians(45*selecteddirection),true)));
        //   distances[2] = LinearDistance(reSampling(normalize(nGest, bentCurve.getPointInt(Math.toRadians(45*selecteddirection),true)),bentCurve.curvePoints.size()), normalize(bentCurve.getPointInt(Math.toRadians(45*selecteddirection),true),bentCurve.getPointInt(Math.toRadians(45*selecteddirection),true)));
            distances[3] = DTWDistance(normalize(reSampling(nGest,straight.curvePoints.size()), straight.getPointInt(Math.toRadians(45*selecteddirection),false)), normalize(straight.getPointInt(Math.toRadians(45*selecteddirection),false),straight.getPointInt(Math.toRadians(45*selecteddirection),false)));
        //   distances[3] = LinearDistance(reSampling(normalize(nGest, straight.getPointInt(Math.toRadians(45*selecteddirection),false)),straight.curvePoints.size()), reSampling(normalize(straight.getPointInt(Math.toRadians(45*selecteddirection),false),straight.getPointInt(Math.toRadians(45*selecteddirection),false)),straight.curvePoints.size()));
            distances[4] = DTWDistance(normalize(reSampling(nGest,bentCurve.curvePoints.size()), bentCurve.getPointInt(Math.toRadians(45*selecteddirection),false)), normalize(bentCurve.getPointInt(Math.toRadians(45*selecteddirection),false),bentCurve.getPointInt(Math.toRadians(45*selecteddirection),false)));
        //   distances[4] = LinearDistance(reSampling(normalize(nGest, bentCurve.getPointInt(Math.toRadians(45*selecteddirection),false)),bentCurve.curvePoints.size()), normalize(bentCurve.getPointInt(Math.toRadians(45*selecteddirection),false),bentCurve.getPointInt(Math.toRadians(45*selecteddirection),false)));
            distances[5] = DTWDistance(normalize(reSampling(nGest,cuspedCurve.curvePoints.size()), cuspedCurve.getPointInt(Math.toRadians(45*selecteddirection),false)),normalize(cuspedCurve.getPointInt(Math.toRadians(45*selecteddirection),false),cuspedCurve.getPointInt(Math.toRadians(45*selecteddirection),false)));
         //  distances[5] = LinearDistance(reSampling(normalize(nGest, cuspedCurve.getPointInt(Math.toRadians(45*selecteddirection),false)),cuspedCurve.curvePoints.size()),normalize(cuspedCurve.getPointInt(Math.toRadians(45*selecteddirection),false),cuspedCurve.getPointInt(Math.toRadians(45*selecteddirection),false)));
            distances[6] = DTWDistance(normalize(reSampling(nGest,pigtailCurve.curvePoints.size()), pigtailCurve.getPointInt(Math.toRadians(45*selecteddirection),false)),normalize(pigtailCurve.getPointInt(Math.toRadians(45*selecteddirection),false),pigtailCurve.getPointInt(Math.toRadians(45*selecteddirection),false)));
         //  distances[6] = LinearDistance(reSampling(normalize(nGest, pigtailCurve.getPointInt(Math.toRadians(45*selecteddirection),false)),pigtailCurve.curvePoints.size()),normalize(pigtailCurve.getPointInt(Math.toRadians(45*selecteddirection),false),pigtailCurve.getPointInt(Math.toRadians(45*selecteddirection),false)));
        double min = Double.POSITIVE_INFINITY;
        int gestureId = -1;
        for (int i = 0;i<distances.length;i++)
        {
            if(min>distances[i])
            {
                min = distances[i];
                gestureId = i;
            }
        }
        selectedgesture = gestureId;
        }
        mode = 3;
        inputGesture.removeAll(inputGesture);
        this.removeAll();
        repaint();
    }
    private double LinearDistance(Point[] curveA,Point[] curveB)
    {
        double distance =0;
        for(int i = 0;i<curveA.length;i++)
        {
            distance = distance + calculateDist(curveA[i], curveB[i]);
        }
        return distance/curveA.length;
    }
    public void mouseDragged(MouseEvent e) {
       currPos = e.getPoint();

       if(mode==1||mode==2)
       {
            if(((currPos.x - center.x)*(currPos.x - center.x) + (currPos.y - center.y)*(currPos.y - center.y))>=radius*radius)
            {
              inputGesture.add(currPos); 
              if(inputGesture.size()==8 && mode==1)
              {
                  selecteddirection = whichDirection();
              }
              if(inputGesture.size()>8 && mode==1)
              {
                  Point[] nGest = inputGesture.toArray(new Point[inputGesture.size()]);
                  double[] distances = new double[7];
                   distances[0] = DTWDistance(normalize(reSampling(nGest,pigtailCurve.curvePoints.size()), pigtailCurve.getPointInt(Math.toRadians(45*selecteddirection),true)),normalize(pigtailCurve.getPointInt(Math.toRadians(45*selecteddirection),true),pigtailCurve.getPointInt(Math.toRadians(45*selecteddirection),true)));
                //   distances[0] = LinearDistance(reSampling(normalize(nGest, pigtailCurve.getPointInt(Math.toRadians(45*selecteddirection),true)),pigtailCurve.curvePoints.size()),normalize(pigtailCurve.getPointInt(Math.toRadians(45*selecteddirection),true),pigtailCurve.getPointInt(Math.toRadians(45*selecteddirection),true)));;
                    distances[1] = DTWDistance(normalize(reSampling(nGest,cuspedCurve.curvePoints.size()), cuspedCurve.getPointInt(Math.toRadians(45*selecteddirection),true)),normalize(cuspedCurve.getPointInt(Math.toRadians(45*selecteddirection),true),cuspedCurve.getPointInt(Math.toRadians(45*selecteddirection),true)));
               //    distances[1] = LinearDistance(reSampling(normalize(nGest, cuspedCurve.getPointInt(Math.toRadians(45*selecteddirection),true)),cuspedCurve.curvePoints.size()),normalize(cuspedCurve.getPointInt(Math.toRadians(45*selecteddirection),true),cuspedCurve.getPointInt(Math.toRadians(45*selecteddirection),true)));
                    distances[2] = DTWDistance(normalize(reSampling(nGest,bentCurve.curvePoints.size()), bentCurve.getPointInt(Math.toRadians(45*selecteddirection),true)), normalize(bentCurve.getPointInt(Math.toRadians(45*selecteddirection),true),bentCurve.getPointInt(Math.toRadians(45*selecteddirection),true)));
                //   distances[2] = LinearDistance(reSampling(normalize(nGest, bentCurve.getPointInt(Math.toRadians(45*selecteddirection),true)),bentCurve.curvePoints.size()), normalize(bentCurve.getPointInt(Math.toRadians(45*selecteddirection),true),bentCurve.getPointInt(Math.toRadians(45*selecteddirection),true)));
                    distances[3] = DTWDistance(normalize(reSampling(nGest,straight.curvePoints.size()), straight.getPointInt(Math.toRadians(45*selecteddirection),false)), normalize(straight.getPointInt(Math.toRadians(45*selecteddirection),false),straight.getPointInt(Math.toRadians(45*selecteddirection),false)));
                //   distances[3] = LinearDistance(reSampling(normalize(nGest, straight.getPointInt(Math.toRadians(45*selecteddirection),false)),straight.curvePoints.size()), reSampling(normalize(straight.getPointInt(Math.toRadians(45*selecteddirection),false),straight.getPointInt(Math.toRadians(45*selecteddirection),false)),straight.curvePoints.size()));
                    distances[4] = DTWDistance(normalize(reSampling(nGest,bentCurve.curvePoints.size()), bentCurve.getPointInt(Math.toRadians(45*selecteddirection),false)), normalize(bentCurve.getPointInt(Math.toRadians(45*selecteddirection),false),bentCurve.getPointInt(Math.toRadians(45*selecteddirection),false)));
                //   distances[4] = LinearDistance(reSampling(normalize(nGest, bentCurve.getPointInt(Math.toRadians(45*selecteddirection),false)),bentCurve.curvePoints.size()), normalize(bentCurve.getPointInt(Math.toRadians(45*selecteddirection),false),bentCurve.getPointInt(Math.toRadians(45*selecteddirection),false)));
                    distances[5] = DTWDistance(normalize(reSampling(nGest,cuspedCurve.curvePoints.size()), cuspedCurve.getPointInt(Math.toRadians(45*selecteddirection),false)),normalize(cuspedCurve.getPointInt(Math.toRadians(45*selecteddirection),false),cuspedCurve.getPointInt(Math.toRadians(45*selecteddirection),false)));
                 //  distances[5] = LinearDistance(reSampling(normalize(nGest, cuspedCurve.getPointInt(Math.toRadians(45*selecteddirection),false)),cuspedCurve.curvePoints.size()),normalize(cuspedCurve.getPointInt(Math.toRadians(45*selecteddirection),false),cuspedCurve.getPointInt(Math.toRadians(45*selecteddirection),false)));
                    distances[6] = DTWDistance(normalize(reSampling(nGest,pigtailCurve.curvePoints.size()), pigtailCurve.getPointInt(Math.toRadians(45*selecteddirection),false)),normalize(pigtailCurve.getPointInt(Math.toRadians(45*selecteddirection),false),pigtailCurve.getPointInt(Math.toRadians(45*selecteddirection),false)));
                 //  distances[6] = LinearDistance(reSampling(normalize(nGest, pigtailCurve.getPointInt(Math.toRadians(45*selecteddirection),false)),pigtailCurve.curvePoints.size()),normalize(pigtailCurve.getPointInt(Math.toRadians(45*selecteddirection),false),pigtailCurve.getPointInt(Math.toRadians(45*selecteddirection),false)));
                double min = Double.POSITIVE_INFINITY;
                int gestureId = -1;
                for (int i = 0;i<distances.length;i++)
                {
                    if(min>distances[i])
                    {
                        min = distances[i];
                        gestureId = i;
                    }
                }
                selectedgesture = gestureId;
              }
              
            }
            else
            {
                if(mode==2)
                    mouseTimer.start();
                inputGesture.removeAll(inputGesture);
                selecteddirection = -1;
                selectedgesture = -1;
                inputGesture.add(center);             
               this.removeAll();               
               repaint();
            }
       }
       repaint();
    }
    public void mouseExited(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseClicked(MouseEvent e) {}
    public void mouseMoved(MouseEvent e) {}

 double DTWDistance(Point[] prototype,Point[] newGesture)
{

	double[][] PathMatrix = new double[prototype.length][newGesture.length];
	int[][] X = new int[prototype.length][newGesture.length], Y = new int[prototype.length][newGesture.length];
        double Min;
	for (int i = 0 ; i<prototype.length ; i++)
            for (int j = 0 ; j<newGesture.length ; j++)
                PathMatrix[i][j] = Integer.MAX_VALUE;
	for (int i = 0 ; i<prototype.length; i++)
	{
		for (int j = 0 ; j<newGesture.length; j++)
		{
			Min = 0;
			X[i][j] = 0;
			Y[i][j] = 0;
			if((i-1)>=0 && (j-1)>=0)
			{
				int k = findMin(PathMatrix[i-1][j],PathMatrix[i][j-1],PathMatrix[i-1][j-1]);
				switch(k)
				{
				case 1:
					Min = PathMatrix[i-1][j];
					X[i][j] = i-1;
					Y[i][j] = j;
					break;
				case 2:
					Min = PathMatrix[i][j-1];
					X[i][j] = i;
					Y[i][j] = j-1;
					break;
				case 3:
					Min = PathMatrix[i-1][j-1];
					X[i][j] = i-1;
					Y[i][j] = j-1;
					break;
				}
			}
			else
			{
				if((i-1)>=0)
				{
					Min = PathMatrix[i-1][j];
					X[i][j] = i-1;
					Y[i][j] = j;
				}
				else
				{
					if((j-1)>=0)
					{
						Min = PathMatrix[i][j-1];
						X[i][j] = i;
						Y[i][j] = j-1;
					}
				}
			}

			PathMatrix[i][j] = calculateDist(prototype[i], newGesture[j])+ Min ;
		}
	}
	int i = prototype.length - 1;
	int j = newGesture.length - 1;
	int k = 0;
	do{
		i = X[i][j];
		j = Y[i][j];
		k++;
	}
	while(i != 0 || j != 0);
	Min = PathMatrix[prototype.length-1][newGesture.length-1];
	return Min/(double)k;
}
static int findMin(double a, double b, double c)
{
    double min = a;
    int index = 1;
    if (min > b){ min = b; index = 2;}
    if (min > c){ min = c; index = 3;}
    return index;
}
static double calculateDist(Point P1, Point P2)
{
	//return (8+abs(P1.cosa - P2.cosa)+abs(P1.dcosa - P2.dcosa)+abs(P1.sina - P2.sina)+abs(P1.dsina - P2.dsina))*sqrt((P1.x - P2.x)*(P1.x - P2.x) + (P1.y - P2.y)*(P1.y - P2.y));
       // System.out.println(P1.toString());
       // System.out.println(P2.toString());
	return Math.sqrt((P1.x - P2.x)*(P1.x - P2.x) + (P1.y - P2.y)*(P1.y - P2.y));
}
        static Point[] reverseOrder(Point[] curve)
        {
            Point[] C = new Point[curve.length];
            for (int i= 1;i<=curve.length;i++)
                C[i-1] = new Point(curve[curve.length-i].x,curve[curve.length-i].y);
            return C;
        }
        private static Point[] normalize(Point[] curve, Point[] prototype)
        {
            int x_min = prototype[0].x ,y_min = prototype[0].y ,x_max = prototype[0].x, y_max = prototype[0].y;
            //Find the minimum bounding box
            for(int i=1; i<prototype.length; i++)
            {
                x_min = Math.min(x_min,prototype[i].x);
                y_min = Math.min(y_min,prototype[i].y);
                x_max = Math.max(x_max,prototype[i].x);
                y_max = Math.max(y_max,prototype[i].y);
            }
            double width = x_max - x_min, height = y_max-y_min;
            x_min = curve[0].x;
            y_min = curve[0].y ;
            x_max = curve[0].x;
            y_max = curve[0].y;
            Point[] result = new Point[curve.length];
            result[0] = new Point(curve[0].x,curve[0].y);
            for(int i=1; i<curve.length; i++)
            {
                x_min = Math.min(x_min,curve[i].x);
                y_min = Math.min(y_min,curve[i].y);
                x_max = Math.max(x_max,curve[i].x);
                y_max = Math.max(y_max,curve[i].y);
                result[i] = new Point(curve[i].x, curve[i].y);
            }
            //normalize
            if(width==0)
                width = x_max-x_min;
            if(height==0)
               height = y_max-y_min;
            for(int i=0; i<result.length; i++)
            {
                if((x_max-x_min)!=0)
                    result[i].x = (int)Math.round((result[i].x-x_min)*width/(x_max-x_min));
                else
                    result[i].x = prototype[i].x;
                if((y_max-y_min)!=0)
                    result[i].y = (int)Math.round((result[i].y-y_min)*height/(y_max-y_min));      
                else
                    result[i].y = prototype[i].y;
            }
            return result;
        }
        private static Point[] removeConsecDouble(Point[] curve)
        {
            ArrayList<Point> result  = new ArrayList<Point>();
            result.add(curve[0]);
            for(int i = 0; i<curve.length-1;i++)
            {
                   if(curve[i].x!=curve[i+1].x || curve[i].y!=curve[i+1].y)
                       result.add(curve[i+1]);   
            }
            return result.toArray(new Point[result.size()]);
        }
        private static Point[] reSampling(Point[] curve, int noOfPoints)
        {
            ArrayList<Point> result  = new ArrayList<Point>();
            int curvelength = curve.length;
            ArrayList<Double> distances = new ArrayList<Double>();
            //distances calculation
            for(int j=0; j<curve.length-1; j++)
            {
                distances.add(calculateDist(curve[j],curve[j+1]));
                result.add(curve[j]);
            }
            result.add(curve[curve.length-1]);
            //upsampling
            while(curvelength<noOfPoints)
            {
                //find the longest segment
                int indLongest = -1;
                double lengthLongest = 0;
                for(int j=0; j<curvelength-1; j++)
                {
                    double dist = distances.get(j);
                    if(dist >lengthLongest)
                    {
                        indLongest=j;
                        lengthLongest = dist;
                    }
                }
                //split the longest segment
                Point newPoint = new Point((result.get(indLongest).x + result.get(indLongest+1).x)/2,(result.get(indLongest).y + result.get(indLongest+1).y)/2);
                result.add(indLongest+1,newPoint);
                //update distances
                distances.set(indLongest,calculateDist(result.get(indLongest),result.get(indLongest+1)));
                distances.add(indLongest+1, calculateDist(result.get(indLongest+1),result.get(indLongest+2)));
                curvelength++;
            }
            //downsampling
            while(curvelength>noOfPoints)
            {
                //find the shortest segment
                int indShortest=-1;
                double lengthShortest = Double.POSITIVE_INFINITY;
                for(int j=0; j<curvelength-2; j++)
                { //dont try to remove beginning and end points
                    double dist = distances.get(j);
                    if(dist<lengthShortest)
                    {
                        indShortest = j;
                        lengthShortest = dist;
                    }
               }

                //Remove point    
                result.remove(indShortest+1);
                //update distances
                distances.set(indShortest,calculateDist(result.get(indShortest),result.get(indShortest+1)));
                distances.remove(indShortest+1);
                curvelength--;
            }
            return result.toArray(new Point[result.size()]);
        }
        int whichDirection()
        {
            int[] votes = new int[8];
            //Counting votes for each direction starting with north
            for (int i=0;i<inputGesture.size();i++)
            {
                double x = inputGesture.get(i).x - center.x;
                double y = -1*(inputGesture.get(i).y - center.y);
                if( (y - Math.tan(Math.toRadians(-67.5))*x)>=0 && (y - Math.tan(Math.toRadians(67.5))*x)>0 )//north
                    votes[0]++;
                if( (y - Math.tan(Math.toRadians(67.5))*x)<=0 && (y - Math.tan(Math.toRadians(22.5))*x)>0 )//northeast
                    votes[1]++;
                if( (y - Math.tan(Math.toRadians(22.5))*x)<=0 && (y - Math.tan(Math.toRadians(-22.5))*x)>0 )//east
                    votes[2]++;
                if( (y - Math.tan(Math.toRadians(-22.5))*x)<=0 && (y - Math.tan(Math.toRadians(-67.5))*x)>0 )//southeast
                    votes[3]++;
                if( (y - Math.tan(Math.toRadians(-67.5))*x)<=0 && (y - Math.tan(Math.toRadians(67.5))*x)<0 )//south
                    votes[4]++;
                if( (y - Math.tan(Math.toRadians(67.5))*x)>=0 && (y - Math.tan(Math.toRadians(22.5))*x)<0 )//southwest
                    votes[5]++;
                if( (y - Math.tan(Math.toRadians(22.5))*x)>=0 && (y - Math.tan(Math.toRadians(-22.5))*x)<0 )//west
                    votes[6]++;
                if( (y - Math.tan(Math.toRadians(-22.5))*x)>=0 && (y - Math.tan(Math.toRadians(-67.5))*x)<0 )//northwest
                    votes[7]++;
            }
            double max = Double.NEGATIVE_INFINITY;
            int index =  -1;
            for (int i=0;i<8;i++)
            {
                if(max<votes[i])
                {
                    max = votes[i];
                    index = i;
                }
            }   
            return index;
            
        }
        abstract void  Callback_Item0();
        abstract void  Callback_Item1();
        abstract void  Callback_Item2();
        abstract void  Callback_Item3();
        abstract void  Callback_Item4();
        abstract void  Callback_Item5();
        abstract void  Callback_Item6();
        abstract void  Callback_Item7();
        abstract void  Callback_Item8();
        abstract void  Callback_Item9();
        abstract void  Callback_Item10();
        abstract void  Callback_Item11();
        abstract void  Callback_Item12();
        abstract void  Callback_Item13();
        abstract void  Callback_Item14();
        abstract void  Callback_Item15();
        abstract void  Callback_Item16();
        abstract void  Callback_Item17();
        abstract void  Callback_Item18();
        abstract void  Callback_Item19();
        abstract void  Callback_Item20();
        abstract void  Callback_Item21();
        abstract void  Callback_Item22();
        abstract void  Callback_Item23();
        abstract void  Callback_Item24();
        abstract void  Callback_Item25();
        abstract void  Callback_Item26();
        abstract void  Callback_Item27();
        abstract void  Callback_Item28();
        abstract void  Callback_Item29();
        abstract void  Callback_Item30();
        abstract void  Callback_Item31();
        abstract void  Callback_Item32();
        abstract void  Callback_Item33();
        abstract void  Callback_Item34();
        abstract void  Callback_Item35();
        abstract void  Callback_Item36();
        abstract void  Callback_Item37();
        abstract void  Callback_Item38();
        abstract void  Callback_Item39();
        abstract void  Callback_Item40();
        abstract void  Callback_Item41();
        abstract void  Callback_Item42();
        abstract void  Callback_Item43();
        abstract void  Callback_Item44();
        abstract void  Callback_Item45();
        abstract void  Callback_Item46();
        abstract void  Callback_Item47();
        abstract void  Callback_Item48();
        abstract void  Callback_Item49();
        abstract void  Callback_Item50();
        abstract void  Callback_Item51();
        abstract void  Callback_Item52();
        abstract void  Callback_Item53();
        abstract void  Callback_Item54();
        abstract void  Callback_Item55();
        private void decideCallback()
        {
            switch(selecteddirection*7+selectedgesture)
            {
                case 0:
                    Callback_Item0();
                    break;
                case 1:
                    Callback_Item1();
                    break;
                case 2:
                    Callback_Item2();
                    break;
                case 3:
                    Callback_Item3();
                    break;
                case 4:
                    Callback_Item4();
                    break;
                case 5:
                    Callback_Item5();
                    break;
                case 6:
                    Callback_Item6();
                    break;
                case 7:
                    Callback_Item7();
                    break;
                case 8:
                    Callback_Item8();
                    break;
                case 9:
                    Callback_Item9();
                    break;
                case 10:
                    Callback_Item10();
                    break;
                case 11:
                    Callback_Item11();
                    break;                  
                case 12:
                    Callback_Item12();
                    break;
                case 13:
                    Callback_Item13();
                    break;
                case 14:
                    Callback_Item14();
                    break;
                case 15:
                    Callback_Item15();
                    break;
                case 16:
                    Callback_Item16();
                    break;
                case 17:
                    Callback_Item17();
                    break;
                case 18:
                    Callback_Item18();
                    break;
                case 19:
                    Callback_Item19();
                    break;
                case 20:
                    Callback_Item20();
                    break;
                case 21:
                    Callback_Item21();
                    break;
                case 22:
                    Callback_Item22();
                    break;
                case 23:
                    Callback_Item23();
                    break;                  
                case 24:
                    Callback_Item24();
                    break;
                case 25:
                    Callback_Item25();
                    break;
                case 26:
                    Callback_Item26();
                    break;
                case 27:
                    Callback_Item27();
                    break;
                case 28:
                    Callback_Item28();
                    break;
                case 29:
                    Callback_Item29();
                    break;
                case 30:
                    Callback_Item30();
                    break;
                case 31:
                    Callback_Item31();
                    break;
                case 32:
                    Callback_Item32();
                    break;
                case 33:
                    Callback_Item33();
                    break;
                case 34:
                    Callback_Item34();
                    break;
                case 35:
                    Callback_Item35();
                    break;  
                case 36:
                    Callback_Item36();
                    break;
                case 37:
                    Callback_Item37();
                    break;
                case 38:
                    Callback_Item38();
                    break;
                case 39:
                    Callback_Item39();
                    break;
                case 40:
                    Callback_Item40();
                    break;
                case 41:
                    Callback_Item41();
                    break;
                case 42:
                     Callback_Item42();
                    break;
                case 43:
                    Callback_Item43();
                    break;
                case 44:
                    Callback_Item44();
                    break;
                case 45:
                    Callback_Item45();
                    break;
                case 46:
                    Callback_Item46();
                    break;
                case 47:
                    Callback_Item47();
                    break;  
                case 48:
                    Callback_Item48();
                    break;
                case 49:
                    Callback_Item49();
                    break;
                case 50:
                    Callback_Item50();
                    break;
                case 51:
                    Callback_Item51();
                    break;
                case 52:
                    Callback_Item52();
                    break;
                case 53:
                    Callback_Item53();
                    break;
                case 54:
                    Callback_Item54();
                    break;
                case 55:
                    Callback_Item55();
                    break;                    
            }
        }
}

