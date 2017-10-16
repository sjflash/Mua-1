package MUA;
import java.util.*;
import java.lang.*;

public class Mua {
    private static HashMap<String,String> nameSpace= new HashMap<>();
    private static Scanner input=new Scanner(System.in);
    //public static HashMap tempNameSpace=new HashMap();
    //主函数
    public static void main(String[] args) {
        System.out.println("----------------------------------------------");
        System.out.print(
         "           "+      " __  __             \n" +
         "           "+      "|  \\/  |_   _  __ _ \n" +
         "           "+      "| |\\/| | | | |/ _\\ |\n" +
         "           "+      "| |  | | |_| | (_| |\n" +
         "Welcome to "+      "|_|  |_|\\__/_|\\__/\\|" +" Version 0.0.1\n");
        System.out.println("----------------------------------------------");
        System.out.print(">>>");

        String command="";

        //处理读入的命令直到读取到单行的exit为止
        while(!command.equals("exit")) {
            command = input.nextLine();//以行读入，隔断处理
//            String[] allCommands=command.split("\\s+");
//            for(int i=0;i<allCommands.length;i++)
//                if(allCommands[i])
        }

        System.out.println("GoodBye!");
    }

    //函数得到的参数中不会有未处理的命令，字的字面量带有"，若非字面量则不应该有"
    //返回值为""时表示有错误发生
    public static String cmdMake(String tempWord,String tempValue){
        //检查tempWord的合法性
        if(!tempWord.startsWith("\"")) {
            printSyntaxErrot("the word literal must starts with '\"'");
            return "";
        }
        if(tempWord.startsWith("\":")){
            printSyntaxErrot("the name can't start with ':'");
            return "";
        }

        String temp=tempWord.substring(1);
        if(temp.equals("")) {
            printSyntaxErrot("the name can't be empty");
            return "";
        }

        if(!testLegal(temp)){
            printSyntaxErrot("can't used reserved word as name");
            return "";
        }

        //在此处不区分value是何种类型 Note：可能要区别list和bool
        //将键值对放入nameSpace中
        if(tempValue.startsWith("\""))
            nameSpace.put(tempWord,tempValue);
        else if(tempValue.startsWith("["))
            if(isBracketsPatch(tempValue))
                nameSpace.put(tempWord,tempValue);
            else{
                printSyntaxErrot("the '[' and ']' didn't patch");
                return "";
            }
        else if(tempValue.equals("false")||tempValue.equals("true"))
            nameSpace.put(tempWord,tempValue);
        else
            if(isDigits(tempValue))
                nameSpace.put(tempWord,clearZero(tempValue));
            else{
                printSyntaxErrot(tempValue+"is not a valid value");
                return "";
            }

        return tempValue;
    }

    //当使用':'调用thing时，传送的tempWord应当加上"
    public static String cmdThing(String tempWord){
        if(!nameSpace.containsKey(tempWord)) {
            printPrompt("didn't have key named " + tempWord.substring(1));
            return "";
        }
        else
            return nameSpace.get(tempWord);
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
            System.out.println(nameSpace.get(tempWord));
        else
            printPrompt("didn't have key named "+tempWord.substring(1));
    }
    public static String cmdRead() {
        String temp=input.next();
        char[] tempArray=temp.toCharArray();
        while(true){
            if(tempArray[0]=='\"')
                return temp;
            else
                if(isDigits(temp))
                    return clearZero(temp);
                else
                    printPrompt(temp+" is invalid, input again:");
            temp=input.next();
            tempArray=temp.toCharArray();
        }

    }
//    public static String cmdReadList(){
//        //ToDo：该部分与main中的一同处理，返回的值应该由[]包含，其中除了四个基本类型外没有要转换的命令了
//    }

