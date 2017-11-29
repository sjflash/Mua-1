package MUA;

public class PrintInfo {
    //输出语法错误信息
    public static void printSyntaxError(String prompt){
        System.out.println("Syntax Error: " +prompt);
    }
    //输出警告信息，本次尚未用到
    //private static void printWarning(String prompt){
    //    System.out.println("Warning: "+prompt);
    //}
    //输出一些提示信息
    public static void printPrompt(String prompt){
        System.out.println("Prompt: "+prompt);
    }

    //输出运行错误
    public static void printRuntimeError(String error){
        System.out.println("Error: "+error);
    }
}
