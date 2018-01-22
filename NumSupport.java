package MUA;

public class NumSupport {
    //判断是否是一个数字，包含负数、小数等，不包含复数
    public static boolean isDigits(String temp){
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

    //isDigits的辅助函数，接受的String temp若为负数，则它的'-'需要先清理后再传入，后面不能再有'-'
    public static boolean isNumValid(String temp){
        char[] tempArray=temp.toCharArray();
        for(char tempChar:tempArray)
            if(tempChar>='0'&&tempChar<='9')
                continue;
            else if(tempChar!='.' && tempChar!='E' && tempChar!='e' && tempChar!='-')
                return false;
        return true;
    }

    //使用isDigits判断过的值，若合法则可以用clearZero来将其格式化，去除多余的0和小数点，补充缺少的0
    public static String clearZero(String temp){
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
                tempRight=tempRight.substring(0,tempRight.length()-1);
            temp=tempLeft+"."+tempRight;
        }
        if(temp.endsWith(".0"))
            temp=temp.substring(0,temp.indexOf(".0"));
        if(flag)
            return "-"+temp;
        else
            return temp;
    }

    //isDigits的辅助函数,计算字符中的小数点个数
    public static int countPoints(String temp){
        int count = 0;
        char[] tempArray=temp.toCharArray();
        for(char tempChar:tempArray)
            if(tempChar=='.')
                count++;
        return count;
    }

    public static double cmdRandom(double range){
        return Math.random()*range;
    }
    public static double cmdSqrt(double num){
        return Math.sqrt(num);
    }
    public static int cmdInt(double num){
        return (int)Math.floor(num);
    }

}
