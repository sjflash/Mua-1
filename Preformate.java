package MUA;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Preformate {
    //判断list的左右括号个数是否匹配
    public static boolean isBracketsPatch(String temp,String typeLeft){
        String typeRight;
        if(typeLeft.equals("["))
            typeRight="]";
        else
            typeRight=")";

        int countLeft=0;
        int countRight=0;
        String[] tempArray=temp.split("\\s+");
        for(int i=0;i<tempArray.length;i++)
            if(tempArray[i].equals(typeLeft))
                countLeft++;
            else if(tempArray[i].equals(typeRight))
                countRight++;
            else
                continue;

        return countLeft==countRight;
    }

    //[]的预格式化函数，预格式化后[和]的左右都有空格（头尾非全部有）
    //typeLeft 为"(", 或者"["
    public static String preformatBracket(String temp,String typeLeft){
        String typeRight;
        if(typeLeft.equals("("))
            typeRight=")";
        else
            typeRight="]";
        String[] tempArray=temp.split("\\s+");
        List<String> tempList= Arrays.asList(tempArray);
        tempList=new ArrayList<String>(tempList);

        for(int i=0;i<tempList.size();i++) {
            String tempString = tempList.get(i);
//            if (tempString.startsWith(typeLeft) || tempString.startsWith(typeRight)) {
//                if (tempString.length() != 1) {
//                    tempList.set(i, tempString.substring(1));
//                    tempList.add(i, tempString.substring(0, 1));
//                }
//            } else if (tempString.endsWith(typeRight) || tempString.endsWith(typeLeft)) {
//                if (tempString.length() != 1) {
//                    tempList.set(i, tempString.substring(0, tempString.length() - 1));
//                    tempList.add(i + 1, tempString.substring(tempString.length() - 1, tempString.length()));
//                    i--;
//                }
//            }
            if((tempString.contains(typeLeft)||tempString.contains(typeRight)) && tempString.length()!=1 && !tempString.startsWith("\"")) {
                int index = 0;
                if (tempString.contains(typeLeft))
                    index = tempString.indexOf(typeLeft);
                else if (tempString.contains(typeRight))
                    index = tempString.indexOf(typeRight);

                String part1 = tempString.substring(0, index);
                String part2 = tempString.substring(index, index + 1);
                String part3 = tempString.substring(index + 1, tempString.length());

                tempList.set(i, part3);
                tempList.add(i, part2);
                tempList.add(i, part1);
                i--;
            }
        }

        temp="";
        for(int i=0;i<tempList.size();i++)
            temp+=tempList.get(i)+" ";
        return temp.trim();
    }
}
