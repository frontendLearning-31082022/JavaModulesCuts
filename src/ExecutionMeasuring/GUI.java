package ExecutionMeasuring;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Set;

public class GUI extends JFrame {

    SimpleExecMeasure simpleExecMeasure;

  public   GUI(SimpleExecMeasure simpleExecMeasure) {
      this.simpleExecMeasure=simpleExecMeasure;
      init();

    }

    void init(){
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        JLabel label = new JLabel("JFrame By Example");


        JButton buttonShowThreads = new JButton();
        buttonShowThreads.setText("Все потоки");
        actionListen_buttonShowThreads(buttonShowThreads);

        JButton buttonStartRecord = new JButton();
        buttonStartRecord.setText("Старт записи времени выполнения потока");
        actionListen_buttonStartRecord(buttonStartRecord);

        JButton buttonStopLog = new JButton();
        buttonStopLog.setText("Остановить запись лога");
        actionListen_buttonStopLog(buttonStopLog);

        JButton buttonStartTestThreads = new JButton();
        buttonStartTestThreads.setText("Запустить потоки для проверки программы");
        actionListen_buttonStartTestThreads(buttonStartTestThreads);



        panel.add(label);
        panel.add(buttonShowThreads);
        panel.add(buttonStartRecord);
        panel.add(buttonStopLog);
        panel.add(buttonStartTestThreads);

        this.add(panel);
        this.setSize(400, 300);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    private void actionListen_buttonStopLog(JButton buttonStopLog) {
        buttonStopLog.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                simpleExecMeasure.turnOn=false;
            }


        } );
    }

    private void actionListen_buttonStartTestThreads(JButton buttonStopLog) {
        buttonStopLog.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                TestThread.testDrive();
            }


        } );
    }

    private void actionListen_buttonStartRecord(JButton buttonStartRecord) {
        buttonStartRecord.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                simpleExecMeasure.startMeasure();
            }


        } );
    }

    private JButton actionListen_buttonShowThreads(JButton buttonShowThreads) {
        buttonShowThreads.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Set<Thread> threads = Thread.getAllStackTraces().keySet();
                String result="";

                ArrayList<String>lines=new ArrayList<>();
                for (Thread t : threads) {
                    String name = t.getName();
                    Thread.State state = t.getState();
                    int priority = t.getPriority();
                    String type = t.isDaemon() ? "Daemon" : "Normal";
//                    result=result+"<p><tr> <td>"+  name+" </td> <td>"+state+" </td> <td>"+type+" </td>"+"</tr></p>";
                    lines.add("<p><tr> <td>"+  name+" </td> <td>"+state+" </td> <td>"+type+" </td>"+"</tr></p>");
                }

                java.util.Collections.sort(lines, new Comparator<String>() {
                    public int compare(String lineA, String lineB) {
                        if (lineA.indexOf("Daemon")>-1)return 1;
                        if (lineB.indexOf("Daemon")>-1)return -1;
                        return lineA.compareTo(lineB);
                    }
                });

                for (String line : lines) {
                    result=result+line;
                }

                result=result.replaceAll("\t"," &emsp; ");
                result=result.replaceAll("\n","");

//               result="<style>" +"table{" + "  border-spacing: 0;" +
//                       "  width:80%;" + "  margin: auto;" + "  text-align: center;" + "}" +
//                       "" + "tr {" + "    background: linear-gradient(rgb(0, 0, 0), rgba(0, 0, 0));\n" +
//                       "}" + "</style>"+result;
                result="<html><body><table>"+result+"</table></body>";


                JOptionPane.showMessageDialog(null,result);

//                String msg = "<html>this is a really long message<br>this is a really long message this is a really long message this is a really long message this is a really long message this is a really long message this is a really long message";
//
//                JOptionPane optionPane = new JOptionPane();
//                optionPane.setMessage(msg);
//                optionPane.setMessageType(JOptionPane.INFORMATION_MESSAGE);
//                JDialog dialog = optionPane.createDialog(null, "Width 100");
//                dialog.setVisible(true);
            }


        } );

        return buttonShowThreads;

    }


}