    //Note:传入的两个值应该是准确的可以运算的数字，且若第一个为thing应该去给它赋予新的值
    public static String cmdCompute(String str1,String str2,String command){
        double num1=Double.parseDouble(clearZero(str1));
        double num2=Double.parseDouble(clearZero(str2));
        double result;
        switch (command) {
            case "add":
                result = num1 + num2;
                break;
            case "sub":
                result = num1 - num2;
                break;
            case "mul":
                result = num1 * num2;
                break;
            case "div":
                result = num1 / num2;
                break;
            default:
                result = num1 % num2;
        }
        return Double.toString(result);
    }
    //传入前确保str1和str2都为word，或者都为num
    public static boolean cmdCompare(String str1,String str2,String command){
        if(str1.startsWith("\"")){
            str1=str1.substring(1);
            str2=str2.substring(1);
            int flag=str1.compareTo(str2);
            return flagJudge(flag,command);
        }
        else{
            double num1=Double.parseDouble(clearZero(str1));
            double num2=Double.parseDouble(clearZero(str2));
            int flag=Double.compare(num1,num2);

            return flagJudge(flag,command);
        }

    }
    public static boolean cmdAndOr(boolean bool1,boolean bool2,String command){
        if(command.equals("and"))
            return (bool1 & bool2);
        else
            return (bool1 | bool2);
    }
    public static boolean cmdNot(boolean bool){
        return !bool;
    }

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
        if(temp.equals("."))
            return false;
        if(temp.equals("-"))
            return false;

        if(temp.startsWith("-"))
            temp=temp.substring(1); //删除'-'，以便判断时不会出现中间有'-'的情况
        if(!isNumValid(temp))
            return false;

        int pointCount = countPoints(temp);
        return (pointCount == 0 || pointCount == 1);
    }
    //isDigits的辅助函数，接受的String中的第一个'-'已经清理，所以后面不能再有'-'
    private static boolean isNumValid(String temp){
        char[] tempArray=temp.toCharArray();
        for(char tempChar:tempArray)
            if(tempChar>='0'&&tempChar<='9')
                continue;
            else if(tempChar!='.')
                return false;
        return true;
    }
    //使用isDigits判断过的值，若合法则可以用clearZero来将其格式化
    private static String clearZero(String temp){
        boolean flag=false;//false表示正数
        if (temp.startsWith("-")) {
            temp = temp.substring(1);
            flag=true;
        }
        int pointCount=countPoints(temp);
        if(pointCount==0)
            while(temp.startsWith("0")&&temp.length()!=1)
                temp=temp.substring(1);
        else{
            if(temp.endsWith("."))
                temp+="0";
            if(temp.startsWith("."))
                temp="0"+temp;
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
        if(flag)
            return "-"+temp;
        else
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

    //检测名称是否与保留字相同，调用testLegal前应该事先去除'\"'
    private static boolean testLegal(String temp){
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
                return false;
            default:
                return true;
        }
    }

    //逻辑运算符结果统一返回函数
    private static boolean flagJudge(int flag,String command){
        if(flag<0)
            switch (command){
                case "eq":
                    return false;
                case "gt":
                    return false;
                case "lt":
                    return true;
                default:
                    return true;
            }

        else if(flag==0)
            switch(command){
                case "eq":
                    return true;
                case "gt":
                    return false;
                case "lt":
                    return false;
                default:
                    return true;
            }
        else
            switch (command){
                case "eq":
                    return false;
                case "gt":
                    return true;
                case "lt":
                    return false;
                default:
                    return true;
            }
    }

    //判断list的括号是否匹配
    private static boolean isBracketsPatch(String temp){
        String tempStr=temp.replaceAll(" ", "");
        char[] tempArray=temp.toCharArray();
        int i;
        int j=tempArray.length-1;
        for(i=0;i<tempArray.length;i++){
            if(tempArray[i]=='['){
                while(j>=0){
                    if(tempArray[j]==']')
                        break;
                    else
                        j--;
                }
                if(i>j)
                    return false;
            }
        }
        return true;
    }

}
