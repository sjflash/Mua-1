package MUA;

import javax.xml.bind.ValidationEvent;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.time.temporal.ValueRange;
import java.util.*;

import static java.math.BigDecimal.ROUND_HALF_UP;

public class ComputeExpression {
    private static int scale=16;

//    public static void main(String[] args) {
//        Scanner input=new Scanner(System.in);
//        String expression=input.next();
//        try{
//            System.out.println(calculate(expression));
//        }catch (Exception ex){
//            System.out.println("Illegal Expression: "+ex.getMessage());
//        }
//    }

    private static HashMap<String,Integer> opPrior=new HashMap<>();

    public static String calculate(String expression) throws Exception{
        expression=preFormate(expression);
        Stack<String> postfix=new Stack<>();
        Stack<String> result=new Stack<>();
        putAllPriors();
        try{
            postfix=toPostfix(expression);
        }catch(Exception ex){
            throw ex;
        }

        Collections.reverse(postfix);//后缀表达式倒置进行计算
        String firstValue,secondValue,currentValue;
        try{
            while(!postfix.isEmpty()){
                currentValue=postfix.pop();
                if(!isOperator(currentValue))
                    result.push(currentValue);//若是数字则压栈
                else{
                    secondValue=result.pop();//若不是数字则弹出两个进行计算，将结果压回栈中
                    firstValue=result.pop();
                    String tempResult=compute(firstValue,secondValue,currentValue);
                    result.push(tempResult);
                }
            }
            if(result.size()!=1)//如果最终栈里不只有一个值，那么说明表达式有错
                throw new Exception("the number of operators and nums don't match");
        }catch (Exception ex){
            throw ex;
        }

        return result.pop();

    }

    //将表达式转换为后缀表达式栈
    public static Stack<String> toPostfix(String expression) throws Exception{
        char[] allChars=expression.toCharArray();

        Stack<String> operators=new Stack<>();
        operators.push("=");//栈底元素，级别最低，减少empty判断用
        Stack<String> result=new Stack<>();
        int currentPos=0;
        boolean flag=false;//控制这个数字是否是负数

        while(currentPos<allChars.length){
            //如果是运算符
            if(isOperator(String.valueOf(allChars[currentPos]))){
                char tempChar=allChars[currentPos];

                //如果碰到右边的括号则进行弹出操作
                if(tempChar==')'){
                    while(!operators.peek().equals("(")&&!operators.equals("="))
                        result.push(operators.pop());
                    if(operators.peek().equals("("))
                        operators.pop();
                    else
                        throw new Exception("The ( and ) don't match");//若发现没有左括号则抛出异常
                }
                else if(tempChar=='(')//遇见左括号则直接压入
                    operators.push(String.valueOf(tempChar));
                else{
                    //处理作为正负号的+和-
                    if(tempChar=='-'||tempChar=='+') {
                        if (currentPos == 0 || allChars[currentPos - 1] == '(') {
                            if (tempChar == '-')
                                flag = !flag;
                            currentPos++;
                            continue;
                        }
                    }

                    //将当前的运算符压入堆栈，弹出优先级比它高的或者想等的
                    while (!isPrior(String.valueOf(tempChar), operators.peek()))
                        result.push(operators.pop());
                    operators.push(String.valueOf(tempChar));

                    //判断运算符后面跟的是不是合法的值
                    int tempCurrent=currentPos+1;
                    //如果到了末尾就直接退出
                    if(tempCurrent==allChars.length)
                        break;
                    String next=String.valueOf(allChars[tempCurrent]);
                    if(isOperator(next))
                        if(next.equals("*")||next.equals("/")||next.equals("%")||next.equals(")"))
                            throw new Exception("operator follows illegal * or / or % or )");
                }
                currentPos++;
            }

            //如果碰到空字符
            else if(allChars[currentPos]==' ')
                currentPos++;

                //如果不是运算符则获取这个数字
            else{
                int tempCurrentPos=currentPos;
                String tempNum="";
                while(tempCurrentPos<allChars.length&&!isOperator(String.valueOf(allChars[tempCurrentPos])))
                    tempNum+=allChars[tempCurrentPos++];

                if(flag){
                    tempNum="-"+tempNum;
                    flag=false;
                }
                if(NumSupport.isDigits(tempNum))
                    result.push(NumSupport.clearZero(tempNum));
                else
                    throw new Exception(tempNum+" is Invalid");
                currentPos=tempCurrentPos;
            }
        }

        while(!operators.peek().equals("="))
            result.push(String.valueOf(operators.pop()));

        return result;
    }

