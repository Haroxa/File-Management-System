import java.util.Scanner;

public class Main {
    static String tip_main = String.format("""
            %1$.8s欢迎进入档案系统%1$.8s
            %2$12s1.登录
            %2$12s2.退出
            %1$.30s""", DataProcessing.asterisk, "");

    public static void main(String[] args) {
        Scanner in =new Scanner(System.in);
        while(true){
            System.out.println(tip_main);
            System.out.printf(DataProcessing.tip_menu);
            int option = DataProcessing.inputInt(in);
            System.out.println(option);
            switch(option){
                case 1:
                    System.out.println("登录");
                    AbstractUser user= AbstractUser.login();
                    if(user!=null){
                        user.showMenu();
                    }else {
                        System.out.println("用户不存在或密码错误");
                    }
                    break;
                case 2:
                    System.out.println("即将退出...");
                    System.exit(0);
                    break;
                default:
                    System.out.println("未知的选项，请重新输入");
                    break;
            }
        }
    }
}