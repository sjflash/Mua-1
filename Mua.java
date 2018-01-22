package MUA;

import java.util.*;
import java.lang.*;
import java.util.ArrayList;

import static MUA.Preformate.isBracketsPatch;

public class Mua {
    //变量的hashmap
    private static HashMap<String,String> nameSpace= new HashMap<>();
    //输出buffer
    //private static ArrayList<String> printBuffer=new ArrayList<>();
    private static Scanner input=new Scanner(System.in);

    //下面两个均用于处理表达式中的括号
    private static Stack<Integer> myLeftBracket=new Stack<>();//存储当前左括号的位置
    private static boolean returnFlag=false;//保存是从)返回，还是其他返回，false为其他返回

    //以下变量用于提供函数支持
    //指示是否在函数之中
    private static boolean isInFunction=false;
    //局部变量表
    private static HashMap<String,String> childNameSpace=new HashMap<>();
    //函数调用位置堆栈，用于在设置函数的返回值确定返回值要放的位置
    private static Stack<Integer> funcPos=new Stack<>();
    //局部变量表堆栈，用于在多层函数调用时，保存前一层函数的局部变量
    private static Stack< HashMap<String,String> > nameSpaceStack=new Stack<>();
    //指示是否有返回值
    private static boolean hasOutput=false;
    //返回值的值，与hasOutput搭配使用
    private static String output="";