    //判断是不是运算符或者括号
    public static boolean isOperator(String myStr){
        return (myStr.equals("+")||myStr.equals("-")||myStr.equals("*")||myStr.equals("/")||myStr.equals("%")||myStr.equals("(")||myStr.equals(")"));
    }

    //判断栈底元素与当前元素的优先级，如果当前元素高则true
    public static boolean isPrior(String charOp,String charPeek){
        return opPrior.get(charOp)>opPrior.get(charPeek);
    }

    //保存优先级关系
    public static void putAllPriors(){
        opPrior.put("=",0);
        opPrior.put("(",1);
        opPrior.put(")",1);
        opPrior.put("+",2);
        opPrior.put("-",2);
        opPrior.put("*",3);
        opPrior.put("/",3);
        opPrior.put("%",3);
    }

    public static String compute(String value1, String value2, String operator) throws Exception{
        BigDecimal v1=new BigDecimal(value1);
        BigDecimal v2=new BigDecimal(value2);
        if(operator.equals("+"))
            return v1.add(v2).toString();
        else if(operator.equals("-"))
            return v1.subtract(v2).toString();
        else if(operator.equals("*"))
            return v1.multiply(v2).toString();
        else if(operator.equals("/")) {
            return v1.divide(v2, scale, ROUND_HALF_UP).toString();
        } else if(operator.equals("%"))
            return v1.divideAndRemainder(v2)[1].toString();
        else
            throw new Exception("one num is invalid");
    }

    //预格式化函数，转化++ -+ +- -- -(
    public static String preFormate(String expression) {
        int pp=expression.indexOf("++");
        int ps=expression.indexOf("+-");
        int sp=expression.indexOf("-+");
        int ss=expression.indexOf("--");
        while(pp!=-1||ps!=-1||sp!=-1||ss!=-1){
            if(pp!=-1)
                expression=expression.substring(0,pp)+"+"+expression.substring(pp+2,expression.length());
            else if(ps!=-1)
                expression=expression.substring(0,ps)+"-"+expression.substring(ps+2,expression.length());
            else if(sp!=-1)
                expression=expression.substring(0,sp)+"-"+expression.substring(sp+2,expression.length());
            else
                expression=expression.substring(0,ss)+"+"+expression.substring(ss+2,expression.length());
            pp=expression.indexOf("++");
            ps=expression.indexOf("+-");
            sp=expression.indexOf("-+");
            ss=expression.indexOf("--");
        }

        int negativeBracket=expression.indexOf("-(");
        while(negativeBracket!=-1){
            expression=expression.substring(0,negativeBracket)+"-1*("+expression.substring(negativeBracket+2,expression.length());
            negativeBracket=expression.indexOf("-(");
        }
        return NumSupport.clearZero(expression);
    }


