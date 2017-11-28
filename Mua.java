package MUA;

import com.sun.javafx.scene.control.skin.VirtualFlow;
import com.sun.org.apache.xpath.internal.operations.Bool;

import java.util.*;
import java.lang.*;
import java.util.ArrayList;
import java.util.List;

public class Mua {
    
    //版本号
    private static final String myVersion="Version 0.0.3\n";

    //变量的hashmap
    private static HashMap<String,String> nameSpace= new HashMap<>();

    //输出buffer
    private static ArrayList<String> printBuffer=new ArrayList<>();

    private static Scanner input=new Scanner(System.in);

    //主函数
    public static void main(String[] args) {
        //输出欢迎信息和版本号，输出命令输入提示符>>>
        {
            System.out.println("----------------------------------------------");
            System.out.print(
                    "           " + " __  __             \n" +
                            "           " + "|  \\/  |_   _  __ _ \n" +
                            "           " + "| |\\/| | | | |/ _\\ |\n" +
                            "           " + "| |  | | |_| | (_| |\n" +
                            "Welcome to " + "|_|  |_|\\__/_|\\__/\\| " + myVersion);
            System.out.println("----------------------------------------------");
            System.out.println("   visit http://zhengcz.cn to get more info   ");
            System.out.println("              Author:st4rlgiht                ");
            System.out.println("----------------------------------------------");
            System.out.print(">>>");
        }

        String command;

        //处理读入的命令直到读取到单行的exit为止
        while(!((command = input.nextLine().trim()).equals("exit"))) {

            //建立备份HasmMap保存命令执行前的所有变量信息
            HashMap<String,String> tempNameSpace=new HashMap<>();
            tempNameSpace.putAll(nameSpace);

            //清空输出buffer
            printBuffer.clear();

            //预格式化[]
            command=preformatBracket(command);
            //若左右括号不匹配，则提示错误
            if(!isBracketsPatch(command)){
                printSyntaxError("'[' and ']' not match!");
                System.out.print(">>>");
                continue;
            }

            //处理注释
            if(command.startsWith("//")){
                System.out.print(">>>");
                continue;
            }
            if(command.contains(" //")){
                command=command.substring(0,command.indexOf(" //"));
                if(command.equals("")){
                    System.out.print(">>>");
                    continue;
                }
            }

            //[]的匹配处理
            String[] allCommands=command.split("\\s+");
            allCommands=bracketToString(allCommands);//将[]分别匹配转换为字符串

            //命令执行
            ArrayList<String> allCommandsList=new ArrayList<>();
            for(String tempCommand:allCommands)
                allCommandsList.add(tempCommand);

            //若命令成功执行，则输出全部printBuffer，备份的tempNameSpace失效
            if(executeCommands(allCommandsList,0))
                for(String tempPrint:printBuffer)
                    System.out.println(tempPrint);

            //若执行失败，则printBuffer的输出全部无效，使用备份的tempNameSpace恢复原来的nameSpace
            else
                nameSpace=tempNameSpace;

            System.out.print(">>>");
        }

        //收到命令exit，提示退出信息
        System.out.println("GoodBye!");
    }

    //cmd函数得到的参数中不会有未处理的命令，字的字面量带有"，若非字面量则不应该有"

