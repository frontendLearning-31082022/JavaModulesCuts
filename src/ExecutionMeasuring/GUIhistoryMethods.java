package ExecutionMeasuring;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.util.LinkedList;

public class GUIhistoryMethods extends JFrame {
    MethodsList methodsHistory;

    volatile  public   JTextArea textArea=new JTextArea();

   public GUIhistoryMethods(MethodsList methodsHistory ){
       this.methodsHistory=methodsHistory;

       init();

       new String();
    }

    void init(){

        this.setTitle("История методов");
        this.methodsHistory.setJtextArea(textArea);

        JScrollPane scroll = new JScrollPane (textArea,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        this.add(scroll);
        this.setVisible (true);

        this.setSize(500, 600);
        this.setLocationRelativeTo(null);
//        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

}
