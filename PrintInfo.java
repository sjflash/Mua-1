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

    public static void printVerAuthor(){
        //版本号
        final String myVersion="Version 0.6\n";

        System.out.println("----------------------------------------------");
        System.out.print(
                "           " + " __  __             \n" +
                        "           " + "|  \\/  |_   _  __ _ \n" +
                        "           " + "| |\\/| | | | |/ _\\ |\n" +
                        "           " + "| |  | | |_| | (_| |\n" +
                        "Welcome to " + "|_|  |_|\\__/_|\\__/\\| " + myVersion);
        System.out.println("----------------------------------------------");
        System.out.println("   visit http://zhengcz.cn to get more info   ");
        System.out.println("              Author:st4rlight                ");
        System.out.println("----------------------------------------------");
        System.out.print(">>>");
    }
}
