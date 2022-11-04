import ExecutionMeasuring.SimpleExecMeasure;
import SQL.Mysql;
import SQL.MysqlQuery;
import SQL.SQLpartsDownLoad;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args)  {

        //1 execute Code
        SimpleExecMeasure.main(null);

        //2 CodeForRead
        Mysql mysql=null;
        try {mysql=new Mysql("dbName");} catch (Exception e) {}

        //3 CodeForRead
        MysqlQuery mysqlQuery=new MysqlQuery("name","url",null);
        mysql.executeQuery(mysqlQuery,false);

        //4 CodeForRead  --- LoadList from db - first 2000 , and while all data tranfered
        SQLpartsDownLoad sqLpartsDownLoad=new SQLpartsDownLoad("SELECT * etc","nameDB","parent local folder path for set ratings to articles");



    }
}