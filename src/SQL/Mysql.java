package SQL;

import MethodsUniversal.ObjectArticle;
import MethodsUniversal.REGex;
import com.company.Learning.AlgorithmsSejevic.Structures.Stack;
//import com.company.MethodsPackage.Jframes.StatusExecutionJframe;
//import com.company.ParserPages_pattern.ParseArticles;
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Element;

import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Mysql {
    public Statement stmt=null;

    String DBName;
    public Connection connection;

   public static Exception exceptionDuplicatePrimary=new Exception("DuplicatePrimary");
    public Mysql(String DBName) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, SQLException {
        String dbURL = "jdbc:mysql://192.168.0.0:3306/"+DBName+"?useUnicode=true&serverTimezone=UTC";
        String user = "userSQL";
        String pass = "password";
        this.DBName=DBName;
        Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
        System.out.println("Connection succesfull!");

        connection = null;

        connection = DriverManager.getConnection(dbURL, user, pass);
        if (connection != null) {
             stmt = connection.createStatement();

        }
    }

   public   boolean insert(String table,String... values){
       String query = "INSERT INTO table (id, name, value) VALUES (?, ?, ?)";
       String sql = "INSERT INTO "+ table + "() VALUES (";
       for (int i = 0; i < values.length; i++) {
           values[i]= "'"+values[i]+"'";
           values[i]=values[i].replaceAll("\\\\", "\\\\\\\\");
       }

       for (String value : values) {
           sql=sql+value+",";
       }
       sql=sql.substring(0,  sql.lastIndexOf(","));
       sql=sql+")";

//        "'testINTELJFROM', 'testINTELJFROM text text text')";
        try {
            int count = stmt.executeUpdate(sql);
            return (count > 0);

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }

    public   boolean updateField(String table,String NameKeyField,String key,String NameValue,String Value){
        String sql = "UPDATE "+ table + " SET "+NameValue+"='"+Value+"' WHERE "+NameKeyField+"='"+key+"'";

//        UPDATE <table_name>
//        SET <col_name1> = <value1>, <col_name2> = <value2>, ...
//        WHERE <condition>;

        try {
            int count = stmt.executeUpdate(sql);
            return (count > 0);

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }
    public   boolean updateFields(String table,String[][] whatToChange){
        String query = "UPDATE "+table+ " SET "+whatToChange[0][2]+"=? WHERE "+whatToChange[0][0]+"=?";

        PreparedStatement ps = null;
        try {ps = connection.prepareStatement(query);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        try {connection.setAutoCommit(false);} catch (SQLException e) {
            e.printStackTrace(); return false;}

        DateFormat dateFormat = new SimpleDateFormat("hh:mm");
        int count = 0;
        for (String[] strings : whatToChange) {
            try {
                ps.setString(1, strings[3]);
                ps.setString(2, strings[1]);

                ps.addBatch();


                if (count % 100 == 0 || count == whatToChange.length) {
                    ps.executeBatch();
                    System.out.print(" " + dateFormat.format(new Date()) + "exec" + count + " ");
                }
                count++;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        try {
            ps.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {this.connection.commit();} catch (SQLException e) {
            throw new RuntimeException(e);}

//        try {this.stmt.close();} catch (SQLException e) {
//            throw new RuntimeException(e);}
//        try {this.connection.close();} catch (SQLException e) {
//            throw new RuntimeException(e);}

        return true;


    }



    public boolean executeQuery(MysqlQuery mysqlQuery,boolean showWindow){
        int tries=5;

        boolean res=false;
        while (tries>0){
            try {     res=  executeQueryPriv(mysqlQuery,showWindow); tries=-1;
            }catch (Exception e){
                tries--;
                try {Thread.sleep(1000);} catch (InterruptedException ex) {throw new RuntimeException(ex);}
                if (  e.getMessage().indexOf("Can''t call commit when autocommit=true")>-1)continue;
//                ExceptionsMethods.ErrorCritical(null,e);
            }
        }


      return res;
    }
    private boolean executeQueryPriv(MysqlQuery mysqlQuery,boolean showWindow){
        mysqlQuery.clearNulltoTXT();

        Date fromDate=new Date();
        System.out.println("Залитие "+" записей. "+fromDate);

        System.out.println("");

        PreparedStatement ps = null;
        try {connection.setAutoCommit(false);
        } catch (SQLException e) {e.printStackTrace(); return false;}


        String query = "INSERT INTO "+mysqlQuery.tableName+ " ("+mysqlQuery.fieldKeyName+", serializedCode"+") VALUES (?, ?)";
        try {ps = connection.prepareStatement(query);
        } catch (SQLException e) {e.printStackTrace();return false;}
        int countLines = 0;

//        StatusExecutionJframe SET=new StatusExecutionJframe(new Object());
//        if (showWindow)SET=new StatusExecutionJframe("Запись SQL новых записей",mysqlQuery.stackNewObjects.size());

        ArrayList<String[]> currentLoad=new ArrayList<>();
        while (mysqlQuery.stackNewObjects.size()>0){
//            SET.MakeStep();
            String[]keyAndCode=mysqlQuery.stackNewObjects.pop();
            currentLoad.add(keyAndCode);
            try {ps.setString(1, keyAndCode[0].replaceAll("\\\\", "\\\\\\\\"));
                ps.setString(2,  keyAndCode[1].replaceAll("\\\\", "\\\\\\\\") );
                ps.addBatch();
                countLines++;
                if (countLines % mysqlQuery.countLineForQuery == 0 || mysqlQuery.stackNewObjects.size()==0) {
                    ps.executeBatch();
                    currentLoad.clear();
                    System.out.print(" "+new SimpleDateFormat("hh:mm").format(new Date())+"exec"+countLines+" ");
                }
            } catch (SQLException e) {
                while (currentLoad.size()>0){
                    try {ps.clearBatch();

                        keyAndCode=currentLoad.remove(0);
                        ps.setString(1, keyAndCode[0].replaceAll("\\\\", "\\\\\\\\"));
                        ps.setString(2,  keyAndCode[1].replaceAll("\\\\", "\\\\\\\\") );
                        ps.addBatch();
                        ps.executeBatch();
                    } catch (SQLException ex) {
//                        ExceptionsMethods.MediumError("SQL Не Выполнилось "+ ex.getMessage()+" URLLL"+ keyAndCode[0]+"   QUERRR"+query);
                        try {ps.clearBatch();} catch (SQLException exc) {}
                    }
                }
            }

        }
//        SET.stopFrame();



        HashMap<String, Stack<String[]>> fieldss= chainGenerateUpdateFields(mysqlQuery.stackUpdateField);

//        StatusExecutionJframe SET2=null;
        for (String nameField : fieldss.keySet()) {
            query = "UPDATE " + mysqlQuery.tableName + " SET " + nameField + "=? WHERE " + mysqlQuery.fieldKeyName + "=?";
            try {
                ps = connection.prepareStatement(query);
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
            countLines = 0;
            Stack<String[]> stackCur = fieldss.get(nameField);
//            SET2=new StatusExecutionJframe("Изменение SQL записей",fieldss.get(nameField).size());
            while (fieldss.get(nameField).size() > 0) {
//                SET2.MakeStep();
                String[] keyAndCode = stackCur.pop();
                currentLoad.add(keyAndCode);
                try {
                    ps.setString(1, keyAndCode[2].replaceAll("\\\\", "\\\\\\\\"));
                    ps.setString(2, keyAndCode[0].replaceAll("\\\\", "\\\\\\\\"));
                    ps.addBatch();
                    countLines++;
                    if (countLines % mysqlQuery.countLineForQuery == 0 || stackCur.size() == 0) {
                        ps.executeBatch();
                        System.out.print(" " + new SimpleDateFormat("hh:mm").format(new Date()) + "exec" + countLines + " ");
                        currentLoad.clear();
                    }
                } catch (SQLException e) {
                    while (currentLoad.size() > 0) {
                        try {
                            ps.clearBatch();

                            keyAndCode = currentLoad.remove(0);
                            ps.setString(1, keyAndCode[0].replaceAll("\\\\", "\\\\\\\\"));
                            ps.setString(2, keyAndCode[1].replaceAll("\\\\", "\\\\\\\\"));
                            ps.addBatch();
                            ps.executeBatch();
                        } catch (SQLException ex) {
//                            ExceptionsMethods.MediumError("SQL Не Выполнилось " + ex.getMessage() + " URLLL" + keyAndCode[0] + "   QUERRR" + query);
                            try {
                                ps.clearBatch();
                            } catch (SQLException exc) {
                            }
                        }
                    }
                }

            }
        }
//        try {SET2.stopFrame();}catch (Exception e){}

        long diff=new Date().getTime()-fromDate.getTime();
        diff=diff/1000;
        System.out.println("");
        System.out.println("  за "+diff+" секунд");

        try {connection.commit();} catch (SQLException e) {
            throw new RuntimeException(e);}
        try {connection.setAutoCommit(true);
        } catch (SQLException e) {e.printStackTrace();}

//        try {stmt.close();} catch (SQLException e) {
//            throw new RuntimeException(e);}
//        try {connection.close();} catch (SQLException e) {
//            throw new RuntimeException(e);}

        return  true;
    }
    HashMap<String,Stack<String[]>> chainGenerateUpdateFields(Stack<String[]> allStack){
        HashMap<String,Stack<String[]>> chain=new HashMap<>();

        while (allStack.size()>0){
            String[] line=allStack.pop();
            if (! chain.containsKey(line[1]))chain.put(line[1],new Stack<>());
            chain.get(line[1]).push(line);
        }
        return chain;
    }


    public boolean insertMultilines(String tableName,HashMap<String, String> byteCodeHashMap) {
        String query = "INSERT INTO "+tableName+ " VALUES (?, ?)";

        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement(query);
        } catch (SQLException e) {
          e.printStackTrace();
          return false;
        }
        int count = 0;
        DateFormat dateFormat = new SimpleDateFormat("hh:mm");

        try {
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace(); return false;
        }




        for (String key : byteCodeHashMap.keySet()) {
            try {
                ps.setString(1, key);
                ps.setString(2,  byteCodeHashMap.get(key) );
                ps.addBatch();
                count++;

            if (count % 100 == 0 || count == byteCodeHashMap.size()) {
                ps.executeBatch();
                System.out.print(" "+dateFormat.format(new Date())+"exec"+count+" ");
            }
        } catch (SQLException e) {
                e.printStackTrace();}
        }



        try {
            ps.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        return true;
    }

    public  ArrayList<String> selectProcedure(String req,String NameField){
        try {if (stmt.isClosed())stmt = connection.createStatement();} catch (SQLException e) {}

        ArrayList<String>rows=new ArrayList<>();
        String query = "{"+req+"}";
        CallableStatement stmt = null; try {
            stmt = connection.prepareCall(query);
           ResultSet  rs = stmt.executeQuery();
            while (rs.next()) {
                rows.add(rs.getString(NameField));
            }

        } catch (SQLException e) {
//           ExceptionsMethods.ErrorCritical(null,e);
        }

//        String[] stringArray = rows.toArray(new String[0]);
        return rows;
    }

    public  String[][] selectDB(String req){
//        req="USE "+DBName+"; "+req;
        String[] fields= REGex.parseByRegex_OneString("(?<=SELECT).+?(?=FROM)",req).trim().split(",");

        try {if (stmt.isClosed())stmt = connection.createStatement();} catch (SQLException e) {}

        ArrayList<String[]> rows=new ArrayList<>();
        try{
            ResultSet rs = stmt.executeQuery(req);
            while(rs.next()){
                //Display values
                String[]oneArticle=new String[fields.length];
                for (int i = 0; i < fields.length; i++) {
                    oneArticle[i]=rs.getString(fields[i]);

                }
                rows.add(oneArticle);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        String[][] result=new String[rows.size()][fields.length];
        for (int i = 0; i < rows.size(); i++) {
            result[i]=rows.get(i);
        }

        return result;

    }


    public boolean insertFull(String table,String... NameColumnAndValues) throws SQLException {
//        NameColumnAndValues
//        String query = "INSERT INTO table (id, name, value) VALUES (?, ?, ?)";
        ArrayList<String>fieldsNames=new ArrayList<>();
        ArrayList<String>values=new ArrayList<>();
        for (int i = 0; i < NameColumnAndValues.length; i++) {
            if (i%2==0){fieldsNames.add(NameColumnAndValues[i]);
            }else {values.add(NameColumnAndValues[i]);}
        }

        String sqlReq = "INSERT INTO "+ table+"(";
        for (String fieldsName : fieldsNames) {
            sqlReq=sqlReq+fieldsName+",";
        }
        sqlReq=sqlReq.substring(0,sqlReq.length()-1);


        sqlReq=sqlReq+")VALUES(";
        for (String fieldsName : values) {
            sqlReq=sqlReq+"'"+fieldsName+"', ";
        }
        sqlReq=sqlReq.substring(0,sqlReq.length()-2);
        sqlReq=sqlReq+");";
        int count=-1;
       count = stmt.executeUpdate(sqlReq);

            return (count > 0);


    }

 public    boolean isException_DuplicatePrimaryKey(Exception exception){
        String message=exception.getMessage();
        if (message.matches(".*Duplicate entry.*for key 'PRIMARY'"))return true;
        return false;   }

    public boolean isException_LockWait_TransactionRollback_(Exception exception){
        String message=exception.getMessage();
        String nameExcep=exception.getClass().toString();
        if (nameExcep.equals("class com.mysql.cj.jdbc.exceptions.MySQLTransactionRollbackException") &&
                message.indexOf("Lock wait timeout exceeded; try restarting transaction")>-1)return true;

        return false;
    }

    public void updateField_HTMLsrc(String table, String NameKeyField, String key, String NameValue, String Value) throws SQLException {

        String request =  "UPDATE "+ table + " SET "+NameValue+"=? WHERE "+NameKeyField+"='"+key+"'";
//        String request = "insert into database_name.articles (title, content) values (?, ?)";

        PreparedStatement ps = null;
            ps = connection.prepareStatement(request);
            ps.setString(1, Value);

            ps.executeUpdate();

    }

    public boolean DataTooLongColumnCheckDo(String serializedCode, String tableName, String linkKey, ObjectArticle objectNew, String insertFull, SQLException e) {

        if (e.getMessage().indexOf("Data too long for column 'serializedCode'")==-1)return false;


//        JsoupMethods.getTextElementsOnly(Jsoup.parse(objectNew.code).body().text() )

        new String();
//        if ()

        return true;
    }

    public String getSELECTreqSerializedCodeDB(String sqLtableName, boolean TypegoodNull, boolean DeletedNull,
                                               boolean firstAddDBnull, int LimitArticles, Date fromDateFirstAddDB) {

        String strDate =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(fromDateFirstAddDB);

        String req="SELECT linkKey,serializedCode FROM "+sqLtableName+" WHERE " +
                ( TypegoodNull ?  "(TypeGood='null' OR TypeGood='' OR TypeGood IS NULL)" :"")
                + (DeletedNull ? " AND (Deleted='0' OR Deleted='null' OR Deleted IS NULL)" : "")
                + (firstAddDBnull ? " AND (dateFirstAddToDB='' OR dateFirstAddToDB='null' OR dateFirstAddToDB IS NULL)" : "")
                + (fromDateFirstAddDB!=null ? " AND (dateFirstAddToDB > '"+ strDate+"')" : "")
                + (LimitArticles>0 ? " LIMIT "+LimitArticles : "")
                + ";";

        return req;
    }
}
