package MUA;

public class TypeJudge {
    public static boolean isNum(String value){
        return NumSupport.isDigits(value);//直接调用isDigits判断，但是在有表达式的时候可能存在问题
    }

    public static boolean isWord(String value){
        return value.startsWith("\"");
    }

    public static boolean isList(String value){
        value=value.trim();
        return value.startsWith("[") && value.endsWith("]") && Preformate.isBracketsPatch(value,"[");
    }

    public static boolean isBool(String value){
        if(value.startsWith("\""))
            value=value.substring(1);
        return value.toLowerCase().equals("false") || value.toLowerCase().equals("true");
    }

    public static boolean isEmpty(String value){
        return value.equals("\"") || value.equals("[]");
    }
}
