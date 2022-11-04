package ExecutionMeasuring;

import java.util.*;

import static GUI.JframeMeths.ShowCheckBoxesChoose;


public class SimpleExecMeasure {

    GUI gui;
    String cuurentMethod="";

    boolean turnOn=false;

    volatile public MethodsList methodsHistory=new MethodsList(new LinkedList<>());

    public SimpleExecMeasure(){
        gui=new GUI(this);
    }


    public static void main(String[] args) {
            SimpleExecMeasure.run();
    }

  public   static void run() {

//        TestThread.testDrive();

        Thread threadMonitor = new Thread() {
            public void run() {
                SimpleExecMeasure simpleExecMeasure=new SimpleExecMeasure();
            }
        };
        threadMonitor.start();
    }

    void startMeasure(){

        turnOn=true;
        Thread choosedThread=chooseThread();
        if (choosedThread==null)return;
        GUIhistoryMethods guIhistoryMethods=new GUIhistoryMethods(methodsHistory);

        Thread threadMonitor = new Thread(){
            public void run(){
             while (turnOn){
                 String temp=   getCurrentMethod(choosedThread);
                 if (!temp.equals(cuurentMethod))methodSwithched(temp);
                 new String();
             }
            }
        };

        threadMonitor.start();
    }

    private void methodSwithched(String temp) {
        HashMap<Date,String> cur=new HashMap<>();
        cur.put(new Date(),temp);

        methodsHistory.addToList(cur);
        cuurentMethod=temp;

    }

    String getCurrentMethod(Thread thread){
        StackTraceElement [] element = thread.getStackTrace();
        String temp="";
       for (int i = element.length - 1; i >= 0; i--) {
           temp=temp+ element[i].getMethodName();
           if (i!=0)temp=temp+" --> ";

       }
       temp=temp+"\n";
      return temp;
    }

    Thread chooseThread(){
        Set<Thread> threads = Thread.getAllStackTraces().keySet();
        ArrayList<String> threadsList=new ArrayList<>();

        for (Thread t : threads) {
            String name = t.getName();
            threadsList.add(name);
        }

        ArrayList<String> tthreads=  ShowCheckBoxesChoose(threadsList,"Выбрать один поток");
        if (tthreads==null)return null;
        String threadName= tthreads.get(0);

        Thread choosedThread=null;
        for (Thread t : threads) {
            String name = t.getName();
            if (name.equals(threadName)){
                choosedThread=t;
                break;
            }
        }
        return choosedThread;
    }

}
