package MUA;

import java.util.ArrayList;

public class ListSup {
    //将list转为数组
    public static ArrayList<String> listToArray(String list){
        list=list.trim();
        list=list.substring(1,list.length()-1).trim();

        String[] allCommands=list.split("\\s+");
        String[] elements=Commands.bracketToString(allCommands);
        ArrayList<String> result=new ArrayList<>();
        for(String temp : elements)
            result.add(temp);

        return result;
    }

    //将一个array转换为list
    public static String ArrayToList(ArrayList<String> array){
        String result="[ ";
        for(String temp:array)
            result+=temp+" ";
        return result+"]";
    }
}
