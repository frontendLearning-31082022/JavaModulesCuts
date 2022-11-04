package SQL;

//import com.company.ParserPages_pattern.ParseArticles;
//import com.company.ParserPages_pattern.PhoneNotifier;
//import com.company.ParserPages_pattern.Viewer.ListOperations.RatingSetter;
//import com.company.ParserPages_pattern.Viewer.Pattern.DeserializeHashMap;
//import com.company.ParserPages_pattern.Viewer.Pattern.DeserializeHashMapSQL;

import MethodsUniversal.ExceptionsMethods;
import MethodsUniversal.ObjectArticle;
import MocksJava_Null.DeserializeHashMapSQL;
import MocksJava_Null.RatingSetter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

public class SQLpartsDownLoad {

    public int FistCount=2000;

    public int CountValue=-1;
    Mysql mysql;

    volatile public  boolean NewArticleNiceRating=false;
    volatile public HashMap<String, ObjectArticle> newArticlesAutoRefresh=new HashMap<>();
    String[][] CurrentReq;

    String  InitialRequest;
    public volatile   HashMap<String,String[]>dowloadList=new HashMap<>();
    public    HashMap<String,String[]>dowloadListAUTOREFRESH=new HashMap<>();
   public   volatile boolean CompleteDownLoad=false;
    public boolean AllDataTraansfered=false;
    JFrame frameNotiFy;
//    PhoneNotifier phoneNotifier=new PhoneNotifier();
    JTextArea messFrame;
   int ThreadRefereshSleepTimeSecs=2;
    int SizeAlready=0;
    String parentFolder;
    HashMap<String,String>deleteAlreadyExported=new HashMap<>();

   volatile boolean AutoRefresh=false;

    public SQLpartsDownLoad(String Request,String dbName,String parentFolder){
//        phoneNotifier.start();
        JframeNotifyInit();

        this.parentFolder=parentFolder;
        String RequestLimit=Request.substring(0,Request.lastIndexOf(";")) +" LIMIT "+FistCount +" ;";
        InitialRequest=Request;

        try {this.mysql=new Mysql(dbName);
        } catch (Exception e) {
            ExceptionsMethods.ErrorCritical(null,e);}

        CurrentReq=  this.mysql.selectDB(RequestLimit);
        for (String[] strings : CurrentReq) {
            dowloadList.put(strings[0],strings);
        }

        downloadOtherPart();

    new String();
    }

    private HashMap<String,String[]> exportNewData(){
        AllDataTraansfered=CompleteDownLoad;

        HashMap<String,String[]> copy= (HashMap<String, String[]>) dowloadList.clone();
        deleteAlreadyExported.keySet().forEach(d->copy.remove(d));
        copy.keySet().forEach(x->deleteAlreadyExported.put(x,null));
//        copy.keySet().forEach(x->dowloadList.remove(x));
        SizeAlready=copy.size();

        return copy;
    }

    public HashMap<String,String[]> getFirstData() {

        return exportNewData();}

    public HashMap<String,String[]> updateDATA(){
        return exportNewData();
    }

    public boolean isNewData(){
        return dowloadList.size()>SizeAlready;
    }

    private void downloadOtherPart(){
       Thread one = new Thread() {
            public void run() {

                Thread.currentThread().setName("SQLpartsDownLoad Thread");
                int offsetChanging=FistCount;

                int lastSize=0;

                while (lastSize!=dowloadList.size()){

                    String Req=InitialRequest.substring(0,InitialRequest.lastIndexOf(";")) +" LIMIT "+FistCount +" OFFSET "+offsetChanging+" ;";

                    String[][] ReqResult=  mysql.selectDB(Req);
                    if (ReqResult==null)continue;
                        lastSize=dowloadList.size();
                    for (String[] strings : ReqResult) {
                        dowloadList.put(strings[0],strings);
                    }
                    offsetChanging=offsetChanging+FistCount;
                    new String();
                    if (lastSize==dowloadList.size() && CountValue==-1){
                        int count=countLinesByInitReq();
                        if (dowloadList.size()==count )break;
                    }
                }

                CompleteDownLoad=true;
                System.out.println("SQLpartsDownLoad complete Thread");
            }
        };

        one.start();
    }

    private int countLinesByInitReq() {
        String ReqCount=InitialRequest.replaceAll("SELECT.*FROM","SELECT COUNT(linkkey) FROM");
        String[][] countReq=  mysql.selectDB(ReqCount);
        int count= Integer.parseInt(countReq[0][0]);
        return count;
    }

    public String[][] getFirstReq() {
        exportNewData();
        return CurrentReq;
    }

    public void turnOnAutoUpdate(){


        if(!CompleteDownLoad || !AllDataTraansfered){ JOptionPane.showMessageDialog(null,"CompleteDownLoad "+CompleteDownLoad+" AllDataTraansfered "+AllDataTraansfered);
            return;}

        AutoRefresh=true;
        dowloadListAUTOREFRESH= (HashMap<String, String[]>) dowloadList.clone();
        dowloadList.clear();

        autoRefreshThreadTurnOn();

    }

