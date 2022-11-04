package MethodsUniversal;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class REGex {
    public static String  parseByRegex_OneString(String PatternString,String InputString,boolean ... caseInSensetive){

        if (caseInSensetive.length>0){
            if (caseInSensetive[0]==true){
                PatternString=toLowerCaseOnlyLetters(PatternString);
                InputString=toLowerCaseOnlyLetters(InputString);
            }
        }

        Pattern pattern = Pattern.compile(PatternString);
        if (caseInSensetive.length>0){ if (caseInSensetive[0]==true)pattern=Pattern.compile(PatternString,Pattern.CASE_INSENSITIVE);  }

        Matcher matcher = pattern.matcher(InputString);
        if (matcher.find())
        {
            return matcher.group(0);
        }
        return null;
    }
    public static Boolean  parseByRegex_OneString_BOOLEAN(String PatternString,String InputString,boolean ... caseInSensetive){

        if (caseInSensetive.length>0){if (caseInSensetive[0]==true){
            PatternString=toLowerCaseOnlyLetters(PatternString);
            InputString=toLowerCaseOnlyLetters(InputString);
        }}


        Pattern pattern = Pattern.compile(PatternString,Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(InputString);
        if (matcher.find())
        {
            return true;
        }
        return false;
    }

    static   String toLowerCaseOnlyLetters(String text){
//       ArrayList<String> forLowering=REGex.parseByRegex_All("\\)[A-я]+\\(",text,true);


//       String newText="";
//       for (String s : forLowering) {
//           newText= text.replace(s,s.toLowerCase());
//       }

        char[] charrrs=text.toCharArray();
        for (int i = 0; i < charrrs.length; i++) {
            String oneChar= String.valueOf(charrrs[i]);
            if (oneChar.matches("[А-я]")){
                charrrs[i]= oneChar.toLowerCase().toCharArray()[0];
            }
        }

        text=new String(charrrs);


        String  forLowering =  text.replaceAll("(?=\\(\\?)((?!\\)).)*(?>\\))","");
        if (text.indexOf("(")==-1 && text.indexOf(")")==-1)text=text.toLowerCase();

        if (forLowering.length()==0)return text;
//        String forLowering=text.replaceAll("(.*)","");
//       char[] chars= text.toCharArray();
//       for (int i = 0; i < chars.length; i++) {
//           if (Character.isLetter(chars[i]))chars[i]= String.valueOf( chars[i]).toLowerCase().charAt(0);
//       }
//        forLowering=  forLowering.replaceAll("[^a-zA-ZА-я0-9]", " ");

        String res= text.replace(forLowering,forLowering.toLowerCase());

        return  res.replace("ё","е") ;
    }


    public static String  parseByRegex_All(String PatternString,String InputString){

        Pattern pattern = Pattern.compile(PatternString);
        Matcher matcher = pattern.matcher(InputString);
        String result="";
        while (matcher.find())
        {
            result=result +matcher.group();
        }
        return result;
    }
}