    //make执行函数，返回值为""时表示有错误发生，返回make得到的值
    private static String cmdMake(String tempWord,String tempValue){
        //检查tempWord是否是字
        if(!tempWord.startsWith("\"")) {
            printSyntaxError("the word literal must starts with '\"'");
            return "";
        }

        //检查是否以：开头
        if(tempWord.startsWith("\":")){
            printSyntaxError("the name can't start with ':'");
            return "";
        }

        String temp=tempWord.substring(1);

        //检查是否是空串
        if(temp.equals("\"")) {
            printSyntaxError("the name can't be empty");
            return "";
        }

        //检查是否是保留字
        if(!testLegal(temp)){
            printSyntaxError("can't used reserved word as name");
            return "";
        }

        //检查是否不以字母或下划线开头
        if(!testFirstChar(temp)){
            printSyntaxError("name must start with letter or _");
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
                printSyntaxError("the '[' and ']' not match");
                return "";
            }
        else if(tempValue.toLowerCase().equals("false")||tempValue.toLowerCase().equals("true"))
            nameSpace.put(tempWord,tempValue);
        else
            if(NumSupport.isDigits(tempValue))
                nameSpace.put(tempWord,NumSupport.clearZero(tempValue));
            else{
                printSyntaxError(tempValue+"is invalid");
                return "";
            }

        return tempValue;
    }

    //thing和：的执行函数，当使用':'调用thing时，输入的tempWord应当包含"
    private static String cmdThing(String tempWord){
        if(!tempWord.startsWith("\"")){
            printSyntaxError(tempWord+" is not a word");
            return "";
        }
        else if(tempWord.equals("\"")){
            printPrompt("the name can't be empty");
            return "";
        }
        else if(!testLegal(tempWord.substring(1))){
            printSyntaxError("word name can't be a reserved word");
            return "";
        }
        else if(!testFirstChar(tempWord.substring(1))){
            printSyntaxError("name must start with letter or _");
            return "";
        }
        else if(!nameSpace.containsKey(tempWord)) {
            printPrompt("not have word named " + "\""+tempWord.substring(1)+"\"");
            return "";
        }
        else
            return nameSpace.get(tempWord);
    }

    //erase执行函数，返回erase的成功与否
    private static boolean cmdErase(String tempWord){
        if(!nameSpace.containsKey(tempWord)){
            printPrompt("didn't have key named "+"\""+tempWord.substring(1)+"\"");
            return false;
        }
        else{
            nameSpace.remove(tempWord);
            return true;
        }
    }

    //isname执行函数，返回tempWord的存在与否
    private static boolean cmdIsName(String tempWord){
        if(tempWord.equals("\"")){
            printPrompt("the name can't be empty");
            return false;
        }
        else
            return nameSpace.containsKey(tempWord);
    }

    //print的执行函数，输出tempWord的值
    private static boolean cmdPrint(String tempWord){
        if(tempWord.startsWith("\""))
            printBuffer.add(tempWord+"\"");
        else if(tempWord.equals("true")||tempWord.equals("false"))
            printBuffer.add(tempWord);
        else if(tempWord.startsWith("["))
            printBuffer.add(tempWord);
        else if(NumSupport.isDigits(tempWord))
            printBuffer.add(NumSupport.clearZero(tempWord));
        else{
            printSyntaxError(tempWord+" is invalid");
            return false;
        }
        return true;
    }

    //read的执行函数，读入一个合法的数字或者单词并返回
    private static String cmdRead() {
        System.out.print("read:");
        String temp=input.nextLine();
        char[] tempArray=temp.toCharArray();
        while(true){
            if(tempArray[0]=='\"')
                return temp;
            else
                if(NumSupport.isDigits(temp))
                    return NumSupport.clearZero(temp);
                else
                    printPrompt(temp+" is invalid, input again");
            System.out.print("read:");
            temp=input.nextLine();
            tempArray=temp.toCharArray();
        }

    }

    //readlist的执行函数，读入一个合法的list并返回
    private static String cmdReadList(){
        System.out.print("readlist:");
        Scanner input=new Scanner(System.in);
        String temp=input.nextLine();
        return "[ "+temp+" ]";
    }