    void autoRefreshThreadTurnOn(){

        Thread one = new Thread() {
            public void run() {

                Thread.currentThread().setName("SQLpartsDownLoad autoRefresh Thread");

                while (AutoRefresh){
                    int count=countLinesByInitReq();
                    if (dowloadListAUTOREFRESH.size()>count)dowloadListUpdate(true);

                    if (dowloadListAUTOREFRESH.size()<count)clearDeletedFromLocalHashmap();

                    if (count==dowloadListAUTOREFRESH.size()){
                        try {Thread.sleep(ThreadRefereshSleepTimeSecs*1000);} catch (InterruptedException e) {}
                        continue;
                    }



                    dowloadListUpdate(false);

                    if (newArticlesAutoRefresh.size()>0){messFrame.setText("Новых статей +"+newArticlesAutoRefresh.size()+
                            "\n"+" С норм рейтингом "+NewArticleNiceRating);messFrame.updateUI(); frameNotiFy.setVisible(true);

//                        phoneNotifier.newArticles(newArticlesAutoRefresh);
                    }



                }


            }
        };

        one.start();
    }

    private void clearDeletedFromLocalHashmap() {

        String[][] ReqResult=  mysql.selectDB(InitialRequest.replaceAll("SELECT linkKey.*FROM","SELECT linkKey FROM"));

        HashMap<String,String>actualBase=new HashMap<>();
        for (String[] strings : ReqResult) {
            actualBase.put(strings[0],null);
        }

        ArrayList<String>deleteNoActualLinksFromLocal=new ArrayList<>();
        for (String keyLink : dowloadListAUTOREFRESH.keySet()) {
            if (!actualBase.containsKey(keyLink))deleteNoActualLinksFromLocal.add(keyLink);
        }

        deleteNoActualLinksFromLocal.forEach(x->dowloadListAUTOREFRESH.remove(x));

    }

    void   dowloadListUpdate(boolean RemoveDeletedOnly){
        String[][] ReqResult=  mysql.selectDB(InitialRequest);
        HashMap<String,String[]>justNowDowload=new HashMap<>();

        for (String[] strings : ReqResult) {
            justNowDowload.put(strings[0],strings);
        }

        if (ReqResult.length<dowloadListAUTOREFRESH.size()){
            ArrayList<String>keyDowloadDelete=new ArrayList<>();
            dowloadListAUTOREFRESH.keySet().forEach(x->{
                if (!justNowDowload.containsKey(x))keyDowloadDelete.add(x);
            });
            keyDowloadDelete.forEach(x->dowloadListAUTOREFRESH.remove(x));
        }
        if (RemoveDeletedOnly)return;

        checkNewArticlesNiceRating(justNowDowload);

        if (justNowDowload.size()>dowloadListAUTOREFRESH.size())dowloadListAUTOREFRESH=justNowDowload;

//        if (ReqResult.length<dowloadList.size()){
//            new String();
//        }
    }

    private void checkNewArticlesNiceRating(HashMap<String, String[]> justNowDowload) {
        HashMap<String,String[]>newArticles=new HashMap<>();
        NewArticleNiceRating=false;

                justNowDowload.keySet(). forEach(b->{
            if (!dowloadListAUTOREFRESH.containsKey(b))newArticles.put(b,justNowDowload.get(b));
        });


        HashMap<String, ObjectArticle> newArticlesOBJECTNOW =new HashMap<>();
        for (String linkKeyArticle : newArticles.keySet()) {
            try {ObjectArticle obj= DeserializeHashMapSQL.deserFromBytes(newArticles.get(linkKeyArticle)[1]);

                new String();
                newArticlesOBJECTNOW.put(linkKeyArticle,obj);
                                                 } catch (Exception e) {continue;}

            new String();
        }


        /*
        RatingSetter ratingSetter=new RatingSetter(java.nio.file.Path.of(parentFolder).getParent().toAbsolutePath().toString(),newArticlesOBJECTNOW,true);
        newArticlesOBJECTNOW= ratingSetter.fillDataBasePriorityByFileDBonly(newArticlesOBJECTNOW,"name");
        newArticlesOBJECTNOW=ratingSetter.fillDataBasePriorityByFileDBonly(newArticlesOBJECTNOW,"URL");
        */

        HashMap<String, ObjectArticle> finalNewArticlesOBJECTNOW = newArticlesOBJECTNOW;
        newArticlesOBJECTNOW.keySet().forEach(x->{
            if (!newArticlesAutoRefresh.containsKey(x))newArticlesAutoRefresh.put(x, finalNewArticlesOBJECTNOW.get(x));
        });

        for ( String key: newArticlesAutoRefresh.keySet()) {
            ObjectArticle objj=newArticlesAutoRefresh.get(key);
            try {
                int rating=Integer.parseInt(String.valueOf(objj.data.get(RatingSetter.NameFieldInData)));
                if (rating>4){NewArticleNiceRating=true;break;}
            }catch (NumberFormatException e){
                new String();
            }

        }
        new String();

    }


    public void JframeNotifyInit(){
         frameNotiFy = new JFrame("Новые статьи");
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
         messFrame = new JTextArea("");
        JButton button = new JButton();
        button.setText("Прочитано");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               frameNotiFy.setVisible(false);
            }
        } );
        panel.add(messFrame);
        panel.add(button);
        frameNotiFy.add(panel);
        frameNotiFy.setSize(200, 120);
        frameNotiFy.setLocation(700,700);
        frameNotiFy.setLocationRelativeTo(null);
        frameNotiFy.setAlwaysOnTop(true);
        frameNotiFy.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frameNotiFy.setVisible(false);
    }

    /*
    public void addNewArticlesToDB(DeserializeHashMap.HashMapCoverSerialize<String, ParseArticles.ObjectArticle> dataBase) {
        for (String key : newArticlesAutoRefresh.keySet()) {
            if (!dataBase.containsKey(key))dataBase.put(key,newArticlesAutoRefresh.get(key));
        }
        newArticlesAutoRefresh.clear();
        NewArticleNiceRating=false;

    }
      */

}




