package GUI;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class JframeMeths {

    public static HashMap<String,Boolean> ShowCheckBoxesChoose(String title, String... checkBoxArgs){
        ArrayList<String> arr=new ArrayList<>();
        for (String checkBoxArg : checkBoxArgs) {
            arr.add(checkBoxArg);
        }
        ArrayList<String> res=  ShowCheckBoxesChoose(arr,title);



        HashMap<String,Boolean> retttt=new HashMap<>();
        if (res==null)return retttt;
        res.forEach(x->retttt.put(x,true));

        return retttt;
    }

    public static  ArrayList<String> ShowCheckBoxesChoose(ArrayList<String> booleans, String title) {
        JPanel layoutPanel = new JPanel(new GridLayout(200, 1));
        HashMap<String,JCheckBox> boxes=new HashMap<>();
        booleans.forEach(x->{
            JCheckBox jCheckBox=new JCheckBox(x);
            layoutPanel.add(jCheckBox);
            boxes.put(x,jCheckBox);
        });
        JScrollPane scroller = new JScrollPane(layoutPanel);
        scroller.setPreferredSize(new Dimension(300, 500));
        JFrame jf=new JFrame();
        jf.setAlwaysOnTop(true);
        int answer = JOptionPane.showConfirmDialog(jf, scroller, title, JOptionPane.OK_CANCEL_OPTION);


        ArrayList<String> result=new ArrayList<>();

        boxes.keySet().forEach(x->{
            if (boxes.get(x).isSelected())result.add(x);
        });

        if (answer!=0)return null;
        return result;
    }


}
