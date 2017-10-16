package MUA;
import java.util.*;
import java.lang.*;

import java.util.*;

public class Mua {
    private static HashMap nameSpace= new HashMap();
    private static Scanner input=new Scanner(System.in);
    //public static HashMap tempNameSpace=new HashMap();

    //主函数
    public static void main(String[] args) {
        System.out.println("Mua v0.0.1");
        System.out.println(">>>");

        String command="";

        //处理读入的命令直到读取到单行的exit为止
        while(!command.equals("exit")) {
            command = input.nextLine();//以行读入，隔断处理
        }
    }

    //函数得到的参数中不会有未处理的命令，字的字面量带有"，若非字面量则不应该有"
    public static String cmdMake(String tempWord,String tempValue){
        //检查tempWord的合法性
        if(tempWord.toCharArray()[0]!='\"') {
            printSyntaxErrot("the word literal must starts with '\"'");
            return "";
        }
        if(tempWord.toCharArray()[0]==':'){
            printSyntaxErrot("the name must starts with ':'");
            return "";
        }

        String temp=tempWord.substring(1);
        if(temp.equals("")) {
            printSyntaxErrot("the name can't be empty");
            return "";
        }

        switch(temp) {
            case "make":
            case "thing":
            case "erase":
            case "isname":
            case "print":
            case "readlist":
            case "read":
            case "add":
            case "sub":
            case "mul":
            case "div":
            case "mod":
            case "eq":
            case "gl":
            case "lt":
            case "and":
            case "or":
            case "not":
                printSyntaxErrot("can't used reserved word as name");
                return "";
            default:
                break;
        }

        //在此处不区分value是何种类型 Note：可能要区别list和bool
        //将键值对放入nameSpace中
        nameSpace.put(tempWord,tempValue);
        return ":"+temp;
    }

    //当使用':'调用thing时，传送的tempWord应当加上"
    public static String cmdThing(String tempWord){
        if(!nameSpace.containsKey(tempWord)) {
            printPrompt("didn't have key named " + tempWord.substring(1));
            return "";
        }
        else
            return (String)nameSpace.get(tempWord);
    }
    public static boolean cmdErase(String tempWord){
        if(!nameSpace.containsKey(tempWord)){
            printPrompt("didn't have key named "+tempWord.substring(1));
            return false;
        }
        else{
            nameSpace.remove(tempWord);
            return true;
        }
    }
    public static boolean cmdIsName(String tempWord){
        return nameSpace.containsKey(tempWord);
    }

    public static void cmdPrint(String tempWord){
        if(nameSpace.containsKey(tempWord))
            System.out.println((String) nameSpace.get(tempWord));
        else
            printPrompt("didn't have key named "+tempWord.substring(1));
    }
    public static String cmdRead() {
        String temp=input.next();
        char[] tempArray=temp.toCharArray();
        while(true){
            if(tempArray[0]=='\"')
                return temp;
            else{
                int pointCount = countPoints(temp);
                if (pointCount == 0) {
                    if(isDigits(temp))
                        return clearZero(temp);
                    else
                        printPrompt(temp + " is not a valid number!");
                }
                else if (pointCount == 1) {
                    if(temp.startsWith(".")&&temp.endsWith("."))
                        printPrompt(temp + " is not a valid number!");
                    else if(temp.startsWith(".")&&!(temp.endsWith(".")))
                        if(isDigits(temp.substring(1)))
                            return clearZero("0"+temp);
                        else
                            printPrompt(temp + " is not a valid number!");
                    else if(!temp.startsWith(".")&&temp.endsWith("."))
                        if(isDigits(temp.substring(0,temp.length()-1)))
                            return clearZero(temp.substring(0,temp.length()-1));
                        else
                            printPrompt(temp + " is not a valid number!");
                    else
                        if(isDigits(temp.substring(0,temp.indexOf('.')))&&isDigits(temp.substring(temp.indexOf('.')+1,temp.length())))
                            return clearZero(temp);
                        else
                            printPrompt(temp + " is not a valid number!");
                }
                else {
                    printPrompt(temp + " is not a valid number!");
                }
            }
            temp=input.next();
            tempArray=temp.toCharArray();
        }

    }
//    public static String cmdReadList(){
//
//    }

    private static void printSyntaxErrot(String prompt){
        System.out.println("Systax Error: " +prompt);
    }
    private static void printWarning(String prompt){
        System.out.println("Warning: "+prompt);
    }
    private static void printPrompt(String prompt){
        System.out.println("Prompt: "+prompt);
    }

    private static boolean isDigits(String temp){
        char[] tempArray=temp.toCharArray();
        for(char tempChar:tempArray)
            if(!Character.isDigit(tempChar))
                return false;
        return true;
    }
    private static String clearZero(String temp){
        int pointCount=countPoints(temp);
        if(pointCount==0)
            while(temp.startsWith("0")&&temp.length()!=1)
                temp=temp.substring(1);
        else{
            String tempLeft=temp.substring(0,temp.indexOf('.'));
            String tempRight=temp.substring(temp.indexOf('.')+1,temp.length());
            while(tempLeft.startsWith("0")&&tempLeft.length()!=1)
                tempLeft=tempLeft.substring(1);
            while(tempRight.endsWith("0")&&tempRight.length()!=1)
                tempRight=tempLeft.substring(0,tempRight.length()-1);
            temp=tempLeft+"."+tempRight;
        }
        if(temp.endsWith(".0"))
            temp=temp.substring(0,temp.indexOf(".0"));
        return temp;
    }
    private static int countPoints(String temp){
        int count = 0;
        char[] tempArray=temp.toCharArray();
        for(char tempChar:tempArray)
            if(tempChar=='.')
                count++;
        return count;
    }

}
