package ExecutionMeasuring;


import MethodsUniversal.REGex;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.http.WebSocket;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

// MethodsList лог ведется по выполненным методам (потоками)
public class MethodsList  implements ActionListener {
    LinkedList<Object> methsList;
    JTextArea jTextArea;
    Thread threadUpdateJtextArea;
    public MethodsList(LinkedList<Object> methsList) {
        this.methsList=methsList;

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        new String();
    }


    public void addToList(HashMap<Date, String> cur) {
        methsList.add(cur);
        updateJtextArea();
    }

    private void updateJtextArea() {
        try {Thread.sleep(ThreadLocalRandom.current().nextInt(2, 400 + 1));} catch (InterruptedException e) {}
        if (Thread.holdsLock(methsList))return;
        threadUpdateJtextArea = new Thread(){
            public void run(){
                synchronized ( methsList ) {

                String res="";
                LinkedList<Object> clone= (LinkedList<Object>) methsList.clone();
                LocalTime last =null;
                long secondBetween=0;
                for (Object dateNString : clone) {
                    HashMap<Date,String>oneEntry= (HashMap<Date, String>) dateNString;
//
                   String time= REGex.parseByRegex_OneString("\\d*:\\d*:\\d*", String.valueOf(oneEntry.keySet().toArray()[0]));
                    LocalTime current=  LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm:ss"));

                    if (last!=null){
                        secondBetween= last.until(current, ChronoUnit.SECONDS);
                    }
                    last=current;
                    res=res+time+"\t"+secondBetween+ "\t"+oneEntry.values().toArray()[0];
                }
                jTextArea.setText(res);
                try { jTextArea.updateUI();}catch (Exception f){}
                jTextArea.setCaretPosition(jTextArea.getDocument().getLength());
                }
            }
        };
        threadUpdateJtextArea.setName("updateJtextAreaLog");
       threadUpdateJtextArea.start();
    }



    public void setJtextArea(JTextArea textArea) {
        this.jTextArea=textArea;
    }
}
