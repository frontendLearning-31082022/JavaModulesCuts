package SQL;

import com.company.Learning.AlgorithmsSejevic.Structures.Stack;
//import com.company.ParserPages_pattern.ParseArticles;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Objects;

public class MysqlQuery {

    public static String FieldsSerialize="serializedCode";

    Stack<String[]>stackUpdateField=new Stack<>();
    Stack<String[]>stackNewObjects=new Stack<>();

    String tableName;
    String fieldKeyName;
    String pathNullKeys;
    int countLineForQuery=100;

    public MysqlQuery(String tableName, String fieldKeyName, String pathNullKeys){
        this.tableName=tableName;
        this.fieldKeyName=fieldKeyName;
        this.pathNullKeys=pathNullKeys;
    }

   public    void   setQuerySize(int count){
        this.countLineForQuery=count;
    }

    public void updateField(String key,String fieldName, String fieldValue) {
        String[] line={key,fieldName,fieldValue};
        stackUpdateField.push(line);

    }

    public void createNew(String keyLink, String byteCode) {
        String[] line={keyLink,byteCode};
        stackNewObjects.push(line);
    }

    public void clearNulltoTXT() {
        //used for log crashed articles
        /*
        if (pathNullKeys==null)return;
        ArrayList<String> keysNull=new ArrayList<>();
        for (String[] stackNewObject : stackNewObjects) {
            if (stackNewObject[0]==null){
                try {WriteToFIleMethods.writeTXTnewLineappend(pathNullKeys,stackNewObject[1]);} catch (
                        IOException e) {ExceptionsMethods.ErrorCritical("",e);}
                keysNull.add((String) stackNewObject[0]); continue;
            }
            if (Objects.equals(stackNewObject[0], "null")){
                try {WriteToFIleMethods.writeTXTnewLineappend(pathNullKeys,stackNewObject[1]);} catch (
                        IOException e) {ExceptionsMethods.ErrorCritical("",e);}
                keysNull.add((String) stackNewObject[0]);
            }
        }

        Stack<String[]> clearedstackNewObjects=new Stack<>();
        while (stackNewObjects.size()>0){
            String[] popped=stackNewObjects.pop();

            if (popped[0]==null)continue;
            if (popped[0].equals("null"))continue;

            clearedstackNewObjects.push(popped);

        }
        stackNewObjects=clearedstackNewObjects;

        */
    }
}