    //给运算表达式添加括号，方便处理
    public static String addBrackets(String expression){
        String[] allWords=expression.split("\\s+");
        ArrayList<String> allExpressions=new ArrayList<>();
        for(String temp:allWords)
            allExpressions.add(temp);

//        //先处理运算符分隔的情况
//        for(int i=0;i<allExpressions.size();i++){
//            if(hasOp(allExpressions.get(i))){
//                String temp=allExpressions.get(i);
//                if(temp.equals("+")||temp.equals("-")||temp.equals("*")||temp.equals("/")||temp.equals("%")) {
//                    if (i == 0) {
//                        if (allExpressions.size() > 1&&canAppend(allExpressions.get(i+1))) {
//                            allExpressions.set(i, allExpressions.get(i) + allExpressions.get(i + 1));
//                            allExpressions.remove(i+1);
//                            i--;
//                        }
//                    }
//                    else{
//                        if(allExpressions.size()!=i+1) {
//                            if(canAppend(allExpressions.get(i-1))){
//                                allExpressions.set(i-1,allExpressions.get(i-1)+allExpressions.get(i));
//                                allExpressions.remove(i);
//                                i--;
//                            }
//                            if(canAppend(allExpressions.get(i+1))){
//                                allExpressions.set(i - 1, allExpressions.get(i - 1) + allExpressions.get(i));
//                                allExpressions.remove(i);
//                                i--;
//                            }
//                        }
//                        else{
//                            if(canAppend(allExpressions.get(i-1))) {
//                                allExpressions.set(i - 1, allExpressions.get(i - 1) + allExpressions.get(i));
//                                allExpressions.remove(i);
//                                i -= 2;
//                            }
//                        }
//                    }
//                }
//                else if(temp.startsWith("+")||temp.startsWith("-")||temp.startsWith("*")||temp.startsWith("/")||temp.startsWith("%")){
//                    if(i!=0)
//                        if(canAppend(allExpressions.get(i-1))) {
//                            allExpressions.set(i - 1, allExpressions.get(i - 1) + allExpressions.get(i));
//                            allExpressions.remove(i);
//                            i -= 2;
//                        }
//                }
//                else if(temp.endsWith("+")||temp.endsWith("-")||temp.endsWith("*")||temp.endsWith("/")||temp.endsWith("%")){
//                    if(temp.charAt(temp.length()-2)!='\\'){
//                        if(allExpressions.size()!=i+1){
//                            if(canAppend(allExpressions.get(i+1))) {
//                                allExpressions.set(i, allExpressions.get(i) + allExpressions.get(i + 1));
//                                allExpressions.remove(i + 1);
//                                i--;
//                            }
//                        }
//                    }
//                }
//            }
//        }

        allExpressions=combineBbrackets(allExpressions);

        //给表达式添加括号，其中不对以运算符开头的String加括号，防止分割运算符（同时也是为了支持括号内任意加空格）
        for(int i=0;i<allExpressions.size();i++) {
            String tempStr=allExpressions.get(i);
            String first=tempStr.substring(0,1);
            String last=tempStr.substring(tempStr.length()-1,tempStr.length());
            if(hasOp(tempStr)&&!tempStr.equals("(")&&!tempStr.equals(")")) {
                if(!isOperator(first)||first.equals("("))
                    if(!isOperator(last)||last.equals(")"))
                        allExpressions.set(i, "( " + allExpressions.get(i) + " )");
            }
        }


        //处理上一步之后的存在的单独以+ 或者 - 开头的表达式(此时它们是正负号)
        for(int i=0;i<allExpressions.size();i++) {
            String temp = allExpressions.get(i);
            if (temp.startsWith("+") || temp.startsWith("-"))
                if (!temp.equals("+") && !temp.equals("-"))
                    allExpressions.set(i, "( " + temp + " )");
        }

        //分隔
        for(int i=0;i<allExpressions.size();i++){
            String temp=allExpressions.get(i);
            for(int j=0;j<temp.length();j++){
                char tempChar=temp.charAt(j);
                if(isOperator(String.valueOf(tempChar))){
                    temp=temp.substring(0,j)+" "+tempChar+" "+temp.substring(j+1);
                    j+=2;
                }
            }
            allExpressions.set(i,temp);
        }

        String result="";
        for(int i=0;i<allExpressions.size();i++)
            result+=allExpressions.get(i)+" ";

        return result.trim();
    }

    //addBrackets辅助函数
    public static boolean hasOp(String expression){
        if(expression.indexOf("+")!=-1)
            return true;
        else if(expression.indexOf("-")!=-1)
            return true;
        else if(expression.indexOf("*")!=-1)
            return true;
        else if(expression.indexOf("/")!=-1)
            return true;
        else if(expression.indexOf("%")!=-1)
            return true;
        else if(expression.indexOf("(")!=-1)
            return true;
        else if(expression.indexOf(")")!=-1)
            return true;
        else
            return false;
    }


    //使得句子中的括号匹配
    public static ArrayList<String> combineBbrackets(ArrayList<String> allCommands){
        for(int i=0;i<allCommands.size();i++){
            String temp=allCommands.get(i);
            if(temp.startsWith("\""))
                continue;
            if(countBrackets(temp,'(')>countBrackets(temp,')')){
                if(allCommands.size()!=i+1) {
                    allCommands.set(i, temp +" "+allCommands.get(i + 1));
                    allCommands.remove(i+1);
                    i--;
                }
            }
            else if(countBrackets(temp,'(')<countBrackets(temp,')')){
                if(i!=0){
                    allCommands.set(i-1,allCommands.get(i-1)+" "+temp);
                    allCommands.remove(i);
                    i-=2;
                }
            }
            else
                continue;
        }
        return allCommands;
    }

    public static int countBrackets(String command,char type){
        int count=0;
        for(int i=0;i<command.length();i++)
            if(command.charAt(i)==type)
                count++;
        return count;
    }

}

