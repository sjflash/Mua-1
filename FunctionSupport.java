package MUA;

import java.util.ArrayList;
import java.util.Arrays;

public class FunctionSupport {
    public static ArrayList<String> getParameters(String functionList){
        String paraStr=ListSup.listToArray(functionList).get(0);
        return ListSup.listToArray(paraStr);
//        //去除首尾[]
//        functionList=functionList.substring(1,functionList.length()-1).trim();
//        String[] function=functionList.split("\\s+");
//        ArrayList<String> result=new ArrayList<>();
//        int countLeft=0;
//        if(hasPara(functionList)) {
//            for (int i = 0; i < functionList.length(); i++) {
//                String temp = function[i];
//                if (temp.equals("[")) {
//                    if (countLeft != 0)
//                        result.add(temp);
//                    countLeft++;
//                } else if (temp.equals("]")) {
//                    countLeft--;
//                    if (countLeft != 0)
//                        result.add(temp);
//                    else
//                        break;
//                } else
//                    result.add(temp);
//            }
//        }
//
//        return result;
    }

    public static ArrayList<String> getAllCommands(String functionList){
        String cmdStr=ListSup.listToArray(functionList).get(1);
        return ListSup.listToArray(cmdStr);
        //去除首尾[]
//        functionList=functionList.substring(1,functionList.length()-1).trim();
//        String[] function=functionList.split("\\s+");
//        ArrayList<String> result=new ArrayList<>();
//        if(!hasPara(functionList)){
//            for(int i=0;i<function.length;i++)
//                result.add(function[i]);
//            return result;
//        }
//        int countLeft=0;
//
//        //过渡第一部分
//        int i=0;
//        if(hasPara(functionList)) {
//            for (i = 0; i < function.length; i++) {
//                String temp = function[i];
//                if (temp.equals("["))
//                    countLeft++;
//                else if (temp.equals("]")) {
//                    countLeft--;
//                    if (countLeft == 0)
//                        break;
//                } else
//                    continue;
//            }
//            i++;
//            countLeft = 0;
//        }
//        for(;i<function.length;i++){
//            String temp=function[i];
//            if(temp.equals("[")){
//                if(countLeft!=0)
//                    result.add(temp);
//                countLeft++;
//            }
//            else if(temp.equals("]")){
//                countLeft--;
//                if(countLeft!=0)
//                    result.add(temp);
//                else
//                    break;
//            }
//            else
//                result.add(temp);
//        }
//
//        return result;
    }

    public static boolean hasPara(String functionList){
        String[] function=functionList.split("\\s+");
        int countLeft=0;
        for(int i=0;i<function.length;i++){
            if(function[i].equals("[")) {
                if(countLeft==0&&i!=0)
                    return false;
                countLeft++;
            }
            else if(function[i].equals("]")){
                countLeft--;
                if(countLeft==0){
                    if(i==function.length-1)
                        return false;
                    else
                        return true;
                }
            }
        }

        return false;
    }
}
