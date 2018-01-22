package MUA;

import org.json.JSONObject;

import java.io.*;
import java.util.*;

public class Commands {
    public static void cmdPoall(HashMap<String,String> nameSpace){
        Iterator iter=nameSpace.entrySet().iterator();
        //遍历局部变量并放到全局变量中，若有同名变量则以局部变量为准
        while(iter.hasNext()){
            Map.Entry entry = (Map.Entry) iter.next();
            System.out.println(entry.getKey().toString());
        }
    }

    public static boolean cmdSave(HashMap<String,String> nameSpace,String fileName){
        fileName+=".json";
        // 生成json格式文件
        try {
            // 保证创建一个新文件
            File file = new File(fileName);
            if (file.exists()) { // 如果已存在,删除旧文件
                file.delete();
            }
            file.createNewFile();

            // 格式化json字符串
            JSONObject myJson=new JSONObject();
            Iterator iter=nameSpace.entrySet().iterator();
            //遍历局部变量并放到全局变量中，若有同名变量则以局部变量为准
            while(iter.hasNext()){
                Map.Entry entry = (Map.Entry) iter.next();
                myJson.put(entry.getKey().toString(),entry.getValue().toString());
            }

            // 将格式化后的字符串写入文件
            Writer write = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
            write.write(myJson.toString());
            write.flush();
            write.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public static boolean cmdLoad(HashMap<String,String> nameSpace,String fileName) {
        fileName += ".json";
        String resultStr = "";
        File file = new File(fileName);// 打开文件
        BufferedReader reader = null;

        try {
            FileInputStream in = new FileInputStream(file);
            reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));// 读取文件
            String tempStr = null;
            while ((tempStr = reader.readLine()) != null) {
                resultStr = resultStr + tempStr;
            }
            reader.close();
            JSONObject myJson = new JSONObject(resultStr);
            Iterator iterator = myJson.keys();
            while(iterator.hasNext()){
                String key = (String) iterator.next();
                String value = myJson.getString(key);
                nameSpace.put(key,value);
            }
            return true;
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    //read的执行函数，读入一个合法的数字或者单词并返回
    public static String cmdRead() {
        Scanner input = new Scanner(System.in);
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
                PrintInfo.printPrompt(temp+" is invalid, input again");
            System.out.print("read:");
            temp=input.nextLine();
            tempArray=temp.toCharArray();
        }

    }

    //readlist的执行函数，读入一个合法的list并返回
    public static String cmdReadList(){
        System.out.print("readlist:");
        Scanner input=new Scanner(System.in);
        String temp=input.nextLine();
        return "[ "+temp+" ]";
    }


    //add,sub,mul,div,mod的执行函数，传入的两个值应该是准确的可以运算的数字，且若第一个为thing应该去给它赋予新的值
    //若command为div和mod，那么第二个参数不应该为0
    public static String cmdCompute(String str1,String str2,String command){
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
    public static boolean cmdCompare(String str1,String str2,String command){
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
    public static boolean cmdAndOr(boolean bool1,boolean bool2,String command){
        if(command.equals("and"))
            return (bool1 & bool2);
        else
            return (bool1 | bool2);
    }
    //not的执行函数，返回相反逻辑值
    public static boolean cmdNot(boolean bool){
        return !bool;
    }
    //比较运算符eq,gt,lt执行结果的统一返回函数，即统一进行返回，避免单个命令判断返回的重复
    private static boolean flagJudge(int flag,String command){
        if(flag<0)
            switch (command){
                case "eq": case "gt": return false;
                case "lt": default: return true;
            }

        else if(flag==0)
            switch(command){
                case "lt": case "gt": return false;
                case "eq": default: return true;
            }
        else
            switch (command){
                case "eq": case "lt": return false;
                case "gt": default: return true;
            }
    }


    //检测变量名称是否与保留字相同，调用testLegal前应该事先去除"
    public static boolean testLegal(String temp){
        switch(temp) {
            case "make": case "thing":
            case "erase": case "isname":
            case "print": case "readlist": case "read":
            case "add": case "sub": case "mul": case "div": case "mod":
            case "eq": case "gt": case "lt":
            case "and": case "or": case "not":
            case "repeat": case "output": case "stop":
            case "sqrt": case "random":
            case "isnumber": case "isword": case "islist": case "isbool": case "isempty":
            case "export": case "int": case "run":
            case "wait" : case "save": case "load": case "erall": case "poall":
            case "word": case "if" : case "sentence": case "list": case "join": case "first": case "last":
            case "butfirst": case "butlast": case "abs":
                return false;
            default:
                return true;
        }
    }
    //检测变量名开头是否符合规范，即必须以字母或者下划线开头
    public static boolean testFirstChar(String temp){
        return (Character.isLetter(temp.charAt(0))||temp.charAt(0)=='_');
    }

    //传入整行命令，返回将其分隔后的数组，同时将整句[]转化为一个String
    public static String[] bracketToString(String[] allCommands){
        int i,j;

        ArrayList<String> tempAllCommands=new ArrayList<>();
        for(i=0;i<allCommands.length;i++) {
            if(allCommands[i].equals("[")){
                int countLeft=0;
                for(j=i;j<allCommands.length;j++){
                    if(allCommands[j].equals("["))
                        countLeft++;
                    else if(allCommands[j].equals("]")) {
                        if (countLeft != 0) {
                            if(--countLeft==0)
                                break;
                        }
                        else
                            break;
                    }
                    else
                        continue;
                }
                String temp="";
                if(j==allCommands.length)   //防止[]的情况，此时]匹配会越界
                    j-=1;
                for(int k=i;k<=j;k++)
                    temp+=allCommands[k]+" ";
                tempAllCommands.add(temp.trim());
                i=j;
            }
            else
                tempAllCommands.add(allCommands[i]);
        }

        return tempAllCommands.toArray(new String[tempAllCommands.size()]);
    }
}
