package share.console;

import java.util.Scanner;

/**
 * TODO 主程序类
 *
 * @author Haroxa
 * @date 2023/11/15
 */
public class Main {
    static String tip_menu = "请选择菜单：";
    static String tip_main = String.format("""
            %1$.8s欢迎进入档案系统%1$.8s
            %2$12s1.登录
            %2$12s2.退出
            %1$.30s""", DataProcessing.asterisk, "");

    public static void main(String[] args) {
        // 如果一个scanner未关闭，再开启一个scanner，
        // 则二者会共享输入缓存区，一但关闭一个，所有与之关联的scanner都会关闭
        Scanner in = new Scanner(System.in);
        while (true) {
            System.out.println(tip_main);
            System.out.printf(tip_menu);
            int option = DataProcessing.inputInt(in);
            System.out.println(option);
            switch (option) {
                case 1:
                    System.out.println("登录");
                    AbstractUser user = AbstractUser.login();
                    if (user != null) {
                        user.showMenu();
                    } else {
                        System.out.println("用户不存在或密码错误");
                    }
                    break;
                case 2:
                    DataProcessing.disconnectFromDB();
                    AbstractUser.exitSystem();
                    break;
                default:
                    System.out.println("未知的选项，请重新输入");
                    break;
            }
        }
    }
}