    //add,sub,mul,div,mod的执行函数，传入的两个值应该是准确的可以运算的数字，且若第一个为thing应该去给它赋予新的值
    //若command为div和mod，那么第二个参数不应该为0
    private static String cmdCompute(String str1,String str2,String command){
        double num1=Double.parseDouble(NumSupport.clearZero(str1));
        double num2=Double.parseDouble(NumSupport.clearZero(str2));
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

    //gt，eq，lt的执行函数，传入的参数str1和str2确保都为word，或者都为num
    private static boolean cmdCompare(String str1,String str2,String command){
        if(str1.startsWith("\"")){
            str1=str1.substring(1);
            str2=str2.substring(1);
            int flag=str1.compareTo(str2);
            return flagJudge(flag,command);
        }
        else{
            double num1=Double.parseDouble(NumSupport.clearZero(str1));
            double num2=Double.parseDouble(NumSupport.clearZero(str2));
            int flag=Double.compare(num1,num2);

            return flagJudge(flag,command);
        }

    }

    //and，or的执行函数，返回逻辑运算结果
    private static boolean cmdAndOr(boolean bool1,boolean bool2,String command){
        if(command.equals("and"))
            return (bool1 & bool2);
        else
            return (bool1 | bool2);
    }

    //not的执行函数，返回相反逻辑值
    private static boolean cmdNot(boolean bool){
        return !bool;
    }

    //repeate的执行函数，传入的参数number必须是一个数,list含有左右的[],返回成功执行与否
    private static boolean cmdRepeat(String number,String commandList){
        int tempNum=Integer.valueOf(number);
        if(tempNum<0){
            printRuntimeError("repeat negative times\n");
            return false;
        }

        //去除首尾[]
        commandList=commandList.substring(1,commandList.length()-1).trim();
        if(commandList.equals(""))
            return true;

        //命令执行
        ArrayList<String> allCommandsList=new ArrayList<>();
        for(String tempCommand:commandList.split("\\s+"))
            allCommandsList.add(tempCommand);

        ArrayList<String> backUp=new ArrayList<>(allCommandsList);
        for(int i=0;i<tempNum;i++){
            if(!executeCommands(allCommandsList,0))
                return false;//若执行失败返回false
            allCommandsList=new ArrayList<>(backUp);
        }
        return true;
    }

    //输出语法错误信息
    private static void printSyntaxError(String prompt){
        System.out.println("Syntax Error: " +prompt);
    }
    //输出警告信息，本次尚未用到
    //private static void printWarning(String prompt){
    //    System.out.println("Warning: "+prompt);
    //}
    //输出一些提示信息
    private static void printPrompt(String prompt){
        System.out.println("Prompt: "+prompt);
    }

    //输出运行错误
    private static void printRuntimeError(String error){
        System.out.println("Error: "+error);
    }

    //检测变量名称是否与保留字相同，调用testLegal前应该事先去除"
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
            case "gt":
            case "lt":
            case "and":
            case "or":
            case "not":
            case "repeat":
                return false;
            default:
                return true;
        }
    }

    //检测变量名开头是否符合规范，即必须以字母或者下划线开头
    private static boolean testFirstChar(String temp){
        return (Character.isLetter(temp.charAt(0))||temp.charAt(0)=='_');
    }

    //比较运算符eq,gt,lt执行结果的统一返回函数，即统一进行返回，避免单个命令判断返回的重复
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

    //判断list的左右括号个数是否匹配
    private static boolean isBracketsPatch(String temp){
        //temp=preformatBracket(temp);
        int countLeft=0;
        int countRight=0;
        String[] tempArray=temp.split("\\s+");
        for(int i=0;i<tempArray.length;i++)
            if(tempArray[i].equals("["))
                countLeft++;
            else if(tempArray[i].equals("]"))
                countRight++;
            else
                continue;

        return countLeft==countRight;
    }

    //[]的预格式化函数，预格式化后[和]的左右都有空格（头尾非全部有）
    private static String preformatBracket(String temp){
        String[] tempArray=temp.split("\\s+");
        List<String> tempList=Arrays.asList(tempArray);
        tempList=new ArrayList<String>(tempList);

        for(int i=0;i<tempList.size();i++){
            String tempString=tempList.get(i);
            if(tempString.startsWith("[")&&tempString.length()!=1){
                tempList.set(i,tempString.substring(1));
                tempList.add(i,"[");
            }
            else
                if(tempString.endsWith("]")&&tempString.length()!=1&&!tempString.startsWith("\"")){
                    tempList.set(i,tempString.substring(0,tempString.length()-1));
                    tempList.add(i+1,"]");
                    i--;
                }
        }
        temp="";
        for(int i=0;i<tempList.size();i++)
            temp+=tempList.get(i)+" ";
        return temp;
    }

    //检测是否是一个合法的变量名，不能是保留字，不能为空，不能以非数字和下划线开头，不能不是单词
    private static boolean isValidName(String temp){
        if(!temp.startsWith("\"")){
            printSyntaxError(temp+" is not a word");
            return false;
        }
        else if(temp.equals("\"")){
            printSyntaxError("name can't be empty");
            return false;
        }
        else if(!testLegal(temp.substring(1))){
            printSyntaxError("name can't be reserved word");
            return false;
        }
        else if(!testFirstChar(temp.substring(1))){
            printSyntaxError("name must start with letter or _");
            return false;
        }
        else
            return true;
    }

    //传入整行命令，返回将其分隔后的数组，同时将整句[]转化为一个String
    private static String[] bracketToString(String[] allCommands){
        int i,j;
        ArrayList<String>  tempAllCommands=new ArrayList<String>();
        for(i=0;i<allCommands.length;i++) {
            if(allCommands[i].equals("[")){
                int countLeft=0;
                for(j=i;j<allCommands.length;j++){
                    if(allCommands[j].equals("["))
                        countLeft++;
                    else if(allCommands[j].equals("]"))
                        if(countLeft!=0)
                            countLeft--;
                        else
                            break;
                    else
                        continue;
                }
                String temp="";
                if(j==allCommands.length)   //防止[]的情况，此时]匹配会越界
                    j-=1;
                for(int k=i;k<=j;k++){
                    if(k!=j)
                        temp+=allCommands[k]+" ";
                    else
                        temp+=allCommands[k];
                }
                tempAllCommands.add(temp);
                i=j+1;
            }
            else
                tempAllCommands.add(allCommands[i]);
        }

        return tempAllCommands.toArray(new String[tempAllCommands.size()]);
    }

    //命令执行函数，version 2
    private static boolean executeCommands(ArrayList<String> allCommands,int tempIndex){
        for(int i=tempIndex;i<allCommands.size();i++) {
            String tempString=allCommands.get(i);
            String temptemp;
            if(!testLegal(tempString)||tempString.startsWith(":")){

                //处理 ':'命令
                if(tempString.startsWith(":")){
                    if(!testLegal(tempString.substring(1))){
                        printSyntaxError(tempString.substring(1)+" is not a legal name");
                        return false;
                    }

                    else{
                        temptemp=cmdThing("\""+tempString.substring(1));
                        if(temptemp.equals(""))
                            return false;
                        else if(i==0) {
                            printBuffer.add("thing "+"\""+tempString.substring(1)+" is "+temptemp+", but didn't know what to do with it");
                            allCommands.remove(i--);
                        }
                        else
                            allCommands.set(i,temptemp);
                    }
                    if(tempIndex!=0)
                        return true;
                    else
                        continue;
                }

                //处理除':'之外的命令
                switch(tempString){
                    case "make":
                        if(!supportExcute(allCommands,tempString,i,2))
                            return false;
                        temptemp=cmdMake(allCommands.get(i+1),allCommands.get(i+2));
                        if(temptemp.equals(""))
                            return false;
                        else {
                            allCommands.set(i,temptemp);
                            allCommands.remove(i+1);
                            allCommands.remove(i+1);
                            if(tempIndex!=0)
                                return true;
                            else
                                if(i==0)
                                    allCommands.remove(i--);
                        }
                        break;
                    case "thing":
                        if(!supportExcute(allCommands,tempString,i,1))
                            return false;
                        temptemp=cmdThing(allCommands.get(i+1));
                        if(temptemp.equals(""))
                            return false;
                        else if(i==0) {
                            printBuffer.add("thing "+allCommands.get(i+1)+" is "+temptemp+", but didn't know what to with it");
                            allCommands.remove(i+1);
                            allCommands.remove(i--);
                        }
                        else {
                            allCommands.set(i, temptemp);
                            allCommands.remove(i + 1);
                        }

                        if(tempIndex!=0)
                            return true;
                        break;
                    case "erase":
                        if(!supportExcute(allCommands,tempString,i,1))
                            return false;
                        if(!isValidName(allCommands.get(i+1)))
                            return false;
                        else{
                            boolean myTemp=cmdErase(allCommands.get(i+1));
                            allCommands.set(i,Boolean.toString(myTemp));
                            allCommands.remove(i+1);
                            if(i==0)
                                allCommands.remove(i--);
                        }
                        if(tempIndex!=0)
                            return true;
                        break;
                    case "isname":
                        if(!supportExcute(allCommands,tempString,i,1))
                            return false;
                        if(!isValidName(allCommands.get(i+1)))
                            return false;
                        else{
                            boolean myTemp=cmdIsName(allCommands.get(i+1));
                            allCommands.set(i,Boolean.toString(myTemp));
                            allCommands.remove(i+1);
                            if(i==0)
                                allCommands.remove(i--);

                        }
                        if(tempIndex==0)
                            return true;
                        break;
                    case "print":
                        if(!supportExcute(allCommands,tempString,i,1))
                            return false;
                        if(cmdPrint(allCommands.get(i+1))){
                            allCommands.remove(i+1);
                            allCommands.remove(i--);
                            if(tempIndex!=0)
                                return true;
                        }
                        else
                            return false;
                        break;
                    case "read":
                        allCommands.set(i,cmdRead());
                        if(tempIndex!=0)
                            return true;
                        break;
                    case "readlist":
                        allCommands.set(i,cmdReadList());
                        if(tempIndex!=0)
                            return true;
                        break;
                    case "add":
                    case "sub":
                    case "mul":
                    case "div":
                    case "mod":
                        if(!supportExcute(allCommands,tempString,i,2))
                            return false;
                        if(!NumSupport.isDigits(allCommands.get(i+1))||!NumSupport.isDigits(allCommands.get(i+2))){
                            printSyntaxError(allCommands.get(i+1)+" and "+allCommands.get(i+2)+" are not all digits");
                            return false;
                        }
                        else{
                            if(NumSupport.clearZero(allCommands.get(i+2)).equals("0")&&(allCommands.get(i).equals("mod")||allCommands.get(i).equals("div"))){
                                printPrompt("the second number can't be 0");
                                return false;
                            }
                            else{
                                allCommands.set(i,cmdCompute(allCommands.get(i+1),allCommands.get(i+2),tempString));
                                allCommands.remove(i+2);
                                allCommands.remove(i+1);
                                if(i==0)
                                    allCommands.remove(i--);
                            }
                        }
                        if(tempIndex!=0)
                            return true;
                        break;
                    case "eq":
                    case "gt":
                    case "lt":
                        if(!supportExcute(allCommands,tempString,i,2))
                            return false;
                        if(NumSupport.isDigits(allCommands.get(i+1))&&NumSupport.isDigits(allCommands.get(i+2))){
                            allCommands.set(i,Boolean.toString(cmdCompare(allCommands.get(i+1),allCommands.get(i+2),tempString)));
                            allCommands.remove(i+2);
                            allCommands.remove(i+1);
                            if(i==0)
                                allCommands.remove(i--);
                        }
                        else if(allCommands.get(i+1).startsWith("\"")&&allCommands.get(i+2).startsWith("\"")){
                            allCommands.set(i,Boolean.toString(cmdCompare(allCommands.get(i+1),allCommands.get(i+2),tempString)));
                            allCommands.remove(i+2);
                            allCommands.remove(i+1);
                            if(i==0)
                                allCommands.remove(i--);
                        }
                        else{
                            printSyntaxError(allCommands.get(i+1)+" and "+allCommands.get(i+2)+" are not all digits or words");
                            return false;
                        }
                        if(tempIndex!=0)
                            return true;
                        break;
                    case "and":
                    case  "or":
                        if(!supportExcute(allCommands,tempString,i,2))
                            return false;
                        if(!allCommands.get(i+1).toLowerCase().equals("false")&&!allCommands.get(i+1).toLowerCase().equals("true")){
                            printSyntaxError(allCommands.get(i+1)+" is not boolean");
                            return false;
                        }
                        if(!allCommands.get(i+2).toLowerCase().equals("false")&&!allCommands.get(i+2).toLowerCase().equals("true")){
                            printSyntaxError(allCommands.get(i+2)+" is not boolean");
                            return false;
                        }
                        else{
                            allCommands.set(i,Boolean.toString(cmdAndOr(Boolean.parseBoolean(allCommands.get(i+1)),Boolean.parseBoolean(allCommands.get(i+2)),tempString)));
                            allCommands.remove(i+2);
                            allCommands.remove(i+1);
                            if(i==0)
                                allCommands.remove(i--);
                        }
                        if(tempIndex!=0)
                            return true;
                        break;
                    case "not":
                        if(!supportExcute(allCommands,tempString,i,1))
                            return false;
                        if(!allCommands.get(i+1).toLowerCase().equals("false")&&!allCommands.get(i+1).toLowerCase().equals("true")){
                            printSyntaxError(allCommands.get(i+1)+" is not boolean");
                            return false;
                        }
                        else{
                            allCommands.set(i,Boolean.toString(cmdNot(Boolean.parseBoolean(allCommands.get(i+1)))));
                            allCommands.remove(i+1);
                            if(i==0)
                                allCommands.remove(i--);
                        }
                        if(tempIndex!=0)
                            return true;
                        break;
                    case "repeat":
                        if(!supportExcute(allCommands,tempString,i,2))
                            return false;
                        if(!NumSupport.isDigits(allCommands.get(i+1))) {
                            printSyntaxError("repeate don't get a num");
                            return false;
                        }
                        if(!allCommands.get(i+2).startsWith("[")||!allCommands.get(i+2).endsWith("]")) {
                            printSyntaxError(allCommands.get(i+2)+" is not a list");
                            return false;
                        }
                        else{
                            allCommands.set(i, Boolean.toString(cmdRepeat(allCommands.get(i+1),allCommands.get(i+2))));
                            if(allCommands.get(i).equals("true")){
                                allCommands.remove(i+2);
                                allCommands.remove(i+1);
                                if(i==0)
                                    allCommands.remove(i--);
                            }
                            else
                                return false;
                        }
                        if(tempIndex!=0)
                            return true;
                        break;
                    default:
                        break;
                }
            }
            else
                continue;
        }

        if(allCommands.size()!=0&&tempIndex==0)
            for(String tempOut:allCommands)
                    printBuffer.add("can't solve: "+tempOut);

        return true;
    }

    //命令辅助执行函数，用于在命令执行中判断参数个数和调用递归
    private static boolean supportExcute(ArrayList<String> allCommands,String command,int i,int paraNum){
        for(int j=1;j<=paraNum;j++){
            if(i+paraNum>=allCommands.size()){
                printSyntaxError(command+" needs 2 arguments but you give "+(allCommands.size()-i-1));
                return false;
            }
            String tempString=allCommands.get(i+j);
            if(!testLegal(tempString)||tempString.startsWith(":"))
                if(!executeCommands(allCommands,i+j))
                    return false;
                else
                    j--;
        }
        return true;
    }

}

