/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package markingmenus;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;
import javax.swing.JFrame;

public class MarkingMenuApp extends MenusPanel
{
    MarkingMenuApp(ArrayList<String> itemNames,ArrayList<Integer> itemIDs,String[] groups)
   {
           super(itemNames,itemIDs,groups);
   }
     public static void main(String[] args) 
    {
        // TODO code application logic here
        ArrayList<String> menuItemNames = new ArrayList<String>();
        ArrayList<Integer> menuItemIDs = new ArrayList<Integer>();
        //reading file
        try {
            FileInputStream fstream = new FileInputStream("Items.txt");
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String str;
            int lineNo=0,itemNo=0;
            ArrayList<String> direction  = new ArrayList<String>(Arrays.asList("N" ,"NE", "E", "SE", "S", "SW", "W", "NW"));
            ArrayList<String> gesture = new ArrayList<String>(Arrays.asList("PL" ,"CL", "BL", "S", "BR", "CR", "PR"));
            while ((str = br.readLine()) != null) {
                StringTokenizer tokens = new StringTokenizer(str,"-");
                lineNo++;
                if(tokens.countTokens()<3) 
                   throw new Exception("Error: Line no. " + lineNo + " - Out of format");
                str = tokens.nextToken();
                str.replaceAll(" ","");
                str.replaceAll("\t","");
                if(!direction.contains(str))
                    throw new Exception("Error: Line no. " + lineNo + " - Direction undefined");
                itemNo = direction.indexOf(str);
                str = tokens.nextToken();
                str.replaceAll(" ","");
                str.replaceAll("\t","");
                if(!gesture.contains(str))
                    throw new Exception("Error: Line no. " + lineNo + " - Gesture undefined");
                itemNo = itemNo*7 + gesture.indexOf(str);
                str = "";
                while(tokens.hasMoreTokens())
                    str = str + tokens.nextToken();
                menuItemNames.add(str);
                menuItemIDs.add(itemNo);
            }
            in.close();
        }   
        catch (Exception e) {
            System.err.println(e);
        }
        String[] groupNames = {"Temperature", "Pressure", "Area","Length","Time","Mass","Energy","Angle"};
        JFrame MM = new JFrame("Modified Flower Menu");
        MM.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        MM.add(new MarkingMenuApp(menuItemNames,menuItemIDs,groupNames));
        MM.setSize(850,850);
        MM.setVisible(true);
    }
        void  Callback_Item0(){
        }
        void  Callback_Item1(){}
         void  Callback_Item2(){}
         void  Callback_Item3(){}
         void  Callback_Item4(){}
         void  Callback_Item5(){}
         void  Callback_Item6(){}
         void  Callback_Item7(){}
         void  Callback_Item8(){}
         void  Callback_Item9(){}
         void  Callback_Item10(){}
         void  Callback_Item11(){}
         void  Callback_Item12(){}
         void  Callback_Item13(){}
         void  Callback_Item14(){}
         void  Callback_Item15(){}
         void  Callback_Item16(){
         }
         void  Callback_Item17(){
         }
         void  Callback_Item18(){
            }
         void  Callback_Item19(){}
         void  Callback_Item20(){}
         void  Callback_Item21(){}
         void  Callback_Item22(){}
         void  Callback_Item23(){}
         void  Callback_Item24(){}
         void  Callback_Item25(){}
         void  Callback_Item26(){}
         void  Callback_Item27(){}
         void  Callback_Item28(){}
         void  Callback_Item29(){}
         void  Callback_Item30(){}
         void  Callback_Item31(){}
         void  Callback_Item32(){}
         void  Callback_Item33(){}
         void  Callback_Item34(){}
         void  Callback_Item35(){}
         void  Callback_Item36(){}
         void  Callback_Item37(){}
         void  Callback_Item38(){}
         void  Callback_Item39(){}
         void  Callback_Item40(){}
         void  Callback_Item41(){}
         void  Callback_Item42(){}
         void  Callback_Item43(){}
         void  Callback_Item44(){}
         void  Callback_Item45(){}
         void  Callback_Item46(){}
         void  Callback_Item47(){}
         void  Callback_Item48(){}
         void  Callback_Item49(){}
         void  Callback_Item50(){}
         void  Callback_Item51(){}
         void  Callback_Item52(){}
         void  Callback_Item53(){}
         void  Callback_Item54(){}
         void  Callback_Item55(){}
}

         