    //主函数
    public static void main(String[] args) {
        //输出欢迎信息和版本号，输出命令输入提示符>>>
        PrintInfo.printVerAuthor();
        //添加预有变量pi
        nameSpace.put("\"pi","3.1415926535");

        String command;
        //处理读入的命令直到读取到单行的exit为止

        while(!((command = input.nextLine().trim()).equals("exit"))) {
//            int count=0;
//            if(command.endsWith("[")){
//                count++;
//                while(count!=0) {
//                    String temp = input.nextLine().trim();
//                    command+=" "+temp;
//                    if(temp.equals("["))
//                        count++;
//                    if(temp.equals("]"))
//                        count--;
//                }
//            }
            //Note: 添加限定, word中不能以'['结尾
            if(command.endsWith("[")){
                String line=input.nextLine().trim();
                while(!line.equals("")){
                    command=command+" "+line;
                    line=input.nextLine().trim();
                }
            }

            //建立备份HasmMap保存命令执行前的所有变量信息，若命令出错则可用于恢复
            //HashMap<String,String> tempNameSpace=new HashMap<>();
            //tempNameSpace.putAll(nameSpace);

            //清空输出buffer
            //printBuffer.clear();

            //预格式化[]
            command=Preformate.preformatBracket(command,"[");

            //若左右括号不匹配，则提示错误
            if(!isBracketsPatch(command,"[")){
                PrintInfo.printSyntaxError("'[' and ']' don't match!");
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

            //添加空格进行运算符的分隔,并添加括号
            if(ComputeExpression.hasOp(command))
                command=ComputeExpression.addBrackets(command);
            //检测左右括号匹配
            if(!isBracketsPatch(command,"(")){
                PrintInfo.printSyntaxError("'(' and ')' don't match!");
                System.out.print(">>>");
                continue;
            }

            //[]的匹配处理
            String[] allCommands=command.split("\\s+");
            allCommands=Commands.bracketToString(allCommands);//将[]分别匹配转换为字符串

            //测试语句
//            ArrayList<String> temp=ListSup.listToArray(allCommands[2]);
//            System.out.println(ListSup.listToArray(temp.get(1)));


            //命令执行
            ArrayList<String> allCommandsList=new ArrayList<>();
            for(String tempCommand:allCommands)
                allCommandsList.add(tempCommand);

            //若命令成功执行，则输出全部printBuffer，备份的tempNameSpace失效
            if(executeCommands(allCommandsList,0));
                //for(String tempPrint:printBuffer)
                    //System.out.println(tempPrint);

            //若执行失败，则printBuffer的输出全部无效，使用备份的tempNameSpace恢复原来的nameSpace
            //else
                //nameSpace=tempNameSpace;

            System.out.print(">>>");
        }

        //收到命令exit，提示退出信息
        System.out.println("GoodBye!");
    }

    //命令执行函数，version 3
    private static boolean executeCommands(ArrayList<String> allCommands,int tempIndex){
        //如果有表达式的话用来保存表达式
        String expression="";
        int leftBracket=0;
        for(int i=tempIndex;i<allCommands.size();i++) {
            String tempString=allCommands.get(i);
            String temptemp;
            if(!Commands.testLegal(tempString)||tempString.startsWith(":")||ComputeExpression.isOperator(tempString)||isAFunction(tempString)){

                //处理 ':'命令
                if(tempString.startsWith(":")){
                    if(!Commands.testLegal(tempString.substring(1))){
                        PrintInfo.printSyntaxError(tempString.substring(1)+" is not a legal name");
                        return false;
                    }

                    else{
                        temptemp=cmdThing("\""+tempString.substring(1));
                        if(temptemp.equals(""))
                            return false;
                        else if(i==0) {
                            System.out.println("thing "+"\""+tempString.substring(1)+" is "+temptemp+", but don't know what to do with it");
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
                    case "make": {
                        if (!supportExcute(allCommands, tempString, i, 2))
                            return false;
                        temptemp = cmdMake(allCommands.get(i + 1), allCommands.get(i + 2));
                        if (temptemp.equals(""))
                            return false;
                        else {
                            allCommands.set(i, temptemp);
                            allCommands.remove(i + 1);
                            allCommands.remove(i + 1);
                            if (tempIndex != 0)
                                return true;
                            else if (i == 0)
                                allCommands.remove(i--);
                        }
                        break;
                    }
                    case "thing":{
                        if(!supportExcute(allCommands,tempString,i,1))
                            return false;
                        temptemp=cmdThing(allCommands.get(i+1));
                        if(temptemp.equals(""))
                            return false;
                        else if(i==0) {
                            System.out.println("thing "+allCommands.get(i+1)+" is "+temptemp+", but didn't know what to with it");
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
                    }
                    case "erase": {
                        if (!supportExcute(allCommands, tempString, i, 1))
                            return false;
                        if (!isValidName(allCommands.get(i + 1)))
                            return false;
                        else {
                            boolean myTemp = cmdErase(allCommands.get(i + 1));
                            allCommands.set(i, Boolean.toString(myTemp));
                            allCommands.remove(i + 1);
                            if (i == 0)
                                allCommands.remove(i--);
                        }
                        if (tempIndex != 0)
                            return true;
                        break;
                    }
                    case "isname": {
                        if (!supportExcute(allCommands, tempString, i, 1))
                            return false;
                        if (!isValidName(allCommands.get(i + 1)))
                            return false;
                        else {
                            boolean myTemp = cmdIsName(allCommands.get(i + 1));
                            allCommands.set(i, Boolean.toString(myTemp));
                            allCommands.remove(i + 1);
                            if (i == 0)
                                allCommands.remove(i--);

                        }
                        if (tempIndex == 0)
                            return true;
                        break;
                    }
                    case "print": {
                        if (!supportExcute(allCommands, tempString, i, 1))
                            return false;
                        if (cmdPrint(allCommands.get(i + 1))) {
                            allCommands.remove(i + 1);
                            allCommands.remove(i--);
                            if (tempIndex != 0)
                                return true;
                        } else
                            return false;
                        break;
                    }
                    case "read": {
                        allCommands.set(i, Commands.cmdRead());
                        if (tempIndex != 0)
                            return true;
                        break;
                    }
                    case "readlist": {
                        allCommands.set(i, Commands.cmdReadList());
                        if (tempIndex != 0)
                            return true;
                        break;
                    }
                    case "add": case "sub": case "mul": case "div": case "mod": {
                        if (!supportExcute(allCommands, tempString, i, 2))
                            return false;
                        if (!NumSupport.isDigits(allCommands.get(i + 1)) || !NumSupport.isDigits(allCommands.get(i + 2))) {
                            PrintInfo.printSyntaxError(allCommands.get(i + 1) + " and " + allCommands.get(i + 2) + " are not all digits");
                            return false;
                        } else {
                            if (NumSupport.clearZero(allCommands.get(i + 2)).equals("0") && (allCommands.get(i).equals("mod") || allCommands.get(i).equals("div"))) {
                                PrintInfo.printPrompt("the second number can't be 0");
                                return false;
                            } else {
                                allCommands.set(i, Commands.cmdCompute(allCommands.get(i + 1), allCommands.get(i + 2), tempString));
                                allCommands.remove(i + 2);
                                allCommands.remove(i + 1);
                                if (i == 0)
                                    allCommands.remove(i--);
                            }
                        }
                        if (tempIndex != 0)
                            return true;
                        break;
                    }
                    case "eq": case "gt": case "lt": {
                        if (!supportExcute(allCommands, tempString, i, 2))
                            return false;
                        if (NumSupport.isDigits(allCommands.get(i + 1)) && NumSupport.isDigits(allCommands.get(i + 2))) {
                            allCommands.set(i, Boolean.toString(Commands.cmdCompare(allCommands.get(i + 1), allCommands.get(i + 2), tempString)));
                            allCommands.remove(i + 2);
                            allCommands.remove(i + 1);
                            if (i == 0)
                                allCommands.remove(i--);
                        } else if (allCommands.get(i + 1).startsWith("\"") && allCommands.get(i + 2).startsWith("\"")) {
                            allCommands.set(i, Boolean.toString(Commands.cmdCompare(allCommands.get(i + 1), allCommands.get(i + 2), tempString)));
                            allCommands.remove(i + 2);
                            allCommands.remove(i + 1);
                            if (i == 0)
                                allCommands.remove(i--);
                        } else {
                            PrintInfo.printSyntaxError(allCommands.get(i + 1) + " and " + allCommands.get(i + 2) + " are not all digits or words");
                            return false;
                        }
                        if (tempIndex != 0)
                            return true;
                        break;
                    }
                    case "and": case  "or": {
                        if (!supportExcute(allCommands, tempString, i, 2))
                            return false;
                        if (!allCommands.get(i + 1).toLowerCase().equals("false") && !allCommands.get(i + 1).toLowerCase().equals("true")) {
                            PrintInfo.printSyntaxError(allCommands.get(i + 1) + " is not boolean");
                            return false;
                        }
                        if (!allCommands.get(i + 2).toLowerCase().equals("false") && !allCommands.get(i + 2).toLowerCase().equals("true")) {
                            PrintInfo.printSyntaxError(allCommands.get(i + 2) + " is not boolean");
                            return false;
                        } else {
                            allCommands.set(i, Boolean.toString(Commands.cmdAndOr(Boolean.parseBoolean(allCommands.get(i + 1)), Boolean.parseBoolean(allCommands.get(i + 2)), tempString)));
                            allCommands.remove(i + 2);
                            allCommands.remove(i + 1);
                            if (i == 0)
                                allCommands.remove(i--);
                        }
                        if (tempIndex != 0)
                            return true;
                        break;
                    }
                    case "not": {
                        if (!supportExcute(allCommands, tempString, i, 1))
                            return false;
                        if (!allCommands.get(i + 1).toLowerCase().equals("false") && !allCommands.get(i + 1).toLowerCase().equals("true")) {
                            PrintInfo.printSyntaxError(allCommands.get(i + 1) + " is not boolean");
                            return false;
                        } else {
                            allCommands.set(i, Boolean.toString(Commands.cmdNot(Boolean.parseBoolean(allCommands.get(i + 1)))));
                            allCommands.remove(i + 1);
                            if (i == 0)
                                allCommands.remove(i--);
                        }
                        if (tempIndex != 0)
                            return true;
                        break;
                    }
                    case "repeat":{
                        if(!supportExcute(allCommands,tempString,i,2))
                            return false;
                        if(!NumSupport.isDigits(allCommands.get(i+1))) {
                            PrintInfo.printSyntaxError("repeat don't get a num");
                            return false;
                        }
                        if(!allCommands.get(i+2).startsWith("[")||!allCommands.get(i+2).endsWith("]")) {
                            PrintInfo.printSyntaxError(allCommands.get(i+2)+" is not a list");
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
                        break;}
                    case "+": case "-": case "*": case "/": case "%": case "^":
                        break;
                    case "(": {
                        myLeftBracket.push(i);
                        while (!returnFlag) {
                            if (!executeCommands(allCommands, i + 1))//递归先执行（后面的值
                                return false;
                        }
                        returnFlag = false;

                        if (i == 0) {
                            PrintInfo.printPrompt("the result is " + allCommands.get(i) + ", but don't know what to do");
                            allCommands.remove(i--);
                        }
                        if(tempIndex!=0)
                            return true;
                        break;
                    }
                    case ")": {
                        String tempExpression = "";
                        int myInt;
                        try {
                            myInt = myLeftBracket.pop();
                        } catch (Exception ex) {
                            PrintInfo.printRuntimeError("( and ) don't match");
                            return false;
                        }
                        for (int tempInt = myInt; tempInt <= i; tempInt++)
                            tempExpression += allCommands.get(tempInt);//产生表达式，从左括号到当前右括号
                        try {
                            String tempResult = ComputeExpression.calculate(tempExpression);//计算值，可能会抛出异常
                            allCommands.set(myInt, tempResult);//将左括号的值设置为运算结果
                            for (int tempInt = i; tempInt > myInt; tempInt--)
                                allCommands.remove(tempInt);//移除左括号(当前保存的是运算结果)后面的东西
                        } catch (Exception ex) {
                            System.out.println(ex.getMessage());
                            return false;
                        }
                        returnFlag = true;//立flag表示从(中返回
                        return true;
                    }
                    case "stop":
                        return true;
                    case "output": {
                        int func = funcPos.peek();
                        if (!supportExcute(allCommands, tempString, i, 1))
                            return false;
                        hasOutput = true;
                        output = allCommands.get(i + 1);
                        allCommands.remove(i + 1);
                        allCommands.remove(i--);
                        if (tempIndex != 0)
                            return true;
                        break;
                    }
                    //v0.7及以后添加
                    case "export": {
                        boolean myTemp = cmdExport();
                        allCommands.set(i, Boolean.toString(myTemp));
                        if (i == 0)
                            allCommands.remove(i--);
                        if (tempIndex != 0)
                            return true;
                        break;
                    }
                    case "isnumber": case "isword": case "islist": case "isbool": case "isempty": {
                        if (!supportExcute(allCommands, tempString, i, 1))
                            return false;
                        boolean myTemp = false;
                        switch (tempString) {
                            case "isnumber":
                                myTemp = TypeJudge.isNum(allCommands.get(i + 1));
                                break;
                            case "isword":
                                myTemp = TypeJudge.isWord(allCommands.get(i + 1));
                                break;
                            case "islist":
                                myTemp = TypeJudge.isList(allCommands.get(i + 1));
                                break;
                            case "isbool":
                                myTemp = TypeJudge.isBool(allCommands.get(i + 1));
                                break;
                            case "isempty":
                                myTemp = TypeJudge.isEmpty(allCommands.get(i + 1));
                                break;
                            default:
                                break;
                        }

                        allCommands.set(i, Boolean.toString(myTemp));
                        allCommands.remove(i + 1);
                        if (i == 0)
                            allCommands.remove(i--);
                        if (tempIndex != 0)
                            return true;
                        break;
                    }
                    case "random": case "sqrt": case "int": {
                        if (!supportExcute(allCommands, tempString, i, 1))
                            return false;
                        if (!NumSupport.isDigits(allCommands.get(i + 1))) {
                            PrintInfo.printRuntimeError(allCommands.get(i + 1) + " is not a valid num.");
                            return false;
                        }
                        if(Double.valueOf(allCommands.get(i+1)) > Double.MAX_VALUE){
                            PrintInfo.printRuntimeError("所给数值超出了Double的范围");
                            return false;
                        }
                        if (tempString.equals("int")) {
                            int tempInt = NumSupport.cmdInt(Double.valueOf(allCommands.get(i + 1)));
                            allCommands.set(i, String.valueOf(tempInt));
                        } else if (tempString.equals("random")) {
                            double tempDouble = NumSupport.cmdRandom(Double.valueOf(allCommands.get(i + 1)));
                            allCommands.set(i, String.valueOf(tempDouble));
                        } else {
                            double tempDouble = NumSupport.cmdSqrt(Double.valueOf(allCommands.get(i + 1)));
                            allCommands.set(i, String.valueOf(tempDouble));
                        }
                        allCommands.remove(i + 1);
                        if (i == 0)
                            allCommands.remove(i--);
                        if (tempIndex != 0)
                            return true;
                        break;
                    }
                    case "run": {
                        if (!supportExcute(allCommands, tempString, i, 1))
                            return false;
                        String tempList = allCommands.get(i + 1).trim();//后面带的这个参数，可能不是list

                        if (!TypeJudge.isList(tempList)) {
                            PrintInfo.printRuntimeError(tempList + " is not a valid list");
                            return false;
                        }

                        boolean tempBool = executeCommands(ListSup.listToArray(tempList), 0);
                        allCommands.set(i, Boolean.toString(tempBool));
                        allCommands.remove(i + 1);
                        if (i == 0)
                            allCommands.remove(i--);
                        if (tempIndex != 0)
                            return true;
                        break;
                    }
                    case "wait": {
                        if (!supportExcute(allCommands, tempString, i, 1))
                            return false;
                        String tempStr = allCommands.get(i + 1);
                        if (!NumSupport.isDigits(tempStr) || Double.valueOf(tempStr) < 0) {
                            PrintInfo.printRuntimeError(tempStr + " 不是有效数字或者小于0");
                            return false;
                        }
                        try {
                            Thread.sleep(Integer.parseInt(tempStr));
                            allCommands.remove(i + 1);
                            allCommands.remove(i--);
                            if (tempIndex != 0)
                                return true;
                        } catch (InterruptedException ex) {
                            PrintInfo.printRuntimeError("InterruptedException");
                            return false;
                        }
                        break;
                    }
                    case "erall": case "poall": {
                        HashMap<String, String> tempNS;
                        tempNS = isInFunction ? childNameSpace : nameSpace;

                        if (tempString.equals("erall"))
                            tempNS.clear();
                        else
                            Commands.cmdPoall(tempNS);
                        allCommands.remove(i--);
                        if (tempIndex != 0)
                            return true;
                        break;
                    }
                    case "save": case "load": {
                        if (!supportExcute(allCommands, tempString, i, 1))
                            return false;
                        String tempStr = allCommands.get(i + 1);
                        if (!TypeJudge.isWord(tempStr) || tempStr.equals("\"")) {
                            PrintInfo.printRuntimeError("word for filename is invalid");
                            return false;
                        }

                        HashMap<String, String> tempNameSpace;
                        tempNameSpace = isInFunction ? childNameSpace : nameSpace;
                        Boolean tempBool = false;
                        if (tempString.equals("save"))
                            tempBool = Commands.cmdSave(tempNameSpace, tempStr.substring(1));
                        else
                            tempBool = Commands.cmdLoad(tempNameSpace, tempStr.substring(1));
                        allCommands.set(i, Boolean.toString(tempBool));
                        allCommands.remove(i + 1);
                        if (i == 0)
                            allCommands.remove(i--);
                        break;
                    }
                    case "word": {
                        if (!supportExcute(allCommands, tempString, i, 2))
                            return false;
                        String arg1 = allCommands.get(i + 1);
                        String arg2 = allCommands.get(i + 2);
                        String result = "";
                        if (!TypeJudge.isWord(arg1)) {
                            PrintInfo.printRuntimeError(arg1 + " is not a valid word");
                            return false;
                        }
                        if (!TypeJudge.isWord(arg1) && !TypeJudge.isBool(arg1) && !TypeJudge.isNum(arg1)) {
                            PrintInfo.printRuntimeError(arg2 + " is not a valid word or number or bool");
                            return false;
                        }
                        if (TypeJudge.isWord(arg2))
                            result = arg1 + arg2.substring(1);
                        else
                            result = arg1 + arg2;
                        allCommands.set(i, result);
                        allCommands.remove(i + 2);
                        allCommands.remove(i + 1);
                        if (i == 0)
                            allCommands.remove(i--);
                        if (tempIndex != 0)
                            return true;
                        break;
                    }
                    case "if":{
                        if (!supportExcute(allCommands, tempString, i, 3))
                            return false;
                        String arg1 = allCommands.get(i + 1);
                        if(arg1.startsWith("\""))
                            arg1=arg1.substring(1);
                        String arg2 = allCommands.get(i + 2);
                        String arg3 = allCommands.get(i + 3);
                        if(!TypeJudge.isBool(arg1)){
                            PrintInfo.printRuntimeError(arg1+" is not a boolean");
                            return false;
                        }
                        if(!TypeJudge.isList(arg2)||!TypeJudge.isList(arg3)){
                            PrintInfo.printRuntimeError(arg2+" or "+arg3+" are not all list");
                            return false;
                        }
                        allCommands.remove(i + 3);
                        allCommands.remove(i+2);
                        allCommands.remove(i+1);
                        allCommands.remove(i);

                        ArrayList<String> tempArr=null;
                        if(arg1.toLowerCase().equals("true"))
                            tempArr=ListSup.listToArray(arg2);
                        else
                            tempArr=ListSup.listToArray(arg3);

                        for(int index=tempArr.size()-1;index>=0;index--)
                            allCommands.add(i,tempArr.get(index));
                        i--;//将1减1以便可以继续执行
                        if (tempIndex != 0)
                            return true;
                        break;
                    }
                    case "sentence": case "list": {
                        if (!supportExcute(allCommands, tempString, i, 2))
                            return false;
                        ArrayList<String> tempArr = new ArrayList<>();
                        String arg1=allCommands.get(i+1);
                        String arg2=allCommands.get(i+2);
                        if(tempString.equals("list")) {//如果是list不用拆开，两个直接组装
                            tempArr.add(arg1);
                            tempArr.add(arg2);
                        }else{//如果是sentence，list需要拆开，再进行组装。
                            if(TypeJudge.isList(arg1))
                                tempArr.addAll(ListSup.listToArray(arg1));
                            else
                                tempArr.add(arg1);
                            if(TypeJudge.isList(arg2))
                                tempArr.addAll(ListSup.listToArray(arg2));
                            else
                                tempArr.add(arg2);
                        }
                        allCommands.set(i, ListSup.ArrayToList(tempArr));
                        allCommands.remove(i + 2);
                        allCommands.remove(i + 1);
                        if (i == 0)
                            allCommands.remove(i--);
                        if (tempIndex != 0)
                            return true;
                        break;
                    }
                    case "join": {
                        if (!supportExcute(allCommands, tempString, i, 2))
                            return false;
                        String arg1 = allCommands.get(i + 1);
                        String arg2 = allCommands.get(i + 2);
                        if (!TypeJudge.isList(arg1)) {
                            PrintInfo.printRuntimeError(arg1 + " is not a valid list");
                            return false;
                        }
                        ArrayList<String> tempArr = ListSup.listToArray(arg1);
                        tempArr.add(arg2);
                        allCommands.set(i, ListSup.ArrayToList(tempArr));
                        allCommands.remove(i + 2);
                        allCommands.remove(i + 1);
                        if (i == 0)
                            allCommands.remove(i--);
                        if (tempIndex != 0)
                            return true;
                        break;
                    }
                    case "first": case "last": case "butfirst": case "butlast": {
                        if (!supportExcute(allCommands, tempString, i, 1))
                            return false;
                        String arg = allCommands.get(i + 1);
                        String result = "";
                        if (!TypeJudge.isWord(arg) && !TypeJudge.isList(arg)) {
                            PrintInfo.printRuntimeError(arg + " is not a valid word or list");
                            return false;
                        }
                        if (TypeJudge.isWord(arg)) {
                            switch (tempString) {
                                case "first":
                                    result = arg.length() == 1 ? "" : "\"" + String.valueOf(arg.charAt(1));
                                    break;
                                case "last":
                                    result = arg.length() == 1 ? "" : "\"" + String.valueOf(arg.charAt(arg.length() - 1));
                                    break;
                                case "butfirst":
                                    result = arg.length() <= 2 ? "" : "\"" + arg.substring(2);
                                    break;
                                case "butlast":
                                    result = arg.length() <= 2 ? "" : arg.substring(0, arg.length() - 1);
                                    break;
                            }
                        } else {
                            ArrayList<String> temp = null;
                            switch (tempString) {
                                case "first":
                                    result = ListSup.listToArray(arg).get(0);
                                    if (!result.startsWith("\""))
                                        result = "\"" + result;
                                    break;
                                case "last":
                                    temp = ListSup.listToArray(arg);
                                    result = temp.get(temp.size() - 1);
                                    if (!result.startsWith("\""))
                                        result = "\"" + result;
                                    break;
                                case "butfirst":
                                    temp = ListSup.listToArray(arg);
                                    temp.remove(0);
                                    result = ListSup.ArrayToList(temp);
                                    break;
                                case "butlast":
                                    temp = ListSup.listToArray(arg);
                                    temp.remove(temp.size() - 1);
                                    result = ListSup.ArrayToList(temp);
                                    break;
                            }
                        }
                        allCommands.set(i, result);
                        allCommands.remove(i + 1);
                        if (i == 0)
                            allCommands.remove(i--);
                        if (tempIndex != 0)
                            return true;
                        break;
                    }
                    case "abs": {
                        if (!supportExcute(allCommands, tempString, i, 1))
                            return false;
                        String arg = allCommands.get(i + 1);
                        if (!TypeJudge.isNum(arg)) {
                            PrintInfo.printRuntimeError(arg + " is not a valid num for abs");
                            return false;
                        }
                        allCommands.set(i, String.valueOf(Math.abs(Double.parseDouble(arg))));
                        allCommands.remove(i + 1);
                        if (i == 0)
                            allCommands.remove(i--);
                        if (tempIndex != 0)
                            return true;
                        break;
                    }
                    default:{//在v0.6以及之后的版本中,此时的default表示读到了一个函数
                        String function;
                        //设置在函数中
                        boolean backUpIsInFunc=isInFunction;
                        isInFunction=true;

                        if(isInFunction){
                            if(childNameSpace.containsKey("\""+tempString))
                                function=childNameSpace.get("\""+tempString);
                            else
                                function=nameSpace.get("\""+tempString);
                        }
                        else
                            function=nameSpace.get("\""+tempString);
                        ArrayList<String> paras=FunctionSupport.getParameters(function);
                        ArrayList<String> commands=FunctionSupport.getAllCommands(function);
                        if(!supportExcute(allCommands,tempString,i,paras.size()))
                            return false;

                        //保存函数调用前的堆栈
                        HashMap<String,String> tempNS=new HashMap<>();
                        tempNS.putAll(childNameSpace);
                        nameSpaceStack.push(tempNS);

                        funcPos.push(i);

                        //保存有无返回值状态
                        boolean backUpHasOutput=hasOutput;

                        //添加局部变量
                        for(int index=0;index<paras.size();index++)
                            childNameSpace.put("\""+paras.get(index),allCommands.get(i+index+1));

                        //开始执行
                        if(!executeCommands(commands,0))
                            return false;

                        //恢复之前的状态
                        isInFunction=backUpIsInFunc;
                        childNameSpace=nameSpaceStack.pop();
                        funcPos.pop();

                        for(int which=paras.size();which>0;which--)
                            allCommands.remove(i + which);
                        if(!hasOutput||i==0)
                            allCommands.remove(i--);
                        else
                            allCommands.set(i,output);
                        hasOutput=backUpHasOutput;

                        if(tempIndex!=0)
                            return true;
                        break;}
                }
            }
            else
                continue;
        }

        if(allCommands.size()!=0&&tempIndex==0) {
            for (String tempOut : allCommands)
                System.out.println("can't solve: " + tempOut);
        }
        return true;
    }

    //cmd函数得到的参数中不会有未处理的命令，字的字面量带有"，若非字面量则不应该有"
    //make执行函数，返回值为""时表示有错误发生，返回make得到的值
    private static String cmdMake(String tempWord,String tempValue){
        //检查tempWord是否是字
        if(!tempWord.startsWith("\"")) {
            PrintInfo.printSyntaxError("the word literal must starts with '\"'");
            return "";
        }

        //检查是否以：开头
        if(tempWord.startsWith("\":")){
            PrintInfo.printSyntaxError("the name can't start with ':'");
            return "";
        }

        String temp=tempWord.substring(1);

        //检查是否是空串
        if(temp.equals("\"")) {
            PrintInfo.printSyntaxError("the name can't be empty");
            return "";
        }

        //检查是否是保留字
        if(!Commands.testLegal(temp)){
            PrintInfo.printSyntaxError("can't used reserved word as name");
            return "";
        }

        //检查是否不以字母或下划线开头
        if(!Commands.testFirstChar(temp)){
            PrintInfo.printSyntaxError("name must start with letter or _");
            return "";
        }

        //在此处不区分value是何种类型 Note：可能要区别list和bool
        //将键值对放入nameSpace中
        if(tempValue.startsWith("\"")) {
            if(isInFunction)
                childNameSpace.put(tempWord,tempValue);
            else
                nameSpace.put(tempWord, tempValue);
        }
        else if(tempValue.startsWith("["))
            if(isBracketsPatch(tempValue,"[")) {
                if(isInFunction)
                    childNameSpace.put(tempWord,tempValue);
                else
                    nameSpace.put(tempWord, tempValue);
            }
            else{
                PrintInfo.printSyntaxError("the '[' and ']' not match");
                return "";
            }
        else if(tempValue.toLowerCase().equals("false")||tempValue.toLowerCase().equals("true")) {
            if(isInFunction)
                childNameSpace.put(tempWord, tempValue);
            else
                nameSpace.put(tempWord, tempValue);
        }
        else {
            if (NumSupport.isDigits(tempValue)) {
                if (isInFunction)
                    childNameSpace.put(tempWord, NumSupport.clearZero(tempValue));
                else
                    nameSpace.put(tempWord, NumSupport.clearZero(tempValue));

            } else {
                PrintInfo.printSyntaxError(tempValue + "is invalid");
                return "";
            }
        }

        return tempValue;
    }

    //thing和：的执行函数，当使用':'调用thing时，输入的tempWord应当包含"
    private static String cmdThing(String tempWord){
        if(!tempWord.startsWith("\"")){
            PrintInfo.printSyntaxError(tempWord+" is not a word");
            return "";
        }
        else if(tempWord.equals("\"")){
            PrintInfo.printPrompt("the name can't be empty");
            return "";
        }
        else if(!Commands.testLegal(tempWord.substring(1))){
            PrintInfo.printSyntaxError("word name can't be a reserved word");
            return "";
        }
        else if(!Commands.testFirstChar(tempWord.substring(1))){
            PrintInfo.printSyntaxError("name must start with letter or _");
            return "";
        }
        else if(!childNameSpace.containsKey(tempWord)&&!nameSpace.containsKey(tempWord)) {
            PrintInfo.printRuntimeError("not have word named " + "\""+tempWord.substring(1)+"\"");
            return "";
        }
        else {
            if(isInFunction)
                if(childNameSpace.containsKey(tempWord))
                    return childNameSpace.get(tempWord);
            return nameSpace.get(tempWord);
        }
    }

    //erase执行函数，返回erase的成功与否
    private static boolean cmdErase(String tempWord){
        if(!childNameSpace.containsKey(tempWord)&&!nameSpace.containsKey(tempWord)){
            PrintInfo.printPrompt("didn't have key named "+"\""+tempWord.substring(1)+"\"");
            return false;
        }
        else{
            if(isInFunction)
                if (childNameSpace.containsKey(tempWord)) {
                    childNameSpace.remove(tempWord);
                    return true;
                }
            nameSpace.remove(tempWord);
            return true;
        }
    }

    //isname执行函数，返回tempWord的存在与否
    private static boolean cmdIsName(String tempWord){
        if(tempWord.equals("\"")){
            PrintInfo.printPrompt("the name can't be empty");
            return false;
        }
        else
            return (nameSpace.containsKey(tempWord) || childNameSpace.containsKey(tempWord));
    }

    //print的执行函数，输出tempWord的值
    private static boolean cmdPrint(String tempWord){
        if(tempWord.startsWith("\"")) {
            if(tempWord.equals("\""))
                System.out.println("\"\"");
            else
                System.out.println(tempWord.substring(1));
        }
        else if(tempWord.equals("true")||tempWord.equals("false"))
            System.out.println(tempWord);
        else if(tempWord.startsWith("["))
            System.out.println(tempWord);
        else if(NumSupport.isDigits(tempWord))
            System.out.println(NumSupport.clearZero(tempWord));
        else{
            PrintInfo.printSyntaxError(tempWord+" is invalid");
            return false;
        }
        return true;
    }

    //repeate的执行函数，传入的参数number必须是一个数,list含有左右的[],返回成功执行与否
    private static boolean cmdRepeat(String number,String commandList){
        int tempNum=Integer.valueOf(number);
        if(tempNum<0){
            PrintInfo.printRuntimeError("repeat negative times\n");
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

    //将本地make的值输出到全局
    private static boolean cmdExport(){
        if(!isInFunction) {
            PrintInfo.printRuntimeError("当前不在函数中");
            return false;
        }
        Iterator iter=childNameSpace.entrySet().iterator();
        //遍历局部变量并放到全局变量中，若有同名变量则以局部变量为准
        while(iter.hasNext()){
          Map.Entry entry = (Map.Entry) iter.next();
          nameSpace.put(entry.getKey().toString(),entry.getValue().toString());
        }

        return true;
    }

    //检测是否是一个合法的变量名，不能是保留字，不能为空，不能以非数字和下划线开头，不能不是单词
    private static boolean isValidName(String temp){
        if(!temp.startsWith("\"")){
            PrintInfo.printSyntaxError(temp+" is not a word");
            return false;
        }
        else if(temp.equals("\"")){
            PrintInfo.printSyntaxError("name can't be empty");
            return false;
        }
        else if(!Commands.testLegal(temp.substring(1))){
            PrintInfo.printSyntaxError("name can't be reserved word");
            return false;
        }
        else if(!Commands.testFirstChar(temp.substring(1))){
            PrintInfo.printSyntaxError("name must start with letter or _");
            return false;
        }
        else
            return true;
    }

    //命令辅助执行函数，用于在命令执行中判断参数个数和调用递归
    private static boolean supportExcute(ArrayList<String> allCommands,String command,int i,int paraNum){
        for(int j=1;j<=paraNum;j++){
            if(i+paraNum>=allCommands.size()){
                PrintInfo.printSyntaxError(command+" needs 2 arguments but you give "+(allCommands.size()-i-1));
                return false;
            }
            String tempString=allCommands.get(i+j);
            if(!Commands.testLegal(tempString)||tempString.startsWith(":")||ComputeExpression.isOperator(tempString)||isAFunction(tempString))
                if(!executeCommands(allCommands,i+j))
                    return false;
                else
                    j--;
        }
        return true;
    }

    private static boolean isAFunction(String name){
        name="\""+name;
        String value="";
        if(isInFunction){
            if(childNameSpace.containsKey(name)){
                value=childNameSpace.get(name);
                if(value.startsWith("["))
                    return true;
                else
                    return false;
            }
        }
        if(nameSpace.containsKey(name)){
            value=nameSpace.get(name);
            if(value.startsWith("["))
                return true;
            else
                return false;
        }

        return false;
    }

}

